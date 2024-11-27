<template>
  <el-dialog
    :visible="visible"
    append-to-body
    class="select-method"
    width="960px"
    :close-on-click-modal="false"
    @close="handleClose"
    @open="handleOpen"
  >
    <span slot="title">
      <div class="title">表达式编辑器</div>
    </span>

    <div class="select-method__content">
      <div class="method-list content__list">
        <div class="list-title">函数列表</div>
        <div class="list-search">
          <el-input v-model="search" class="param-value__input" placeholder="请输入函数名" clearable></el-input>
        </div>
        <div class="list-content">
          <el-tree
            ref="tree"
            class="filter-tree"
            accordion
            :data="methods"
            :props="defaultProps"
            :filter-node-method="filterNode"
            @node-click="handleNodeClick"
          >
            <template #default="{ data }">
              <span class="custom-tree-node">
                {{ data.method }}
              </span>
            </template>
          </el-tree>
        </div>
      </div>

      <div class="method-expression content__list">
        <div class="list-title">表达式</div>
        <div class="list-content">
          <textarea ref="expression" class="expression-text" rows="3" autofocus />
        </div>

        <div v-if="selectedMethod" class="list-extra">
          <template v-if="selectedMethod.type === SHOW_TYPE.METHOD">
            <div class="list-item list-item--bold">函数介绍</div>
            <div class="list-item"><span>函数名称：</span>{{ selectedMethod.name }}</div>
            <div class="list-item"><span>函数描述：</span>{{ selectedMethod.description }}</div>
            <div v-if="selectedMethod.paramDesc" class="list-item">
              <span>参数说明：</span>
              <div v-html="selectedMethod.paramDesc"></div>
            </div>
            <div class="list-item"><span>返回值类型：</span>{{ selectedMethod.returnType }}</div>
            <div v-if="selectedMethod.example" class="list-item">
              <span>使用示例：</span>
              <div v-html="selectedMethod.example"></div>
            </div>
          </template>
          <template v-if="selectedMethod.type === SHOW_TYPE.PARAM">
            <div class="list-item list-item--bold">接口介绍</div>
            <div class="list-item"><span>接口分组：</span>{{ selectedMethod.group }}</div>
            <div class="list-item"><span>接口名称：</span>{{ selectedMethod.name }}</div>
            <div v-if="selectedMethod.description" class="list-item">
              <span>接口描述：</span>{{ selectedMethod.description }}
            </div>
            <div v-if="selectedMethod.url" class="list-item">
              <a class="list-url" :href="selectedMethod.url" target="_blank">查看接口文档</a>
            </div>
          </template>
        </div>
      </div>

      <div v-if="showParamList" class="method-params content__list">
        <div class="list-title">参数列表</div>
        <div class="list-content">
          <el-tree
            class="filter-tree"
            accordion
            :data="list"
            :props="defaultParamsProps"
            @node-click="handleParamsClick"
          >
            <template #default="{ data }">
              <span class="custom-tree-node ellipsis" :title="descTooltip(data)">
                {{ data.label }}
                <span v-if="data.dataType" class="custom-tree-type"> &lt; {{ data.dataType }} &gt; </span>
                <span v-if="data.desc" class="custom-tree-type"> &lt; {{ data.desc }} &gt; </span>
              </span>
            </template>
          </el-tree>
        </div>
      </div>
    </div>
    <span slot="footer">
      <div class="dialog-footer">
        <el-button type="plain" @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleConfirm">确定</el-button>
      </div>
    </span>
  </el-dialog>
</template>
<script>
  import api from '@/api/api-method';
  import apiController from '@/api/api-controller';
  import { ApiType } from '@/utils/enum';

  const SHOW_TYPE = {
    METHOD: 'method',
    PARAM: 'param',
  };

  export default {
    name: 'SelectMethodDialog',
    props: {
      visible: {
        type: Boolean,
        default: false,
      },
      expression: {
        type: String,
        default: '',
      },
      operatorId: {
        type: String,
        default: null,
      },
      taskInfo: {
        type: Object,
        default: () => ({}),
      },
      showParamList: {
        type: Boolean,
        default: true,
      },
      /**
       * 获取算子列表的Query参数
       */
      responseQueryParams: {
        type: String,
        default: '',
      },
    },
    data() {
      return {
        SHOW_TYPE,
        methods: [],
        list: [],
        search: '',
        selectedMethod: null,
        defaultProps: {
          children: 'list',
          label: 'method',
        },
        defaultParamsProps: {
          children: 'childApiRespParams',
          label: 'label',
        },
      };
    },
    computed: {
      descTooltip() {
        return data => {
          if (data.desc) {
            return `${data.label} < ${data.desc} >`;
          }
          return `${data.label} < ${data.dataType} >`;
        };
      },
    },
    watch: {
      search(val) {
        this.$refs.tree.filter(val);
      },
    },
    mounted() {
      this.handleGetAllMethod();
    },
    methods: {
      formatParams(list) {
        const header = list.filter(item => item.type === 'HEAD');
        const query = list.filter(item => item.type === 'QUERY');
        const body = list.find(item => item.type === 'BODY');
        const data = [];

        const loop = paramsList => {
          return paramsList.map(item => {
            return {
              ...item,
              label: item.assetColumns,
              dataType: item.assetDataType,
              childApiRespParams: loop(item.childApiRespParams || []),
            };
          });
        };

        if (header.length) {
          data.push({
            label: 'HEAD',
            childApiRespParams: loop(header),
          });
        }

        if (query.length) {
          data.push({
            label: 'QUERY',
            childApiRespParams: loop(query),
          });
        }

        if (body) {
          // 获取根节点数据类型 根节点为数组类型则根节点可选
          const rootDataType = JSON.parse(body.responsePostData)?.root?.type;
          const isArrayRoot = rootDataType === 'array';
          data.push({
            ...body,
            label: 'BODY',
            dataType: isArrayRoot ? 'array<object>' : 'object',
            disable: !isArrayRoot,
            childApiRespParams: loop(body.childApiRespParams),
          });
        }

        return data;
      },
      filterNode(value, data) {
        if (!value) {
          return true;
        }
        return data.method.indexOf(value) !== -1;
      },
      handleNodeClick(data) {
        this.$refs.expression.focus();
        if (data.list) {
          return;
        }
        this.selectedMethod = {
          type: SHOW_TYPE.METHOD,
          name: data.method + data.param,
          description: data.description,
          returnType: data.returnType,
          example: (data.example || '').replaceAll('\n', '<br />'),
          paramDesc: (data.paramDesc || '').replaceAll('\n', '<br />'),
        };
        this.insertContent(data.method + data.param);
      },

      findApi(node) {
        return node.data.nodeId ? node.data : this.findApi(node.parent);
      },
      findParamDTOS(node) {
        return node.data.apiRespParamDTOS || this.findParamDTOS(node.parent);
      },
      async handleParamsClick(data, node) {
        if (!data.assetColumns || (data.assetColumns && data?.disable)) {
          return;
        }
        // 找到节点的根
        const nodeRoot = this.findApi(node);
        const params = {
          expressionType: this.operatorId ? 'BETWEEN_OPERATOR' : 'LITEFLOW_RESULT',
          nodeName: nodeRoot.nodeName,
          reqApiCondition: {
            httpParamKind: 'QUERY',
          },
          apiRespParamDTOS: this.findParamDTOS(node),
        };
        params.apiRespParam = data;
        const queryStr = '?expressionType=' + params.expressionType;
        const res = await apiController.buildExpression(queryStr, params);
        if (res.success && !!res.data) {
          this.insertContent(`{${res.data}}`);
        }
      },
      insertContent(html) {
        const el = this.$refs.expression;
        // 得到光标前的位置
        const startPos = el.selectionStart;
        // 得到光标后的位置
        const endPos = el.selectionEnd;
        if (startPos != null) {
          const val = el.value;
          el.value = val.substring(0, startPos) + html + val.substring(endPos, val.length);
          el.focus();
          el.selectionStart = startPos;
          el.selectionEnd = startPos + html.length;
        } else {
          el.value += html;
          el.focus();
        }
      },
      handleOpen() {
        this.queryResponseParams();

        this.$nextTick(() => {
          if (this.$refs.expression) {
            // 自动聚焦输入框
            this.$refs.expression.focus();
            this.$refs.expression.value = this.expression;
          }
        });
      },
      async handleGetAllMethod() {
        const res = await api.getMethodList();
        this.methods = res.data;
      },
      queryResponseParams() {
        if (!this.showParamList) {
          return;
        }
        const { apiAttr, dataAssetApiId, apiType } = this.taskInfo.dataAssetApi;
        const operators = apiAttr.operators;
        if (operators?.start?.component) {
          delete operators.start.component;
        }
        const params = {
          ...apiAttr,
          apiId: dataAssetApiId,
          operatorId: this.operatorId,
          operators,
        };
        params.operators.global = {
          clazzName: 'com.wakedata.dw.open.model.api.flow.operator.global.GlobalOperator',
          operatorId: 'globalParam',
          parentOperatorId: '',
          name: 'globalParam',
          desc: '全局变量',
        };
        // 在没有任何算子的情况下需要获取设置全局返回参数，
        if (!apiAttr && !this.operatorId && apiType === ApiType.LITE_FLOW) {
          const liteFlow = ApiType._list.find(o => o.value === ApiType.LITE_FLOW);
          params.apiId = null;
          params.clazzName = liteFlow.clazzName;
          delete params.operatorId;
        }
        apiController.queryResponseParams(this.responseQueryParams, params).then(({ success, data }) => {
          if (success && !!data) {
            this.list = data.map(item => {
              return {
                ...item,
                label: item.nodeName,
                desc: item.nodeDesc,
                childApiRespParams: this.formatParams(item.apiRespParamDTOS),
              };
            });
          }
        });
      },
      handleClose() {
        this.$refs.expression.value = '';
        this.selectedMethod = null;
        this.search = '';
        this.$emit('update:visible', false);
      },
      async handleConfirm() {
        try {
          const expression = this.$refs.expression.value.trim();
          if (expression) {
            await api.checkExpression({ functionExpress: expression });
          }
          this.$emit('selected', expression);
          this.handleClose();
        } catch (error) {}
      },
    },
  };
</script>
<style scoped lang="less">
  .select-method {
    /deep/ .el-dialog__body {
      padding: 16px 20px;
    }
    &__content {
      display: flex;
      .content__list:not(:first-child) {
        border-left: 0;
      }
    }
    .method-list,
    .method-expression,
    .method-params {
      border: 1px solid #ddd;
      height: 500px;
      display: flex;
      flex-direction: column;
    }
    .method-list {
      width: 200px;
    }
    .method-params {
      width: 203px;
    }
    .method-expression {
      flex: 1;
    }
    .title {
      padding-left: 10px;
      font-weight: 700;
      border-left: 4px solid #2776fb;
    }
    .dialog-footer {
      margin: 0 auto;
      width: 250px;
      height: 30px;
      display: flex;
      justify-content: space-evenly;

      /deep/ .el-button {
        width: 60px;
      }
    }
    .list-title {
      height: 40px;
      padding: 10px 24px;
      border-bottom: 1px solid #ddd;
    }
    .list-search {
      height: 40px;
      border-bottom: 1px solid #ddd;
      display: flex;
      align-items: center;
      /deep/ .el-input__inner {
        border: 0;
      }
      /deep/ .el-input__validateIcon {
        display: none;
      }
    }
    .list-content {
      overflow-x: hidden;
      display: flex;
      flex: 1;
    }
    .list-extra {
      height: 318px;
      border-top: 1px solid #ddd;
      padding: 10px;
      overflow: auto;
      .list-item {
        margin-bottom: 5px;
        display: flex;
      }
      .list-item--bold {
        font-weight: bold;
      }
      span {
        width: 84px;
        text-align: right;
      }
      .list-url {
        text-decoration: none;
        color: #0f40f5;
      }
    }
    .expression-text {
      flex: 1;
      max-height: 120px;
      margin: 10px;
      border: 1px solid #ddd;
      border-radius: 4px;
      padding: 6px 8px;
      outline: none;
      &.edit {
        border-color: #b3d8ff;
      }
    }
    .filter-tree {
      overflow-x: hidden;
    }
    .custom-tree-node {
      flex: 1;
      padding-right: 8px;
      .custom-tree-type {
        color: #999;
        font-style: oblique;
        font-size: 12px;
      }
      .ellipsis {
        overflow: hidden;
        text-overflow: ellipsis;
        display: inline-block;
        white-space: nowrap;
      }
    }
  }
</style>
