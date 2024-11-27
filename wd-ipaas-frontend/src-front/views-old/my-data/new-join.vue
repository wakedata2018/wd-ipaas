<template>
  <el-dialog
    class="bd-dialog kafka-edit-dialog"
    :close-on-click-modal="false"
    :visible.sync="isShow"
    v-loading="loading.saving"
    :title="title"
  >
    <el-form label-width="100px" ref="elForm" :model="form" :rules="rules">
      <el-form-item label="接入名称" prop="dataAccessAppName">
        <el-input placeholder="请输入接入名称" maxLength="64" v-model="form.dataAccessAppName"></el-input>
      </el-form-item>
      <!-- <el-form-item label="负责人" prop="inCharge">
        <el-input placeholder="请输入负责人" maxLength="32" v-model="form.inCharge" :multiple="false"></el-input>
        <user-select
          v-model="form.inCharge"
          label-key="user"
          placeholder="请选择负责人"
          :multiple="false"
        />
      </el-form-item> -->
      <el-form-item prop="dataAccessDescription">
        <span slot="label">描述</span>
        <el-input type="textarea" maxLength="128" placeholder="请输入描述" v-model="form.dataAccessDescription"></el-input>
      </el-form-item>
    </el-form>
    <div class="bd-dialog-footer" style="bottom: auto;">
      <el-button size="medium" @click="onCancle()">取消</el-button>
      <el-button type="primary" size="medium" class="bd-button" @click="onSave">保存</el-button>
    </div>
  </el-dialog>
</template>

<script>
import daaAPI from "@/api/data-access-app.js";
import UserSelect from "@/components/user/user-select.vue";
import { mapState } from "vuex";
export default {
  components: { UserSelect },
  props: {
    appInfo: {
      type: Object,
      default() {
        return null;
      }
    },
    visible: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      loading: {
        saving: false
      },
      form: {
        dataAccessAppName: '',
        dataAccessDescription: '',
        inCharge: ''
      },
      rules: {
        dataAccessAppName: [
          {
            required: true,
            message: '请输入接入名称'
          }
        ],
        // inCharge: [
        //   {
        //     required: true,
        //     message: '请输入负责人'
        //   }
        // ],
        dataAccessDescription: [
          {
            required: true,
            message: '请输入描述'
          }
        ]
      }
    };
  },
  computed: {
    ...mapState({
      user: state => state.user
    }),
    isShow: {
      get() {
        this.form.inCharge = this.user.displayName;
        return this.visible;
      },
      set(val) {
        this.$emit('update:visible', val);
      }
    },
    isEdit() {
      return !!this.appInfo;
    },
    title() {
      return this.isEdit ? '编辑申请' : '新增申请';
    }
  },
  watch: {
    isShow(val) {
      this.resetFields();
      if (val) {
        this.setValues();
      }
    }
  },
  methods: {
    resetFields() {
      const form = this.$refs.elForm;
      if (form && form.resetFields) {
        form.resetFields();
      }
      this.form.dataAccessAppName = '';
      this.form.dataAccessDescription = '';
      this.form.inCharge = this.user.displayName;
    },
    setValues() {
      if (!this.appInfo) {
        return;
      }
      for (const key in this.form) {
        if (key === 'inCharge') {
          const accout = this.appInfo[key];
          this.form.inCharge = [{ id: accout, znachn: accout }];
          continue;
        }
        this.$set(this.form, key, this.appInfo[key]);
      }
    },
    onCancle() {
      this.isShow = false;
    },
    onSave() {
      const form = this.$refs.elForm;
      form.validate(isVlidate => {
        if (!isVlidate) {
          return;
        }
        if (this.appInfo) {
          this.update();
        } else {
          this.add();
        }
      });
    },
    add() {
      this.loading.saving = true;
      daaAPI
        .create(this.toParams(this.form))
        .done(res => {
          this.$message({
            type: 'success',
            message: '保存成功'
          });
          this.$emit('saved', 'create', this.form, res.data);
          this.isShow = false;
        })
        .always(res => {
          this.loading.saving = false;
        });
    },
    update() {
      this.loading.saving = true;
      const parmas = {
        dataAccessAppId: this.appInfo.dataAccessAppId,
        ...this.toParams(this.form)
      };
      daaAPI
        .edit(parmas)
        .done(res => {
          this.$message({
            type: 'success',
            message: '保存成功'
          });
          this.$emit('saved', 'edit', this.form, this.appInfo);
          this.isShow = false;
        })
        .always(res => {
          this.loading.saving = false;
        });
    },
    toParams() {
      const params = this.$plain(this.form);
      // console.log(params)
      params.inCharge = params.inCharge;
      // console.log(params)
      return params;
    }
  }
};
</script>

<style lang="less" scoped>
/deep/.el-textarea__inner{
  min-height: 150px !important;
}
/deep/ .el-dialog {
  margin-top: 20vh !important;
}
</style>