import services from '@/common/services';

const PREFIX = '/dw/open/api';

export default {
  /**
   * 订阅事件列表
   */
  subscribeList(params) {
    return services.json(`${PREFIX}/topic/list`, params, {
      action: '订阅事件列表',
    });
  },

  /**
   * 添加topic订阅地址
   */
  addSubscribeAddress(params) {
    return services.json(`${PREFIX}/topic/add`, params, {
      action: '添加topic订阅地址',
    });
  },

  /**
   * 修改订阅地址
   */
  updateSubscribeAddress(params) {
    return services.json(`${PREFIX}/topic/update`, params, {
      action: '修改订阅地址',
    });
  },

  /**
   * 删除订阅地址
   */
  deleteSubscribeAddress(params) {
    return services.get(
      `${PREFIX}/topic/delete`,
      { params },
      {
        action: '删除订阅地址',
      }
    );
  },

  /**
   * 订阅地址详情
   */
  subscribeTopicDetail(params) {
    return services.get(
      `${PREFIX}/topic/detail`,
      { params },
      {
        action: '订阅地址详情',
      }
    );
  },

  /**
   * 添加http订阅地址
   */
  addHttpAddress(params) {
    return services.json(`${PREFIX}/http/address/add`, params, {
      action: '添加http订阅地址',
    });
  },

  /**
   * 订阅地址列表
   */
  subscribeAddressList(params) {
    return services.json(
      `${PREFIX}/address/list`,
      { params },
      {
        action: '订阅地址列表',
      }
    );
  },

  /**
   * 订阅地址详情
   */
  subscribeAddressDetail(params) {
    return services.get(
      `${PREFIX}/address/detail`,
      { params },
      {
        action: '订阅地址详情',
      }
    );
  },

  /**
   * 获取事件列表
   */
  getEventList(params) {
    return services.json(`${PREFIX}/event/list`, params, {
      action: '获取事件列表',
    });
  },

  /**
   * 删除事件
   */
  deleteEvent(params) {
    return services.get(
      `${PREFIX}/event/delete`,
      { params },
      {
        action: '删除事件',
      }
    );
  },

  /**
   * 添加事件
   */
  addEvent(params) {
    return services.json(`${PREFIX}/event/add`, params, {
      action: '添加事件',
    });
  },

  /**
   * 事件详情
   */
  eventDetail(params) {
    return services.get(
      `${PREFIX}/event/detail`,
      { params },
      {
        action: '事件详情',
      }
    );
  },

  /**
   * 更新事件
   */
  updateEvent(params) {
    return services.json(`${PREFIX}/event/update`, params, {
      action: '更新事件',
    });
  },
};
