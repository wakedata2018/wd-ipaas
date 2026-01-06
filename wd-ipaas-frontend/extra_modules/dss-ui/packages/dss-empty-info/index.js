// 导入组件，组件必须声明 name
import dssEmptyInfo from './src/dss-empty-info.vue';

// 为组件提供 install 安装方法，供按需引入
dssEmptyInfo.install = function(Vue) {
  Vue.component(dssEmptyInfo.name, dssEmptyInfo);
};

// 默认导出组件
export default dssEmptyInfo;
