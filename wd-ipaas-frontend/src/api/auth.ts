import { requestByPost, requestByGet, requestByPagination } from '@wakeapp/wakedata-backend';
import { AuthInfo } from '@/utils/enum/auth-list';

const controller = '/dw/open/business/connector';

interface PaginationInfo {
  pageNo: number;
  pageSize: number;
}

export default {
  // 查询鉴权列表
  fetchAuthList(params: PaginationInfo) {
    return requestByPagination(`${controller}/auth.config/query`, params, { method: 'POST' });
  },
  // 查询单个鉴权信息
  fetchAuthInfoById(id: number) {
    return requestByGet(`${controller}/auth.config/queryById`, {}, { searchParams: { id: id as unknown as string } });
  },
  // 更新鉴权信息
  updateAuthInfo(params: AuthInfo) {
    return requestByPost(`${controller}/auth.config/modify`, params);
  },
  // 获取指定平台关联的连接器列表
  fetchEnvironmentListById(id: number) {
    return requestByPost(`${controller}/query`, { id });
  },
  // 查询平台下连接器分组信息
  fetchInterfaceGroupById(id: number) {
    return requestByGet(
      `${controller}/api/group/query.by.connector.id`,
      {},
      { searchParams: { connectorId: id as unknown as string } }
    );
  },
  // 查询指定连接器分组下接口列表
  fetchInterfaceBytId(id: number) {
    return requestByPost(`${controller}/api/query`, { apiGroupId: id });
  },
  // 连接器接口详情
  fetchInterfaceDetail(id: number) {
    return requestByGet(`${controller}/api/detail`, {}, { searchParams: { id: id as unknown as string } });
  },
  // 获取租户下所有连接器鉴权配置
  fetchConnectorAuthInfo(){
    return requestByGet(`${controller}/auth.config/getAllAuthConfig`)
  }
};
