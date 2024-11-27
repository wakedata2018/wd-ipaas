/**
 * 低代码应用相关
 */
const IPAAS_APP = {
  value: 'IPAAS_APP',
  label: 'IPAAS_APP',
};

const LOW_CODE_APP = {
  value: 'LOW_CODE_APP',
  label: 'LOW_CODE_APP',
};

const APP_TYPE_LIST = [IPAAS_APP, LOW_CODE_APP];

const PASS = {
  value: 'PASS',
  label: '通过',
};

const CREATED = {
  value: 'CREATED',
  label: '创建',
};

const REFUSE = {
  value: 'REFUSE',
  label: '拒绝',
};

const APP_STATUS = [PASS, CREATED, REFUSE];

const API_STATUS = {
  CREATED: {
    label: '待审批',
    value: 'CREATED',
  },
  PASS: {
    label: '通过',
    value: 'PASS',
  },
  REFUSE: {
    label: '未通过',
    value: 'CREATED',
  },
};

const API_STATUS_NAME = {
  [API_STATUS.CREATED.value]: API_STATUS.CREATED.label,
  [API_STATUS.PASS.value]: API_STATUS.PASS.label,
  [API_STATUS.REFUSE.value]: API_STATUS.REFUSE.label,
};

const PUBLISH_STATUS = {
  PUBLISH: {
    label: '已发布',
    value: 1,
  },
  UN_PUBLISH: {
    label: '未发布',
    value: 0,
  },
};

export { IPAAS_APP, LOW_CODE_APP, APP_TYPE_LIST, APP_STATUS, PASS, API_STATUS_NAME, PUBLISH_STATUS };
