import services from '@/common/services';

// 注意：后面页面url地址不要带code参数，ajax除外

export default {
  getUserInfo(params) {
    return services.json(`/dw/open/manage/login/get.account.info`, params, {
      action: '获取用户信息',
    });
  },
  currentUserMenu() {
    return services.get(`/permission/manage/ipaas/get.menu.list`, {
      action: '获取菜单信息',
    });
  },
  loginOut() {
    return services.json(`/dw/open/manage/login/logout`, {
      action: '退出登录',
    });
  },
  accessToken(params) {
    return services.get('/dw/open/test/token/get.test.access.token', {
      params,
      action: '获取临时授权',
    });
  },
};
