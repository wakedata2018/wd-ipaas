<template>
  <div class="connector-tree">
    <el-tree
      :props="treeProps"
      :data="treeData"
      :load="loadNode"
      lazy
      draggable
      :allow-drag="handleAllowDray"
      :allow-drop="handleAllowDrop"
      @node-drag-start="handleDragstart"
    >
      <!-- eslint-disable-next-line vue/no-deprecated-slot-scope-attribute -->
      <span slot-scope="{ node }" class="tree-node-item">
        <div v-if="node.level === 3" class="node-img">
          <div :style="{ background: `url(${networkImg})` }" class="content"></div>
        </div>
        <span :title="node.label">{{ node.label }}</span>
      </span>
    </el-tree>
  </div>
</template>

<script>
  import { setIsDropGraph } from '../../../components/g6-editor/event-bus';
  import nodeData from '../node-convert/helper/node-data.js';
  import apiControllerApi from '@/api/api-controller.js';

  import networkImg from '@/assets/images/task/editor/network.svg';

  export default {
    name: 'NodeConnector',
    data() {
      return {
        treeProps: {
          label: 'groupName',
          children: 'connectorApiGroupDTOList',
          isLeaf: (data, node) => {
            return data.draggable;
          },
        },
        offsetX: 0,
        offsetY: 0,
        treeData: [],
        loading: false,
        networkImg,
        basicInfo: null, // 连机器基本信息配置
      };
    },

    created() {
      this.fetchConnectorComponent();
      this.fetchGroupConnector();
    },

    methods: {
      handleAllowDray(node) {
        return node.data.draggable;
      },

      handleAllowDrop(draggingNode, dropNode, type) {
        return false;
      },

      async loadNode(node, resolve) {
        const { level, data } = node;
        if (level < 2) {
          return resolve(data.children || []);
        } else if (level === 2) {
          const params = {
            connectorId: data.connectorId,
            groupName: data.groupName,
            id: data.id,
          };
          const a = await this.fetchGroupConnectorById(params);
          return resolve(a);
        } else {
          return resolve([]);
        }
      },

      /**
       * formatTreeData
       * 将接口数据转换成树需要的
       * labelName (string), 'connectorName' or 'apiName'
       * draggable (booloan), 只有第三级可拖拽
       */
      formatTreeData(arr = [], labelName, draggable = false) {
        const res = arr.map(item => {
          return {
            ...item,
            groupName: item[labelName],
            children: item.connectorApiGroupDTOList || [],
            draggable,
          };
        });
        return res;
      },

      fetchGroupConnector() {
        apiControllerApi.fetchGroupConnector().then(res => {
          this.treeData = this.formatTreeData(res.data, 'connectorName');
        });
      },
      // 获取连接器算子的基本配置信息
      fetchConnectorComponent() {
        nodeData.fetchNodes().done(nodes => {
          this.basicInfo = nodes.find(node => node.uniqueName === 'api_connector');
        });
      },

      async fetchGroupConnectorById(params) {
        const res = await apiControllerApi.fetchGroupConnectorById(params);
        if (res.success && res.data) {
          return this.formatTreeData(res.data.apiDTOList, 'apiName', true) || [];
        } else {
          return [];
        }
      },

      handleDragstart(data, event) {
        if (data && data.data.draggable) {
          this.offsetX = event.offsetX;
          this.offsetY = event.offsetY;
          setIsDropGraph(true);
          const res = nodeData.toConnectorNode(data.data, this.basicInfo);
          // 这里是model().data 的值
          event.dataTransfer.setData('nodeInfo', JSON.stringify(res)); // 把数据转成图表需要的
          event.dataTransfer.setData('nodeType', 'apiList'); // todo 不知道做什么的
        }
      },
    },
  };
</script>

<style lang="less" scoped>
  .connector-tree {
    padding-left: 12px;
    .group-select {
      margin: 10px 0 20px;
      z-index: 10000;
    }
    .tree-node-item {
      overflow: hidden;
      text-overflow: ellipsis;
      .node-img {
        background: rgb(255, 127, 71);
        margin-right: 3px;
        border-radius: 100%;
        width: 16px;
        height: 16px;
        display: inline-block;
        overflow: hidden;
        vertical-align: middle;
        .content {
          width: calc(100% - 6px);
          height: calc(100% - 6px);
          margin: 3px;
          background-size: auto 100% !important;
          background-position-x: 50% !important;
        }
      }
    }
  }
</style>
