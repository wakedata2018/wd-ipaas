<template>
  <el-dialog
    class="bd-dialog not-lock-scroll"
    :close-on-click-modal="false"
    :visible.sync="isShow"
    v-loading="loading.saving"
    :lock-scroll="false"
    :title="title"
    width="750px"
  >
    <el-scrollbar class="scrollbar">
      <el-form label-width="100px" ref="elForm" :model="form" :rules="rules">
        <el-form-item label="接入名称" prop="dataAccessAppName">
          <el-input
            placeholder="请输入接入名称"
            maxlength="64"
            @blur="onInputApp"
            v-model="form.dataAccessAppName"
          ></el-input>
        </el-form-item>
        <el-form-item label="负责人" prop="inCharge">
          <el-select v-model="selectedInCharge" filterable placeholder="请选择负责人" :disabled="isEdit">
            <el-option v-for="item in inChargeList" :key="item.id" :label="item.nickName" :value="item.account">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="鉴权方式" prop="authType">
          <el-select v-model="form.authType" filterable placeholder="请选择鉴权方式" :disabled="isEdit">
            <el-option v-for="item in authTypeList" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
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
      </el-form>
    </el-scrollbar>
    <div class="bd-dialog-footer" style="bottom: auto">
      <el-button size="medium" @click="onCancle()">取消</el-button>
      <el-button type="primary" size="medium" class="bd-button" @click="onSave">保存</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import daaAPI from '@/api/data-access-app';
  import appTypeEnum from '@/utils/enum/app-type.js';

  export default {
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
        selectedInCharge: '',
        loading: {
          saving: false,
        },
        form: {
          dataAccessAppName: '',
          dataAccessDescription: '',
          inCharge: '',
          authType: '',
        },
        configsData: [{ key: '', value: '' }],
        authTypeList: [],
        rules: {
          dataAccessAppName: [
            {
              required: true,
              message: '请输入接入名称',
            },
          ],
          dataAccessDescription: [
            {
              required: true,
              message: '请输入描述',
            },
          ],
          inCharge: [
            {
              required: true,
              message: '请输入负责人',
            },
          ],
          authType: [
            {
              required: true,
              message: '请输入鉴权方式',
            },
          ],
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
      selectedInCharge(val) {
        const item = this.inChargeList.find(inChargeItem => inChargeItem.account === val);
        if (item) {
          this.form.inCharge = [{ user: item.account, id: item.account }];
        }
      },
    },
    created() {
      daaAPI.getAuthTypes().done(res => {
        const authTypeList = [];
        const keyValue = res.data || {};
        for (const key in keyValue) {
          authTypeList.push({
            label: keyValue[key],
            value: key,
          });
        }
        this.authTypeList = authTypeList;
      });
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
        this.form.dataAccessDescription = '';
        this.form.authType = '';
        this.form.inCharge = [];
        this.selectedInCharge = '';
      },
      setValues() {
        if (!this.appInfo) {
          return;
        }
        for (const key in this.form) {
          if (key === 'inCharge') {
            const accout = this.appInfo[key];
            this.form.inCharge = [{ user: accout, id: accout }];
            this.selectedInCharge = accout;
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
              message: '保存成功',
            });
            this.$emit('saved', 'create', this.form, res.data);
            this.isShow = false;
          })
          .always(() => {
            this.loading.saving = false;
          });
      },
      update() {
        this.loading.saving = true;
        const parmas = {
          dataAccessAppId: this.appInfo.dataAccessAppId,
          ...this.toParams(this.form),
        };
        daaAPI
          .edit(parmas)
          .done(() => {
            this.$message({
              type: 'success',
              message: '保存成功',
            });
            this.$emit('saved', 'edit', this.form, this.appInfo);
            this.isShow = false;
          })
          .always(() => {
            this.loading.saving = false;
          });
      },
      toParams() {
        const params = this.$plain(this.form);
        params.inCharge = params.inCharge[0].user;
        return params;
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
    width: calc(100% - 10px) !important;
  }
</style>
