import services from '@/common/services';
const controller = '/dw/open/business/devtool';

export default {
  // 水晶球专用，会带有水晶球权限
  list(params) {
    return services.get(`${controller}/show_all`, {
      action: '获取开发工具列表',
      params,
    });
  },
};
