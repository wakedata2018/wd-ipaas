/**
 * 路由定义
 */
// import report from "../api/report.js";
import Vue from 'vue';
import Router from 'vue-router';
import store from '@/store';
import pageUtils from '@/utils/page.js';

const routerPush = Router.prototype.push;
Router.prototype.push = function push(location) {
  return routerPush.call(this, location).catch(error => error);
};
Vue.use(Router);
const routes = [
  {
    path: '/',
    redirect: '/index',
  },
  {
    name: 'index',
    path: '/index',
    component: () => import('../views/index/index.vue'),
    meta: {
      title: '首页',
    },
  },
  {
    name: 'data-manage',
    path: '/data-manage',
    component: () => import('../views/data-manage/index.vue'),
    meta: {
      title: '数据源管理',
    },
  },
  // {
  //   name:'data.add',
  //   path:'/data-manage/data-add',
  //   component:()=> import('../views/data-manage/data-add.vue'),
  //   meta: {
  //     title: '数据源添加',
  //   },
  // },
  {
    path: '/data-application/join-application',
    component: () => import('../views/data-application/join-application/index.vue'),
    name: 'joinApplication',
    meta: {
      title: '接入应用管理',
    },
  },
  {
    path: '/data-application/application',
    component: () => import('../views/data-application/application/index.vue'),
    name: 'data-application',
    meta: {
      title: '应用管理',
    },
  },
  {
    path: '/data-application/api-detail',
    component: () => import('../views/data-application/application/api-detail/index.vue'),
    name: 'api-detail',
    meta: {
      title: 'API详情',
    },
  },
  {
    path: '/data-application/join-third-application',
    component: () => import('../views/data-application/join-third-application/index.vue'),
    name: 'joinThirdApplication',
    meta: {
      title: '接入第三方应用管理',
    },
  },
  {
    path: '/data-application/third-auth-api-detail',
    component: () => import('../views/data-application/join-third-application/api-detail/index.vue'),
    name: 'joinThirdApplication',
    meta: {
      title: '接入第三方应用管理',
    },
  },
  {
    path: '/data-application/low-code-application',
    component: () => import('../views/data-application/low-code-application/index.vue'),
    name: 'lowCodeApplication',
    meta: {
      title: '低代码应用管理',
    },
  },
  {
    path: '/data-application/low-code-application-detail',
    component: () => import('../views/data-application/low-code-application/api-detail/index.vue'),
    name: 'lowCodeApplication-detail',
    meta: {
      title: '低代码应用详情',
    },
  },
  {
    path: '/data-application',
    redirect: '/data-application/application-list',
    // component: () => import('../views/data-application/approval-manage/index.vue'),
    name: 'appManage',
    meta: {
      title: '应用管理',
    },
  },
  {
    path: '/data-application/approval-manage',
    component: () => import('../views/data-application/approval-manage/index.vue'),
    name: 'approvalManage',
    meta: {
      title: '审批管理',
    },
  },
  {
    name: 'apipublish',
    path: '/api-publish/apipublish',
    component: () => import('../views/api-manage/api-publish/index.vue'),
    meta: {
      title: '发布管理',
    },
  },
  {
    name: 'apiapprove',
    path: '/api-approve/apiapprove',
    component: () => import('../views/api-manage/api-approve/index.vue'),
    meta: {
      title: '审批管理',
    },
  },
  {
    name: 'theme',
    path: '/api-theme/theme',
    component: () => import('../views/api-manage/api-theme/index.vue'),
    meta: {
      title: '接口分类管理',
    },
  },
  {
    name: 'swagger',
    path: '/api-swagger/swagger',
    component: () => import('../views/api-manage/api-swagger/index.vue'),
    meta: {
      title: 'Swagger管理',
    },
  },
  {
    name: 'api-import',
    path: '/api-swagger/swagger/api-import',
    component: () => import('../views/api-manage/api-swagger/api-import-list.vue'),
    meta: {
      title: '导入确认',
    },
  },
  {
    name: 'apicall',
    path: '/api-statistics/apicall',
    component: () => import('../views/api-statistics/api-call/index.vue'),
    meta: {
      title: 'API调用统计',
    },
  },
  {
    name: 'apierr',
    path: '/api-statistics/apierr',
    component: () => import('../views/api-statistics/api-err/index.vue'),
    meta: {
      title: '接口调用错误记录',
    },
  },
  {
    name: 'api-log',
    path: '/api-statistics/api-log',
    component: () => import('../views/api-statistics/api-log/index.vue'),
    meta: {
      title: '接口调用日志',
    },
  },
  {
    name: 'apivisit',
    path: '/api-statistics/apivisit',
    component: () => import('../views/api-statistics/api-visit/index.vue'),
    meta: {
      title: '应用接口访问记录',
    },
  },
  {
    name: 'flowcontrol',
    path: '/system-manage/flowcontrol',
    component: () => import('../views/system-manage/flow-control/index.vue'),
    meta: {
      title: '流量控制',
    },
  },
  {
    name: 'monitoralarm',
    path: '/system-manage/monitoralarm',
    component: () => import('../views/system-manage/monitor-alarm/index.vue'),
    meta: {
      title: '监控告警',
    },
  },
  {
    name: 'logaudit',
    path: '/system-manage/logaudit',
    component: () => import('../views/system-manage/log-audit/index.vue'),
    meta: {
      title: '操作日志',
    },
  },
  {
    name: 'apiArrange',
    path: '/api-arrange-editor',
    component: () => import('../views/api-arrange-editor/index.vue'),
    meta: {
      title: '服务编排',
    },
  },
  {
    name: 'eventManage/list',
    path: '/event-manage/list',
    component: () => import('../views/event-manage/list/index.vue'),
    meta: {
      title: '事件列表',
    },
  },
  {
    name: 'eventManage-edit',
    path: '/event-manage/list/edit',
    component: () => import('../views/event-manage/list/edit.vue'),
    meta: {
      title: '新增/修改事件',
    },
  },
  {
    name: 'eventManage-subscribe',
    path: '/event-manage/subscribe',
    component: () => import('../views/event-manage/subscribe/index.vue'),
    meta: {
      title: '订阅地址',
    },
  },
  {
    name: 'eventManage-subscribe',
    path: '/event-manage/subscribe/edit',
    component: () => import('../views/event-manage/subscribe/edit.vue'),
    meta: {
      title: '查看/修改地址',
    },
  },
  {
    name: 'eventManage',
    path: '/event-manage/list',
    component: () => import('../views/event-manage/list/index.vue'),
    meta: {
      title: '事件列表',
    },
  },
  {
    path: '*',
    redirect: '/index',
  },
  {
    name: 'noPermission',
    path: '/noPermission',
    component: () => import('../views/no-permission/index.vue'),
    meta: {
      title: window.i18n.t('common.noPermission'),
    },
  },
  {
    name: 'timer-task',
    path: '/system-setting/timer-task',
    component: () => import('../views/system-setting/timer-task/index.vue'),
    meta: {
      title: '定时任务',
    },
  },
  {
    name: 'key-manage',
    path: '/system-setting/key-manage',
    component: () => import('../views/system-setting/key-manage/index.vue'),
    meta: {
      title: '连接密钥管理',
    },
  },
  {
    name: 'category-manage',
    path: '/link-manage/category-manage',
    component: () => import('../views/link-manage/category-manage'),
    meta: {
      title: '分类管理',
    },
  },
  {
    name: 'platform-list',
    path: '/link-manage/platform-list',
    component: () => import('../views/link-manage/platform-list'),
    meta: {
      title: '平台列表',
    },
  },
  {
    name: 'platform-interface',
    path: '/link-manage/platform-interface',
    component: () => import('../views/link-manage/platform-interface'),
    meta: {
      title: '连接器接口列表',
    },
  },
  {
    name: 'auth-list',
    path: '/link-manage/auth-list',
    component: () => import('../views/link-manage/auth-list'),
    meta: {
      title: '连接器鉴权管理',
    },
  },
  {
    name: 'params-mapping',
    path: '/api-manage/params-mapping',
    component: () => import('../views/api-manage/params-mapping/index.vue'),
    meta: {
      title: '参数映射管理',
    },
  },
  {
    name: 'system-setting',
    path: '/system-setting',
    component: () => import('../views/system-setting/index.vue'),
    meta: {
      title: '系统设置',
    },
  },
];
const router = new Router({
  mode: 'hash',
  routes,
});
router.beforeEach((to, from, next) => {
  const title = pageUtils.findPageName(store.state.permitList, to.name) || to.meta.title;
  window.document.title = title + '-' + localStorage.getItem('systemName');
  next();
});

export default router;
