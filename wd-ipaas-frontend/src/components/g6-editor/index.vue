<template>
  <div class="g6-editor">
    <slot name="toolbar"></slot>
    <slot name="leftpanel"></slot>
    <slot name="rightpanel"></slot>
    <page
      :height="height"
      :width="width"
      :is-readonly="isReadonly"
      @after-inited="onInited"
      @dragover.native="onDragover"
      @drop.native="onDrop"
    ></page>
    <slot></slot>
  </div>
</template>

<script>
  import Page from './page.vue';
  import Editor from './editor';
  import Command from './command';
  import eventBus, { EventName, setIsDropGraph } from './event-bus';
  import { __setCurrentEditorContext } from './context';

  export default {
    components: {
      Page,
      // ItemPanel
    },
    props: {
      height: {
        type: Number,
        default: document.documentElement.clientHeight,
      },
      width: {
        type: Number,
        default: document.documentElement.clientWidth,
      },
      /**
       * 元素创建前调用
       */
      beforeCreateElement: {
        type: Function,
        default: null,
      },
      isReadonly: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        editor: {},
        command: {},
      };
    },
    created() {
      this.init();
      this.bindEvent();
    },
    beforeDestroy() {
      eventBus.$off(EventName.updateItem, this.updateItem);
      eventBus.$off(EventName.addItem, this.addItem);
    },
    methods: {
      init() {
        this.editor = new Editor({ beforeCreateElement: this.$props.beforeCreateElement });
        this.command = new Command(this.editor);
      },
      bindEvent() {
        eventBus.$on(EventName.updateItem, this.updateItem);
        eventBus.$on(EventName.addItem, this.addItem);
      },
      updateItem(item) {
        this.command.executeCommand('update', item);
      },
      addItem(item) {
        this.command.executeCommand('add', [item]);
      },
      onInited(graph) {
        const param = {
          graph,
          editor: this.editor,
          command: this.command,
        };
        this.editor.setGraph(graph);
        this.$emit('after-inited', param);
        __setCurrentEditorContext(param);
      },
      onDragover(ev) {
        ev.preventDefault();
      },
      onDrop(ev) {
        if (!this.isReadonly) {
          setIsDropGraph(true);
        }
      },
    },
  };
</script>

<style lang="less" scoped>
  .g6-editor {
    box-sizing: border-box;
    position: relative;
  }
</style>
