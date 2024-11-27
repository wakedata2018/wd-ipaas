<template>
  <div>
    <el-dialog
      fullscreen
      custom-class="anim-left"
      lock-scroll
      append-to-body
      class="bd-dialog api-grant-dialog"
      title="API授权"
      :visible.sync="isShow"
      v-loading="loading"
      :close-on-click-modal="false"
    >
      <el-scrollbar ref="scrollbar" class="page-scrollbar">
        <div class="scrollbar-content">
          <el-form label-width="120px" style="margin-top: 8px">
            <el-row :gutter="20" class="assets-details-row">
              <el-col :span="12" v-if="apiInfo && (apiInfo.dataAssetName || apiInfo.apiSql)">
                <el-form-item label="资产名称：" show-overflow-tooltip>
                  <div class="name-title" :title="apiInfo ? apiInfo.dataAssetName || apiInfo.apiSql || '' : ''">
                    {{ apiInfo ? apiInfo.dataAssetName || apiInfo.apiSql || '' : '' }}
                  </div>
                  <el-button
                    class="copy-btn"
                    type="text"
                    icon="el-icon-document-copy"
                    style="vertical-align: middle"
                    v-clipboard:copy="apiInfo ? apiInfo.dataAssetName || apiInfo.apiSql || '' : ''"
                    v-clipboard:success="copy"
                  ></el-button>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="API Path：">/{{ apiInfo && apiInfo.dataAssetApiMethod }}</el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20" class="assets-details-row">
              <el-col :span="12">
                <el-form-item label="接入名称：">
                  <el-select placeholder="请选择" v-model="appId" style="max-width: 250px; width: 100%">
                    <el-option
                      v-for="app in appList"
                      :key="app.dataAccessAppId"
                      :value="app.dataAccessAppId"
                      :label="app.dataAccessAppName"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="接入端负责人：">
                  <el-input
                    disabled
                    maxlength="64"
                    :value="appInfo.inCharge"
                    readonly
                    style="max-width: 250px"
                  ></el-input>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
          <access-table :data="assetTable" />
          <div class="sub-title">授权外部应用</div>
          <el-button @click="appAuthVisible = true" type="primary" style="margin-bottom: 20px">授权应用</el-button>
          <third-app-table
            :app-auth-list="appAuthList"
            :app-auth-list-loading="appAuthListLoading"
            :api-info="apiInfo"
            @get-third-app-auth-api-list="getThirdAppAuthApiList"
          />
        </div>
      </el-scrollbar>
      <div class="bd-dialog-footer">
        <el-button @click="isShow = false" size="medium" class="cancel">取消</el-button>
        <el-button type="primary" @click="onGrant" size="medium" class="bd-button" :disabled="finalParams.length === 0"
          >授权</el-button
        >
      </div>
    </el-dialog>
    <appAuthDialog
      :visible.sync="appAuthVisible"
      :api-info="apiInfo"
      @get-third-app-auth-api-list="getThirdAppAuthApiList"
    />
  </div>
</template>

<script>
  import approvalAPI from '@/api/approval';
  import ruleAPI from '@/api/data-access-rule';
  import daaAPI from '@/api/data-access-app';
  import AccessTable from '@/bz-components/access-table.vue';
  import appAuthDialog from './component/app-auth-dialog.vue';
  import thirdAppTable from './component/third-app-table';
  import { mapState } from 'vuex';
  import { Message } from 'element-ui';

  export default {
    components: { AccessTable, appAuthDialog, thirdAppTable },
    props: {
      apiInfo: {
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
    data: () => ({
      loading: false,
      appList: [],
      appId: null,
      assetTable: [],
      grantStatus: false,
      appAuthVisible: false,
      appAuthList: [],
      appAuthListLoading: false,
    }),
    computed: {
      ...mapState({
        user: state => state.user,
      }),
      isShow: {
        get() {
          return this.visible;
        },
        set(val) {
          this.$emit('update:visible', val);
        },
      },
      appInfo() {
        return this.appList.find(app => app.dataAccessAppId === this.appId) || {};
      },
      finalParams() {
        const checkeds = this.getCheckeds();
        const params = {
          dataAccessAppId: this.appId,
          dataAssetApiId: this.apiInfo.dataAssetApiId,
          dataAccessRuleFieldList: checkeds.map(col => col.datasourceTableColumnName),
        };
        const finalParams = [...(checkeds.length > 0 ? [params] : [])];
        return finalParams;
      },
    },
    watch: {
      isShow(val) {
        if (val) {
          this.init();
        } else {
          this.appList = [];
          this.appId = null;
          this.assetTable = [];
        }
      },
      appId(val) {
        if (val) {
          this.getAssetTable();
        }
      },
    },
    methods: {
      init() {
        this.getAppList();
        this.getAssetTable();
      },
      getAssetTable() {
        if (!this.appId || !this.apiInfo || !this.apiInfo.dataAssetApiId) {
          return;
        }
        this.loading = true;
        ruleAPI
          .getByAppAndAsset(this.appId, this.apiInfo.dataAssetApiId)
          .done(res => {
            if (!res.data) {
              return;
            }
            this.assetTable = this.addApplyProp(res.data.dataAccessRuleDetailList);
            if (this.assetTable.length <= 0) {
              this.grantStatus = true;
            } else {
              const isAuth = this.assetTable.find(item => {
                return item.authorize === false;
              });
              if (!isAuth) {
                this.grantStatus = true;
              } else {
                this.grantStatus = false;
              }
            }
          })
          .always(() => {
            this.loading = false;
          });
      },
      getAppList() {
        this.loading = true;
        daaAPI
          .listWithDefaultApp({
            status: 'PASS',
            apiId: this.apiInfo.dataAssetApiId,
          })
          .done(res => {
            const { appAccessVoList, defaultAppIdList } = res.data;
            this.appList = appAccessVoList || [];
            this.defaultAppIdList = defaultAppIdList || [];
            if (this.defaultAppIdList.length) this.appId = this.defaultAppIdList[0];
            else if (this.appList.length) this.appId = this.appList[0].dataAccessAppId;
            this.getThirdAppAuthApiList();
          })
          .always(() => {
            this.loading = false;
          });
      },
      getThirdAppAuthApiList() {
        this.appAuthListLoading = true;
        daaAPI
          .getThirdAuthInfoOfApi({
            apiId: this.apiInfo.dataAssetApiId,
          })
          .then(res => {
            this.appAuthList = res.data;
          })
          .finally(() => {
            this.appAuthListLoading = false;
          });
      },
      addApplyProp(assetList) {
        const list = assetList || [];
        if (!list || list.length === 0) {
          return list;
        }
        list.forEach(col => {
          col.apply = col.authorize;
        });
        return list;
      },
      getCheckeds() {
        return this.assetTable.filter(item => !item.authorize && item.apply);
      },
      onGrant() {
        if (this.finalParams.length === 0) {
          this.$message({
            type: 'error',
            message: '请选择要授权的数据',
          });
          return;
        }
        this.loading = true;
        approvalAPI
          .authorize(this.finalParams, this.appInfo.inCharge)
          .done(() => {
            this.$message({
              type: 'success',
              message: '授权成功',
            });
            // this.isShow = false;
            // this.init();
            // this.$emit('granted');
          })
          .always(() => {
            this.loading = false;
          });
      },
      copy(e) {
        if (e.text) {
          Message.closeAll();
          this.$message({
            message: '复制成功！',
            type: 'success',
          });
        }
      },
    },
  };
</script>

<style scoped lang="less">
  .api-grant-dialog {
    .page-scrollbar {
      overflow-x: hidden;
      overflow-y: hidden;
      margin-bottom: 10px;
      /deep/ .el-scrollbar__wrap {
        overflow-x: hidden;
        max-height: calc(100vh - 210px);
      }
      .scrollbar-content {
        padding: 10px;
        margin-bottom: 30px;
      }
      // .new-task-form {
      //   padding: 0 10px 17px 0;
      // }
    }

    .main-title {
      height: 22px;
      font-size: 16px;
      margin: 40px 20px;
      font-weight: 600;
      color: rgba(51, 51, 51, 1);
      line-height: 22px;
    }
    /deep/ .el-input,
    /deep/ .el-textarea {
      max-width: 400px;
    }
  }
  .grid-content {
    line-height: 28px;

    .label-text {
      display: inline-block;
      width: 90px;
      text-align: right;
      padding-right: 2px;
      box-sizing: border-box;
      color: #606266;
    }
  }
  .assets-details-bar {
    width: 100%;
    margin-bottom: 20px;
    // border: 1px solid #ebeef5;
    box-sizing: border-box;

    .assets-details-row {
      text-align: left;
    }
  }
  .name-title {
    overflow: hidden;
    max-width: calc(100% - 20px);
    display: inline-block;
    text-overflow: ellipsis;
    white-space: nowrap;
    vertical-align: middle;
  }
  .sub-title {
    font-weight: 800;
    font-size: 16px;
    margin: 20px 0 20px 0;
    color: #000;
  }
</style>
