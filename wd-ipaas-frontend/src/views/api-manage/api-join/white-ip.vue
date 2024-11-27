<template>
  <el-dialog
    :visible.sync="isShow"
    title="IP白名单"
    :close-on-click-modal="false"
    v-loading="loading"
    class="bd-dialog white-ip"
  >
    <el-form label-width="120px" style="padding-bottom: 20px;" :model="form" :rules="rules" ref="elForm">
      <el-form-item label="授权IP：" prop="ips">
         <template slot="label">
          <tips-icon content="多个IP用英文逗号隔开"></tips-icon>
          授权IP：
        </template>
        <el-input type="textarea" :rows="10" placeholder="多个IP用逗号隔开" v-model.trim="form.ips" />
        <div>配置即生效，为空则不限制</div>
      </el-form-item>
    </el-form>
    <div class="bd-dialog-footer" slot="footer">
      <el-button size="medium" @click="isShow = false">取消</el-button>
      <el-button type="primary" size="medium" class="bd-button" @click="onSave">保存</el-button>
    </div>
  </el-dialog>
</template>

<script>
import whiteIPAPI from "../../../api/white-ip.js";
import TipsIcon from "@/components/tips-icon";
const ipReg = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;

export default {
  props: {
    appInfo: {
      type: Object,
      default() {
        return {};
      }
    },
    visible: {
      type: Boolean,
      default: false
    },
    appInfo: {
      type: Object,
      default: null
    }
  },
  components: { TipsIcon },
  data() {
    const checkIP = (rule, value, callback) => {
      if (!value) {
        return callback();
      }
      const ips = value.split(',');
      for (const ip of ips) {
        if (!ipReg.test(ip)) {
          callback(`${ip}不是一个有效的IP`);
        }
      }
      callback();
    };
    return {
      form: {
        ips: ''
      },
      rules: {
        ips: [{ validator: checkIP, trigger: 'blur' }]
      },
      loading: false
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
    },
    appId() {
      return this.appInfo ? this.appInfo.dataAccessAppId : 0;
    }
  },
  watch: {
    visible(val) {
      if (val) {
        this.form.ips = '';
        this.getIPList();
        const form = this.$refs.elForm;
        if (form && form.clearValidate) {
          form.clearValidate();
        }
      }
    }
  },
  methods: {
    onSave() {
      const form = this.$refs.elForm;

      form.validate(isValidate => {
        if (!isValidate) {
          return;
        }
        this.save();
      });
    },
    save() {
      this.loading = true;
      whiteIPAPI
        .add(this.appId, this.form.ips)
        .done(res => {
          this.$emit('saved');
          this.$message({
            message: '保存成功',
            type: 'success'
          });
          this.isShow = false;
        })
        .always(() => {
          this.loading = false;
        });
    },
    getIPList() {
      this.loading = true;
      whiteIPAPI
        .show(this.appId)
        .done(res => {
          this.form.ips = res.data.map(item => item.ip).join(',');
        })
        .always(() => {
          this.loading = false;
        });
    }
  }
};
</script>

<style lang="less" scoped>
.white-ip {
  /deep/ .el-dialog {
    max-width: 600px;
  }
}
</style>