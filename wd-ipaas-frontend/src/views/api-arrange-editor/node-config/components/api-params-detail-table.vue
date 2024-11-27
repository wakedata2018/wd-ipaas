<template>
  <div class="http-api-sql-detail-table">
    <div v-if="!isReadonly && !isEdit && canAdd" style="padding: 5px 0">
      <el-button type="primary" icon="el-icon-plus" @click="onAdd">添加</el-button>
    </div>
    <el-tabs v-model="tab">
      <el-tab-pane label="请求头" name="head">
        <api-params-table
          v-model="headList"
          params-type="HEAD"
          :params-map="paramsMap"
          :mode="isReadonly ? 'fixed' : 'preview'"
          is-flow
          @select-params="selectParams"
          @select-method="selectMethod"
        >
        </api-params-table>
      </el-tab-pane>
      <el-tab-pane label="QUERY参数" name="query">
        <api-params-table
          v-model="queryList"
          params-type="QUERY"
          :params-map="paramsMap"
          :mode="isReadonly ? 'fixed' : 'preview'"
          is-flow
          @select-params="selectParams"
          @select-method="selectMethod"
        ></api-params-table>
      </el-tab-pane>
      <el-tab-pane v-if="isReqMethodPost" label="请求体" name="body">
        <div class="response-params-content">
          <el-row class="table-head" :gutter="10">
            <el-col :span="4" class="table-head__item"
              ><tips-icon :content="$t('validator.httpFieldNameDesc')"></tips-icon>参数名称</el-col
            >
            <el-col :span="4" class="table-head__item">参数类型</el-col>
            <el-col :span="2" class="table-head__item">是否必填</el-col>
            <el-col :span="4" class="table-head__item">示例值</el-col>
            <el-col :span="3" class="table-head__item">选择方式</el-col>
            <el-col :span="5" class="table-head__item">参数值</el-col>
            <el-col :span="4" class="table-head__item">描述</el-col>
          </el-row>
          <json-schema-editor
            mode="flowDetail"
            :value="bodyTree"
            disabled-type
            :disabled="isReadonly"
            custom
            @select-params="selectParams"
            @select-method="selectMethod"
          />
        </div>
      </el-tab-pane>
    </el-tabs>
    <select-parameters-dialog
      :visible.sync="showDialog"
      :task-info="taskInfo"
      :operator-id="operatorId"
      @selected="selected"
    ></select-parameters-dialog>
    <SelectMethodDialog
      :visible.sync="showMethodDialog"
      :expression="expression"
      :task-info="taskInfo"
      :operator-id="operatorId"
      @selected="selectedMethod"
    ></SelectMethodDialog>
  </div>
</template>

<script>
  import TipsIcon from '@/components/tips-icon';
  import Validator from '@/utils/validator';
  import apiController from '@/api/api-controller';
  import { ApiType, ModeMap, ModeEnum, TYPE_ELE, POSITION_LIST, TYPES_LIST } from '@/utils/enum';
  import jsonSchemaEditor from '@/components/json-schema-editor/index';
  import SelectParametersDialog from '@/components/select-parameter-dialog';
  import SelectMethodDialog from '@/components/select-method-dialog';
  import textUtils from '@/utils/text-utils';
  import { ApiParamsTable } from '@/components/api-edit/index.js';
  import { getIdFromItem } from '../../utils';

  export default {
    name: 'ApiParamsDetailTable',
    components: { TipsIcon, jsonSchemaEditor, SelectParametersDialog, SelectMethodDialog, ApiParamsTable },
    inject: ['curNode'],
    props: {
      config: {
        type: Array,
        default: () => [],
      },
      isReadonly: {
        type: Boolean,
        default: false,
      },
      isEdit: {
        type: Boolean,
        default: false,
      },
      canAdd: {
        type: Boolean,
        default: true,
      },
      refresh: {
        type: Boolean,
        default: false,
      },
      taskInfo: {
        type: Object,
        default: () => ({}),
      },
      apiDetail: {
        type: Object,
        default: () => ({}),
      },
      // 参数设置别名
      paramsMap: {
        type: Object,
        default: null,
      },
      /**
       * 表单类型 HTTP 和 连接器
       * api_http  | api_connector
       */
      type: {
        type: String,
        default: 'api_http',
      },
    },
    data() {
      const START_ELE = {
        desc: 'start',
        name: 'start',
        operatorId: 'start',
      };
      return {
        TYPE_ELE,
        START_ELE,
        ModeEnum,
        POSITION_LIST,
        TYPES_LIST,
        ModeMap,
        activeName: 'parameters',
        jsonObject: [],
        defaultProps: {
          children: 'childApiRespParams',
          label: 'assetColumns',
        },
        bodyTree: {
          root: {
            type: 'object',
            name: 'root',
          },
        },
        tab: 'query',
        showDialog: false,
        showMethodDialog: false,
        expression: '',
        callback: null,
        methodCallback: null,
        selectNodeId: null, // 选择的公共参数的算子id
        inited: false,
      };
    },
    computed: {
      headList() {
        return this.config?.filter(item => item.httpParamKind === 'HEAD') ?? [];
      },
      queryList() {
        return this.config.filter(item => item.httpParamKind === 'QUERY' || item.httpParamKind === 'FILTER') ?? [];
      },
      bodyParams() {
        return this.config.find(item => item.httpParamKind === 'BODY' || item.type === 'BODY');
      },
      operatorId() {
        return getIdFromItem(this.curNode());
      },
      currentApiMethod() {
        return this.apiDetail.dataAssetApi?.reqMethod || this.apiDetail.connectorApi?.reqMethod;
      },
      // POST 请求方式 请求参数才展示请求体
      isReqMethodPost() {
        return this.currentApiMethod === 'POST';
      },
    },
    watch: {
      config: {
        immediate: true,
        handler(val) {
          this.handlerBodyTree();
        },
      },
    },
    methods: {
      handlerBodyTree() {
        if (this.bodyParams && this.isReqMethodPost) {
          if (!this.inited) {
            this.tab = 'body';
          }
          const operators = this.taskInfo.dataAssetApi.apiAttr?.operators;
          const parameters = this.apiDetail.parameters;
          let jsonSchema = this.bodyParams.jsonSchema;
          if (operators[this.operatorId]) {
            if (this.type === 'api_http') {
              jsonSchema = operators[this.operatorId].component.parameters.find(
                item => item.httpParamKind === 'BODY'
              ).jsonSchema;
            } else if (this.type === 'api_connector') {
              jsonSchema =
                operators[this.operatorId].component.requestParams.find(item => item.httpParamKind === 'BODY')
                  ?.jsonSchema ?? jsonSchema;
            }
          } else if (parameters) {
            jsonSchema = parameters.find(item => item.httpParamKind === 'BODY').jsonSchema;
          }
          if (jsonSchema) {
            this.bodyTree = JSON.parse(jsonSchema);
          }
        }
      },
      onAdd() {
        const operator = {
          assetColumns: '',
          httpParamKind: 'QUERY',
          assetDatatype: 'string',
          required: false,
          sample: '',
          descriptions: '',
          type: 'PARAMETERS',
          operatorId: 'start',
          expression: '',
        };
        // eslint-disable-next-line vue/no-mutating-props
        this.config.push(operator);
      },
      validate() {
        let index = -1;
        let repeat = false;
        const fieldArr = [];
        const parameters = this.config;
        for (let i = 0; i < parameters.length > 0; i++) {
          const item = parameters[i];
          if (
            !item.assetColumns ||
            !Validator.httpFieldEnNameValidatorFun(item.assetColumns).result ||
            !item.httpParamKind ||
            !item.assetDatatype ||
            (item.required && (!item.expression || !item.operatorId))
          ) {
            index = i;
            break;
          }
          if (fieldArr.indexOf(item.assetColumns) !== -1) {
            repeat = true;
            break;
          }
          fieldArr.push(item.assetColumns);
        }

        const found = index !== -1 || repeat;
        return !found;
      },

      selectParams(callback) {
        // json-schema编辑器 点击选择引用值
        this.callback = callback;
        this.showDialog = true;
      },
      selected({ nodeId, data }) {
        this.selectNodeId = nodeId;
        this.callback?.({ operatorId: nodeId, id: data });
        this.$emit('input', this.config);
      },

      selectMethod(expression, callback) {
        this.showMethodDialog = true;
        this.expression = expression;
        if (typeof callback === 'function') {
          this.methodCallback = callback;
        }
      },
      selectedMethod(expression) {
        this.expression = null;
        this.methodCallback?.(expression);
      },
      // 数据保存到store中
      updateApiAttr(newList, parameters = []) {
        const operator = this.taskInfo.dataAssetApi.apiAttr?.operators?.[this.operatorId];
        const { name, desc } = this.apiDetail;
        if (operator) {
          return {
            ...operator.component,
            requestParamMappings: newList,
            requestParams: parameters,
            parameters,
            name,
            desc,
          };
        }
      },
      saveData() {
        let newParams = null;
        const newParameters = [];
        if (this.isReqMethodPost) {
          // 合并选择方式参数
          // root节点类型为object/array<object>
          const json = this.bodyTree.root.properties
            ? textUtils.tree2mapValue(this.bodyTree.root.properties)
            : textUtils.tree2mapValue(this.bodyTree);
          newParams = {
            dataType: 'object',
            operatorId: 'start',
            field: 'body',
            httpParamKind: 'BODY',
            type: 'reference',
            expression: JSON.stringify(json),
          };

          const parameters = this.apiDetail?.parameters ?? this.apiDetail.requestParams;
          if (parameters) {
            for (const item of parameters) {
              if (item.httpParamKind === 'BODY') {
                item.jsonSchema = JSON.stringify(this.bodyTree);
              }
              newParameters.push(item);
            }
          }
        }

        const list = [];
        this.config.forEach(obj => {
          const item = {
            field: obj.assetColumns,
            operatorId: this.selectNodeId || 'start',
            httpParamKind: obj.httpParamKind,
            type: obj.paramValueType,
          };
          if (obj.paramValueType === ModeEnum.reference || obj.paramValueType === ModeEnum.method) {
            item.expression = obj.expression;
          } else {
            item.fixedValue = obj.fixedValue;
          }
          if (item.httpParamKind === 'BODY' && this.isReqMethodPost) {
            list.push(newParams);
          } else {
            list.push(item);
          }
        });

        return this.updateApiAttr(list, newParameters);
      },
    },
  };
</script>

<style scoped lang="less">
  @import './style.less';
  .http-api-sql-detail-table {
    .table-head {
      border-bottom: 1px solid #ebeef5;
      padding: 6px 0px;
      background-color: #f3f6f8;
      color: #909399;
      font-weight: bold;
      margin: 0px 0px 5px 0px !important;
      display: flex;
      align-items: center;
    }
    .table-head__item {
      padding: 0 10px !important;
      line-height: 23px;
      font-size: 12px;
    }

    .expression__value {
      flex: 1;
      position: relative;
      display: flex;
      align-items: center;
      & /deep/ .el-input__inner {
        padding-right: 24px;
      }
    }
    .expression__icon {
      cursor: pointer;
      position: absolute;
      padding: 0px 3px;
      right: 13px;
      background: #fff;
    }

    .select-params {
      width: 100%;
      border-radius: 4px;
      border: 1px solid #dcdfe6;
      text-align: left;
      height: 28px;
      padding: 0 15px;
    }
    .ellipsis {
      overflow: hidden;
      text-overflow: ellipsis;
      display: inline-block;
      white-space: nowrap;
    }
    ::v-deep .cell {
      display: flex;
      flex-direction: row;
      align-items: center;
    }
    .fix_value {
      width: 100%;
    }
  }
</style>
