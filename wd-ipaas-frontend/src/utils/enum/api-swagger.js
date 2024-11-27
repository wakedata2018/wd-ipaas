/**
 * 解析结果
 */
const PARSE_RESULT = {
  ALL: {
    label: '全部',
    value: -1,
  },
  ERROR: {
    label: '失败',
    value: 2,
  },
  SUCCESS: {
    label: '成功',
    value: 1,
  },
};

/**
 * swagger导入状态
 */
const IMPORT_STATUS = {
  NONE: {
    label: '未导入',
    value: 1,
  }, // 未导入
  PARTIAL_SUCCESS: {
    label: '部分导入',
    value: 2,
  }, // 部分成功导入
  ALL_SUCCESS: {
    label: '全部导入',
    value: 3,
  }, // 全部成功导入
};

/**
 * API类型
 */
const API_TYPE = {
  SQL: {
    label: 'SQL模式',
    value: 0,
  },
  DATA: {
    label: '数据表',
    value: 1,
  },
  HTTP: {
    label: 'HTTP模式',
    value: 2,
  },
};

/**
 * 导入方式
 */
const IMPORT_FORM_TYPE = {
  URL: 'URL',
  FILE: 'FILE',
};

const IMPORT_TYPE_OPTIONS = [
  {
    label: 'URL',
    value: 'URL',
  },
  {
    label: '文件上传',
    value: 'FILE',
  },
];

export { PARSE_RESULT, IMPORT_STATUS, API_TYPE, IMPORT_FORM_TYPE, IMPORT_TYPE_OPTIONS };
