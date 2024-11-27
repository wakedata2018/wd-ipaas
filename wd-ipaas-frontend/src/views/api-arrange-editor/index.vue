<template>
  <div class="task-editor" :class="isReadonly ? 'task-view' : ''">
    <g6-editor
      ref="g6Editor"
      class="editor"
      :class="!rightExpanded ? '' : 'right-expanded'"
      :is-readonly="isReadonly"
      :before-create-element="handleBeforeCreateElement"
      @after-inited="handleInited"
    >
      <toolbar :graph="graph" :command="command" :task-info="taskInfo" :readonly="isReadonly">
        <toolbar-extra
          ref="extra"
          :graph="graph"
          :task-info="taskInfo"
          :readonly.sync="isReadonly"
          @toolbar-command="toolbarCommand"
          @update-task-info="updateTaskInfo"
        />
      </toolbar>
      <left-panel
        v-if="!isReadonly"
        slot="leftpanel"
        class="left-panel"
        @drag-resize-end="dragResizeLeftEnd"
      ></left-panel>
      <right-panel
        ref="rightpanel"
        slot="rightpanel"
        class="right-panel"
        :task-info="taskInfo"
        :active-name="activeName"
        :class="!rightExpanded ? 'hide' : ''"
        :is-readonly="isReadonly"
        :default-value="defaultValue"
        @change-right-active="changeRightActive"
        @drag-resize-end="dragResizeRightEnd"
      ></right-panel>
      <!-- v-if="!isReadonly" -->

      <context-menu-edit v-if="!isReadonly" :task-info="taskInfo" />
      <context-menu-view v-else :task-info="taskInfo" />
      <query-tabs
        ref="queryTabs"
        :is-readonly="isReadonly"
        :task-info="taskInfo"
        :class="`${!rightExpanded ? 'full' : ''} ${isReadonly ? 'read-only' : ''}`"
      />
      <node-config
        :command="command"
        :task-info="taskInfo"
        :is-readonly="isReadonly"
        @update-task-info="updateTaskInfo"
        @toolbar-command="toolbarCommand"
      />
    </g6-editor>
    <chart-tooltip :tooltip-html="tooltipContent" :visible="tooltipIsShown" />
  </div>
</template>

<!-- eslint-disable n/no-callback-literal -->
<script>
  import { Loading } from 'element-ui';
  import { event } from 'dss-common';
  import { debounce } from 'throttle-debounce';

  import chartTooltip from '@/bz-components/chart-tooltip';
  import apiControllApi from '@/api/api-controller';
  import successImg from '@/assets/images/task/success.png';
  import warnImg from '@/assets/images/task/warn.png';
  import eventBus, { EventName } from '../../components/g6-editor/event-bus';
  import G6Editor from '../../components/g6-editor/index.vue';
  import { getViewCenter } from '../../components/g6-editor/utils';
  import { OPERATION_TYPE } from '@/views/api-manage/api-publish/types';

  import LeftPanel from './left-panel/index.vue';
  import RightPanel from './right-panel/index.vue';
  import Toolbar from './tools/toolbar.vue';
  import ToolbarExtra from './tools/toolbar-extra.vue';
  import ContextMenuEdit from './context-menu/edit/index.vue';
  import ContextMenuView from './context-menu/view/index.vue';
  import NodeConfig from './node-config/index.vue';
  import customNode from './flow/custom-node';
  import branchNode from './flow/branch-node';
  import judgeNode from './flow/judge-node';
  import customEdge from './flow/custom-edge';
  import pollingCombo from './flow/polling-combo';
  import abnormalCombo from './flow/abnormal-combo';
  import validate from './flow/validate';
  import QueryTabs from './query-tabs/index.vue';
  import { getInitForm } from './global/task-conf';
  import nodeConvert from './node-convert';
  import { getDefinition } from './definitions';
  import { getUniqueNameFromModelConfig } from './utils';
  import nodeData from './node-convert/helper/node-data';

  export default {
    components: {
      G6Editor,
      LeftPanel,
      RightPanel,
      Toolbar,
      ToolbarExtra,
      NodeConfig,
      ContextMenuEdit,
      ContextMenuView,
      QueryTabs,
      chartTooltip,
    },
    beforeRouteLeave(to, from, next) {
      const { isEdit, modified, isReadonly, isCopy } = this.$refs.extra;
      if ((isEdit && !modified) || isReadonly || isCopy) {
        next(true);
        return;
      }
      this.$confirm('任务还未保存，确定离开吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          next(true);
        })
        .catch(() => {
          next(false);
        });
    },
    data() {
      return {
        activeName: null,
        taskInfo: getInitForm(),
        graph: null,
        command: null,
        areaZoom: 1,
        tooltipIsShown: false,
        tooltipContent: '',
        dialog: {
          view: false,
          apitest: false,
        },
        defaultValue: {},

        nodeStatusObj: {},
        isShowingStatus: false,
      };
    },
    computed: {
      isEdit() {
        if (this.taskInfo && this.taskInfo.dataAssetApi && this.taskInfo.dataAssetApi.dataAssetApiId) {
          return true;
        }
        return false;
      },
      rightExpanded() {
        let result = false;
        if (this.activeName) {
          result = true;
        }
        return result;
      },
      isCopy() {
        return this.$route.query?.operation === OPERATION_TYPE.COPY;
      },
      isReadonly() {
        return !this.$route.query?.id || !!this.$route.query?.readonly;
      },
    },
    watch: {
      '$route.query'() {
        this.onRefresh();
      },
      isReadonly: {
        handler(val) {
          this.$nextTick(() => {
            event.trigger('resize');
            if (val) {
              setTimeout(() => {
                this.fitViewOnce();
              }, 600);
            }
          });
        },
      },
      rightExpanded(val) {
        this.onExpandRight(val);
      },
      areaZoom(val) {
        if (this.graph) {
          this.graph.zoomTo(val, getViewCenter(this.graph));
        }
      },
    },
    created() {
      this.init();
      const fitViewOnce = function () {
        let first = true;
        return () => {
          if (first) {
            first = false;
            this.fitViewAutomatically();
          }
        };
      }.bind(this);
      this.fitViewOnce = fitViewOnce();
    },
    beforeDestroy() {
      validate.destroy();
      this.unBindEvent();
    },
    methods: {
      init() {
        customNode.init();
        branchNode.init();
        judgeNode.init();
        customEdge.init();
        pollingCombo.init();
        abnormalCombo.init();
        validate.init();
        this.bindEvent();
      },
      bindEvent() {
        eventBus.$on(EventName.refreshData, this.onRefresh);
        eventBus.$on(EventName.showOrHideTooltip, this.onShowOrHideTooltip);
        eventBus.$on(EventName.fitViewAutomatically, this.fitViewAutomatically);
        eventBus.$on(EventName.saveApi, this.onSave);
        eventBus.$on(EventName.updateTaskInfo, this.updateTaskInfo);
      },
      unBindEvent() {
        this.graph.off(EventName.beforelayout, this.handleBeforeLayout);
        this.graph.off(EventName.afterlayout, this.handleAfterLayout);
        eventBus.$off(EventName.refreshData, this.onRefresh);
        eventBus.$off(EventName.showOrHideTooltip, this.onShowOrHideTooltip);
        eventBus.$off(EventName.fitViewAutomatically, this.fitViewAutomatically);
        eventBus.$off(EventName.saveApi, this.onSave);
        eventBus.$off(EventName.updateTaskInfo, this.updateTaskInfo);
      },
      onExpandRight(val) {
        if (this.$refs.rightpanel) {
          const rightPanel = window.$(this.$refs.rightpanel.$el);
          const rightPanelWidth = rightPanel.outerWidth();
          const notRightPanelWidth = 30;
          const newRightPanelWidth = rightPanelWidth - notRightPanelWidth + 1;
          if (val) {
            rightPanel.css('right', '+=' + newRightPanelWidth + 'px');
            window.$(this.$refs.g6Editor.$el).css('padding-right', '+=' + newRightPanelWidth + 'px');
            window.$(this.$refs.queryTabs.$el).css('right', '+=' + newRightPanelWidth + 'px');
          } else {
            rightPanel.css('right', '-=' + newRightPanelWidth + 'px');
            window.$(this.$refs.g6Editor.$el).css('padding-right', '');
            window.$(this.$refs.queryTabs.$el).css('right', '');
          }
          eventBus.$emit(EventName.changeG6Size, {
            rightExpanded: val,
            rightPanelWidth,
          });
        }
      },
      handleInited({ graph, command }) {
        this.graph = graph;
        this.command = command;
        this.graph.on(EventName.beforelayout, this.handleBeforeLayout);
        this.graph.on(EventName.afterlayout, this.handleAfterLayout);
        this.getTaskInfo();
      },
      async handleBeforeCreateElement(model) {
        const uniqueName = getUniqueNameFromModelConfig(model);
        if (uniqueName) {
          const define = getDefinition(uniqueName);
          model = define?.beforeCreate ? await define.beforeCreate(model, this.graph) : model;
          if (define?.validate) {
            model.stateImage = (await define.validate(model)) ? successImg : warnImg;
          }
        }
        return model;
      },
      getTaskInfo() {
        const query = this.$route.query;
        const id = parseInt(query.id);
        if (!id) {
          return;
        }

        this.loading = Loading.service({ fullscreen: true });
        apiControllApi
          .showDetail({ dataAssetApiId: id })
          .done(
            res => {
              // 获取所有算子列表
              return nodeData.fetchNodes(res).done(cmps => {
                const data = res.data || {};
                this.taskInfo = data;
                if (data.dataAssetApi?.apiAttr) {
                  const componentOperator = nodeConvert.paramsToNode(data.dataAssetApi.apiAttr, cmps);
                  this.graph.read(componentOperator);
                  this.$nextTick(() => {
                    this.fitViewAutomatically();
                  });
                }
                // 复制操作需要清除apiId
                if (this.isCopy) {
                  const temp = this.taskInfo.dataAssetApi;
                  temp.dataAssetApiId = null;
                  temp.apiName = `${temp.apiName}_copy`;
                  temp.dataAssetApiMethod = `${temp.dataAssetApiMethod}_copy`;
                  delete temp.apiAttr.apiId;
                  delete temp.apiAttr.id;
                }
              });
            },
            res => {
              const { code } = res;
              if (code === 300) {
                this.$router.replace('/api-arrange-editor');
                if (this.graph) {
                  this.graph.read([]);
                  this.$nextTick(_ => {
                    this.fitViewAutomatically();
                  });
                  // this.zoomToCenter();
                }
              }
            }
          )
          .always(() => {
            this.loading.close();
          });
      },
      updateTaskInfo() {
        /**
         * 收集循环算子以及其子节点信息
         */
        const allCombos = this.graph.getCombos();
        let comboList = {};
        allCombos.forEach(combo => {
          const list = [];
          const comboId = combo.getModel().id;
          const nodes = combo.getNodes();
          nodes.forEach(node => {
            const nodeModel = node.getModel();
            const nodeId = nodeModel.id;
            const edges = node.getEdges();
            /**
             * 寻找单节点没有连线和首个子节点集合
             */
            if (!edges.length || !edges.some(edge => edge.getTarget().getID() === nodeId)) {
              list.push(nodeId);
            }
          });
          comboList = {
            ...comboList,
            [comboId]: list,
          };
        });

        const data = this.graph.save();
        const { operators, locationJson } = nodeConvert.nodeToParams(data);
        const newOperators = this.taskInfo.dataAssetApi.apiAttr.operators;
        const keys = Object.keys(operators);

        for (let i = 0; i < keys.length; i++) {
          const key = keys[i];
          /** 判断为循环算子节点 */
          if (key.includes('combo')) {
            operators[key].subFirstOperators = comboList[key]; // 循环算子首个子节点集合
          }
          /** 判断为异常算子节点 */
          if (key.includes('multiCombo')) {
            /** try catch根据id后面补字段区分 */
            operators[key].subFirstTryOperators = comboList[`${key}_try`];
            operators[key].subFirstCatchOperators = comboList[`${key}_catch`];
          }
        }

        this.taskInfo.dataAssetApi.apiAttr.operators = operators;
        this.taskInfo.dataAssetApi.apiAttr.locationJson = locationJson;
      },
      fitViewAutomatically() {
        this.zoomToCenter();
        this.onFitView();
        this.$nextTick(_ => {
          if (this.areaZoom > 1) {
            this.onZoomToOriginal();
          }
        });
      },
      zoomToCenter() {
        if (this.graph) {
          this.graph.fitView();
          this.$nextTick(_ => {
            this.graph.zoomTo(1, getViewCenter(this.graph));
          });
        }
      },
      onFitView() {
        if (this.graph) {
          this.graph.fitView();
          const zoom = this.graph.getZoom();
          this.areaZoom = zoom;
        }
      },
      onZoomToOriginal() {
        this.areaZoom = 1;
      },
      validateNodesName() {
        const data = this.graph.save();
        const componentOperator = nodeConvert.nodeToParams(data);
        if (!!componentOperator && !!componentOperator.operators) {
          const operators = componentOperator.operators;
          const componentOperatorArr = Object.keys(operators);
          // this.initRightPanel(operators);
          const nameArr = [];
          let result = true;
          for (let i = 0; i < componentOperatorArr.length; i++) {
            const operatorKey = componentOperatorArr[i];
            if (operatorKey !== 'start') {
              const operatorObj = operators[operatorKey];
              /// 检查除输出算子外的算子的重名
              if (operatorObj) {
                const name = operatorObj.name;
                if (name) {
                  if (name === 'globalParam') {
                    this.$message.warning('globalParam不能作为步骤名称。');
                    result = false;
                    break;
                  }
                  if (nameArr.indexOf(name) !== -1) {
                    this.$message.warning('步骤名称不能重复，请修改重复的步骤名称。');
                    result = false;
                    break;
                  } else {
                    nameArr.push(name);
                  }
                }
              }
            }
          }
          return result;
        } else {
          return true;
        }
      },
      /// 可执行或保存的指令
      async toolbarCommand(operation, needValidate = true) {
        /// true表示需要校验
        if (needValidate) {
          this.$message.closeAll();
          if (!this.validateNodesName()) {
            return;
          }
          try {
            await this.$refs.rightpanel.validate();
          } catch {
            this.$message.warning('请检查设置。');
            this.activeName = 'setting';
            return;
          }
          if (!this.$refs.queryTabs.validateTabs()) {
            return;
          }
          if (!validate.checkLinks(this.graph)) {
            return;
          }
        }
        this['on' + operation]();
      },
      initRightPanel(data) {
        const newData = Object.values(data);
        if (newData?.length > 1 && newData[1]?.component?.dataAssetApi) {
          const { apiGroupId, protocol, reqMethod, apiType, dataAssetApiMethod, secret } =
            newData[1]?.component?.dataAssetApi;
          this.defaultValue = {
            apiGroupId,
            protocol,
            reqMethod,
            apiType,
            dataAssetApiMethod,
            secret,
          };
        }
      },
      // 重新获取数据赋值给taskInfo
      onRefresh() {
        this.getTaskInfo();
      },
      onSave(refresh) {
        this.$refs.extra.save(refresh, this.isCopy);
      },
      async onSaveAndPublish() {
        await this.$refs.extra.save(true);
        this.$refs.extra.publish(true);
      },
      async onSaveAndTest() {
        // 如果保存失败则不会执行测试
        await this.$refs.extra.save(true);
        this.onTest();
      },
      // 回到列表页
      onBackToList() {
        this.$router.push({
          name: 'apipublish',
        });
      },
      onTest() {
        this.$refs.extra.test();
      },
      onStartRun() {
        this.$refs.extra.startRun();
      },
      changeRightActive(tabName) {
        this.activeName = tabName;
      },
      dragResizeLeftEnd(changedWidth) {
        this.dragResizeEnd('left', changedWidth);
      },
      dragResizeRightEnd(changedWidth) {
        this.dragResizeEnd('right', changedWidth);
      },
      dragResizeEnd(position, changedWidth) {
        const widthOffset = changedWidth;
        const rightPanel = window.$(this.$refs.rightpanel.$el);
        const rightPanelWidth = rightPanel.outerWidth();
        const mainAreaWidthOffset = widthOffset + 1;
        window.$(this.$refs.g6Editor.$el).css('padding-' + position, '+=' + mainAreaWidthOffset + 'px');
        window.$(this.$refs.queryTabs.$el).css(position, '+=' + mainAreaWidthOffset + 'px');
        eventBus.$emit(EventName.changeG6Size, {
          rightExpanded: null,
          widthOffset,
          rightPanelWidth,
        });
      },
      onShowOrHideTooltip(show, content) {
        this.tooltipIsShown = show;
        if (show) {
          this.tooltipContent = content;
        } else {
          this.tooltipContent = '';
        }
      },
      handleBeforeLayout() {
        if (this.graph.get('layout')) {
          this.loading = Loading.service();
        }
      },
      handleAfterLayout() {
        this.loading?.close();
      },
    },
  };
</script>
<style lang="less" scoped>
  @import '../../css/var.less';
  @right-panel-width: 320px;
  .task-editor {
    user-select: none;
    overflow: hidden;
    height: calc(100vh - @header-height);
  }
  .right-panel {
    position: absolute;
    right: 0;
    top: 60px;
    bottom: -3px;
    // left: calc(100vw - @right-panel-width);
    width: @right-panel-width;
    z-index: 1;
    border-left: 1px solid #dfe0e2;
    transition: right 0.5s;
    &.hide {
      // left: calc(100vw - 30px);
      right: calc(-@right-panel-width + 30px);
    }
  }
  .editor {
    height: calc(~'100vh - 56px');
    padding-left: 250px;
    padding-right: 30px;
    background: white;
    box-sizing: border-box;
    transition: padding-right 0.5s;
    &.right-expanded {
      padding-right: @right-panel-width;
    }
  }

  .left-panel {
    position: absolute;
    left: 0;
    top: 60px;
    bottom: -3px;
    width: 200px;
    border-right: 1px solid #dfe0e2;
  }

  /deep/ .page {
    box-sizing: border-box;
    padding: 80px 20px 0;
    overflow: hidden;

    .graph-container {
      background: #f3f4f8;
      overflow: hidden;
    }
  }
  .task-view {
    user-select: none;
    overflow: hidden;
    height: calc(100vh - @header-height);
    .editor {
      padding-left: 0px;
    }
  }
  /deep/ .query-tabs {
    right: calc(@right-panel-width + 1px);
    &.full {
      right: 30px;
    }
    &.read-only {
      left: 0;
      // right: 0;
      right: 30px;
    }
  }
</style>
