// 样式
import '@/common/global.js';
import '@/common/use-codemirror.js';
import './fonts/font-awesome-4.7.0/css/font-awesome.min.css';
import './css/main.less';
import './css/element-variables.scss';
import '@wakeadmin/components/style/index.scss';
import Vue from 'vue';
import { plugin } from '@wakeadmin/components';

// 引入样式文件
// import 'dss-ui/lib/dss-ui.css';

// 引入组件库
import DssUI from 'dss-ui';

// 注册组件库
// app
import App from './app';
import UnAuth from './unauth.vue';
import i18n from './assets/i18n/i18n';
import store from './store';
import router from './router';
import baseApi from './api/base.js';
import filters from 'dss-common/lib/filters';
import VueClipboard from 'vue-clipboard2';
import { debounce } from '@wakeadmin/utils';
import { initial } from '@wakeapp/wakedata-backend';

// import 'default-passive-events'
// echarts
import echarts from 'echarts';

import 'vue-json-viewer/style.css';

import './atomics';

Vue.use(DssUI);
Vue.prototype.$echarts = echarts;
// vue 2.x 用法
Vue.use(plugin);

Vue.use(VueClipboard);
// 修改默认登录验证方式

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

Object.keys(filters).forEach(key => {
  Vue.filter(key, filters[key]);
});

baseApi.getUserInfo().done(res => {
  const data = res.data || res.value;
  if (!data) {
    window.top.location.href = '/login.html';
  }
  const userInfo = {
    displayName: data.name,
    loginName: data.name,
    tenantId: data.tenantId,
  };
  store.commit('setUserInfo', userInfo);

  baseApi.currentUserMenu().done(_res => {
    if (_res.data && _res.data.length > 0) {
      store.commit('setPermitList', _res.data);
      /* eslint-disable no-new */
      new Vue({
        el: '#app',
        data: {
          userinfo: {},
        },
        i18n,
        router,
        store,
        render: h => h(App),
      });
    } else {
      new Vue({
        router,
        i18n,
        store,
        render: h => h(UnAuth),
      }).$mount('#app');
    }
  });
});
