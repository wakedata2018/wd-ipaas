import services from '@/common/services';

const controller = '/dw/open/business/function';

export default {
  getMethodList() {
    return services.get(`${controller}/get.all.fn`, {
      action: '获取表达式列表',
    });
  },
  checkExpression(params) {
    return services.json(`${controller}/check`, params, {
      action: '校验表达式',
    });
  },
};
