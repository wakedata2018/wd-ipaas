import { ref, computed } from 'vue';
import { useRoute } from 'vue-router/composables';
import { useStore } from '@/store/hook';
import { Button } from 'element-ui';

import { defineFatTable, FatFormMode } from '@wakeadmin/components';
import DefaultLayout from '@/components/table-layout';
import ShowEditDialog from './show-edit-dialog.vue';

import AuthApi from '@/api/auth';
import { AuthList, AuthListDialogRef } from '@/utils/enum/auth-list';
import pageUtils from '@/utils/page.js';

export default defineFatTable<AuthList, {}>(() => {
  const store = useStore();
  const route = useRoute();

  const title = computed(() => {
    return pageUtils.findPageName(store.state.permitList, route.name);
  });
  const formDialogRef = ref<AuthListDialogRef>();

  // 获取鉴权信息详情
  const fetchAuthDetail = async (id: number) => {
    return await AuthApi.fetchAuthInfoById(id);
  };

  const openDialog = async (id: number, mode: FatFormMode) => {
    let initialValue = null;
    if (id) {
      initialValue = await fetchAuthDetail(id);
    }
    formDialogRef.value?.open(initialValue, mode);
  };

  return () => ({
    rowKey: 'id',
    async request(params) {
      const res = await AuthApi.fetchAuthList({
        pageNo: params.pagination.page,
        pageSize: params.pagination.pageSize,
        ...params.query,
      });
      return { list: res.data, total: res.totalCount };
    },
    title: title.value,
    searchText: '查询',
    requestOnQueryChange: true,
    layout: DefaultLayout,
    renderSubmitter(table) {
      return (
        <div class="bd-search-group inline-block" style={{ marginLeft: '18px' }}>
          <Button type="primary" class="bd-button bd-strong" onClick={() => table.search()}>
            查询
          </Button>
          <Button type="primary" plain size="mini" class="bd-button bd-strong" onClick={() => table.reset()}>
            重置
          </Button>
        </div>
      );
    },
    renderAfterForm(table) {
      return <ShowEditDialog ref={formDialogRef} onRefresh={table.refresh} />;
    },
    columns: [
      {
        label: '连接器名称',
        prop: 'connectorName',
        queryable: true,
      },
      {
        label: '接口环境',
        prop: 'connectorEnvironmentName',
      },
      {
        label: '鉴权接口名称',
        prop: 'connnectorApiName',
      },
      {
        label: '最后更新时间',
        prop: 'updateTime',
      },
      {
        label: '状态',
        prop: 'status',
        align: 'center',
        valueType: 'select',
        valueProps: {
          options: [
            { label: '已启用', value: 1, color: '#fb4938' },
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
        actions(table, row) {
          return [
            {
              name: '查看',
              className: 'bd-button bd-table-primary',
              onClick: () => openDialog(row.id, 'preview'),
            },
            {
              name: '编辑',
              className: 'bd-button bd-table-primary',
              onClick: () => openDialog(row.id, 'editable'),
            },
          ];
        },
      },
    ],
  });
});
