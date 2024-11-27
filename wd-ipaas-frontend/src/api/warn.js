import services from '@/common/services';
const controller = '/dw/open/business/warn';
export default {
  add(params) {
    return services.post(`${controller}/add`, params, {
      action: '新增相关告警',
    });
  },
  delete(params) {
    return services.post(`${controller}/delete`, params, {
      action: '删除告警',
    });
  },
  query(params) {
    return services.get(`${controller}/query`, {
      action: '查询告警',
      params,
    });
  },
  status(params) {
    return services.post(`${controller}/status`, params, {
      action: '启用禁用告警',
    });
  },
  edit(params) {
    return services.post(`${controller}/update`, params, {
      action: '编辑告警',
    });
  },
  queryApiGroup(params) {
    return services.get(`${controller}/query/api/group`, {
      action: '查询api接口分类',
      params,
    });
  },
  queryApi(params) {
    return services.get(`${controller}/query/api`, {
      action: '通过api接口分类查询api',
      params,
    });
  },
};
