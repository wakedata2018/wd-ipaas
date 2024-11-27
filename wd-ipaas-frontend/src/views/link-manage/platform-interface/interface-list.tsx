import { ref, watch } from 'vue';
import { Button } from 'element-ui';

import { defineFatTable } from '@wakeadmin/components';
import CreateInterfaceDialog from './create-interface-dialog.vue';

import InterfaceApi from '@/api/link-interface';
import { PlatFormInterfaceList, CurSelectGroupInfo, enableStatusEnum } from './type';

export default defineFatTable<PlatFormInterfaceList, {}, { currentGroupInfo?: CurSelectGroupInfo }>(({ props }) => {
  const formDialogRef = ref();
  const isShowAddInterface = ref<boolean>(true);

  const openDialog = (row?: PlatFormInterfaceList | {}, extra?: CurSelectGroupInfo) => {
    formDialogRef.value?.open(row, extra);
  };

  const addPlatForm = () => {
    openDialog({}, props.extra?.currentGroupInfo);
  };

  watch(
    () => props.extra?.currentGroupInfo,
    cur => {
      isShowAddInterface.value = !!cur?.parentId;
    },
    {
      deep: true,
      immediate: true,
    }
  );

  return () => ({
    rowKey: 'id',
    async remove(row) {
      await InterfaceApi.delInterface(row[0].id);
      return true;
    },
    async request(params: any) {
      if (params?.query?.id) {
        // 判断查询一级还是二级
        const otherParams = params?.query?.parentId
          ? {
              apiGroupId: params.query.id,
            }
          : {
              connectorId: params.query.id,
            };
        const res = await InterfaceApi.getInterfaceList(
          {
            pageNo: params.pagination.page,
            pageSize: params.pagination.pageSize,
            ...otherParams,
          },
          {
            method: 'POST',
          }
        );

        return { list: res.data, total: res.totalCount };
      }
      return { list: [], total: 0 };
    },
    confirmBeforeRemove: '删除后数据不可恢复， 确认删除吗？',
    title: '接口列表',
    requestOnQueryChange: true,
    requestOnMounted: true,
    queryWatchDelay: 100,
    renderNavBar(table) {
      return (
        <div>
          <CreateInterfaceDialog ref={formDialogRef} onRefresh={table.refresh} />
          {isShowAddInterface.value && (
            <Button type="text" onClick={addPlatForm}>
              新增接口
            </Button>
          )}
        </div>
      );
    },
    columns: [
      {
        label: '接口名称',
        prop: 'apiName',
      },
      {
        label: '所属连接器',
        prop: 'connectorName',
      },
      {
        label: '接口分组',
        prop: 'apiGroupName',
      },
      {
        label: '状态',
        prop: 'enableStatus',
        align: 'center',
        valueType: 'select',
        valueProps: {
          options: [
            { label: enableStatusEnum.OFF_LINE.label, value: enableStatusEnum.OFF_LINE.value, color: '#fb4938' },
            { label: enableStatusEnum.ON_LINE.label, value: enableStatusEnum.ON_LINE.value, color: '#228B22' },
          ],
        },
      },
      {
        label: '操作',
        type: 'actions',
        width: 150,
        actionsSize: 'small',
        align: 'center',
        stripe: true,
        actions(table, row) {
          return [
            {
              name: '编辑',
              className: 'bd-button bd-table-primary',
              onClick: () => openDialog(row),
            },
            {
              name: '删除',
              className: 'bd-button bd-table-danger',
              type: 'danger',
              onClick: () => table.remove(row),
            },
          ];
        },
      },
    ],
  });
});
