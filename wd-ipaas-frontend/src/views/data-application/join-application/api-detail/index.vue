<template>
  <div class="bd-page">
    <div class="bd-header">
      <el-breadcrumb separator="/" class="title">
        <el-breadcrumb-item :to="{ path: '/data-application/join-application' }" class="uncheck"
          >接入应用管理</el-breadcrumb-item
        >
        <el-breadcrumb-item class="check">接入应用API详情</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <!-- <el-container>
      <el-aside width="140px">
        <api-select :list='list' :loading="loading.api" @input="setapiGroup" :value="filteredApiGroup"></api-select>
      </el-aside>
      <el-main>
        <detail-info :infoData='infoList' :loading="loading.info" :authDate="authDate"></detail-info>
      </el-main>
    </el-container> -->
    <div class="bd-container">
      <api-select
        class="left-container"
        :list="list"
        v-loading="loading.api"
        @input="setapiGroup"
        :value="filteredApiGroup"
        :add-third-auth-visable.sync="addThirdAuthVisable"
      ></api-select>
      <detail-info
        class="right-container"
        :id="filteredApiGroup"
        :info-data="infoList"
        v-loading="loading.api || loading.info"
        :auth-date="authDate"
        @get-api-list="getApiList"
        :filtered-api-group.sync="filteredApiGroup"
      ></detail-info>
    </div>
  </div>
</template>

<script>
  import apiSelect from './api-select';
  import detailInfo from './detail-info';
  import dataAccessAppApi from '@/api/data-access-app.js';
  import apiCol from '@/api/api-controller.js';
  import accessApi from '@/api/app-access-times.js';

  export default {
    components: { apiSelect, detailInfo },
    data() {
      return {
        filteredApiGroup: null,
        list: [],
        infoList: {},
        loading: {
          api: false,
          info: false,
        },
        authDate: '',
        addThirdAuthVisable: false,
        apiList: [],
        totalCount: 0,
      };
    },
    created() {
      this.getApiList();
    },
    methods: {
      setapiGroup(val) {
        this.filteredApiGroup = val;
        this.getInfoList();
        this.getAuthDate();
      },
      getApiList() {
        this.loading.api = true;
        const appId = this.$route.query.appId;
        dataAccessAppApi
          .reference({ appId })
          .done(res => {
            this.list = res.data;
            if (!this.filteredApiGroup) {
              this.filteredApiGroup = this.list[0].apiId;
            }
            this.getInfoList();
            this.getAuthDate();
          })
          .always(() => {
            this.loading.api = false;
          });
      },
      getInfoList() {
        this.loading.info = true;
        const dataAssetId = this.filteredApiGroup;
        const params = {
          timestamp: new Date().getTime(),
          dataAssetId,
        };
        accessApi
          .assetDetail(params)
          .done(res => {
            this.infoList = res.data;
            const data = res.data;
            if (!data) return;
          })
          .always(() => {
            this.loading.info = false;
          });
      },
      getAuthDate() {
        const appId = this.$route.query.appId;
        dataAccessAppApi
          .authDate({
            appId: appId,
            apiId: this.filteredApiGroup,
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
      margin-top: 80px;
      background: white;

      height: calc(~'100vh - 162px');
      position: relative;
      .left-container {
        position: absolute;
        top: 0;
        left: 0;
        height: 100%;
        border-right: 1px solid #e6e6e6;
      }
      .right-container {
        height: 100%;
        margin-left: 251px;
      }
    }
  }
</style>
