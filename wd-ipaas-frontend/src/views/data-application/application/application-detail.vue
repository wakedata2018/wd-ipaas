<template>
  <el-dialog
    v-loading="loading"
    fullscreen
    custom-class="anim-left"
    lock-scroll
    append-to-body
    class="bd-dialog page-application-detail"
    title="应用详情"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <el-scrollbar ref="scrollbar" class="page-scrollbar">
      <div class="scrollbar-content">
        <div class="basic-info">
          <div class="main-title">基础信息</div>
          <div class="basic-info-content">
            <el-form :model="form">
              <el-row>
                <el-col :span="12"
                  ><el-form-item label="应用名称">{{ form.dataAccessAppName }}</el-form-item></el-col
                >
                <el-col :span="12"
                  ><el-form-item label="创建时间">{{ form.createTime }}</el-form-item></el-col
                >
              </el-row>
              <el-row>
                <el-col :span="12">
                  <el-form-item label="鉴权类型">{{ authTypeName }}</el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="发布状态">
                    {{ publishStatusName }}
                    <el-button
                      type="primary"
                      :disabled="!publishStatusVal"
                      class="publish-btn"
                      @click="updatePublishStatus(0)"
                    >
                      上线</el-button
                    >
                    <el-button type="primary" :disabled="publishStatusVal" @click="updatePublishStatus(1)"
                      >下线</el-button
                    >
                    <el-tooltip class="item" effect="dark" content="点击下线应用会失效，谨慎操作" placement="top-start">
                      <i class="el-icon-info tip"></i>
                    </el-tooltip> </el-form-item
                ></el-col>
              </el-row>
              <el-row>
                <el-col :span="12">
                  <el-form-item label="APP Key">{{ form.dataAccessKey }}</el-form-item></el-col
                >
                <el-col :span="12">
                  <el-form-item label="APP Secret"
                    >{{ form.dataAccessSecret }}
                    <el-button type="primary" class="reset-btn" @click="secretReset">重置</el-button>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="12"
                  ><el-form-item label="应用路径">{{ form.apiAccessPrefix }}</el-form-item></el-col
                >
                <el-col v-if="isShowConnectorAuth" :span="12"
                  ><el-form-item label="鉴权方式">{{ form.connectorAuthName }}</el-form-item></el-col
                >
              </el-row>
              <el-form-item label="授权惟客云应用">
                {{ form.authorizedAppName }}
              </el-form-item>
              <el-form-item label="权限包">
                已授权权限包<span class="num">（{{ form.authedApiCount }}）</span> | 未授权权限包<span class="num"
                  >（{{ form.unAuthApiCount }}）</span
                >
              </el-form-item>
            </el-form>
          </div>
        </div>
        <div class="api-auth">
          <div class="main-title">API授权</div>
          <el-table :data="apiAuthList" class="dss-table bd-table">
            <el-table-column prop="apiGroupName" label="API分类"></el-table-column>
            <el-table-column prop="dataAssetName" label="API名称"></el-table-column>
            <el-table-column prop="dataAssetDescription" label="API介绍"></el-table-column>
            <el-table-column prop="appAuthStatus" label="授权状态">
              <template #default="scope">
                <span :class="['un-auth', { auth: scope.row.appAuthStatus }]">{{
                  authStatus[scope.row.appAuthStatus]
                }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="320" align="center">
              <template #default="scope">
                <el-button type="text" class="bd-button bd-table-primary" @click="onDetail(scope.row)">详情</el-button>
                <el-button
                  v-if="!scope.row.appAuthStatus"
                  type="text"
                  class="bd-button bd-table-primary"
                  @click="updateAuth(scope.row, 1)"
                  >授权</el-button
                >
                <el-button
                  v-if="scope.row.appAuthStatus"
                  type="text"
                  class="bd-button bd-table-primary"
                  @click="updateAuth(scope.row, 0)"
                  >解除授权</el-button
                >
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="table-pagination">
          <el-pagination
            layout="total, sizes, prev, pager, next"
            :total="totalCount"
            :current-page="pageParams.pageNo"
            :page-size="pageParams.pageSize"
            @size-change="onSizeChange"
            @current-change="onPageChange"
          >
          </el-pagination>
        </div>
      </div>
    </el-scrollbar>
    <view-api :visible.sync="isShowApiDetail" :data-asset-api-id="dataAssetApiId"></view-api>
  </el-dialog>
</template>

<script>
  import ViewApi from '../../api-manage/api-publish/view-api';
  import applicationApi from '@/api/api-application.js';
  import { APPLICATION_PUBLISH_STATUS, APPLICATION_AUTHOR_STATUS } from '@/enum.js';
  import { AuthType, AuthTypeOptions } from '@/utils/enum/auth-list';

  export default {
    components: {
      ViewApi,
    },
    props: {
      id: {
        type: Number,
        default: null,
      },
      visible: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        publishStatusEnums: APPLICATION_PUBLISH_STATUS, // 发布状态
        authStatus: APPLICATION_AUTHOR_STATUS, // 授权状态
        form: {
          dataAccessAppName: null, // 应用名称
          dataAccessKey: null, // App key
          dataAccessSecret: null, // App Secret
          createTime: null, // 创建时间
          publishStatus: null, // 发布状态
          authedApiCount: null, // 已授权权限包数量
          unAuthApiCount: null, // 未授权权限包数量
          lesseeId: null, // 授权的应用
          apiAccessPrefix: null, // 应用访问路径
          connectorAuthName: null, // 鉴权方式名称
          authType: null, // 鉴权类型
        },
        apiAuthList: null,
        totalCount: 0,
        pageParams: {
          pageSize: 10,
          pageNo: 1,
        },
        AuthType,
        AuthTypeOptions,
        isShowApiDetail: false,
        dataAssetApiId: null,
      };
    },
    computed: {
      dialogVisible: {
        get() {
          return this.visible;
        },
        set(val) {
          this.$emit('update:visible', val);
        },
      },
      publishStatusName() {
        return this.publishStatusEnums[this.form.publishStatus]?.label ?? '';
      },
      publishStatusVal() {
        return !!this.publishStatusEnums[this.form.publishStatus]?.value ?? '';
      },
      authTypeName() {
        return AuthTypeOptions.find(item => item.value === this.form.authType)?.label;
      },
      isShowConnectorAuth() {
        return this.form.authType === AuthType.CONNECTOR_AUTH;
      },
    },
    watch: {
      id: {
        immediate: true,
        handler() {
          this.getInfo();
          this.getApiAuthList();
        },
      },
    },
    methods: {
      getInfo() {
        this.loading = true;
        applicationApi
          .getApplicaptionDetail({
            dataAccessAppId: this.id,
          })
          .then(res => {
            this.form = res.data;
            this.loading = false;
          });
      },
      getApiAuthList() {
        this.loading = true;
        applicationApi
          .getApiAuthList({
            appId: this.id,
            ...this.pageParams,
          })
          .then(res => {
            this.totalCount = res.totalCount;
            this.apiAuthList = res.data;
            this.loading = false;
          });
      },
      async updatePublishStatus(val) {
        try {
          const params = {
            dataAccessAppId: this.id,
            status: val,
          };
          const actionName = val ? '下线' : '上线';
          this.$confirm(`确定${actionName}当前应用吗?`, '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          })
            .then(async () => {
              await applicationApi.updateApplicaptionPublishStatus(params);
              this.$message({
                type: 'success',
                message: `${actionName}成功`,
              });
              this.getInfo();
            })
            .catch(res => {
              this.$message({
                type: 'info',
                message: '用户取消操作',
              });
            });
        } catch (err) {
          console.log(err);
        }
      },
      secretReset() {
        try {
          this.$confirm('密钥重置后原来的TOKEN需要作废，确定重置吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          })
            .then(async () => {
              await applicationApi.resetSecret({ dataAccessAppId: this.id });
              this.$message({
                type: 'success',
                message: '重置成功',
              });
              this.getInfo();
            })
            .catch(res => {
              this.$message({
                type: 'info',
                message: '用户取消重置操作',
              });
            });
        } catch (err) {
          console.log(err);
        }
      },
      onPageChange(pageNo) {
        this.pageParams.pageNo = pageNo;
        this.getApiAuthList();
      },
      onSizeChange(size) {
        this.pageParams.pageSize = size;
        this.pageParams.pageNo = 1;
        this.getApiAuthList();
      },
      // API文档详情
      onDetail(val) {
        this.dataAssetApiId = val.dataAssetApiId;
        this.isShowApiDetail = true;
      },

      async updateAuth(row, status) {
        const params = {
          appId: this.id,
          apiId: row.dataAssetApiId,
        };
        try {
          this.loading = true;
          await applicationApi.updateAuth(params, status);
          this.getApiAuthList();
          this.getInfo();
        } catch (err) {
          console.error(err);
        } finally {
          this.loading = false;
        }
      },
    },
  };
</script>
<style scoped lang="less">
  .page-application-detail {
    /deep/ .el-dialog__body {
      padding: 10px 20px;
    }

    .basic-info {
      .reset-btn,
      .publish-btn {
        margin-left: 10px;
      }

      .num {
        color: red;
      }

      .tip {
        font-size: 18px;
        vertical-align: middle;
        margin-left: 18px;
        color: gray;
      }

      .un-auth {
        color: red;
      }

      .auth {
        color: #2776fb;
      }
    }

    .basic-info,
    .api-auth {
      padding-top: 10px;
    }

    .main-title {
      height: 22px;
      font-size: 16px;
      margin: 10px 0;
      font-weight: 600;
      color: rgba(51, 51, 51, 1);
      line-height: 22px;
    }
  }
</style>
