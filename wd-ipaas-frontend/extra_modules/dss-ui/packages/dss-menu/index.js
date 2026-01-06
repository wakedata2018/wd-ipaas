// 导入组件，组件必须声明 name
import dssMenu from './src/dss-menu.vue';

// 为组件提供 install 安装方法，供按需引入
dssMenu.install = function(Vue) {
  Vue.component(dssMenu.name, dssMenu);
};

// 默认导出组件
export default dssMenu;
