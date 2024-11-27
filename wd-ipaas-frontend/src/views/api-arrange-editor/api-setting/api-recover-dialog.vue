<template>
  <el-dialog
    fullscreen
    custom-class="anim-left"
    lock-scroll
    append-to-body
    class="bd-dialog api-grant-dialog"
    title="API权限回收"
    :visible.sync="isShow"
    v-loading="loading"
    width="100%"
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
              <el-form-item label="接入端名称：">
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
        <access-table :data="assetTable" :reverse="true" />
        <div class="sub-title">节点权限回收</div>
        <el-tabs v-model="activeName">
          <el-tab-pane
            v-for="authNode in authNodeList"
            :key="authNode"
            :label="authNode"
            :name="authNode"
          ></el-tab-pane>
        </el-tabs>
        <access-table :data="currentAuthObj.dataAccessRuleDetailList" :reverse="true" />
      </div>
    </el-scrollbar>
    <div class="bd-dialog-footer" slot="footer">
      <el-button size="medium" @click="isShow = false">取消</el-button>
      <el-button size="medium" type="primary" class="bd-button" :disabled="finalParams.length === 0" @click="onRecover"
        >权限回收</el-button
      >
    </div>
  </el-dialog>
</template>

<script>
  import approvalAPI from '@/api/approval.js';
  import ruleAPI from '@/api/data-access-rule.js';
  import daaAPI from '@/api/data-access-app.js';
  import AccessTable from '@/bz-components/access-table.vue';
  import { mapState } from 'vuex';
  import { Message } from 'element-ui';

  export default {
    components: { AccessTable },
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
    data() {
      return {
        loading: false,
        appList: [],
        appId: '',
        assetTable: [],
        operatorAppAccessRules: {},
        activeName: '',
      };
    },
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
              .filter(i => i.apply && i.authorize)
              .map(i => i.datasourceTableColumnName);
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
          .listPermitted({
            apiId: this.apiInfo.dataAssetApiId,
            // status: "PASS",
          })
          .done(res => {
            this.appList = res.data || [];
            if (this.appList.length > 0) {
              this.appId = this.appList[0].dataAccessAppId;
            }
          })
          .always(() => {
            this.loading = false;
          });
      },
      addApplyProp(assetList) {
        const list = assetList || [];
        if (!list || list.length === 0) {
          return list;
        }
        list.forEach(col => {
          col.apply = false;
        });
        return list;
      },
      getCheckeds() {
        return this.assetTable.filter(item => item.authorize && item.apply);
      },
      onRecover() {
        if (this.finalParams.length === 0) {
          this.$message({
            type: 'error',
            message: '请选择要回收的权限数据',
          });
          return;
        }
        this.loading = true;
        approvalAPI
          .revoke(this.finalParams, this.user.loginName)
          .done(() => {
            this.$message({
              type: 'success',
              message: '回收权限成功',
            });
            this.isShow = false;
            this.$emit('recovered');
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

<style lang="less" scoped>
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
