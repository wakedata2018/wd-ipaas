import services from '@/common/services';
const controller = '/dw/open/business/api_group';

export default {
  getThemeList(params) {
    return services.get(`${controller}/list`, {
      action: '获取接口分类列表',
      params,
    });
  },
  deleteItem(params) {
    return services.post(`${controller}/delete`, params, {
      action: '删除单个接口分类',
    });
  },
  addGroup(params) {
    return services.post(`${controller}/add`, params, {
      action: '添加接口分类',
    });
  },
  editGroup(params) {
    return services.post(`${controller}/update`, params, {
      action: '编辑接口分类',
    });
  },
  getApiGroup(params) {
    return services.get(`${controller}/list/api`, params, {
      action: '获取api接口分类列表',
    });
  },
  filterGroup(params) {
    return services.get(`${controller}/list/page/like`, {
      action: '过滤和分页功能',
      params,
    });
  },
  show(params) {
    return services.get(`${controller}/show`, {
      action: '查询API接口分类',
      params,
    });
  },
  addTree(params) {
    return services.json(`${controller}/add`, params, {
      action: '添加API接口分类',
    });
  },
  deleteTree(params) {
    return services.post(`${controller}/delete`, params, {
      action: '删除API接口分类节点',
    });
  },
  updateTree(params) {
    return services.json(`${controller}/update`, params, {
      action: '更新API接口分类',
    });
  },
  apiGroupList(params) {
    return services.get(`${controller}/pageList`, {
      params,
      action: '查询接口分类列表',
    });
  },
  getGroupList(params) {
    return services.get(`${controller}/list`, {
      action: '查询API接口分类列表',
      params,
    });
  },
};
