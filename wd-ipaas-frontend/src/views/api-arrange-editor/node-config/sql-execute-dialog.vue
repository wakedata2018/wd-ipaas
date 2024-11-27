<template>
  <FatFormDrawer
    ref="form"
    title="SQL算子"
    :visible="props.visible"
    :initial-value="config"
    :submit="handleSubmit"
    :submit-props="{
      disabled: saveDisabled,
    }"
    label-suffix=""
    drawer-size="80%"
    :class="{ 'bd-dialog': true, 'auth-list-dialog': true, 'sql-execute-dialog': true, 'show-mode': isReadonly }"
    :disabled="isReadonly"
    append-to-body
    destroy-on-close
    @close="onClose"
    @values-change="onFormValueChange"
  >
    <el-tabs v-model="activeName">
      <el-tab-pane label="基础信息" name="basic">
        <FatFormGroup>
          <FatFormItem prop="name" label="步骤名称" :col="12" required> </FatFormItem>
          <FatFormItem label="步骤描述" :col="12" prop="desc"> </FatFormItem>
        </FatFormGroup>
        <div class="main-title">参数信息</div>
        <FatFormGroup>
          <FatFormItem
            prop="sqlOperatorParam.dataSourceId"
            label="选择数据源"
            :col="12"
            value-type="select"
            :value-props="{ options: DataSourceOptions }"
            required
          >
          </FatFormItem>
        </FatFormGroup>
        <FatFormGroup
          class="sql"
          label="SQL语句"
          tooltip="例：select ID, TASK_NAME from T_STREAM_TASK where ID = ${ID:long}"
          vertical
          required
        >
          <CodemirrorEditor
            ref="codeMirrorEditor"
            :refresh="visible"
            prop="apiSql"
            :config="apiSqlConfig"
            :is-readonly="isReadonly"
            @change-value="onInputChangeApiSql"
            @change-setting-value="onBlurChangeApiSql"
          >
            <template #example>
              <el-button type="text" class="example-btn" @click="openExample">示例值</el-button>
            </template>
          </CodemirrorEditor>
          <el-button v-if="!isReadonly" type="primary" class="bd-button" @click="handleSqlEnter">检测语句 </el-button>
        </FatFormGroup>
        <api-sql-params
          v-if="sqlParams.response.length"
          v-model="sqlParams"
          :select-params="selectParams"
          :select-method="selectMethod"
          :mode="mode"
          :request-tip="requestTip"
          is-flow
        />
      </el-tab-pane>
      <el-tab-pane label="Redis锁" name="redisLock">
        <redis-lock ref="redisLock" :data="redisData" :check-fields="sqlParams.request" :is-readonly="isReadonly" />
      </el-tab-pane>
    </el-tabs>

    <SelectParametersDialog
      :visible.sync="showParamsDialog"
      :task-info="taskInfo"
      :operator-id="operatorId"
      @selected="selected"
    >
    </SelectParametersDialog>
    <SelectMethodDialog
      :visible.sync="showMethodDialog"
      :expression="expressionVal"
      :task-info="taskInfo"
      :operator-id="operatorId"
      @selected="selectedMethod"
    ></SelectMethodDialog>
  </FatFormDrawer>
</template>

<script lang="tsx" setup>
  import { computed, ref, toRefs } from 'vue';

  import { MessageBox } from 'element-ui';
  import { FatFormDrawer, FatFormItem, FatFormGroup, useFatFormRef } from '@wakeadmin/components';
  import SelectParametersDialog from '@/components/select-parameter-dialog/index.vue';
  import SelectMethodDialog from '@/components/select-method-dialog/index.vue';
  import CodemirrorEditor from '@/bz-components/codemirror-editor/index.vue';
  import ApiSqlParams from '@/components/api-edit/api-sql-params.vue';
  import RedisLock from '@/bz-components/redis-lock.vue';

  import dataSource from '@/api/data-source';
  import { sqlPlacholderText } from '@/utils/sql-placholder';

  import {
    ModeEnum,
    ParamsType,
    RequestParams,
    ResponseParams,
    RequestOrParamsData,
    DataSource,
    RequestParamMappingsType,
  } from '@/utils/enum';
  import { paramsTransformToTree, treeTransformToParams } from '@/utils/api-http';
  import eventBus, { EventName } from '@/components/g6-editor/event-bus';
  import dataAnalysis from '@/api/data-analysis';
  import cloneDeep from 'lodash/cloneDeep';
  import { getIdFromItem } from '../utils';
  import { handleFilter } from '@/utils/api-http.js';

  const props = defineProps({
    visible: {
      type: Boolean,
      default: false,
    },
    config: {
      type: Object,
      default: () => ({}),
    },
    curNode: {
      type: Object,
      default: null,
    },
    taskInfo: {
      type: Object,
      default: () => ({}),
    },
    isReadonly: {
      type: Boolean,
      default: false,
    },
  });

  const { config } = toRefs(props);
  const redisData = ref(config.value.apiAttr);

  const requestTip = `参数为空时，系统在执行SQL内将不拼接使用此参数的条件及对应的查询字段，若勾选“允许为空”，则不启用此规则；`;
  const activeName = ref('basic');

  // 赋值类型弹窗显示
  // 引用值
  const showParamsDialog = ref(false);
  const paramsCallback = ref();

  // 表达式
  const showMethodDialog = ref(false);
  const expressionVal = ref('');
  const methodCallback = ref();

  // 选择的公共参数的算子id
  const selectNodeId = ref();

  // 选择数据源
  const DataSourceOptions = ref([]);
  // 表单实例
  const form = useFatFormRef();
  // sql语句
  const apiSqlConfig = ref({
    apiSql: props.config?.sqlOperatorParam?.sql ?? '',
  });

  // 请求参数和响应参数
  const sqlParams = ref();

  const operatorId = computed(() => {
    return getIdFromItem(props.curNode);
  });

  /**
   * sql语句操作类型
   */
  const sqlOperationType = ref();

  const saveDisabled = ref(!props.config.parameters.length);

  const init = () => {
    const list: RequestParams[] = [];
    const { parameters = [], requestParamMappings = [] } = props.config;
    parameters.forEach((item: RequestParams) => {
      const findRequestParamMapping = (requestParamMappings || []).find(_item => _item.field === item.assetColumns);
      const obj = findRequestParamMapping
        ? {
            ...item,
            operatorId: findRequestParamMapping.operatorId || 'start',
            expression: findRequestParamMapping.expression,
            paramValueType: findRequestParamMapping.type,
            fixedValue: findRequestParamMapping.fixedValue,
          }
        : item;
      list.push(obj);
    });
    // 请求参数和响应参数
    const arr = Object.entries(
      JSON.parse(props.config?.responseParams?.[0]?.responsePostData ?? '{}')?.root?.properties ?? {}
    );
    sqlParams.value = {
      request: list,
      response: (arr.length && treeTransformToParams(arr, [])) || [],
    };
  };

  // 初始化数据
  init();

  /**
   * 获取环境下拉数据
   */
  const getDataSourceOptions = async () => {
    const response = await dataSource.list();
    DataSourceOptions.value = response.data
      .filter((item: DataSource) => item.dbType !== 'HBASE')
      .map((item: DataSource) => {
        return {
          label: item.connectionName,
          value: item.id,
        };
      });
  };
  getDataSourceOptions();

  // 显示模式
  const mode = computed(() => {
    return (props.isReadonly && 'preview') || 'edit';
  });

  // 定义Emits
  const emits = defineEmits<{
    (e: 'update:visible', value: boolean): void;
    (e: 'save', value: any, model: any): void;
  }>();

  const onClose = () => {
    emits('update:visible', false);
  };

  const redisLock = ref<any | null>(null);

  const apiAttrObj = computed(() => {
    return redisLock.value.redisForm;
  });

  const validate = (values: any) => {
    if (!props.curNode || props.curNode.destroyed) {
      throw new Error('该节点不存在，无法保存，请重新选择节点。');
    }

    if (values.name === 'globalParam') {
      throw new Error('globalParam不能作为步骤名称。');
    }

    const operators = props.taskInfo.dataAssetApi.apiAttr.operators;
    const isFind = Object.keys(operators).some(key => {
      return operators[key].name === values.name && key !== operatorId.value;
    });
    if (isFind) {
      throw new Error('步骤名称不能重复，请重新设置。');
    }
  };

  // 提交表单
  const handleSubmit = async (values: any) => {
    const params = {
      ...values,
      sqlOperationType: sqlOperationType.value,
      parameters: [],
      results: [],
      responseParams: [],
      requestParamMappings: [],
    };
    params.sqlOperatorParam = {
      ...params.sqlOperatorParam,
      sql: apiSqlConfig.value.apiSql,
    };

    params.parameters = sqlParams.value.request;
    params.results = sqlParams.value.response.find((item: ResponseParams) => item.assetColumns === 'data')?.children;
    params.responseParams = [
      {
        assetColumns: 'body',
        assetDataType: 'json',
        type: 'BODY',
        responsePostData: JSON.stringify(paramsTransformToTree(sqlParams.value.response)),
      },
    ];
    params.requestParamMappings = params.parameters.map((obj: RequestParams): RequestParamMappingsType => {
      const item = {
        field: obj.assetColumns,
        operatorId: selectNodeId.value || 'start',
        httpParamKind: obj.httpParamKind,
        type: obj.paramValueType,
      };
      if (obj.paramValueType === ModeEnum.reference || obj.paramValueType === ModeEnum.method) {
        item.expression = obj.expression;
      } else {
        item.fixedValue = obj.fixedValue;
      }
      return item;
    });

    // redis锁相关
    params.apiAttr = apiAttrObj.value;
    params.apiAttr.configType = 2;
    await redisLock.value.validator();

    // 表单校验
    validate(values);

    emits('save', params, cloneDeep(props.curNode.getModel()));
    onClose();
  };

  // 表单值改变联动改变其他值
  const onFormValueChange = async (values: any, prop: string, value: number) => {
    if (prop === 'sqlOperatorParam.dataSourceId') {
      sqlParams.value = {
        request: [],
        response: [],
      };
      apiSqlConfig.value.apiSql = '';
      saveDisabled.value = true;
    }
  };

  // sql语句变动 失去焦点
  const onBlurChangeApiSql = () => {
    if (!saveDisabled.value) {
      saveDisabled.value = true;
    }
  };

  // sql语句输入变动
  const onInputChangeApiSql = () => {
    if (!saveDisabled.value) {
      saveDisabled.value = true;
    }
  };

  const sqlDataTransform = (response: RequestOrParamsData[], type: ParamsType) => {
    return (response || []).map(item => {
      const base = {
        assetColumns: item.alias || item.datasourceTableColumnName,
        assetDatatype: item.datasourceTableColumnType ?? 'string',
        required: item.required,
        sample: item.sample ?? '',
        descriptions: item.datasourceTableColumnDesc ?? '',
      };
      if (type !== ParamsType.REQUEST) {
        return base;
      } else {
        // 请求参数
        return {
          ...base,
          allowEmpty: !!item.allowEmpty,
          type: 'PARAMETERS',
          httpParamKind: 'QUERY',
        };
      }
    });
  };

  /**
   * 公共响应参数转换
   */
  const responseBase = (item: RequestOrParamsData) => {
    return {
      assetColumns: item.alias || item.datasourceTableColumnName,
      assetDatatype: item.datasourceTableColumnType ?? 'string',
      required: !!item.required,
      sample: item.sample ?? '',
      descriptions: item.datasourceTableColumnDesc ?? '',
    };
  };

  const sqlResponseDataTransform = (base: RequestOrParamsData[], response: RequestOrParamsData[]): ResponseParams[] => {
    return base.map(item => {
      let obj = responseBase(item);
      if (item.datasourceTableColumnName === 'data') {
        const responseData = sqlDataTransform(response, ParamsType.RESPONSE);
        obj = {
          ...obj,
          children: responseData,
        };
      }
      return obj;
    });
  };

  // 点击检测语句
  const handleSqlEnter = async () => {
    const { sqlOperatorParam } = form.value?.values;
    // 由于存在特定的一些占位符，格式化SQL后会出现多余空格而导致后端解析报错，故替换处理
    const formatApiSql = apiSqlConfig.value.apiSql.replace(/: /g, ':');
    const params = {
      sql: formatApiSql,
      dataSourceId: sqlOperatorParam.dataSourceId,
    };
    const res = await dataAnalysis.sql(params);
    const { requestParam, responseParam, baseResponseParam } = res.data;
    sqlOperationType.value = await res.data.sqlOperationType;
    // 请求参数
    sqlParams.value.request = handleFilter(sqlParams.value.request, sqlDataTransform(requestParam, ParamsType.REQUEST));

    // 响应参数
    sqlParams.value.response = handleFilter(
      sqlParams.value.response,
      sqlResponseDataTransform(baseResponseParam, responseParam)
    );
    saveDisabled.value = false;
  };

  const selectParams = (callback: Function) => {
    showParamsDialog.value = true;
    if (typeof callback === 'function') {
      paramsCallback.value = callback;
    }
  };
  const selected = ({ nodeId, data }: { nodeId: number; data: number }) => {
    selectNodeId.value = nodeId;
    paramsCallback.value?.({ operatorId: nodeId, id: data });
  };
  const selectMethod = (expression: string, callback: Function) => {
    showMethodDialog.value = true;
    expressionVal.value = expression;
    if (typeof callback === 'function') {
      methodCallback.value = callback;
    }
  };
  const selectedMethod = (expression: string) => {
    expressionVal.value = '';
    methodCallback.value?.(expression);
  };

  const openExample = () => {
    MessageBox.confirm(sqlPlacholderText, '帮助', {
      showCancelButton: false,
      showConfirmButton: false,
      customClass: 'example',
      dangerouslyUseHTMLString: true,
    });
  };
</script>
<style lang="less" scoped>
  .sql-execute-dialog {
    .main-title {
      height: 22px;
      font-size: 16px;
      margin: 10px 0;
      font-weight: 600;
      color: rgba(51, 51, 51, 1);
      line-height: 22px;
    }

    ::v-deep .sql {
      .fat-space__item {
        width: 100%;
      }
    }

    ::v-deep .example-btn {
      color: #2776fb !important;
    }
  }

  .sql-execute-dialog.show-mode {
    ::v-deep .fat-form-drawer__footer {
      display: none;
    }
  }

  :deep(.el-select__tags > span) {
    display: flex;
  }

  :deep(.el-select__tags > span:nth-child(1)) {
    width: calc(100% - 50px);
  }
</style>
