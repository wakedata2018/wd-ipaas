import services from '@/common/services';

const PREFIX = 'dw/open/business/swagger';

export default {
  /**
   * 查询swagger信息列表
   */
  getSwaggerParamList(params) {
    return services.get(`${PREFIX}/list/page/like`, {
      params,
      action: '查询swagger信息列表',
    });
  },
  addSwagger(params, fileType) {
    const path = fileType ? `${PREFIX}/add.from.file` : `${PREFIX}/add`;
    return services.json(path, params, {
      action: '新增swagger信息',
    });
  },
  deleteSwagger(params) {
    return services.post(`${PREFIX}/delete`, params, {
      action: '删除swagger信息',
    });
  },
  updateSwagger(params, fileType) {
    const path = fileType ? `${PREFIX}/update.from.file` : `${PREFIX}/update`;
    return services.json(path, params, {
      action: '修改swagger信息',
    });
  },
  showSwaggerParam(params) {
    return services.get(`${PREFIX}/show`, {
      params,
      action: '查询swagger信息',
    });
  },
  updateSwaggerApi(params) {
    return services.post(`${PREFIX}/api/update`, params, {
      action: '更新swagger api信息',
    });
  },
  getSwaggerTempList(params) {
    return services.json(`${PREFIX}/api/list/page/temporary.api`, params, {
      action: '查询swagger临时api列表',
    });
  },
  importSwaggerResult(params) {
    return services.json(`${PREFIX}/api/add/temporary.api`, params, {
      action: '导入swagger临时表',
    });
  },
  deleteApi(params) {
    return services.json(`${PREFIX}/api/delete`, params, {
      action: '删除临时API数据',
    });
  },
  importApi(params) {
    return services.json(`${PREFIX}/add/swagger.api`, params, {
      action: 'swaggerApi确认导入',
    });
  },
  /**
   * 重新拉取swagger
   */
  pullSwagger(params) {
    return services.json(`${PREFIX}/api/again/add.temporary.api`, params, {
      action: 'swagger重新拉取',
    });
  },
  /**
   * swagger
   */
  coverImport(params) {
    return services.json(`${PREFIX}/api/convert/add.api`, params, {
      action: 'swagger覆盖导入',
    });
  },
};
