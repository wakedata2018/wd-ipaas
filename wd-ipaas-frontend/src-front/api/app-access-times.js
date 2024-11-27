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
  assetDetail(parmas) {
    return services.get(`${controller}/asset_detail`, {
      action: '统计API访问次数',
      parmas,
    });
  },
  getItemList() {
    return services.get(`${controller}/api/list`, {
      action: '获取过滤信息',
    });
  },
};
