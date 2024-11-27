import services from '@/common/services';
const controller = '/dw/open/business';

export default {
  accessTimes(params) {
    return services.json(`${controller}/statistics/api_right_rate`, params, {
      action: '获取API访问次数',
    });
  },
  statics(params) {
    return services.get(`${controller}/group_by_time_unit`, {
      action: '统计API访问次数',
      params,
    });
  },
  assetDetail(params) {
    return services.get(`${controller}/asset_detail`, {
      action: 'API接口详情',
      params,
    });
  },
  getItemList() {
    return services.get(`${controller}/api/list`, {
      action: '获取过滤信息',
    });
  },

  getAppInvoke(params) {
    return services.json(`${controller}/statistics/app_invoke`, params, {
      action: '获取应用访问统计',
    });
  },

  getAppList() {
    return services.get(`${controller}/data_access_app/list`, {
      action: '获取应用列表',
      params: { status: 'PASS' },
    });
  },
};
