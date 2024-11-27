<template>
  <el-form ref="form" :model="config" @submit.native.prevent>
    <div class="main-title">选择表</div>
    <el-form-item label="选择数据源" prop="dataAssetApi.dataSourceId" label-width="140px">
      <el-select
        v-loading="optionsSource.loading"
        :value="config.dataAssetApi.dataSourceId"
        filterable
        disabled
        placeholder="请选择数据源"
      >
        <el-option
          v-for="item in optionsSource.list"
          :key="item.id"
          :label="item.connectionName"
          :value="item.id"
        ></el-option>
      </el-select>
    </el-form-item>
    <el-form-item prop="dataAssetApi.apiSql" label="查询SQL" label-width="140px">
      <template slot="label">
        <tips-icon :content="'例：select ID, TASK_NAME from T_STREAM_TASK where ID = ${ID:long}'"></tips-icon>
        SQL语句
      </template>
      <CodemirrorEditor
        ref="codeMirrorEditor"
        prop="apiSql"
        :refresh="refresh"
        :config="config.dataAssetApi"
        is-readonly
      />
    </el-form-item>
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

<script>
  import dataSourceApi from '@/api/data-source';
  import { ApiResponseEdit } from '@/components/api-edit';
  import CodemirrorEditor from '@/bz-components/codemirror-editor';
  import TipsIcon from '@/components/tips-icon';
  import ApiParamsDetailTable from '../components/api-params-detail-table.vue';

  export default {
    name: 'CustomSqlConfig',
    components: { ApiParamsDetailTable, ApiResponseEdit, TipsIcon, CodemirrorEditor },
    props: {
      config: {
        type: Object,
        default: null,
      },
      taskInfo: {
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
        optionsSource: {
          list: [],
          loading: false,
        },
        /**
         * 混合了API详情的请求和返回参数以及画布的字段映射参数。
         */
        requestTable: [],
        responseData: [],
      };
    },
    computed: {
      paramsMapConfig() {
        return {
          dataType: 'assetDatatype',
          description: 'descriptions',
        };
      },
      reqMethod() {
        return this.config.dataAssetApi.reqMethod;
      },
    },
    watch: {
      refresh() {
        this.getColumn();
      },
    },
    created() {
      this.showDataSource();
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
      showDataSource() {
        this.optionsSource.loading = true;
        dataSourceApi
          .list()
          .then(res => {
            // CUSTOM_SQL限制类型为Hbase的数据源只支持数据表类型的API
            this.optionsSource.list = res.data.filter(item => item.dbType !== 'HBASE');
          })
          .catch(() => {
            this.optionsSource.list = [];
          })
          .always(() => {
            this.optionsSource.loading = false;
          });
      },
      getRequestTable() {
        return this.requestTable ?? [];
      },
      getColumn(config = this.config) {
        const { parameters: configParameters = [], requestParamMappings = [], responseParams = [] } = config;

        this.requestTable = configParameters;
        this.responseData = responseParams;

        if (this.reqMethod === 'GET') {
          const list = [];
          configParameters.forEach(item => {
            const findRequestParamMapping = (requestParamMappings || []).find(
              _item => _item.field === item.assetColumns
            );
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
          this.requestTable = list;
        }
      },

      saveData() {
        return this.$refs.apiParamsDetailTable.saveData();
      },
    },
  };
</script>
