import services from '@/common/services';
const controller = '/dw/open/business/user/show_all';

export default {
  listUser(params) {
    return services.get(`${controller}/paged/like`, {
      action: '用户列表',
      params,
    });
  },
};
