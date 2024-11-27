<template>
  <div class="bd-page">
    <div class="bd-header">
      <el-breadcrumb separator="/" class="title">
        <el-breadcrumb-item :to="{ path: '/data-application/join-application' }" class="uncheck"
          >低代码应用管理</el-breadcrumb-item
        >
        <el-breadcrumb-item class="check">低代码应用API详情</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
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
      <add-third-auth-dialog
        ref="authDialog"
        :visible.sync="addThirdAuthVisable"
        @success="getApiList"
      ></add-third-auth-dialog>
    </div>
  </div>
</template>

<script>
  import apiSelect from './api-select';
  import detailInfo from './detail-info';
  import dataAccessAppApi from '@/api/data-access-app.js';
  import accessApi from '@/api/app-access-times.js';
  import addThirdAuthDialog from './add-third-auth-dialog';

  export default {
    components: { apiSelect, detailInfo, addThirdAuthDialog },
    data() {
      return {
        filteredApiGroup: null,
        list: [],
        infoList: {},
        loading: {
          api: false,
          info: false,
        },
        authDate: null,
        addThirdAuthVisable: false,
      };
    },
    computed: {
      appId() {
        return this.$route.query.appId;
      },
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
        dataAccessAppApi
          .reference({ appId: this.appId })
          .then(res => {
            this.list = res.data;
            console.log('🚀 ~ this.filteredApiGroup', this.filteredApiGroup);
            if (!this.filteredApiGroup && this.list.length) {
              this.filteredApiGroup = this.list[0].apiId;
            }
            if (this.filteredApiGroup) {
              this.getInfoList();
              this.getAuthDate();
            } else {
              this.infoList = {};
              this.authDate = null;
            }
          })
          .finally(() => {
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
          .then(res => {
            this.infoList = res.data;
          })
          .finally(() => {
            this.loading.info = false;
          });
      },
      getAuthDate() {
        dataAccessAppApi
          .authDate({
            appId: this.appId,
            apiId: this.filteredApiGroup,
          })
          .then(res => {
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
