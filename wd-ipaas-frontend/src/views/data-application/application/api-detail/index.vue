<template>
  <div class="bd-page">
    <div class="bd-header">
      <el-breadcrumb separator="/" class="title">
        <el-breadcrumb-item :to="{ path: `/data-application/application` }" class="uncheck"
          >应用管理</el-breadcrumb-item
        >
        <el-breadcrumb-item :to="{ path: `/data-application/application-detail?id=${dataAccessAppId}` }" class="uncheck"
          >应用详情</el-breadcrumb-item
        >
        <el-breadcrumb-item class="check">API详情</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="bd-container">
      <detail-info
        class="right-container"
        :id="dataAssetId"
        :info-data="infoList"
        v-loading="loading.api || loading.info"
        :auth-date="authDate"
      ></detail-info>
    </div>
  </div>
</template>

<script>
  import detailInfo from './detail-info';
  import dataAccessAppApi from '@/api/data-access-app.js';
  import accessApi from '@/api/app-access-times.js';

  export default {
    components: { detailInfo },
    data() {
      return {
        dataAssetId: null,
        list: [],
        infoList: {},
        loading: false,
        authDate: '',
        addThirdAuthVisable: false,
        apiList: [],
        totalCount: 0,
      };
    },
    computed: {
      dataAccessAppId() {
        return this.$route.query.dataAccessAppId;
      },
    },
    created() {
      this.getInfoList();
    },
    methods: {
      getInfoList() {
        this.loading = true;
        const { id } = this.$route.query;
        const params = {
          timestamp: new Date().getTime(),
          dataAssetId: id,
        };
        accessApi
          .assetDetail(params)
          .done(res => {
            this.infoList = res.data;
            const data = res.data;
            this.getAuthDate();
            if (!data) return;
          })
          .always(() => {
            this.loading = false;
          });
      },
      getAuthDate() {
        const { dataAccessAppId, id } = this.$route.query;
        dataAccessAppApi
          .authDate({
            appId: dataAccessAppId,
            apiId: id,
          })
          .done(res => {
            this.authDate = res.data;
          });
      },
    },
  };
</script>

<style lang="less" scoped>
  .bd-page {
    box-sizing: border-box;

    .bd-header {
      ::v-deep .uncheck {
        font-size: 16px;
        font-weight: 600;
      }

      ::v-deep .check {
        span {
          font-size: 16px;
          font-weight: 600;
          color: #bdbdbd;
        }
      }
    }

    .bd-container {
      background: white;

      height: calc(~'100vh - 162px');
      position: relative;

      .right-container {
        height: 100%;
      }
    }
  }
</style>
