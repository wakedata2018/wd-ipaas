<template>
  <el-dialog
    :visible.sync="isShow"
    title="选择分支"
    class="bd-dialog scrollbar-dialog not-lock-scroll"
    top="80px"
    width="900px"
    :close-on-click-modal="false"
    @closed="onCancel"
  >
    <el-scrollbar class="scrollbar">
      <el-form ref="form" :rules="rules" label-position="left" :model="form" :disabled="isReadonly">
        <el-form-item label="分支名称" prop="ruleName">
          <el-select v-model="form.ruleName" filterable placeholder="请选择" @change="handleRule">
            <el-option
              v-for="item in branchParams"
              :key="item.judgmentConditions"
              :disabled="jundeDisabeld(item.branchName)"
              :label="item.branchName"
              :value="item.branchName"
            >
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </el-scrollbar>
    <div v-if="!isReadonly" slot="footer" class="bd-dialog-footer">
      <el-button class="dss-btn-circle" @click="onCancel">取消</el-button>
      <el-button class="dss-btn-circle" type="primary" @click="onSave">提交</el-button>
    </div>
  </el-dialog>
</template>
<script>
  import { Message } from 'element-ui';
  import cloneDeep from 'lodash/cloneDeep';

  export default {
    props: {
      visible: {
        type: Boolean,
        default: false,
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
        array: [],
        options: [],
        rules: {
          ruleName: [{ required: true, message: '请选择分支名称', trigger: 'blur' }],
        },
        form: {
          ruleName: '',
          judgmentConditions: '',
        },
        isChanged: false,
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
      curNodeModel() {
        return this.curNode && !this.curNode.destroyed ? this.curNode.getModel() : null;
      },
      apiAttrs() {
        return this.curNode?.getSource?.().getModel().form.dataAssetApi.apiAttrs;
      },
      branchParams() {
        return this.curNode?.getSource?.().getModel().form.dataAssetApi.branchParams;
      },
      sourceId() {
        return this.curNodeModel?.source;
      },
      targetId() {
        return this.curNodeModel?.target;
      },
    },
    watch: {
      isShow: {
        immediate: true,
        handler(val) {
          this.isChanged = false;
          this.$nextTick(() => {
            if (val) {
              this.$refs.form.resetFields();
              this.initForm();
            }
          });
        },
      },
    },
    methods: {
      handleRule(val) {
        this.isChanged = true;
        this.form.judgmentConditions =
          this.branchParams.find(item => item.branchName === val)?.judgmentConditions || '';
      },
      initForm() {
        this.$nextTick(() => {
          this.$refs.form.resetFields();
          this.form.ruleName = this.apiAttrs?.find(i => i.toOperatorId === this.targetId)?.ruleName;
        });
      },
      jundeDisabeld(val) {
        return this.apiAttrs?.some(line => line.ruleName === val && line.toOperatorId);
      },
      onCancel() {
        if (this.apiAttrs) {
          // 如果没有选择分支，则为false，会删除连线
          this.$emit('cancel', !!this.apiAttrs.find(i => i.toOperatorId === this.targetId));
          this.isShow = false;
        }
      },
      onSave() {
        this.$refs.form.validate(isValidate => {
          if (!isValidate) {
            Message.closeAll();
            return;
          }
          this.save();
        });
      },
      save() {
        if (this.apiAttrs && this.form.ruleName && this.isChanged) {
          const source = cloneDeep(this.curNode.getSource());
          const updateData = {
            item: source,
            oldModel: cloneDeep(source.getModel()),
          };

          // 删除存在已有的线
          let idx = this.apiAttrs.findIndex(i => i.toOperatorId === this.targetId);
          idx !== -1 && this.apiAttrs.splice(idx, 1);

          // 删除当前线或者已存在的线
          idx = this.apiAttrs.findIndex(i => i.ruleName === this.form.ruleName);
          idx !== -1 && this.apiAttrs.splice(idx, 1);

          this.apiAttrs.push({
            clazzName: 'com.wakedata.dw.open.model.api.rule.ApiRuleAttr',
            fromOperatorId: this.sourceId,
            toOperatorId: this.targetId,
            ruleName: this.form.ruleName,
            id: this.form.judgmentConditions,
          });
          updateData.newModel = cloneDeep(this.curNode.getSource().getModel());
          this.$emit('save', updateData, this.form.ruleName);
        }
        this.isShow = false;
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
