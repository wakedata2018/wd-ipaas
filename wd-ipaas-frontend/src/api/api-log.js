import services from '@/common/services';

const controller = '/dw/open/business/statistics';

export default {
  getLogList(params) {
    return services.json(`${controller}/api_invoke`, params, {
      action: '获取接口日志',
    });
  },
  getLogDetail(params) {
    return services.get(`${controller}/getAccessLogInfo`, {
      params,
      action: '获取接口日志详情',
    });
  },
};
