import 'promise-polyfill/src/polyfill';
// import 'dss-common/lib/style/dss-ui.scss';
import './style/dss-ui.scss';
// import 'dss-common/lib/auth';
import Vue from 'vue';
import ElementUI from 'element-ui';
import VuePlain from 'dss-common/lib/plugins/vue-plain';
window.Promise = Promise;

Vue.use(ElementUI, { size: 'mini' });
Vue.use(VuePlain);
