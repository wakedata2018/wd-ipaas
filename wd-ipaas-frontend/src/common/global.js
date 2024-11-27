// import 'promise-polyfill/src/polyfill';
// import 'dss-common/lib/style/dss-ui.scss';
// import '../css/dss-ui.scss';
// import './auth';
import Vue from 'vue';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import VuePlain from 'dss-common/lib/plugins/vue-plain';
window.Promise = Promise;

Vue.use(ElementUI, { size: 'mini' });
Vue.use(VuePlain);
