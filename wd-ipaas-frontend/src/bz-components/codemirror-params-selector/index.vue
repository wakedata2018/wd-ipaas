<template>
  <div class="params-selector">
    <el-scrollbar class="scroller">
      <div class="content">
        <el-tree
          default-expand-all
          :indent="10"
          :props="defaultProps"
          :data="paramsArr"
          v-loading="loading"
          ref="tree"
        >
          <span class="custom-tree-container" slot-scope="{ node, data }">
            <span class="custom-tree-node" :title="data.label" draggable @dragstart="handleDragstart(node, data)">
              <i class="fa" :class="`fa-${node.level===1?'table':'columns'}`"></i>
              <rolling-title
                :text-str="data.label"
                :customizedStyle="{width:'calc(100% - 20px)','vertical-align': 'middle',height: '24px', 'margin-top': '-3px'}"
              ></rolling-title>
            </span>
          </span>
        </el-tree>
      </div>
    </el-scrollbar>
  </div>
</template>

<script>
import { RollingTitle } from 'dss-common/lib/components';

export default {
  components: { RollingTitle },
  props: {
    paramsMap: {
      type: Object,
      default: _ => ({})
    }
  },

  data() {
    return {
      defaultProps: {
        children: 'children',
        label: 'label'
      }
    };
  },
  computed: {
    paramsArr() {
      let result = [];
      let paramsMap = this.paramsMap;
      Object.keys(paramsMap).forEach(tableName => {
        result.push({
          label: tableName,
          children: paramsMap[tableName].map(field => ({
            label: field
          }))
        });
      });
      return result;
    }
  },
  methods: {
    handleDragstart(node, data) {
      event.dataTransfer.setData(
        'Text', data.label
      );
    }
  }
};
</script>

<style scoped lang="less">
.params-selector {
  position: relative;
  width: 100%;
  height: 100%;
  border: 1px solid #dcdfe6;
  border-radius: 3px;
  overflow: hidden;

  box-sizing: border-box;
  .scroller {
    height: 100%;
    height: 100%;
    overflow: hidden;
    /deep/ .el-scrollbar__wrap {
      padding: 0px;
      max-height: auto;
    }
  }
  .content {
    // padding: 10px;
    // padding: 5px;
    width: 100%;
    overflow: hidden;
    box-sizing: border-box;
  }
}
.custom-tree-container {
  display: inline-block;
  width: calc(100% - 20px);
  height: 24px;
  overflow: hidden;
  position: relative;
  font-size: 0px;

  .custom-tree-node {
    display: inline-block;
    width: 100%;
    position: relative;
    height: 24px;
    line-height: 24px;
    font-size: 12px;
    i {
      vertical-align: middle;
    }
  }
}
</style>
