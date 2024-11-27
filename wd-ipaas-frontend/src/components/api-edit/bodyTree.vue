<template>
  <div class="http-body-wrapper">
    <el-row class="table-head" :gutter="10">
      <el-col :span="isBody ? 6 : 4" class="table-head__item"
        ><tips-icon :content="$t('validator.httpFieldNameDesc')"></tips-icon>参数名称</el-col
      >
      <el-col :span="4" class="table-head__item">参数类型</el-col>
      <el-col v-if="isShowRequired" :span="2" class="table-head__item">是否必填</el-col>
      <el-col v-if="isShowSample" :span="4" class="table-head__item">示例值</el-col>
      <el-col v-if="isShowSelect" :span="3" class="table-head__item">选择方式</el-col>
      <el-col v-if="isShowSelect" :span="5" class="table-head__item">参数值</el-col>
      <el-col :span="4" class="table-head__item">描述</el-col>
      <el-col v-if="!(preview || isReadonly) && !isTimerTask" :span="4" class="table-head__item">操作</el-col>
    </el-row>
    <json-schema-editor
      v-if="tree"
      ref="jse"
      :value="tree"
      :mode="mode"
      :disabled-type="preview || isReadonly"
      custom
      :is-show-required="isShowRequired"
      :is-show-sample="isShowSample"
      :disabled="preview || isReadonly"
      :tree-type="treeType"
      @select-params="selectParams"
      @select-method="selectMethod"
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
    name: 'BodyTree',
    components: {
      jsonSchemaEditor,
      TipsIcon,
    },
    props: {
      tree: {
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
       * request 请求体 | response响应体
       */
      treeType: {
        type: String,
        default: 'request',
      },
      /**
       * 模式
       * body 普通模式 | flowResponse 流程编排响应体 | timerTask 定时任务模式
       */
      mode: {
        type: String,
        default: 'body',
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
       * 是否显示示例值 默认true
       */
      isShowSample: {
        type: Boolean,
        default: true,
      },
    },
    computed: {
      /**
       * 响应体不显示 是否必填
       */
      isShowRequired() {
        return this.treeType === 'request';
      },

      isShowSelect() {
        return this.isFlowResponse || this.isTimerTask;
      },

      /**
       * 定时任务模式
       */
      isTimerTask() {
        return this.mode === 'timerTask';
      },

      /**
       * 流程编排类型
       */
      isFlowResponse() {
        return this.mode === 'flowResponse';
      },
      isBody() {
        return this.mode === 'body';
      },
    },
  };
</script>

<style lang="less" scoped>
  .http-body-wrapper {
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
