<template>
  <div style="" class="run-log" :class="{ fullscreen: isFullscreen }">
    <log-toolbar
      :task-id="!!this.config ? this.config.taskId : null"
      :is-fullscreen.sync="isFullscreen"
      ref="toolbar"
    >
      <template v-if="runResult.errorCode">
        <span
          class="result-code"
          :style="{
            color: !runResult.success
              ? 'rgb(255, 97, 128)'
              : 'rgb(51, 225, 203)',
          }"
        >
          <i
            :class="!runResult.success ? 'el-icon-error' : 'el-icon-success'"
          ></i
          >&nbsp;&nbsp;
          {{ !runResult.success ? "失败" : "成功" }}
        </span>
      </template>
    </log-toolbar>
    <el-scrollbar
      style="height: calc(100% - 42px); margin-top: 42px"
      ref="scrollbar"
    >
      <div
        v-if="runResult.success === false && errorMessage"
        class="run-log-content"
        :style="{
          color: !runResult.success ? 'rgb(255, 97, 128)' : 'rgb(51, 225, 203)',
        }"
        v-html="'[错误信息]<br/>' + errorMessage"
      ></div>
      <div
        v-loading="loading"
        class="run-log-content"
        v-else-if="log.length > 0"
      >
        <pre v-for="(line, index) in log" :key="index">{{ line }}</pre>
      </div>
    </el-scrollbar>
    <div
      class="dss-tip"
      v-if="log.length === 0 && (runResult.success === true || !errorMessage)"
    >
      暂无日志
    </div>
  </div>
</template>

<script>
import LogToolbar from "./log-toolbar.vue";

export default {
  components: { LogToolbar },
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
      isFullscreen: false,
      loading: false,
    };
  },
  computed: {
    log() {
      return this.runResult &&
        this.runResult.data &&
        this.runResult.data.__enable_log__
        ? this.runResult.data.__enable_log__.split("\n")
        : [];
    },
    errorMessage() {
      return this.runResult && this.runResult.errorMessage
        ? this.runResult.errorMessage.replace(/\n/g, "<br/>")
        : null;
    },
  },
  watch: {
    JsonDisplay() {
      console.log(123);
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
            s.update();
          });
        } else {
          scrollbar.wrap.scrollTop = 0;
          this.$refs.scrollbar.update();
        }
      });
    },
  },
};
</script>

<style lang="less" scoped>
.run-log {
  user-select: text;
  height: 100%;
  position: relative;
  display: inline-block;
  width: 100%;
  // transition: all 2.5s;
  .run-log-content {
    padding: 10px;
    font-size: 13px;
    pre {
      padding-left: 4px;
      margin-top: 0;
      margin-bottom: 0;
      font-size: 13px;
      font-family: Consolas, "Courier New", monospace;
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
    bottom: 27px;
    top: 42px;
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
.result-code {
  font-size: 12px;
}
</style>
