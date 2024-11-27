import services from '@/common/services';
const controller = '/dw/open/business/statistics';

export default {
  getErrList(params) {
    return services.json(`${controller}/api_error`, params, {
      action: '获取错误统计列表',
    });
  },
};
