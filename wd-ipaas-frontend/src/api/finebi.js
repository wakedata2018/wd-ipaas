import services from '@/common/services';
const controller = '/dw/open/business/fr_dashboard';

export default {
  /**
   * 登录findbi
   * @param {Object} params
   * {
   *  fine_username: 'admin' // 用户名, String
   *  fine_password: '123456' // 密码， String
   *  validity: -1 // unknow, Number
   * }
   */
  getHomePage() {
    return services.get(`${controller}/login/cross/domain`, {
      action: '获取默认报表',
    });
  },
  create(params) {
    return services.post(`${controller}/create`, params, {
      action: '创建仪表盘',
    });
  },
  delete(params) {
    return services.post(`${controller}/delete`, params, {
      action: '删除仪表盘',
    });
  },
  update(params) {
    return services.post(`${controller}/update_name`, params, {
      action: '修改仪表盘名称',
    });
  },
  getReports() {
    return services.get(`${controller}/user_fr_dashboard`, {
      action: '获取报表列表',
    });
  },
  getDirectory(params) {
    return services.get(`${controller}/user_fr_directory`, {
      params,
      action: '获取目录',
    });
  },
  queryDirectory(params) {
    return services.get(`${controller}/user_fr_directory/query`, {
      params,
      action: '查询目录',
    });
  },
  save_as(params) {
    // vGET /POST /dw/business/fr_dashboard/save_as
    return services.post(`${controller}/save_as`, params, {
      action: '另存为',
    });
  },
  setHomePage(frDashboardId) {
    return services.post(
      `${controller}/homepage`,
      { frDashboardId },
      {
        action: '设置为首页',
      }
    );
  },
  orderChange(frDashboardId, targetPreFrDashboardId) {
    return services.post(
      `${controller}/order/change`,
      {
        frDashboardId,
        targetPreFrDashboardId,
      },
      {
        action: '调整顺序',
      }
    );
  },
};
