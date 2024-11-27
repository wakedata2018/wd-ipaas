<template>
  <el-form ref="form" :model="config" label-position="right" label-width="140px" inline @submit.native.prevent>
    <template v-if="dataAssetApi.apiAttr">
      <div class="main-title">后端服务定义</div>

      <el-form-item label="接口域名" prop="dataAssetApi.apiAttr.host">
        <template slot="label">
          <tips-icon content="以http://或https://开头，并且不包含Path"></tips-icon>
          接口域名
        </template>
        <el-input
          v-model="dataAssetApi.apiAttr.host"
          maxlength="200"
          type="text"
          :disabled="disabled"
          placeholder="请输入接口域名"
          class="interface-input"
        />
      </el-form-item>
      <el-form-item label="接口地址" prop="dataAssetApi.apiAttr.path">
        <template slot="label">
          <tips-icon :content="$t('validator.nameWithoutChineseValidateDesc')"></tips-icon>
          接口地址
        </template>
        <el-input
          v-model="dataAssetApi.apiAttr.path"
          maxlength="220"
          type="text"
          :disabled="disabled"
          placeholder="请输入接口地址"
          class="interface-input"
        />
      </el-form-item>
      <br />
      <el-form-item
        v-if="dataAssetApi.apiType == 'WEB_SERVICE'"
        class="main-form-item"
        label="方法名"
        prop="dataAssetApi.apiAttr.wsMethod"
      >
        <el-input
          v-model="dataAssetApi.apiAttr.wsMethod"
          maxlength="100"
          type="text"
          placeholder="请输入方法名"
          :disabled="disabled"
        />
      </el-form-item>
      <el-form-item
        v-if="dataAssetApi.apiType == 'WEB_SERVICE'"
        class="main-form-item"
        label="命名空间"
        prop="dataAssetApi.apiAttr.wsNameSpaceUri"
      >
        <el-input
          v-model="dataAssetApi.apiAttr.wsNameSpaceUri"
          maxlength="100"
          type="text"
          placeholder="请输入命名空间"
          :disabled="disabled"
        />
      </el-form-item>
      <br />
      <!-- <el-form-item label="正常返回结果示例" prop="dataAssetApi.apiAttr.resultExample">
        <el-input
          v-model="dataAssetApi.apiAttr.resultExample"
          maxlength="200"
          type="textarea"
          :disabled="disabled"
          placeholder="请输入正常返回结果示例"
        />
      </el-form-item>
      <el-form-item label="异常返回结果示例" prop="dataAssetApi.apiAttr.errorExample">
        <el-input
          v-model="dataAssetApi.apiAttr.errorExample"
          maxlength="200"
          type="textarea"
          :disabled="disabled"
          placeholder="请输入异常返回结果示例"
        />
      </el-form-item>
      <br /> -->
      <el-form-item label="响应超时设置" prop="dataAssetApi.apiAttr.timeout">
        <el-input-number v-model="dataAssetApi.apiAttr.timeout" :min="-1" :max="999999" :disabled="disabled">
        </el-input-number>
        毫秒，最大输入999999
      </el-form-item>
      <div class="main-title">请求参数</div>
      <api-params-detail-table
        ref="apiParamsDetailTable"
        :config.sync="requestTable"
        :api-detail="config"
        :refresh="refresh"
        :is-readonly="isReadonly"
        :is-edit="isEdit"
        :task-info="taskInfo"
        :params-map="paramsMapConfig"
        type="api_http"
      />
      <br />

      <!-- <div class="main-title">错误码定义</div>
      <http-error-code-table
        ref="httpErrorCodeTable"
        :table-list="dataAssetApi.apiAttr.httpCodes"
        :disabled="disabled"
      /> -->
      <div class="main-title">返回参数</div>
      <api-response-edit ref="response" v-model="responseData" mode="preview" :is-readonly="isReadonly" />
    </template>
  </el-form>
</template>

<script>
  import { ApiResponseEdit } from '@/components/api-edit';
  import TipsIcon from '@/components/tips-icon';
  import { INIT_ELE, TYPE_ELE } from '@/utils/enum';
  import ApiParamsDetailTable from '../components/api-params-detail-table.vue';
  import { getIdFromItem } from '../../utils';

  export default {
    components: { ApiParamsDetailTable, ApiResponseEdit, TipsIcon },
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
        /**
         * 混合了API详情的请求和返回参数以及画布的字段映射参数。
         */
        requestTable: [],
        responseData: null,
        paramsMapConfig: {
          dataType: 'assetDatatype',
          description: 'descriptions',
        },
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
    },
    watch: {
      refresh() {
        this.getColumn();
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
      externalApiPath(rule, value, callback) {
        const regRule = /^[/_\-0-9a-zA-Z.]+$/g;
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
      async getColumn() {
        const { parameters: configParameters = [], requestParamMappings = [], responseParams = [] } = this.config;
        let mappings = requestParamMappings;
        // 将编辑的值回显到表单中，这个时候还没有保存，编辑的值临时保存在store的flowDetail中
        const nodeId = getIdFromItem(this.curNode());
        const operator = this.taskInfo.dataAssetApi.apiAttr?.operators?.[nodeId];
        if (operator) {
          const newRequestParamMappings = operator.requestParamMappings;
          if (newRequestParamMappings !== requestParamMappings) {
            mappings = newRequestParamMappings;
          }
        }

        const list = [];
        configParameters.forEach(item => {
          const findRequestParamMapping = (mappings || []).find(_item => _item.field === item.assetColumns);
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
        this.responseData = responseParams;
        // this.setReponseData(responseParams);
      },
      // async setReponseData(data) {
      //   const headData = this.$plain(this.TYPE_ELE.HEAD);
      //   const bodyData = this.$plain(this.TYPE_ELE.BODY);

      //   if (data?.length) {
      //     const setKey = arr => {
      //       arr.forEach(o => {
      //         o.key = `${o.assetColumns}_${Math.floor(Math.random() * 99999)}`;
      //         if (o.childApiRespParams.length) setKey(o.childApiRespParams);
      //       });
      //       return arr;
      //     };

      //     const cos = await setKey(data);
      //     const head = cos.filter(p => p.type === this.TYPE_ELE.HEAD.type);
      //     const body = cos.filter(p => p.type === this.TYPE_ELE.BODY.type);

      //     headData.childApiRespParams = head;
      //     bodyData.childApiRespParams = body;
      //   }
      //   const newData = [
      //     {
      //       ...this.TYPE_ELE.JSON_OBJECT,
      //       childApiRespParams: [headData, bodyData],
      //     },
      //   ];
      //   this.responseData = newData;
      // },
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
        return this.$refs.apiParamsDetailTable.validate();
      },
      saveData() {
        return this.$refs.apiParamsDetailTable?.saveData();
      },
    },
  };
</script>

<style lang="less" scoped>
  /deep/ .el-textarea {
    width: 650px;
  }

  .interface-input {
    ::v-deep {
      width: 250px;
    }
  }
</style>
