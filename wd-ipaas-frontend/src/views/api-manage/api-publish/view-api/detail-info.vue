<template>
  <div class="api-publish-view-detail-info">
    <div class="info-item">
      <span class="label-text">接口分类：</span>
      <span class="value-text">{{ baseInfo.apiGroupName }}</span>
    </div>

    <div class="info-item">
      <span class="label-text">接口类型：</span>
      <span class="value-text">
        {{ APIType[baseInfo.apiType] }}
        <el-button v-if="goFlow && baseInfo && baseInfo.apiType === 'LITE_FLOW'" type="text" @click="handleToFlow"
          >查看编排详情</el-button
        >
      </span>
    </div>

    <div class="info-item">
      <span class="label-text">接口状态：</span>
      <span class="value-text">{{ APIStatus.getDesc(baseInfo.dataAssetPublishStatus) }}</span>
    </div>

    <div class="info-item">
      <span class="label-text">接口名称：</span>
      <span class="value-text">{{ baseInfo.apiName }}</span>
    </div>

    <div class="info-item">
      <span class="label-text">请求方式：</span>
      <span class="value-text">{{ baseInfo.reqMethod }}</span>
    </div>

    <div v-if="apiType !== 'LITE_FLOW'" class="info-item">
      <span class="label-text">是否公开：</span>
      <span class="value-text">{{ isSecret(baseInfo.secret) }}</span>
    </div>

    <div class="info-item url">
      <span class="label-text">请求地址：</span>
      <span class="value-text">https://{{ url }}</span>
    </div>
    <div class="info-item">
      <span class="label-text">返回格式：</span>
      <span class="value-text">{{ baseInfo.responseContentType }}</span>
    </div>

    <!-- http 接口展示字段 -->
    <span v-if="apiType === 'EXTERNAL_HTTP'">
      <div class="info-item">
        <span class="label-text">响应超时时间：</span>
        <span class="value-text">{{ apiAttr.timeout ? `${apiAttr.timeout}毫秒` : apiAttr.timeout }}</span>
      </div>

      <div class="info-item">
        <span class="label-text">后端接口域名：</span>
        <span class="value-text">{{ apiAttr.host }}</span>
      </div>

      <div class="info-item">
        <span class="label-text">后端接口地址：</span>
        <span class="value-text">{{ apiAttr.path }}</span>
      </div>
    </span>

    <!-- 数据表接口展示字段 -->
    <span v-if="apiType === 'NORMAL_TABLE'">
      <div class="info-item">
        <span class="label-text">数据源：</span>
        <span class="value-text">{{ dataSource.connectionName }}</span>
      </div>

      <div class="info-item">
        <span class="label-text">数据表：</span>
        <span class="value-text">{{ baseInfo.dataAssetName }}</span>
      </div>
    </span>

    <!-- sql接口展示字段 -->
    <span v-if="apiType === 'CUSTOM_SQL'">
      <div class="info-item info-block">
        <span class="label-text">sql语句：</span>
        <span class="value-text">{{ baseInfo.apiSql }}</span>
      </div>
    </span>

    <!-- webservice接口展示字段 -->
    <span v-if="apiType === 'WEB_SERVICE'">
      <div class="info-item">
        <span class="label-text">后端接口域名：</span>
        <span class="value-text">{{ apiAttr.host }}</span>
      </div>

      <div class="info-item">
        <span class="label-text">后端接口地址：</span>
        <span class="value-text">{{ apiAttr.path }}</span>
      </div>

      <div class="info-item">
        <span class="label-text">响应超时时间：</span>
        <span class="value-text">{{ apiAttr.timeout ? `${apiAttr.timeout}毫秒` : apiAttr.timeout }}</span>
      </div>

      <div class="info-item">
        <span class="label-text">方法名：</span>
        <span class="value-text">{{ apiAttr.wsMethod }}</span>
      </div>

      <div class="info-item">
        <span class="label-text">命名空间：</span>
        <span class="value-text">{{ apiAttr.wsNameSpaceUri }}</span>
      </div>
    </span>

    <div class="info-item info-block">
      <span class="label-text">描述：</span>
      <span class="value-text">{{ baseInfo.apiDescription }}</span>
    </div>
  </div>
</template>
<script>
  import { ApiType as APIType, SecretStatus } from '@/utils/enum/api-manage.js';
  import { APIStatus } from '@/enum.js';

  export default {
    name: 'ApiManageApiPublishViewApiDetailInfo',
    props: {
      apiType: {
        type: String,
        default: '',
      },
      apiDetail: {
        type: Object,
        default: () => ({}),
      },
    },

    data() {
      return {
        APIStatus,
        SecretStatus,
        APIType,
      };
    },

    computed: {
      goFlow() {
        // 列表查看详情可跳转编排
        const curPage = this.$route.path;
        return curPage === '/api-publish/apipublish';
      },
      baseInfo() {
        return this.apiDetail.baseInfo || {};
      },
      apiAttr() {
        return this.apiDetail.baseInfo?.apiAttr || {};
      },

      dataSource() {
        return this.apiDetail.dataSource || {};
      },

      url() {
        let url = '';
        if (this.baseInfo && this.baseInfo.dataAssetApiUri) {
          url = window.location.host + this.baseInfo.dataAssetApiUri;
        }
        return url;
      },
    },

    methods: {
      isSecret(status) {
        return SecretStatus[status];
      },

      handleToFlow() {
        const routeUrl = this.$router.resolve({
          path: '/api-arrange-editor',
          query: {
            id: this.baseInfo.dataAssetApiId,
            readonly: true,
            resource: 'detail',
          },
        });
        window.open(routeUrl.href, '_blank');
      },
    },
  };
</script>

<style lang="less" scoped>
  @import './style.less';
  .api-publish-view-detail-info {
    background: #f3f6f8;
    padding: 20px 0;
    margin-bottom: 20px;
    .info-item {
      display: inline-block;
      width: 33.33%;
      margin-bottom: 10px;
      font-size: 12px;
      color: #333;

      .label-text {
        flex: none;
        display: inline-block;
        width: 110px;
        text-align: right;
        color: #8d939d;
      }
      .value-text {
        /deep/ .el-button--text {
          line-height: 1px;
          padding-left: 6px;
        }
      }
    }
    .url {
      width: 66.66%;
    }
    .info-block {
      display: block;
      width: 100%;
    }
  }

  .el-tabs {
    /deep/ .el-tabs__nav .el-tabs__active-bar {
      height: 1px;
    }
  }

  .title {
    font-weight: 600;
  }
</style>
