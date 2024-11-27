<template>
  <inner-sidebar
    :resizable="!!activeName"
    @drag-resize-end="dragResizeEnd"
    :right-side="true"
    :sidebar-style="{ width: '320px', 'padding-left': '0px', 'z-index': 6 }"
  >
    <RightTab :type-selected="activeName" @change-type-selected="changeTypeSelected" />
    <div class="panel-content">
      <el-scrollbar ref="rightPanelScroller" class="page-scrollbar">
        <div class="scrollbar-content">
          <!-- v-show="activeName==='setting'" -->
          <setting-tab-content
            ref="settingTabContent"
            :task-info="taskInfo"
            :is-readonly="isReadonly"
            :default-value="defaultValue"
          />
        </div>
      </el-scrollbar>
    </div>
  </inner-sidebar>
</template>

<script>
  import RightTab from './right-tab';
  import SettingTabContent from './setting-tab-content';
  import { getInitForm } from '../global/task-conf.js';
  import InnerSidebar from '@/components/inner-sidebar';

  export default {
    components: {
      RightTab,
      SettingTabContent,
      InnerSidebar,
    },
    props: {
      taskInfo: {
        type: Object,
        default: () => getInitForm(),
      },
      activeName: {
        type: String,
        default: null,
      },
      isReadonly: {
        type: Boolean,
        default: false,
      },
      defaultValue: {
        type: Object,
        default: () => {
          return {};
        },
      },
    },
    data() {
      return {
        // activeName: null
      };
    },
    watch: {
      activeName(val) {
        let selected = false;
        if (this.activeName !== val) {
          selected = true;
        }
        if (selected) {
          this.scrollMetrics(0);
        }
      },
    },
    methods: {
      changeTypeSelected(val) {
        let activeName = null;
        if (this.activeName !== val) {
          activeName = val;
        }
        this.$emit('change-right-active', activeName);
      },
      validate() {
        return this.$refs.settingTabContent.validate();
      },
      scrollMetrics(offsetTop) {
        window.$(this.$refs.rightPanelScroller.$refs.wrap)[0].scrollTo(0, offsetTop);
      },
      dragResizeEnd(changedWidth) {
        this.$emit('drag-resize-end', changedWidth);
      },
    },
  };
</script>

<style lang="less" scoped>
  @import '../../../css/var.less';
  @toolbar-height: 60px;
  .right-panel {
    background-color: white;
    /deep/ .el-scrollbar__wrap {
      background: white;
    }
    .panel-content {
      margin-left: 30px;

      .page-scrollbar {
        overflow-x: hidden;
        overflow-y: hidden;
        height: calc(100vh - @header-height - @toolbar-height);
        /deep/ .el-scrollbar__wrap {
          overflow-x: hidden;
        }
        .scrollbar-content {
          padding: 10px 20px 0 10px;
        }
      }
    }
  }
</style>
