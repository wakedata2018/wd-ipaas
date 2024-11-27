<template>
  <div class="page-api-join bd-page">
    <div class="bd-header">
      <div class="title">审批管理</div>
      <!-- <div class="header-right">
        <el-button type="primary" @click="onAdd">+新增</el-button>
      </div> -->
    </div>
    <div class="bd-container">
      <SearchCard :query.sync="query" @search="onSearch" />
      <div class="app-table">
        <el-table v-loading="loading.list" :data="tableData" class="dss-table bd-table">
          <el-table-column prop="dataAccessAppName" label="接入名称"></el-table-column>
          <el-table-column prop="dataAccessKey" label="接入KEY"></el-table-column>
          <el-table-column prop="dataAccessDescription" show-overflow-tooltip label="描述"></el-table-column>
          <el-table-column prop="inCharge" label="申请人" align="center" width="100"></el-table-column>
          <el-table-column prop="createTime" label="申请时间" width="140"></el-table-column>
          <!-- <el-table-column prop="message" label="审批意见" show-overflow-tooltip></el-table-column> -->
          <!-- <el-table-column prop="status" label="审批状态" align="center" width="120">
            <template slot-scope="scope">
              <el-tag
                class="status-tag"
                :style="{'background-color': ApprovalStatus.toColor(scope.row.status)}"
              >{{ApprovalStatus.getDesc(scope.row.status)}}</el-tag>
            </template>
          </el-table-column>-->
          <el-table-column label="操作" align="center" width="320">
            <template #default="scope">
              <!-- <el-button type="text" @click="termination(scope.row)" class="termination">终止</el-button> -->
              <!-- <el-button class="bd-button bd-table-primary" @click="onEdit(scope.row)">编辑</el-button> -->
              <!-- <el-button type="text" @click="onStart(scope.row)" class="start">启动</el-button> -->
              <!-- <el-button class="bd-button bd-table-danger" @click="onDelete(scope.row)">删除</el-button> -->
              <el-button class="bd-button bd-table-primary" @click="onApproval(scope.row)">审批</el-button>
              <!-- <el-button class="bd-button bd-table-danger" @click="onDelete(scope.row)">拒绝</el-button> -->
              <el-button class="bd-button bd-table-primary" @click="onReset(scope.row)">重置密钥</el-button>
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
  // import NewApp from './new-app.vue';
  import DisplayApp from './display-app.vue';
  // import WhiteIp from './white-ip.vue';
  import ApprovalDialog from './approval-dialog.vue';
  import SearchCard from '@/bz-components/search-card.vue';
  import joinApprove from '@/api/join-approve.js';
  import IPager from '../../../mixins/i-pager.js';
  import IPagesize from '../../../mixins/i-pagesize.js';
  import { ApprovalStatus } from '@/enum.js';
  import { mapState } from 'vuex';

  export default {
    components: { DisplayApp, SearchCard, ApprovalDialog },
    mixins: [IPager, IPagesize],
    data() {
      return {
        ApprovalStatus, // 审批状态
        approvalId: null,
        tableData: [],
        tableProp: 'tableData',
        appInfo: null,
        displayApp: null,

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
          statusEnum: '',
        },
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
      // handleSizeChange() {
      //   console.log('改变页面条数');
      // },
      // handleCurrentChange() {
      //   console.log('改变当前页面');
      // },
      getAppList() {
        this.loading.list = true;
        joinApprove
          .getList({
            ...this.page,
            ...this.form,
            status: 'CREATED',
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
      // onAdd() {
      //   this.appInfo = null;
      //   this.dialog.app = true;
      // },
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
      onSearch(val) {
        this.loading.list = true;
        this.page.pageNo = 1;
        this.form = { ...this.form, ...val };
        this.getAppList();
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
      // delete(appInfo) {
      //   this.loading.app = true;
      //   daaAPI
      //     .delete(appInfo.dataAccessAppId)
      //     .done(res => {
      //       this.getAppList();
      //       this.$message('删除成功。');
      //     })
      //     .always(() => {
      //       this.loading.app = false;
      //     });
      // },
      onReset(appInfo) {
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
        this.loading.app = true;
        joinApprove
          .resetSecret({ dataAccessAppId: appInfo.dataAccessAppId })
          .done(res => {
            this.displayApp = res.data;
            this.dialog.display = true;
          })
          .always(() => {
            this.loading.app = false;
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
        this.page.pageNo = 1;
        this.page.pageSize = val;
        this.getAppList();
      },
      onCurrentChange(val) {
        this.page.pageNo = val;
        this.getAppList();
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
