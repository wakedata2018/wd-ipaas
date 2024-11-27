<template>
  <div class="toolbar-extra">
    <template v-if="(dataAssetApiId && !isReadonly) || isCopy">
      <template v-if="!isCopy">
        <el-button v-if="isPublished" type="primary" plain @click="onUnpublish">下线</el-button>
        <el-button v-else type="primary" plain @click="onPublish">保存并上线</el-button>
        <el-button type="primary" icon="el-icon-video-play" @click="onSaveAndTest">{{
          isPublished ? '测试' : '保存并测试'
        }}</el-button>
      </template>
      <el-button type="primary" icon="el-icon-files" @click="onSave">保存</el-button>
    </template>
  </div>
</template>

<script>
  import eventBus, { EventName } from '@/components/g6-editor/event-bus';
  import { getBBox } from '@/components/g6-editor/utils';
  import { getInitForm } from '../global/task-conf.js';
  import apiControllApi from '@/api/api-controller.js';
  import dataAssetApi from '@/api/data-asset-api.js';
  import { Loading, Message } from 'element-ui';
  import { ApiPublishStatus } from '@/utils/enum/index.js';
  import { getDefinition } from '../definitions';
  import { getUniqueNameFromModelConfig } from '../utils';
  import { OPERATION_TYPE } from '@/views/api-manage/api-publish/types';

  export default {
    components: {},
    props: {
      readonly: {
        type: Boolean,
        default: false,
      },
      taskInfo: {
        type: Object,
        default: () => getInitForm(),
      },
      graph: {
        type: Object,
        default: () => ({}),
      },
    },
    data() {
      return {
        modified: false,
      };
    },
    computed: {
      isEdit() {
        return !!this.dataAssetApiId;
      },
      isCopy() {
        return this.$route.query?.operation === OPERATION_TYPE.COPY;
      },
      isPublished() {
        return this.dataAssetPublishStatus === ApiPublishStatus.PUBLISH;
      },
      isReadonly() {
        const isReadonly = (this.isEdit && this.isPublished) || !!this.$route.query.readonly;
        this.$emit('update:readonly', isReadonly);
        return isReadonly;
      },
      dataAssetApiId() {
        return this.taskInfo?.dataAssetApi?.dataAssetApiId || null;
      },
      dataAssetPublishStatus() {
        return this.taskInfo?.dataAssetApi?.dataAssetPublishStatus;
      },
    },
    created() {
      this.bindEvent();
    },
    beforeDestroy() {
      eventBus.$off(EventName.add, this.onG6Change);
      eventBus.$off(EventName.update, this.onG6Change);
      eventBus.$off(EventName.delete, this.onDeleteNode);
    },
    methods: {
      bindEvent() {
        eventBus.$on(EventName.add, this.onG6Change);
        eventBus.$on(EventName.update, this.onG6Change);
        eventBus.$on(EventName.delete, this.onDeleteNode);
      },
      async validate() {
        const nodes = this.graph.getNodes();
        const combosNodes = this.graph.getCombos();

        let warnCount = 0;
        const res = await Promise.all(
          [...nodes, ...combosNodes].map(async node => {
            const model = node.getModel();
            const uniqueName = getUniqueNameFromModelConfig(model);
            if (uniqueName) {
              const define = getDefinition(uniqueName);
              if (define?.validate) {
                const valid = await define.validate(model);
                if (!valid) {
                  // 防止提示信息弹窗重叠
                  setTimeout(() => {
                    this.$message.warning(`请检查 '${model.form.name || model.title || model.label}' 是否正确填写`);
                  }, warnCount++ * 10);
                }
                return valid;
              }
            }
            return true;
          })
        );
        return res.every(i => i);
      },
      onSaveAndPublish() {
        this.$emit('toolbar-command', 'SaveAndPublish');
      },
      onSaveAndTest() {
        this.$emit('toolbar-command', 'SaveAndTest');
      },
      onSave() {
        this.$emit('toolbar-command', 'Save');
      },
      async save(refresh = true, backToList = false) {
        if (!(await this.validate())) {
          return;
        }
        this.$emit('update-task-info');
        const params = Object.assign({}, this.taskInfo);
        let loadingInstance;
        if (refresh) {
          loadingInstance = Loading.service({ fullscreen: true });
        }
        let action = 'editApi';
        if (!params.dataAssetApi.dataAssetApiId) {
          delete params.dataAssetApi.dataAssetApiId;
          action = 'newApiAdd';
        }

        try {
          await apiControllApi[action](params);
          this.modified = false;
          if (backToList) {
            this.$emit('toolbar-command', 'BackToList');
          } else if (refresh) {
            this.$message({
              type: 'success',
              message: '保存成功',
            });
            // 重新刷新数据 getTaskInfo
            this.onRefresh();
          }
        } catch (error) {
          console.error(error);
        } finally {
          loadingInstance?.close();
        }
      },

      onTest() {
        this.$emit('toolbar-command', 'Test');
      },

      onRefresh() {
        // 第一个参数为是否做值校验
        this.$emit('toolbar-command', 'Refresh', false);
      },

      // onGrant() {
      //   eventBus.$emit(EventName.toolbarCommand.toolbarCommand, EventName.toolbarCommand.grant);
      // },
      // onRecover() {
      //   eventBus.$emit(EventName.toolbarCommand.toolbarCommand, EventName.toolbarCommand.recover);
      // },
      async test() {
        eventBus.$emit(EventName.toolbarCommand.toolbarCommand, EventName.toolbarCommand.test);
      },
      onPublish() {
        // 复制会清除apiId
        if (!this.dataAssetApiId && !this.isCopy) {
          return;
        }
        this.confirmTpl({
          title: `确定保存并上线API吗？`,
          method: 'publish',
        });
      },
      publish() {
        dataAssetApi.publish({ dataAssetApiId: this.dataAssetApiId }).done(() => {
          Message.closeAll();
          this.$message({
            type: 'success',
            message: '发布成功',
          });
          this.onRefresh(true);
        });
      },
      onUnpublish() {
        if (!this.dataAssetApiId) {
          return;
        }
        this.confirmTpl({
          title: `确定下线API吗？`,
          successTip: `下线成功`,
          method: 'unpublish',
          params: {
            dataAssetApi: this.dataAssetApiId,
          },
          callback() {
            this.onRefresh(true);
          },
        });
      },
      confirmTpl(config) {
        this.$confirm(config.title, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
          .then(async () => {
            if (config.method === 'publish') {
              this.onSaveAndPublish();
              return;
            }
            dataAssetApi[config.method](config.params).done(res => {
              Message.closeAll();
              this.$message({
                type: 'success',
                message: config.successTip,
              });
              if (config.callback) {
                config.callback.call(this, res);
              }
            });
          })
          .catch(() => {
            // console.log('已取消');
          });
      },

      translateToView() {
        const nodes = this.graph.getNodes();
        if (nodes.length) {
          const box = getBBox(nodes);
          const point = this.graph.getPointByCanvas(0, 0);
          this.graph.translate(point.x, point.y);
          this.graph.translate(-box.left + 30, -box.top + 30);
        }
      },
      onG6Change() {
        this.modified = true;
      },
      onDeleteNode() {
        this.$emit('update-task-info');
        this.onG6Change();
      },
    },
  };
</script>

<style lang="less" scoped>
  .toolbar-extra {
    float: right;
    display: flex;
    height: 100%;
    flex-direction: row;
    margin-right: 20px;
    align-items: center;

    .toolbar-extra-btns {
      display: flex;
      flex-direction: row;
    }

    .task-status {
      display: inline-block;
      margin-right: 20px;
      font-size: 12px;
      color: #333;
    }
  }
</style>
