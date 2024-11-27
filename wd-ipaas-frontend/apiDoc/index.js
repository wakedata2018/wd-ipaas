// 样式
import 'element-ui/lib/theme-chalk/index.css';
import '@/css/main.less';
import i18n from '../src/assets/i18n/i18n';

import baseApi from './apis';

import Vue from 'vue';
import ElementUI from 'element-ui';
import apiDoc from './apiDoc.vue';
import store from './store';
import { debounce } from '@wakeadmin/utils';
import Router from 'vue-router';
import { initial } from '@wakeapp/wakedata-backend';

Vue.use(Router);

const router = new Router({
  mode: 'hash',
  scrollBehavior(to) {
    const position = {
      behavior: 'smooth',
    };
    if (to.hash) {
      position.selector = to.hash;
    } else {
      // 滚动到顶部
      position.x = 0;
      position.y = 0;
    }
    return position;
  },
});

const gotoLogin = debounce(
  () => {
    const url = encodeURIComponent(window.location.href);
    window.top.location.href = `/login.html?gotoUrl=${url}`;
  },
  5000,
  { leading: true }
);

initial({
  baseURL: '/',
  fetch: window.fetch.bind(window),
  interceptor: async (request, next) => {
    const response = await next();

    // 会话失效
    if (!response.success && response.errorCode === 401) {
      gotoLogin();
    }

    return response;
  },
});

Vue.use(ElementUI);

baseApi.getUserInfo().done(res => {
  const data = res.data || res.value;
  if (!data) {
    // store.commit('setUserInfo', null);
    window.top.location.href = '/login.html';
    return;
  } else {
    const userInfo = {
      displayName: data.name,
      loginName: data.name,
      tenantId: data.tenantId,
    };
    store.commit('setUserInfo', userInfo);
  }

  new Vue({
    i18n,
    router,
    store,
    render: h => h(apiDoc),
  }).$mount('#api-doc');
});
