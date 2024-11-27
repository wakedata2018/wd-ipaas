<template>
  <el-dialog
    :visible.sync="isShow"
    title="更新变量"
    class="bd-dialog update-variable"
    top="60px"
    fullscreen
    custom-class="anim-left"
    :close-on-click-modal="false"
  >
    <el-scrollbar class="page-scrollbar">
      <el-form
        ref="form"
        :model="form"
        :rules="rules"
        label-position="right"
        label-width="auto"
        :disabled="isReadonly"
        @submit.native.prevent
      >
        <el-row type="flex" justify="space-between">
          <el-col :span="11">
            <el-form-item label="步骤名称" prop="name">
              <template slot="label">
                <tips-icon content="英文字母开头，仅包含数字、字母和下划线"></tips-icon>
                步骤名称:
              </template>
              <el-input v-model.trim="form.name" size="mini" maxlength="30"> </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="11">
            <el-form-item prop="desc" label="步骤描述">
              <el-input v-model.trim="form.desc" size="mini" maxlength="50"> </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item prop="resultData" class="block">
          <json-schema-variable
            v-model="tree"
            mode="variable"
            :select-params="selectParams"
            :select-method="selectMethod"
            :select-variable="selectVariable"
            select-name-mode
            :is-readonly="isReadonly"
          />
        </el-form-item>
      </el-form>
    </el-scrollbar>
    <div v-if="!isReadonly" slot="footer" class="bd-dialog-footer">
      <el-button class="dss-btn-circle" @click="isShow = false">取消</el-button>
      <el-button class="dss-btn-circle" type="primary" @click="onSave">确定</el-button>
    </div>
    <SelectVariableDialog
      :visible.sync="showVariableDialog"
      :task-info="taskInfo"
      :operator-id="operatorId"
      @selected="selectedVariable"
    />

    <SelectParametersDialog
      :visible.sync="showParamDialog"
      :task-info="taskInfo"
      :operator-id="operatorId"
      @selected="selectedParams"
    ></SelectParametersDialog>

    <SelectMethodDialog
      :visible.sync="showMethodDialog"
      :expression="expression"
      :task-info="taskInfo"
      :operator-id="operatorId"
      @selected="selectedMethod"
    ></SelectMethodDialog>
  </el-dialog>
</template>

<script>
  import cloneDeep from 'lodash/cloneDeep';
  import TipsIcon from '@/components/tips-icon';
  import eventBus, { EventName } from '@/components/g6-editor/event-bus';
  import JsonSchemaVariable from '@/components/json-schema-variable';
  import SelectParametersDialog from '@/components/select-parameter-dialog';
  import SelectVariableDialog from '@/components/select-variable-dialog';
  import SelectMethodDialog from '@/components/select-method-dialog';
  import Validator from '@/utils/validator.js';
  import textUtils from '@/utils/text-utils';

  import { getUniqueNameFromItem, getIdFromItem } from '../utils';

  export default {
    name: 'UpdateVariableDialog',
    components: { TipsIcon, JsonSchemaVariable, SelectParametersDialog, SelectMethodDialog, SelectVariableDialog },
    props: {
      visible: {
        type: Boolean,
        default: false,
      },
      config: {
        type: Object,
        default: null,
      },
      curNode: {
        type: Object,
        default: null,
      },
      isReadonly: {
        type: Boolean,
        default: false,
      },
      taskInfo: {
        type: Object,
        default: null,
      },
    },
    data() {
      return {
        form: {},
        refresh: false,
        rules: {
          name: [{ required: true, trigger: 'blur', validator: Validator.enNameValidator }],
          desc: [{ required: true, message: '步骤描述不能为空', trigger: 'blur' }],
        },
        showParamDialog: false,
        showMethodDialog: false,
        showVariableDialog: false,
        expression: null,
        tree: null,
        cache: null,
        callback: null,
        methodCallback: null,
      };
    },
    computed: {
      uniqueName() {
        return getUniqueNameFromItem(this.curNode);
      },
      operatorId() {
        return getIdFromItem(this.curNode);
      },
      isShow: {
        get() {
          return this.visible;
        },
        set(val) {
          if (!val) {
            this.$emit('cancel', this.uniqueName);
          }
          this.$emit('update:visible', val);
        },
      },
    },
    watch: {
      isShow(val) {
        this.$nextTick(() => {
          if (val) {
            this.initForm();
          }
        });
      },
      tree: {
        deep: true,
        handler(val) {
          const newVal = JSON.stringify(val);
          if (newVal !== this.cache) {
            this.form.variableParams = {
              variableJsonSchema: newVal,
            };
          }
        },
      },
    },
    methods: {
      initForm() {
        this.cache = null;
        this.$refs.form.resetFields();
        this.refresh = !this.refresh;
        const { name, desc, variableParams } = this.config;
        this.form = {
          name,
          desc,
          variableParams,
        };
        if (!this.form?.variableParams?.variableJsonSchema) {
          this.tree = {
            root: {
              type: 'object',
              name: 'root',
              description: '根层级',
              properties: {},
            },
          };
        } else {
          this.cache = this.form.variableParams.variableJsonSchema;
          this.tree = JSON.parse(this.cache);
        }
      },
      validate() {
        if (!this.curNode || this.curNode.destroyed) {
          throw new Error('该节点不存在，无法保存，请重新选择节点。');
        }

        if (this.form.name === 'globalParam') {
          throw new Error('globalParam不能作为步骤名称。');
        }

        const operators = this.taskInfo.dataAssetApi.apiAttr.operators;
        const isFind = Object.keys(operators).some(key => {
          return operators[key].name === this.form.name && key !== this.operatorId;
        });
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
      onSave() {
        this.$refs.form.validate(isValidate => {
          if (!isValidate) {
            this.$message.error(this.$t('validator.emptyWarning'));
            return;
          }
          this.save();
        });
      },
      save() {
        try {
          this.validate();
          this.$emit('save', this.form, cloneDeep(this.curNode.getModel()));
          this.isShow = false;
        } catch (error) {
          this.$message.error(error.message);
        }
      },
      selectParams(callback) {
        this.showParamDialog = true;
        if (typeof callback === 'function') {
          this.callback = callback;
        }
      },
      selectedParams({ nodeId, data }) {
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
      selectVariable(callback) {
        this.showVariableDialog = true;
        if (typeof callback === 'function') {
          this.callback = callback;
        }
      },
      selectedVariable(data) {
        this.callback?.(data);
      },
    },
  };
</script>
<style lang="less" scoped>
  .update-variable {
    ::v-deep .page-scrollbar {
      box-sizing: border-box;
      overflow-x: hidden;
      overflow-y: hidden;
      margin-bottom: 10px;
      .el-scrollbar__bar.is-horizontal {
        display: none;
      }
      .el-scrollbar__wrap {
        overflow-x: hidden;
        max-height: calc(100vh - 210px);

        .el-scrollbar__view {
          padding: 17px 0px;
        }
      }
    }
    ::v-deep .el-dialog__footer {
      position: absolute;
      left: 0;
      bottom: 0;
      width: 100%;
      height: 70px;
    }
    ::v-deep .el-form {
      .block.el-form-item {
        display: flex;
        flex-direction: column;
        .el-form-item__label-wrap {
          margin-left: 0px !important;
        }
        > .el-form-item__content {
          margin-left: 0px !important;
          > .el-form-item__error {
            padding-left: 30px;
          }
        }
      }
    }
  }
</style>
