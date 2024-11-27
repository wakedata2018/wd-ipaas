/**
 *api管理
 */

// 申请状态
const APPLY_STATUS = [
  {
    value: 0,
    label: '未申请',
  },
  {
    value: 1,
    label: '申请中',
  },
  {
    value: 2,
    label: '申请成功',
  },
  {
    value: 3,
    label: '申请失败',
  },
];

// 发布状态
const PUBLISH_STATUS = [
  {
    value: 0,
    label: '未发布',
  },
  {
    value: 1,
    label: '已发布',
  },
];

const API_ENUM = {
  sql: 0,
  dataTable: 1,
  http: 2,
  service: 3,
  webService: 7,
};

// API类型
const API_TYPE = [
  {
    value: -1,
    label: '全部',
  },
  {
    value: 1,
    label: '数据表',
  },
  {
    value: '0',
    label: 'SQL模式',
  },
  {
    value: 2,
    label: 'HTTP模式',
  },
  {
    value: 7,
    label: 'WebService模式',
  },
  {
    value: 3,
    label: '服务编排',
  },
];

const ApiEnum = {
  dataTable: 'NORMAL_TABLE',
  sql: 'CUSTOM_SQL',
  http: 'EXTERNAL_HTTP',
  service: 'LITE_FLOW',
  webService: 'WEB_SERVICE',
};

const ApiType = {
  [ApiEnum.http]: 'HTTP模式',
  [ApiEnum.sql]: 'SQL模式',
  [ApiEnum.dataTable]: '数据表',
  [ApiEnum.service]: '服务编排',
  [ApiEnum.webService]: 'WebService模式',
};

const ApplicationEnum = {
  unapply: 'UN_APPLY',
  inApply: 'IN_APPLY',
  apply: 'APPLY',
  failureApply: 'FAILURE_APPLY',
};

const ApplicationStatus = {
  [ApplicationEnum.unapply]: '未申请',
  [ApplicationEnum.inApply]: '申请中',
  [ApplicationEnum.apply]: '申请成功',
  [ApplicationEnum.failureApply]: '申请失败',
};

const QuerySecretOptions = [
  {
    label: '公开',
    value: 1,
  },
  {
    label: '加密',
    value: 0,
  },
];
const SecretEnum = {
  public: 'PUBLIC',
  private: 'PRIVATE',
};

const SecretStatus = {
  [SecretEnum.public]: '公开',
  [SecretEnum.private]: '加密',
};

const ApiApplyStatus = [
  {
    value: -1,
    label: '全部',
  },
  {
    value: '0',
    label: '未申请',
  },
  {
    value: 1,
    label: '申请中',
  },
  {
    value: 2,
    label: '申请成功',
  },
  {
    value: 3,
    label: '申请失败',
  },
];

const ModeEnum = {
  fixed: 'fixed',
  reference: 'reference',
  method: 'function',
};

const ModeMap = {
  [ModeEnum.fixed]: '固定值',
  [ModeEnum.reference]: '引用值',
  [ModeEnum.method]: '表达式',
};

/**
 * 服务类型
 */
const SERVICE_TYPE = [
  {
    label: 'mq',
    value: 3,
  },
  {
    label: 'kafka',
    value: 2,
  },
];

/**
 * 启用状态
 */
const ENABLE_TYPE = [
  {
    label: '是',
    value: 1,
  },
  {
    label: '否',
    value: 0,
  },
];

/**
 * 锁类型
 */
const LOCK_TYPE = [
  {
    label: '可重入锁',
    value: 'Reentrant',
  },
  {
    label: '公平锁',
    value: 'Fair',
  },
  {
    label: '读锁',
    value: 'Read',
  },
  {
    label: '写锁',
    value: 'Write',
  },
];

export {
  APPLY_STATUS,
  PUBLISH_STATUS,
  API_TYPE,
  ApiType,
  ApplicationStatus,
  SecretStatus,
  ApiApplyStatus,
  ApplicationEnum,
  ModeMap,
  ModeEnum,
  SERVICE_TYPE,
  ENABLE_TYPE,
  LOCK_TYPE,
  SecretEnum,
  QuerySecretOptions,
  API_ENUM,
}; // 应用类型
