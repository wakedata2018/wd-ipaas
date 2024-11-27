<template>
  <el-form
    ref="form"
    :model="config"
    :rules="rules"
    label-position="right"
    label-width="140px"
    inline
    @submit.native.prevent
  >
    <div class="main-title">选择表</div>
    <el-form-item label="数据源" prop="dataAssetApi.dataSourceId" style="width: auto; display: inline-block">
      <el-select
        v-model="dataAssetApi.dataSourceId"
        v-loading="optionsSource.loading"
        :disabled="disabled"
        filterable
        placeholder="请选择数据源"
        @change="handleChangeDataSource()"
      >
        <el-option
          v-for="item in optionsSource.list"
          :key="item.id"
          :label="item.connectionName"
          :value="item.id"
        ></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="选择数据表" prop="dataAssetApi.dataAssetName" style="width: auto; display: inline-block">
      <el-button
        type="primary"
        icon="el-icon-coin"
        :disabled="!dataAssetApi.dataSourceId || disabled"
        @click="changeDataAssetName"
      >
        {{ dataAssetApi.dataAssetName ? dataAssetApi.dataAssetName : '请选择数据表' }}
      </el-button>
    </el-form-item>
    <br />
    <el-form-item label="操作类型" required prop="dataAssetApi.operationType">
      <el-radio-group v-model="dataAssetApi.operationType" :disabled="disabled">
        <el-radio v-for="i in BaseOperate.list" :key="i.value" :label="i.value">{{ i.label }}</el-radio>
      </el-radio-group>
    </el-form-item>

    <div class="main-title">请求参数</div>

    <api-params-detail-table
      ref="apiParamsDetailTable"
      :config.sync="requestTable"
      :api-detail="config"
      :refresh="refresh"
      :is-readonly="isReadonly"
      :task-info="taskInfo"
      :can-add="false"
      :params-map="paramsMapConfig"
      type="api_http"
    />

    <div class="main-title">返回参数</div>
    <api-response-edit ref="response" v-model="responseData" mode="preview" :is-readonly="isReadonly" />
  </el-form>
</template>
<!-- eslint-disable vue/no-mutating-props -->
<script>
  import dataSourceApi from '@/api/data-source.js';
  import { ApiResponseEdit } from '@/components/api-edit';
  import { BaseOperate } from '@/enum';
  import { getIdFromItem } from '@/views/api-arrange-editor/utils';
  import ApiParamsDetailTable from '../components/api-params-detail-table.vue';

  export default {
    name: 'NormalTableConfig',
    components: { ApiParamsDetailTable, ApiResponseEdit },
    props: {
      config: {
        type: Object,
        default: null,
      },
      taskInfo: {
        type: Object,
        default: () => ({}),
      },
      curNode: {
        type: Object,
        default: () => ({}),
      },
      refresh: {
        type: Boolean,
        default: false,
      },
      isReadonly: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        rules: {
          'dataAssetApi.dataSourceId': [{ required: true, message: '请选择数据源名称' }],
          'dataAssetApi.dataAssetName': [{ required: true, message: '数据表不能为空' }],
        },
        optionsSource: {
          list: [],
          loading: false,
        },
        dialog: {
          tableSelector: false,
        },
        /**
         * 混合了API详情的请求和返回参数以及画布的字段映射参数。
         */
        requestAndResponseTable: {
          parameters: [],
          results: [],
          filters: [],
        },
        columnLoading: false,
        dataAssetActive: BaseOperate.list[0].value,
        BaseOperate,
        selectedSource: {},

        /**
         * 混合了API详情的请求和返回参数以及画布的字段映射参数。
         */
        requestTable: [],
        responseData: [],
      };
    },
    computed: {
      dataAssetApi() {
        return this.config?.dataAssetApi ?? {};
      },
      isEdit() {
        return this.dataAssetApi?.dataAssetApiId != null;
      },
      disabled() {
        return this.isReadonly || this.isEdit;
      },
      operatorId() {
        return getIdFromItem(this.curNode) || 'start';
      },
      paramsMapConfig() {
        return {
          dataType: 'assetDatatype',
          description: 'descriptions',
        };
      },
    },
    watch: {
      refresh() {
        this.getColumn();
      },
    },
    created() {
      this.showDataSource();
      this.getSelectedDataSource();
    },
    methods: {
      clearValidate(keys) {
        this.$nextTick(() => {
          this.$refs.form?.clearValidate(keys);
        });
      },
      validate() {
        return this.$refs.form?.validate();
      },
      changeSelection(results, parameters, filters) {
        this.requestAndResponseTable = {
          parameters,
          results,
          filters,
        };
      },
      showDataSource() {
        this.optionsSource.loading = true;
        dataSourceApi
          .list()
          .then(res => {
            this.optionsSource.list = res.data;
          })
          .catch(() => {
            this.optionsSource.list = [];
          })
          .always(() => {
            this.optionsSource.loading = false;
          });
      },
      changeDataAssetName() {
        this.dialog.tableSelector = true;
      },
      getSelectedDataSource() {
        this.selectedSource = {};
        const arr = this.optionsSource.list.filter(item => item.id === this.config.dataAssetApi.dataSourceId);
        if (!!arr && arr.length > 0) {
          this.selectedSource = arr[0];
        }
      },
      handleChangeDataSource() {
        this.config.results = [];
        this.config.parameters = [];
        this.config.dataAssetApi.dataAssetName = null;
        /**
         * 重新生成请求参数
         */
      },

      getRequestTable() {
        return this.requestTable ?? [];
      },

      getColumn(config = this.config) {
        const { parameters: configParameters = [], requestParamMappings = [], responseParams = [] } = config;

        this.requestTable = configParameters;
        this.responseData = responseParams;

        if (this.dataAssetApi.reqMethod === 'GET') {
          const list = [];
          configParameters.forEach(item => {
            const findRequestParamMapping = (requestParamMappings || []).find(_item => {
              let name = _item.field;
              if (item.typeAttr === 'FILTER') {
                name = _item.field.substring(7);
              }
              return name === item.assetColumns;
            });

            const obj = findRequestParamMapping
              ? {
                  ...item,
                  operatorId: findRequestParamMapping.operatorId || 'start',
                  expression: findRequestParamMapping.expression,
                  paramValueType: findRequestParamMapping.type,
                  fixedValue: findRequestParamMapping.fixedValue,
                }
              : item;
            /**
             * 重名做特殊处理
             */
            if (item.typeAttr === 'FILTER') {
              obj.assetColumns = `filter_${item.assetColumns}`;
            }
            list.push(obj);
          });
          this.requestTable = list;
        }
      },

      saveData() {
        return this.$refs.apiParamsDetailTable.saveData();
      },
    },
  };
</script>

<style lang="less" scoped></style>
