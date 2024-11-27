import services from '@/common/services';
const controller = '/dw/open/business/data_access_app';

export default {
  create(params) {
    return services.post(`${controller}/create`, params, {
      action: '创建数据访问接入',
    });
  },
  delete(dataAccessAppId) {
    return services.post(
      `${controller}/delete`,
      {
        dataAccessAppId,
      },
      {
        action: '删除数据访问接入',
      }
    );
  },
  edit(params) {
    return services.post(`${controller}/edit`, params, {
      action: '修改数据访问接入',
    });
  },
  list(params) {
    return services.get(`${controller}/list`, {
      action: '获取数据访问接入列表',
      params,
    });
  },
  getList(params) {
    return services.get(`${controller}/list/page/like`, {
      action: '获取数据列表以及筛选功能',
      params,
    });
  },
  resetSecret(dataAccessAppId) {
    return services.post(
      `${controller}/reset_secret`,
      { dataAccessAppId },
      {
        action: '重置密钥',
      }
    );
  },
};
