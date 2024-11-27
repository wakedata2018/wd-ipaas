<template>
  <div class="codemirror-editor" :class="`${fullScreen ? 'full' : ''} ${isReadonly ? 'readonly' : ''}`">
    <div class="codemirror-panel">
      <template v-if="!isReadonly">
        <!-- <el-button-group>
          <el-button
            type="primary"
            size="mini"
            :title="$t('common.undo')"
            :disabled="!enableUndo"
            icon="fa fa-undo"
            @click="undo"
          >{{` ${$t('common.undo')}`}}</el-button>
          <el-button
            type="primary"
            size="mini"
            :title="$t('common.redo')"
            :disabled="!enableRedo"
            icon="fa fa-repeat"
            @click="redo"
          ></el-button>
        </el-button-group> -->
        <el-button size="mini" @click="format">{{ $t('common.format') }}</el-button>
        <slot name="example"> </slot>
      </template>
      <el-button
        type="text"
        size="mini"
        style="float: right"
        :title="!fullScreen ? $t('common.maximize') : $t('common.restore')"
        :icon="!fullScreen ? 'el-icon-full-screen' : 'el-icon-copy-document'"
        @click="changeScreen"
      ></el-button>
    </div>
    <div class="codemirror-and-params-container">
      <div class="codemirror-and-params" style="width: 100%">
        <codemirror
          ref="codeMirrorEditor"
          v-model="valueObj"
          class="codemirror-editor"
          :options="cmOptions"
          :merge="codeMerge"
          :placeholder="placeholder"
          @input="changeValue"
          @blur="changeSettingValue"
          @focus="focusCodeMirror"
          @dragenter.native="onDragEnter"
          @dragover.native="onDragOver"
          @drag.native="onDrag"
          @drop.native="ondrop"
          @dragleave.native="onDragLeave"
        />
      </div>
    </div>
  </div>
</template>

<!-- eslint-disable wkvue/no-import-style-in-script -->
<script>
  import * as CodeMirror from 'codemirror/lib/codemirror';
  import 'codemirror/mode/sql/sql';
  import 'codemirror/theme/eclipse.css';
  import 'codemirror/addon/hint/sql-hint.js';
  import 'codemirror/addon/selection/active-line';
  import 'codemirror/addon/merge/merge.css';
  import 'codemirror/addon/merge/merge.js';
  import 'codemirror/addon/display/placeholder';
  import './diff_match_patch.js';
  import sqlFormatter from 'sql-formatter';
  import debounce from 'lodash/debounce';

  const DefaultOptions = {
    // codemirror options
    mode: { name: 'text/x-sql' }, // 定义mode
    tabSize: 4,
    extraKeys: { Ctrl: 'autocomplete' }, // 自动提示配置
    theme: 'eclipse', // 选中的theme
    matchBrackets: true, // 括号匹配
    lineWrapping: true, // 自动换行
    styleActiveLine: true, // 当前行背景高亮
    lineNumbers: true, // 显示行号
    line: true,
    highlightDifferences: true,
    dragDrop: true,
    hintOptions: {
      tables: { FOR: [], SYSTEM_TIME: [], OF: [] },
    },
    // more codemirror options, 更多 codemirror 的高级配置...
  };

  export default {
    components: {},
    props: {
      config: {
        type: Object,
        default: () => ({}),
      },
      isReadonly: {
        type: Boolean,
        default: false,
      },
      prop: {
        type: String,
        default: 'script',
      },
      topicMap: {
        type: Object,
        default: () => ({}),
      },
      isDraggingPage: {
        type: Boolean,
        default: false,
      },

      codeMerge: {
        type: Boolean,
        default: false,
      },
      refresh: {
        type: Boolean,
        default: false,
      },
      placeholder: {
        type: String,
        default: '请输入',
      },
    },

    data() {
      return {
        cmOptions: window.$.extend(true, {}, DefaultOptions),
        mergeOptions: {
          allowEditingOriginals: false, // 目标文件是否可以编辑
          revertButtons: false, // 比较替换按钮
        },
        doubleOptions: {},
        codeFullscreen: false,
        historyCodeList: {
          loading: false,
          isVisible: false,
          codeList: [],
        },
        codeType: 1,
        codeMirrorEditor: null,
        oldValue: null,
        firstEdit: true,
        undoStartIndex: 0,
        enableUndo: false,
        enableRedo: false,
        movingCursor: null,
        fullScreen: false,
      };
    },
    computed: {
      valueObj: {
        get() {
          const value = this.config[this.prop];
          return value || '';
        },
        set(value) {
          this.$set(this.config, this.prop, value);
        },
      },
    },

    watch: {
      topicMap: {
        immediate: true,
        handler(val) {
          if (val) {
            this.addTableToHint(val, this.config.sourceTableList);
          }
        },
      },
      isReadonly: {
        immediate: true,
        handler(val) {
          this.$nextTick(() => {
            this.setReadOnly(val);
          });
        },
      },
      refresh() {
        this.undoStartIndex = 0;
        if (this.codeMirrorEditor) {
          this.codeMirrorEditor.clearHistory();
          this.codeMirrorEditor.refresh();
        }
        this.firstEdit = true;
        this.enableUndo = false;
        this.enableRedo = false;
        this.fullScreen = false;
        this.$nextTick(() => {
          this.setUndoFirstIndex();
          this.loadReadonlySetting();
          this.oldValue = this.valueObj;
        });
      },
    },

    mounted() {
      this.loadReadonlySetting();
    },
    beforeDestroy() {
      this.unbindCodeMirrorEvent();
    },
    methods: {
      addTableToHint(topicMap) {
        if (topicMap) {
          this.cmOptions.hintOptions.tables = {
            ...this.cmOptions.hintOptions.tables,
            ...topicMap,
          };
        } else {
          this.cmOptions.hintOptions.tables = window.$.extend(true, {}, DefaultOptions.hintOptions.tables);
        }
      },
      unbindCodeMirrorEvent() {
        if (this.$refs.codeMirrorEditor) {
          this.codeMirrorEditor = this.$refs.codeMirrorEditor.codemirror;
          this.codeMirrorEditor.off('cursorActivity');
        }
      },
      bindCodeMirrorEvent() {
        if (this.$refs.codeMirrorEditor) {
          this.codeMirrorEditor = this.$refs.codeMirrorEditor.codemirror;
          this.codeMirrorEditor.on('cursorActivity', this.hintEvent.bind(this));
        }
      },
      hintEvent() {
        if (!this.isReadonly) {
          // this.codeMirrorEditor.showHint();
          this.reCalculateEnable();
        }
      },
      changeSettingValue() {
        let val = null;
        if (this.codeMirrorEditor) {
          val = this.codeMirrorEditor.getValue();
        }
        if (this.oldValue !== val) {
          this.$emit('change-setting-value');
        }
        this.oldValue = val;
      },
      changeValue() {
        debounce(() => {
          this.$emit('change-value');
        }, 800)();
      },
      focusCodeMirror() {
        this.reCalculateEnable();
      },
      setUndoFirstIndex(offset = 0) {
        if (this.firstEdit) {
          const historySizeObj = this.codeMirrorEditor.historySize();
          this.undoStartIndex = historySizeObj.undo + offset > 0 ? historySizeObj.undo + offset : 0;
          this.firstEdit = false;
        }
      },

      canUndo() {
        if (this.codeMirrorEditor) {
          const historySizeObj = this.codeMirrorEditor.historySize();
          if (historySizeObj.undo > this.undoStartIndex) {
            return true;
          }
        }
        return false;
      },
      canRedo() {
        if (this.codeMirrorEditor) {
          const historySizeObj = this.codeMirrorEditor.historySize();
          if (historySizeObj.redo > 0) {
            return true;
          }
        }
        return false;
      },
      undo() {
        if (this.enableUndo) {
          this.codeMirrorEditor.undo();
          this.reCalculateEnable();
        }
      },
      redo() {
        if (this.enableRedo) {
          this.codeMirrorEditor.redo();
          this.reCalculateEnable();
        }
      },
      reCalculateEnable() {
        if (!this.firstEdit) {
          this.enableUndo = this.canUndo();
          this.enableRedo = this.canRedo();
        }
      },
      onDragEnter() {
        // 阻止默认动作
        event.preventDefault();
      },
      onDragOver() {
        // 阻止默认动作
        // event.preventDefault();
      },
      onDrag() {
        // 阻止默认动作
        // event.preventDefault();
      },
      onDragLeave() {
        // 阻止默认动作
        // event.preventDefault();
      },
      ondrop() {
        // 阻止默认动作
        event.preventDefault();
        this.reCalculateEnable();
        event.dataTransfer.clearData();
      },
      format() {
        if (this.codeMirrorEditor) {
          let sqlContent = '';
          sqlContent = this.codeMirrorEditor.getValue();
          /* 将sql内容进行格式后放入编辑器中 */
          this.codeMirrorEditor.setValue(sqlFormatter.format(sqlContent, { language: 'n1ql' }));
        }
      },
      changeScreen() {
        this.fullScreen = !this.fullScreen;
      },
      setReadOnly(isReadonly) {
        if (this.$refs.codeMirrorEditor) {
          this.codeMirrorEditor = this.$refs.codeMirrorEditor.codemirror;
          if (isReadonly) {
            this.codeMirrorEditor.off('cursorActivity');
          }
          this.codeMirrorEditor.setOption('readOnly', isReadonly);
        }
      },
      loadReadonlySetting() {
        if (!this.isReadonly) {
          this.bindCodeMirrorEvent();
        } else {
          this.setReadOnly(this.isReadonly);
        }
      },
    },
  };
</script>

<style scoped lang="less">
  /deep/ .vue-codemirror {
    height: 440px;
    margin-bottom: 2px;

    .CodeMirror {
      height: 100%;

      .CodeMirror-merge {
        box-sizing: content-box;
        height: 100%;
        border: 0px;

        .CodeMirror-merge-pane {
          height: 100%;
        }
      }

      .CodeMirror-gutter {
        background-color: #f7f9fb;
      }

      .CodeMirror-merge-gap {
        border: 0px;
        border-top: 1px solid #ebeef5;
        border-bottom: 1px solid #ebeef5;
        box-sizing: content-box;
      }
    }

    .CodeMirror-scroll {
      height: 100%;
      margin: 0px;
    }

    .CodeMirror-sizer {
      border-right-width: 0px !important;
    }

    .CodeMirror {
      height: 100%;
      font-family: 'Courier New', Courier, monospace;
      font-size: 12px;
      line-height: 20px;
      border-radius: 3px;
      border: 1px solid #dcdfe6 !important;
      transition: border 0.3s, box-shadow 0.3s;

      &.CodeMirror-focused {
        border: 1px solid rgb(72, 167, 255) !important;
        box-shadow: 0 0 6px rgba(72, 167, 255, 0.5);
      }
    }
  }

  .codemirror-panel {
    padding: 3px 0;

    /deep/ .el-button {
      padding: 5px 15px;

      &.el-button--text {
        padding: 6px 2px !important;
        color: #606266;
      }
    }
  }

  .codemirror-editor {
    background: white;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;

    &.full {
      position: fixed;
      left: 0px;
      top: 0px;
      width: 100vw;
      height: 100vh;
      padding: 20px;
      z-index: 20;

      /deep/ .vue-codemirror {
        height: calc(100vh - 80px);
      }
    }

    &.readonly {
      /deep/ .vue-codemirror {
        .CodeMirror-scroll {
          background-color: #f5f7fa;

          .CodeMirror-line {
            background-color: #f5f7fa;
            color: #c0c4cc;
          }
        }
      }
    }
  }

  .codemirror-and-params-container {
    font-size: 0px;
    flex-wrap: nowrap;
    display: flex;
    flex-direction: row;
    position: relative;

    .codemirror-and-params {
      font-size: 13px;
      display: inline-block;

      &.params {
        padding-right: 10px;
      }
    }
  }

  ::v-deep .CodeMirror pre.CodeMirror-placeholder {
    color: #c0c4cc;
  }
</style>
