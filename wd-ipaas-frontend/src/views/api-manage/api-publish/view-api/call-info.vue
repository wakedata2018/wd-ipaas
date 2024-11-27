<template>
  <div>
    <api-http
      mode="fixed"
      :show-body="reqMethod === 'POST'"
      :body-data="bodyData"
      :parameters="parameters"
      :response-data="responseList"
    >
    </api-http>
  </div>
</template>
<script>
  import ApiHttp from '../api-http.vue';
  import { getReponseDataList } from '@/utils/api-http.js';

  export default {
    components: { ApiHttp },
    props: {
      apiDetail: {
        type: Object,
        default: () => ({}),
      },
    },
    data() {
      return {
        activeName: 'param',
      };
    },
    computed: {
      /**
       * 所有参数
       */
      allParameters() {
        return this.apiDetail?.requiredInput.concat(this.apiDetail?.optionalInput);
      },
      /**
       * 请求方式
       */
      reqMethod() {
        return this.apiDetail?.method ?? '';
      },
      /**
       * Body请求参数改造
       */
      bodyData() {
        const postBody = this.allParameters?.find(item => item.httpParamKind === 'BODY');
        if (postBody) {
          return {
            bodyType: 'json',
            tree: JSON.parse(postBody.jsonSchema),
          };
        } else {
          return {
            bodyType: 'json',
            tree: {
              root: {
                type: 'object',
                name: 'root',
                description: '根层级',
              },
            },
          };
        }
      },
      parameters() {
        return this.allParameters?.filter(item => item.httpParamKind !== 'BODY') ?? [];
      },
      /**
       * 响应参数
       */
      responseList() {
        const isHttpResponse = this.apiDetail?.httpResponse.length;
        const response = (isHttpResponse && this.apiDetail?.httpResponse) || this.apiDetail?.liftFlowResponse || [];
        return getReponseDataList(response);
      },
    },
    methods: {},
  };
</script>

<style lang="less" scoped>
  @import './style.less';

  .el-tabs {
    /deep/ .el-tabs__nav .el-tabs__active-bar {
      height: 1px;
    }
  }

  .title {
    font-weight: 600;
  }
</style>
