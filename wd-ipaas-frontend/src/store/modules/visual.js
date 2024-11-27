// 可视化(编辑)

import types from '../types';
import Vue from 'vue';

const _state = {
  viewConfig: {},
  echartsThemesList: [
    {
      label: '基本',
      list: [
        'default',
        'chalk',
        'essos',
        'infographic',
        'macarons',
        'roma',
        'shine',
        'walden',
        'westeros',
        'wonderland'
      ]
    },
    {
      label: '特色',
      list: ['vintage', 'purple-passion', 'dark', 'halloween', 'kpiplay']
    }
  ]
};

const mutations = {
  // 可视化视图配置
  [types.visual.VIEW_CONFIG](state, payload) {
    state.viewConfig = Vue.plain(payload.viewConfig);
  }
};

const actions = {
  // 设置可视化视图配置
  updateEditViewConfig({ commit }, viewConfig) {
    commit(types.visual.VIEW_CONFIG, {
      viewConfig: viewConfig || {}
    });
  }
};

export default {
  state: _state,
  mutations,
  actions
};
