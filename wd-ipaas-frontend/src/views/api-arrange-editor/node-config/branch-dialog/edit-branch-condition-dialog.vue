<template>
  <el-dialog
    :visible.sync="isShow"
    title="新建分支条件"
    class="bd-dialog scrollbar-dialog not-lock-scroll medium"
    custom-class="anim-left"
    fullscreen
  >
    <el-scrollbar class="scrollbar">
      <el-form ref="formRef" :model="branchForm" :rules="rules" label-position="left">
        <el-form-item label="分支名称" label-width="120px" prop="branchName">
          <el-input v-model="branchForm.branchName" maxlength="30" size="mini" style="width: 50%"></el-input>
        </el-form-item>

        <el-form-item label="条件规则" label-width="120px" prop="branchRuleExpression">
          <template slot="label"
            >条件规则
            <tips-icon :content="$t(tips)"></tips-icon>
          </template>
          <el-input
            v-model="branchForm.branchRuleExpression"
            :disabled="!branchForm.conditions?.length"
            style="width: 50%; margin-right: 10px"
            placeholder="(条件名称1 and  条件名称2) or 条件名称3"
          ></el-input>
        </el-form-item>

        <el-form-item prop="conditions">
          <edit-branch-condition-table
            ref="conditionTableRef"
            :table-data="branchForm.conditions"
            :task-info="taskInfo"
            :cur-node="curNode"
            @onDelete="onDelete"
          ></edit-branch-condition-table>
        </el-form-item>

        <el-form-item>
          <el-button type="text" :disabled="branchForm?.conditions?.length >= 5" @click="onInsertCondition"
            >+ 添加条件</el-button
          >
        </el-form-item>
      </el-form>
    </el-scrollbar>

    <div slot="footer" class="bd-dialog-footer">
      <el-button class="dss-btn-circle" @click="onCancel">取消</el-button>
      <el-button class="dss-btn-circle" type="primary" @click="onSave">保存</el-button>
    </div>
  </el-dialog>
</template>
<script>
  import cloneDeep from 'lodash/cloneDeep';
  import TipsIcon from '@/components/tips-icon';
  import branchParamsEnum from '@/utils/enum/branch-params-enum';
  import { getInitForm } from '../../global/task-conf';
  import EditBranchConditionTable from './edit-branch-condition-table';

  export default {
    name: 'BranchDialog',
    components: {
      EditBranchConditionTable,
      TipsIcon,
    },
    props: {
      visible: {
        type: Boolean,
        default: false,
      },
      editRuleParams: {
        type: Object,
        default: () => {
          return {};
        },
      },
      curNode: {
        type: Object,
        default: null,
      },
      taskInfo: {
        type: Object,
        default: () => getInitForm(),
      },
    },
    data() {
      return {
        branchParamsEnum,
        branchForm: {
          branchName: '',
          branchRuleExpression: '',
          conditions: [],
        },
        rules: {
          branchName: [{ required: true, message: '请填写分支名称', trigger: 'blur' }],
          branchRuleExpression: [
            { required: true, message: '请填写规则验证', trigger: 'blur' },
            { required: true, validator: this.onValidate, trigger: 'blur' },
          ],
          conditions: [
            {
              required: true,
              validator: (rule, value, callback) => {
                const rules = value.map(i => i.id);
                const s = new Set(rules);
                if (rules.length !== s.size) {
                  callback(new Error('规则名不允许重复，请重新设置'));
                }
                callback();
              },
              trigger: 'blur',
            },
          ],
        },
        tips: `根据需要组合条件，如：（条件名称1 and  条件名称2） or 条件名称3`,
      };
    },

    computed: {
      isShow: {
        get() {
          return this.visible;
        },
        set(val) {
          this.$emit('update:visible', val);
        },
      },
    },

    watch: {
      isShow: {
        immediate: true,
        handler(val) {
          if (val) {
            this.init();
          }
        },
      },
    },

    methods: {
      resetFields() {
        const formRef = this.$refs.formRef;
        if (formRef) {
          this.init();
          formRef.clearValidate();
        }
      },

      init() {
        this.branchForm = cloneDeep(this.editRuleParams);
        this.$set(
          this.branchForm,
          'conditions',
          this.branchForm.conditions.map(item => {
            return {
              ...item,
              value1: {
                ...item.value1,
                dataType: item.value1.dataType.split(/[<>]/).slice(0, 2),
              },
              value2: {
                ...item.value2,
                dataType: item.value2.dataType.split(/[<>]/).slice(0, 2),
              },
            };
          })
        );
      },

      onSave() {
        this.$refs.formRef.validate(isValidate => {
          if (!isValidate) {
            return;
          }

          if (this.$refs.conditionTableRef.validateConditions()) {
            this.save();
          }
        });
      },

      save() {
        let { conditions, ...other } = this.$plain(this.branchForm);
        conditions = conditions.map(item => {
          const v1DataType = item.value1.dataType;
          const v2DataType = item.value2.dataType;
          return {
            ...item,
            value1: {
              ...item.value1,
              dataType: Array.isArray(v1DataType)
                ? v1DataType[0] + (v1DataType[1] ? `<${v1DataType[1]}>` : '')
                : v1DataType,
            },
            value2: {
              ...item.value2,
              dataType: Array.isArray(v2DataType)
                ? v2DataType[0] + (v2DataType[1] ? `<${v2DataType[1]}>` : '')
                : v2DataType,
            },
          };
        });

        this.$emit('edit', { conditions, ...other });
        this.isShow = false;
      },

      onCancel() {
        this.resetFields();
        this.isShow = false;
      },

      onInsertCondition() {
        if (this.branchForm?.conditions?.length >= 5) {
          return;
        }
        const condition = {
          id: '', // 条件名称
          value1: {
            dataType: '', // "string"
            type: '', //
            expression: '', //
          },
          operator: '', // 比较类型
          value2: {
            dataType: '', // "string"
            type: '', //
            expression: '', //
          },
        };
        this.branchForm.conditions?.push(condition);
        this.branchForm.conditions?.forEach((item, index) => {
          item._conditionId = index + Math.random();
        });
      },

      async onDelete(index) {
        this.branchForm.conditions.splice(index, 1);
      },

      returnConditionRuleNames() {
        const condition = this.branchForm.conditions || [];
        const names = condition.map(item => {
          return item.id;
        });
        return names;
      },

      unifSymbol(text) {
        let res = text.replaceAll('(', '( ');
        res = res.replaceAll('（', '( ');
        res = res.replaceAll(')', ' )');
        res = res.replaceAll('）', ' )');
        return res;
      },

      ValidateRuleExpression() {
        const names = this.returnConditionRuleNames(); // 条件名
        const rule = this.unifSymbol(this.branchForm.branchRuleExpression); // 条件规则
        const expressionKeys = ['(', ')', 'and', 'or', ' ', ...names];
        const rulesArr = rule ? rule.split(' ') : [];

        if (!rule || !names) {
          this.$message.error('规则验证失败：无匹配数据');
          return false;
        }

        let res = true;
        rulesArr.forEach(item => {
          if (item && !expressionKeys.includes(item)) {
            console.log('error-->', item, !expressionKeys.includes(item));
            res = false;
          }
        });
        return res;
      },

      onValidate(rule, value, callback) {
        this.ValidateRuleExpression();
        if (!this.ValidateRuleExpression()) {
          callback(new Error(`规则错误！${this.tips}`));
        } else {
          callback();
        }
      },
    },
  };
</script>
<style lang="less" scoped>
  .add-branch {
    width: 40%;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
</style>
