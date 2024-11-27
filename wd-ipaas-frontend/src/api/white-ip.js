import services from '@/common/services';
const controller = '/dw/open/business/whitelist';
const blacklistController = '/dw/open/business/blacklist';

export default {
  show(accessAppId) {
    return services.post(
      `${controller}/show`,
      { accessAppId },
      {
        action: '获取IP白名单列表',
      }
    );
  },
  add(accessAppId, ips) {
    return services.post(
      `${controller}/add`,
      { accessAppId, ips },
      {
        action: '保存IP白名单',
      }
    );
  },
  showBlacklist(accessAppId) {
    return services.get(`${blacklistController}/show`, {
      action: '获取IP白名单列表',
      params: {
        appId: accessAppId,
      },
    });
  },
  addBlacklist(accessAppId, ips) {
    return services.post(
      `${blacklistController}/add`,
      { appId: accessAppId, ips },
      {
        action: '保存IP白名单',
      }
    );
  },
};
