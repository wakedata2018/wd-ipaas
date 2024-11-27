import services from '@/common/services';

const controller = '/dw/open/business/connector';

export default {
  fetchSecretAdd(params) {
    return services.json(`${controller}/secret/add`, params, {
      action: '新增平台密钥',
    });
  },

  fetchSecretPage(params) {
    return services.json(`${controller}/secret/page`, params, {
      action: '分页查询平台密钥',
    });
  },

  fetchConnectList(params) {
    return services.get(`${controller}/query.name`, {
      action: '查询平台列表',
      params,
    });
  },

  fetchEnvironmentListById(params) {
    return services.get(`${controller}/environment/findByConnectorId`, {
      action: '查询平台环境',
      params,
    });
  },

  fetchSecretById(params) {
    return services.get(`${controller}/secret/findByConnectorId`, {
      action: '查询平台密钥',
      params,
    });
  },

  fetchParamsById(params) {
    return services.get(`${controller}/findParamsByConnectorId`, {
      action: '查询平台参数',
      params,
    });
  },

  deleteSecretById(params) {
    return services.get(`${controller}/secret/deleteById`, {
      action: '删除平台密钥',
      params,
    });
  },

  updateSecretPage(params) {
    return services.json(`${controller}/secret/update`, params, {
      action: '修改平台密钥',
    });
  },
};
