import { requestByGet, requestByPost, requestByPagination } from '@wakeapp/wakedata-backend';
import { ParamsMappingInfo } from '@/utils/enum/params-mapping';

const controller = '/dw/open/business/api/resp/param/mapping/rule';

export default {
  // 查询参数映射分页列表
  fetchParamsMappingList(params: any) {
    return requestByPagination(`${controller}/query`, params, { method: 'POST' });
  },
  // 查询参数映射规则列表
  getParamsMappingList() {
    return requestByGet(`${controller}/query.id.name`);
  },
  // 查询单条
  getParamsMappingById(id: number) {
    return requestByPost(`${controller}/query.by.id?id=${id}`, {}, { method: 'POST' });
  },
  // 新增 和 更新
  addOrUpdateParamsMapping(params: ParamsMappingInfo) {
    const address = params.id ? '/update' : '/add';
    return requestByPost(`${controller}${address}`, params);
  },
  // 删除
  delParamsMapping(id: string) {
    return requestByPost(`${controller}/delete?id=${id}`);
  },
  // 获取集成云参数返回体格式
  getCommonParamsList() {
    return requestByGet(`${controller}/get.resp.base.param`);
  },
};
