// vuex init

import Vue from 'vue';
import Vuex from 'vuex';
Vue.use(Vuex);

// 使用频率高，赋给全局变量
['mapState', 'mapGetters', 'mapMutations', 'mapActions'].forEach(item => (window[item] = Vuex[item]));

// 注册到 vm，可以通过 this.$vuex 调用
// Vue.prototype.$vuex = Vuex;
const initState = {
  user: null,
  theme: 'white',
  apiDeployList: [],
  permitList: [],
  responseTree: null,
};

const actions = {
  permitListChange({ commit }, val) {
    commit('setPermitList', val);
  },
};

const mutations = {
  setUserInfo(state, info) {
    state.user = info;
  },
  setPermitList(state, payload) {
    state.permitList = payload;
  },
  changeTheme(state, theme) {
    state.theme = theme;
    document.body.className = `theme-${theme}`;
  },
  setApiDeploy(state, deploy) {
    state.apiDeployList.push(deploy);
  },
  setResponseTree(state, tree) {
    state.responseTree = tree;
  },
};

export default new Vuex.Store({
  state: initState,
  mutations,
  actions,
});
