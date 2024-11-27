import services from '@/common/services';
const controller = '/dw/open/business/datasource';
export default {
  addNewdata(params) {
    return services.post(`${controller}/add`, params, {
      action: '申请资产',
    });
  },
  showDataName() {
    return services.get(`${controller}/show`, {
      action: '接入源名',
    });
  },
  testDataSource(params) {
    return services.get(`${controller}/test`, {
      action: '数据源测试',
      params,
    });
  },
  showDataList(params) {
    return services.post(`${controller}/page/like`, params, {
      action: '查询数据源列表',
    });
  },
  list(params) {
    return services.get(`${controller}/list`, {
      action: '显示所有数据源',
      params,
    });
  },
  deleteDataSource(params) {
    return services.post(`${controller}/delete`, params, {
      action: '删除数据源',
    });
  },
  showDataTable(params) {
    return services.get(`${controller}/list/table`, {
      action: '获取表名称',
      params,
    });
  },
  showDataTablePage(params) {
    return services.get(`${controller}/list/table/page`, {
      action: '获取表名称',
      params,
    });
  },
  showColumn(params) {
    return services.get(`${controller}/list/table/column.v2`, {
      action: '获取列',
      params,
    });
  },
  showItem(params) {
    return services.get(`${controller}/show`, {
      action: '获取单个数据源详情',
      params,
    });
  },
  edit(params) {
    return services.post(`${controller}/update`, params, {
      action: '编辑数据源',
    });
  },
};
