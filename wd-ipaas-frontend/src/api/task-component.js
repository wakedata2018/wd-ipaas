import services from '@/common/services';
const contr = '/dw/open/business/api/component/operator';
export default {
  list(params) {
    return services.post(`${contr}/list/impl`, params, {
      action: '获取算子列表',
    });
  },
  getOperatorFields(operatorId, operatorGraph) {
    return services.post(`${contr}/inputEdges?operatorId=` + operatorId, operatorGraph, {
      headers: {
        'Content-Type': 'application/json',
      },
      transformRequest: function (data, requestConfig) {
        return JSON.stringify(data);
      },
      hidden: true,
      type: 'json',
      action: '获取可选节点',
    });
  },
};
