<template>
  <div class="page" @drop="onDrop">
    <div ref="canvas" class="graph-container" style="position: relative"></div>
  </div>
</template>

<script>
  import G6 from '@antv/g6';
  import { debounce } from 'throttle-debounce';
  import overallEventBus, { overallEventName } from '@/utils/overall-event-bus.js';

  import { initBehavors } from './behavior';
  import eventBus, { EventName, getIsDropGraph } from './event-bus';
  import { getCurrentEditorContext } from './context';

  export default {
    props: {
      data: {
        type: Object,
        default: () => ({}),
      },
      isReadonly: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        pageId: 'graph-container',
      };
    },
    watch: {
      isReadonly: {
        immediate: true,
        handler(val) {
          this.setReadonly(val);
        },
      },
    },
    created() {
      initBehavors();
    },
    mounted() {
      this.$nextTick(() => {
        this.init();
      });

      this.resize = debounce(300, () => {
        if (this.graph) {
          const canvasCom = this.$refs.canvas;
          const $el = window.$(canvasCom);
          const width = $el.outerWidth();
          const height = $el.outerHeight() || 500;
          const ua = navigator.userAgent.toLowerCase();
          let ratio = 0;
          if (~ua.indexOf('mozilla')) {
            if (window.devicePixelRatio !== undefined) {
              ratio = window.devicePixelRatio;
            }
          } else if (~ua.indexOf('msie')) {
            if (screen.deviceXDPI && screen.logicalXDPI) {
              ratio = screen.deviceXDPI / screen.logicalXDPI;
            }
          } else if (window.outerWidth !== undefined && window.innerWidth !== undefined) {
            ratio = window.outerWidth / window.innerWidth;
          }
          const g6Canvas = window.$(canvasCom.children[0]);
          if (ratio <= 1) {
            g6Canvas.css({
              transform: `scale(${ratio})`,
              'transform-origin': '0px 0px',
              width: `${width / ratio}px`,
              height: `${height / ratio}px`,
            });
          } else {
            g6Canvas.css({
              transform: `scale(1)`,
              'transform-origin': '0px 0px',
            });
          }
          this.graph.changeSize(width, height);
          // this.$nextTick(_ => {
          //   g6Canvas.css({width: `${width/ratio}px`, height: `${height/ratio}px`});
          // })
        }
        // overallEventBus.$emit(overallEventName.windowResize);
      });
      overallEventBus.$on(overallEventName.windowResize, this.resize);

      eventBus.$off(EventName.changeG6Size).$on(EventName.changeG6Size, this.event_changeG6Size);
      eventBus
        .$off(EventName.returnToOriginalMode)
        .$on(EventName.returnToOriginalMode, this.event_returnToOriginalMode);
    },
    beforeDestroy() {
      this.graph.destroy();
      overallEventBus.$off(overallEventName.windowResize);
      eventBus.$off(EventName.changeG6Size);
      eventBus.$off(EventName.returnToOriginalMode);
    },
    methods: {
      init() {
        const $el = window.$(this.$refs.canvas);
        const width = $el.outerWidth();
        const height = $el.outerHeight() || 500;
        this.graph = new G6.Graph({
          container: this.$refs.canvas,
          height,
          width,
          fitView:true,
          modes: {
            // 支持的 behavior
            default: [
              'drag-canvas',
              'zoom-canvas',
              'hover-node',
              'keyboard',
              'add-menu',
              'click-combo',
              'click-select',
              'drag-combo',
              'create-edge',
              'drag-node',
            ],
            mulitSelect: ['mulit-select'],
            view: ['drag-canvas', 'zoom-canvas', 'hover-node', 'add-menu', 'click-select'],
          },
        });
        this.graph.$el = this.$refs.canvas;
        this.setReadonly(this.isReadonly);
        this.$emit('after-inited', this.graph);
        this.readData();
      },
      setReadonly(val) {
        this.$nextTick(() => {
          if (this.graph) {
            this.graph.setMode(val ? 'view' : 'default');
          }
        });
      },
      readData() {
        if (this.data) {
          this.graph.read(this.data);
        }
      },
      expandChanged(val, widthOffset = 0, rightPanelWidth = 320) {
        const notRightPanelWidth = 30;
        const newRightPanelWidth = rightPanelWidth - notRightPanelWidth;
        const $el = window.$(this.$refs.canvas);
        let width = $el.outerWidth();
        if (val != null) {
          if (!val) {
            width += newRightPanelWidth;
          }
        }

        if (widthOffset < 0) {
          width -= widthOffset;
        }

        const height = $el.outerHeight() || 500;
        this.graph.changeSize(width, height);
      },
      event_changeG6Size(obj) {
        const { rightExpanded, widthOffset, rightPanelWidth } = obj;
        this.expandChanged(rightExpanded, widthOffset, rightPanelWidth);
      },
      event_returnToOriginalMode() {
        this.graph?.setMode(this.isReadonly ? 'view' : 'default');
      },
      async onDrop(event) {
        event.preventDefault();
        if (!this.isReadonly) {
          // 获取拖入的算子信息和类型
          const nodeInfo = event.dataTransfer.getData('nodeInfo');
          const nodeType = event.dataTransfer.getData('nodeType');
          const nodeInfoObj = JSON.parse(nodeInfo);
          const x = event.clientX || event.pageX;
          const y = event.clientY || event.pageY;
          const context = getCurrentEditorContext();
          if (!!this.graph && getIsDropGraph()) {
            // 设置算子的位置信息
            if (nodeType === 'apiList' || nodeType === 'tree') {
              const xy = this.graph.getPointByClient(x, y);
              nodeInfoObj.x = xy.x;
              nodeInfoObj.y = xy.y;
              nodeInfoObj.from = nodeType;

              if (context?.editor.params.beforeCreateElement) {
                try {
                  await context.editor.params.beforeCreateElement(nodeInfoObj);
                } catch (err) {
                  this.$message({ type: 'warning', message: err.message });
                  // 停止交易
                  return;
                }
              }

              eventBus.$emit(EventName.addItem, nodeInfoObj);
            }
          }
        }
      },
    },
  };
</script>

<style scoped lang="less">
  @bottom-height: 75px;
  .page {
    width: 100%;
    height: 100%;
    overflow: hidden;
    .graph-container {
      width: 100%;
      height: calc(100% - @bottom-height);
      box-sizing: border-box;
    }
  }
</style>
