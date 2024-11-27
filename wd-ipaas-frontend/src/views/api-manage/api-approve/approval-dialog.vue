<template>
  <el-dialog
    :visible.sync="isShow"
    title="审批"
    class="bd-dialog approval-dialog"
    :close-on-click-modal="false"
    width="100%"
    v-loading="loading"
  >
    <el-form label-width="90px" style="padding-bottom: 20px">
      <el-form-item label="审批意见：">
        <el-input type="textarea" maxlength="128" :rows="8" v-model="form.message" />
      </el-form-item>
    </el-form>
    <div class="bd-dialog-footer" slot="footer">
      <el-button size="medium" @click="isShow = false">取消</el-button>
      <el-button size="medium" type="primary" class="bd-button" @click="onSave">保存</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import { ApprovalStatus } from '@/enum.js';
  import approvalAPI from '@/api/approval.js';
  export default {
    props: {
      visible: {
        type: Boolean,
        default: false,
      },
      approvalId: {
        type: Number,
        default: null,
      },
      action: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        approval: ApprovalStatus.approval,
        failureApproval: ApprovalStatus.failureApproval,
        loading: '',
        form: {
          message: '',
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
    },
    watch: {
      visible(val) {
        if (val) {
          this.form.statusEnum = ApprovalStatus.approval.value;
          this.form.message = '';
        }
      },
    },
    methods: {
      onSave() {
        this.save();
      },
      save() {
        this.loading = true;
        const params = {
          approvalComments: this.form.message,
          approvalId: this.approvalId,
          auditResults: this.action,
        };
        approvalAPI
          .apiApproval(params)
          .done(res => {
            this.isShow = false;
            this.$emit('saved');
          })
          .always(() => {
            this.loading = false;
            this.isShow = false;
            this.$emit('saved');
          });
      },
    },
  };
</script>

<style lang="less" scoped>
  .approval-dialog {
    /deep/ .el-dialog {
      max-width: 600px;
    }
  }
</style>
