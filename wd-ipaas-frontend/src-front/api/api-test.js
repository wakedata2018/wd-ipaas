import services from '@/common/services';
import axios from 'axios';

const controller = '/dw/open/business';

export default {
  getTestInfo(params) {
    return services.get(`${controller}/asset_detail`, {
      action: '获取测试时的参数',
      params,
    });
  },
  testApi(apiPath, params, config) {
    return axios
      .get(`/dw/open/api/${apiPath}`, {
        action: '测试',
        params,
        ...config,
      })
      .done(res => res.data);
  },
};
