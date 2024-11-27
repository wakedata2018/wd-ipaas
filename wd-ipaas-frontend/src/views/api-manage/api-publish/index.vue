<template>
  <div class="page-api-publish">
    <div class="bd-page sidebar-visible">
      <div class="bd-header">
        <div class="title">{{ title }}</div>
        <div class="header-right">
          <WKAuthButton code="add" type="primary" @click="onNew()">新增API</WKAuthButton>
          <WKAuthButton code="arrange" type="primary" @click="onArrange">新增服务编排</WKAuthButton>
          <WKAuthButton code="api-import-export" type="primary" @click="onImpOrExp">导入/导出</WKAuthButton>
        </div>
      </div>
      <div class="bd-container">
        <theme-select
          ref="themeSelect"
          v-model="filteredThemeGroup"
          v-loading="apiGroupList.loading"
          class="left-container"
          :list="apiGroupList.list"
          :expanded-keys="defaultExpandedKeys"
          @input="setThemeGroup"
        ></theme-select>
        <div class="right-container">
          <el-scrollbar style="height: 100%">
            <div class="bd-search split condition">
              <el-form
                ref="apiForm"
                size="mini"
                label-position="right"
                class="bd-form"
                label-width="100px"
                :model="query"
                inline
              >
                <el-form-item label="API类型" prop="apiType">
                  <el-radio-group v-model="query.apiType" @change="onSearch">
                    <el-radio-button v-for="item in apiTypeOption" :key="item.value" :label="item.value">{{
                      item.label
                    }}</el-radio-button>
                  </el-radio-group>
                </el-form-item>
                <br />
                <el-form-item label="API名称" prop="apiName">
                  <el-input
                    v-model.trim="query.apiName"
                    class="w200"
                    type="text"
                    maxlength="64"
                    placeholder="请输入API名称"
                  />
                </el-form-item>
                <el-form-item v-if="authorize(permitList, 'add')" label="发布时间" prop="dateRange">
                  <el-date-picker
                    v-model="query.dateRange"
                    class="w330"
                    type="datetimerange"
                    start-placeholder="开始时间"
                    end-placeholder="结束时间"
                    range-separator="至"
                    value-format="yyyy-MM-dd HH:mm:ss"
                    placeholder="请选择时间范围"
                    placement="bottom-start"
                  ></el-date-picker>
                </el-form-item>
                <el-form-item v-if="authorize(permitList, 'add')" label="发布状态" prop="dataAssetPublishStatus">
                  <el-select v-model="query.dataAssetPublishStatus" class="w200" placeholder="请选择发布状态">
                    <el-option v-for="item in statusArray" :key="item.value" :label="item.label" :value="item.key">
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="API PATH" prop="dataAssetApiMethod">
                  <el-input
                    v-model.trim="query.dataAssetApiMethod"
                    class="w200"
                    type="text"
                    maxlength="100"
                    placeholder="请输入API PATH"
                  />
                </el-form-item>
                <el-form-item label="是否公开" prop="secret">
                  <el-select v-model="query.secret" class="w200" placeholder="请选择是否公开" clearable>
                    <el-option
                      v-for="item in QuerySecretOptions"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    >
                    </el-option>
                  </el-select>
                </el-form-item>
                <!-- <el-form-item label="数据来源" prop="source">
                  <el-input
                    class="w200"
                    type="text"
                    maxlength="64"
                    placeholder="请输入数据来源"
                    v-model.trim="query.source"
                  />
                </el-form-item> -->
                <el-form-item v-if="authorize(permitList, 'application')" label="申请状态" prop="apiApplyStatus">
                  <el-select v-model="query.apiApplyStatus" class="w200">
                    <el-option v-for="item in ApiApplyStatus" :key="item.value" :label="item.label" :value="item.value">
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-form>
              <div class="bd-search-group">
                <el-button type="primary" class="bd-button bd-strong" @click="onSearch">查询</el-button>
                <el-button type="primary" plain size="mini" class="bd-button bd-strong" @click="onReset"
                  >重置
                </el-button>
              </div>
            </div>
            <div class="publish-table">
              <div class="bd-actions">
                <WKAuthButton code="release" type="primary" :disabled="!offlineList.length" @click="handlePublish">
                  批量上线
                </WKAuthButton>
                <WKAuthButton
                  code="offline"
                  type="danger"
                  plain
                  :disabled="!onlineList.length"
                  @click="handleUnpublish"
                >
                  批量下线
                </WKAuthButton>
                <!-- <WKAuthButton code="delete" type="danger" plain @click="handleDelete" :disabled="!selected.length">
                  批量删除
                </WKAuthButton> -->
              </div>
              <el-table
                v-loading="loading.apiList"
                :data="apiList"
                style="width: 100%"
                class="dss-table bd-table"
                @selection-change="handleSelectionChange"
              >
                <el-table-column type="selection" width="50" />
                <el-table-column prop="apiName" label="API名称" min-width="220" show-overflow-tooltip>
                  <template #default="scope">
                    <el-button type="text" :title="scope.row.apiName" class="name ellipsis" @click="onView(scope.row)">
                      {{ scope.row.apiName }}
                    </el-button>
                  </template>
                </el-table-column>
                <el-table-column label="API类型" min-width="120" show-overflow-tooltip>
                  <template #default="scope">
                    {{ APIType[scope.row.apiType] }}
                  </template>
                </el-table-column>
                <el-table-column prop="apiGroupName" label="接口分类" min-width="120"> </el-table-column>
                <el-table-column prop="dataAssetApiMethod" label="API Path" min-width="120" show-overflow-tooltip>
                  <template #default="scope">{{ scope.row.dataAssetApiMethod }}</template>
                </el-table-column>
                <el-table-column prop="secret" label="是否公开">
                  <template #default="scope">{{ SecretStatus[scope.row.secret] }}</template>
                </el-table-column>
                <el-table-column
                  v-if="authorize(permitList, 'add')"
                  prop="dataAssetPublishStatus"
                  label="发布状态"
                  width="80"
                  align="center"
                >
                  <template #default="scope">
                    <div
                      :style="{
                        color: APIStatus.toColor(scope.row.dataAssetPublishStatus),
                      }"
                    >
                      {{ APIStatus.getDesc(scope.row.dataAssetPublishStatus) }}
                    </div>
                  </template>
                </el-table-column>
                <el-table-column v-if="authorize(permitList, 'add')" prop="publishTime" label="发布时间" width="160">
                  <template #default="scope">{{
                    isPublish(scope.row.dataAssetPublishStatus) ? scope.row.publishTime : ''
                  }}</template>
                </el-table-column>
                <el-table-column v-if="authorize(permitList, 'application')" label="申请状态" show-overflow-tooltip>
                  <template #default="scope">{{ ApplicationStatus[scope.row.applyStatus] }}</template>
                </el-table-column>
                <el-table-column label="操作" :min-width="operationWidth" header-align="center">
                  <template #default="scope">
                    <div class="operation">
                      <!-- <WKAuthButton code="check" class="bd-button bd-table-primary" @click="onView(scope.row)">
                        查看</WKAuthButton
                      > -->

                      <WKAuthButton
                        v-if="!isPublish(scope.row.dataAssetPublishStatus)"
                        code="edit"
                        class="bd-button bd-table-primary"
                        @click="onEdit(scope.row)"
                      >
                        <el-tooltip class="item" effect="dark" content="编辑">
                          <i class="el-icon-edit"></i>
                        </el-tooltip>
                      </WKAuthButton>

                      <WKAuthButton
                        code="copy"
                        class="bd-button bd-table-primary"
                        @click="onEdit(scope.row, OPERATION_TYPE.COPY)"
                      >
                        <el-tooltip class="item" effect="dark" content="复制">
                          <i class="el-icon-document-copy"></i>
                        </el-tooltip>
                      </WKAuthButton>

                      <!-- <WKAuthButton code="auth" @click="onGrant(scope.row)" class="bd-button bd-table-primary"
                        v-if="isPublish(scope.row.dataAssetPublishStatus)">授权</WKAuthButton> -->
                      <WKAuthButton
                        v-if="
                          scope.row.applyStatus === ApplicationEnum.unapply ||
                          scope.row.applyStatus === ApplicationEnum.failureApply
                        "
                        code="application"
                        class="bd-button bd-table-primary"
                        @click="application(scope.row)"
                      >
                        <el-tooltip class="item" effect="dark" content="申请">
                          <i class="el-icon-s-order"></i> </el-tooltip
                      ></WKAuthButton>
                      <WKAuthButton code="test" class="bd-button bd-table-primary" @click="onTest(scope.row)"
                        ><el-tooltip class="item" effect="dark" content="测试">
                          <i class="el-icon-s-claim"></i>
                        </el-tooltip>
                      </WKAuthButton>
                      <!-- <el-button @click="onRecover(scope.row)" class="bd-button bd-table-primary"
                        v-if="isPublish(scope.row.dataAssetPublishStatus)">回收</el-button> -->

                      <WKAuthButton
                        v-if="!isPublish(scope.row.dataAssetPublishStatus)"
                        code="online"
                        class="bd-button bd-table-primary"
                        @click="onPublish(scope.row)"
                        ><el-tooltip class="item" effect="dark" content="上线">
                          <i class="el-icon-top"></i>
                        </el-tooltip>
                      </WKAuthButton>
                      <WKAuthButton
                        v-if="isPublish(scope.row.dataAssetPublishStatus)"
                        code="offline"
                        class="bd-button bd-table-primary"
                        @click="onUnpublish(scope.row)"
                        ><el-tooltip class="item" effect="dark" content="下线">
                          <i class="el-icon-bottom"></i>
                        </el-tooltip>
                      </WKAuthButton>
                      <WKAuthButton
                        v-if="!isPublish(scope.row.dataAssetPublishStatus)"
                        code="delete"
                        class="bd-button bd-table-danger"
                        @click="onDelete(scope.row)"
                        ><el-tooltip class="item" effect="dark" content="删除">
                          <i class="el-icon-delete"></i> </el-tooltip
                      ></WKAuthButton>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
              <div style="text-align: right; margin-top: 20px">
                <el-pagination
                  layout="total, sizes, prev, pager, next"
                  :total="totalCount"
                  :current-page="page.pageNo"
                  :page-size="page.pageSize"
                  @size-change="onSizeChange"
                  @current-change="onCurrentChange"
                >
                </el-pagination>
              </div>
            </div>
          </el-scrollbar>
        </div>
      </div>
    </div>
    <!-- 查看按钮弹窗 -->
    <view-api ref="dialog" :data-asset-api-id="dataAssetApiId" :visible.sync="dialog.view" />
    <!-- 编辑按钮弹窗 -->
    <api-edit
      ref="dialogE"
      :api-info="apiInfo"
      :operation-type="operationType"
      :visible.sync="dialog.detail"
      :group-id-info="filteredThemeGroup"
      :tree-data="apiGroupList.list"
      @saved="onEditSuccess"
    />
    <!-- 测试按钮弹窗 -->
    <api-test :api-info="apiInfo" :visible.sync="dialog.apitest" />

    <api-grant-dialog ref="dialogGrant" :visible.sync="dialog.grant" :api-info="apiInfo" @granted="onGranted" />
    <api-recover-dialog :visible.sync="dialog.recover" :api-info="apiInfo" @recovered="onRecovered" />
    <api-arrange-grant-dialog
      ref="dialogArrangeGrant"
      :visible.sync="dialog.arrangeGrant"
      :api-info="apiInfo"
      @granted="onGranted"
    />
    <api-arrange-recover-dialog :visible.sync="dialog.arrangeRecover" :api-info="apiInfo" @recovered="onRecovered" />
    <ChoreographerDialog
      :visible.sync="dialog.arrange"
      :group-id-info="filteredThemeGroup"
      @enter-choreography="enterChoreography"
    ></ChoreographerDialog>
    <api-import-export-dialog
      v-if="dialog.apiImportExport"
      :visible.sync="dialog.apiImportExport"
      @saved="onEditSuccess"
    />
  </div>
</template>

<script>
  import { mapState } from 'vuex';
  import { Message } from 'element-ui';

  import ApiArrangeGrantDialog from '../../api-arrange-editor/api-setting/api-grant-dialog.vue';
  import ApiArrangeRecoverDialog from '../../api-arrange-editor/api-setting/api-recover-dialog.vue';
  import { APIStatus } from '@/enum.js';
  import { ApiType } from '@/utils/enum';
  import IPagesize from '../../../mixins/i-pagesize.js';
  import apiController from '@/api/api-controller.js';
  import dataAsset from '@/api/data-asset-api.js';
  import apiGroup from '@/api/api-group.js';
  import apiApproval from '@/api/approval.js';
  import dataSource from '@/api/data-source.js';
  import WKAuthButton from '@/components/wk-auth-button/index.vue';
  import { authorize } from '@/utils/authorize';
  import pageUtils from '@/utils/page.js';
  import {
    API_TYPE,
    ApiType as APIType,
    ApplicationStatus,
    ApiApplyStatus,
    ApplicationEnum,
    SecretStatus,
    QuerySecretOptions,
  } from '@/utils/enum/api-manage.js';
  import ApiGrantDialog from './api-grant-dialog.vue';
  import ApiRecoverDialog from './api-recover-dialog.vue';
  import ChoreographerDialog from './component/choreographer-dialog';
  import ApiImportExportDialog from './component/api-import-export-dialog.vue';
  import ThemeSelect from './theme-select.vue';
  import ApiEdit from './api-edit.vue';
  import ApiTest from './api-test/index.vue';
  import ViewApi from './view-api/index.vue';
  import { OPERATION_TYPE } from './types';

  export default {
    components: {
      ApiGrantDialog,
      ApiRecoverDialog,
      ApiArrangeGrantDialog,
      ApiArrangeRecoverDialog,
      ChoreographerDialog,
      ApiEdit,
      ApiTest,
      ViewApi,
      ThemeSelect,
      WKAuthButton,
      ApiImportExportDialog,
    },
    mixins: [IPagesize],
    data() {
      return {
        OPERATION_TYPE,
        APIStatus,
        applicationLoading: false,
        apiTypeOption: API_TYPE,
        APIType,
        ApplicationStatus,
        ApplicationEnum,
        ApiApplyStatus,
        authorize,
        apiList: [],
        loading: {
          apiList: false,
        },
        query: {
          dateRange: [],
          apiName: '',
          dataAssetPublishStatus: -1,
          apiType: -1, // API类型
          dataSourceId: null,
          apiApplyStatus: -1,
          dataAssetApiMethod: '',
          source: '',
        },
        totalCount: 0,
        page: {
          pageNo: 1,
          pageSize: 10,
        },
        apiInfo: {},
        dialog: {
          view: false,
          grant: false,
          recover: false,
          detail: false,
          apitest: false,
          arrangeRecover: false,
          arrangeGrant: false,
          arrange: false,
          apiImportExport: false,
        },
        statusArray: [
          { label: '全部', value: '', key: -1 },
          { label: '已发布', value: 'PUBLISH', key: 1 },
          { label: '未发布', value: 'UN_PUBLISH', key: '0' },
        ],
        datasourceArray: [],
        apiGroupList: {
          list: [],
          loading: false,
        },
        filteredThemeGroup: null,
        ApiType,
        defaultExpandedKeys: 0,
        selected: [],
        dataAssetApiId: null,
        operationType: OPERATION_TYPE.VIEW,
        SecretStatus,
        QuerySecretOptions,
      };
    },
    computed: {
      ...mapState({
        permitList: 'permitList',
      }),
      title() {
        return pageUtils.findPageName(this.permitList, this.$route.name);
      },
      offlineList() {
        return this.selected.filter(i => !this.isPublish(i.dataAssetPublishStatus));
      },
      onlineList() {
        return this.selected.filter(i => this.isPublish(i.dataAssetPublishStatus));
      },
      operationWidth() {
        let _width = 80;
        if (Array.isArray(this.apiList) && this.apiList.length) {
          this.apiList.forEach(item => {
            if (!this.isPublish(item.dataAssetPublishStatus)) {
              _width = 130;
            }
          });
        }
        return _width;
      },
    },
    async created() {
      // 获取缓存信息
      if (Object.keys(this.$route.params).length) {
        this.init();
      }
      this.getDataSourceList();
      await this.getApiGroupList();
      this.getApiList();
    },
    methods: {
      init() {
        const { params } = this.$route;
        this.page = params.pageParams;
        this.filteredThemeGroup = params.theme;
        this.query = params.filterForm;
      },
      getDataSourceList() {
        dataSource.list().done(res => {
          this.datasourceArray = res.data;
        });
      },

      handleSelectionChange(val) {
        this.selected = val;
      },
      handlePublish() {
        const params = this.offlineList.map(i => i.dataAssetApiId);
        this.confirmTpl({
          title: `确定批量上线选中接口吗？`,
          successTip: `上线成功`,
          method: 'apiBatchPublish',
          params,
          callback() {
            this.getApiList();
          },
        });
      },
      handleUnpublish() {
        const params = this.onlineList.map(i => i.dataAssetApiId);
        this.confirmTpl({
          title: `确定批量下线选中接口吗？`,
          successTip: `下线成功`,
          method: 'apiBatchUnPublish',
          params,
          callback() {
            this.getApiList();
          },
        });
      },
      handleDelete() {
        const params = this.selected.filter(i => !this.isPublish(i.dataAssetPublishStatus)).map(i => i.dataAssetApiId);
        if (params.length !== this.selected.length) {
          this.$message({
            type: 'error',
            message: '删除失败，存在未下线的API，请先下线API之后执行删除',
          });
          return;
        }

        this.confirmTpl({
          title: `当前选中 ${this.selected.length} 行数据，删除之后将无法恢复，确定都删除吗？`,
          successTip: `删除成功`,
          method: 'apiBatchDelete',
          params,
          callback() {
            this.getApiList();
          },
        });
      },
      setThemeGroup(val) {
        this.filteredThemeGroup = val;
        this.page = {
          pageNo: 1,
          pageSize: 10,
        };
        this.getApiList();
      },
      getApiList() {
        this.loading.apiList = true;

        // 过滤空值
        const queryParams = Object.entries(this.query).reduce((pre, [key, value]) => {
          if (key !== 'dateRange') {
            return { ...pre, ...(value !== '' && { [key]: value }) };
          } else if (Array.isArray(value) && value.length) {
            return { ...pre, startPublishTime: value[0], endPublishTime: value[1] };
          }
          return pre;
        }, {});

        const params = {
          ...this.page,
          ...queryParams,
          apiGroupId: this.filteredThemeGroup || this.$route.query.apiGroupId,
        };
        // 平台管理 请求接口需要移除 申请状态 默认值
        if (this.authorize(this.permitList, 'add')) {
          delete params.apiApplyStatus;
        }
        apiController
          .queryList(params)
          .done(res => {
            this.apiList = res.data;
            this.totalCount = res.totalCount;
          })
          .always(() => {
            this.loading.apiList = false;
          });
      },
      onArrange() {
        this.dialog.arrange = true;
      },
      enterChoreography(id) {
        this.$router.push({
          path: '/api-arrange-editor',
          query: { id },
        });
      },
      isPublish(status) {
        return APIStatus.getPublish().value === status;
      },
      onPublish(asset) {
        this.confirmTpl({
          title: `确定要上线接口吗？`,
          successTip: `发布成功`,
          method: 'apiPublish',
          params: {
            dataAssetApiId: asset.dataAssetApiId,
          },
          callback(res) {
            this.getApiList();
          },
        });
      },

      onGrant(asset) {
        this.apiInfo = asset;
        if (asset.apiType === ApiType.LITE_FLOW) {
          this.dialog.arrangeGrant = true;
        } else {
          this.dialog.grant = true;
        }
      },
      application(row) {
        if (!this.applicationLoading) {
          this.applicationLoading = true;
          apiApproval
            .applicationApi({ dataAssetApiId: row.dataAssetApiId })
            .then(({ success, data }) => {
              if (success && !!data) {
                this.$message.success('申请api成功');
                this.getApiList();
              }
            })
            .always(() => {
              this.applicationLoading = false;
            });
        }
      },
      onGranted() {
        this.dialog.grant = false;
        this.getApiList();
      },
      onRecover(asset) {
        this.apiInfo = asset;
        if (asset.apiType === ApiType.LITE_FLOW) {
          this.dialog.arrangeRecover = true;
        } else {
          this.dialog.recover = true;
        }
      },
      onRecovered() {
        this.dialog.recover = false;
        this.getApiList();
      },
      onUnpublish(asset) {
        this.$confirm('确定要下线接口吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
          .then(() => {
            this.unpublish(asset);
          })
          .catch(res => {
            // console.log('用户取消操作', res);
          });
      },
      unpublish(asset) {
        this.loading.apiList = true;
        dataAsset
          .unpublish({ dataAssetApi: asset.dataAssetApiId })
          .done(res => {
            this.getApiList();
          })
          .always(() => {
            this.loading.apiList = false;
          });
      },
      onView(info) {
        this.dataAssetApiId = info.dataAssetApiId;
        this.dialog.view = true;
        this.$refs.dialog.show();
      },

      /**
       * 编辑与复制处理方法
       * @param 表单信息 info
       * @param 操作类型 复制/编辑
       */
      onEdit(info, operation = OPERATION_TYPE.EDIT) {
        this.operationType = operation;
        // 服务编排
        if (info.apiType === ApiType.LITE_FLOW) {
          // 缓存列表页查询信息
          this.$router.push({
            name: 'apiArrange',
            query: { id: info.dataAssetApiId, operation },
            params: { theme: this.filteredThemeGroup, filterForm: this.query, pageParams: this.page },
          });
        } else {
          this.apiInfo = info;
          this.dialog.detail = true;
        }
      },

      onEditSuccess() {
        this.getApiList();
      },
      onNew() {
        this.apiInfo = {};
        this.operationType = OPERATION_TYPE.CREATE;
        this.dialog.detail = true;
      },
      onTest(info) {
        this.apiInfo = info;
        this.dialog.apitest = true;
      },
      onDelete(appInfo) {
        this.$confirm('确定删除该接口吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          // center: true
        })
          .then(() => {
            this.delete(appInfo);
          })
          .catch(res => {
            // console.log('用户取消操作', res);
          });
      },
      delete(appInfo) {
        this.loading.apiList = true;
        dataAsset
          .delete({ dataAssetApiId: appInfo.dataAssetApiId })
          .done(res => {
            this.getApiList();
            this.$message.success('删除成功');
          })
          .always(() => {
            this.loading.apiList = false;
          });
      },
      onSearch() {
        this.page.pageNo = 1;
        this.page.pageSize = 10;
        this.getApiList();
      },
      onReset() {
        this.$refs.themeSelect?.$refs.vuetree?.setCurrentKey(-1);
        this.filteredThemeGroup = null;
        this.resetFormFields(); // 这里不能使用element-ui里面form的resetFields，因为此处这个页面做了路由缓存
        this.onSearch();
      },
      onSizeChange(val) {
        this.page = {
          pageNo: 1,
          pageSize: val,
        };
        this.getApiList();
      },
      onCurrentChange(val) {
        this.page.pageNo = val;
        this.getApiList();
      },
      async getApiGroupList() {
        this.apiGroupList.loading = true;
        await apiGroup
          .getThemeList()
          .done(res => {
            this.apiGroupList.list = [{ id: null, groupName: '全部' }, ...(res.data || [])];
            this.defaultExpandedKeys = parseInt(this.$route.query.apiGroupId);
          })
          .always(() => {
            this.apiGroupList.loading = false;
          });
      },
      confirmTpl(config) {
        this.$confirm(config.title, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }).then(() => {
          apiController[config.method](config.params).done(res => {
            Message.closeAll();
            this.$message({
              type: 'success',
              message: config.successTip,
            });
            if (config.callback) {
              config.callback.call(this, res);
            }
          });
        });
      },
      onImpOrExp() {
        this.dialog.apiImportExport = true;
      },
      resetFormFields() {
        this.query = {
          dateRange: [],
          apiName: '',
          dataAssetPublishStatus: -1,
          apiType: -1, // API类型
          dataSourceId: null,
          apiApplyStatus: -1,
          dataAssetApiMethod: '',
          source: '',
        };
      },
    },
  };
</script>

<style lang="less" scoped>
  @import '@/css/var.less';
  .bd-page {
    padding-top: 20px;
    margin-bottom: 0px;
    padding-bottom: 10px;
  }
  .page-api-publish {
    box-sizing: border-box;

    .operation {
      display: flex;
      flex-wrap: wrap;
      .bd-button {
        border: none;
        background: white;
        margin-left: 0px;
        margin-right: 0px;
        padding: 5px 10px;
      }
    }
    .bd-container {
      background: white;

      height: calc(~'100vh - 172px');
      position: relative;
      .condition {
        margin-bottom: 0;
      }
      .right-container {
        height: 100%;
        margin-left: 251px;

        .publish-table {
          background: white;
          padding: 0 20px 15px 20px;

          // /deep/ .name {
          // color: #2776fb;
          // }
          .ellipsis {
            overflow: hidden;
            text-overflow: ellipsis;
            display: inline-block;
            white-space: nowrap;
          }
        }
      }

      .left-container {
        position: absolute;
        top: 0;
        left: 0;
        height: 100%;
        border-right: 1px solid #e6e6e6;
      }
    }

    /deep/ .el-radio-button__inner {
      border: none;
      background-color: white;
    }

    /deep/ .el-radio-button {
      border-left: none;
    }

    /deep/ .is-active {
      color: @dss--primary-color;
      border-bottom: 2px solid @dss--primary-color;
    }

    /deep/ .el-radio-button__orig-radio:checked + .el-radio-button__inner {
      color: @dss--primary-color;
      box-shadow: none;
    }

    /deep/ .el-table--enable-row-hover .el-table__body tr:hover .bd-button {
      background: @body-backgroud;
    }
  }
  .bd-search {
    align-items: center;
  }
</style>
