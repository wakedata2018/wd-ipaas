import {services} from 'dss-common';

const controller = '/dw/open/business/api/analysis';
export default {
  sql(params) {
    return services.get(`${controller}/sql`,{
      action:'sql解析接口',
      params
    })
  }
}
