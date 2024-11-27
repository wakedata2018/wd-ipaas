/**
 * 路由定义
 */
// import report from "../api/report.js";
import Vue from "vue";
import VueRouter from "vue-router";
const routerPush = VueRouter.prototype.push;
Vue.use(VueRouter);
VueRouter.prototype.push = function push(location) {
 return routerPush.call(this, location).catch((error) => error);
};
export default function getRouter() {
 const router = new VueRouter({
  routes: [
   // {
   //   name:'index',
   //   path:'/',
   //   component:()=> import('../views/index/index.vue')
   // },
   {
    path: "*",
    redirect: "/index",
   },
   {
    name: "front-index",
    path: "/index",
    component: () => import("../views/index/index.vue"),
    meta: {
     title: "首页",
    },
   },
   {
    name: "front-index-company",
    path: "/index/company",
    component: () => import("../views/index/company/company.vue"),
    meta: {
     title: "公司数据",
    },
   },
   {
    name: "front-index-userdata",
    path: "/index/userdata",
    component: () => import("../views/index/user-data/index.vue"),
    meta: {
     title: "用户数据",
    },
   },
   {
    name: "front-mydata",
    path: "/mydata",
    component: () => import("../views/my-data/index.vue"),
    meta: {
     title: "我的数据",
    },
   },
   {
    name: "front-mydata-join",
    path: "/mydata/join",
    component: () => import("../views/my-data/data/data.vue"),
    meta: {
     title: "我的接入",
    },
   },
   {
    name: "front-mydata-apply",
    path: "/mydata/apply",
    component: () => import("../views/my-data/apply/apply.vue"),
    meta: {
     title: "我的申请",
    },
   },
   {
    name: "front-mydata-collect",
    path: "/mydata/collect",
    component: () => import("../views/my-data/collect/collect.vue"),
    meta: {
     title: "我的收藏",
    },
   },
   {
    name: "front-mydata-apply-detail",
    path: "/apply/detail",
    component: () => import("../views/index/user-data/index.vue"),
    meta: {
     title: "申请数据详情",
    },
   },
   {
    name: "front-mydata-collect-detail",
    path: "/collect/detail",
    component: () => import("../views/index/user-data/index.vue"),
    meta: {
     title: "收藏数据详情",
    },
   },
   {
    name: "front-mydata-statistics",
    path: "/mydata/statistics",
    component: () => import("../views/my-data/statistics/statistics.vue"),
    meta: {
     title: "我的API统计",
    },
   },
   {
    name: "front-statistics-detail",
    path: "/statistics/detail",
    component: () => import("../views/index/user-data/index.vue"),
    meta: {
     title: "API统计详情",
    },
   },
   {
    name: "noPermission",
    path: "/noPermission",
    component: (resolve) =>
     require(["../views/no-permission/index.vue"], resolve),
    meta: {
     title: i18n.t("common.noPermission"),
    },
   },
  ],
 });

 router.afterEach((to, from) => {
  const meta = to.meta;
//   report.report(`${!!meta ? meta.title : to.name}`);
 });

 return router;
}
