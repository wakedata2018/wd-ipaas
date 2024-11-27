<template>
  <el-dialog
    :visible="visible"
    append-to-body
    class="select-parameters-dialog"
    :close-on-click-modal="false"
    @open="handlerOpen"
    @close="handlerClose"
  >
    <span slot="title">
      <div class="title">选择公共参数值</div>
    </span>
    <div class="select-parameters-dialog-wrap">
      <div class="step-list">
        <div class="step-list-title"><span>步骤列表</span></div>
        <el-scrollbar class="content">
          <el-tree
            ref="tree"
            :data="stepListData"
            :props="defaultProps"
            :current-node-key="currentStepId"
            node-key="id"
            highlight-current
            class="step-tree"
            @node-click="onClickStep"
          >
            <template #default="{ data }">
              <span class="step-tree-node ellipsis" :title="`${data.label} &lt; ${data.desc} &gt;`">
                {{ data.label }}
                <span class="step-tree-node-desc"> &lt; {{ data.desc }} &gt; </span>
              </span>
            </template>
          </el-tree>
        </el-scrollbar>
      </div>
      <el-scrollbar class="table">
        <el-table
          ref="paramsTable"
          :data="list"
          row-key="id"
          default-expand-all
          :row-class-name="tableRowClassName"
          :tree-props="{ children: 'childApiRespParams', hasChildren: 'hasChildApiRespParams' }"
        >
          <el-table-column label="参数名称" prop="label"></el-table-column>
          <el-table-column label="参数类型" prop="assetDataType"></el-table-column>
          <el-table-column label="描述" prop="description"></el-table-column>
          <el-table-column label="操作" prop="action" width="100">
            <template #default="scope">
              <span v-if="disabledRow(scope.row)" type="text" class="select-btn" @click="rowClick(scope.row)"
                >选择</span
              >
            </template>
          </el-table-column>
        </el-table>
      </el-scrollbar>
    </div>
  </el-dialog>
</template>
<script>
  import apiController from '@/api/api-controller';
  import { ApiType } from '@/utils/enum';

  export default {
    name: 'SelectParametersDialog',
    props: {
      visible: {
        type: Boolean,
        default: false,
      },
      operatorId: {
        type: String,
        default: null,
      },
      taskInfo: {
        type: Object,
        default: () => ({}),
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
        list: [], // 参数列表
        currentStepId: null, // 当前步骤ID
        currentStepName: null, // 当前步骤名称
        defaultProps: {
          label: 'label',
        },
        data: [], // 数据源
      };
    },
    computed: {
      // 步骤列表
      stepListData() {
        return this.data.map(item => {
          return { label: item.nodeName, id: item.nodeId, desc: item.nodeDesc };
        });
      },
      // 不可选的行
      disabledRow() {
        return row => {
          return row.id !== 'HEAD' && row.id !== 'QUERY' && !row.disable;
        };
      },
    },
    watch: {
      currentStepId: {
        immediate: true,
        handler(val) {
          this.$nextTick(() => {
            this.$refs.tree?.setCurrentKey(val);
          });
          const dataItem = this.data.find(item => item.nodeId === val);
          this.currentStepName = dataItem?.nodeName ?? '';
          this.list = this.listData(dataItem?.apiRespParamDTOS ?? []);
        },
      },
    },
    methods: {
      handlerOpen() {
        this.currentStepId = null;
        this.queryResponseParams();
      },
      queryResponseParams() {
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
            this.data = data;
            this.currentStepId = data[0]?.nodeId;
          }
        });
      },
      tableRowClassName({ row }) {
        return row.id === 'HEAD' || row.id === 'QUERY' || row.disable ? 'table-disable' : '';
      },
      handlerClose() {
        this.$emit('update:visible', false);
      },
      rowClick(row) {
        if (!this.disabledRow(row)) {
          this.$message.error('请点击表格中的可选参数');
          return;
        }
        const params = {
          expressionType: this.operatorId ? 'BETWEEN_OPERATOR' : 'LITEFLOW_RESULT',
          nodeName: this.currentStepName,
          reqApiCondition: {
            httpParamKind: 'QUERY',
          },
          apiRespParamDTOS: this.data.find(item => item.nodeId === this.currentStepId)?.apiRespParamDTOS,
        };
        params.apiRespParam = row;
        const queryStr = '?expressionType=' + params.expressionType;
        apiController.buildExpression(queryStr, params).then(({ success, data }) => {
          if (success && !!data) {
            this.$emit('selected', { data });
            this.handlerClose();
          }
        });
      },
      // 参数列表数据转换
      listData(data) {
        const header = data.filter(item => item.type === 'HEAD');
        const query = data.filter(item => item.type === 'QUERY');
        const body = data.find(item => item.type === 'BODY');
        const list = [];

        const formatData = params => {
          return params.map(item => {
            const result = {
              ...item,
              label: item.assetColumns,
            };
            if (item?.childApiRespParams?.length) {
              return {
                ...result,
                childApiRespParams: formatData(item.childApiRespParams),
              };
            } else {
              return result;
            }
          });
        };

        if (header.length) {
          list.push({
            label: 'HEAD',
            id: 'HEAD',
            childApiRespParams: formatData(header),
          });
        }

        if (query.length) {
          list.push({
            label: 'QUERY',
            id: 'QUERY',
            childApiRespParams: formatData(query),
          });
        }

        if (body) {
          // 获取根节点数据类型 根节点为数组类型则根节点可选
          const rootDataType = JSON.parse(body.responsePostData)?.root?.type;
          const isArrayRoot = rootDataType === 'array';
          list.push({
            ...body,
            label: 'BODY',
            disable: !isArrayRoot,
            childApiRespParams: formatData(body.childApiRespParams),
          });
        }

        return list;
      },

      // 选择步骤
      onClickStep(val) {
        this.currentStepId = val.id;
      },
    },
  };
</script>
<style scoped lang="less">
  .select-parameters-dialog {
    // height: 100px;
    /deep/ .el-dialog__wrapper {
      z-index: 3000 !important;
    }
    .title {
      padding-left: 10px;
      font-weight: 700;
      border-left: 4px solid #2776fb;
    }

    .select-btn {
      color: #2776fb;
      cursor: pointer;
    }

    &-wrap {
      display: flex;
      .step-list {
        border: 1px solid #eee;
        height: 500px;
        width: 220px;
        &-title {
          border-bottom: 1px solid #eee;
          font-weight: 600;
          text-align: center;
          height: 36px;
          font-size: 12px;
          line-height: 36px;
          > span {
            vertical-align: middle;
            color: #909399;
          }
        }
      }
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

    .step-tree-node {
      flex: 1;
      padding-right: 8px;

      &-desc {
        color: #999;
        font-style: oblique;
        font-size: 12px;
      }
    }

    .ellipsis {
      overflow: hidden;
      text-overflow: ellipsis;
      display: inline-block;
      white-space: nowrap;
    }
    ::v-deep .step-tree.el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content {
      background: #2776fb !important;
      color: #fff !important;

      .step-tree-node {
        &-desc {
          color: #fff !important;
        }
      }
    }

    ::v-deep .table {
      flex: 3;
      height: 500px;
      overflow-x: hidden;
      border: 1px solid #eee;
      .el-scrollbar__wrap {
        overflow-x: hidden;
      }
      .current-row {
        td,
        .el-table--enable-row-hover .el-table__body tr:hover > td.el-table__cell,
        .select-btn {
          background: #2776fb !important;
          color: #fff !important;
        }
      }
    }
    ::v-deep .el-table .table-disable {
      background-color: #f5f7fa;
      color: #c0c4cc;
      cursor: not-allowed;
    }
    ::v-deep .el-table .table-disable.current-row {
      td,
      .el-table--enable-row-hover .el-table__body tr:hover > td.el-table__cell,
      .select-btn {
        background-color: #f5f7fa !important;
        color: #c0c4cc !important;
      }
    }
  }
</style>
