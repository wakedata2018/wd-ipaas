<!-- eslint-disable vue/no-deprecated-slot-scope-attribute -->
<template>
  <div>
    <div class="operator-search">
      <el-input v-model="keyword">
        <i slot="prefix" class="el-input__icon el-icon-search"></i>
      </el-input>
    </div>
    <el-tree
      ref="tree"
      v-loading="loading"
      :indent="10"
      class="node-tree"
      :data="nodeTypes"
      :filter-node-method="filterNode"
    >
      <span
        slot-scope="{ node, data }"
        class="custom-tree-node"
        :draggable="data.itemType === 'node' || data.itemType === 'combo' || data.itemType === 'multiCombo'"
        @dragstart="handleDragstart($event, data)"
      >
        <!-- @dragend="handleDragEnd($event,data)" -->
        <span :class="data.class" class="connection-item" :title="node.label">
          <div
            v-if="!!data.image"
            class="card-img card-content"
            :class="`${isDatasource(data) ? 'full-card-image' : ''}`"
            :style="{ background: `${data.color}` }"
          >
            <div :style="{ background: `url(${getIcon(data)})` }"></div>
          </div>
          <div class="card-text card-content">{{ node.label }}</div>
          <!-- {{ node.label }} -->
        </span>
      </span>
    </el-tree>
  </div>
</template>

<script>
  import eventBus, { EventName, setIsDropGraph } from '../../../components/g6-editor/event-bus';
  import nodeData from '../node-convert/helper/node-data';
  import sinkImgs from '@/utils/datasource-images';

  const watchValue = {
    refresh: false,
  };

  function refresh() {
    watchValue.refresh = !watchValue.refresh;
  }

  export { refresh };

  export default {
    data() {
      const ELELIST = [
        {
          label: 'API',
          class: 'category',
        },
        {
          label: 'WebService类型API',
          class: 'category',
        },
        {
          label: '连接器',
          class: 'category',
        },
        {
          label: '数据库API',
          class: 'category',
        },
      ];
      return {
        ELELIST,
        offsetX: 0,
        offsetY: 0,
        nodeTypes: [],
        defaultProps: {},
        loading: false,
        watchValue,
        keyword: '',
      };
    },
    watch: {
      'watchValue.refresh'() {
        this.fetchComponent();
      },
      keyword(val) {
        this.$refs.tree.filter(val);
      },
    },
    created() {
      this.bindEvent();
      this.fetchComponent();
    },
    beforeDestroy() {
      this.unBindEvent();
    },
    methods: {
      filterNode(value, data) {
        if (!value) {
          return true;
        }
        return data.label.indexOf(value) !== -1;
      },
      getIcon(item) {
        const { isDatasource, dbType } = item;

        if (isDatasource && dbType) {
          if (sinkImgs[dbType.toLowerCase()]) {
            return sinkImgs[dbType.toLowerCase()];
          }
        }
        return item.image;
      },
      isDatasource(item) {
        const { isDatasource, dbType } = item;
        if (isDatasource && dbType) {
          if (sinkImgs[dbType.toLowerCase()]) {
            return true;
          }
        }
        return false;
      },
      bindEvent() {
        eventBus.$on(EventName.refreshData, this.fetchComponent);
      },
      unBindEvent() {
        eventBus.$off(EventName.refreshData, this.fetchComponent);
      },
      handleDragstart(event, data) {
        this.offsetX = event.offsetX;
        this.offsetY = event.offsetY;
        setIsDropGraph(true);
        event.dataTransfer.setData('nodeInfo', JSON.stringify(data));
        event.dataTransfer.setData('nodeType', 'tree');
      },
      fetchComponent() {
        this.loading = true;
        nodeData
          .fetchTreeNode()
          .done(res => {
            const filterEle = this.ELELIST.map(o => o.label);
            this.nodeTypes = res.filter(o => !filterEle.includes(o.label)); // 暂时隐藏api注册
            // this.defaultExpaned();
          })
          .always(() => {
            this.loading = false;
          });
      },
      defaultExpaned() {
        this.$nextTick(() => {
          const tree = this.$refs.tree;
          if (!tree) {
            return;
          }
          const root = this.$refs.tree.root;
          if (root.childNodes && root.childNodes.length) {
            root.childNodes[4].expanded = true;
          }
        });
      },
    },
  };
</script>

<style lang="less" scoped>
  .node-tree {
    color: #333;
    font-size: 12px;
    padding-left: 10px;
    box-sizing: border-box;
    padding-right: 2px;

    /deep/ .category {
      font-weight: bold;
    }

    /deep/ .el-icon-caret-right {
      font-size: 15px;
    }

    .custom-tree-node {
      overflow: hidden;
      width: 100%;

      text-overflow: ellipsis;
    }
  }
  .connection-item {
    width: 100%;
    text-align: left;
    box-sizing: border-box;
    overflow: hidden;

    font-size: 12px;
    // background: #f3f6f8;
    color: #333333;
    box-sizing: border-box;
    display: inline-block;
    cursor: pointer;
    vertical-align: middle;
    .card-content {
      vertical-align: middle;
      display: inline-block;
      &.card-img {
        // margin: 10px;
        // padding: 10px;
        // background-color: #bbbfca;
        margin-right: 3px;
        border-radius: 100%;
        width: 16px;
        height: 16px;
        // border: 1px solid #f5f5f5;
        overflow: hidden;
        & > div {
          width: calc(100% - 2 * 3px);
          height: calc(100% - 2 * 3px);
          margin: 3px;
          background-size: auto 100% !important;
          background-position-x: 50% !important;
        }
        &.full-card-image {
          & > div {
            width: 100%;
            height: 100%;
            margin: 0px;
          }
        }
      }
      &.card-text {
        // width: calc(100% - 33px);
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }
  }
</style>
