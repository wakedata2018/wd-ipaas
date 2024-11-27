const WAKECLOUD_APP = {
  typeId: 0,
  value: '0',
  label: '惟客云应用',
};

const THIRD_AUTH_APP = {
  typeId: 1,
  value: '1',
  label: '第三方应用',
};

const APP_TYPE_LIST = [{ ...WAKECLOUD_APP }, { ...THIRD_AUTH_APP }];

const FIELD_LOCATION = [
  {
    name: 'HEAD',
    label: 'HEAD',
  },
  {
    name: 'QUERY',
    label: 'QUERY',
  },
  {
    name: 'BODY',
    label: 'BODY',
  },
];

export default { APP_TYPE_LIST, WAKECLOUD_APP, THIRD_AUTH_APP, FIELD_LOCATION }; // 应用类型
