import { ref, computed } from 'vue';
import { useRoute } from 'vue-router/composables';
import { Button } from 'element-ui';
import { defineFatTable, FatFormMode } from '@wakeadmin/components';
import { requestByPost } from '@wakeapp/wakedata-backend';

import { useStore } from '@/store/hook';
import WKAuthButton from '@/components/wk-auth-button/index.vue';
import DefaultLayout from '@/components/table-layout';
import platformListApi from '@/api/platform-list';
import pageUtils from '@/utils/page.js';

import ImportExportDialog from './import-export-dialog.vue';
import CreateOrEditDialog from './create-or-edit-dialog.vue';
import { PlatFormBasicInfo } from './type';

export default defineFatTable<PlatFormBasicInfo, {}>(() => {
  const formDialogRef = ref();

  const importOrExportVisible = ref();

  const store = useStore();
  const route = useRoute();

  const title = computed(() => {
    return pageUtils.findPageName(store.state.permitList, route.name);
  });

  const openDialog = (row?: PlatFormBasicInfo | {}, mode?: FatFormMode) => {
    formDialogRef.value?.open(row, mode);
  };

  const addPlatForm = () => {
    openDialog();
  };

  const onOpenImpOrExp = () => {
    importOrExportVisible.value = true;
  };

  const onCloseImpOrExp = () => {
    importOrExportVisible.value = false;
  };

  // 查询
  const onSearch = (table: any) => {
    table.search();
  };

  // 重置
  const onReset = (table: any) => {
    table.reset();
  };

  return () => ({
    rowKey: 'id',
    async request(params) {
      const res = await platformListApi.queryPlatForm({
        pageNo: params.pagination.page,
        pageSize: params.pagination.pageSize,
        ...params.query,
      });
      return { list: res.data, total: res.totalCount };
    },
    async remove(row) {
      await requestByPost(`/dw/open/business/connector/delete?id=${row[0].id}`, { method: 'POST' });
    },
    confirmBeforeRemove: '删除后数据不可恢复， 确认删除吗？',
    title: title.value,
    searchText: '查询',
    layout: DefaultLayout,
    renderNavBar() {
      return (
        <div>
          <WKAuthButton code="add-link" type="primary" onClick={addPlatForm}>
            新增连接器
          </WKAuthButton>
          <WKAuthButton code="link-import-export" type="primary" onClick={onOpenImpOrExp}>
            导入/导出
          </WKAuthButton>
        </div>
      );
    },
    renderAfterForm(table) {
      return (
        <div>
          <CreateOrEditDialog ref={formDialogRef} onRefresh={table.refresh} />
          <ImportExportDialog
            visible={importOrExportVisible.value}
            onClose={onCloseImpOrExp}
            onRefresh={table.refresh}
          />
        </div>
      );
    },
    renderSubmitter(table) {
      return (
        <div style={{ display: 'inline-block', marginTop: '20px', marginLeft: '18px' }}>
          <Button type="primary" class="bd-button bd-strong" onClick={() => onSearch(table)}>
            查询
          </Button>
          <Button type="primary" plain size="mini" class="bd-button bd-strong" onClick={() => onReset(table)}>
            重置
          </Button>
        </div>
      );
    },
    requestOnQueryChange: true,
    columns: [
      {
        label: '连接器名称',
        prop: 'name',
        valueType: 'search',
        queryable: true,
      },
      {
        label: '连接器分类',
        prop: 'groupName',
      },
      {
        label: '接口数量',
        prop: 'apiCount',
      },
      {
        label: '最后更新时间',
        prop: 'updateTime',
      },
      {
        label: '状态',
        prop: 'enableStatus',
        align: 'center',
        valueType: 'select',
        valueProps: {
          options: [
            { label: '已启用', value: 1, color: '#228B22' },
            { label: '未启用', value: 0, color: '#fb4938' },
          ],
        },
        queryable: true,
      },
      {
        label: '操作',
        type: 'actions',
        actionsSize: 'small',
        align: 'center',
        width: '200',
        actions(table, row) {
          return [
            {
              name: '查看',
              className: 'bd-button bd-table-primary',
              onClick: () => openDialog(row, 'preview'),
            },
            {
              name: '编辑',
              className: 'bd-button bd-table-primary',
              onClick: () => openDialog(row),
            },
            {
              name: '删除',
              type: 'danger',
              className: 'bd-button bd-table-danger',
              onClick: () => table.remove(row),
            },
          ];
        },
      },
    ],
  });
});
