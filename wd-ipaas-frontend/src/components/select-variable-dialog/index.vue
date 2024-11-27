<template>
  <el-dialog
    :visible="visible"
    append-to-body
    class="select-variable-dialog"
    :close-on-click-modal="false"
    @open="handlerOpen"
    @close="handlerClose"
  >
    <span slot="title">
      <div class="title">选择变量参数</div>
    </span>
    <div class="select-variable-dialog-wrap">
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
          ></el-tree>
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
          <el-table-column label="参数名称" prop="assetColumns"></el-table-column>
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
    name: 'SelectVariableDialog',
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
          return { label: item.nodeName, id: item.nodeId };
        });
      },
      // 不可选的行
      disabledRow() {
        return row => {
          return !row.disabled;
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
          this.list = dataItem?.apiRespParamDTOS?.[0]?.childApiRespParams ?? [];
        },
      },
    },
    methods: {
      handlerOpen() {
        this.currentStepId = null;
        this.queryResponseParams();
      },
      queryResponseParams() {
        const queryStr = '?operatorType=com.wakedata.dw.open.model.api.flow.operator.variable.CreateVariableOperator';
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
        apiController.queryResponseParams(queryStr, params).then(({ success, data }) => {
          if (success && !!data) {
            this.data = this.listData(data);
            this.currentStepId = this.data[0]?.nodeId;
          }
        });
      },
      tableRowClassName({ row }) {
        return row.disabled ? 'table-disable' : '';
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
          expressionType: 'UPDATE_VARIABLE_OPERATOR',
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
            this.$emit('selected', data);
            this.handlerClose();
          }
        });
      },
      // 参数列表数据转换
      listData(listDataSource) {
        return listDataSource.map(field => {
          if (field.apiRespParamDTOS?.[0]?.childApiRespParams.length) {
            // 数组子参数不能选择
            const loop = (data, disabled) => {
              return data?.map(item => {
                if ((item.assetDataType.includes('array') && item.childApiRespParams?.length) || disabled) {
                  console.log(item.assetDataType, disabled);
                  return {
                    ...item,
                    disabled,
                    childApiRespParams: loop(item.childApiRespParams, true),
                  };
                }
                return item;
              });
            };
            return {
              ...field,
              apiRespParamDTOS: [
                {
                  ...field.apiRespParamDTOS?.[0],
                  childApiRespParams: loop(field.apiRespParamDTOS?.[0]?.childApiRespParams, false),
                },
              ],
            };
          }
          return [];
        });
      },
      // 选择步骤
      onClickStep(val) {
        this.currentStepId = val.id;
      },
    },
  };
</script>
<style scoped lang="less">
  .select-variable-dialog {
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
        min-width: 200px;
        flex: 1;
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

    ::v-deep .step-tree.el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content {
      background: #2776fb !important;
      color: #fff !important;
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
