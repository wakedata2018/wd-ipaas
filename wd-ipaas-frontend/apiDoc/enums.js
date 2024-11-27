/**
 * 响应示例 或 异常示例
 */
const SAMPLE_TYPE = {
  RESPON: 0,
  ERROR: 1,
};

/**
 * 静态页面id
 */
const STATIC_PAGE_ID = {
  /**
   * 接口调用页面
   */
  INTERFACE_CALL: 'interface-call',
  /**
   * 获取TOKEN
   */
  ACCESS_TOKEN: 'access-token',
};

const STATIC_PAGE_NAME = {
  [STATIC_PAGE_ID.GUIDE]: '使用指南',
  // [STATIC_PAGE_ID.QUICK_START]: '快速入门',
  [STATIC_PAGE_ID.INTERFACE_CALL]: '接口调用',
  [STATIC_PAGE_ID.ACCESS_TOKEN]: 'accessToken文档',
};

export { SAMPLE_TYPE, STATIC_PAGE_ID, STATIC_PAGE_NAME };
