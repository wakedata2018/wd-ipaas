import services from '@/common/services';

const controller = '/dw/open/business';

export default {
  getCardList(params) {
    return services.get(`${controller}/api/list/collect/approval`, {
      action: '获取首页卡片列表',
      params,
    });
  },
  getApiDetail(params) {
    return services.get(`${controller}/api/show`, {
      action: '获取API详情',
      params,
    });
  },
  getApiGroupName(params) {
    return services.get(`${controller}/api_group/show`, {
      action: '获取api接口分类名称',
      params,
    });
  },
  getApiGroup() {
    // return services.get(`${controller}/api_group/list/api`,{
    return services.get(`${controller}/api_group/list/api_tree`, {
      action: '获取接口分类列表',
    });
  },
  addCollect(params) {
    return services.post(`${controller}/collect/add`, params, {
      action: '添加收藏',
    });
  },
  deleteCollect(params) {
    return services.post(`${controller}/collect/delete`, params, {
      action: '取消收藏',
    });
  },
  apply(params) {
    return services.json(`${controller}/approval/approval`, params, {
      action: '申请',
    });
  },
  getGpList(params) {
    return services.get(`${controller}/api/list/page/like`, {
      action: '获取group下面的卡片列表',
      params,
    });
  },
  getApiUseTimes(params) {
    return services.get(`${controller}/statistics/access_method_times`, {
      action: '获取API调用次数',
      params,
    });
  },
};
