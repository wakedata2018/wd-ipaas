// 导入组件，组件必须声明 name
import dssProjectSelector from './src/dss-project-selector.vue';

// 为组件提供 install 安装方法，供按需引入
dssProjectSelector.install = function(Vue) {
  Vue.component(dssProjectSelector.name, dssProjectSelector);
};

// 默认导出组件
export default dssProjectSelector;
