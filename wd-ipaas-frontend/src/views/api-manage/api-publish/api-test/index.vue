<template>
  <el-dialog
    v-loading="loading"
    fullscreen
    custom-class="anim-left"
    lock-scroll
    append-to-body
    class="bd-dialog api-test-dialog"
    title="API测试"
    :visible="visible"
    :close-on-click-modal="false"
    @open="handleOpen"
    @close="handleClose"
  >
    <el-scrollbar ref="scrollbar" class="page-scrollbar">
      <div class="scrollbar-content">
        <template v-if="baseInfo">
          <div v-if="isPublicInterface">
            <span class="api-title">【{{ baseInfo.apiGroupName }}】{{ baseInfo.apiName }}</span>
            <el-button v-if="apiDetail.apiDocUrl" class="api-url" type="text" @click="handleClick">
              查看接口文档
            </el-button>
          </div>
          <div style="margin-top: 10px">
            <span class="api-method" :style="{ backgroundColor: METHOD_COLOR[baseInfo.reqMethod] }">
              {{ baseInfo.reqMethod }}
            </span>
            <el-tooltip content="点击复制接口地址" placement="top" effect="light">
              <span class="api-link" @click="copyLink(baseInfo.dataAssetApiMethod)">{{
                baseInfo.dataAssetApiMethod
              }}</span>
            </el-tooltip>
          </div>
        </template>
        <el-form :model="appForm" class="app">
          <el-form-item label="测试应用">
            <el-select v-model="appForm.appKey" placeholder="请选择测试应用">
              <el-option
                v-for="item in appOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-form>
        <api-http-test
          v-if="apiDetail"
          ref="testForm"
          :show-body="apiInfo.reqMethod === 'POST'"
          :request-type="requestType"
          :body-data.sync="bodyData"
          :parameters.sync="parameters"
          :response-info="responseInfo"
          :response-loading="responseLoading"
          :disable-mock-list="disableMockList"
          @change-type="onChangeType"
        >
        </api-http-test>
      </div>
    </el-scrollbar>
    <div class="bd-dialog-footer">
      <el-button size="medium" class="cancel" @click="handleClose">取消</el-button>
      <el-button type="primary" size="medium" class="bd-button" @click="onTest">测试</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import cloneDeep from 'lodash/cloneDeep';
  import vkbeautify from 'vkbeautify';
  import apiTest from '@/api/api-test';
  import { schema2json } from '@/utils/schema2json';
  import { XML } from '@/utils/xmljson';
  import textUtils from '@/utils/text-utils.js';
  import ApiHttpTest from './api-http-test/index.vue';
  import { BODY_TYPE, METHOD_COLOR } from './constants';
  import accessApi from '@/api/app-access-times.js';
  import { SecretEnum } from '@/utils/enum/api-manage.js';

  const defaultJsonSchema = '{"root":{"type":"object","name":"root","properties":{}}}';

  export default {
    name: 'ApiTest',
    components: { ApiHttpTest },
    props: {
      apiInfo: {
        type: Object,
        default() {
          return {};
        },
      },
      visible: {
        type: Boolean,
        default: false,
      },
      isArrangePage: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        METHOD_COLOR,
        responseInfo: null, // 响应返回参数
        loading: false,
        responseLoading: false,
        apiDetail: null, // Api详情
        parameters: [], // HEAD、QUERY的参数
        apiParameters: [], // 切换测试应用 重新获取的请求头和QUERY集合
        requestType: BODY_TYPE.DEFAULT, // 单选框类型 'default' | 'json' | 'xml'
        appOptions: [], // 测试应用
        appForm: {
          appKey: '', // 测试应用Id
        },
        disableMockList: [],
        SecretEnum, // 接口加密类型
      };
    },

    computed: {
      /**
       * 公开接口
       */
      isPublicInterface() {
        return this.baseInfo.secret === SecretEnum.public;
      },
      baseInfo() {
        return this.apiDetail?.baseInfo;
      },
      /**
       * 所有参数
       */
      allParameters() {
        return this.apiDetail?.requiredInput.concat(this.apiDetail?.optionalInput);
      },
      /**
       * 请求头参数
       */
      headParams() {
        return this.parameters
          .filter(item => item.httpParamKind === 'HEAD')
          .reduce((pre, cur) => {
            if (cur.sample !== '' && cur.sample != null) {
              return { ...pre, [cur.assetColumns]: encodeURI(cur.sample) };
            }
            return pre;
          }, {});
      },
      /**
       * Query请求参数
       */
      queryParams() {
        return this.parameters?.filter(item => item.httpParamKind === 'QUERY') ?? [];
      },
      /**
       * Body请求参数改造
       */
      bodyData() {
        const postBody = this.allParameters?.find(item => item.httpParamKind === 'BODY') ?? {
          jsonSchema: defaultJsonSchema,
        };
        const tree = JSON.parse(postBody.jsonSchema);
        const jsonVal = tree.root.type === 'object' ? tree.root.properties : tree.root;
        const json = schema2json(jsonVal);
        const xotree = new XML.ObjTree();
        return {
          tree,
          jsonStr: vkbeautify.json(JSON.stringify(json)),
          xmlStr: vkbeautify.xml(xotree.writeXML(json)),
        };
      },
    },
    watch: {
      'appForm.appKey': {
        handler(val) {
          this.getParameters();
        },
      },
    },
    methods: {
      async getParameters() {
        // 测试选择应用变动
        const res = await apiTest.chooseTestAppKey({
          appKey: this.appForm.appKey,
        });

        const appParameters = Object.values(res.data).reduce((pre, cur) => {
          return [...pre, ...cur];
        }, []);

        // 接口默认参数和测试应用返回的默认参数均不可Mock
        const getDisableMockList = () => {
          const otherParameters = this.apiDetail.requiredInput.filter(item => {
            return item.defaultInputParam;
          });
          return [...otherParameters, ...appParameters].map(item => item.assetColumns);
        };

        this.disableMockList = getDisableMockList();

        // 接口参数和默认选中应用参数集合
        this.parameters = [...this.apiParameters, ...appParameters];
      },
      handleClose() {
        this.apiDetail = null;
        this.$emit('update:visible', false);
      },
      handleOpen() {
        this.responseInfo = null;
        this.requestType = BODY_TYPE.DEFAULT;
        if (this.apiInfo) {
          this.init();
        }
      },
      handleClick() {
        window.open(this.apiDetail.apiDocUrl, '_blank');
      },
      async init() {
        try {
          this.loading = true;
          const { data } = await apiTest.getTestInfo({
            dataAssetId: this.apiInfo.dataAssetApiId,
            timestamp: Date.now(),
            isApiTest: true,
          });

          this.apiDetail = data;
          // 接口 HEAD、QUERY参数集合
          this.apiParameters = this.allParameters?.filter(item => item.httpParamKind !== 'BODY') ?? [];
          // 获取测试应用列表
          await this.getAppList();
          // 默认选中第一个应用
          if (this.appOptions.length) {
            this.appForm.appKey = this.appOptions[0].value;
            await this.getParameters();
          }
        } catch (error) {
        } finally {
          this.loading = false;
        }
      },

      onChangeType(val) {
        this.requestType = val;
        this.responseInfo = null;
      },

      onTest(hideLoading) {
        const filterList = this.allParameters.filter(o => o.httpParamKind === 'FILTER');
        const query = this.mergeParameters(this.queryParams);
        const filter = this.mergeParameters(filterList);
        const queryStr = textUtils.handlerQuery(this.apiDetail.baseInfo.dataAssetApiUri ?? '', { ...query, ...filter });
        const { xmlStr, jsonStr, tree } = this.bodyData;
        let type = this.requestType;
        const headParams = cloneDeep(this.headParams);

        this.$refs.testForm.validate(async valid => {
          if (!valid) {
            return;
          }

          const jsonVal = tree.root.type === 'object' ? tree.root.properties : tree.root;

          const bodyParams =
            this.requestType === BODY_TYPE.DEFAULT
              ? JSON.stringify(schema2json(jsonVal))
              : this.requestType === BODY_TYPE.XML
              ? xmlStr
              : jsonStr;

          type = type !== BODY_TYPE.XML ? BODY_TYPE.JSON : type;
          this.responseInfo = null; // 点击测试按钮需要清空响应数据

          headParams['Content-Type'] = type === BODY_TYPE.XML ? 'application/xml' : 'application/json';

          try {
            !hideLoading && (this.loading = true);
            this.responseLoading = true;
            const { data } = await apiTest.request(this.apiInfo.reqMethod || '', queryStr, bodyParams, headParams);
            if (this.isArrangePage) {
              this.$emit('tested', data);
            }
            this.responseInfo = { type, data };
          } catch (error) {
            console.error(error);
          } finally {
            this.loading = false;
            this.responseLoading = false;
          }
        });
      },
      copyLink(content) {
        const ele = document.createElement('input'); // 创建一个input标签
        ele.setAttribute('value', content); // 设置改input的value值
        document.body.appendChild(ele); // 将input添加到body
        ele.select(); // 获取input的文本内容
        document.execCommand('copy'); // 执行copy指令
        document.body.removeChild(ele);
        this.$message.success('复制成功');
      },
      /**
       * 合并参数
       */
      mergeParameters(list) {
        let query = {};
        for (const obj of list) {
          if (obj.assetDatatype === 'json' && obj.httpParamKind !== 'QUERY') {
            query = { ...query, ...JSON.parse(obj.sample) };
          } else {
            if (obj.sample !== '' && obj.sample != null) {
              query = { ...query, [obj.assetColumns]: obj.sample };
            }
          }
        }
        return query;
      },
      /**
       * 测试应用列表
       */
      async getAppList() {
        const res = await accessApi.getAppList();
        this.appOptions = res.data.map(item => {
          return {
            label: item.dataAccessAppName,
            value: item.dataAccessKey,
          };
        });
      },
    },
  };
</script>

<style scoped lang="less">
  .api-test-dialog {
    .page-scrollbar {
      overflow-x: hidden;
      overflow-y: hidden;

      /deep/ .el-scrollbar__wrap {
        overflow-x: hidden;
        max-height: calc(100vh - 210px);
      }

      .scrollbar-content {
        margin-bottom: 20px;

        .app {
          padding: 10px 0px;
        }

        ::v-deep .api-info {
          .el-form-item {
            padding-right: 100px;
          }

          .el-form-item__label {
            font-weight: 800;
          }
        }
      }

      .api-title {
        height: 22px;
        font-size: 16px;
        color: #333333;
        line-height: 22px;
        font-weight: 500;
      }

      .api-url {
        margin-left: 16px;
      }

      .api-method {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        height: 24px;
        margin-right: 8px;
        padding: 0 8px;
        font-size: 12px;
        line-height: 24px;
        font-weight: 700;
        border-radius: 4px;
        color: #fff;
      }

      .api-link {
        display: inline-block;
        cursor: pointer;
        height: 24px;
        line-height: 24px;

        &:hover {
          border-bottom: 1px dashed #ccc;
        }

        &:active {
          opacity: 0.6;
        }
      }
    }
  }
</style>
