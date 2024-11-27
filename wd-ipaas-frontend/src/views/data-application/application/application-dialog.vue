<template>
  <el-dialog
    :visible.sync="isShow"
    :title="title"
    class="bd-dialog application-dialog"
    :close-on-click-modal="false"
    width="660px"
  >
    <el-form ref="ruleForm" label-width="130px" style="padding-bottom: 20px" :model="form" :rules="rules">
      <el-form-item label="应用名称" prop="dataAccessAppName">
        <el-input
          v-model.trim="form.dataAccessAppName"
          maxlength="30"
          placeholder="建议简短易懂，仅用于IPAAS平台为开发者展示，后续可修改"
        />
      </el-form-item>
      <el-form-item label="鉴权类型" prop="authType">
        <el-select v-model="form.authType" placeholder="请选择鉴权类型" @change="onAuthTypeChange">
          <el-option
            v-for="item of AuthTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="授权惟客云应用" prop="apiAuthId">
        <el-select v-model="form.apiAuthId" placeholder="请选择授权惟客云应用">
          <el-option v-for="item in appOptions" :key="item.id" :label="item.appName" :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item v-if="isShowAppType" label="指定鉴权方式" prop="connectorAuthId">
        <el-select v-model="form.connectorAuthId" placeholder="请选择指定鉴权方式">
          <el-option
            v-for="item in AppTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="描述" prop="dataAccessDescription">
        <el-input
          v-model.trim="form.dataAccessDescription"
          type="textarea"
          placeholder="请输入内容"
          maxlength="225"
          show-word-limit
          rows="5"
        >
        </el-input>
      </el-form-item>
    </el-form>
    <div slot="footer" class="bd-dialog-footer">
      <el-button size="medium" @click="isShow = false">取消</el-button>
      <el-button size="medium" type="primary" class="bd-button" @click="onSave">保存</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import applicationApi from '@/api/api-application.js';

  import authApi from '@/api/auth';
  import { AuthType, AuthTypeOptions } from '@/utils/enum/auth-list';

  export default {
    props: {
      model: {
        type: Object,
        default: () => {
          return {};
        },
      },
      appAuthType: {
        type: String,
        default: null,
      },
      visible: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        appOptions: null,
        form: {
          dataAccessAppName: null,
          dataAccessDescription: null,
          apiAuthId: null,
          connectorAuthId: null,
          authType: null,
        },
        rules: {
          dataAccessAppName: { required: true, message: '请输入', trigger: 'blur' },
          authType: { required: true, message: '请输入', trigger: 'blur' },
          connectorAuthId: { required: true, message: '请输入', trigger: 'blur' },
        },
        AuthType,
        AuthTypeOptions,
        AppTypeOptions: [],
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
        return !!this.model;
      },
      title() {
        return this.isEdit ? '编辑应用' : '新增应用';
      },
      isShowAppType() {
        return this.form.authType === AuthType.CONNECTOR_AUTH;
      },
    },
    watch: {
      isShow(val) {
        this.$nextTick(() => {
          if (val) {
            this.reInitValue();
          }
        });
      },
      'form.connectorAuthId': {
        async handler() {
          await this.connectAuthInfo();
        },
      },
    },
    async mounted() {
      this.getAuthApplicaptionList();
    },
    methods: {
      reInitValue() {
        this.$refs.ruleForm.resetFields();
        if (this.isEdit && this.model) {
          for (const key in this.form) {
            if (Object.prototype.hasOwnProperty.call(this.model, key)) {
              this.form[key] = this.model[key];
            }
          }
          const params = {
            appId: this.model.dataAccessAppId,
          };
          // 获取授权应用
          applicationApi
            .getAuthApplicaption(params, this.isEdit)
            .then(result => {
              this.form.apiAuthId = JSON.parse(result.data).id;
            })
            .catch(err => {
              console.log(err);
            });
        }
      },
      onSave() {
        this.$refs.ruleForm.validate(vaild => {
          if (!vaild) {
            return;
          }
          this.save();
        });
      },
      save() {
        const apiAuthConfig = this.appOptions.find(item => item.id === this.form.apiAuthId);
        let params = {
          apiAuthConfig: JSON.stringify(apiAuthConfig),
          ...this.form,
        };
        if (this.isEdit) {
          params = {
            dataAccessAppId: this.model.dataAccessAppId,
            ...params,
          };
        } else {
          params = {
            appAuthType: this.appAuthType,
            ...params,
          };
        }
        applicationApi
          .updateApplication(params, this.isEdit)
          .then(result => {
            this.isShow = false;
            this.$message({
              type: 'success',
              message: '保存成功',
            });
            this.$emit('save-success');
          })
          .catch(err => {
            console.log(err);
          });
      },
      async onAuthTypeChange(value) {
        if (value === AuthType.CONNECTOR_AUTH && !this.AppTypeOptions.length) {
          await this.connectAuthInfo();
        }
      },
      // 获取可授权应用列表
      async getAuthApplicaptionList() {
        try {
          const { data } = await applicationApi.getAuthApplicaptionList();
          this.appOptions = data;
        } catch (err) {
          console.error(err);
        }
      },
      // 获取指定鉴权方式
      async connectAuthInfo() {
        try {
          const response = await authApi.fetchConnectorAuthInfo();
          this.AppTypeOptions = response.map(item => {
            return {
              label: item.authName,
              value: item.id,
            };
          });
        } catch (err) {
          console.error(err);
        }
      },
    },
  };
</script>
