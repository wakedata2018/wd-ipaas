import { services } from 'dss-common';

export default {
  getTopMenu(params) {
    return services.get('/get/top/menu', {
      action: '获取顶部菜单',
      params
    })
  },
  getUserItem(username, host = "") {
    return services.get(`${host}/get/user/item/info`, {
      action: '获取项目信息',
      params: {
        username
      }
    })
  }
}
