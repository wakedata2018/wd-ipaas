<template>
  <el-dialog
    :visible.sync="isShow"
    title="选择连线类型"
    class="bd-dialog scrollbar-dialog not-lock-scroll"
    top="80px"
    width="900px"
    :close-on-click-modal="false"
    @open="handleOpen"
    @close="handleClose"
  >
    <el-scrollbar class="scrollbar">
      <el-form ref="form" :rules="rules" label-position="left" :model="form" :disabled="isReadonly">
        <el-form-item label="连线类型" prop="lineType">
          <el-radio-group v-model="form.lineType">
            <el-radio :label="JUDGE_TYPE.TRUE" :disabled="hasTrue">TRUE</el-radio>
            <el-radio :label="JUDGE_TYPE.FALSE" :disabled="hasFalse">FALSE</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
    </el-scrollbar>
    <div v-if="!isReadonly" class="bd-dialog-footer">
      <el-button class="dss-btn-circle" @click="handleClose">取消</el-button>
      <el-button class="dss-btn-circle" type="primary" @click="handleSave">提交</el-button>
    </div>
  </el-dialog>
</template>
<script>
  import cloneDeep from 'lodash/cloneDeep';

  const JUDGE_TYPE = {
    TRUE: 1,
    FALSE: 0,
    UNKNOWN: -1,
  };

  const JUDGE_TYPE_RULE_NAME = {
    [JUDGE_TYPE.TRUE]: 'TRUE',
    [JUDGE_TYPE.FALSE]: 'FALSE',
  };
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
        JUDGE_TYPE,
        rules: { lineType: [{ required: true, message: '请选择连线类型', trigger: 'change' }] },
        form: {
          lineType: 1,
        },
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
      sourceId() {
        return this.curNodeModel?.source;
      },
      targetId() {
        return this.curNodeModel?.target;
      },
      hasTrue() {
        return !!this.apiAttrs?.find(
          i => i.ruleName === JUDGE_TYPE_RULE_NAME[JUDGE_TYPE.TRUE] && i.toOperatorId !== this.targetId
        )?.toOperatorId;
      },
      hasFalse() {
        return !!this.apiAttrs?.find(
          i => i.ruleName === JUDGE_TYPE_RULE_NAME[JUDGE_TYPE.FALSE] && i.toOperatorId !== this.targetId
        )?.toOperatorId;
      },
    },
    methods: {
      handleOpen() {
        this.$nextTick(() => {
          this.$refs.form.resetFields();
          const currentType = this.apiAttrs?.find(i => i.toOperatorId === this.targetId)?.ruleName;
          this.form.lineType = currentType
            ? JUDGE_TYPE[currentType]
            : !this.hasTrue
            ? JUDGE_TYPE.TRUE
            : !this.hasFalse
            ? JUDGE_TYPE.FALSE
            : JUDGE_TYPE.UNKNOWN;
        });
      },
      handleClose() {
        if (this.apiAttrs) {
          this.isShow = false;
          this.$emit(
            'cancel',
            this.apiAttrs.filter(i => !!i.toOperatorId && i.toOperatorId !== this.targetId).length === 2
          );
        }
      },
      handleSave() {
        this.$refs.form.validate(isValidate => {
          if (isValidate) {
            if (this.apiAttrs && this.form.lineType !== JUDGE_TYPE.UNKNOWN) {
              const source = cloneDeep(this.curNode.getSource());
              const updateData = {
                item: source,
                oldModel: cloneDeep(source.getModel()),
              };
              // 删除存在已有的线
              let idx = this.apiAttrs.findIndex(i => i.toOperatorId === this.targetId);
              idx !== -1 && this.apiAttrs.splice(idx, 1);

              // 删除当前线或者已存在的 TRUE 、 FALSE 线
              idx = this.apiAttrs.findIndex(i => i.ruleName === JUDGE_TYPE_RULE_NAME[this.form.lineType]);
              idx !== -1 && this.apiAttrs.splice(idx, 1);

              this.apiAttrs.push({
                clazzName: 'com.wakedata.dw.open.model.api.rule.ApiRuleAttr',
                fromOperatorId: this.sourceId,
                toOperatorId: this.targetId,
                ruleName: JUDGE_TYPE_RULE_NAME[this.form.lineType],
              });

              updateData.newModel = cloneDeep(this.curNode.getSource().getModel());
              this.$emit('save', updateData, JUDGE_TYPE_RULE_NAME[this.form.lineType]);
            }
            this.isShow = false;
          }
        });
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
