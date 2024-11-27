// 样式
import 'element-ui/lib/theme-chalk/index.css';
import '@/css/main.less';
import Vue from 'vue';
import ElementUI from 'element-ui';
import login from './login.vue';

import { initial } from '@wakeapp/wakedata-backend';

Vue.use(ElementUI);
initial({
  baseURL: '/',
  fetch: window.fetch.bind(window),
  interceptor: async (request, next) => {
    const response = await next();
    return response;
  },
});

new Vue({
  render: h => h(login),
}).$mount('#login-div');
