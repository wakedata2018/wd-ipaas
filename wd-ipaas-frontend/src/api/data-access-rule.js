import services from '@/common/services';
const controller = '/dw/open/business/data_access_rule';

export default {
  appAccessRule(params) {
    return services.get(`${controller}/app_access_rule`, {
      action: '获取我的资产列表',
      params,
    });
  },
  get(dataAccessRuleId) {
    return services.get(
      `${controller}/get`,
      {
        dataAccessRuleId,
      },
      {
        action: '获取资产',
      }
    );
  },
  validate(params) {
    return services.get(`${controller}/validate`, {
      action: '获取列',
      params,
    });
  },
  getByAppAndAsset(dataAccessAppId, dataAssetId) {
    return services.get(`${controller}/app_asset_access_rule`, {
      action: '获取资产',
      params: { dataAccessAppId, dataAssetId },
    });
  },
};
