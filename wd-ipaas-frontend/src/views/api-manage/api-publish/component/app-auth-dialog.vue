<template>
  <el-dialog title="授权应用" :visible.sync="isShow" width="800px" center>
    <el-form ref="elForm" :model="form" inline :rules="rules">
      <el-form-item prop="appName" label="授权应用">
        <el-input v-model="form.appName" placeholder="搜索应用名称"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="setValues">搜索</el-button>
      </el-form-item>
    </el-form>
    <div style="color: #9493c1; margin: 10px 10px">搜索结果:({{ totalCount }})</div>
    <el-table :data="parameters" @selection-change="handleSelectionChange" v-loading="appListTableLoading">
      <el-table-column type="selection" width="50" align="center"></el-table-column>
      <el-table-column label="名称" prop="appName"></el-table-column>
      <el-table-column label="描述" prop="description"></el-table-column>
    </el-table>
    <span slot="footer" class="dialog-footer">
      <el-button type="primary" @click="onSave" style="width: 50%">确 定（已选择：{{ selectNum }}）</el-button>
    </span>
  </el-dialog>
</template>
<script>
  import dataAssessApi from '@/api/data-access-app.js';

  export default {
    props: {
      visible: {
        type: Boolean,
        default: false,
      },
      operatorId: {
        type: Number,
        default: -1,
      },
      apiInfo: {
        type: Object,
        default: () => {
          return {};
        },
      },
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
      isShow: {
        immediate: true,
        handler(val) {
          this.resetFields();
          if (val) {
            this.setValues();
          }
        },
      },
      operatorId: {
        immediate: true,
        handler(val) {
          this.operatorAuthId = val;
        },
      },
    },
    data() {
      return {
        form: {
          appName: '',
        },
        parameters: [],
        totalCount: 0,
        addAuthParamsList: [],
        rules: {
          appName: [{ required: true, message: '请输入应用名称', trigger: 'blur' }],
        },
        operatorAuthId: -1,
        appListTableLoading: false,
        selectNum: 0,
      };
    },
    methods: {
      resetFields() {
        if (this.$refs.elForm) {
          this.$refs.elForm.resetFields();
        }
      },
      setValues() {
        this.appListTableLoading = true;
        dataAssessApi
          .getAuthInformationList({ appName: this.form.appName })
          .then(res => {
            this.parameters = res.data;
            this.totalCount = res.totalCount;
          })
          .finally(() => {
            this.appListTableLoading = false;
          });
      },
      handleSelectionChange(val) {
        this.selectNum = val.length;
        this.addAuthParamsList = val.map(item => {
          return {
            authInfoId: item.id,
          };
        });
      },
      onSave() {
        this.addAuthParamsList.forEach(item => {
          item.apiId = this.operatorAuthId < 0 ? this.apiInfo.dataAssetApiId : this.operatorAuthId;
          item.createBy = this.$store.state.user.loginName;
        });
        dataAssessApi
          .addThirdAuth(this.addAuthParamsList)
          .done(res => {
            this.$message.success('授权成功');
            this.$emit('get-third-app-auth-api-list');
            this.isShow = false;
          })
          .always(() => {
            this.isShow = false;
          });
      },
    },
  };
</script>
<style lang="less" scoped></style>
