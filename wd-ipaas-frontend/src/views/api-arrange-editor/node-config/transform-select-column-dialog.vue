<template>
  <el-dialog
    :visible.sync="isShow"
    title="选择列"
    class="bd-dialog scrollbar-dialog not-lock-scroll"
    :lock-scroll="false"
    top="80px"
    width="850px"
  >
    <el-scrollbar class="page-scrollbar">
      <div class="content-div">
        <el-form
          ref="form"
          :model="form"
          :rules="rules"
          label-position="left"
          label-width="120px"
          @submit.native.prevent
        >
          <el-form-item label="步骤名称" prop="name">
            <template slot="label">
              <tips-icon :content="$t('validator.nameWithoutChineseValidateDesc')"></tips-icon>
              步骤名称
            </template>
            <el-input v-model="form.name" size="mini" maxlength="50" :disabled="disabled"> </el-input>
          </el-form-item>
          <el-form-item prop="desc">
            <template slot="label">
              <tips-icon :content="$t('validator.nameWithChineseValidateDesc')"></tips-icon>
              步骤描述
            </template>
            <el-input v-model="form.desc" size="mini" maxlength="50" :disabled="disabled"> </el-input>
          </el-form-item>
          <field-params-table
            ref="fieldParamsTable"
            :table-list="form.responseParamMappings"
            :operation-list="operationList"
            :disabled="disabled"
            :cur-model="curModel"
          />
        </el-form>
      </div>
    </el-scrollbar>
    <div v-if="!isReadonly" slot="footer" class="bd-dialog-footer">
      <el-button @click="isShow = false">取消</el-button>
      <el-button type="primary" @click="onSave">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import cloneDeep from 'lodash/cloneDeep';
  import { Message } from 'element-ui';
  import taskComponentApi from '@/api/task-component.js';
  import { getCurrentEditorContext } from '@/components/g6-editor/context';
  import TipsIcon from '@/components/tips-icon';
  import Validator from '@/utils/validator.js';
  import nodeConvert from '../node-convert';
  import { getInitForm } from '../node-convert/transform-select-column';
  import { getIdFromItem, getUniqueNameFromItem } from '../utils';
  import FieldParamsTable from './components/field-params-table';

  export default {
    components: { FieldParamsTable, TipsIcon },
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
    },
    data() {
      return {
        form: getInitForm(),
        rules: {},
        refresh: false,
        operationList: [],
      };
    },
    computed: {
      operatorId() {
        return getIdFromItem(this.curNode);
      },
      isShow: {
        get() {
          return this.visible;
        },
        set(val) {
          if (!val) {
            this.$emit('cancel', getUniqueNameFromItem(this.curNode));
          }
          this.$emit('update:visible', val);
        },
      },
      disabled() {
        return this.isReadonly;
      },
      curModel() {
        if (!this.curNode || !this.curNode.getModel || this.curNode.destroyed) {
          return null;
        }
        return this.curNode.getModel();
      },
    },
    watch: {
      isShow(val) {
        const form = this.$refs.form;
        if (form && form.resetFields) {
          form.resetFields();
        }
        if (val) {
          this.getOperatorFields();
        }
        this.$nextTick(() => {
          if (!val) {
            this.form = getInitForm();
          } else {
            this.initForm();
          }
        });
      },
    },
    created() {
      this.rules = {
        name: [
          {
            required: true,
            trigger: 'blur',
            validator: Validator.enNameValidator,
          },
        ],
        desc: [
          {
            required: true,
            trigger: 'blur',
            message: '不能为空',
          },
        ],
      };
    },
    methods: {
      initForm() {
        this.refresh = !this.refresh;
        this.form = Object.assign(this.form, getInitForm(), this.config);
      },
      async getOperatorFields() {
        const { graph } = getCurrentEditorContext();
        const operatorId = this.curNode?.getID();
        if (operatorId && graph) {
          const data = graph.save();
          const componentOperator = nodeConvert.nodeToParams(data);
          const { data: newData, success } = await taskComponentApi.getOperatorFields(operatorId, componentOperator);
          if (success && newData) {
            this.operationList = [...newData];
          } else {
            this.operationList = [];
          }
        }
      },
      validate() {
        if (!this.curNode || this.curNode.destroyed) {
          throw new Error('该节点不存在，无法保存，请重新选择节点。');
        }

        const operators = this.taskInfo.dataAssetApi.apiAttr.operators;
        const isFind = Object.keys(operators).some(key => {
          return operators[key].name === this.form.name && key !== this.operatorId;
        });
        if (isFind) {
          throw new Error('步骤名称不能重复，请重新设置。');
        }
      },
      onSave() {
        this.$refs.form.validate(isValidate => {
          const foundEmpty = this.$refs.fieldParamsTable.validate();
          if (!isValidate || !foundEmpty) {
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
    },
  };
</script>

<style scoped lang="less">
  .page-scrollbar {
    box-sizing: border-box;
    overflow-x: hidden;
    overflow-y: hidden;
    margin-bottom: 10px;
    /deep/ .el-scrollbar__wrap {
      overflow-x: hidden;
      max-height: calc(100vh - 350px);
    }

    .content-div {
      padding: 0 10px 30px 0;
    }
  }
</style>
