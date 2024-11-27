import {services} from 'dss-common';

const controller = '/dw/open/business/database';
export default {
  showDataName() {
    return services.get(`${controller}/all`, {
      action: '数据库类型',
    })
  },
  showDataType(params) {
    return services.get(`${controller}/show`, {
      action: '查询数据库类型',
      params
    })
  }
}
