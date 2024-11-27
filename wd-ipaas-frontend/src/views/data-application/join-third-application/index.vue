<template>
  <div class="page-api-join bd-page">
    <div class="bd-header">
      <div class="title">接入应用管理</div>
    </div>
    <div class="bd-container">
      <search-card :query.sync="query" @search="onSearch" />
      <div class="join-list">
        <info-card-list :card-list="showJoinList" card-type="join" v-loading="loading.list" @command="onCommand" />
      </div>
      <new-app :app-info="appInfo" :visible.sync="dialog.app" @saved="onSaved" :in-charge-list="inChargeList" />
    </div>
  </div>
</template>

<script>
  import NewApp from './new-app.vue';
  import SearchCard from '@/bz-components/search-card.vue';
  import daaAPI from '@/api/data-access-app.js';
  import IPager from '../../../mixins/i-pager.js';
  import IPagesize from '../../../mixins/i-pagesize.js';
  import { ApprovalStatus } from '@/enum.js';
  import InfoCardList from '@/bz-components/info-card-list';
  import { mapState } from 'vuex';

  export default {
    components: { NewApp, SearchCard, InfoCardList },
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
          .getAuthInformationList({
            ...this.form,
          })
          .then(res => {
            // 因为后端返回数据不含status无法显示按钮，给第三方应用添加status属性显示编辑和删除按钮
            res.data.forEach(item => {
              item.status = 'PASS';
            });
            this.showJoinList = res.data;
          })
          .finally(() => {
            this.loading.list = false;
          });
      },
      onAdd() {
        this.appInfo = null;
        this.dialog.app = true;
      },
      onSaved() {
        this.getAppList();
      },
      onCommand(operation, source) {
        const method = this['on' + operation];
        if (method) {
          method(source);
        }
      },
      onSearch(val) {
        this.loading.list = true;
        this.form = { ...this.form, ...val };
        this.getAppList();
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
          .deleteAuthInformation({
            authInfoId: appInfo.id,
          })
          .then(res => {
            this.getAppList();
            this.$message.success('删除成功。');
          })
          .finally(() => {
            this.loading.app = false;
          });
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
          id: val.id,
          appName: val.appName,
        };
        if (params) {
          this.$router.push({
            path: '/data-application/third-auth-api-detail',
            query: params,
          });
        }
      },
    },
  };
</script>

<style scoped lang="less">
  .page-api-join {
    box-sizing: border-box;
    margin-bottom: 50px;
    .join-list {
      background: white;
      padding-bottom: 15px;
    }
  }
</style>
