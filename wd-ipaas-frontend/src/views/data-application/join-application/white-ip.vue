<template>
  <el-dialog
    :visible.sync="isShow"
    title="设置IP"
    :close-on-click-modal="false"
    v-loading="loading"
    class="bd-dialog white-ip"
  >
    <el-form
      label-width="100px"
      style="padding-bottom: 20px"
      :model="form"
      :rules="rules"
      ref="elForm"
    >
      <el-form-item label="" prop="ipType">
        <el-radio-group v-model="ipType">
          <el-radio label="white">白名单</el-radio>
          <el-radio label="black">黑名单</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="授权IP" prop="ips">
        <template slot="label">
          <tips-icon content="多个IP用英文逗号隔开"></tips-icon>
          授权IP
        </template>
        <el-input
          type="textarea"
          :rows="10"
          placeholder="多个IP用逗号隔开"
          v-model.trim="form.ips"
        />
        <div>配置即生效，为空则不限制</div>
      </el-form-item>
    </el-form>
    <div class="bd-dialog-footer" slot="footer">
      <el-button size="medium" @click="isShow = false">取消</el-button>
      <el-button type="primary" size="medium" class="bd-button" @click="onSave"
        >保存</el-button
      >
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
      },
    },
    visible: {
      type: Boolean,
      default: false,
    },
  },
  components: { TipsIcon },
  data() {
    const checkIP = (rule, value, callback) => {
      if (!value) {
        return callback();
      }
      const ips = value.split(",");
      for (const ip of ips) {
        if (!ipReg.test(ip)) {
          callback(`不是一个有效的IP`);
        }
      }
      callback();
    };
    return {
      form: {
        ips: "",
      },
      rules: {
        ips: [{ validator: checkIP, trigger: "blur" }],
      },
      loading: false,
      ipType: 'white'
    };
  },
  computed: {
    isShow: {
      get() {
        return this.visible;
      },
      set(val) {
        this.$emit("update:visible", val);
      },
    },
    appId() {
      return this.appInfo ? this.appInfo.dataAccessAppId : 0;
    },
  },
  watch: {
    visible(val) {
      if (val) {
        this.form.ips = "";
        this.ipType = 'white';
        this.getIPList();
        const form = this.$refs.elForm;
        if (form && form.clearValidate) {
          form.clearValidate();
        }
      }
    },
    ipType: {
      handler(val) {
        this.getIPList(val);
      }
    }
  },
  methods: {
    onSave() {
      const form = this.$refs.elForm;

      form.validate((isValidate) => {
        if (!isValidate) {
          return;
        }
        this.save();
      });
    },
    save(ipType = this.ipType) {
      this.loading = true;
      const action = ipType === 'white' ? 'add' : 'addBlacklist';
      whiteIPAPI[action](this.appId, this.form.ips)
        .done((res) => {
          this.$emit("saved");
          this.$message({
            message: "保存成功",
            type: "success",
          });
          this.isShow = false;
        })
        .always(() => {
          this.loading = false;
        });
    },
    getIPList(ipType = this.ipType) {
      this.loading = true;
      const action = ipType === 'white' ? 'show' : 'showBlacklist';
      whiteIPAPI[action](this.appId)
        .done((res) => {
          this.form.ips = res.data.map((item) => item.ip).join(",");
        })
        .always(() => {
          this.loading = false;
        });
    },
  },
};
</script>

<style lang="less" scoped>
.white-ip {
  /deep/ .el-dialog {
    max-width: 600px;
  }
}
</style>