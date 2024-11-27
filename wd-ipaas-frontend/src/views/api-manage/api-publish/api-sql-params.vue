<template>
  <div class="api-sql-params">
    <div v-if="isShowRequest" class="main-title">请求参数</div>
    <api-params-tree-table
      v-if="isShowRequest"
      ref="queryForm"
      :value="value.request"
      table-type="request"
      params-type="QUERY"
      :can-add="false"
      :can-operation="false"
      :fixed-list="requestFixList"
      :disable-list="requestDisableList"
      :params-map="paramsMap"
      :is-flow="isFlow"
      :mode="mode"
      @select-params="selectParams"
      @select-method="selectMethod"
    ></api-params-tree-table>
    <div class="main-title">响应参数</div>
    <api-params-tree-table
      ref="responseForm"
      :value="value.response"
      table-type="response"
      params-type="RESPONSE"
      :can-add="false"
      :can-operation="false"
      :hidden-list="responseHiddenList"
      :fixed-list="responseFixList"
      :params-map="paramsMap"
      :mode="mode"
      is-object-root-disable
    ></api-params-tree-table>
  </div>
</template>

<script>
  import { ApiParamsTreeTable } from '@/components/api-edit/index.js';

  export default {
    components: { ApiParamsTreeTable },
    props: {
      value: {
        type: Object,
        default: () => ({}),
      },
      isReadonly: {
        type: Boolean,
        default: false,
      },
      /**
       * 是否是流程编排中的参数
       */
      isFlow: {
        type: Boolean,
        default: false,
      },
      /**
       * 显示模式：编辑模式 | 预览模式 | 固定模式
       * edit | preview | fixed
       */
      mode: {
        type: String,
        default: 'edit',
      },
      selectParams: {
        type: Function,
        default: () => ({}),
      },
      selectMethod: {
        type: Function,
        default: () => ({}),
      },
    },

    data() {
      return {
        paramsMap: {
          dataType: 'assetDatatype',
          description: 'descriptions',
        },
      };
    },
    computed: {
      isShowRequest() {
        return !!this.value.request?.length;
      },
      requestFixList() {
        return ['assetColumns', 'assetDatatype'];
      },
      responseFixList() {
        return ['assetColumns'];
      },
      responseHiddenList() {
        return ['required', 'allowEmpty'];
      },
      requestDisableList() {
        return ['required'];
      },
    },
  };
</script>

<style lang="less" scoped>
  .api-sql-params {
    .main-title {
      height: 22px;
      font-size: 16px;
      margin: 10px 0;
      font-weight: 600;
      color: rgba(51, 51, 51, 1);
      line-height: 22px;
    }
  }
</style>
