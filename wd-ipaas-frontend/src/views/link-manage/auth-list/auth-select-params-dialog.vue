<template>
  <el-dialog
    :visible="props.visible"
    append-to-body
    class="auth-select-parameters-dialog"
    :close-on-click-modal="false"
    @close="onClose"
  >
    <span slot="title">
      <div class="title">选择引用参数值</div>
    </span>
    <div class="auth-select-parameters-dialog-wrap">
      <el-scrollbar class="table">
        <el-table
          ref="paramsTable"
          :data="treeData"
          row-key="id"
          default-expand-all
          :row-class-name="tableRowClassName"
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
<script lang="tsx" setup>
  import { computed, PropType } from 'vue';

  import { Message } from 'element-ui';

  import { InterfaceParams } from '@/utils/enum/auth-list';

  const props = defineProps({
    params: {
      type: Array as PropType<InterfaceParams[]>,
      default: () => [],
    },
    visible: {
      type: Boolean,
      default: false,
    },
  });

  const treeData = computed(() => {
    const header = props.params.filter(item => item.httpParamKind === 'HEAD');
    const query = props.params.filter(item => item.httpParamKind === 'QUERY');
    const body = props.params.find(item => item.httpParamKind === 'BODY');
    const list = [];

    const addExpression = (arr: InterfaceParams[], parentExpression: string) => {
      return arr.map(item => {
        return {
          ...item,
          expression: `${parentExpression}.${item.assetColumns}`,
        };
      });
    };

    if (header.length) {
      list.push({
        assetColumns: 'HEAD',
        id: 'HEAD',
        children: addExpression(header, '$.HEAD'),
        expression: '$.HEAD',
      });
    }

    if (query.length) {
      list.push({
        assetColumns: 'QUERY',
        id: 'QUERY',
        children: addExpression(query, '$.QUERY'),
        expression: '$.QUERY',
      });
    }
    if (body) {
      let initArry: InterfaceParams[] = [];
      const loop: any = (data: any, parentExpression: string) => {
        return data.map((item: any) => {
          let children = [];
          if (item[1].type === 'array' && item[1].items.properties) {
            children = loop(Object.entries(item[1].items.properties), `${parentExpression}.${item[1].name}`);
          } else if (item[1].properties) {
            children = loop(Object.entries(item[1].properties), `${parentExpression}.${item[1].name}`);
          }
          return {
            id: item[0],
            assetColumns: item[1].name,
            assetDataType: item[1].type,
            description: item[1].description,
            expression: `${parentExpression}.${item[1].name}`,
            children,
          };
        });
      };
      if (body?.responsePostData) {
        const data = JSON.parse(body?.responsePostData)?.root?.properties ?? {};
        initArry = loop(Object.entries(data), '$.BODY');
      }
      list.push({
        assetColumns: 'BODY',
        id: 'BODY',
        description: body.description,
        children: initArry,
        expression: '$.BODY',
      });
    }

    return list;
  });

  const disabledRow = computed(() => {
    return (row: InterfaceParams) => {
      return row.id !== 'HEAD' && row.id !== 'QUERY' && row.id !== 'BODY';
    };
  });

  const tableRowClassName = ({ row }) => {
    return row.id === 'HEAD' || row.id === 'QUERY' || row.id === 'BODY' ? 'table-disable' : '';
  };

  // 定义Emits
  const emits = defineEmits<{
    (e: 'update:visible', value: boolean): void;
    (e: 'select', value: string): void;
  }>();

  const rowClick = row => {
    if (!disabledRow.value(row)) {
      Message.error('请点击表格中的可选参数');
      return;
    }
    emits('select', row.expression);
  };

  const onClose = () => {
    emits('update:visible', false);
  };
</script>
<style scoped lang="less">
  .auth-select-parameters-dialog {
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
        flex: 1;
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
  }
</style>
