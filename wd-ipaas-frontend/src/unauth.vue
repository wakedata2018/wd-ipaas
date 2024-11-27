<template>
  <div id="app" class="app">
    <web-header class="app-header"></web-header>
    <div class="main-container">
      <dss-no-permission :text="text"/>
    </div>
  </div>
</template>

<script>
import WebHeader from './bz-components/data-header';

export default {
  components: { WebHeader },
  props: {
    text: {
      type: String,
      default: ''
    }
  },
  data() {
    return {};
  },
  mounted() {},
  beforeDestroy() {},
  methods: {}
};
</script>

<style lang="less" scoped>
@import './css/var.less';
.app {
  // @sider-bar-collapse-width: 64px;
  @header-height: 52px;

  .app-header {
    height: @header-height;
    line-height: @header-height;
    background: #333333;
  }

  .main-container {
    position: absolute;
    top: @header-height;
    left: 0;
    right: 0;
    bottom: 0;
    transition: all 0.5s;

    .main-scroll {
      width: 100%;
      transition: all 0.5s;
      height: calc(~'100vh - ' @header-height);
      /deep/ .el-scrollbar__wrap {
        overflow-x: hidden;
      }

      /deep/ .el-scrollbar__bar {
        z-index: 5;
      }
    }

    .main {
      width: 100%;
      height: 100%;
    }
  }
}
</style>

