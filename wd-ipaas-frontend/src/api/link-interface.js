import { requestByPost, requestByGet, requestByPagination } from '@wakeapp/wakedata-backend';
import services from '@/common/services';
import { getDownload } from '@/utils/exportFile.js';
import { OperationType } from '@/utils/enum';

const prefix = '/dw/open/business';
const controller = '/dw/open/business/connector';

export default {
  // 查询对应平台下接口分组列表
  queryInterfaceGroup(param) {
    const params = param || {};
    return requestByGet(`${controller}/api/group/query.by.connector.id`, params);
  },
  // 删除接口分组
  delInterfaceGroup(id) {
    return requestByPost(`${controller}/api/group/delete?id=${id}`, {});
  },
  // 查询连接器平台信息
  queryPlatForm() {
    return requestByGet(`${controller}/query.name`);
  },
  // 新增连接器接口
  addInterface(params) {
    return requestByPost(`${controller}/api/add`, params);
  },
  // 更新平台接口
  updateInterface(params) {
    return requestByPost(`${controller}/api/modify`, params);
  },
  // 删除平台接口
  delInterface(id) {
    return requestByPost(`${controller}/api/delete`, {}, { searchParams: { id } });
  },
  // 查询平台接口
  detailInterface(params) {
    return requestByGet(`${controller}/api/detail`, params);
  },
  // 查询单个api分组（包含其下api列表）
  getInterfaceList(params, config) {
    return requestByPagination(`${controller}/api/query`, params, config);
  },
  // 查询连接器API详情
  getApiDetail(params) {
    return requestByGet(`${controller}/api/detail`, params);
  },
  // 导入导出
  importOrExportApi(type, params) {
    if (type === OperationType.IMPORT) {
      return requestByPost(`${prefix}/import/connector.from.file`, params, {
        headers: {
          'content-type': 'application/x-www-form-urlencoded',
        },
      });
    } else {
      // 接口导出
      return getDownload(`${prefix}/export/connector.to.file`, params);
    }
  },
};
