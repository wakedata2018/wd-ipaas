// 导入组件，组件必须声明 name
import dssAbout from './src/dss-about.vue';

// 为组件提供 install 安装方法，供按需引入
dssAbout.install = function(Vue) {
  Vue.component(dssAbout.name, dssAbout);
};

// 默认导出组件
export default dssAbout;
