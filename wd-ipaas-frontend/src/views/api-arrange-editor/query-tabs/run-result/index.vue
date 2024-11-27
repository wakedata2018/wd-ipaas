<template>
  <div style="height: 100%" class="run-log">
    <el-tabs
      type="border-card"
      v-model="activeTabName"
      tab-position="bottom"
      class="log-tabs"
      v-if="resultTabs && resultTabs.length > 0"
    >
      <el-tab-pane
        :label="resultTab"
        :name="resultTab"
        v-for="resultTab in resultTabs"
        :key="resultTab"
      >
      </el-tab-pane>
    </el-tabs>
    <el-scrollbar
      :key="activeTabName"
      style="height: calc(100% - 32px); display: inline-block; width: 100%"
      ref="scrollbar"
    >
      <div class="run-log-content" v-show="JsonDisplay">
        <pre>{{ JsonDisplay }}</pre>
      </div>
    </el-scrollbar>
    <div class="dss-tip" v-if="!JsonDisplay">暂无结果</div>
  </div>
</template>

<script>
export default {
  components: {},
  props: {
    config: {
      type: Object,
      default: null,
    },
    runResult: {
      type: Object,
      default: () => ({
        data: {
          __enable_log__: null,
        },
      }),
    },
  },
  data() {
    return {
      activeTabName: "",
    };
  },
  computed: {
    results() {
      return this.runResult && this.runResult.data ? this.runResult.data : {};
      // return {
      //   aa: { das: "asddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddasasddas",das1: "asddas",d2as: "asddas",d3as: "asddas",da4s: "asddas",d5as: "asddas",da6s: "asddas",d7as: "asddas",da8s: "asddas",das0: "asddas",da9s: "asddas",das01: "asddas",da19s: "asddas" },
      //   aa1: { das: "asddas" },
      //   aa2: null,
      //   aa3: { das: "asddas" },
      // };
    },
    activeResult() {
      const result =
        this.results && this.activeTabName && this.results[this.activeTabName]
          ? this.results[this.activeTabName]
          : null;
      return result;
    },
    JsonDisplay() {
      return this.activeResult
        ? JSON.stringify(this.activeResult, null, 2)
        : "";
    },
    resultTabs() {
      const result = this.results
        ? Object.keys(this.results).filter((item) => item !== "__enable_log__")
        : [];
      return result;
    },
  },
  watch: {
    resultTabs: {
      immediate: true,
      handler(val) {
        this.activeTabName = val.length > 0 ? val[0] : "";
      },
    },
    activeResult() {
      this.scrollToTop();
    },
  },
  beforeDestroy() {},
  methods: {
    scrollToTop() {
      this.$nextTick(() => {
        const scrollbar = this.$refs.scrollbar;
        if (!scrollbar) {
          return;
        }
        if (Object.prototype.toString.call(scrollbar) === "[object Array]") {
          scrollbar.forEach((s) => {
            s.wrap.scrollTop = 0;
            this.$nextTick(s.update);
          });
        } else {
          scrollbar.wrap.scrollTop = 0;
          this.$nextTick(this.$refs.scrollbar.update);
        }
      });
    },
  },
};
</script>

<style lang="less" scoped>
.run-log {
  user-select: text;
  position: relative;
  // transition: all 2.5s;

  .run-log-content {
    padding: 10px;
    pre {
      padding-left: 4px;
      margin-top: 0;
      margin-bottom: 0;
      font-size: 13px;
      font-family: Consolas, "Courier New", monospace;
      white-space: pre;
    }
  }

  // position: fixed;
  // left: 0;
  // right: 0;
  // top: 0;
  // bottom: 0;
  // z-index: 9999;

  .log-tabs {
    position: absolute;
    left: 0;
    bottom: 30px;
    top: 0;
    right: 0;

    border: none;
    box-shadow: none;

    /deep/ .el-tabs__content {
      .el-tab-pane {
        bottom: 0 !important;
      }
    }

    /deep/ .el-tabs__item {
      height: 30px;
      line-height: 30px;
      font-size: 12px;
      background-color: white;
      margin: 0px;

      &:not(.is-active) {
        top: 0px;
        border-top: 1px solid #dcdfe6;
      }
    }
    /deep/ .el-tabs__header {
      height: 30px;
      background-color: white;
      z-index: 40;
    }
  }

  &.fullscreen {
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    right: 0;
    z-index: 299;
    background: white;
  }
}
</style>
