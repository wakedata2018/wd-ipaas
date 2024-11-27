import services from '@/common/services';
const controller = '/user/admin';
export default {
  admin(params) {
    return services.get(`${controller}`, {
      action: '判断用户是否为管理员',
      params,
    });
  },
};
