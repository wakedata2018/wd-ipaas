<template>
  <el-dialog
    class="bd-dialog try-catch-dialog"
    width="100%"
    top="60px"
    fullscreen
    custom-class="anim-left"
    :visible="visible"
    :close-on-click-modal="false"
    title="异常算子"
    @close="handleClose"
  >
    <el-scrollbar class="page-scrollbar">
      <el-form ref="form" :model="form" :rules="rules" class="end-form" label-width="auto" :disabled="isReadonly">
        <el-row type="flex" justify="space-between">
          <el-col :span="11">
            <el-form-item label="步骤名称" prop="name">
              <el-input
                v-model.trim="form.name"
                class="try-catch-dialog-input"
                placeholder="输入下划线、英文字母、数字，不超过30个字"
                maxlength="30"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="11">
            <el-form-item label="步骤描述" prop="desc">
              <el-input
                v-model.trim="form.desc"
                class="try-catch-dialog-input"
                placeholder="不超过50个字符"
                maxlength="50"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
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
  import { getIdFromItem } from '../utils';

  export default {
    name: 'TryCatchDialog',
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
        form: {
          name: 'try_catch',
          desc: '异常算子',
        },
        rules: {
          name: [{ required: true, message: '请输入算子名称', trigger: 'blur' }],
        },
      };
    },
    computed: {
      operatorId() {
        return getIdFromItem(this.curNode);
      },
    },
    watch: {
      visible(val) {
        if (val) {
          this.form = cloneDeep(this.config);
        } else {
          this.$refs.form.resetFields();
        }
      },
    },

    methods: {
      handleClose() {
        this.$emit('cancel', false);
        this.$emit('update:visible', false);
      },
      validate() {
        if (!this.curNode || this.curNode.destroyed) {
          throw new Error('该节点不存在，无法保存，请重新选择节点。');
        }

        const operators = this.taskInfo.dataAssetApi.apiAttr.operators;
        const isFind = Object.keys(operators).some(key => {
          return operators[key].name === this.form.name && key !== this.operatorId;
        });

        if (this.form.name === 'globalParam') {
          throw new Error('globalParam不能作为步骤名称。');
        }

        if (isFind) {
          throw new Error('步骤名称不能重复，请重新设置。');
        }

        if (this.tree?.root?.properties) {
          const res = textUtils.hasEmptyOrMultiName(this.tree.root.properties);
          if (!res.success) {
            throw new Error('参数名称' + res.message);
          }
        }
      },
      async onSave() {
        try {
          await this.$refs.form.validate();
          await this.validate();
          this.$emit('save', this.form, cloneDeep(this.curNode.getModel()));
          this.handleClose();
        } catch (error) {
          this.$message.error(error.message);
        }
      },
    },
  };
</script>
<style lang="less" scoped>
  .try-catch-dialog {
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

    .row {
      color: #333;
      font-size: 14px;
      height: 30px;
      line-height: 30px;
    }
  }
</style>
