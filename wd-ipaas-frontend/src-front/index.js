// 样式
import "@/common/global.js";
import '@/common/use-codemirror.js';
import "./fonts/font-awesome-4.7.0/css/font-awesome.min.css";
import "./css/main.less";
import Vue from "vue";

import mydata from "./api/mydata.js";

// 引入样式文件
// import 'dss-ui/lib/dss-ui.css';

// 引入组件库
import DssUI from "dss-ui";
Vue.use(DssUI);
// 注册组件库
// app
import App from "./app";
import UnAuth from "./unauth.vue";

import i18n from "./assets/i18n/i18n";
import store from "./store/index";
import getRouter from "./router/index";
import baseApi from "./api/base.js";
import filters from "dss-common/lib/filters";
import VueClipboard from "vue-clipboard2";
Vue.use(VueClipboard);

//echarts
import echarts from "echarts";
Vue.prototype.$echarts = echarts;

// 修改默认登录验证方式

Object.keys(filters).forEach((key) => {
 Vue.filter(key, filters[key]);
});
// mydata.deleteJoin({dataAccessAppId:17})
// mydata.getJoinInfo().done(res=>{
//   console.log(res)
//   if(res.data.length>0){
//     store.commit('setDataAccessApp',res.data)
//   }
// })
function createErrorPage(res) {
 if (!res.code || res.code !== 401) {
  setUserInfoCookie(res);
  new Vue({
   router,
   i18n,
   store,
   render: (createElement) =>
    createElement(UnAuth, {
     props: {
      text:
       !!res.code && !!res.message ? res.message : i18n.t("common.innerError"),
     },
    }),
   // h => h(UnAuth)
  }).$mount("#app");
 }
}
baseApi.getUserInfo().done((res) => {
 // console.log(res)
 let data = res.data || res.value;
 const userInfo = {
   displayName:data.displayName,
   email:data.email,
   employeeNumber:data.employeeNumber,
   loginName:data.loginName,
   telephone: data.telephone,
 };
 store.commit("setUserInfo", userInfo);
 const router = getRouter();
 if (res.data.menu && res.data.menu.length > 0) {
  let menu = res.data.menu || [];
  let permitList = menu.map((item) => item);
  store.commit("setPermitList", permitList);

  mydata.getJoinInfo().done((res) => {
   // console.log(res)
   if (res.data.length > 0) {
    store.commit("setDataAccessApp", res.data);
   }
  });
  new Vue({
   el: "#app",
   data: {
    userinfo: {},
   },
   i18n,
   router: router,
   store: store,
   render: (h) => h(App),
  });
 } else {
  new Vue({
   router,
   i18n,
   store,
   render: (h) => h(UnAuth),
  }).$mount("#app");

  return;
 }
});
