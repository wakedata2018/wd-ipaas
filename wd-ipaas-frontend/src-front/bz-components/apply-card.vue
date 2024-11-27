<template>
  <el-dialog
    class="bd-dialog apply not-lock-scroll"
    :close-on-click-modal="false"
    :visible.sync="isShow"
    v-loading="loading.saving"
    :title="title"
    top="52px"
    :lock-scroll="false"
    width="800px"
    :modal-append-to-body="false"
  >
    <el-form label-width="100px" style="margin-bottom: 20px" ref="elForm" :model="form" :rules="rules" v-if="apiInfo">
      <el-form-item label="应用名：">
        <div>{{ appName }}</div>
      </el-form-item>
      <el-form-item label="申请名称：">{{ apiInfo.apiName }}</el-form-item>
      <el-form-item label="申请API：">
        {{ apiInfo.dataAssetApiUrl }}
      </el-form-item>
      <el-form-item label="描述：">{{ apiInfo.apiDescription }}</el-form-item>
      <div class="main-title">选择参数</div>

      <el-form-item label="关键词" style="width: auto; display: inline-block; margin-top: 10px">
        <el-input
          placeholder="请输入内容"
          maxlength="64"
          v-model.trim="name"
          clearable
          @keydown.native="onSearchEnter"
        ></el-input>
      </el-form-item>
      <el-button class="bd-button bd-strong search" @click="onSearchParams">查询</el-button>
      <access-table :data="assetTable" :keyword="keyword" />
      <template v-if="tabList.length > 0">
        <div class="main-title">节点参数</div>
        <el-tabs>
          <el-tab-pane v-for="tab in tabList" :key="tab.title" :label="tab.title">
            <access-table :data="tab.config.dataAccessRuleDetailList" :keyword="keyword" />
          </el-tab-pane>
        </el-tabs>
      </template>
    </el-form>
    <div class="bd-dialog-footer" style="bottom: auto">
      <el-button size="medium" @click="onCancle()">取消</el-button>
      <el-button type="primary" size="medium" class="bd-button" @click="onSave">保存</el-button>
    </div>
  </el-dialog>
</template>

<script>
  // import daaAPI from "@/api/data-access-app.js";
  import AccessTable from './apply-table.vue';
  import apply from '../api/apply';
  import myApp from '../api/my-app';

  export default {
    components: { AccessTable },
    props: {
      apiInfo: {
        type: Object,
        default() {
          return null;
        },
      },
      visible: {
        type: Boolean,
        default: false,
      },
      appId: {
        type: [Number, String],
        default: '',
      },
    },
    data() {
      return {
        loading: {
          saving: false,
        },
        keyword: null,
        name: null,
        assetTable: [], // 存放可申请的数据
        data: {},
        form: {
          apiName: '',
          apiDescription: '',
          inCharge: '',
          dataAssetApiUrl: '',
          dataAssetApiId: null,
        },
        appList: [],
        rules: {
          apiName: [
            {
              required: true,
              message: '请输入API名称',
            },
          ],
          apiDescription: [
            {
              required: true,
              message: '请输入描述',
            },
          ],
        },
        tabList: [],
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
        return !!this.apiInfo;
      },
      title() {
        return 'API申请';
      },
      dataAccessApp() {
        return this.appList.find(item => item.dataAccessAppId === this.appId);
      },
      appName() {
        if (this.dataAccessApp) {
          return this.dataAccessApp.dataAccessAppName;
        }
        return '';
      },
    },
    watch: {
      async isShow(val) {
        this.resetFields();
        if (val) {
          this.setValues();
          if (this.apiInfo) {
            this.dataAccessAppId = this.appId;
            this.getApplyList();
          }
        }
      },
    },
    async created() {
      this.setApp();
    },
    methods: {
      async setApp() {
        this.appList = await myApp.getAppList();
      },
      getApplyList() {
        this.loading.saving = true;
        this.tabList = [];
        apply
          .getApplyDetail({
            dataAssetId: this.form.dataAssetApiId || null,
            dataAccessAppId: this.appId,
          })
          .done(res => {
            res.data.dataAccessRuleDetailList.forEach(item => {
              item.apply = false;
            });
            this.data = res.data;
            this.assetTable = res.data.dataAccessRuleDetailList;
            const operatorAppAccessRules = res.data.operatorAppAccessRules || {};
            for (const key in operatorAppAccessRules) {
              const config = operatorAppAccessRules[key];
              config.dataAccessRuleDetailList = (config.dataAccessRuleDetailList || []).map(item => {
                this.$set(item, 'apply', false);
                return item;
              });
              this.tabList.push({
                title: key,
                config,
              });
            }
          })
          .always(() => {
            this.loading.saving = false;
          });
      },
      onSearchEnter(e) {
        if (!e) e = window.event;
        if ((e.keyCode || e.which) === 13) {
          this.onSearchParams();
        }
      },
      onSearchParams() {
        this.keyword = this.name;
      },
      getCheckeds() {
        return this.assetTable.filter(item => item.apply);
      },
      resetFields() {
        const form = this.$refs.elForm;
        if (form && form.resetFields) {
          form.resetFields();
        }
        this.form.dataAccessAppName = '';
        this.form.dataAccessDescription = '';
        this.form.inCharge = [];
      },
      setValues() {
        if (!this.apiInfo) {
          return;
        }
        for (const key in this.form) {
          if (key === 'inCharge') {
            const accout = this.apiInfo[key];
            this.form.inCharge = [{ user: accout, id: accout }];
            continue;
          }
          this.$set(this.form, key, this.apiInfo[key]);
        }
      },
      onCancle() {
        this.isShow = false;
        this.assetTable = [];
      },
      onSave() {
        const form = this.$refs.elForm;
        form.validate(isVlidate => {
          if (!isVlidate) {
            return;
          }
          this.add();
        });
      },
      add() {
        const checkeds = this.getCheckeds();
        const params = [];
        const paramarObj = {};
        paramarObj.dataAccessAppId = this.appId;
        paramarObj.dataAssetApiId = this.apiInfo.dataAssetApiId;
        paramarObj.dataAccessRuleFieldList = [];

        checkeds.forEach(item => {
          paramarObj.dataAccessRuleFieldList.push(item.datasourceTableColumnName);
        });
        if (paramarObj.dataAccessRuleFieldList.length > 0) {
          params.push(paramarObj);
        }

        this.tabList.forEach(tab => {
          const cols = [];
          tab.config.dataAccessRuleDetailList.forEach(item => {
            if (item.apply && !item.authorize) {
              cols.push(item.datasourceTableColumnName);
            }
          });
          if (cols.length > 0) {
            params.push({
              dataAccessAppId: this.appId,
              dataAssetApiId: tab.config.apiId,
              dataAccessRuleFieldList: cols,
            });
          }
        });

        if (params.length === 0) {
          this.$message({
            message: '请选择要返回的参数',
            type: 'warning',
          });
          return;
        }

        // console.log(paramars);
        this.loading.saving = true;
        apply
          .applyApi(params)
          .done(() => {
            // console.log(res);
            this.$message({
              type: 'success',
              message: '申请成功',
            });
            this.isShow = false;
            this.$emit('saved');
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
      onAppChanged() {
        this.getApplyList();
      },
    },
  };
</script>

<style lang="less" scoped>
  /deep/.el-textarea__inner {
    min-height: 150px !important;
  }

  // /deep/.el-table {
  //   height: 300px;
  //   overflow: auto;
  // }
  .apply {
    line-height: 0 !important;
    /deep/.el-form-item {
      margin-bottom: 10px;
    }
    // /deep/.el-dialog{
    //    max-height:calc(100% - 100px);
    //    max-width:calc(100% - 100px)
    // }
    .main-title {
      margin: 20px 0 10px 0;
      font-weight: 600;
      // color: #000;
    }
    /deep/.el-dialog__body {
      padding: 5px 20px !important;
    }
  }
</style>
