<template>
  <div class="page-api-join bd-page">
    <div class="bd-header">
      <div class="title">审批管理</div>
      <!-- <div class="header-right">
        <el-button type="primary" @click="onAdd">+新增</el-button>
      </div> -->
    </div>
    <div class="bd-container">
      <!-- <SearchCard :query.sync="query" @search="onSearch" /> -->
      <div class="bd-search condition">
        <el-form size="mini" label-position="right" class="bd-form" inline label-width="68px">
          <el-form-item label="关键词">
            <el-input v-model.trim="query.keyword" type="text" style="width: 250px" max-length="32" />
          </el-form-item>
          <el-form-item label="申请时间">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              @change="handleDate"
            ></el-date-picker>
          </el-form-item>
          <br />
          <el-form-item label="审批状态">
            <dss-select v-model="query.status" :data="ApprovalStatusArray"></dss-select>
          </el-form-item>
        </el-form>
        <div class="bd-search-group">
          <el-button type="primary" class="bd-button bd-strong" @click="onSearch">查询</el-button>
          <el-button type="primary" plain size="mini" class="bd-button bd-strong" @click="onReset">重置</el-button>
        </div>
      </div>
      <div class="app-table">
        <el-table v-loading="loading.list" :data="tableData" class="dss-table bd-table">
          <el-table-column prop="dataAccessAppName" label="接入名称"></el-table-column>
          <el-table-column prop="dataAccessKey" label="APP ID">
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
          <el-table-column prop="dataAccessDescription" show-overflow-tooltip label="描述"></el-table-column>
          <el-table-column prop="inCharge" label="申请人" align="center" width="100"></el-table-column>
          <el-table-column prop="createTime" label="申请时间" width="140"></el-table-column>
          <!-- <el-table-column prop="message" label="审批意见" show-overflow-tooltip></el-table-column> -->
          <el-table-column prop="status" label="审批状态" align="center" width="120">
            <template #default="scope">
              <div :style="{ color: getStatusColor(scope.row.status) }">
                {{ getStatusDesc(scope.row.status) }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="320">
            <template #default="scope">
              <!-- <el-button type="text" @click="termination(scope.row)" class="termination">终止</el-button> -->
              <!-- <el-button class="bd-button bd-table-primary" @click="onEdit(scope.row)">编辑</el-button> -->
              <!-- <el-button type="text" @click="onStart(scope.row)" class="start">启动</el-button> -->
              <!-- <el-button class="bd-button bd-table-danger" @click="onDelete(scope.row)">删除</el-button> -->

              <el-button
                v-if="scope.row.status === 'CREATED'"
                class="bd-button bd-table-primary"
                @click="onApproval(scope.row)"
                >审批</el-button
              >
              <!-- <el-button class="bd-button bd-table-danger" @click="onDelete(scope.row)">拒绝</el-button> -->
              <el-button
                v-if="scope.row.status === 'PASS'"
                class="bd-button bd-table-primary"
                @click="onResetKey(scope.row)"
                >重置密钥</el-button
              >
              <el-button
                v-if="scope.row.status === 'PASS'"
                class="bd-button bd-table-danger"
                @click="onDisapproval(scope.row)"
                >强制删除</el-button
              >
              <!-- <el-button class="bd-button bd-table-primary" @click="onShowWhiteIP(scope.row)">认证机制</el-button> -->
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
          ></el-pagination>
        </div>
        <div class="page">
          <!-- <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          style="float:right;"
          :current-page.sync="pageIndex"
          :page-sizes="[7,14,21]"
          :page-size.sync="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="tableData.length">
          </el-pagination>-->
          <!-- <pager
            :page-size.sync="page.pageSize"
            :current-page.sync="page.pageNo"
            :total="total"
            :requestFn="getAppList"
            style="float:right;"
          />-->
        </div>
      </div>
      <!-- <new-app :app-info="appInfo" :visible.sync="dialog.app" @saved="onSaved" /> -->
      <display-app :app-info="displayApp" :visible.sync="dialog.display" />
      <approval-dialog :visible.sync="dialog.approve" :approval-id="approvalId" @saved="onSaved"></approval-dialog>
      <!-- <white-ip :visible.sync="dialog.whiteIP" :app-info="appInfo"></white-ip> -->
    </div>
  </div>
</template>

<script>
  import SmHeader from '@/bz-components/sm-header.vue';
  // import NewApp from './new-app.vue';
  import DisplayApp from './display-app.vue';
  // import WhiteIp from './white-ip.vue';
  import ApprovalDialog from './approval-dialog.vue';
  import SearchCard from '@/bz-components/search-card.vue';
  import Pager from '@/bz-components/pager.vue';
  import joinApprove from '@/api/join-approve.js';
  import IPager from '../../../mixins/i-pager.js';
  import IPagesize from '../../../mixins/i-pagesize.js';
  import { ApprovalStatus } from '@/enum.js';
  import approvalAPI from '@/api/approval.js';
  import DssSelect from '@/components/dss-select/index.vue';
  import { mapState } from 'vuex';
  import { date } from 'dss-common';
  import { Message } from 'element-ui';

  export default {
    components: {
      SmHeader,
      DisplayApp,
      SearchCard,
      Pager,
      ApprovalDialog,
      DssSelect,
    },
    mixins: [IPager, IPagesize],
    data() {
      return {
        ApprovalStatus, // 审批状态
        approvalId: null,
        tableData: [],
        tableProp: 'tableData',
        appInfo: null,
        displayApp: null,
        dateRange: [],
        loading: {
          list: false,
        },
        dialog: {
          app: false,
          display: false,
          whiteIP: false,
          approve: false,
        },
        query: {
          keyword: '',
          from: null,
          to: null,
          status: '',
        },
        ApprovalStatusArray: [
          { label: '所有', value: '' },
          { label: '待审批', value: 'CREATED' },
          { label: '已通过', value: 'PASS' },
          { label: '未通过', value: 'REFUSE' },
        ],
        totalCount: 0,
        page: {
          pageSize: 10,
          pageNo: 1,
        },
      };
    },
    computed: {
      ...mapState({
        username: state => state.user.displayName,
      }),
    },
    created() {
      this.getAppList();
    },
    methods: {
      getStatusColor(status) {
        switch (status) {
          case 'CREATED':
            return '#00c6f7';
          case 'PASS':
            return '#33e1cb';
          case 'REFUSE':
            return '#fb4938';
          default:
            return '#ffc43d';
        }
      },
      getStatusDesc(status) {
        switch (status) {
          case 'CREATED':
            return '待审批';
          case 'PASS':
            return '通过';
          case 'REFUSE':
            return '未通过';
          default:
            return '';
        }
      },
      getAppList() {
        this.loading.list = true;
        joinApprove
          .getList({
            ...this.page,
            ...this.query,
            // status: null,
          })
          .done(res => {
            // console.log(res)
            this.tableData = res.data;
            this.totalCount = res.totalCount;
          })
          .always(() => {
            this.loading.list = false;
          });
      },
      onSaved(type, parmas, app) {
        if (type === 'create') {
          this.displayApp = app;
          this.dialog.display = true;
        }
        this.getAppList();
      },
      // 审批
      onApproval(applyInfo) {
        // console.log(applyInfo)
        this.approvalId = applyInfo.dataAccessAppId;
        this.dialog.approve = true;
      },
      onDisapproval(applyInfo) {
        this.$confirm('确定强制删除该接入吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          // center: true
        })
          .then(() => {
            this.delete(applyInfo);
          })
          .catch(res => {
            console.log('用户取消操作', res);
          });
      },
      onSearch(val) {
        this.loading.list = true;
        this.page.pageNo = 1;
        this.query = { ...this.query, ...val };
        this.getAppList();
      },
      handleDate(val) {
        const time1 = date.format(val ? val[0] : null, 'YYYY-MM-DD 00:00:00');
        const time2 = date.format(val ? val[1] : null, 'YYYY-MM-DD 23:59:59');
        this.query.from = time1;
        this.query.to = time2;
      },
      onReset() {
        this.query = {
          keyword: '',
          from: null,
          to: null,
          status: '',
        };
        this.dateRange = [];
        // this.query.ApprovalStatus = '';
        this.page.pageNo = 1;
        this.onSearch();
      },

      // 操作
      // onEdit(appInfo) {
      //   this.appInfo = appInfo;
      //   this.dialog.app = true;
      // },
      // onDelete(appInfo) {
      //   this.$confirm('确定拒绝该接入吗?', '提示', {
      //     confirmButtonText: '确定',
      //     cancelButtonText: '取消',
      //     type: 'warning'
      //     // center: true
      //   })
      //     .then(() => {
      //       this.delete(appInfo);
      //     })
      //     .catch(res => {
      //       console.log('用户取消操作', res);
      //     });
      // },
      delete(appInfo) {
        this.loading.list = true;
        joinApprove
          .delete({
            dataAccessAppId: appInfo.dataAccessAppId,
            forceDelete: true,
          })
          .then(res => {
            this.onReset();
            this.$message({
              message: '强制删除成功',
              type: 'success',
            });
          })
          .always(() => {
            this.loading.list = false;
          });
      },
      onResetKey(appInfo) {
        this.$confirm('确定重置密钥吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          // center: true
        })
          .then(() => {
            this.resetSecret(appInfo);
          })
          .catch(res => {
            console.log('用户取消操作', res);
            // console.log(appInfo)
          });
      },
      resetSecret(appInfo) {
        this.loading.list = true;
        joinApprove
          .resetSecret({ dataAccessAppId: appInfo.dataAccessAppId })
          .done(res => {
            this.displayApp = res.data;
            this.dialog.display = true;
          })
          .always(() => {
            this.loading.list = false;
          });
      },
      // onApprove(appInfo){
      //   this.$confirm('确定同意接入吗?', '提示', {
      //     confirmButtonText: '确定',
      //     cancelButtonText: '取消',
      //     type: 'warning'
      //     // center: true
      //   })
      //   .then(() => {
      //       this.approve(appInfo);
      //     })
      //     .catch(res => {
      //       console.log('用户取消操作', res);
      //     });
      // },
      // approve(appInfo){
      //   joinApprove
      //     .passApprove({
      //       dataAccessAppId:appInfo.dataAccessAppId,
      //       status:'PASS'
      //     })
      //     .done(res=>{
      //       console.log(res)
      //     })
      // },
      // onShowWhiteIP(appInfo) {
      //   this.appInfo = appInfo;
      //   this.dialog.whiteIP = true;
      // },
      // onSaved(type, parmas, app) {
      //   if (type === 'create') {
      //     this.displayApp = app;
      //     this.dialog.display = true;
      //   }
      //   this.getAppList();
      // },
      termination() {
        console.log('终止操作');
      },
      onStart() {
        console.log('启动');
      },
      onSizeChange(val) {
        this.page.pageSize = val;
        this.getAppList();
      },
      onCurrentChange(val) {
        this.page.pageNo = val;
        this.getAppList();
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
  .page-api-join {
    // height: @full-height;
    // overflow: auto;

    box-sizing: border-box;
    margin-bottom: 50px;
    .app-table {
      background: white;
      padding-bottom: 15px;
    }
  }
</style>
