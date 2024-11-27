const SIGN_TYPE_LIST = [
  {
    value: 0,
    label: '大于',
  },
  {
    value: 1,
    label: '大于等于',
  },
  {
    value: 2,
    label: '等于',
  },
  {
    value: 3,
    label: '小于',
  },
  {
    value: 4,
    label: '小于等于',
  },
  {
    value: 5,
    label: '不等于',
  },
  // {
  //     value:6,
  //     label:'不为空'
  // },
  // {
  //     value:7,
  //     label:'包含'
  // },
  // {
  //     value:8,
  //     label:'不包含'
  // }
];
const VALUE_TYPE = {
  variable: {
    value: 0,
    label: '变量',
  },
  constant: {
    value: 1,
    label: '常量',
  },
};
const VALUE_TYPE_LIST = [
  {
    value: 0,
    label: '变量',
  },
  {
    value: 1,
    label: '常量',
  },
];
export default { SIGN_TYPE_LIST, VALUE_TYPE, VALUE_TYPE_LIST };
