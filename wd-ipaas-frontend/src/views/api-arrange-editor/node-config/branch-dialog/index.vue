<template>
  <div>
    <el-dialog
      :visible.sync="isShow"
      title="分支算子"
      class="bd-dialog scrollbar-dialog not-lock-scroll medium"
      top="80px"
      custom-class="anim-left"
      :close-on-click-modal="false"
      fullscreen
    >
      <el-scrollbar class="scrollbar">
        <el-form ref="form" :model="form" :rules="rules" label-width="auto" :disabled="isReadonly">
          <el-row type="flex" justify="space-between">
            <el-col :span="11">
              <el-form-item label="步骤名称" prop="name">
                <template slot="label"
                  >步骤名称
                  <tips-icon content="英文字母开头，仅包含数字、字母和下划线"></tips-icon>
                </template>
                <el-input v-model="form.name" maxlength="30"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="11">
              <el-form-item label="步骤描述" prop="desc">
                <el-input v-model="form.desc"></el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <div class="branch-table-block">
            <div class="main-title">分支条件管理</div>
            <el-form-item prop="branchParams" label-width="0">
              <edit-branch-table
                :table-data.sync="form.branchParams"
                @onEdit="onShowEditBranchDialog"
                @onDelete="onDeleteBranch"
              ></edit-branch-table>
            </el-form-item>

            <el-button
              v-if="!isReadonly"
              type="text"
              :disabled="form.branchParams.length >= 10"
              @click="onInsertBranch()"
            >
              + 添加分支
            </el-button>
          </div>
        </el-form>
      </el-scrollbar>

      <div v-if="!isReadonly" slot="footer" class="bd-dialog-footer">
        <el-button class="dss-btn-circle" @click="onCancel">取消</el-button>
        <el-button class="dss-btn-circle" type="primary" @click="onSave">保存</el-button>
      </div>
    </el-dialog>

    <edit-condition-dialog
      :visible.sync="dialog.editBranch"
      :edit-rule-params.sync="editRuleParams"
      :cur-node="curNode"
      :task-info="taskInfo"
      @edit="onEditCondition"
    ></edit-condition-dialog>
  </div>
</template>
<script>
  import { Message } from 'element-ui';
  import cloneDeep from 'lodash/cloneDeep';
  import eventBus, { EventName } from '@/components/g6-editor/event-bus';
  import TipsIcon from '@/components/tips-icon';
  import Validator from '@/utils/validator.js';
  import { getInitForm } from '../../global/task-conf';
  import { getUniqueNameFromItem, getIdFromItem } from '../../utils';

  import EditBranchTable from './edit-branch-table';
  import EditConditionDialog from './edit-branch-condition-dialog.vue';

  export default {
    name: 'BranchDialog',
    components: {
      EditBranchTable,
      EditConditionDialog,
      TipsIcon,
    },
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
        form: {
          name: '',
          desc: '',
          branchParams: [],
          apiAttrs: [],
        },
        rules: {
          name: [{ required: true, validator: Validator.enNameValidator, trigger: 'blur' }],
          desc: [
            { required: true, message: '步骤描述不能为空', trigger: 'blur' },
            { max: 50, message: '不超过50个字符', trigger: 'blur' },
          ],
          branchParams: [
            {
              required: true,
              validator: (rule, value, callback) => {
                const branchs = value.map(i => i.branchName);
                const s = new Set(branchs);
                if (branchs.length !== s.size) {
                  callback(new Error('分支名不允许重复，请重新设置'));
                }
                callback();
              },
              trigger: 'blur',
            },
          ],
        },
        dialog: {
          editBranch: false,
        },
        editRuleIndex: null,
        editRuleParams: {},
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
    },

    methods: {
      initForm() {
        this.form.name = this.config.name;
        this.form.desc = this.config.desc;
        this.form.branchParams = cloneDeep(this.config.dataAssetApi.branchParams) || [];
      },
      onInsertBranch() {
        if (this.form.branchParams.length >= 10) {
          return;
        }
        this.form.branchParams.push({
          judgmentConditions: (Math.random() * 100000).toFixed(),
          branchName: '',
          branchRuleExpression: '',
          conditions: [],
        });
      },
      onShowEditBranchDialog(params, index) {
        this.editRuleIndex = index;
        this.editRuleParams = params;
        this.dialog.editBranch = true;
      },
      onEditCondition(params) {
        this.$set(this.form.branchParams, this.editRuleIndex, params);
      },
      async onDeleteBranch(index) {
        this.form.branchParams.splice(index, 1);
      },
      formatBranch(arr) {
        return arr?.filter(item => item.branchName);
      },
      updateBranch(arr) {
        const dataAssetApi = this.curNode.getModel().form?.dataAssetApi;
        const edges = cloneDeep(this.curNode.getEdges());
        const newAttrs = [];
        const dels = [];

        // 更新分支名
        dataAssetApi.apiAttrs?.forEach(old => {
          const attr = arr.find(i => String(i.judgmentConditions) === String(old.id));
          if (attr) {
            old.ruleName = attr.branchName;
            newAttrs.push(old);
          } else {
            dels.push(old);
          }
        });

        // 删除分支线，并会记录
        if (dels.length) {
          const tmp = edges.filter(node => {
            const _model = node.getModel();
            return dels.find(item => item.fromOperatorId === _model.source && item.toOperatorId === _model.target);
          });
          this.$emit('onDelete', tmp);
        }

        return newAttrs;
      },
      onCancel() {
        this.$refs.form?.resetFields();
        this.isShow = false;
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
          const { name, desc, branchParams } = this.form;
          const branchs = this.formatBranch(branchParams);
          this.$emit(
            'save',
            { name, desc, dataAssetApi: { apiAttrs: this.updateBranch(branchs), branchParams: branchs } },
            cloneDeep(this.curNode.getModel())
          );
          this.isShow = false;
        } catch (error) {
          this.$message.error(error.message);
        }
      },
    },
  };
</script>
<style lang="less" scoped>
  .branch-table-block {
    .main-title {
      height: 22px;
      font-size: 16px;
      margin: 10px 0;
      font-weight: 600;
      color: rgba(51, 51, 51, 1);
      line-height: 22px;
    }
  }
</style>
