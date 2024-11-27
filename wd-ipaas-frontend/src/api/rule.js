import services from '@/common/services';
const controller = '/dw/open/business/rule';

export default {
  add(params) {
    return services.post(`${controller}/add`, params, {
      action: '新增限流规则',
    });
  },
  delete(params) {
    return services.post(`${controller}/delete`, params, {
      action: '删除限流规则',
    });
  },
  query(params) {
    return services.get(`${controller}/query`, {
      action: '限流规则查询',
      params,
    });
  },
  update(params) {
    return services.post(`${controller}/update`, params, {
      action: '编辑限流规则',
    });
  },
};
