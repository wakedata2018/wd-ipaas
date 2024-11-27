<template>
  <div class="low-code-app bd-page">
    <div class="bd-header">
      <div class="title">应用管理</div>
    </div>
    <div class="bd-container">
      <!-- <SearchCard :query.sync="query" @search="onSearch" /> -->
      <div class="app-list">
        <info-card-list
          :card-list="appList"
          :show-operation="true"
          card-type="lowcode"
          v-loading="loading"
          @command="onCommand"
        >
        </info-card-list>
      </div>
      <new-app :app-info="appInfo" :visible.sync="dialog.app" @saved="onSaved" />
    </div>
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
  </div>
</template>

<script>
  import NewApp from './new-app.vue';
  import daaAPI from '@/api/data-access-app';
  import { LOW_CODE_APP, PASS } from '@/utils/enum/third-app';
  import InfoCardList from '@/bz-components/info-card-list';
  import SearchCard from '@/bz-components/search-card.vue';

  export default {
    components: { NewApp, InfoCardList, SearchCard },
    data() {
      return {
        LOW_CODE_APP,
        appList: [],
        appInfo: null,
        loading: false,
        totalCount: 0,
        page: {
          pageSize: 10,
          pageNo: 1,
        },
        query: {
          keyword: '',
          statusEnum: '',
        },
        dialog: {
          app: false,
        },
      };
    },
    created() {
      this.getAppList();
    },
    methods: {
      async getAppList() {
        this.loading = true;
        try {
          const { data, totalCount } = await daaAPI.getList({
            // ...this.page,
            ...this.query,
            status: PASS.value,
            appType: LOW_CODE_APP.value,
          });
          this.appList = data || [];
          this.totalCount = totalCount;
        } finally {
          this.loading = false;
        }
      },
      onCommand(operation, source) {
        const method = this['on' + operation];
        if (method) method(source);
      },
      onAdd() {
        this.appInfo = null;
        this.dialog.app = true;
      },
      onSaved(type, parmas, app) {
        this.getAppList();
      },

      // 操作
      onEdit(appInfo) {
        this.appInfo = appInfo;
        this.dialog.app = true;
      },
      onDelete(appInfo) {
        this.$confirm('确定删除该应用吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }).then(() => {
          this.delete(appInfo);
        });
      },
      async delete(appInfo) {
        this.loading = true;
        try {
          await daaAPI.delete(appInfo.dataAccessAppId);
          this.getAppList();
          this.$message.success('删除成功。');
        } finally {
          this.loading = false;
        }
      },
      onSearch(val) {
        this.loading = true;
        this.query = { ...val };
        this.page.pageNo = 1;
        this.getAppList();
      },
      onSizeChange(val) {
        this.page.pageSize = val;
        this.onCurrentChange(1);
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
            path: '/data-application/low-code-application-detail',
            query: params,
          });
        }
      },
    },
    mounted() {},
  };
</script>

<style scoped lang="less">
  .low-code-app {
    box-sizing: border-box;
    padding-top: 80px;
    margin-bottom: 50px;
    .app-list {
      background: white;
      padding-bottom: 15px;
    }
  }
</style>
