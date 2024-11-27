import services from '@/common/services';
const controller = '/dw/open/business/data_access_app';
const authInfoController = '/dw/open/business/auth';
const authInfoApiController = '/dw/open';

export default {
  // 负责人列表
  queryInChargeList(params) {
    return services.post('/permission/web/admin/user/query', {
      action: '获取接入负责人列表',
      params,
    });
  },
  create(params) {
    return services.post(`${controller}/create`, params, {
      action: '创建数据访问接入',
    });
  },
  delete(dataAccessAppId) {
    return services.post(
      `${controller}/delete`,
      {
        dataAccessAppId,
      },
      {
        action: '删除数据访问接入',
      }
    );
  },
  edit(params) {
    return services.post(`${controller}/edit`, params, {
      action: '修改数据访问接入',
    });
  },
  list(params) {
    return services.get(`${controller}/list`, {
      action: '获取数据访问接入列表',
      params,
    });
  },
  listWithDefaultApp(params) {
    return services.get(`${controller}/listWithDefaultApp`, {
      action: '获取数据访问接入列表',
      params,
    });
  },
  listPermitted(params) {
    return services.get(`${controller}/list/permission/app`, {
      action: '获取已授权的数据访问接入列表',
      params,
    });
  },
  getList(params) {
    return services.get(`${controller}/list/page/like`, {
      action: '获取数据列表以及筛选功能',
      params,
    });
  },
  resetSecret(dataAccessAppId) {
    return services.post(
      `${controller}/reset_secret`,
      { dataAccessAppId },
      {
        action: '重置密钥',
      }
    );
  },
  reference(params) {
    return services.get(`${controller}/app/reference`, {
      action: '获取数据访问接入列表',
      params,
    });
  },
  authDate(params) {
    return services.get(`${controller}/app/auth/date`, {
      action: 'api授权时间',
      params,
    });
  },
  getAuthTypes() {
    return services.get(`${controller}/getAuthTypes`, {
      action: '获取授权类型',
    });
  },
  getTokenInternal(appId, apiPath, issuedAt) {
    return services.get(`${controller}/getTokenInternal`, {
      params: {
        appId,
        apiPath,
        issuedAt,
      },
      action: '获取token值',
    });
  },
  getAuthParams(appId, apiId) {
    return services.get(`${controller}/getAuthParams`, {
      params: {
        appId,
        apiId,
      },
      action: '获取token值',
    });
  },
  addAuthInformation(params) {
    return services.json(`${authInfoController}/add`, params, {
      action: '添加认证信息',
    });
  },
  getAuthInformationList(params) {
    return services.get(`${authInfoController}/list/page/like`, {
      action: '获取认证信息列表',
      params,
    });
  },
  getAuthInformationDetail(params) {
    return services.get(`${authInfoController}/show`, {
      action: '获取认证信息详情',
      params,
    });
  },
  deleteAuthInformation(params) {
    return services.post(`${authInfoController}/delete`, params, {
      action: '删除认证信息',
    });
  },
  updateAuthInformation(params) {
    return services.json(`${authInfoController}/update`, params, {
      action: '修改认证信息',
    });
  },
  getThirdAppApiList(params) {
    return services.get(`${authInfoController}/list/query/name`, {
      action: '获取第三方应用api列表',
      params,
    });
  },
  addThirdAuth(params) {
    return services.json(`${authInfoController}/batch/authorization`, params, {
      action: '新增第三方应用授权',
    });
  },
  getThirdAuthInfoOfApiOperator(params) {
    return services.get(`${authInfoApiController}/business/api/query/external/application`, {
      action: '获取api已授权的第三方应用列表(编排)',
      params,
    });
  },
  getThirdAuthInfoOfApi(params) {
    return services.get(`${authInfoController}/query/application/list`, {
      action: '获取api已授权的第三方应用列表',
      params,
    });
  },
  relieveThirdAuth(params) {
    return services.get(`${authInfoController}/delete/authorization`, {
      action: '解除第三应用的授权',
      params,
    });
  },
  batchAuthorize(params) {
    return services.json(`${authInfoApiController}/business/approval/batchAuthorize`, params, {
      action: '低代码应用批量授权api',
    });
  },
  batchRevoke(params) {
    return services.json(`${authInfoApiController}/business/approval/batchRevoke`, params, {
      action: '低代码应用批量解除授权',
    });
  },
  initIndentify(params) {
    return services.get(`${authInfoApiController}/nacos/api/getServiceList`, {
      params,
      action: '获取应用类型',
    });
  },
  getMetadataByServiceName(params) {
    return services.get(`${authInfoApiController}/nacos/api/getMetadataByServiceName`, {
      params,
      action: '获取应用类型参数数据',
    });
  },
};
