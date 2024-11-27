<template>
  <el-dialog
    v-loading="loading"
    :visible.sync="isShow"
    width="100%"
    top="60px"
    fullscreen
    class="bd-dialog api-dialog"
    :class="isReadonly ? 'is-readonly' : ''"
    custom-class="anim-left"
    :title="'API - ' + dialogTitle"
    :close-on-click-modal="false"
    @opened="onOpened"
  >
    <el-scrollbar :key="uniqueName" class="page-scrollbar">
      <div class="content-div">
        <api-commmon ref="formCommon" :config="form" :refresh="refresh" :is-readonly="isReadonly" />
        <component
          :is="cmpType"
          v-if="cmpType"
          ref="formCmp"
          :config="form"
          :refresh="refresh"
          :is-readonly="isReadonly"
          :task-info="taskInfo"
          :cur-node="curNode"
        />
      </div>
    </el-scrollbar>
    <div v-if="!isReadonly" class="bd-dialog-footer">
      <el-button class="dss-btn-circle" @click="isShow = false">取消</el-button>
      <el-button class="dss-btn-circle" type="primary" @click="onSave">确定</el-button>
    </div>
  </el-dialog>
</template>

<!-- eslint-disable vue/no-mutating-props -->
<script>
  import { Message } from 'element-ui';
  import cloneDeep from 'lodash/cloneDeep';
  import apiControllApi from '@/api/api-controller';
  import eventBus, { EventName } from '@/components/g6-editor/event-bus';
  import { ApiType } from '@/utils/enum';
  import { getInitForm } from '../../global/task-conf';
  import { getIdFromItem, getUniqueNameFromItem } from '../../utils';
  import ApiCommmon from './api-common.vue';
  import CustomSqlConfig from './custom-sql-config.vue';
  import ExternalHttpConfig from './external-http-config.vue';
  import NormalTableConfig from './normal-table-config.vue';

  export default {
    components: {
      ApiCommmon,
      CustomSqlConfig,
      ExternalHttpConfig,
      NormalTableConfig,
    },
    props: {
      visible: {
        type: Boolean,
        default: false,
      },
      config: {
        type: Object,
        default() {
          return {};
        },
      },
      taskInfo: {
        type: Object,
        default: () => ({}),
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
        refresh: false,
        form: getInitForm(),
        loading: false,
      };
    },
    computed: {
      operatorId() {
        return getIdFromItem(this.curNode);
      },
      uniqueName() {
        return getUniqueNameFromItem(this.curNode);
      },
      isShow: {
        get() {
          return this.visible;
        },
        set(val) {
          if (!val) {
            this.form = getInitForm();
            this.$emit('cancel', this.uniqueName);
          }
          this.$emit('update:visible', val);
        },
      },
      apiType() {
        return this.uniqueName?.replace(/^api_/, '').toUpperCase();
      },
      dialogTitle() {
        switch (this.apiType) {
          case ApiType.CUSTOM_SQL:
            return ApiType._custom_sql.label;
          case ApiType.NORMAL_TABLE:
            return ApiType._normal_table.label;
          case ApiType.EXTERNAL_HTTP:
            return ApiType._external_http.label;
          case ApiType.WEB_SERVICE:
            return ApiType._web_service.label;
          default:
            return 'API';
        }
      },
      cmpType() {
        switch (this.apiType) {
          case ApiType.CUSTOM_SQL:
            return CustomSqlConfig;
          case ApiType.NORMAL_TABLE:
            return NormalTableConfig;
          case ApiType.EXTERNAL_HTTP:
            return ExternalHttpConfig;
          case ApiType.WEB_SERVICE:
            return ExternalHttpConfig;
          default:
            return null;
        }
      },
    },
    watch: {
      isShow(val) {
        this.$nextTick(() => {
          if (!val) {
            this.form = getInitForm();
          }
        });
      },
    },
    methods: {
      refreshComponent() {
        this.refresh = !this.refresh;
      },
      async initForm() {
        this.form = cloneDeep(this.config);
      },
      validate() {
        if (!this.curNode || this.curNode.destroyed) {
          throw new Error('该节点不存在，无法保存，请重新选择节点。');
        }

        if (this.form.name === 'globalParam') {
          throw new Error('globalParam不能作为步骤名称。');
        }

        const operators = this.taskInfo.dataAssetApi.apiAttr.operators;
        const isFind = Object.keys(operators).some(key => {
          return operators[key].name === this.form.name && key !== this.operatorId;
        });
        if (isFind) {
          throw new Error('步骤名称不能重复，请重新设置。');
        }
      },
      async onSave() {
        const forms = this.getForms();
        const pList = forms.map(form => form.form.validate());
        const isValidate = await Promise.all(pList);
        Promise.all(pList)
          .then(() => {
            this.save();
          })
          .catch(() => {
            this.$message.error(this.$t('validator.emptyWarning'));
          });
      },
      save() {
        try {
          this.validate();
          // 先做上面的校验，再做下面的保存数据到taskInfo
          // 这里处理了请求参数请求体和选择方式的合并
          const params = this.$refs.formCmp?.saveData?.();
          this.$emit('save', params, cloneDeep(this.curNode.getModel()));
          this.isShow = false;
        } catch (err) {
          Message.closeAll();
          this.$message.error(err.message);
        }
      },
      getForms() {
        const { formCommon, formCmp } = this.$refs;
        const formList = [];
        if (formCommon) {
          formList.push({ form: formCommon });
        }
        if (formCmp) {
          formList.push({ form: formCmp });
        }
        return formList;
      },
      async onOpened() {
        this.loading = true;
        await this.initForm();
        this.refreshComponent();
        this.getForms().forEach(({ form }) => {
          form.clearValidate();
        });
        this.loading = false;
      },
    },
  };
</script>

<style scoped lang="less">
  .api-dialog {
    .page-scrollbar {
      box-sizing: border-box;
      overflow-x: hidden;
      overflow-y: hidden;

      /deep/ .el-scrollbar__wrap {
        overflow-x: hidden;
        max-height: calc(100vh - 226px);
      }

      .content-div {
        padding: 0 0 20px 0;
      }
    }

    &.is-readonly {
      .page-scrollbar {
        /deep/ .el-scrollbar__wrap {
          max-height: calc(100vh - 180px);
        }
      }
    }
  }

  /deep/.codemirror-editor {
    .vue-codemirror {
      height: 234px !important;
      margin-bottom: 2px;
    }

    &.full {
      .vue-codemirror {
        height: calc(100vh - 80px) !important;
      }
    }
  }

  /deep/.codemirror-and-params-container {
    font-size: 0px;
    flex-wrap: nowrap;
    display: flex;
    flex-direction: row;
    position: relative;

    .codemirror-and-params {
      font-size: 13px;
      display: inline-block;
      box-sizing: border-box;

      &.params {
        padding-right: 10px;
      }
    }
  }

  /deep/.main-title {
    height: 22px;
    font-size: 16px;
    margin: 10px 0;
    font-weight: 600;
    color: rgba(51, 51, 51, 1);
    line-height: 22px;
  }
</style>
