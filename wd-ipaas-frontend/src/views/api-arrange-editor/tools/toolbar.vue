<template>
  <div class="toolbar">
    <el-button icon="el-icon-back" style="position: relative; top: -2px" @click="onGoback">返回</el-button>
    <span class="separator"></span>
    <i
      data-command="showOrHideSidemenu"
      class="command iconfont"
      :class="`${!!showSidemenu ? 'el-icon-s-fold' : 'el-icon-s-unfold'}`"
      :title="`${!!showSidemenu ? '收起菜单' : '展开菜单'}`"
      @click="showOrHideSidemenu"
    ></i>
    <span class="separator"></span>
    <template v-if="!readonly">
      <i
        data-command="undo"
        class="command iconfont el-icon-refresh-left"
        title="撤销"
        :class="{ disable: !canUndo }"
        @click="handleUndo"
      ></i>
      <i
        data-command="redo"
        class="command iconfont el-icon-refresh-right"
        title="恢复"
        :class="{ disable: !canRedo }"
        @click="handleRedo"
      ></i>
      <i
        data-command="copy"
        class="command iconfont el-icon-copy-document"
        title="复制"
        :class="{ disable: !copyable }"
        @click="handleCopy"
      ></i>
      <i
        data-command="delete"
        class="command iconfont el-icon-delete"
        title="删除"
        :class="{ disable: !removable }"
        @click="handleDelete"
      ></i>
      <span class="separator"></span>
    </template>
    <i data-command="zoomIn" class="command iconfont el-icon-zoom-in" title="放大" @click="handleZoomIn"></i>
    <i data-command="zoomOut" class="command iconfont el-icon-zoom-out" title="缩小" @click="handleZoomOut"></i>
    <el-dropdown trigger="click" @command="changeLayout">
      <i data-command="changeLayout" class="command iconfont el-icon-sort" title="布局切换"></i>
      <el-dropdown-menu slot="dropdown">
        <el-dropdown-item command="vertical">垂直布局</el-dropdown-item>
        <el-dropdown-item command="horizontal">水平布局</el-dropdown-item>
        <!-- <el-dropdown-item command="grid">网格布局</el-dropdown-item> -->
      </el-dropdown-menu>
    </el-dropdown>

    <i data-command="changeMode" class="command iconfont el-icon-files" title="繁简切换" @click="handleChangeMode"></i>
    <i
      data-command="autoZoom"
      class="command iconfont el-icon-full-screen"
      title="适应画布"
      @click="handleAutoZoom"
    ></i>
    <i
      data-command="resetZoom"
      class="command iconfont el-icon-c-scale-to-original"
      title="实际尺寸"
      @click="handleResetZoom"
    ></i>
    <span class="separator"></span>
    <i
      ref="copyBtn"
      data-command="copyTaskInfo"
      class="command iconfont el-icon-document-copy"
      title="复制任务信息"
    ></i>
    <slot></slot>
  </div>
</template>

<script>
  import Clipboard from 'clipboard';
  import { Loading } from 'element-ui';
  import cloneDeep from 'lodash/cloneDeep';
  import overallEventBus, { overallEventName } from '@/utils/overall-event-bus.js';
  import eventBus, { EventName, triggerIsDetail } from '@/components/g6-editor/event-bus';
  import { changeLayout, uniqueId, getBox, getViewCenter } from '@/components/g6-editor/utils';
  import { isRemovable, isCopyable } from '../definitions';

  export default {
    props: {
      readonly: {
        type: Boolean,
        default: false,
      },
      taskInfo: {
        type: Object,
        default: () => ({}),
      },
      graph: {
        type: Object,
        default: () => ({}),
      },
      command: {
        type: Object,
        default: () => ({}),
      },
    },
    data() {
      return {
        redoList: [],
        undoList: [],
        selectedItem: null,
        multiSelect: false,
        addGroup: false,
        dialog: {
          task: false,
        },
        // 侧边菜单是否已展开
        showSidemenu: false,
        clipboard: null,
      };
    },
    computed: {
      hasSelected() {
        return this.selectedItem && this.selectedItem.length > 0;
      },
      copyable() {
        return (
          this.hasSelected &&
          this.selectedItem.every(i => {
            return isCopyable(i);
          })
        );
      },
      removable() {
        return (
          this.hasSelected &&
          this.selectedItem.every(i => {
            return isRemovable(i);
          })
        );
      },
      canUndo() {
        return !!this.undoList.length;
      },
      canRedo() {
        return !!this.redoList.length;
      },
    },
    watch: {
      selectedItem(val) {
        if (val && val.length > 1) {
          this.addGroup = true;
        } else {
          this.addGroup = false;
        }
      },
    },
    created() {
      this.bindEvent();
      /// 默认收起侧边菜单
      overallEventBus.$emit(overallEventName.showOrHideSidemenu, { value: false });
    },
    mounted() {
      this.onCopyTaskInfo();
    },
    beforeDestroy() {
      this.unBindEvent();
      overallEventBus.$emit(overallEventName.showOrHideSidemenu, { value: true });

      if (this.clipboard) {
        this.clipboard.off('success');
        this.clipboard.off('error');
        this.clipboard.destroy();
        this.clipboard = null;
      }
    },
    methods: {
      onCopyTaskInfo() {
        if (this.$refs.copyBtn) {
          this.clipboard = new Clipboard(this.$refs.copyBtn, {
            text: () => {
              return JSON.stringify(this.taskInfo);
            },
          });
          this.clipboard.on(
            'success',
            function (e) {
              this.$message.success(
                this.$t('common.copy') + this.$t('common.englishCharSplit') + this.$t('common.successfully')
              );
            }.bind(this)
          );
          this.clipboard.on(
            'error',
            function (e) {
              this.$message.error(
                this.$t('common.copy') + this.$t('common.englishCharSplit') + this.$t('common.failed')
              );
            }.bind(this)
          );
        }
      },
      unBindEvent() {
        eventBus.$off(EventName.add, this.changeStack);
        eventBus.$off(EventName.update, this.changeStack);
        eventBus.$off(EventName.delete, this.changeStack);
        eventBus.$off(EventName.layout, this.changeStack);
        eventBus.$off(EventName.removeLastStack, this.handleRemoveNextStack);
        eventBus.$off(EventName.clearStack, this.handleClearStack);
        eventBus.$off(EventName.nodeselectchange, this.handleNodeSelectChange);
        eventBus.$off(EventName.deleteItem, this.handleDelete);
        eventBus.$off(EventName.muliteSelectEnd, this.event_muliteSelectEnd);
      },
      bindEvent() {
        eventBus.$on(EventName.add, this.changeStack);
        eventBus.$on(EventName.update, this.changeStack);
        eventBus.$on(EventName.delete, this.changeStack);
        eventBus.$on(EventName.layout, this.changeStack);
        eventBus.$on(EventName.removeLastStack, this.handleRemoveNextStack);
        eventBus.$on(EventName.clearStack, this.handleClearStack);
        eventBus.$on(EventName.deleteItem, this.handleDelete);
        eventBus.$on(EventName.muliteSelectEnd, this.event_muliteSelectEnd);
        eventBus.$on(EventName.nodeselectchange, this.handleNodeSelectChange);
      },
      changeStack(data) {
        this.redoList = data.redoList;
        this.undoList = data.undoList;
      },
      handleRemoveNextStack() {
        this.undoList.pop();
        this.command.removeLastStack();
      },
      handleClearStack() {
        this.redoList = [];
        this.undoList = [];
        this.command.clearStack();
      },
      handleNodeSelectChange({ target, selectedItems, select }) {
        const items = [];
        for (const key in selectedItems) {
          items.push(...selectedItems[key]);
        }
        this.selectedItem = select ? items : null;
      },
      event_muliteSelectEnd() {
        this.multiSelect = false;
        this.selectedItem = this.graph.findAllByState('node', 'selected');
      },
      handleUndo() {
        if (this.canUndo) {
          this.command.undo();
        }
      },
      handleRedo() {
        if (this.canRedo) {
          this.command.redo();
        }
      },
      updatePos(x, y, list) {
        [x, y] = [x + 20, y + 20];
        if (list.includes(`${x},${y}`)) {
          return this.updatePos(x, y, list);
        }
        return [x, y];
      },
      handleCopy() {
        if (this.readonly || !this.copyable) {
          return;
        }

        if (this.hasSelected) {
          const items = [];
          const pos = [];
          this.graph.getNodes().forEach(node => {
            if (!node.destroyed) {
              const { x, y } = node.getModel();
              pos.push(`${x},${y}`);
            }
          });
          this.selectedItem.forEach(item => {
            const model = cloneDeep(item.getModel());
            if (model.itemType === 'node' || model.itemType === 'combo') {
              [model.x, model.y] = this.updatePos(model.x, model.y, pos);
              pos.push(`${model.x},${model.y}`);
              model.form.name = model.form.name + '_copy';
              items.push(model);
            }
          });
          if (items.length) {
            this.command.executeCommand('add', items);
          }
        }
      },
      handleDelete() {
        if (this.readonly || !this.removable) {
          return;
        }

        if (this.hasSelected) {
          this.command.executeCommand('delete', this.selectedItem);
          this.selectedItem = null;
        }
      },
      showOrHideSidemenu() {
        /// 菜单栏的宽度
        const sidebarWidth = 180;
        this.showSidemenu = !this.showSidemenu;
        overallEventBus.$emit(overallEventName.windowResize);
        overallEventBus.$emit(overallEventName.showOrHideSidemenu, { value: this.showSidemenu });
        eventBus.$emit(EventName.changeG6Size, {
          rightExpanded: null,
          widthOffset: !this.showSidemenu ? -sidebarWidth : 0,
        });
      },
      handleZoomIn() {
        const currentZoom = this.graph.getZoom();
        this.graph.zoomTo(currentZoom + 0.5, getViewCenter(this.graph));
      },
      handleZoomOut() {
        const currentZoom = this.graph.getZoom();
        this.graph.zoomTo(currentZoom - 0.5, getViewCenter(this.graph));
      },
      changeLayout,
      handleChangeMode() {
        triggerIsDetail();
        const data = this.graph.save();
        this.graph.changeData(data);
      },
      handleToBack() {
        if (this.hasSelected) {
          this.selectedItem.forEach(item => {
            item.toBack();
            this.graph.paint();
          });
        }
      },
      handleToFront() {
        if (this.hasSelected) {
          this.selectedItem.forEach(item => {
            if (item.getType() === 'edge') {
              // const nodeGroup = this.graph.get("nodeGroup");
              // const edgeGroup = item.get("group");
              // nodeGroup.toFront();
              // edgeGroup.toFront()
            } else {
              item.toFront();
            }
            this.graph.paint();
          });
        }
      },
      handleAutoZoom() {
        this.graph.fitView(20);
      },
      handleResetZoom() {
        this.graph.zoomTo(1, getViewCenter(this.graph));
      },
      handleMuiltSelect() {
        this.multiSelect = true;
        this.graph.setMode('mulitSelect');
      },
      getPosition(items) {
        const boxList = [];
        items.forEach(item => {
          const box = item.getBBox();
          boxList.push(getBox(box.x, box.y, box.width, box.height));
        });
        let minX1, minY1, MaxX2, MaxY2;
        boxList.forEach(box => {
          if (typeof minX1 === 'undefined') {
            minX1 = box.x1;
          }
          if (typeof minY1 === 'undefined') {
            minY1 = box.y1;
          }
          if (typeof MaxX2 === 'undefined') {
            MaxX2 = box.x2;
          }
          if (typeof MaxY2 === 'undefined') {
            MaxY2 = box.y2;
          }
          if (minX1 > box.x1) {
            minX1 = box.x1;
          }
          if (minY1 > box.y1) {
            minY1 = box.y1;
          }
          if (MaxX2 < box.x2) {
            MaxX2 = box.x2;
          }
          if (MaxY2 < box.y2) {
            MaxY2 = box.y2;
          }
        });
        const width = MaxX2 - minX1;
        const height = MaxY2 - minY1;
        const x = minX1 + width / 2;
        const y = minY1 + height / 2;
        const id = 'team' + uniqueId();
        const model = {
          id,
          width,
          height,
          x,
          y,
          shape: 'teamNode',
        };
        this.command.executeCommand('add', model);
        // const item = this.graph.findById(id);
        // item.get("group").toBack();
        // const edgeGroup = this.graph.get("edgeGroup");
        // edgeGroup.toFront();
        // this.graph.paint();
      },
      onGoback() {
        this.$router.push({
          name: 'apipublish',
          params: this.$route.params,
        });
      },
    },
  };
</script>

<style scoped>
  .toolbar {
    box-sizing: border-box;
    top: 0;
    left: 0;
    right: 0;
    height: 60px;
    line-height: 60px;
    background: white;
    z-index: 10;
    position: absolute;
    padding-left: 24px;
    box-shadow: 0px 4px 16px 0px rgba(26, 66, 117, 0.1);
  }

  .toolbar .command {
    box-sizing: border-box;
    width: 27px;
    height: 27px;
    margin: 0px 6px;
    border-radius: 2px;
    padding-left: 4px;
    line-height: 26px;
    display: inline-block;
    cursor: pointer;
    border: 1px solid rgba(2, 2, 2, 0);
    color: #606266;
  }
  .toolbar .command:hover {
    border: 1px solid #e9e9e9;
  }
  .toolbar .disable {
    color: rgba(0, 0, 0, 0.25);
    cursor: not-allowed;
  }
  .toolbar .separator {
    margin: 4px;
    border-left: 1px solid #e9e9e9;
  }
</style>
