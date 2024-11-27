import { requestByPost, requestByGet, requestByPagination } from '@wakeapp/wakedata-backend';

const controller = '/dw/open/business/connector';

export default {
  // 获取平台分类列表
  getCategory(params) {
    return requestByPost(`${controller}/group/query`, params);
  },
  // 查询连接器平台信息
  queryPlatForm(params) {
    return requestByPagination(`${controller}/query`, params, { method: 'POST' });
  },
  // 新建平台
  addPlatForm(params, config) {
    return requestByPost(`${controller}/add`, params);
  },
  // 更新平台
  updatePlatForm(params) {
    return requestByPost(`${controller}/modify`, params);
  },
  // 删除平台
  delPlatForm(id) {
    return requestByPost(`${controller}?id=${id}`);
  },
  // 鉴权方式
  getAuthType() {
    return requestByGet(`${controller}/query.auth.type`);
  },
};
