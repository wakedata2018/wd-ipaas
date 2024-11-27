<template>
  <el-dialog :visible.sync="isShow" title="审批" class="bd-dialog approval-dialog" :close-on-click-modal="false" width="100%" v-loading="loading">
    <el-form label-width="90px" style="padding-bottom: 20px">
      <el-form-item label="审批结果：">
        <el-radio-group v-model="form.status">
          <el-radio :label="approval">审核通过</el-radio>
          <el-radio :label="failureApproval">审核不通过</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="审批意见：">
        <el-input type="textarea" maxLength="128" :rows="8" v-model="form.approvalMessage" />
      </el-form-item>
    </el-form>
    <div class="bd-dialog-footer" slot="footer">
      <el-button size="medium" @click="isShow = false">取消</el-button>
      <el-button size="medium" type="primary" class="bd-button" @click="onSave">保存</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { ApprovalStatus } from "@/enum.js";
import joinApprove from "@/api/join-approve.js";

export default {
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    approvalId: {
      type: Number,
      default: null
    }
  },
  data() {
    return {
      approval: 'PASS',
      failureApproval: 'REFUSE',
      loading: '',
      form: {
        status: 'PASS',
        approvalMessage: ''
      }
    };
  },
  computed: {
    isShow: {
      get() {
        return this.visible;
      },
      set(val) {
        this.$emit('update:visible', val);
      }
    }
  },
  watch: {
    visible(val) {
      if (val) {
        this.form.approvalMessage = '';
      }
    }
  },
  methods: {
    onSave() {
      this.save();
    },
    save() {
      this.loading = true;
      joinApprove
        .passApprove({
          dataAccessAppId: this.approvalId,
          ...this.form
        })
        .done(res => {
          this.$message({
            message: '审批成功。',
            type: 'success'
          });
          this.isShow = false;
          this.$emit('saved');
        })
        .always(() => {
          this.loading = false;
        });
    }
  }
};
</script>

<style lang="less" scoped>
.approval-dialog {
  /deep/ .el-dialog {
    max-width: 600px;
  }
}
</style>