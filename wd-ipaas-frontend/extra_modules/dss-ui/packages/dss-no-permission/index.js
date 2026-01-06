// 导入组件，组件必须声明 name
import dssNoPermission from './src/dss-no-permission.vue';

// 为组件提供 install 安装方法，供按需引入
dssNoPermission.install = function(Vue) {
  Vue.component(dssNoPermission.name, dssNoPermission);
};

// 默认导出组件
export default dssNoPermission;
