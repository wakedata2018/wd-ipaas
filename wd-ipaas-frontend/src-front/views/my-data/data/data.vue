<template>
  <div class="page-api-join bd-page">
    <div class="bd-header">
      <div class="title">我的接入</div>
    </div>
    <div class="bd-container">
      <div class="join-list">
        <info-card-list
          :card-height="200"
          :card-list="showJoinList"
          card-type="join"
          v-loading="loading.list"
          @command="onCommand"
          :is-my="true"
        />
      </div>
      <new-app :app-info="appInfo" :visible.sync="dialog.app" @saved="onSaved" :in-charge-list="inChargeList" />
      <display-app :app-info="displayApp" :visible.sync="dialog.display" />
      <white-ip :visible.sync="dialog.whiteIP" :app-info="appInfo"></white-ip>
    </div>
  </div>
</template>

<script>
  import NewApp from './new-app.vue';
  import DisplayApp from './display-app.vue';
  import WhiteIp from '@/views/data-application/join-application/white-ip.vue';
  import daaAPI from '@/api/data-access-app';
  import IPager from '../../../mixins/i-pager';
  import IPagesize from '../../../mixins/i-pagesize';
  import { ApprovalStatus } from '@/enum';
  import InfoCardList from '@/bz-components/info-card-list';
  import { mapState } from 'vuex';
  import mydata from '../../../api/mydata';
  import myApp from '../../../api/my-app';

  export default {
    components: {
      NewApp,
      DisplayApp,
      WhiteIp,
      InfoCardList,
    },
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
        mydata
          .getJoinInfo()
          .done(res => {
            this.tableData = res.data;
            this.showJoinList = res.data;
            this.totalCount = res.totalCount;
            myApp.clear();
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
        if (type === 'create' && app.status === 'PASS') {
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
      onCardClick(app) {
        if (app.status === 'PASS') {
          this.displayApp = app;
          this.dialog.display = true;
        }
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
          .done(() => {
            this.getAppList();
            this.$message('删除成功。');
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
      onShowApiDetail(val) {
        // console.log(val);
        // const params = {
        //   appId: val.dataAccessAppId,
        //   appName: val.dataAccessAppName,
        // };
        // if (params) {
        //   this.$router.push({
        //     path: '/index',
        //     query: params,
        //   });
        // }
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
    .join-list {
      background: white;
      padding-bottom: 15px;
    }
  }
</style>
