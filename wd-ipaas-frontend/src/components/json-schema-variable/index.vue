<template>
  <div class="variable-wrapper">
    <el-row class="table-head" :gutter="10">
      <el-col :span="4" class="table-head__item"
        ><tips-icon :content="$t('validator.httpFieldNameDesc')"></tips-icon>变量名称</el-col
      >
      <el-col :span="4" class="table-head__item">参数类型</el-col>
      <el-col :span="3" class="table-head__item">赋值类型</el-col>
      <el-col :span="5" class="table-head__item">值</el-col>
      <el-col :span="4" class="table-head__item">描述</el-col>
      <el-col v-if="!preview || !isReadonly" :span="4" class="table-head__item">操作</el-col>
    </el-row>
    <json-schema-editor
      v-if="value"
      ref="jse"
      mode="flowResponse"
      :value="value"
      :is-show-required="false"
      :is-show-sample="false"
      :select-name-mode="selectNameMode"
      :disabled-type="isReadonly"
      disable-root-type
      @select-params="selectParams"
      @select-method="selectMethod"
      @select-variable="selectVariable"
    />
  </div>
</template>

<script>
  import jsonSchemaEditor from '@/components/json-schema-editor/index';
  import TipsIcon from '@/components/tips-icon';
  /**
   * 用于渲染 请求体和响应体
   */
  export default {
    name: 'JsonSchemaVariable',
    components: {
      jsonSchemaEditor,
      TipsIcon,
    },
    props: {
      value: {
        type: Object,
        require: true,
        default: () => ({}),
      },

      preview: {
        type: Boolean,
        default: false,
      },
      /**
       * 只读模式，没有输入框
       */
      isReadonly: {
        type: Boolean,
        default: false,
      },
      /**
       * 选择参数
       */
      selectParams: {
        type: Function,
        // eslint-disable-next-line no-empty-function
        default: () => {},
      },
      /**
       * 选择方法
       */
      selectMethod: {
        type: Function,
        // eslint-disable-next-line no-empty-function
        default: () => {},
      },
      /**
       * 选择变量
       */
      selectVariable: {
        type: Function,
        // eslint-disable-next-line no-empty-function
        default: () => {},
      },
      /**
       * 选择变量模式
       */
      selectNameMode: {
        type: Boolean,
        default: false,
      },
    },
  };
</script>

<style lang="less" scoped>
  .variable-wrapper {
    width: 100%;
    border-radius: 4px;

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
