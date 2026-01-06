import { services } from 'dss-common';
const contr = '/dw/projects';
export default {
  list(params, apiPrefix) {
    return services.get(`${!!apiPrefix ? apiPrefix : ''}${contr}/list`, {
      action: '获取当前用户所有项目',
      params
    });
  },
  getCurrentProject(params, apiPrefix) {
    return services.get(`${!!apiPrefix ? apiPrefix : ''}${contr}/currentProject`, {
      action: '获取当前项目',
      params
    });
  },
  changeProject(params, apiPrefix) {
    return services.get(`${!!apiPrefix ? apiPrefix : ''}${contr}/change`, {
      action: '更改项目',
      params
    });
  }
};
