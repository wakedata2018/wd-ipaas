import services from '@/common/services';

const controller = '/dw/open/business';

export default {
  deleteRule(params) {
    return services.get(`${controller}/rule_condition/deleteRule`, {
      params,
      action: '删除规则',
    });
  },
  deleteCondition(params) {
    return services.get(`${controller}/rule_condition/deleteCondition`, {
      params,
      action: '删除条件',
    });
  },
};
