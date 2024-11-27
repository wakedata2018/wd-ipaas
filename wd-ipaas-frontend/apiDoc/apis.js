import services from '@/common/services';

const controller = '/dw/open/document/api';

export default {
  getUserInfo(params) {
    return services.json(`/dw/open/manage/login/get.account.info`, params, {
      action: '获取用户信息',
    });
  },
  getApiClassify() {
    return services.json(`${controller}/group/list`, {
      action: '获取接口分类',
    });
  },
  getApiDetail(params) {
    return services.get(`${controller}/show`, {
      params,
      action: '获取接口详情',
      hidden: true,
    });
  },
  getApiCommonParams(params) {
    return services.get(`${controller}/get/requiredInput`, {
      params,
      action: '获取接口公共参数',
    });
  },
};
