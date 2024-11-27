<!--
  g6组件配置对话框
-->
<template>
  <div>
    <api-dialog
      ref="api"
      :visible.sync="dialog.api"
      :config="form.api"
      :cur-node="curNode"
      :task-info="taskInfo"
      :is-readonly="isReadonly"
      @cancel="onCancel"
      @save="onSave"
    />
    <connector-dialog
      ref="connector"
      :visible.sync="dialog.connector"
      :config="form.connector"
      :cur-node="curNode"
      :task-info="taskInfo"
      :is-readonly="isReadonly"
      @cancel="onCancel"
      @save="onSave"
    />
    <create-variable-dialog
      ref="create_variable"
      :visible.sync="dialog.create_variable"
      :config="form.create_variable"
      :cur-node="curNode"
      :task-info="taskInfo"
      :is-readonly="isReadonly"
      @cancel="onCancel"
      @save="onSave"
    />
    <update-variable-dialog
      ref="update_variable"
      :visible.sync="dialog.update_variable"
      :config="form.update_variable"
      :cur-node="curNode"
      :task-info="taskInfo"
      :is-readonly="isReadonly"
      @cancel="onCancel"
      @save="onSave"
    />
    <transform-groovery-script-dialog
      ref="transform_groovery_script"
      :visible.sync="dialog.transform_groovery_script"
      :config="form.transform_groovery_script"
      :cur-node="curNode"
      :task-info="taskInfo"
      :is-readonly="isReadonly"
      @cancel="onCancel"
      @save="onSave"
    />
    <transform-select-column-dialog
      ref="transform_select_column"
      :visible.sync="dialog.transform_select_column"
      :config="form.transform_select_column"
      :cur-node="curNode"
      :is-readonly="isReadonly"
      @cancel="onCancel"
      @save="onSave"
    />
    <transform-select-row-dialog
      ref="transform_select_row"
      :visible.sync="dialog.transform_select_row"
      :config="form.transform_select_row"
      :cur-node="curNode"
      :is-readonly="isReadonly"
      @cancel="onCancel"
      @save="onSave"
    />
    <transform-sql-union-dialog
      ref="transform_sql_union"
      :visible.sync="dialog.transform_sql_union"
      :config="form.transform_sql_union"
      :is-readonly="isReadonly"
      @cancel="onCancel"
      @save="onSave"
    />
    <crontab-dialog
      ref="crontab"
      :visible.sync="dialog.crontab"
      :is-readonly="isReadonly"
      :config.sync="form.crontab"
      :cur-node="curNode"
      @cancel="onCancel"
      @save="onSave"
    />
    <branch-dialog
      ref="branch"
      :visible.sync="dialog.branch"
      :is-readonly="isReadonly"
      :config.sync="form.branch"
      :cur-node="curNode"
      :task-info="taskInfo"
      @cancel="onCancel"
      @save="onSave"
      @onDelete="onDelete"
    />
    <branch-edge-dialog
      ref="branchEdge"
      :visible.sync="dialog.branchEdge"
      :is-readonly="isReadonly"
      :cur-node.sync="curNode"
      @cancel="onEdgeCancel"
      @save="onEdgeSave"
    ></branch-edge-dialog>
    <judge-dialog
      ref="judge"
      :visible.sync="dialog.judge"
      :config.sync="form.judge"
      :cur-node="curNode"
      :task-info="taskInfo"
      :is-readonly="isReadonly"
      @cancel="onCancel"
      @save="onSave"
    ></judge-dialog>
    <judge-edge-dialog
      ref="judgeEdge"
      :visible.sync="dialog.judgeEdge"
      :cur-node.sync="curNode"
      :is-readonly="isReadonly"
      @cancel="onEdgeCancel"
      @save="onEdgeSave"
    ></judge-edge-dialog>
    <event-send-dialog
      ref="branchEdge"
      :visible.sync="dialog.event_send"
      :config="form.event_send"
      :is-readonly="isReadonly"
      :task-info="taskInfo"
      :cur-node="curNode"
      @cancel="onCancel"
      @save="onSave"
    ></event-send-dialog>
    <event-receive-dialog
      ref="event_receive"
      :visible.sync="dialog.event_receive"
      :config="form.event_receive"
      :cur-node="curNode"
      :task-info="taskInfo"
      :req-method="reqMethod"
      :api-id="apiId"
      :is-readonly="isReadonly"
      @cancel="onCancel"
      @save="onSave"
    ></event-receive-dialog>
    <foreach-dialog
      v-if="dialog.foreach"
      ref="event_receive"
      :visible.sync="dialog.foreach"
      :config="form.foreach"
      :cur-node="curNode"
      :is-readonly="isReadonly"
      :task-info="taskInfo"
      @cancel="onCancel"
      @save="onSave"
    ></foreach-dialog>
    <sql-execute-dialog
      v-if="dialog.sql_execute"
      ref="sql_execute"
      :visible.sync="dialog.sql_execute"
      :config="form.sql_execute"
      :cur-node="curNode"
      :is-readonly="isReadonly"
      :task-info="taskInfo"
      @cancel="onCancel"
      @save="onSave"
    >
    </sql-execute-dialog>

    <api-view-dialog :api-info="currentAPIInfo" :visible.sync="dialog.view" />
    <api-test-dialog :api-info="currentAPIInfo" is-arrange-page :visible.sync="dialog.test" @tested="onTested" />
    <api-grant-dialog :visible.sync="dialog.grant" :api-info="currentAPIInfo" />
    <api-recover-dialog :visible.sync="dialog.recover" :api-info="currentAPIInfo" />
    <start-dialog
      :visible.sync="dialog.start"
      :task-info="taskInfo"
      :cur-node="curNode"
      :config="form.start"
      :is-readonly="isReadonly"
      @cancel="onCancel"
      @save="onSave"
    ></start-dialog>
    <end-dialog
      :visible.sync="dialog.end"
      :task-info="taskInfo"
      :cur-node="curNode"
      :config="form.end"
      :is-readonly="isReadonly"
      @cancel="onCancel"
      @save="onSave"
    ></end-dialog>
    <try-catch-dialog
      :visible.sync="dialog.try_catch"
      :task-info="taskInfo"
      :cur-node="curNode"
      :config="form.try_catch"
      :is-readonly="isReadonly"
      @cancel="onCancel"
      @save="onSave"
    ></try-catch-dialog>
  </div>
</template>

<!-- eslint-disable vue/no-mutating-props -->
<script>
  import cloneDeep from 'lodash/cloneDeep';
  import successImg from '@/assets/images/task/success.png';
  import eventBus, { EventName } from '../../../components/g6-editor/event-bus';
  import ApiDialog from './api-dialog/index.vue';
  import ConnectorDialog from './connector-dialog/index.vue';
  import TransformSqlUnionDialog from './transform-sql-union-dialog.vue';
  import TransformSelectRowDialog from './transform-select-row-dialog.vue';
  import TransformSelectColumnDialog from './transform-select-column-dialog.vue';
  import TransformGrooveryScriptDialog from './transform-groovery-script-dialog.vue';
  import EventReceiveDialog from './event-receive.vue';
  import CrontabDialog from './crontab-dialog.vue';
  import EventSendDialog from './event-send.vue';
  import BranchDialog from './branch-dialog/index.vue';
  import BranchEdgeDialog from './branch-edge-dialog.vue';
  import JudgeDialog from './judge-dialog.vue';
  import JudgeEdgeDialog from './judge-edge-dialog.vue';
  import ApiViewDialog from '../../api-manage/api-publish/view-api/index.vue';
  import ApiTestDialog from '../../api-manage/api-publish/api-test/index.vue';
  import ApiGrantDialog from '../api-setting/api-grant-dialog.vue';
  import ApiRecoverDialog from '../api-setting/api-recover-dialog.vue';
  import StartDialog from './start-dialog';
  import EndDialog from './end-dialog';
  import CreateVariableDialog from './create-variable-dialog.vue';
  import UpdateVariableDialog from './update-variable-dialog.vue';
  import ForeachDialog from './foreach-dialog/index.vue';
  import SqlExecuteDialog from './sql-execute-dialog.vue';
  import TryCatchDialog from './try-catch-dialog.vue';
  import tryCatchNode from '../node-convert/try-catch';

  // g6节点model.data的uniqueName和对话框映射关系，
  const map = {
    api_normal_table: 'api',
    api_external_http: 'api',
    api_connector: 'connector',
    api_custom_sql: 'api',
    api_lite_flow: 'api',
    api_web_service: 'api',
  };

  export default {
    components: {
      ApiDialog,
      ConnectorDialog,
      TransformSqlUnionDialog,
      TransformSelectColumnDialog,
      TransformSelectRowDialog,
      TransformGrooveryScriptDialog,
      EventReceiveDialog,
      CrontabDialog,
      EventSendDialog,
      ApiViewDialog,
      ApiTestDialog,
      ApiGrantDialog,
      ApiRecoverDialog,
      BranchDialog,
      BranchEdgeDialog,
      StartDialog,
      EndDialog,
      JudgeDialog,
      JudgeEdgeDialog,
      CreateVariableDialog,
      UpdateVariableDialog,
      ForeachDialog,
      SqlExecuteDialog,
      TryCatchDialog,
    },
    provide() {
      return {
        curNode: () => this.curNode,
      };
    },
    props: {
      isReadonly: {
        type: Boolean,
        default: false,
      },
      taskInfo: {
        type: Object,
        default: () => ({}),
      },
      command: {
        type: Object,
        default: () => ({}),
      },
    },
    data() {
      return {
        // 控制弹窗类型
        dialog: {},
        form: {},
        curNode: null,
        currentAPIInfoType: 'node', // or  flow
        curApiInfo: {},
      };
    },
    computed: {
      /**
       * 当前编辑 API 信息
       */
      currentAPIInfo() {
        if (this.currentAPIInfoType === 'node') {
          // 节点 API
          return this.curApiInfo;
        } else {
          // 流程 API
          return this.taskInfo.dataAssetApi;
        }
      },
      /**
       * API请求类型
       */
      reqMethod() {
        return this.taskInfo.dataAssetApi.reqMethod;
      },
      apiId() {
        return this.taskInfo.dataAssetApi.apiAttr.apiId;
      },
    },
    created() {
      this.bindEvent();
    },
    beforeDestroy() {
      eventBus.$off(EventName.showNodeDialog, this.handleNodeDblClick);
      eventBus.$off(EventName.showEdgeDialog, this.handleEdgeDblClick);
      eventBus.$off(EventName.afterAddNode, this.onAddNode);
      eventBus.$off(EventName.afterAddEdge, this.onAfterAddEdge);
      eventBus.$off(EventName.afterAddMultiCombo, this.onAfterAddMultiCombo);
      eventBus.$off(EventName.nodeEdit, this.showDialog);
      eventBus.$off(EventName.nodeViewDetail, this.showDetailDialog);
      eventBus.$off(EventName.toolbarCommand.toolbarCommand, this.showToolbarCommandDialog);
    },
    methods: {
      bindEvent() {
        eventBus.$on(EventName.showNodeDialog, this.handleNodeDblClick);
        eventBus.$on(EventName.showEdgeDialog, this.handleEdgeDblClick);
        eventBus.$on(EventName.showComboDialog, this.handleComboDblClick);
        eventBus.$on(EventName.afterAddNode, this.onAddNode);
        eventBus.$on(EventName.afterAddEdge, this.onAfterAddEdge);
        eventBus.$on(EventName.afterAddMultiCombo, this.onAfterAddMultiCombo);
        eventBus.$on(EventName.nodeEdit, this.showDialog);
        eventBus.$on(EventName.nodeViewDetail, this.showDetailDialog);
        eventBus.$on(EventName.nodeViewDetail, this.showDetailDialog);
        eventBus.$on(EventName.toolbarCommand.toolbarCommand, this.showToolbarCommandDialog);
      },
      /**
       * 双击编辑 弹窗
       */
      handleNodeDblClick(e) {
        this.showDialog(e.item);
      },
      handleEdgeDblClick(e) {
        this.showEdgeDialog(e.item);
      },
      handleComboDblClick(e) {
        this.showDialog(e.item);
      },
      // 从左边拖拽算子进入画布，会调用此方法
      onAddNode(item) {
        const model = item.getModel();
        if (model.from === 'tree' || model.from === 'apiList') {
          this.showDialog(item, true);
          // 更新节点
          this.$emit('update-task-info');
        }
      },
      showEdgeDialog(item) {
        const nodeType = item.getSource()._cfg.currentShape;
        if (nodeType === 'branchNode') {
          this.curNode = item;
          this.$set(this.dialog, 'branchEdge', true);
        } else if (nodeType === 'judgeNode') {
          this.curNode = item;
          this.$set(this.dialog, 'judgeEdge', true);
        }
      },
      // 弹窗并赋值表单
      showDialog(item, isAdd = false) {
        // 使用g6获取节点model信息 用于赋值表单
        const model = item.getModel();

        const { uniqueName } = model.data;
        const name = map[uniqueName] ?? uniqueName;

        eventBus.$emit(EventName.showOrHideTooltip, false);

        this.curNode = item;
        // 读取g6节点model的form属性，表示可修改属性
        this.$set(this.form, name, model.form);

        // 新拖入算子只做保存，不弹窗
        if (!isAdd) {
          this.$set(this.dialog, name, true);
        }
      },
      showDetailDialog(item) {
        const model = item.getModel();
        const { form } = model;
        if (form) {
          const { dataAssetApi } = form;
          if (dataAssetApi) {
            this.currentAPIInfoType = 'node';
            this.curApiInfo = dataAssetApi;
            this.$set(this.dialog, 'view', true);
          }
          this.curNode = null;
        }
      },
      showToolbarCommandDialog(showToolbarCommand) {
        if (this.taskInfo) {
          this.currentAPIInfoType = 'flow';
          this.$set(this.dialog, showToolbarCommand, true);
        }
      },
      onDelete(item) {
        this.command.executeCommand('delete', item);
      },
      onSave(newData, oldModel) {
        if (this.curNode) {
          const { name, desc } = newData;
          const model = Object.assign({}, this.curNode.getModel(), {
            label: desc,
            form: newData,
            data: { desc, name },
            stateImage: successImg, // 更改算子图标
          });

          // 已经保存过，删除from属性，撤消时，不会再弹出对话框
          delete model.from;
          const updateData = {
            item: this.curNode,
            newModel: cloneDeep(model),
            oldModel,
          };
          const { eventId, isHttpSubscriber, addressType, topicId, tags, subscribeRecord } =
            updateData.item.getModel()?.form || {};
          if (subscribeRecord && Object.values(subscribeRecord).length) {
            this.taskInfo.subscribeRecord = subscribeRecord;
            this.taskInfo.subscribeRecord.isHttpSubscriber = isHttpSubscriber;
            if (topicId && isHttpSubscriber === 0) {
              this.taskInfo.subscribeRecord.addressType = addressType;
              this.taskInfo.subscribeRecord.topicId = topicId;
            }
          }
          if (eventId) {
            this.taskInfo.subscribeRecord.eventId = eventId;
            this.taskInfo.subscribeRecord.isHttpSubscriber = isHttpSubscriber;
            if (topicId && isHttpSubscriber === 0) {
              this.taskInfo.subscribeRecord.addressType = addressType;
              this.taskInfo.subscribeRecord.topicId = topicId;
            }
          }
          if (tags) {
            this.taskInfo.subscribeRecord.tags = tags;
          }
          this.command.executeCommand('update', [updateData]);
          this.$emit('toolbar-command', 'Save');
          this.curNode = null;
        }
      },
      onEdgeSave(updateData, edgeName) {
        this.command.executeCommand('update', [updateData]);
        this.command.updateItem(this.curNode, { label: edgeName });
        this.curNode = null;
      },
      onAfterAddEdge(item, graph, isRedo) {
        if (item.destroyed) {
          return;
        }
        // 恢复操作不进行弹窗
        if (!isRedo) {
          this.showEdgeDialog(item);
        }
        // 连线后更新
        this.$emit('update-task-info');
      },
      /**
       * 拖入异常算子特殊处理(自定义两个combo)
       */
      onAfterAddMultiCombo(item, graph) {
        graph.addItems([
          {
            type: 'combo',
            layer: 0,
            model: {
              ...item,
              padding: [40, 2, 2, 2],
            },
          },
          tryCatchNode.makeTryCatchNode('try', item.id, item.x, item.y),
          tryCatchNode.makeTryCatchNode('catch', item.id, item.x, item.y),
        ]);
        this.$emit('update-task-info');
      },
      onEdgeCancel(isEdit) {
        if (!isEdit && this.curNode) {
          this.command.executeCommand('delete', [this.curNode]);
        }
        this.curNode = null;
      },
      onCancel(uniqueName) {
        this.curNode = null;
        this.form[map[uniqueName] ?? uniqueName] = null;
      },
      onTested(data) {
        eventBus.$emit(EventName.showTestResult, data);
      },
    },
  };
</script>

<style lang="less" scoped>
  /deep/ .el-dialog__body {
    padding: 30px !important;
  }
  /deep/ .el-scrollbar__wrap {
    padding: 0 !important;
  }
  /deep/.scrollbar-dialog {
    .scrollbar {
      padding-bottom: 45px;
      margin-bottom: 0px;
    }
  }
</style>
