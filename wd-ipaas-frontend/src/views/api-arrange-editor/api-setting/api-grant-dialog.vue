<template>
  <div>
    <el-dialog
      fullscreen
      custom-class="anim-left"
      lock-scroll
      append-to-body
      class="bd-dialog api-grant-dialog"
      title="API授权"
      v-loading="loading"
      :visible.sync="isShow"
    >
      <el-scrollbar ref="scrollbar" class="page-scrollbar">
        <div class="scrollbar-content">
          <el-form label-width="120px" style="margin-top: 8px">
            <el-row :gutter="20" class="assets-details-row">
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
          <div class="sub-title">节点授权</div>
          <el-tabs v-model="activeName">
            <el-tab-pane
              v-for="authNode in authNodeList"
              :key="authNode"
              :label="authNode"
              :name="authNode"
            ></el-tab-pane>
          </el-tabs>
          <access-table :data="currentAuthObj.dataAccessRuleDetailList" />
          <div class="sub-title">授权外部应用</div>
          <third-app-table
            :app-auth-visible.sync="appAuthVisible"
            :app-auth-list="appAuthList"
            :operator-id.sync="operatorId"
            :app-auth-list-loading="appAuthListLoading"
            @get-third-app-auth-api-list="getThirdAppAuthApiList"
          />
        </div>
      </el-scrollbar>
      <div class="bd-dialog-footer">
        <el-button @click="isShow = false" size="medium" class="cancel">取消</el-button>
        <el-button type="primary" @click="onGrant" :disabled="finalParams.length === 0" size="medium" class="bd-button"
          >授权</el-button
        >
      </div>
    </el-dialog>
    <appAuthDialog
      :visible.sync="appAuthVisible"
      :api-info="apiInfo"
      :operator-id="operatorId"
      @get-third-app-auth-api-list="getThirdAppAuthApiList"
    />
  </div>
</template>

<script>
  import approvalAPI from '@/api/approval';
  import ruleAPI from '@/api/data-access-rule';
  import daaAPI from '@/api/data-access-app';
  import AccessTable from '@/bz-components/access-table.vue';
  import ThirdAppTable from './third-app-table';
  import appAuthDialog from '../../api-manage/api-publish/component/app-auth-dialog';
  import { mapState } from 'vuex';
  import { Message } from 'element-ui';

  export default {
    components: { AccessTable, ThirdAppTable, appAuthDialog },
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
      operatorAppAccessRules: {},
      grantStatus: false,
      activeName: '',
      appAuthVisible: false,
      appAuthList: [],
      operatorId: -1,
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
      currentAuthObj() {
        const obj = this.operatorAppAccessRules[this.activeName];
        return obj || { appId: null, dataAccessRuleDetailList: [] };
      },
      authNodeList() {
        return Object.keys(this.operatorAppAccessRules);
      },
      finalParams() {
        const checkeds = this.getCheckeds();
        const params = {
          dataAccessAppId: this.appId,
          dataAssetApiId: this.apiInfo.dataAssetApiId,
          dataAccessRuleFieldList: checkeds.map(col => col.datasourceTableColumnName),
        };
        const nodeParams = [];
        this.authNodeList.forEach(item => {
          const obj = this.operatorAppAccessRules[item];
          if (obj) {
            const selectedColumn = obj.dataAccessRuleDetailList
              .filter(filterItem => filterItem.apply && !filterItem.authorize)
              .map(mapItem => mapItem.datasourceTableColumnName);
            if (selectedColumn.length > 0) {
              nodeParams.push({
                dataAccessAppId: this.appId,
                dataAssetApiId: obj.apiId,
                dataAccessRuleFieldList: selectedColumn,
              });
            }
          }
        });

        const finalParams = [...(checkeds.length > 0 ? [params] : []), ...nodeParams];
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
          this.operatorAppAccessRules = {};
          this.activeName = '';
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
            const finalOperatorRules = {};
            const operatorAppAccessRules = res.data.operatorAppAccessRules;
            if (operatorAppAccessRules) {
              const keys = Object.keys(operatorAppAccessRules);
              keys.forEach(key => {
                const item = operatorAppAccessRules[key];
                finalOperatorRules[key] = {
                  ...item,
                  dataAccessRuleDetailList: this.addApplyProp(item.dataAccessRuleDetailList),
                };
              });
              this.activeName = keys[0] ? keys[0] : '';
              this.operatorAppAccessRules = finalOperatorRules;
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
          .getThirdAuthInfoOfApiOperator({
            dataAccessAppId: this.appId,
            dataAssetId: this.apiInfo.dataAssetApiId,
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
            this.isShow = false;
            this.init();
            this.$emit('granted');
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
      margin: 10px 40px;
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
    margin: 20px 0 0;
    color: #000;
  }
  /deep/.el-tabs__nav-wrap::after {
    height: 0px;
  }
</style>
