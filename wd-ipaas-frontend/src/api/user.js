import services from '@/common/services';
const controller = '/dw/open/business/platform_user';

export default {
  // 水晶球专用，会带有水晶球权限
  list(params) {
    return services.get(`${controller}/show/like`, {
      action: '用户列表',
      params,
    });
  },
  // 不带权限
  listUser(params) {
    return services.get(`${controller}/show_like/paged`, {
      action: '用户列表',
      params,
    });
  },
  admin(params) {
    return services.get(`/user/admin`, {
      action: '判断用户是否为管理员',
      params,
    });
  },
};
