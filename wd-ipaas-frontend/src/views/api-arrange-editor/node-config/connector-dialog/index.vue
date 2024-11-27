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
        <api-commmon ref="formCommon" :config="form" :is-readonly="isReadonly" />
        <connector-api-config
          ref="formCmp"
          :config="form"
          :refresh="refresh"
          :is-readonly="isReadonly"
          :task-info="taskInfo"
          @setConnectorSecretKey="setConnectorSecretKey"
        />
      </div>
    </el-scrollbar>
    <div v-if="!isReadonly" class="bd-dialog-footer">
      <el-button class="dss-btn-circle" @click="isShow = false">取消</el-button>
      <el-button class="dss-btn-circle" type="primary" @click="onSave">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import cloneDeep from 'lodash/cloneDeep';
  import apiControllApi from '@/api/api-controller';
  import eventBus, { EventName } from '@/components/g6-editor/event-bus';
  import { ApiType } from '@/utils/enum';
  import ApiCommmon from './api-common.vue';
  import ConnectorApiConfig from './connector-api-config.vue';
  import { getIdFromItem, getUniqueNameFromItem } from '../../utils';

  export default {
    name: 'ConnectApiDialog',
    components: {
      ApiCommmon,
      ConnectorApiConfig,
    },
    provide() {
      return {
        currentNodeApiDetail: () => this.currentNodeApiDetail,
      };
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
        form: {},
        loading: false,
        currentNodeApiDetail: null,
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
            this.$emit('cancel', this.uniqueName);
          }
          this.$emit('update:visible', val);
        },
      },
      dialogTitle() {
        return ApiType._connector_api.label;
      },
    },
    methods: {
      setConnectorSecretKey(data) {
        this.form.environmentId = data.environmentId;
        this.form.connectorApi.key = data;

        // 不能保存 secretKey ，因为会导致循环引用
        const keys = Object.keys(data);
        keys.forEach(item => {
          if (item !== 'secretKey') {
            this.form.connectorSecretKey[item] = data[item];
          }
        });
      },
      refreshComponent() {
        this.refresh = !this.refresh;
      },
      initForm() {
        this.form = cloneDeep(this.config);
      },
      formatName(path) {
        const res = path.split('/') || [];
        return res[res.length - 1].replace(/\./g, '_');
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
          const params = this.$refs.formCmp?.$refs.apiParamsDetailTable?.saveData();
          const { requestParamMappings, requestParams, parameters } = params;
          const resultParams = {
            ...this.form,
            requestParamMappings,
            requestParams,
            parameters,
          };
          this.$emit('save', resultParams, cloneDeep(this.curNode.getModel()));
          this.isShow = false;
        } catch (error) {
          this.$message.error(error.message);
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
        this.initForm();
        this.$nextTick(() => {
          this.refreshComponent();
          this.getForms().forEach(({ form }) => {
            this.$nextTick(() => {
              form.clearValidate();
            });
          });
          this.loading = false;
        });
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
