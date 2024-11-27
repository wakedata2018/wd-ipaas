import services from '@/common/services';
const controller = '/dw/open/api/business/operator_log';

export default {
  list(params) {
    return services.get(`${controller}/list`, {
      action: '获取操作日志列表',
      params,
    });
  },
};
