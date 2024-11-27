import services from '@/common/services';
const PREXFIX = '/dw/open/business/data_access_app';

export default {
  getApplicationList(params) {
    return services.get(`${PREXFIX}/list/page/like`, {
      params,
      action: '获取应用管理列表',
    });
  },
  updateApplication(params, isEdit) {
    return services.post(isEdit ? PREXFIX + '/edit' : PREXFIX + '/create', params, {
      action: isEdit ? '编辑应用' : '新增应用',
    });
  },
  deleteApplicaption(params) {
    return services.get(`${PREXFIX}/delete`, {
      params,
      action: '删除应用',
    });
  },
  getApplicaptionDetail(params) {
    return services.get(`${PREXFIX}/appDetail`, {
      params,
      action: '获取应用详情',
    });
  },
  getAuthApplicaptionList(params) {
    return services.post(`${PREXFIX}/queryAppInfo`, params, {
      action: '获取租户授权应用列表',
    });
  },
  getAuthApplicaption(params) {
    return services.get(`${PREXFIX}/getAuthConfigByAppId`, {
      params,
      action: '获取租户授权应用',
    });
  },
  updateApplicaptionPublishStatus(params) {
    return services.get(`${PREXFIX}/updateLine`, {
      params,
      action: '更新应用发布状态',
    });
  },
  getApiAuthList(params) {
    return services.get(`${PREXFIX}/apiAuthList`, {
      params,
      action: '获取API授权列表',
    });
  },
  resetSecret(params) {
    return services.post(`${PREXFIX}/reset_secret`, params, {
      action: '重置密钥',
    });
  },
  updateAuth(params, status) {
    return services.get(status ? PREXFIX + '/auth' : PREXFIX + '/deauth', {
      action: status ? 'API接口授权' : 'API接口解除授权',
      params,
    });
  },
};
