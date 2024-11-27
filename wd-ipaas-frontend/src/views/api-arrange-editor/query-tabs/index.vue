<template>
  <div class="query-tabs" :class="{ 'is-collapse': isCollapse }">
    <div class="box">
      <el-tabs v-model="activeName" class="result-tabs" @tab-click="showTabs" @tab-remove="onRemove">
        <el-tab-pane v-for="tab in tabs" :key="tab.key" :label="tab.label" :name="tab.key" :closable="tab.closable">
          <component
            :is="tab.component"
            :ref="'query-tab-component-' + tab.key"
            :is-readonly="isReadonly"
            :task-info="taskInfo"
            :config="tab.config"
            :run-result="runResult"
          />
        </el-tab-pane>
      </el-tabs>
      <el-button
        class="btn-collapse"
        icon="el-icon-d-arrow-left"
        :class="{ 'is-collapse': isCollapse }"
        plain
        round
        @click="isCollapse = !isCollapse"
        >{{ isCollapse ? '查看' : '收起' }}</el-button
      >
    </div>
  </div>
</template>

<script>
  import eventBus, { EventName } from '../../../components/g6-editor/event-bus';

  import RunResult from './run-result/index.vue';
  import RunLog from './run-log/index.vue';
  import { getInitForm } from '../global/task-conf.js';

  export default {
    components: {
      RunResult,
      RunLog,
    },
    props: {
      taskInfo: {
        type: Object,
        default: () => getInitForm(),
      },
      isReadonly: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        tabs: [
          {
            component: 'run-result',
            key: 'run_result',
            label: '运行结果',
            config: null,
            closable: false,
          },
          {
            component: 'run-log',
            key: 'run_log',
            label: '运行日志',
            config: null,
            closable: false,
          },
        ],
        activeName: 'request_params',
        isCollapse: true,
        /**
         * 运行日志和运行结果
         */
        runResult: {
          data: {
            __enable_log__: null,
          },
        },
      };
    },
    watch: {
      activeName() {
        this.showTabs();
      },
    },
    created() {
      this.bindEvent();
    },
    beforeDestroy() {
      eventBus.$off(EventName.queryTab, this.onAddTabs);
      eventBus.$off(EventName.showTestResult, this.onShowTestResult);
    },
    methods: {
      showTabs() {
        this.isCollapse = false;
        this.$nextTick(() => {
          window.$(window).trigger('resize');
        });
      },
      bindEvent() {
        eventBus.$on(EventName.queryTab, this.onAddTabs);
        eventBus.$on(EventName.showTestResult, this.onShowTestResult);
      },
      onAddTabs(tabInfo, isAutoOpen = true) {
        if (isAutoOpen) {
          this.isCollapse = false;
        }
        const tab = this.tabs.find(t => t.key === tabInfo.key);
        if (tab) {
          tab.config = tabInfo.config;
          this.activeName = tab.key;
          return;
        }
        this.tabs.push(tabInfo);
        this.activeName = tabInfo.key;
      },
      onRemove(name) {
        const index = this.tabs.findIndex(tab => tab.key === name);
        if (index <= -1) {
          return;
        }
        this.tabs.splice(index, 1);
        const len = this.tabs.length;
        if (len === 0) {
          this.activeName = '';
          return;
        }
        let activeIndex = index - 1;
        if (activeIndex < 0) {
          activeIndex = 0;
        }

        this.activeName = this.tabs[index - 1].key;
      },
      onShowTestResult(result) {
        if (result && !result.success) {
          this.activeName = 'run_log';
        } else {
          this.activeName = 'run_result';
        }
        this.isCollapse = false;
        this.runResult = result;
      },
      validateTabs() {
        let result = {
          result: true,
        };
        for (let i = 0; i < this.tabs.length; i++) {
          const item = this.tabs[i];
          const tabCmp = this.$refs[`query-tab-component-${item.key}`][0];

          if (tabCmp && tabCmp.validate) {
            const res = tabCmp.validate();
            if (!res.result) {
              result = res;
              break;
            }
          }
        }
        if (!result.result) {
          this.activeName = result.tabName;
          this.isCollapse = false;
          this.$message.closeAll();
          this.$message({
            type: 'warning',
            message: `请检查${result.tabTitle}设置：${result.message}`,
          });
        }
        return result.result;
      },
    },
  };
</script>

<style lang="less" scoped>
  .query-tabs {
    position: absolute;
    bottom: -2px;
    left: 250px;
    background: white;
    // right: 0;
    box-sizing: content-box;
    padding: 10px 20px 30px;
    height: 300px;
    transition: height 0.5s, right 0.5s;
    /deep/.el-select {
      width: 100%;
    }

    &.is-collapse {
      height: 40px;
    }
    /deep/ .el-tabs__item {
      height: 39px;
    }
    /deep/ .el-tabs__content {
      padding: 0 0 0;
      height: 100%;
    }
    .box {
      border: 1px solid rgba(26, 66, 117, 0.1);
      height: 100%;
      overflow: hidden;
    }

    .btn-collapse {
      position: absolute;
      top: 16px;
      right: 38px;

      /deep/ .el-icon-d-arrow-left {
        transition: transform 0.5s;
        transform: rotateZ(-90deg);
      }

      &.is-collapse {
        /deep/ .el-icon-d-arrow-left {
          transform: rotateZ(90deg);
        }
      }
    }

    .result-tabs {
      height: 100%;
      position: relative;

      /deep/ .el-tabs__header {
        margin: 0;
      }

      /deep/ .el-tabs__nav-wrap {
        padding: 0 100px 0 20px;

        &::after {
          height: 1px;
        }
      }

      /deep/ .el-tabs__content {
        .el-tab-pane {
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          bottom: 37px;
        }
      }
    }
  }
  /deep/.dss-tip {
    text-align: center;
    font-size: 12px;
    color: #909399;
    position: absolute;
    top: 50%;
    translate: transformX(-50%);
    width: 100%;
  }
</style>
