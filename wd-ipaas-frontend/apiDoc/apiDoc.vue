<template>
  <div class="api-doc">
    <!-- 文档头部 -->
    <api-doc-header :info="info" />
    <!-- 文档内容 -->
    <div class="doc-content">
      <!-- 导航 -->
      <side-menu :menu-list="apiMenuList" :active="defaultActive" />
      <el-empty v-if="errorMessage" :description="errorMessage" :image="emptyImg"></el-empty>

      <!-- 展示静态页面 -->
      <component
        :is="STATIC_PAGE_VIEW[defaultActive]"
        v-if="!errorMessage && isStaticPage"
        id="api"
        class="doc-content-api"
      />

      <!-- 接口详情 -->
      <div v-if="!errorMessage && !isStaticPage" id="api" class="doc-content-api">
        <h1 id="header" class="header-message-title">{{ apiName }}</h1>
        <div class="header-message-content">
          <div class="desc">接口基本介绍：{{ apiDesc }}</div>
        </div>
        <h2 class="submodule-message-title">公共参数</h2>
        <div class="submodule-message-content">
          <div class="sub-wrap">
            <div class="sub-wrap__title">请求地址：</div>
            <el-table :data="requestUrl" header-row-class-name="table-header" border>
              <el-table-column prop="env" label="环境" width="150"> </el-table-column>
              <el-table-column prop="reqMethod" label="请求方式" width="150"> </el-table-column>
              <el-table-column prop="httpsUrl" label="HTTPS地址"> </el-table-column>
            </el-table>
          </div>
          <div class="sub-wrap">
            <div class="sub-wrap__title">公共请求参数：</div>
            <el-table :data="commonParams.requiredInput" header-row-class-name="table-header" border>
              <el-table-column prop="assetColumns" label="参数名称"> </el-table-column>
              <el-table-column prop="assetDatatype" label="参数类型"> </el-table-column>
              <el-table-column prop="httpParamKind" label="请求类型"> </el-table-column>
              <el-table-column prop="required" label="是否必须">
                <template #default="scope">
                  {{ scope.row.required ? '是' : '否' }}
                </template>
              </el-table-column>
              <el-table-column prop="descriptions" label="参数说明"> </el-table-column>
            </el-table>
          </div>
          <div class="sub-wrap">
            <div class="sub-wrap__title">公共响应参数：</div>
            <el-table :data="commonParams.returnOutput" header-row-class-name="table-header" border>
              <el-table-column prop="attributeName" label="参数名称"> </el-table-column>
              <el-table-column prop="attributeType" label="类型"> </el-table-column>
              <el-table-column prop="attributeDescribe" label="参数说明"> </el-table-column>
            </el-table>
          </div>
        </div>
        <h2 id="requestParams" class="submodule-message-title">请求参数</h2>
        <el-table
          :data="requestParams"
          header-row-class-name="table-header"
          row-key="id"
          default-expand-all
          :tree-props="{ children: 'children' }"
          border
        >
          <el-table-column prop="assetColumns" label="参数名称" min-width="100"> </el-table-column>
          <el-table-column prop="assetDatatype" label="类型" width="110"> </el-table-column>
          <el-table-column prop="httpParamKind" label="请求类型" width="110"> </el-table-column>
          <el-table-column prop="required" label="是否必须" width="80">
            <template #default="scope">
              {{ scope.row.required ? '是' : '否' }}
            </template>
          </el-table-column>
          <el-table-column prop="sample" label="示例值"> </el-table-column>
          <el-table-column prop="description" label="描述"> </el-table-column>
        </el-table>
        <h2 id="responseParams" class="submodule-message-title">响应参数</h2>
        <el-table :data="outPutParams" header-row-class-name="table-header" row-key="id" default-expand-all border>
          <el-table-column prop="assetColumns" label="参数名称" min-width="80"> </el-table-column>
          <el-table-column prop="assetDatatype" label="类型" width="110"> </el-table-column>
          <el-table-column prop="description" label="描述"> </el-table-column>
        </el-table>
        <h2 v-if="requestSample" id="requestSample" class="submodule-message-title">请求示例</h2>
        <api-json-viewer v-if="requestSample" :value="requestSample" />
        <h2 id="responseSample" class="submodule-message-title">响应示例</h2>
        <api-json-viewer :value="responseOrErrorSample(sampleType.RESPON)" />
        <h2 id="errorSample" class="submodule-message-title">异常示例</h2>
        <api-json-viewer :value="responseOrErrorSample(sampleType.ERROR)" />
      </div>
      <!-- 滚动目录树 -->
      <api-side-catalog v-if="!errorMessage" :show="ready" />
    </div>
  </div>
</template>

<script>
  import apiDoc from './apis.js';
  import { ApiJsonViewer, ApiSideCatalog, ApiDocHeader, SideMenu } from './components';
  import { QuickStartPage, InterfaceCallPage, AccessTokenPage } from './views';
  import { GUIDE_DATA } from './views/guide/data';
  import { SAMPLE_TYPE, STATIC_PAGE_ID } from './enums.js';
  import { jsonSchemaToRequestParams } from './utils.js';
  import emptyImg from '../common/images/404.png';

  import SettingApi from '@/api/setting';

  export default {
    components: {
      ApiSideCatalog,
      ApiDocHeader,
      SideMenu,
      ApiJsonViewer,
      QuickStartPage,
      InterfaceCallPage,
    },
    data() {
      return {
        STATIC_PAGE_VIEW: {
          [STATIC_PAGE_ID.INTERFACE_CALL]: InterfaceCallPage,
          [STATIC_PAGE_ID.ACCESS_TOKEN]: AccessTokenPage,
        },
        /**
         * 公共参数
         */
        commonParams: {
          requiredInput: null, // 公共请求参数
          returnOutput: null, // 公共响应参数
        },
        /**
         * 接口目录树
         */
        apiMenuList: [],
        /**
         * 当前激活页id
         */
        defaultActive: '',
        /**
         * 接口详情
         */
        apiDetail: null,
        /**
         * 等待接口内容加载完毕 再加载滑动目录组件
         */
        ready: false,
        sampleType: SAMPLE_TYPE,
        /**
         * 错误提示
         */
        errorMessage: '',
        emptyImg,
        info: {
          systemName: '', // 系统名称
          miniLogoUrl: '', // logo
        },
      };
    },
    computed: {
      /**
       * 请求方法
       */
      reqMethod() {
        return this.apiDetail?.dataAssetApi?.reqMethod ?? '';
      },
      /**
       * 接口名称
       */
      apiName() {
        return (this.apiDetail && this.apiDetail?.dataAssetApi?.apiName) || '';
      },
      /**
       * 接口介绍
       */
      apiDesc() {
        return (this.apiDetail && this.apiDetail?.dataAssetApi?.apiDescription) ?? '暂无描述';
      },
      /**
       * 请求接口地址
       */
      requestUrl() {
        const dataAssetApiMethod = this.apiDetail?.dataAssetApi?.dataAssetApiMethod ?? '';
        const domainName = this.apiDetail?.domainName ?? '';
        const requestData = [
          {
            env: '正式环境',
            reqMethod: this.reqMethod,
            httpsUrl: `${domainName}${dataAssetApiMethod}`,
          },
        ];
        return requestData;
      },
      /**
       * 请求参数
       */
      requestParams() {
        return (this.apiDetail?.parameters || []).map(item => {
          try {
            let jsonSchemaChild = [];
            if (item.jsonSchema) {
              const obj = JSON.parse(item.jsonSchema)?.root;
              // 对象类型
              if (obj.type === 'object') {
                jsonSchemaChild = jsonSchemaToRequestParams(obj);
              } else if (obj.type === 'array' && obj.items?.properties) {
                // 对象数组
                jsonSchemaChild = jsonSchemaToRequestParams(obj.items);
              }
            }
            return {
              ...item,
              description: item.descriptions,
              sample: !item.jsonSchema && item.sample,
              children: jsonSchemaChild,
            };
          } catch (error) {
            return item;
          }
        });
      },
      /**
       * 响应参数
       */
      outPutParams() {
        return (this.apiDetail?.responseParams || []).map(item => {
          return {
            ...item,
            children:
              (item.responsePostData && jsonSchemaToRequestParams(JSON.parse(item.responsePostData)?.root ?? null)) ||
              [],
          };
        });
      },

      /**
       * 响应示例
       */
      resultSample() {
        return this.apiDetail?.responseParamsExample;
      },
      /**
       * 响应示例json
       */
      resultJsonSample() {
        return JSON.parse(this.resultSample?.responseExample ?? '{}');
      },
      /**
       * 响应示例xml
       */
      resultXmlSample() {
        return this.resultSample?.responseExampleXml ?? '<xml></xml>';
      },
      /**
       * 异常示例
       */
      errorSample() {
        return this.apiDetail?.errorExample;
      },

      /**
       * 异常示例json
       */
      errorJsonSample() {
        return JSON.parse(this.errorSample?.errorExampleJson ?? '{}');
      },

      /**
       * 异常示例xml
       */
      errorXmlSample() {
        return this.errorSample?.errorExampleXml ?? '<xml></xml>';
      },
      /**
       * 请求示例json
       */
      requestJsonSample() {
        return JSON.parse(this.apiDetail?.parametersExample?.requestExample ?? '{}');
      },
      /**
       * 请求示例xml
       */
      requestXmlSample() {
        return this.apiDetail?.parametersExample?.requestExampleXml ?? null;
      },
      /**
       * 请求示例 POST请求才显示请求示例
       */
      requestSample() {
        if (this.reqMethod === 'POST') {
          return [this.requestJsonSample, this.requestXmlSample];
        }
        return null;
      },
      /**
       * Api接口详情
       */
      apiAttr() {
        return this.apiDetail?.dataAssetApi?.apiAttr ?? '';
      },
      /**
       * 响应示例 type = 0或 异常示例
       */
      responseOrErrorSample() {
        return (type = this.sampleType.RESPON) => {
          const result =
            type === this.sampleType.RESPON
              ? [this.resultJsonSample, this.resultXmlSample]
              : [this.errorJsonSample, this.errorXmlSample];
          return result;
        };
      },

      /**
       * 是否是静态页面
       */
      isStaticPage() {
        return Object.values(STATIC_PAGE_ID).includes(this.defaultActive);
      },
    },
    watch: {
      // 路由变化时触发  首次加载也会触发
      $route: {
        async handler() {
          await this.init();
        },
      },
    },
    async created() {
      await this.getApiMenu();
      await this.init();
      await this.getNameAndLogoUrl();
      window.document.title = this.info.systemName + '·API文档';
    },
    mounted() {
      // 修改全局样式
      document.body.classList.add('scollStyle');
    },
    beforeDestroy() {
      document.body.classList.remove('scollStyle');
    },
    methods: {
      async getNameAndLogoUrl() {
        const { systemDetailInfo } = await SettingApi.fetchSetting();
        this.info = systemDetailInfo;
      },
      /**
       * 获取公共参数
       */
      async getCommomParams(id) {
        const {
          data: { requiredInput, returnOutput },
        } = await apiDoc.getApiCommonParams({
          dataAssetId: id,
        });
        this.commonParams = {
          requiredInput,
          returnOutput,
        };
      },
      /**
       * 获取接口分类列表
       */
      async getApiMenu() {
        try {
          const { data } = await apiDoc.getApiClassify();
          this.apiMenuList = data.filter(item => item.apiList.length);
          this.apiMenuList.unshift(GUIDE_DATA); // 手动插入一条‘接入指南’菜单
          this.saveMenuData();
        } catch (err) {
          console.error(err);
        }
      },

      /**
       * 保存菜单数据至vuex
       */
      saveMenuData() {
        this.$store.commit('setMenuList', this.apiMenuList);
        const menuPathList = [];
        this.apiMenuList.forEach(item => {
          item.apiList.forEach(child => {
            menuPathList.push(child.dataAssetApiId);
          });
        });
        this.$store.commit('setMenuPathList', menuPathList);
      },

      /**
       * 初始化数据
       */
      async init() {
        this.ready = false;
        const { path } = this.$route;
        const id = path.slice(1);
        this.defaultActive = id || STATIC_PAGE_ID.INTERFACE_CALL;
        if (this.isStaticPage) {
          this.$nextTick(() => {
            this.ready = true;
            this.errorMessage = '';
          });
          return;
        }
        try {
          await this.getApiDetail(this.defaultActive);
          await this.getCommomParams(this.defaultActive);
          this.ready = true;
          this.errorMessage = '';
        } catch (err) {
          if (err.code === '220002') {
            this.errorMessage = '暂无数据';
          } else {
            this.errorMessage = '抱歉，您访问的页面走丢了';
          }
          console.error(err);
        }
      },
      /**
       * 获取接口详情
       */
      async getApiDetail(id) {
        const { data } = await apiDoc.getApiDetail({
          dataAssetApiId: id,
        });
        this.apiDetail = data;
      },
    },
  };
</script>
<style scoped lang="less">
  /deep/ .el-table--border {
    border: 1px solid #ebeef5;
  }
  .api-doc {
    ::v-deep .el-table th.el-table__cell {
      opacity: 0.48;
      background: #f5f7fa;
      color: #303133;
    }

    .doc-content {
      display: flex;
      position: relative;
      top: 60px;
      width: 100%;

      &-api {
        flex: 1;
        padding: 0px 64px;
        margin-left: 312px;
        overflow: hidden;

        .header-message-title {
          font-size: 30px;
          color: #303133;
          line-height: 38px;
          margin-top: 44px;
        }

        .header-message-content {
          .authorize {
            color: #1b99ff;
            line-height: 14px;
            margin: 16px 0px;
          }

          .desc {
            color: #606266;
          }
        }

        .submodule-message-title {
          font-weight: 500;
          font-size: 24px;
          color: #303133;
          line-height: 32px;
        }

        .submodule-message-content {
          .sub-wrap {
            &__title {
              font-size: 16px;
              color: #303133;
              line-height: 32px;
              margin: 16px 0px;
            }
          }
        }
      }
      .el-empty {
        flex: 1;
        padding: 0px 64px;
        margin: 200px 0px 0px 312px;
        overflow: hidden;
      }
    }
  }
</style>
<!-- eslint-disable-next-line wkvue/no-style-scoped -->
<style>
  .scollStyle {
    overflow-y: scroll !important;
    background: #fff !important;
  }
</style>
