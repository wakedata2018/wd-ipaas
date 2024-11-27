import Vue from 'vue';
import locale from 'element-ui/lib/locale';
import VueI18n from 'vue-i18n';
import en from './langs/en';
import cn from './langs/zh-CN';
import Cookies from 'js-cookie';
import util from 'dss-common';
Vue.use(VueI18n);

const messages = {
  en,
  'zh-CN': cn
};

const i18n = new VueI18n({
  locale: Cookies.get('lang') || localStorage.getItem('lang') || 'zh-CN', // 设置默认语言
  messages,
  silentTranslationWarn: true
});
util.locale.i18n((key, value) => i18n.t(key, value));
locale.i18n((key, value) => i18n.t(key, value)); //为了实现element插件的多语言切换
window.i18n = i18n;
export default i18n;