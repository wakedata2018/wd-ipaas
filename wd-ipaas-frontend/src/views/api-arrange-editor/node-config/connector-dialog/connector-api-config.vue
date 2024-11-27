<!-- eslint-disable vue/no-mutating-props -->
<template>
  <el-form
    ref="form"
    :model="config"
    label-position="right"
    label-width="140px"
    inline
    :disabled="isReadonly"
    @submit.native.prevent
  >
    <template v-if="config.connectorApi">
      <div class="main-title">接口信息</div>
      <div class="api-info-block">
        <div class="main-tips">
          {{ config.connectorApi.connectorName }}
          <span style="font-weight: bold">
            【{{ config.connectorApi.apiGroupName }}】
            {{ config.connectorApi.apiName }}
          </span>
        </div>
        <el-form-item class="interface-input" prop="connectorApi.apiMethod">
          <el-input
            v-model="config.connectorApi.apiMethod"
            maxlength="220"
            type="text"
            disabled
            placeholder="请输入接口地址"
          >
            <template slot="prepend">
              {{ config.connectorApi.reqMethod }}
            </template>
          </el-input>
        </el-form-item>
      </div>

      <div class="main-title">连接器信息</div>
      <div class="connector-form-block">
        <el-form-item label="选择密钥" prop="connectorSecretKey.secretKey" :rules="rule">
          <el-select v-model="config.connectorSecretKey.secretKey" placeholder="请选择" @change="handleKey">
            <el-option v-for="item in keyList" :key="item.secretKey" :label="item.name" :value="item.secretKey">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="连接器名称" prop="connectorApi.connectorName">
          【{{ config.connectorApi.connectorName }}】
        </el-form-item>

        <br />

        <el-form-item label="endpoint" prop="connectorSecretKey.environmentAddress">
          <el-tooltip
            effect="dark"
            :content="config.connectorSecretKey.environmentAddress"
            :disabled="isShowEnvironmentAddressTooltip"
          >
            <el-input
              v-model="config.connectorSecretKey.environmentAddress"
              maxlength="200"
              disabled
              placeholder="不超过200个字符"
            />
          </el-tooltip>
        </el-form-item>
        <br />

        <div v-for="item in connectorSecretKeyData" :key="item.label" class="dynamic-items">
          <el-form-item :label="item.label">
            <el-input v-model="item.value" maxlength="200" disabled placeholder="不超过200个字符" />
          </el-form-item>
          <br />
        </div>
      </div>

      <div class="main-title">请求参数</div>
      <api-params-detail-table
        ref="apiParamsDetailTable"
        :config.sync="requestTable"
        :api-detail="config"
        :is-readonly="isReadonly"
        :can-add="false"
        :task-info="taskInfo"
        :params-map="paramsMap"
        type="api_connector"
      />
      <!-- :operation-list="httpConf" -->
      <br />

      <div class="main-title">返回参数</div>
      <api-response-edit ref="response" v-model="responseData" mode="preview" :is-readonly="isReadonly" />
    </template>
  </el-form>
</template>

<script>
  import api from '@/api/api-key-manage';
  import { ApiResponseEdit } from '@/components/api-edit';
  import { INIT_ELE, TYPE_ELE } from '@/utils/enum';
  import ApiParamsDetailTable from '../components/api-params-detail-table.vue';
  import { getIdFromItem } from '../../utils';

  export default {
    name: 'ConnectorApiConfig',
    components: { ApiParamsDetailTable, ApiResponseEdit },
    inject: ['curNode'],

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
        INIT_ELE,
        TYPE_ELE,
        rules: {},
        keyList: [],
        requestTable: [],
        responseData: [],
        connectorSecretKeyData: [],
        rule: {
          message: '请选择密钥',
          required: true,
        },
        paramsMap: {
          dataType: 'assetDataType',
        },
      };
    },

    computed: {
      isShowEnvironmentAddressTooltip() {
        const val = this.config.connectorSecretKey.environmentAddress || '';
        const tooltipsLength = 38; // 估算的一个长度
        return val.length < tooltipsLength;
      },
    },

    watch: {
      refresh() {
        this.fetchSecretById();
      },
      'config.connectorSecretKey'(val) {
        this.$nextTick(() => {
          this.initConnectorSecretKey(val);
        });
      },
      config: {
        deep: true,
        immedate: true,
        handler() {
          if (!this.requestTable.length) {
            this.getColumn();
          }
        },
      },
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
      initConnectorSecretKey(item) {
        this.$emit('setConnectorSecretKey', item);
        this.formatSecretKeyData(item.paramsJson);
      },

      handleKey(key) {
        const item = this.findKeyItem(key);
        this.$emit('setConnectorSecretKey', item);
        this.formatSecretKeyData(item.paramsJson);
      },

      findKeyItem(key) {
        const arr = this.keyList;
        let res = {};
        arr.forEach(item => {
          if (item.secretKey === key) {
            res = item;
          }
        });
        return res;
      },

      fetchSecretById() {
        const { connectorId } = this.config.connectorApi;
        api.fetchSecretById({ connectorId }).then(res => {
          this.keyList = res.data ?? [];
        });
      },

      formatSecretKeyData(objJson) {
        const obj = objJson ? JSON.parse(objJson) : {};
        const res = [];
        for (const key in obj) {
          res.push({
            label: key,
            value: obj[key],
          });
        }
        this.connectorSecretKeyData = res;
      },

      externalApiPath(rule, value, callback) {
        const regRule = /^[_\-0-9a-zA-Z.]+$/g;
        const propLabel = '接口地址';
        if (!value) {
          return callback(new Error(`${propLabel}不能为空`));
        } else if (value.length > 200) {
          callback(new Error(`${propLabel}过长,不能超过200个字符`));
        } else if (!regRule.test(value)) {
          return callback(new Error(`${propLabel}格式不正确`));
        }

        return callback();
      },

      // 处理 requestTable & responseData 数据格式
      async getColumn(config = this.config) {
        const { requestParams = [], responseParams = [] } = config;
        let mappings = requestParams;
        // 将编辑的值回显到表单中，这个时候还没有保存，编辑的值临时保存在store的flowDetail中
        const nodeId = getIdFromItem(this.curNode());
        const operator = this.taskInfo.dataAssetApi.apiAttr?.operators?.[nodeId];
        if (operator) {
          const newRequestParamMappings = operator.requestParamMappings;
          mappings = newRequestParamMappings;
        }

        const list = [];
        requestParams.forEach(item => {
          const findRequestParamMapping = (mappings || []).find(
            _item => _item && _item.field.toLowerCase() === item.assetColumns.toLowerCase()
          );
          let obj = findRequestParamMapping
            ? {
                ...item,
                operatorId: findRequestParamMapping.operatorId || 'start',
                expression: findRequestParamMapping.expression,
                paramValueType: findRequestParamMapping.type,
                fixedValue: findRequestParamMapping.fixedValue,
              }
            : item;
          if (obj.httpParamKind === 'BODY') {
            obj = {
              ...obj,
              dataType: 'object',
            };
          }
          list.push(obj);
        });
        this.requestTable = list;
        this.responseData = responseParams;
      },
      getRequestTable() {
        return this.requestTable ?? [];
      },
      getResponseTable() {
        const response = this.responseData?.length ? this.responseData[0]?.childApiRespParams : null;
        let newResponse = [];
        if (response) {
          response.forEach(o => {
            newResponse = newResponse.concat(o.childApiRespParams);
          });
        }
        return newResponse;
      },
      validateDetailTable() {
        return this.$refs.form.validate();
      },
    },
  };
</script>

<style lang="less" scoped>
  .api-info-block {
    .main-tips {
      margin-left: 70px;
      margin-bottom: 10px;
    }
    .interface-input {
      width: 419px;
      margin-left: 70px;
      /deep/ .el-form-item__content {
        width: 100%;
      }
      /deep/ .el-input-group__prepend {
        background: #efefef;
      }
      /deep/ .el-input {
        width: 100% !important;
      }
    }
  }

  .connector-form-block {
    /deep/ .el-input,
    /deep/ .el-input-number,
    /deep/ .el-select {
      width: 350px;
    }
    /deep/ .el-textarea {
      width: 650px;
    }
  }
</style>
