<template>
  <el-dialog
    class="bd-dialog not-lock-scroll"
    :close-on-click-modal="false"
    :visible.sync="isShow"
    v-loading="loading"
    :lock-scroll="false"
    :title="title"
    width="950px"
  >
    <el-scrollbar class="scrollbar">
      <el-form label-width="130px" ref="elForm" :model="form" :rules="rules">
        <el-form-item label="接入名称" prop="dataAccessAppName">
          <el-input
            placeholder="请输入接入名称"
            maxlength="64"
            @blur="onInputApp"
            v-model="form.dataAccessAppName"
          ></el-input>
        </el-form-item>
        <el-form-item label="应用类型" prop="appType">
          <el-select
            v-model="form.appType"
            filterable
            placeholder="请选择应用类型"
            @change="getAppData"
            :disabled="isEdit"
          >
            <el-option
              filterable
              v-for="item in indentifyList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="上传图片">
          <img-upload @on-success="handleLogo">
            <i class="el-icon-plus upload-plus" v-if="!logoUploadBase64"></i>
            <img class="icon upload-plus" v-else :src="logoUploadBase64" />
          </img-upload>
        </el-form-item>
        <el-form-item prop="dataAccessDescription">
          <span slot="label">描述</span>
          <el-input
            type="textarea"
            maxlength="128"
            placeholder="请输入描述"
            v-model="form.dataAccessDescription"
          ></el-input>
        </el-form-item>

        <el-form-item label="新增字段">
          <el-button class="add-params" type="primary" @click="onAdd" v-if="configsData && configsData.length"
            >新增字段</el-button
          >
          <el-table :data="configsData" style="width: 100%" class="dss-table bd-table">
            <el-table-column label="字段名称">
              <template #default="{ row }">
                <span class="required">*</span>
                <el-input v-model="row.key" :disabled="!!authorizationApi"></el-input>
              </template>
            </el-table-column>
            <el-table-column label="字段值">
              <template #default="{ row }">
                <span class="required">*</span>
                <el-input v-model="row.value"></el-input>
              </template>
            </el-table-column>
            <el-table-column label="字段位置" min-width="150" v-if="form.appType === appTypeEnum.THIRD_AUTH_APP.value">
              <template #default="{ row }">
                <span class="required">*</span>
                <el-select v-model="row.location" placeholder="请选择">
                  <el-option
                    v-for="item in appTypeEnum.FIELD_LOCATION"
                    :key="item.name"
                    :label="item.label"
                    :value="item.name"
                  >
                  </el-option>
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" v-if="form.appType === appTypeEnum.THIRD_AUTH_APP.value">
              <template #default="{ $index }">
                <el-button @click="onDelete($index)" class="bd-button bd-table-danger" v-if="$index">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
      </el-form>
    </el-scrollbar>
    <div class="bd-dialog-footer" style="bottom: auto">
      <el-button size="medium" @click="isShow = false">取消</el-button>
      <el-button type="primary" size="medium" class="bd-button" @click="onSave">保存</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import daaAPI from '@/api/data-access-app';
  import appTypeEnum from '@/utils/enum/app-type';
  import imgUpload from '@/components/img-upload/index.vue';

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
      inChargeList: {
        type: Array,
        default: () => [],
      },
    },
    data() {
      return {
        appTypeEnum,
        loading: false,
        logoUploadDialogVisible: false,
        logoUploadBase64: '',
        form: {
          dataAccessAppName: '',
          dataAccessDescription: '',
          appType: '',
          appLogo: '',
        },
        configsData: [{ key: '', value: '', location: '' }],
        rules: {
          dataAccessAppName: [{ required: true, message: '请输入接入名称' }],
          dataAccessDescription: [{ required: true, message: '请输入描述' }],
          appType: [{ required: true, message: '请选择应用类型' }],
        },
        indentifyList: [],
        authorizationApi: '', // 其他特定应用需要
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
        return this.isEdit ? '编辑接入' : '新增接入';
      },
    },
    watch: {
      isShow(val) {
        this.resetFields();
        if (val) {
          this.setValues();
        }
      },
    },
    mounted() {
      this.initType();
    },
    methods: {
      async initType() {
        // 暂时使用下拉框展示所有，后期修改成图表
        const { data } = await daaAPI.initIndentify({
          pageNo: 1,
          pageSize: 1000,
        });
        const result = data ? JSON.parse(data) : {};
        const doms = result?.doms?.map(o => ({
          value: o,
          label: o,
        }));
        this.indentifyList = [{ ...appTypeEnum.THIRD_AUTH_APP }, ...doms];
      },
      onInputApp() {
        this.form.dataAccessAppName = this.form.dataAccessAppName.replace(/(^\s*)|(\s*$)/g, '');
      },
      resetFields() {
        const form = this.$refs.elForm;
        if (form && form.resetFields) {
          form.resetFields();
        }
        this.form = {
          dataAccessAppName: '',
          dataAccessDescription: '',
          appType: '',
          appLogo: '',
        };
        this.authorizationApi = '';
        this.configsData = [{ key: '', value: '', location: '' }];
        this.logoUploadBase64 = '';
      },
      setValues() {
        if (!this.appInfo) return;
        this.form.dataAccessAppName = this.appInfo.appName;
        this.form.dataAccessDescription = this.appInfo.description;
        this.authorizationApi = this.appInfo.authorizationApi;
        this.configsData = this.appInfo.configs ? JSON.parse(this.appInfo.configs) : {};
        this.logoUploadBase64 = this.appInfo.appLogo;

        Object.keys(this.appInfo).forEach(key => {
          this.form[key] = this.appInfo[key];
        });
      },
      onSave() {
        const form = this.$refs.elForm;
        form.validate(isVlidate => {
          if (!isVlidate) return;
          const data = this.configsData.find(o => o.key === '' || o.value === '');

          if (data) return this.$message.error('相关字段不能为空');
          this.add();
        });
      },
      async add() {
        this.loading = true;
        let url = 'addAuthInformation';
        const params = {
          appLogo: this.logoUploadBase64,
          appName: this.form.dataAccessAppName,
          appType: this.form.appType,
          description: this.form.dataAccessDescription,
          configs: JSON.stringify(this.configsData),
        };

        if (this.form.id) {
          params.id = this.form.id;
          url = 'updateAuthInformation';
        }

        if (this.authorizationApi) params.authorizationApi = this.authorizationApi;

        try {
          const { success } = await daaAPI[url](params);
          if (success) {
            this.$message.success('保存成功');
            this.$emit('saved');
            this.isShow = false;
          }
        } finally {
          this.loading = false;
        }
      },
      onAdd() {
        const tableData = {
          key: '',
          value: '',
          location: '',
        };
        this.configsData.push(tableData);
      },
      onDelete(index) {
        this.configsData.splice(index, 1);
      },
      handleLogo(data) {
        this.logoUploadBase64 = data;
      },
      async getAppData() {
        if (this.form.appType === appTypeEnum.THIRD_AUTH_APP.value) {
          this.configsData = [{ key: '', value: '', location: '' }];
          this.authorizationApi = '';
          return;
        }

        const res = await daaAPI.getMetadataByServiceName({
          serviceName: this.form.appType,
        });

        const { authorizationApi } = res?.data ?? {};
        this.authorizationApi = authorizationApi;
        const { authorizationApi: apiData } = authorizationApi ? JSON.parse(authorizationApi) : {};
        const { json = [], query = [] } = apiData?.length ? apiData[0] : {};
        const newData = json.concat(query);
        this.configsData = newData.map(o => ({
          key: o,
          value: '',
        }));
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
  .scrollbar {
    box-sizing: border-box;
    overflow-x: hidden;
    overflow-y: hidden;
    margin-bottom: 10px;
    /deep/ .el-scrollbar__wrap {
      overflow-x: hidden;
      max-height: calc(100vh - 350px);
    }
  }
  .required {
    color: #f56c6c;
    margin-right: 4px;
    width: 6px;
    display: inline-block;
  }
  /deep/ .el-input {
    width: calc(100% - 20px) !important;
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
  .add-params {
    margin-bottom: 10px;
  }
</style>
