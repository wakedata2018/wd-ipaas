<template>
  <div :v-loading="responseLoading">
    <el-radio-group v-model="bodyType" class="api-type">
      <el-radio :label="BODY_TYPE.JSON">JSON</el-radio>
      <el-radio :label="BODY_TYPE.XML">XML</el-radio>
    </el-radio-group>
    <div v-if="!responseInfo" class="empty-tip">暂无数据</div>

    <div v-else :class="{ fullscreen: isFullscreen }">
      <tool-bar :is-fullscreen.sync="isFullscreen">
        <div v-if="jsonFormat && bodyType === BODY_TYPE.JSON" class="monaco">
          <MonacoEditor ref="jsonEditorRef" style="height: 100%" :value="jsonFormat" language="json" read-only />
        </div>

        <div v-if="xmlFormat && bodyType === BODY_TYPE.XML" class="monaco">
          <MonacoEditor ref="xmlEditorRef" style="height: 100%" :value="xmlFormat" language="xml" read-only />
        </div>
      </tool-bar>
    </div>
  </div>
</template>

<script>
  import vkbeautify from 'vkbeautify';
  import MonacoEditor from '@/components/monaco-editor';
  import { XML } from '@/utils/xmljson';
  import { BODY_TYPE } from '../constants';
  import ToolBar from './toolbar';

  export default {
    name: 'ApiResponseViewer',
    components: {
      ToolBar,
      MonacoEditor,
    },
    props: {
      /**
       * API测试响应返回值
       */
      responseInfo: {
        type: Object,
        default: null,
      },
      /**
       * 单选框类型 'default' | 'json' | 'xml'
       */
      requestType: {
        type: String,
        default: '',
      },
      responseLoading: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        BODY_TYPE,
        xotree: new XML.ObjTree(),
        isFullscreen: false,
      };
    },

    computed: {
      /**
       * xml格式化
       */
      xmlFormat() {
        if (!this.responseInfo) {
          return '';
        }
        const { type, data } = this.responseInfo;
        return type === BODY_TYPE.XML ? vkbeautify.xml(this.xotree.xml_unescape(data)) : '';
      },
      jsonFormat() {
        if (!this.responseInfo) {
          return '';
        }
        const { type, data } = this.responseInfo;
        return type !== BODY_TYPE.XML ? vkbeautify.json(this.xotree.xml_unescape(JSON.stringify(data))) : '';
      },
      bodyType: {
        get() {
          const typeVal = this.requestType;
          return typeVal !== BODY_TYPE.XML ? BODY_TYPE.JSON : typeVal;
        },
        set(val) {
          this.$emit('change-type', val);
        },
      },
    },

    watch: {
      isFullscreen: {
        deep: true,
        handler(val) {
          this.$nextTick(() => {
            this.resize();
          });
        },
      },
    },

    methods: {
      resize() {
        const _ref =
          this.jsonFormat && this.bodyType === BODY_TYPE.JSON ? this.$refs.jsonEditorRef : this.$refs.xmlEditorRef;
        _ref.getEditor().layout();
      },
    },
  };
</script>

<style lang="less" scoped>
  .api-type {
    margin: 15px 0;
  }
  .empty-tip {
    padding-bottom: 30px;
  }
  .monaco {
    height: 240px;
  }
  .fullscreen {
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    right: 0;
    z-index: 299;
    background: white;
    .monaco {
      height: calc(100vh - 40px);
    }
  }
</style>
