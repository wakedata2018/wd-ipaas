<template>
  <div class="page-api-join bd-page">
    <div class="bd-header">
      <div class="title">接入应用管理</div>
      <!-- <div class="header-right">
        <el-button type="primary" @click="onAdd">+新增</el-button>
      </div> -->
    </div>
    <div class="bd-container">
      <SearchCard :query.sync="query" @search="onSearch" />
      <div class="join-list">
        <info-card-list :card-list="showJoinList" card-type="join" v-loading="loading.list" @command="onCommand" />
      </div>
      <new-app :app-info="appInfo" :visible.sync="dialog.app" @saved="onSaved" :in-charge-list="inChargeList" />
      <display-app :app-info="displayApp" :visible.sync="dialog.display" />
      <white-ip :visible.sync="dialog.whiteIP" :app-info="appInfo"></white-ip>
    </div>
  </div>
</template>

<script>
  import SmHeader from '@/bz-components/sm-header.vue';
  import NewApp from './new-app.vue';
  import DisplayApp from './display-app.vue';
  import WhiteIp from './white-ip.vue';
  import SearchCard from '@/bz-components/search-card.vue';
  import Pager from '@/bz-components/pager.vue';
  import daaAPI from '@/api/data-access-app.js';
  import IPager from '../../../mixins/i-pager.js';
  import IPagesize from '../../../mixins/i-pagesize.js';
  import { ApprovalStatus } from '@/enum.js';
  import InfoCardList from '@/bz-components/info-card-list';
  import { mapState } from 'vuex';
  import { IPAAS_APP, PASS } from '@/utils/enum/third-app';

  export default {
    components: { SmHeader, NewApp, DisplayApp, WhiteIp, SearchCard, Pager, InfoCardList },
    mixins: [IPager, IPagesize],
    data() {
      return {
        ApprovalStatus, // 审批状态
        approvalId: null,
        tableData: [],
        showJoinList: [],
        tableProp: 'tableData',
        appInfo: null,
        displayApp: null,
        inChargeList: [],
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
      getAppList() {
        this.loading.list = true;
        daaAPI
          .getList({
            // ...this.page,
            ...this.form,
            status: PASS.value,
            appType: IPAAS_APP.value,
          })
          .done(res => {
            this.showJoinList = res.data;
            this.tableData = res.data;
            this.totalCount = res.totalCount;
          })
          .always(() => {
            this.loading.list = false;
          });
      },
      onAdd() {
        this.appInfo = null;
        this.dialog.app = true;
      },
      onSaved(type, parmas, app) {
        if (type === 'create') {
          this.displayApp = app;
          this.dialog.display = true;
        }
        this.getAppList();
      },
      onCommand(operation, source) {
        const method = this['on' + operation];
        if (method) {
          method(source);
        }
      },
      // 审批
      onApproval(applyInfo) {
        this.approvalId = applyInfo.approvalId;
        this.dialog.approve = true;
      },
      onSearch(val) {
        this.loading.list = true;
        // this.page.pageNo = 1;
        this.form = { ...this.form, ...val };
        this.getAppList();
      },
      // 获取负责人列表
      queryInChargeList() {
        const params = {
          accountType: 1,
        };
        daaAPI.queryInChargeList(params).then(res => {
          if (res.success) {
            this.inChargeList = res.data;
          }
        });
      },
      getInChargeName(account) {
        return (this.inChargeList.find(item => item.account === account) || {}).displayName || account || '';
      },

      // 操作
      onEdit(appInfo) {
        this.appInfo = appInfo;
        this.dialog.app = true;
      },
      onDelete(appInfo) {
        this.$confirm('确定删除该接入吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          // center: true
        })
          .then(() => {
            this.delete(appInfo);
          })
          .catch(res => {
            console.log('用户取消操作', res);
          });
      },
      delete(appInfo) {
        this.loading.app = true;
        daaAPI
          .delete(appInfo.dataAccessAppId)
          .done(res => {
            this.getAppList();
            this.$message.success('删除成功。');
          })
          .always(() => {
            this.loading.app = false;
          });
      },
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
          });
      },
      resetSecret(appInfo) {
        this.loading.app = true;
        daaAPI
          .resetSecret(appInfo.dataAccessAppId)
          .done(res => {
            this.displayApp = res.data;
            this.dialog.display = true;
          })
          .always(() => {
            this.loading.app = false;
          });
      },
      onShowWhiteIP(appInfo) {
        this.appInfo = appInfo;
        this.dialog.whiteIP = true;
      },
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
      onShowApiDetail(val) {
        const params = {
          appId: val.dataAccessAppId,
          appName: val.dataAccessAppName,
        };
        if (params) {
          this.$router.push({
            path: '/data-application/api-detail',
            query: params,
          });
        }
      },
    },
    mounted() {
      this.queryInChargeList();
    },
  };
</script>

<style scoped lang="less">
  .page-api-join {
    // height: @full-height;
    // overflow: auto;

    box-sizing: border-box;
    margin-bottom: 50px;
    .join-list {
      background: white;
      padding-bottom: 15px;
    }
  }
</style>
