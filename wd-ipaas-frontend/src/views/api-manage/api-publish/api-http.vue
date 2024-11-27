<template>
  <div class="api-http">
    <el-form ref="form" :model="form">
      <div class="main-title">请求参数</div>
      <el-tabs v-model="activePane">
        <el-tab-pane label="请求头" name="head">
          <api-params-table
            ref="requestHeadForm"
            v-model="requestHeadParam"
            :params-map="paramsMapConfig"
            params-type="HEAD"
            :fixed-list="fixedList"
            :hidden-list="hiddenList"
            :mode="mode"
          ></api-params-table>
        </el-tab-pane>
        <el-tab-pane label="QUERY参数" name="query">
          <api-params-table
            ref="queryForm"
            v-model="queryParam"
            :params-map="paramsMapConfig"
            :mode="mode"
            params-type="QUERY"
            :fixed-list="fixedList"
            :hidden-list="hiddenList"
          ></api-params-table>
        </el-tab-pane>
        <el-tab-pane v-if="showBody" label="请求体" name="body">
          <JsonSchemaShow v-if="isPreview && bodyData.tree" :value="bodyData.tree" root />
          <request-body v-else-if="!isPreview" :tree="bodyData.tree" :preview="isPreview"> </request-body>
        </el-tab-pane>
      </el-tabs>
      <!-- <div class="main-title" >错误码定义</div>
      <el-table :data="httpCodes" style="width: 100%" class="dss-table bd-table">
        <el-table-column prop="errorCode" label="错误码">
          <template slot-scope="scope">
            <el-form-item :prop="'errorList.' + scope.$index + '.errorCode'">
              <el-input-number
                v-model="scope.row.errorCode"
                style="width: 100%"
                :min="0"
                :max="99999"
                :disabled="isReadonly"
              ></el-input-number>
            </el-form-item>
          </template>
        </el-table-column>
        <el-table-column prop="errorMsg" label="错误信息">
          <template slot-scope="scope">
            <el-form-item :prop="'errorList.' + scope.$index + '.errorMsg'">
              <el-input v-model="scope.row.errorMsg" style="width: 100%" maxlength="256" :disabled="isReadonly">
              </el-input>
            </el-form-item>
          </template>
        </el-table-column>
        <el-table-column prop="solution" label="解决方案 ">
          <template slot-scope="scope">
            <el-form-item :prop="'errorList.' + scope.$index + '.solution'">
              <el-input v-model="scope.row.solution" style="width: 100%" maxlength="256" :disabled="isReadonly">
              </el-input>
            </el-form-item>
          </template>
        </el-table-column>
        <el-table-column label="操作" v-if="!isReadonly">
          <template slot-scope="scope">
            <el-button @click="onDeleteError(scope.$index, scope.row)" class="bd-button bd-table-danger"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table> -->
      <div class="main-title">响应参数</div>
      <!-- API详情 -->
      <api-http-response-detail v-if="isPreview" :value="response" />
      <!-- API新增编辑 -->
      <api-response-edit v-else ref="response" v-model="response" />
    </el-form>
  </div>
</template>

<!-- eslint-disable vue/no-mutating-props -->
<script>
  import Validator from '@/utils/validator';
  import { ApiParamsTable, BodyTree as RequestBody, ApiResponseEdit } from '@/components/api-edit/index.js';
  import ApiHttpResponseDetail from './api-http-response-detail.vue';
  import JsonSchemaShow from './view-api/json-schema-show.vue';

  export default {
    components: { RequestBody, ApiParamsTable, ApiHttpResponseDetail, ApiResponseEdit, JsonSchemaShow },
    props: {
      parameters: {
        type: Array,
        default: () => [],
      },
      httpCodes: {
        type: Array,
        default: () => [],
      },
      responseData: {
        type: Array,
        default: () => [],
      },
      isReadonly: {
        type: Boolean,
        default: false,
      },
      /**
       * 请求体参数
       */
      bodyData: {
        type: Object,
        default: () => ({}),
      },
      showBody: {
        type: Boolean,
        default: false,
      },
      /**
       * 固定列
       */
      fixedList: {
        type: Array,
        default: () => [],
      },
      /**
       * 隐藏列
       */
      hiddenList: {
        type: Array,
        default: () => [],
      },
      /**
       * 显示模式：编辑模式 | 预览模式 | 固定模式
       * edit | preview | fixed
       */
      mode: {
        type: String,
        default: 'edit',
      },
    },
    data() {
      return {
        form: {
          errorList: this.httpCodes,
        },
        // 参数设置别名
        paramsMapConfig: {
          dataType: 'assetDatatype',
          description: 'descriptions',
        },
        activePane: 'query',
      };
    },
    computed: {
      /**
       * 请求头参数
       */
      requestHeadParam: {
        get() {
          return this.parameters?.filter(item => item.httpParamKind === 'HEAD') ?? [];
        },
        set(val) {
          const newVal = [...this.queryParam, ...val];
          this.$emit('update:parameters', newVal);
        },
      },
      /**
       * QUERRY参数
       */
      queryParam: {
        get() {
          return (
            this.parameters?.filter(item => item.httpParamKind === 'QUERY' || item.httpParamKind === 'FILTER') ?? []
          );
        },
        set(val) {
          const newVal = [...this.requestHeadParam, ...val];
          this.$emit('update:parameters', newVal);
        },
      },
      response: {
        get() {
          return this.responseData;
        },
        set(val) {
          this.$emit('update:responseData', val);
        },
      },
      /**
       * 请求体预览模式
       */
      isPreview() {
        return this.mode === 'fixed';
      },
    },
    watch: {
      showBody: {
        immediate: true,
        handler(val) {
          if (!val) {
            this.activePane = 'query';
          } else {
            this.activePane = 'body';
          }
        },
      },
    },
    methods: {
      onAddRowError() {
        this.httpCodes.push({});
      },
      onDeleteError(index, info) {
        if (this.httpCodes.length > 0) {
          this.httpCodes.splice(index, 1);
        }
      },
      validateParams() {
        let index = -1;
        let repeat = false;
        const fieldArr = [];
        const parameters = this.parameters;
        for (let i = 0; i < parameters.length > 0; i++) {
          const item = parameters[i];
          if (
            !item.assetColumns ||
            !Validator.httpFieldEnNameValidatorFun(item.assetColumns).result ||
            !item.httpParamKind ||
            !item.assetDatatype
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
      validateError() {
        let index = -1;
        let repeat = false;
        const fieldArr = [];
        const parameters = this.httpCodes || [];
        for (let i = 0; i < parameters.length > 0; i++) {
          const item = parameters[i];
          if (!item.errorCode) {
            index = i;
            break;
          }
          if (fieldArr.indexOf(item.errorCode) !== -1) {
            repeat = true;
            break;
          }
          fieldArr.push(item.errorCode);
        }
        const found = index !== -1 || repeat;
        return !found;
      },
      async validate() {
        const results = await Promise.allSettled([
          this.$refs.requestHeadForm.validate(),
          this.$refs.queryForm.validate(),
          this.$refs.response.validate(),
        ]);
        for (const item of results) {
          if (item.status === 'rejected') {
            /**
             * 抛出错误
             */
            throw item.reason;
          }
        }
      },
    },
  };
</script>

<style lang="less" scoped>
  .api-http {
    .main-title {
      height: 22px;
      font-size: 16px;
      margin: 10px 0;
      font-weight: 600;
      color: rgba(51, 51, 51, 1);
      line-height: 22px;
    }

    .empty {
      text-align: center;
      display: block;
      color: #909399;
      font-size: 12px;
    }

    .btn-box {
      margin-top: 10px;
    }
  }
</style>
