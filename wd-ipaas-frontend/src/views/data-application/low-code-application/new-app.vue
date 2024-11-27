<template>
  <el-dialog
    class="bd-dialog not-lock-scroll"
    :close-on-click-modal="false"
    :visible.sync="isShow"
    v-loading="loading"
    :lock-scroll="false"
    :title="title"
    width="750px"
  >
    <el-form label-width="100px" ref="elForm" :model="form" :rules="rules">
      <el-form-item label="应用名称" prop="dataAccessAppName">
        <el-input
          placeholder="请输入应用名称"
          maxlength="64"
          @blur="onInputApp"
          v-model="form.dataAccessAppName"
        ></el-input>
      </el-form-item>
      <el-form-item label="应用LOGO" prop="inCharge">
        <img-upload @on-success="handleLogo">
          <i class="el-icon-plus upload-plus" v-if="!logoUploadBase64"></i>
          <img class="icon upload-plus" v-else :src="logoUploadBase64" />
        </img-upload>
      </el-form-item>
      <el-form-item prop="dataAccessDescription">
        <span slot="label">应用描述</span>
        <el-input
          type="textarea"
          :maxlength="255"
          placeholder="请输入描述"
          v-model="form.dataAccessDescription"
        ></el-input>
      </el-form-item>
      <el-form-item prop="dataAccessDescription">
        <span slot="label">惟客账号</span>
        <el-input type="text" placeholder="请输入用户名" v-model="form.wakeUserName"></el-input>
        <el-input type="password" placeholder="请输入密码" v-model="form.wakePassword" class="text_input"></el-input>
      </el-form-item>
    </el-form>
    <div class="bd-dialog-footer" style="bottom: auto">
      <el-button size="medium" @click="isShow = false">取消</el-button>
      <el-button type="primary" size="medium" class="bd-button" @click="onSave">保存</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import daaAPI from '@/api/data-access-app';
  import imgUpload from '@/components/img-upload/index.vue';
  import { LOW_CODE_APP } from '@/utils/enum/third-app';

  export default {
    components: { imgUpload },
    props: {
      appInfo: {
        type: Object,
        default() {
          return null;
        },
      },
      visible: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        logoUploadBase64: null,
        loading: false,
        form: {
          dataAccessAppName: null,
          dataAccessDescription: null,
          appType: LOW_CODE_APP.value,
          lowCodeLogo: null,
          wakeUserName: null,
          wakePassword: null,
        },
        rules: {
          dataAccessAppName: [{ required: true, message: '请输入应用名称' }],
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
      isEdit() {
        return !!this.appInfo;
      },
      title() {
        return this.isEdit ? '编辑应用' : '新增应用';
      },
    },
    watch: {
      isShow(val) {
        this.resetFields();
        if (val) this.setValues();
      },
    },
    methods: {
      onInputApp() {
        this.form.dataAccessAppName = this.form.dataAccessAppName.replace(/(^\s*)|(\s*$)/g, '');
      },
      resetFields() {
        const form = this.$refs.elForm;
        if (form && form.resetFields) {
          form.resetFields();
        }
        this.form.dataAccessAppName = '';
        this.form.lowCodeLogo = '';
        this.form.dataAccessDescription = '';
        this.logoUploadBase64 = '';
        this.form.wakeUserName = null;
        this.form.wakePassword = null;
      },
      setValues() {
        if (!this.appInfo) return;
        for (const key in this.form) {
          this.form[key] = this.appInfo[key];
        }
        const { lowCodeAccountPo } = this.appInfo;
        if (lowCodeAccountPo) {
          const { userName, pwd } = lowCodeAccountPo;
          this.form.wakeUserName = userName;
          this.form.wakePassword = pwd;
        }
        this.logoUploadBase64 = this.form.lowCodeLogo;
      },
      onSave() {
        const form = this.$refs.elForm;
        form.validate(isValidate => {
          if (!isValidate) return;
          if (this.isEdit) return this.update();
          return this.add();
        });
      },
      async add() {
        this.loading = true;
        try {
          const { data } = await daaAPI.create({
            ...this.form,
            lowCodeLogo: this.logoUploadBase64,
          });
          this.$message({ type: 'success', message: '保存成功' });
          this.$emit('saved', 'create', this.form, data);
          this.isShow = false;
        } finally {
          this.loading = false;
        }
      },
      async update() {
        this.loading = true;
        try {
          await daaAPI.edit({
            ...this.form,
            dataAccessAppId: this.appInfo.dataAccessAppId,
            lowCodeLogo: this.logoUploadBase64,
          });
          this.$message({ type: 'success', message: '保存成功' });
          this.$emit('saved', 'edit', this.form, this.appInfo);
          this.isShow = false;
        } finally {
          this.loading = false;
        }
      },
      handleLogo(data) {
        this.logoUploadBase64 = data;
      },
    },
  };
</script>

<style lang="less" scoped>
  /deep/.el-textarea__inner {
    min-height: 150px !important;
  }
  /deep/.el-dialog__body {
    padding: 20px 60px;
  }
  .text_input {
    margin-top: 10px;
  }

  .upload-plus {
    width: 80px;
    height: 80px;
    display: flex;
    justify-content: center;
    align-items: center;
    border: 1px dashed #ccc;
    border-radius: 5px;
  }
</style>
