<template>
  <div class="page-application bd-page">
    <div class="bd-header">
      <el-breadcrumb v-if="showModel" separator="/" class="title">
        <el-breadcrumb-item class="uncheck" :to="{ path: '/data-application/application' }">{{
          title
        }}</el-breadcrumb-item>
        <el-breadcrumb-item class="check">创建应用</el-breadcrumb-item>
      </el-breadcrumb>
      <div v-else class="title">{{ title }}</div>
      <div class="header-right">
        <el-button v-if="!showModel" type="primary" @click="onChooseModel">新增应用</el-button>
      </div>
    </div>
    <div class="bd-container">
      <!-- 引导页 没有应用时显示 -->
      <el-empty v-if="noData" description="还没有已经完成创建的应用">
        <el-button type="primary" @click="onChooseModel">开始新增我的第一个应用</el-button>
      </el-empty>
      <!-- 选择创建应用的模式 -->
      <application-model v-if="showModel" @on-create="onCreateModel" />
      <!-- 列表 -->
      <div v-if="showList" class="application-list">
        <div class="bd-search">
          <el-form
            ref="filterForm"
            size="mini"
            :model="form"
            label-position="right"
            class="bd-form"
            inline
            label-width="100px"
          >
            <el-form-item label="应用名称" prop="dataAccessAppName">
              <el-input v-model.trim="form.dataAccessAppName" type="text" maxlength="30" placeholder="请输入应用名称" />
            </el-form-item>
            <el-form-item label="App Key" prop="dataAccessKey">
              <el-input v-model.trim="form.dataAccessKey" type="text" maxlength="50" placeholder="请输入App Key" />
            </el-form-item>
            <el-form-item label="创建时间">
              <el-date-picker
                v-model="dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                placeholder="请选择申请时间"
                @change="handleDate"
              ></el-date-picker>
            </el-form-item>
          </el-form>
          <div class="bd-search-group">
            <el-button type="primary" class="bd-button bd-strong" @click="onSearch">查询</el-button>
            <el-button type="primary" plain size="mini" class="bd-button bd-strong" @click="onReset">重置</el-button>
          </div>
        </div>
        <div class="theme-table">
          <el-table v-loading="loading" :data="applicationList" class="dss-table bd-table">
            <el-table-column prop="dataAccessAppName" label="应用名称" show-overflow-tooltip></el-table-column>
            <el-table-column prop="dataAccessKey" label="App Key" min-width="150">
              <template #default="scope">
                <template v-if="!!scope.row.dataAccessKey">
                  {{ scope.row.dataAccessKey + ' ' }}
                  <el-button
                    v-clipboard:copy="scope.row.dataAccessKey"
                    v-clipboard:success="copy"
                    type="text"
                    icon="el-icon-document-copy"
                  ></el-button>
                </template>
                <template v-else>-</template>
              </template>
            </el-table-column>
            <el-table-column prop="apiNum" label="已授权API数"></el-table-column>
            <el-table-column prop="publishStatus" label="发布状态" align="center">
              <template #default="scope">
                <span
                  :class="{
                    'publish-status': true,
                    'dss-status-success': !publishStatusEnum[scope.row.publishStatus]?.value,
                    'dss-status-error': !!publishStatusEnum[scope.row.publishStatus]?.value,
                  }"
                  >{{ publishStatus(scope.row) }}</span
                >
              </template>
            </el-table-column>
            <el-table-column prop="dataAccessDescription" show-overflow-tooltip label="描述"></el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="140" show-overflow-tooltip></el-table-column>
            <el-table-column label="操作" align="center">
              <template #default="scope">
                <el-button type="text" class="bd-button bd-table-primary" @click="onDetail(scope.row)">详情</el-button>
                <el-button type="text" class="bd-button bd-table-primary" @click="onEdit(scope.row)">编辑</el-button>
                <el-button type="text" class="bd-button bd-table-danger" @click="onDelete(scope.row)">删除</el-button>
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
    </div>
    <application-dialog :visible.sync="isShow" :model="info" :app-auth-type="appAuthType" @save-success="onSuccess" />
    <application-detail v-if="isShowDetail" :id="detailId" :visible.sync="isShowDetail"></application-detail>
  </div>
</template>
<script>
  import { mapState } from 'vuex';
  import { date } from 'dss-common';
  import applicationDialog from './application-dialog.vue';
  import ApplicationDetail from './application-detail.vue';

  import applicationApi from '@/api/api-application.js';
  import ApplicationModel from './application-model';
  import { APPLICATION_PUBLISH_STATUS } from '@/enum.js';
  import pageUtils from '@/utils/page.js';

  export default {
    components: {
      applicationDialog,
      ApplicationModel,
      ApplicationDetail,
    },
    data() {
      return {
        isInit: true,
        noData: false,
        dateRange: [],
        loading: false,
        isShow: false, // 显示弹窗
        isShowDetail: false, // 详情
        isShowApiDetail: false, // API详情
        detailId: '', // 应用详情id
        info: null, //
        form: {
          dataAccessAppName: null,
          dataAccessKey: null,
          startTime: null,
          endTime: null,
        },
        totalCount: 0,
        pageParams: {
          pageSize: 10,
          pageNo: 1,
        },
        applicationList: [],
        showList: false, // 显示列表
        showModel: false, // 选择创建的应用模式
        appAuthType: null, // 新增应用的模式类型  目前只有惟客云 后面扩展
        publishStatusEnum: APPLICATION_PUBLISH_STATUS,
      };
    },
    computed: {
      ...mapState({
        permitList: state => state.permitList,
      }),
      title() {
        return pageUtils.findPageName(this.permitList, this.$route.name);
      },
      publishStatus() {
        return row => {
          return this.publishStatusEnum[row.publishStatus].label ?? '';
        };
      },
    },
    created() {
      this.getAppList();
    },

    mounted() {
      this.isInit = false;
    },
    methods: {
      handleDate(val) {
        const startTime = date.format(val ? val[0] : null, 'YYYY-MM-DD 00:00:00');
        const endTime = date.format(val ? val[1] : null, 'YYYY-MM-DD 23:59:59');
        this.form.startTime = startTime;
        this.form.endTime = endTime;
      },
      getAppList() {
        this.loading = true;
        applicationApi
          .getApplicationList({
            ...this.form,
            ...this.pageParams,
          })
          .then(res => {
            this.noData = false;
            this.applicationList = res.data;
            this.totalCount = res.totalCount;

            // 首次加载查找无应用则显示引导页
            if (this.isInit && !this.applicationList) {
              this.isInit = false;
              this.noData = true;
            } else {
              this.showList = true;
            }
          })
          .finally(() => {
            this.loading = false;
          });
      },
      onSearch() {
        this.getAppList();
      },
      onReset() {
        this.dateRange = [];
        this.$refs.filterForm.resetFields();
        this.getAppList();
      },
      onDetail(val) {
        this.detailId = val.dataAccessAppId;
        this.isShowDetail = true;
      },
      onEdit(row) {
        this.info = row;
        this.isShow = true;
      },
      onDelete(row) {
        this.$confirm('应用删除之后将无法恢复，确定删除吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
          .then(res => {
            applicationApi
              .deleteApplicaption({ dataAccessAppId: row.dataAccessAppId })
              .then(() => {
                this.getAppList();
                this.noData = false;
              })
              .catch(error => {
                console.error(error);
              });
          })
          .catch(() => {
            this.$message({
              type: 'info',
              message: '已取消删除',
            });
          });
      },
      onAdd() {
        this.showModel = true;
      },
      onPageChange(pageNo) {
        this.pageParams.pageNo = pageNo;
        this.getAppList();
      },
      onSizeChange(size) {
        this.pageParams.pageSize = size;
        this.pageParams.pageNo = 1;
        this.getAppList();
      },
      // 创建第一个应用
      onChooseModel() {
        this.noData = false;
        this.showModel = true;
        this.showList = false;
        this.info = null;
      },
      // 创建应用的模式
      onCreateModel(type) {
        this.appAuthType = type;
        this.showModel = false;
        this.showList = true;
        this.isShow = true;
      },
      onSuccess() {
        this.getAppList();
      },
      copy(e) {
        if (e.text) {
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
  .bd-container {
    background: #fff;
  }
</style>
