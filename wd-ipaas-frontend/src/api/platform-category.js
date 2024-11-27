import { requestByPost } from '@wakeapp/wakedata-backend';

const controller = '/dw/open/business/connector';

export default {
  // 获取平台分类列表
  getCategory(params) {
    return requestByPost(`${controller}/group/query`, params);
  },
  // 新镇分组
  addCategory(params) {
    return requestByPost(`${controller}/group/add`, params);
  },
  // 删除分类
  delCategory(id) {
    return requestByPost(`${controller}/group/delete?id=${id}`);
  },
};
