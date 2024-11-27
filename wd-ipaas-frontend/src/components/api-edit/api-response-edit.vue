<template>
  <el-tabs v-model="activeTabName" @tab-click="onChangeActiveTab">
    <el-tab-pane label="响应头" name="head">
      <api-params-table
        ref="responseHeadForm"
        v-model="headParams"
        params-type="HEAD"
        :params-map="paramsMapConfig"
        table-type="response"
        :hidden-list="['required']"
        :mode="mode"
        :is-flow="isFlowType"
        @select-params="selectParams"
        @select-method="selectMethod"
      >
      </api-params-table>
    </el-tab-pane>
    <el-tab-pane label="响应体" name="body">
      <response-body
        :tree="tree"
        tree-type="response"
        :preview="isBodyPreview || isReadonly"
        :mode="bodyMode"
        :is-readonly="isReadonly"
        :select-params="selectParams"
        :select-method="selectMethod"
        :is-show-sample="isShowSample"
      />
    </el-tab-pane>
  </el-tabs>
</template>
<script>
  import ApiParamsTable from './api-params-table.vue';
  import ResponseBody from './bodyTree';

  export default {
    name: 'ApiResponseEdit',
    components: { ApiParamsTable, ResponseBody },
    props: {
      value: {
        type: Array,
        default: () => [],
      },
      /**
       * 模式
       * edit | preview
       */
      mode: {
        type: String,
        default: 'edit',
      },
      /**
       * 只读模式，没有输入框
       */
      isReadonly: {
        type: Boolean,
        default: false,
      },
      /**
       * 模式
       * body 普通模式 | flowResponse 流程编排响应体
       */
      bodyMode: {
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
      activeName: {
        type: String,
        default: 'body',
      },
    },
    data() {
      return {
        jsonSchema: true,
        tree: {
          root: {
            type: 'object',
            name: 'root',
            description: '根层级',
            properties: {},
          },
        },
        inited: false,
        activeTabName: '',
        // 参数设置别名
        paramsMapConfig: {
          sample: 'defaultValue',
        },
        cacheTree: '', // 缓存tree
      };
    },
    computed: {
      headParams: {
        get() {
          return this.value?.filter(item => item?.type === 'HEAD' || item?.httpParamKind === 'HEAD') ?? [];
        },
        set(val) {
          this.$emit('input', [this.bodyParams, ...val]);
        },
      },
      bodyParams() {
        if (!this.value) {
          return null;
        }
        let idx = this.value.findIndex(item => item.type === 'BODY' || item?.httpParamKind === 'BODY');
        if (idx === -1) {
          idx = this.value.length;
          // eslint-disable-next-line vue/no-side-effects-in-computed-properties
          this.$set(this.value, idx, {
            assetColumns: 'body',
            assetDataType: 'json',
            type: 'BODY',
            descriptions: '请求体参数',
          });
        }
        return this.value[idx];
      },
      isBodyPreview() {
        return this.mode === 'preview';
      },
      /**
       * 服务编排类型
       */
      isFlowType() {
        return this.bodyMode === 'flowResponse';
      },
    },
    watch: {
      activeName: {
        immediate: true,
        handler(val) {
          this.activeTabName = val;
        },
      },
      bodyParams: {
        immediate: true,
        handler(val) {
          if (val && val.responsePostData !== this.cacheTree) {
            this.initTree();
          }
        },
      },
      tree: {
        deep: true,
        handler(val) {
          this.$emit('input', [...this.headParams, { ...this.bodyParams, responsePostData: JSON.stringify(val) }]);
        },
      },
    },
    methods: {
      initTree() {
        const responsePostData = this.bodyParams?.responsePostData;
        this.cacheTree = responsePostData;
        if (!responsePostData) {
          this.tree = {
            root: {
              type: 'object',
              name: 'root',
              description: '根层级',
              properties: {},
            },
          };
        } else {
          this.tree = JSON.parse(responsePostData);
        }
      },
      validate() {
        return this.$refs.responseHeadForm.validate();
      },
      onChangeActiveTab(tab) {
        this.$emit('update:activeName', tab.name);
      },
    },
  };
</script>
