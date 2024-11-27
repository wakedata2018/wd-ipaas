const TIME_TYPE = {
  second: {
    value: 0,
    maxNum: 59,
    label: '秒',
  },
  min: {
    value: 1,
    maxNum: 59,
    label: '分',
  },
  hour: {
    value: 2,
    maxNum: 23,
    label: '时',
  },
  day: {
    value: 3,
    maxNum: 31,
    label: '日',
  },
  month: {
    value: 4,
    maxNum: 12,
    label: '月',
  },
  year: {
    value: 6,
    maxNum: 9999,
    label: '年',
  },
};
const TIME_TYPE_LIST = [];
for (const key in TIME_TYPE) {
  TIME_TYPE_LIST.push(TIME_TYPE[key]);
}
export default { TIME_TYPE, TIME_TYPE_LIST };
