import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex);

const initState = {
  menuList: [],
  menuPathList: [], // 所有菜单path
};

const mutations = {
  setUserInfo(state, info) {
    state.user = info;
  },
  setMenuList(state, menuList) {
    state.menuList = menuList;
  },

  setMenuPathList(state, menuPathList) {
    state.menuPathList = menuPathList;
  },
};

export default new Vuex.Store({
  state: initState,
  mutations,
});
