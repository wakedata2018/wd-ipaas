import services from '@/common/services';
const controller = '/dw/open/business/approval';

export default {
  dataAccessRule(params) {
    return services.json(`${controller}/data_access_rule`, params, {
      action: '申请资产',
    });
  },
  detail(params) {
    return services.get(`${controller}/detail`, {
      action: '获取申请详情',
      params,
    });
  },
  frDataAsset(params) {
    return services.json(`${controller}/fr_data_asset`, params, {
      action: '获取列',
    });
  },
  history(params) {
    return services.get(`${controller}/history`, {
      params,
      action: '获取申请历史',
    });
  },
  historyAll(params) {
    return services.get(`${controller}/history/all`, {
      params,
      action: '获取审批列表',
    });
  },
  success(params) {
    return services.post(`${controller}/do`, params, {
      action: '审批通过',
    });
  },
  authorize(params, userIdentification) {
    return services.json(`${controller}/authorize`, params, {
      action: '审批通过',
      params: {
        userIdentification,
      },
    });
  },
  revoke(params, userIdentification) {
    return services.json(`${controller}/revoke`, params, {
      action: '审批通过',
      params: {
        userIdentification,
      },
    });
  },
};
