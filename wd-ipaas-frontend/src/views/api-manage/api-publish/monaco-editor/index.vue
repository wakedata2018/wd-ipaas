<template>
  <div ref="monaco"
       class="monaco-editor"></div>
</template>

<script>
import * as monaco from 'monaco-editor';
import eidtorConfig from './eidtor-config.js';
// import 'monaco-editor/esm/vs/basic-languages/javascript/javascript.contribution';
monaco.editor.defineTheme('DevTheme', {
  base: 'vs-dark',
  inherit: true,
  rules: [{ background: '2D313B', token: '' }],
  colors: {
    // "editor.foreground": "#000000",
    'editor.background': '#2D313B',
    // "editorCursor.foreground": "#8B0000",
    // "editor.lineHighlightBackground": "#0000FF20"
    // 'editorLineNumber.foreground': '#ffffff',
    'editorLineNumber.background': '#3E4453',
    // 'editorWidget.background': '#ffffff',
    // 'editorMarkerNavigationError.background': '#ffffff', // Editor marker navigation widget error color.
    // 'editorMarkerNavigationWarning.background': '#ffffff', // Editor marker navigation widget warning color.
    // 'editorMarkerNavigation.background': '#ffffff', // Editor marker navigation widget background.
    // 'editorSuggestWidget.background': '#ffffff'
    // "editor.selectionBackground": "#88000030",
    // "editor.inactiveSelectionBackground": "#88000015"
  }
});
export default {
  props: {
    language: {
      type: String,
      default: ''
    },
    // 编辑器中的内容
    codes: {
      type: String,
      default: ''
    }
  },
  watch: {
    codes(val) {
      this.monacoEditor.setValue(val);
    },
  },
  data () {
    return {
      // 默认配置
      defaultOptions: {
        theme: 'DevTheme', // 编辑器接口分类：vs, hc-black, or vs-dark
        // lineDecorationsWidth: 20,
        selectOnLineNumbers: true,
        roundedSelection: false,
        readOnly: this.readOnly, // 只读
        cursorStyle: 'line', // 光标样式
        automaticLayout: true, // 自动布局 // 外层容器千万别用flex布局。。。。
        // glyphMargin: true, // 字形边缘
        useTabStops: false,
        fontSize: 16, // 字体大小
        autoIndent: false // 自动缩进
      },
      monacoEditor: null,
      resizeInterval: null
    }
  },
  mounted() {
    this.initEditor();
  },
  methods: {
    initEditor () {
      this.monacoEditor = monaco.editor.create(this.$refs.monaco, {
        value: this.codes,
        ...this.defaultOptions,
        language: eidtorConfig.language[this.language] || 'sql',
      });
      this.monacoEditor.onDidChangeModelContent(event => {
        const newValue = this.monacoEditor.getValue();
        // console.log(newValue);
        this.$emit('codes-change', newValue);
      });
    },
    resizeEditor() {
      this.clearResize();
      this.monacoEditor.layout();
      this.resizeInterval = setInterval(() => {
        this.monacoEditor.layout();
      }, 50);
    },
    clearResize () {
      if (this.resizeInterval) {
        clearInterval(this.resizeInterval);
      }
    },
  },
  beforeDestroy () {
    this.clearResize();
  }
}
</script>

<style lang="less" scoped>
.monaco-editor {
  width: 100%;
  height: 100%;
}
</style>