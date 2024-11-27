import { services } from "dss-common";
const controller = "/dw/open/business/data_access_app";

export default {
 getList(params) {
  return services.get(`${controller}/list/page/like`, {
   action: "获取要审批的列表",
   params,
  });
 },
 passApprove(params) {
  return services.post(`${controller}/approval`, params, {
   action: "同意审批",
  });
 },
 refuseApprove(params) {
  return services.post(`${controller}/approval`, params, {
   action: "拒绝",
  });
 },
 resetSecret(params) {
  return services.post(`${controller}/reset_secret`, params, {
   action: "重置密钥",
  });
 },
 delete(params) {
  return services.post(`${controller}/delete`, params, {
   action: "删除审批",
  });
 },
};
