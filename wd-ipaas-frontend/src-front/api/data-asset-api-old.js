import services from '@/common/services';
const controller = '/dw/open/business/data_asset_api';

export default {
  appendInCharge(params) {
    return services.post(`${controller}/in_charge/append`, params, {
      action: '添加负责人',
    });
  },
  removeInCharge(dataAccessAppId) {
    return services.post(
      `${controller}/in_charge/remove`,
      {
        dataAccessAppId,
      },
      {
        action: '移除负责人',
      }
    );
  },
  columnList(params) {
    return services.get(`/dw/open/business/datasource/list/table/column`, {
      action: '获取列',
      params,
    });
  },
  tableList(params) {
    return services.get(`${controller}/table/list`, {
      action: '获取表',
      params,
    });
  },
  list(params) {
    return services.get(`${controller}/list/page/like`, {
      action: '获取资产列表',
      params,
    });
  },
  getList(params) {
    return services.get(`${controller}/list`, {
      action: '资产列表',
      params,
    });
  },
  publish(dataAssetApiId) {
    return services.post(
      `${controller}/publish`,
      { dataAssetApiId },
      {
        action: '发布资源',
      }
    );
  },
  unpublish(dataAssetApiId) {
    return services.post(
      `${controller}/un_publish`,
      { dataAssetApiId },
      {
        action: '下线资源',
      }
    );
  },
};
