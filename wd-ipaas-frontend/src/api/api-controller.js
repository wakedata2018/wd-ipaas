import services from '@/common/services';
import { getDownload } from '@/utils/exportFile.js';
import { OperationType } from '@/utils/enum';

const prefix = '/dw/open/business/';
const controller = `${prefix}api`;
const controller1 = `${prefix}connector/api`;

export default {
  addLiteflow(params) {
    return services.json(`${controller}/liteflow/add`, params, {
      action: '新增服务编排类型API',
    });
  },
  queryResponseParams(str, params) {
    return services.json(`${controller}/component/operator/listAllOperatorTemplateDTOs${str}`, params, {
      action: '获取流程编排API算子参数信息',
    });
  },
  newApiAdd(params) {
    return services.json(`${controller}/add_api`, params, {
      action: '新增API',
    });
  },
  editApi(params) {
    return services.json(`${controller}/update_api`, params, {
      action: '编辑API',
    });
  },
  showDetail(params) {
    return services.get(`${controller}/show`, {
      params,
      action: '获取API详情',
    });
  },
  fetchConnectorDetail(params) {
    return services.get(`${controller1}/detail`, {
      params,
      action: '查询连接器API详情',
    });
  },
  getList(params) {
    return services.get(`${controller}/list/page/like`, {
      action: '查询Api列表',
      params,
    });
  },
  queryList(params) {
    return services.json(`${controller}/list/page`, params, {
      action: '查询api列表',
    });
  },
  apiBatchPublish(params) {
    return services.json(`${controller}/batch.online`, params, {
      action: '批量发布api',
    });
  },
  apiBatchUnPublish(params) {
    return services.json(`${controller}/batch.offline`, params, {
      action: '批量下线api',
    });
  },
  apiBatchDelete(params) {
    // return services.josn(`${controller}/batch.online`, params, {
    //   action: '批量删除api',
    // });
  },
  apiPublish(params) {
    return services.post(`${controller}/publish`, params, {
      action: '发布api',
    });
  },
  apiUnPublish(params) {
    return services.json(`${controller}/un_publish`, params, {
      action: '下线api',
    });
  },
  listNotAuthApi(params) {
    return services.json(`${controller}/listNotAuthApi`, params, {
      action: '获取应用未授权api',
    });
  },
  queryRespParam(params) {
    return services.get(`${controller}/listRespParamByApiId`, {
      action: '查询api响应参数列表',
      params,
    });
  },
  buildExpression(queryStr, params) {
    return services.json(`${controller}/buildExpression${queryStr}`, params, {
      action: '构建表达式',
    });
  },
  saveLiteFlowResult(params) {
    return services.json(`${controller}/saveLiteFlowResult`, params, {
      action: '保存流程编排结果模板信息',
    });
  },
  findLiteflowResult(params) {
    return services.get(`${controller}/findLiteflowResult`, {
      params,
      action: '根据apiId获取返回流程编排结果模板参数树',
    });
  },
  fetchGroupConnector(params) {
    return services.get(`${controller1}/group/query.by.connector.id`, {
      params,
      action: '查询连接器平台api分组列表',
    });
  },

  fetchGroupConnectorById(params) {
    return services.post(`${controller1}/group/queryById?id=${params.id}`, params, {
      action: '查询连接器平台api分组',
    });
  },
  importOrExportApi(type, params) {
    if (type === OperationType.IMPORT) {
      return services.post(`${prefix}import/open.api.from.file`, params, {
        action: '接口导入',
      });
    } else if (type === OperationType.EXPORT) {
      // 接口导出
      return getDownload(`${prefix}export/open.api.to.file`, params, OperationType.EXPORT);
    } else {
      // 文档导出
      return getDownload(`${prefix}export/open.api.to.markdown`, params, OperationType.EXPORT_DOC);
    }
  },
};
