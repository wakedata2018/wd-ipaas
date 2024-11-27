import { object } from 'dss-common';

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
      return '#33e1cb';
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

const ApiApplyStatus = [
  {
    value: -1,
    label: '全部',
  },
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

export { APIStatus, AuthStatus, ApprovalStatus, PowerStatus, ApiApplyStatus };
