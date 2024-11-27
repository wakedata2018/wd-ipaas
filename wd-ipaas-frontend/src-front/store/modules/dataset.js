// 数据集

import types from '../types';

const _state = {
  // 数据集列表
  datasets: null,
  // 总数
  count: 0,
  // 数据集状态列表
  statusList: {
    draft: {
      name: '草稿',
      type: 'warning' // 'gray'
    },
    online: {
      name: '在线',
      type: 'success'
    },
    offline: {
      name: '离线',
      type: 'gray'
    },
    shared: {
      name: '共享',
      isHide: true,
      type: 'primary'
    },
    unkonwn: {
      name: '未知',
      isHide: true,
      type: 'danger'
    },
    removed: {
      name: '废弃',
      isHide: true,
      type: 'danger'
    }
  },
  // 用于字段到中文的描述翻译，无翻译的不显示
  filedEnToCn: {
    desc: '描述',
    name: '名称',
    require: '是否必填',
    type: '类型',
    expr: '表达式',
    defaults: '默认值'
  }
};

const mutations = {
  // 设置数据集列表
  [types.dataset.SET_DATASETS](state, payload) {
    state.datasets = payload.datasets;
  }
};

const actions = {
  // 查询数据集列表
  [types.dataset.FETCH_DATASETS]({ commit }) {
    commit(types.dataset.SET_DATASETS, {
      datasets: []
    });
  }
};

export default {
  state: _state,
  mutations,
  actions
};
