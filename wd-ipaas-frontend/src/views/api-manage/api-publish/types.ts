/**
 * api弹窗操作类型
 */
export enum OPERATION_TYPE {
  /** 查看 */
  VIEW = 'VIEW',
  /** 新建 */
  CREATE = 'CREATE',
  /** 编辑 */
  EDIT = 'EDIT',
  /** 复制 */
  COPY = 'COPY',
}

/**
 * api类型
 */
export enum API_TYPE {
  /** 全部 */
  ALL = 'ALL',
  /** 数据表 */
  TABLE = 'NORMAL_TABLE',
  /** SQL模式 */
  SQL = 'CUSTOM_SQL',
  /** HTTP模式 */
  HTTP = 'EXTERNAL_HTTP',
  /** webService */
  WS = 'WEB_SERVICE',
  /** 服务编排 */
  LITE_FLOW = 'LITE_FLOW'
}
