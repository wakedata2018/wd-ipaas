<template>
  <el-dialog
    class="bd-dialog end-dialog"
    width="100%"
    top="60px"
    fullscreen
    custom-class="anim-left"
    :visible="visible"
    :close-on-click-modal="false"
    title="结束节点"
    @close="handleClose"
  >
    <el-scrollbar class="page-scrollbar">
      <el-form ref="form" :model="form" class="end-form" label-width="auto" :disabled="isReadonly">
        <el-row type="flex" justify="space-between">
          <el-col :span="11">
            <el-form-item label="步骤名称" prop="name">
              <el-input
                v-model.trim="form.name"
                class="end-dialog-input"
                placeholder="输入下划线、英文字母、数字，不超过30个字"
                maxlength="30"
                disabled
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="11">
            <el-form-item label="步骤描述" prop="desc">
              <el-input
                v-model.trim="form.desc"
                class="end-dialog-input"
                placeholder="不超过50个字符"
                maxlength="50"
                disabled
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div class="parmas-header">
        <h3>返回参数</h3>
      </div>
      <div class="response-params">
        <api-response-edit
          ref="response"
          v-model="responseData"
          body-mode="flowResponse"
          :mode="isReadonly ? 'fixed' : 'edit'"
          :is-readonly="isReadonly"
          :select-params="selectParams"
          :select-method="selectMethod"
          :is-show-sample="false"
          :active-name.sync="activeName"
        />
        <SelectParametersDialog :visible.sync="showDialog" :task-info="taskInfo" @selected="selected">
        </SelectParametersDialog>
        <SelectMethodDialog
          :visible.sync="showMethodDialog"
          :expression="expression"
          :task-info="taskInfo"
          @selected="selectedMethod"
        ></SelectMethodDialog>
      </div>
    </el-scrollbar>
    <div v-if="!isReadonly" class="bd-dialog-footer">
      <el-button class="dss-btn-circle" @click="handleClose">取消</el-button>
      <el-button class="dss-btn-circle" type="primary" @click="onSave">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import cloneDeep from 'lodash/cloneDeep';
  import eventBus, { EventName } from '@/components/g6-editor/event-bus';
  import SelectParametersDialog from '@/components/select-parameter-dialog';
  import SelectMethodDialog from '@/components/select-method-dialog';
  import { ApiResponseEdit } from '@/components/api-edit';
  import textUtils from '@/utils/text-utils';
  import { getInitForm } from '../global/task-conf.js';

  export default {
    name: 'EndDialog',
    components: { SelectParametersDialog, SelectMethodDialog, ApiResponseEdit },
    props: {
      visible: {
        type: Boolean,
        default: false,
      },
      config: {
        type: Object,
        default: null,
      },
      taskInfo: {
        type: Object,
        default: () => getInitForm(),
      },
      curNode: {
        type: Object,
        default: null,
      },
      isReadonly: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        showDialog: false,
        showMethodDialog: false,
        expression: null,
        callback: null,
        methodCallback: null,
        form: {
          name: 'end',
          desc: '结束算子',
        },
        responseData: null,
        activeName: 'body',
      };
    },
    watch: {
      visible(val) {
        if (val) {
          this.form = cloneDeep(this.config);
          this.responseData = cloneDeep(this.taskInfo.resutRespParamDTOS);
        } else {
          this.$refs.form.resetFields();
          this.$refs.response.inited = false;
        }
      },
    },
    methods: {
      selectParams(callback) {
        this.showDialog = true;
        if (typeof callback === 'function') {
          this.callback = callback;
        }
      },
      selected({ nodeId, data }) {
        this.callback?.({ operatorId: nodeId, id: data });
      },
      selectMethod(expression, callback) {
        this.showMethodDialog = true;
        this.expression = expression;
        if (typeof callback === 'function') {
          this.methodCallback = callback;
        }
      },
      selectedMethod(expression) {
        this.expression = null;
        this.methodCallback?.(expression);
      },
      handleClose() {
        this.$emit('cancel', false);
        this.$emit('update:visible', false);
      },
      isValidateTree(data) {
        const root = data?.root;
        if (root?.properties) {
          const res = textUtils.hasEmptyOrMultiName(root.properties);
          if (!res.success) {
            throw new Error('BODY参数名称' + res.message);
          }
        }
      },
      async onSave() {
        try {
          // 校验响应体参数
          const bodyTree = this.responseData.find(item => item.type === 'BODY')?.responsePostData ?? '{}';
          this.isValidateTree(JSON.parse(bodyTree));
          // 校验响应头
          await this.$refs.response.validate();

          this.$emit('save', this.form, cloneDeep(this.curNode.getModel()));
          this.$set(this.taskInfo, 'resutRespParamDTOS', this.responseData);
          this.handleClose();
        } catch (error) {
          this.$message.error(error.message);
        }
      },
    },
  };
</script>
<style lang="less" scoped>
  .end-dialog {
    .page-scrollbar {
      box-sizing: border-box;
      overflow-x: hidden;
      overflow-y: hidden;
      margin-bottom: 10px;

      /deep/ .el-scrollbar__wrap {
        overflow-x: hidden;
        max-height: calc(100vh - 226px);

        .el-scrollbar__view {
          padding-bottom: 17px;
        }
      }
    }

    /deep/ .el-dialog__footer {
      position: absolute;
      left: 0;
      bottom: 0;
      width: 100%;
      height: 70px;
      background: orange;
    }

    .parmas-header {
      display: flex;
      flex-direction: row;
      justify-content: space-between;
      align-items: center;
    }

    .row {
      color: #333;
      font-size: 14px;
      height: 30px;
      line-height: 30px;
    }
  }
</style>
