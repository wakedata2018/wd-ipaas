import services from '@/common/services';
const controller = '/dw/open/business/statistics';

export default {
  getData(params) {
    return services.get(`${controller}/index`, {
      action: '获取首页数据',
      params,
    });
  },
};
