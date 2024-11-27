import zhLocale from 'element-ui/lib/locale/lang/zh-CN';
import utils from 'dss-common';

const cn = {
  platform: {
    offine: '离线开发',
    realtime: '实时开发',
    open: '昆仑平台 - 集成云',
    machineLearning: '机器学习',
    dataAsset: '数据资产',
    priv: '数据安全',
    chart: '自助分析',
  },
  validator: {
    noPermission: '您当前没有权限，请联系管理人员',
  },
  ...zhLocale,
  ...utils.zhCN,
};

export default cn;
