<template>
  <div class="json-schema-params-mapping">
    <el-row class="table-head" :gutter="10">
      <el-col :span="6" class="table-head__item"
        ><tips-icon :content="$t('validator.httpFieldNameDesc')"></tips-icon>参数名称</el-col
      >
      <el-col :span="4" class="table-head__item">参数类型</el-col>
      <el-col :span="4" class="table-head__item">选择映射参数</el-col>
      <el-col :span="6" class="table-head__item">备注</el-col>
      <el-col v-if="!isReadonly" :span="4" class="table-head__item">操作</el-col>
    </el-row>
    <json-schema-mapping-editor
      :value="tree"
      :is-show-required="false"
      :is-show-sample="false"
      :disabled="isReadonly"
      :mapping-options="mappingOptions"
    />
  </div>
</template>

<script>
  import jsonSchemaMappingEditor from './json-schema-mapping-editor.vue';
  import TipsIcon from '@/components/tips-icon';
  /**
   * 用于渲染 请求体和响应体
   */
  export default {
    name: 'JsonSchemaMapping',
    components: {
      jsonSchemaMappingEditor,
      TipsIcon,
    },
    props: {
      tree: {
        type: Object,
        default: () => ({}),
      },
      /**
       * 只读模式，没有输入框
       */
      isReadonly: {
        type: Boolean,
        default: false,
      },
      mappingOptions: {
        type: Array,
        default: () => [],
      },
    },
  };
</script>

<style lang="less" scoped>
  .json-schema-params-mapping {
    width: 100%;
    border-radius: 4px;
    padding-bottom: 10px;

    .row {
      color: #333;
      font-size: 14px;
      height: 30px;
      line-height: 30px;
    }
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
  }
</style>
