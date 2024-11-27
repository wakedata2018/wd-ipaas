// 数据集
import { Message } from 'element-ui';
import types from '../types';

const _state = {
  // 数据库字段类型列表
  datatype: [],
  // 操作符类型列表
  exprtype: [],
  // 报表布局类型查询
  reportLayout: [],
  // 可视化块类型
  visualType: {
    unknown: { label: '未知', value: 'unknown', icon: 'fa-question' },
    table: { label: '表格', value: 'table', icon: 'fa-table' },
    line: { label: '折线图', value: 'line', icon: 'fa-line-chart' },
    pie: { label: '饼图', value: 'pie', icon: 'fa-pie-chart' },
    bar: { label: '柱状图', value: 'bar', icon: 'fa-bar-chart' },
    funnel: { label: '漏斗图', value: 'funnel', icon: 'fa-eject' },
    map: { label: '地图', value: 'map', icon: 'iconfont icon-map-marked-alt' },
    custom: { label: '自定义', value: 'custom', icon: 'fa-th-large' },
    scatter: { label: '散点图', value: 'scatter', icon: 'iconfont icon-scatter-plot' },
  }
};

const mutations = {
  // 数据库字段类型列表
  [types.config.DATATYPE](state, list) {
    state.datatype = list || [];
  },
  // 操作符类型列表
  [types.config.EXPRTYPE](state, list) {
    state.exprtype = list || [];
  },
  // 报表布局类型查询
  [types.config.REPORTLAYOUT](state, list) {
    state.exprtype = list || {};
  }
};

// const actions = {
//   // 数据库字段类型列表
//   getDatatype({ commit }) {
//     return DW.dataModel
//       .get({
//         url: api.config.datatype,
//         cache: 'sessionStorage',
//         fromCache: 'sessionStorage'
//       })
//       .then(
//         result => {
//           commit(types.config.DATATYPE, result.value);
//         },
//         () => {
//           Message.error('数据类型列表获取失败！');
//         }
//       );
//   },
//   // 操作符类型列表
//   getExprtype({ commit }) {
//     return DW.dataModel
//       .get({
//         url: api.config.exprtype,
//         cache: 'sessionStorage',
//         fromCache: 'sessionStorage'
//       })
//       .then(
//         result => {
//           commit(types.config.EXPRTYPE, result.value);
//         },
//         () => {
//           Message.error('操作符类型列表获取失败！');
//         }
//       );
//   },
//   // 报表布局类型查询
//   getReportLayout({ commit }) {
//     return DW.dataModel
//       .get({
//         url: api.config.layout,
//         cache: 'sessionStorage',
//         fromCache: 'sessionStorage'
//       })
//       .then(
//         result => {
//           commit(types.config.REPORTLAYOUT, result.value);
//         },
//         () => {
//           Message.error('报表布局类型获取失败！');
//         }
//       );
//   }
// };

export default {
  // getters,
  state: _state,
  mutations,
  //actions
};
