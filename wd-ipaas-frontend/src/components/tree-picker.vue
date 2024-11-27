<template>
  <div>
    <el-popover
      placement="bottom-start"
      trigger="click"
      v-model="visible"
      popper-class="wake-tree-picker"
      :popper-options="{ padding: 0 }"
    >
      <el-input
        slot="reference"
        ref="reference"
        :value="displayText"
      ></el-input>

      <el-scrollbar :style="{ 'min-width': inputWidth + 'px', height: '100%' }">
        <div @mousedown.stop>
          <el-tree
            ref="tree"
            :data="treeData"
            default-expand-all
            :props="defaultProps"
            @node-click="onNodeClick"
          ></el-tree>
        </div>
      </el-scrollbar>
    </el-popover>
  </div>
</template>

<script>
import {
  addResizeListener,
  removeResizeListener
} from 'element-ui/lib/utils/resize-event';
import { valueEquals } from 'element-ui/lib/utils/util';
import Emitter from 'element-ui/lib/mixins/emitter';

export default {
  name: 'TreePicker',
  mixins: [Emitter],
  props: {
    value: {
      type: [String, Number, Array],
      default: ''
    },
    data: {
      type: Array,
      default: () => {
        return [];
      }
    },
    nodeKey: {
      type: String,
      default: 'id'
    },
    defaultProps: {
      type: Object,
      default: () => {
        return {
          children: 'children',
          label: 'groupName'
        };
      }
    },
  },
  data() {
    return {
      visible: false,
      selected: null,
      inputWidth: 0
    };
  },
  computed: {
    displayText() {
      if (this.selected) {
        return this.selected[this.defaultProps.label];
      }
      return '';
    },
    treeData() {
      const list = [
        {
          id: 0,
          level: null,
          parentId: 0,
          groupName: '首层接口分类',
          children: this.data
        }
      ];
      // this.data.map(item => {
      //   list.push(item);
      // });
      return list;
    }
  },
  watch: {
    visible(val) {
      if (val) {
        this.updatePopover();
      }
    },
    value: {
      immediate: true,
      handler(val, oldVal) {
        if (!valueEquals(val, oldVal)) {
          this.dispatch('ElFormItem', 'el.form.change', val);
        }
        if (val || val==0) {
          this.getFlatData(this.treeData);
        } else {
          this.selected = {};
        }
      }
    }
  },
  mounted() {
    document.body.addEventListener('mousedown', this.onMousedown);
    addResizeListener(this.$refs.reference.$el, this.handleResize);
  },
  beforeDestroy() {
    document.body.removeEventListener('mousedown', this.onMousedown);
    removeResizeListener(this.$refs.reference.$el, this.handleResize);
  },
  methods: {
    getFlatData(arr) {
      return arr.find(item => {
        if (this.value == item.id) {
          this.selected = item;
        } else {
          this.getFlatData(item.children);
        }
      });
    },
    onNodeClick(data) {
      const id = data[this.nodeKey];
      this.$emit('input', id);
      this.$emit('getLevel',data.level);
      this.selected = data;
      this.visible = false;
    },
    updatePopover() {
      this.handleResize();
    },
    handleResize() {
      const reference = this.$refs.reference;
      const input = reference.$el.querySelector('input');
      this.inputWidth = input.getBoundingClientRect().width;
    },
    onMousedown(e) {
      const reference = this.$refs.reference;
      if (reference.$el.contains(e.target)) {
        return;
      }
      this.visible = false;
    },
    setSelected() {
      if (!this.value) {
        this.selected = null;
        return;
      }
      const tree = this.$refs.tree;
      const node = tree.getNode(this.value);
      this.selected = node;
    }
  }
};
</script>

<style lang="less">
.wake-tree-picker {
  padding: 0;

  .el-scrollbar__wrap {
    max-height: 300px;
    // overflow-x: hidden;
  }
}
</style>
