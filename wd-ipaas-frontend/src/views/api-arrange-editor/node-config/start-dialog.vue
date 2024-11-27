<template>
  <el-dialog
    class="bd-dialog start-dialog"
    width="100%"
    top="60px"
    fullscreen
    custom-class="anim-left"
    :visible="visible"
    :close-on-click-modal="false"
    title="开始节点"
    @close="handleClose"
  >
    <el-scrollbar class="page-scrollbar">
      <el-form ref="form" :model="form" label-width="auto" :disabled="isReadonly">
        <el-row type="flex" justify="space-between">
          <el-col :span="11">
            <el-form-item label="步骤名称" prop="name">
              <el-input
                v-model.trim="form.name"
                class="start-dialog-input"
                placeholder="输入下划线、英文字母、数字，不超过30个字"
                maxlength="30"
                disabled
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="11">
            <el-form-item label="步骤描述" prop="desc">
              <el-input
                v-model.trim="form.desc"
                class="start-dialog-input"
                placeholder="不超过50个字符"
                maxlength="50"
                disabled
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div class="main-title">请求参数</div>
      <el-tabs v-model="activePane" class="request-content">
        <el-tab-pane label="请求头" name="head">
          <api-params-table
            ref="headForm"
            v-model="headParams"
            params-type="HEAD"
            :params-map="paramsMapConfig"
            :mode="isReadonly ? 'fixed' : 'edit'"
          ></api-params-table>
        </el-tab-pane>
        <el-tab-pane label="QUERY参数" name="query">
          <api-params-table
            ref="queryForm"
            v-model="queryParams"
            params-type="QUERY"
            :params-map="paramsMapConfig"
            :mode="isReadonly ? 'fixed' : 'edit'"
          ></api-params-table>
        </el-tab-pane>
        <el-tab-pane v-if="showBody" label="请求体" name="body">
          <request-body :tree="tree" :is-readonly="isReadonly"> </request-body>
        </el-tab-pane>
      </el-tabs>
    </el-scrollbar>
    <div v-if="!isReadonly" class="bd-dialog-footer">
      <el-button class="dss-btn-circle" @click="handleClose">取消</el-button>
      <el-button class="dss-btn-circle" type="primary" @click="onSave">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import cloneDeep from 'lodash/cloneDeep';
  import { ApiParamsTable, BodyTree as RequestBody } from '@/components/api-edit/index.js';
  import textUtils from '@/utils/text-utils';
  import eventBus, { EventName } from '@/components/g6-editor/event-bus';
  import { getInitForm } from '../global/task-conf.js';

  export default {
    name: 'StartDialog',
    components: { ApiParamsTable, RequestBody },
    props: {
      visible: {
        type: Boolean,
        default: false,
      },
      config: {
        type: Object,
        default: null,
      },
      taskInfo: {
        type: Object,
        default: () => getInitForm(),
      },
      curNode: {
        type: Object,
        default: null,
      },
      isReadonly: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        form: {
          desc: '开始算子',
          name: 'start',
        },
        activePane: 'query',
        parameters: null,
        tree: null,
        inited: false,
        // 参数设置别名
        paramsMapConfig: {
          dataType: 'assetDatatype',
          description: 'descriptions',
        },
      };
    },
    computed: {
      dataAssetApiId() {
        return this.taskInfo && this.taskInfo.dataAssetApi && this.taskInfo.dataAssetApi.dataAssetApiId;
      },
      /**
       * POST类型的展示请求体
       */
      showBody() {
        return this.taskInfo?.dataAssetApi?.reqMethod === 'POST' ?? false;
      },
      /**
       * 请求头参数
       */
      headParams: {
        get() {
          return this.parameters?.filter(item => item.httpParamKind === 'HEAD') ?? [];
        },
        set(val) {
          val = val.map(item => ({
            dataAssetId: this.dataAssetApiId,
            autoPare: false,
            ...item,
          }));
          this.parameters = [...val, ...this.queryParams, this.bodyParams];
        },
      },
      /**
       * QUERRY参数
       */
      queryParams: {
        get() {
          return this.parameters?.filter(item => item.httpParamKind === 'QUERY') ?? [];
        },
        set(val) {
          val = val.map(item => ({
            dataAssetId: this.dataAssetApiId,
            autoPare: false,
            ...item,
          }));
          this.parameters = [...this.headParams, ...val, this.bodyParams];
        },
      },
      /**
       * 提交的请求体
       */
      bodyParams() {
        if (!this.parameters || !this.showBody) {
          return {};
        }
        let idx = this.parameters.findIndex(item => item.httpParamKind === 'BODY');
        if (idx === -1) {
          idx = this.parameters.length;
          // eslint-disable-next-line vue/no-side-effects-in-computed-properties
          this.$set(this.parameters, idx, {
            assetColumns: 'body',
            assetDatatype: 'json',
            httpParamKind: 'BODY',
            dataAssetId: this.dataAssetApiId,
            type: 'PARAMETERS',
            typeAttr: 'OPERATOR',
            descriptions: '请求体参数',
            autoPare: false,
          });
        }
        return this.parameters[idx];
      },
    },
    watch: {
      visible(val) {
        if (val) {
          this.form = cloneDeep(this.config);
          this.parameters = cloneDeep(this.taskInfo?.parameters);
          this.inited = false;
        } else {
          this.$refs.form.resetFields();
        }
      },
      bodyParams: {
        immediate: true,
        handler(val) {
          if (val && !this.inited) {
            this.initTree();
          }
        },
      },
      tree: {
        deep: true,
        handler(val) {
          // 根节点类型
          const rootType = val.root.type;
          if (rootType === 'array') {
            this.$set(this.bodyParams, 'assetDatatype', 'array<object>');
          } else {
            this.$set(this.bodyParams, 'assetDatatype', 'json');
          }
          this.$set(this.bodyParams, 'jsonSchema', JSON.stringify(val));
        },
      },
    },
    methods: {
      initTree() {
        const postBody = this.bodyParams.jsonSchema;
        if (!postBody) {
          this.tree = {
            root: {
              type: 'object',
              name: 'root',
              description: '根层级',
              properties: {},
            },
          };
        } else {
          this.tree = JSON.parse(postBody);
        }
        this.inited = true;
      },
      handleClose() {
        this.$emit('cancel', false);
        this.$emit('update:visible', false);
      },
      async onSave() {
        try {
          await this.$refs.headForm?.validate();
          await this.$refs.queryForm?.validate();
          /** 更新请求体 */
          if (this.showBody) {
            /** 请求体校验 */
            const res = textUtils.hasEmptyOrMultiName(this.tree.root?.properties);
            if (!res.success) {
              throw new Error('BODY参数名称' + res.message);
            }
          }

          this.$emit('save', this.form, cloneDeep(this.curNode.getModel()));
          this.$set(this.taskInfo, 'parameters', this.parameters);
          this.handleClose();
        } catch (error) {
          this.$message.error(error.message || '参数名称填写错误');
        }
      },
    },
  };
</script>
<style lang="less" scoped>
  .start-dialog {
    .page-scrollbar {
      box-sizing: border-box;
      overflow-x: hidden;
      overflow-y: hidden;
      margin-bottom: 10px;

      /deep/.el-scrollbar__wrap {
        overflow-x: hidden;
        max-height: calc(100vh - 226px);

        .el-scrollbar__view {
          padding-bottom: 17px;
        }
      }
    }

    .main-title {
      height: 22px;
      font-size: 16px;
      margin: 10px 0;
      font-weight: 600;
      color: rgba(51, 51, 51, 1);
      line-height: 22px;
    }

    .table-input {
      width: calc(100% - 40px);
    }

    .required {
      color: #f56c6c;
      margin-right: 4px;
      width: 6px;
      display: inline-block;
    }

    /deep/ .el-dialog__footer {
      position: absolute;
      left: 0;
      bottom: 0;
      width: 100%;
      height: 70px;
      background: orange;
    }

    .start-dialog-table {
      margin-bottom: 100px;
      max-height: calc(100% - 150px);
      overflow-y: auto;
    }
  }
</style>
