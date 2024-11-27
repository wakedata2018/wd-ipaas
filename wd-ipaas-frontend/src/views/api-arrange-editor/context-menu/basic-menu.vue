<template>
  <div>
    <ul
      v-show="menus.length && visible"
      ref="contextMenu"
      class="el-scrollbar__view el-select-dropdown__list context-menu"
      :style="style"
    >
      <li
        v-for="menu in menus"
        :key="menu.cmd"
        class="el-select-dropdown__item"
        :class="{ 'el-dropdown-menu__item--divided': menu.divided }"
        @click="handleClick(menu, node)"
      >
        {{ menu.name }}
      </li>
    </ul>
    <rename />
  </div>
</template>

<script>
  import Rename from './dialog/rename.vue';
  import eventBus, { EventName } from '@/components/g6-editor/event-bus';
  import { getCurrentEditorContext } from '@/components/g6-editor/context';
  import { getUniqueNameFromItem } from '../utils';

  /**
   * 右键菜单父类
   */
  export default {
    components: { Rename },
    props: {
      taskInfo: {
        type: Object,
        default: _ => ({
          taskConf: {},
        }),
      },
    },
    data() {
      return {
        visible: false,
        // 所有节点共有的菜单
        style: {
          left: 0,
          top: 0,
        },
        node: null,
        menuObjMap: {},
      };
    },
    computed: {
      menus() {
        const commonMenus = this.getCommonMenus();
        if (this.nodeMenuObj) {
          const nodeMenus = this.nodeMenuObj.getMenus(this.node);
          return commonMenus.concat(nodeMenus);
        }
        return commonMenus;
      },
      uniqueName() {
        return this.node ? getUniqueNameFromItem(this.node) : '';
      },
      nodeMenuObj() {
        if (!this.uniqueName) {
          return;
        }
        return this.menuObjMap[this.uniqueName];
      },
    },
    created() {
      this.bindEvent();
      document.body.addEventListener('mousedown', this.onMousedown);
    },
    beforeDestroy() {
      eventBus.$off(EventName.contextmenuClick, this.onContextMenu);
      eventBus.$off(EventName.mousedown, this.onMousedown);
      document.body.removeEventListener('mousedown', this.onMousedown);
    },
    methods: {
      bindEvent() {
        eventBus.$on(EventName.contextmenuClick, this.onContextMenu);
        eventBus.$on(EventName.mousedown, this.onMousedown);
      },
      onContextMenu(e) {
        const { $el } = e.currentTarget;
        this.style.left = e.canvasX + $el.offsetLeft + 'px';
        this.style.top = e.canvasY + $el.offsetTop + 'px';
        this.visible = true;
        this.node = e.item;
      },
      onMousedown(e) {
        if (!this.visible) {
          return;
        }
        const $contextMenu = this.$refs.contextMenu;
        if ($contextMenu && e.target instanceof Node) {
          const isContain = $contextMenu.contains(e.target);
          if (isContain) {
            return;
          }
        }
        this.visible = false;
      },
      handleClick(menu, node) {
        this.visible = false;
        const { command, graph } = getCurrentEditorContext();
        if (!command) {
          return;
        }

        const { cmd } = menu;
        const methodName = `on_${cmd}`;
        const method = this[methodName];

        if (method) {
          // 通用功能处理（如：删除）
          method(command, node, graph, this);
        } else if (this.nodeMenuObj) {
          const cmdMethod = this.nodeMenuObj[methodName];
          if (cmdMethod) {
            cmdMethod(command, node, graph, this);
          }
        }
      },
    },
  };
</script>

<style lang="less" scoped>
  .context-menu {
    position: absolute;
    border: 1px solid #e4e7ed;
    border-radius: 4px;
    background-color: #fff;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    box-sizing: border-box;
    margin: 5px 0;
    z-index: 4;

    li {
      cursor: pointer;
      font-size: 12px;
      height: 28px;
      line-height: 28px;

      &:hover {
        background-color: #f5f7fa;
      }
    }

    .el-dropdown-menu__item--divided:before {
      height: 1px;
    }
  }
</style>
