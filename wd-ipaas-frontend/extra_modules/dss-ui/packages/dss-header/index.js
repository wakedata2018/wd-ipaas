// 导入组件，组件必须声明 name
import dssHeader from './src/dss-header.vue';

// 为组件提供 install 安装方法，供按需引入
dssHeader.install = function(Vue) {
  Vue.component(dssHeader.name, dssHeader);
};

// 默认导出组件
export default dssHeader;
