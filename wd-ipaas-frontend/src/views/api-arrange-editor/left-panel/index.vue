<template>
  <inner-sidebar
    :resizable="true"
    :right-side="false"
    :sidebar-style="{ width: '250px', 'z-index': 6 }"
    @drag-resize-end="dragResizeEnd"
  >
    <el-scrollbar class="left-panel">
      <el-collapse v-model="activeName" class="operators">
        <el-collapse-item title="编排算子" name="1">
          <node-tree />
        </el-collapse-item>
        <el-collapse-item title="API列表" name="2">
          <node-api />
        </el-collapse-item>
        <el-collapse-item title="连接器" name="3">
          <node-connector />
        </el-collapse-item>
      </el-collapse>
    </el-scrollbar>
  </inner-sidebar>
</template>

<script>
  import InnerSidebar from '@/components/inner-sidebar';
  import NodeTree from './node-tree.vue';
  import NodeApi from './node-api.vue';
  import NodeConnector from './node-connector.vue';

  export default {
    components: {
      NodeTree,
      NodeApi,
      NodeConnector,
      InnerSidebar,
    },
    data() {
      return {
        activeName: ['1'],
      };
    },
    methods: {
      dragResizeEnd(changedWidth) {
        this.$emit('drag-resize-end', changedWidth);
      },
    },
  };
</script>

<style lang="less" scoped>
  @import '../../../css/var.less';
  @toolbar-height: 60px;
  .left-panel {
    height: calc(100vh - @header-height - @toolbar-height);
    /deep/ .el-scrollbar__wrap {
      background: white;
    }
    /deep/.operator-search {
      box-sizing: border-box;
      padding: 0 13px;

      .el-input__inner {
        border-radius: 0px;
        background: transparent;
        border-left: none;
        border-right: none;
        border-top: none;
        border-bottom: 1px solid #ccc;
      }
    }
    .operators {
      /deep/ .el-collapse-item__header {
        height: 40px;
        line-height: 40px;
        color: #2776fb;
        font-weight: bold;
        position: relative;
        padding-left: 36px;

        .el-collapse-item__arrow {
          position: absolute;
          left: 15px;
          top: 11px;
        }

        .el-icon-arrow-right {
          font-size: 17px;

          &::before {
            content: '\E791';
          }
        }

        &.is-active {
          color: #333;

          .el-icon-arrow-right {
            color: #e0e0e0;
          }
        }
      }

      /deep/ .el-tree {
        // background: #f4f6f9;
      }

      /deep/ .el-collapse-item__content {
        // background: #f4f6f9;
      }

      /deep/ .el-collapse-item__wrap {
        overflow: initial;
      }
    }
  }
</style>
