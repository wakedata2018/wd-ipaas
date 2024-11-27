<template>
  <div class="api-http">
    <div class="main-title">
      <span>请求参数</span>
      <el-button class="mock-btn" type="primary" @click="onClickMock">一键Mock</el-button>
    </div>
    <el-tabs v-model="activePane">
      <el-tab-pane label="请求头" name="head">
        <ApiRequestHeadTable
          ref="requestHeadForm"
          v-model="headParam"
          :disable-mock-list="disableMockList"
        ></ApiRequestHeadTable>
      </el-tab-pane>
      <el-tab-pane label="QUERY参数" name="query">
        <ApiRequestHeadTable
          ref="queryForm"
          v-model="queryParam"
          :type="TABLE_TYPE.QUERY"
          :disable-mock-list="disableMockList"
        ></ApiRequestHeadTable>
      </el-tab-pane>
      <el-tab-pane v-if="showBody" label="请求体" name="body">
        <ApiRequestBodyTable
          ref="bodyForm"
          :body-data="bodyData"
          :request-type="requestType"
          @change-type="handleChangeType"
        >
        </ApiRequestBodyTable>
      </el-tab-pane>
    </el-tabs>
    <div class="main-title">响应参数</div>
    <!-- API测试 -->
    <ApiResponseViewer
      :response-loading="responseLoading"
      :response-info="responseInfo"
      :request-type="requestType"
      @change-type="handleChangeType"
    />
  </div>
</template>

<script>
  import ApiRequestHeadTable from './api-request-head-table.vue';
  import ApiRequestBodyTable from './api-request-body-table.vue';
  import ApiResponseViewer from './api-response-viewer.vue';
  import { TABLE_TYPE, BODY_TYPE } from '../constants';

  import MockApi from '@/api/mock-api';
  import { MockSetup, loopFindChildMockRule } from '@/utils/mock.js';
  import getObjectVal from 'lodash/get';
  import { createMockRequestTemp } from '@/utils/create-mock-template';
  import { eventBus } from '@/utils/event-bus';

  export default {
    components: { ApiRequestBodyTable, ApiRequestHeadTable, ApiResponseViewer },
    props: {
      parameters: {
        type: Array,
        default: () => [],
      },
      /**
       * 是否显示请求体
       */
      showBody: {
        type: Boolean,
        default: false,
      },
      /**
       * 请求体参数
       */
      bodyData: {
        type: Object,
        default: () => ({}),
      },
      /**
       * API测试响应返回值
       */
      responseInfo: {
        type: [Object, String],
        default: null,
      },
      /**
       * 单选框类型 'default' | 'json' | 'xml'
       */
      requestType: {
        type: String,
        default: '',
      },
      responseLoading: {
        type: Boolean,
        default: false,
      },
      disableMockList: {
        type: Array,
        default: () => [],
      },
    },
    data() {
      return {
        TABLE_TYPE,
        activePane: 'query',
      };
    },
    computed: {
      /**
       * 请求头参数
       */
      headParam: {
        get() {
          return this.parameters?.filter(item => item.httpParamKind === 'HEAD') ?? [];
        },
        set(val) {
          const newVal = [...this.queryParam, ...val];
          this.$emit('update:parameters', newVal);
        },
      },
      /**
       * QUERRY参数
       */
      queryParam: {
        get() {
          return (
            this.parameters?.filter(item => item.httpParamKind === 'QUERY' || item.httpParamKind === 'FILTER') ?? []
          );
        },
        set(val) {
          const newVal = [...this.headParam, ...val];
          this.$emit('update:parameters', newVal);
        },
      },
    },
    watch: {
      showBody: {
        handler(val) {
          if (!val) {
            this.activePane = 'query';
          }
        },
      },
    },
    methods: {
      handleChangeType(val) {
        const idx = this.headParam.findIndex(i => i.assetColumns === 'Content-Type');
        this.$set(this.headParam[idx], 'sample', val === BODY_TYPE.XML ? 'application/xml' : 'application/json');
        this.$emit('change-type', val);
      },

      async validateEmpty() {
        return !!(
          (await this.$refs.requestHeadForm.validate()) &&
          (await this.$refs.queryForm.validate()) &&
          (this.showBody ? await this.$refs.bodyForm.validate() : true)
        );
      },
      async validate(fn) {
        fn(await this.validateEmpty());
      },
      async onClickMock() {
        // 构造mock参数模板
        const headTemp = createMockRequestTemp(this.headParam);
        const queryTemp = createMockRequestTemp(this.queryParam);
        const bodyParam =
          this.bodyData.tree.root.type === 'object' ? this.bodyData.tree.root.properties : this.bodyData.tree;
        const bodyTemp = createMockRequestTemp(bodyParam);
        // 记录请求参数每个Tab页参数是否配置Mock规则
        const hasMockRule = {
          head: false,
          query: false,
          body: false,
        };
        // 注册mock接口
        MockSetup(headTemp, 'head');
        MockSetup(queryTemp, 'query');
        MockSetup(bodyTemp, 'body');

        // 调用MockJs获取模拟数据
        const headRes = await MockApi.getMockData('head');
        const queryRes = await MockApi.getMockData('query');
        const bodyRes = await MockApi.getMockData('body');

        /**
         * 数据回显到参数值
         * 请求头和Query
         * @param {data} 行数据
         * @param {paramObj} 参数值
         * @param {module} 请求头/Query
         */
        const setSample = (data, paramObj, type) => {
          Object.entries(paramObj).forEach(item => {
            const name = item[0];
            const rule = item[1];
            const currentItemIndex = data.findIndex(i => i.assetColumns === name);
            const inDisableMockList = this.disableMockList.includes(name);
            if (currentItemIndex !== -1 && !inDisableMockList && rule) {
              this.$set(data[currentItemIndex], 'sample', rule);
              hasMockRule[type] = true;
            }
          });
        };
        // 请求体
        const setVal = (datSource, paramObj) => {
          const loop = (data, parentPath) => {
            Object.values(data).forEach(item => {
              if (item.type === 'object') {
                loop(item.properties, [...parentPath, item.name]);
              } else {
                // 数组类型是否开启Mock
                let hasMockRuleChild = false;
                // 数组类型
                if (item.type === 'array') {
                  hasMockRuleChild = loopFindChildMockRule(item, hasMockRuleChild);
                }
                const itemPath = [...parentPath, item.name];
                const rule = parentPath.length ? getObjectVal(paramObj, itemPath) : paramObj[item.name];
                const ruleVal = Array.isArray(rule) ? JSON.stringify(rule) : rule;
                if (ruleVal || typeof ruleVal === 'boolean') {
                  // 非数组类型直接赋值给value
                  // 数组类型则要判断子节点是否有设置Mock规则，设置了则开启Mock
                  if (item.type !== 'array' || hasMockRuleChild) {
                    this.$set(item, 'value', ruleVal);
                    hasMockRule.body = true;
                  }
                }
              }
            });
          };
          loop(datSource, []);
        };
        setSample(this.headParam, headRes.data, 'head');
        setSample(this.queryParam, queryRes.data, 'query');
        setVal(bodyParam, bodyRes.data);
        /** 未配置任何mock规则 */
        if (!hasMockRule[this.activePane]) {
          return this.$message.error('Mock规则未配置');
        }
      },
    },
  };
</script>

<style lang="less" scoped>
  .api-http {
    .main-title {
      height: 22px;
      font-size: 16px;
      margin-top: 30px;
      font-weight: 600;
      color: rgba(51, 51, 51, 1);
      line-height: 22px;

      display: flex;
      justify-content: space-between;
      align-items: center;

      .mock-btn {
        margin-right: 10px;
      }
    }

    .empty {
      text-align: center;
      display: block;
      color: #909399;
      font-size: 12px;
    }

    .btn-box {
      margin-top: 10px;
    }
  }
</style>
