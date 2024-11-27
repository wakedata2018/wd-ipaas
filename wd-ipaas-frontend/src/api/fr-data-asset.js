import services from '@/common/services';
const controller = '/dw/open/business/fr_data_asset';

export default {
  detail(params) {
    return services.get(`${controller}/detail`, {
      action: '获取数据资产详情',
      params,
    });
  },
  list(params) {
    return services.get(`${controller}/list`, {
      action: '获取数据资产列表',
      params,
    });
  },
  preview(params) {
    return services.get(`${controller}/preview`, {
      action: '获取表数据',
      params,
    });
  },
  packageList(params) {
    return services.get(`${controller}/package`, {
      action: '获取目录列表',
      params,
    });
  },
};
