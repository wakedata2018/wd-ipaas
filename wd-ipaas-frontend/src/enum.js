import { object } from 'dss-common';

const DATA_TYPE_PHOENIX = 'PHOENIX';
const DATA_TYPE_HBASE = 'HBASE';

function getDesc(type) {
  let valueLabel = null;
  if (object.isNumber(type)) {
    valueLabel = this.list.find(item => item.key === type);
  } else {
    valueLabel = this.list.find(item => item.value === type);
  }
  if (valueLabel) {
    return valueLabel.label;
  }
  return '';
}

const PUBLISH = {
  value: 'PUBLISH',
  label: '已发布',
};

const UN_PUBLISH = {
  value: 'UN_PUBLISH',
  label: '未发布',
};

const APIStatus = {
  list: [PUBLISH, UN_PUBLISH],
  getDesc,
  getPublish() {
    return PUBLISH;
  },
  toColor(status) {
    if (status === PUBLISH.value) {
      return '#228B22';
    }
    return '#fb4938';
  },
};

const AuthStatus = {
  list: [
    {
      value: 'AUTHORIZED',
      label: '已拥有',
    },
    {
      value: 'UNAUTHORIZED',
      label: '未授权',
    },
  ],
  getDesc,
  isAuthorized(value) {
    return value === 'AUTHORIZED';
  },
};

const CREATED = {
  value: 'CREATED',
  label: '待审批',
  key: 0,
};
const IN_APPROVAL = {
  value: 'IN_APPROVAL',
  label: '审批中',
  key: 1,
};
const APPROVAL = {
  value: 'APPROVAL',
  label: '已通过',
  key: 2,
};
const FAILURE_APPROVAL = {
  value: 'FAILURE_APPROVAL',
  label: '已拒绝',
  key: 3,
};
const PASS_FAIL = {
  value: 'PASS_FAIL',
  label: '处理失败',
  key: 4,
};

const APPROVAL_STATUS = {
  ALL: {
    label: '全部',
    value: -1,
  },
  IN_APPROVAL: {
    label: '审核中',
    value: 1,
  },
  APPROVAL: {
    label: '审核成功',
    value: 2,
  },
  FAILURE_APPROVAL: {
    label: '审核失败',
    value: 3,
  },
};

const ApprovalStatus = {
  list: [CREATED, IN_APPROVAL, APPROVAL, FAILURE_APPROVAL, PASS_FAIL],
  getDesc,
  isWaiting(status) {
    const list = [CREATED, IN_APPROVAL];
    if (object.isNumber(status)) {
      return !!list.find(item => item.key === status);
    }
    // 待审批或新建状态才可进行审核
    return !!list.find(item => item.value === status);
  },
  approval: APPROVAL,
  failureApproval: FAILURE_APPROVAL,
  toColor(status) {
    const watingList = [CREATED, IN_APPROVAL];
    if (watingList.find(item => item.value === status)) {
      return '#00c6f7';
    }
    if (status === APPROVAL.value) {
      return '#33e1cb';
    }
    if (status === FAILURE_APPROVAL.value) {
      return '#fb4938';
    }
    return '#ffc43d';
  },
};

const PowerStatus = {
  list: [
    {
      value: 'PART',
      label: '部分权限',
    },
    {
      value: 'NONE',
      label: '没有权限',
    },
    {
      value: 'ALL',
      label: '全部权限',
    },
  ],
  getDesc,
};

const INSERT_OPERATE = {
  value: 'INSERT',
  label: '增加',
};
const UPDATE_OPERATE = {
  value: 'UPDATE',
  label: '修改',
};
const DELETE_OPERATE = {
  value: 'DELETE',
  label: '删除',
};
const QUERY_OPERATE = {
  value: 'QUERY',
  label: '查询',
};

const BaseOperate = {
  list: [INSERT_OPERATE, UPDATE_OPERATE, DELETE_OPERATE, QUERY_OPERATE],
  getDesc,
};

const HTTP_TYPE = {
  value: 1,
  label: 'HTTP',
};
const KAFKA_TYPE = {
  value: 2,
  label: 'Kafka',
};
const MQ_TYPE = {
  value: 3,
  label: 'MQ',
};

const addressTypeList = {
  list: [HTTP_TYPE, KAFKA_TYPE, MQ_TYPE],
  getDesc,
};

const eventStatis = [
  {
    value: null,
    label: '全部',
  },
  {
    value: 0,
    label: '关闭',
  },
  {
    value: 1,
    label: '启用',
  },
];

// 应用类型  目前只有惟客云  后面可扩展

const APPLICATION_TYPE = [
  {
    title: '自有开发模式',
    desc: '惟客云开放平台接口',
    type: 'WAKE_CLOUD',
  },
  // 暂时先屏蔽
  // {
  //   title: '第三方开放模式',
  //   desc: '用于第三方平台接口提供',
  //   type: 'OTHERS',
  // },
];

// 应用发布状态
const APPLICATION_PUBLISH_STATUS = {
  ON_LINE: {
    label: '已上线',
    value: 0,
  },
  OFF_LINE: {
    label: '已下线',
    value: 1,
  },
};

// API授权状态
const APPLICATION_AUTHOR_STATUS = ['未授权', '已授权'];

// 比较类型枚举
const EXPRESSION_TYPE_ENUM = {
  GT: 'gt', // 大于
  GE: 'ge', // 大于等于
  EQ: 'eq', // 等于
  LT: 'lt', // 小于
  LE: 'le', // 小于或等于
  NE: 'ne', // 不等于
  ISNULL: 'isNull', // 为空
  ISNOTNULL: 'isNotNull', // 不为空
  ISEMPTY: 'isEmpty', //  is empty
  ISNOTEMPTY: 'isNotEmpty', // is not empty
};

const EXPRESSION_TYPE_MAP = {
  [EXPRESSION_TYPE_ENUM.GT]: '大于', // 大于
  [EXPRESSION_TYPE_ENUM.GE]: '大于等于', // 大于等于
  [EXPRESSION_TYPE_ENUM.EQ]: '等于', // 等于
  [EXPRESSION_TYPE_ENUM.LT]: '小于', // 小于
  [EXPRESSION_TYPE_ENUM.LE]: '小于等于', // 小于或等于
  [EXPRESSION_TYPE_ENUM.NE]: '不等于', // 不等于
  [EXPRESSION_TYPE_ENUM.ISNULL]: '为空', // 为空
  [EXPRESSION_TYPE_ENUM.ISNOTNULL]: '不为空', // 不为空
  [EXPRESSION_TYPE_ENUM.ISEMPTY]: 'is empty', // is empty
  [EXPRESSION_TYPE_ENUM.ISNOTEMPTY]: 'is not empty', // is not empty
};

export {
  DATA_TYPE_PHOENIX,
  DATA_TYPE_HBASE,
  APIStatus,
  AuthStatus,
  ApprovalStatus,
  PowerStatus,
  INSERT_OPERATE,
  UPDATE_OPERATE,
  DELETE_OPERATE,
  QUERY_OPERATE,
  BaseOperate,
  addressTypeList,
  MQ_TYPE,
  HTTP_TYPE,
  KAFKA_TYPE,
  eventStatis,
  APPLICATION_TYPE,
  APPLICATION_PUBLISH_STATUS,
  APPLICATION_AUTHOR_STATUS,
  APPROVAL_STATUS,
  EXPRESSION_TYPE_ENUM,
  EXPRESSION_TYPE_MAP,
};
