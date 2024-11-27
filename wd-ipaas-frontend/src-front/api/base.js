import services from '@/common/services';

// 注意：后面页面url地址不要带code参数，ajax除外

export default {
  // getUserInfo() {
  //   return services.get(`${host}/user_info`, {
  //     action: '获取用户信息',
  //     hidden: true
  //   });
  // },
  getUserInfo() {
    return services.get(`/permission/web/permission/login/query`, {
      action: '获取用户信息',
      hidden: true,
    });
  },
  logout() {
    return services
      .get(`/logout`, {
        // .get(`/dw/logout`, {
        action: '退出系统',
      })
      .done(res => {
        // console.log(res.data.globalLogoutUrl)
        // window.top.location.reload();
        window.top.location.href = res.value;
      });
  },
};
