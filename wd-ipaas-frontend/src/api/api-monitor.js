import services from '@/common/services';
const controller = '/dw/open/business/api';

export default {
  assetMonitor(params) {
    return services.get(`${controller}/asset_monitor`, {
      params,
      action: '获取API详情监控信息',
    });
  },
  monitorAppTimes(params) {
    return services.get(`${controller}/monitor_app_times`, {
      params,
      action: '获取API调用次数排行',
    });
  },
  monitorQps(params) {
    return services.get(`${controller}/monitor_qps`, {
      params,
      action: '获取qps耗时',
    });
  },
  monitorTimes(params) {
    return services.get(`${controller}/monitor_times`, {
      params,
      action: '获取API调用次数分布',
    });
  },
  appApproveInfo(params) {
    return services.get(`${controller}/app_approve_info`, {
      params,
      action: '获取API审批信息',
    });
  },
  deleteMyApi(params) {
    return services.post(`${controller}/delete_my_api`, params, {
      action: '删除我的申请',
    });
  },
};
