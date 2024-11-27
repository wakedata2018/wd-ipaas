import services from '@/common/services';
const controller = '/dw/open/business/api';

export default {
  showDetail(params) {
    return services.get(`${controller}/show`, {
      action: '获取资产详情',
      params,
    });
  },
  publish(params) {
    return services.post(`${controller}/publish`, params, {
      action: '发布',
    });
  },
  unpublish(params) {
    return services.post(`${controller}/un_publish`, params, {
      action: '下线',
    });
  },
  delete(params) {
    return services.post(`${controller}/delete`, params, {
      action: '删除',
    });
  },
};
