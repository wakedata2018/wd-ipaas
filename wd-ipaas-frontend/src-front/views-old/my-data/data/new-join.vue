<template>
  <el-dialog
    class="bd-dialog kafka-edit-dialog"
    :visible.sync="isShow"
    v-loading="loading.saving"
    :title="title"
  >
    <el-form label-width="100px" ref="elForm" :model="form" :rules="rules">
      <el-form-item label="接入名称" prop="dataAccessAppName">
        <el-input placeholder="请输入接入名称" v-model="form.dataAccessAppName"></el-input>
      </el-form-item>
      <!-- <el-form-item label="负责人" prop="inCharge">
        <el-input placeholder="请输入负责人" v-model="form.inCharge" :multiple="false"></el-input>
        <user-select
          v-model="form.inCharge"
          label-key="zusrid"
          placeholder="请选择负责人"
          :multiple="false"
        />
      </el-form-item> -->
      <el-form-item prop="dataAccessDescription">
        <span slot="label">描述</span>
        <el-input type="textarea" placeholder="请输入描述" v-model="form.dataAccessDescription"></el-input>
      </el-form-item>
    </el-form>
    <div class="bd-dialog-footer" style="bottom: auto;">
      <el-button size="medium" @click="onCancle()">取消</el-button>
      <el-button type="primary" size="medium" class="bd-button" @click="onSave">保存</el-button>
    </div>
  </el-dialog>
</template>

<script>
import mydata from "../../../api/mydata.js";
import { mapState } from "vuex";
// import UserSelect from '@/components/user/user-select.vue';

export default {
  // components: { UserSelect },
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
        inCharge: [
          {
            required: true,
            message: '请输入负责人'
          }
        ]
        // dataAccessDescription: [
        //   {
        //     required: true,
        //     message: '请输入描述'
        //   }
        // ]
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
        // console.log(this.user.displayName)
        // console.log(this.form.inCharge);
        return this.visible;
      },
      set(val) {
        this.$emit('update:visible', val);
        // this.form.inCharge = this.user.displayName;
      }
    },
    isEdit() {
      // return !!this.appInfo.dataAccessAppId;
      return this.appInfo ? !!this.appInfo.dataAccessAppId : !!0;
    },
    title() {
      return this.isEdit ? '编辑接入' : '新增接入';
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
        // if (key === 'inCharge') {
        //   const accout = this.appInfo[key];
        //   this.form.inCharge = [{ zusrid: accout, znachn: accout }];
        //   continue;
        // }
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
        if (!!this.appInfo.dataAccessAppId) {
          this.update();
        } else {
          this.add();
        }
      });
    },
    add() {
      mydata.addJoin({ ...this.form }).done(res => {
        this.$message({
          type: 'success',
          message: '添加成功'
        });
        this.$emit('saved')
      });
    },
    update() {
      mydata.editJoin({ 
        ...this.form ,
        dataAccessAppId:this.appInfo.dataAccessAppId
      }).done(res => {
        this.$message({
          type: 'success',
          message: '编辑成功'
        });
        this.$emit('saved')
      });
    },
    toParams() {
      const params = this.$plain(this.form);
      // params.inCharge = params.inCharge[0].zusrid;
      return params;
    }
  }
};
</script>

<style lang="less" scoped>
/deep/ .el-dialog {
  margin-top: 20vh !important;
}
</style>