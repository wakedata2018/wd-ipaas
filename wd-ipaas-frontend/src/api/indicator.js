import services from '@/common/services';
const controller = '/dw/open/business/target';

export default {
  list(params) {
    return services.post(`${controller}/selectMainData`, params, {
      action: '获取指标列表',
    });
  },
  myIndicator(params) {
    return services.get(`${controller}/index`, {
      action: '获取我的订阅指标列表',
      params,
    });
  },
  get(id) {
    return services.get(`${controller}/subscribe/${id}`, {
      action: '获取指标',
    });
  },
  myApproval(params) {
    return services.post(`${controller}/approval/myApproval`, params, {
      action: '获取我的指标申请列表',
    });
  },
  allApproval(params) {
    return services.post(`${controller}/approval/all`, params, {
      action: '获取指标申请列表',
    });
  },
  detailApproval(approvalId) {
    return services.get(`${controller}/approval/details`, {
      action: '获取指标申请详情',
      params: { approvalId },
    });
  },
  success(approvalId) {
    return services.get(`${controller}/approval/Success`, {
      action: '指标审批',
      params: { approvalId },
    });
  },
  subscribeDetails(ids) {
    return services.get(`${controller}/subscribeDetails`, {
      action: '获取订阅指标',
      params: { ids },
    });
  },
  getProjectsByArea(areaCode) {
    return services.get(`${controller}/findProjectByArea`, {
      action: '获取区域对应项目',
      params: { areaCode },
    });
  },
  subscribe(params) {
    return services.json(`${controller}/subscribe/userDimPermission`, params, {
      action: '指标授权',
    });
  },
  cancelUserDimPermission(indicatorId, strUsers) {
    return services.post(
      `${controller}/subscribe/cancelUserDimPermission`,
      { indicatorId, userNameList: strUsers },
      {
        action: '取消指标授权',
      }
    );
  },
};
