import helperParam from './helper/param.js';
import { ApiType, ApiPublicKind } from '@/utils/enum/index.js';
import { v1 as uniqueId } from 'uuid';
import { getInitDataAssetApi, getDefaultHttpApiAttr } from '../global/task-conf.js';

const mapApiForm = {};
/**
 * API的component结构：
 * {
 *    dataAssetApi: {},
 *    parameters: [
 *      {
 *        operatorId: 关联节点id,
 *        field: 字段名,
 *        expression: 条件表达式
 *      },
 *      ...
 *    ],
 *    results: [结构同parameters],
 *    publicKind: ApiPublishKind
 * }
 */
mapApiForm[ApiType.CUSTOM_SQL] = function (item, cmp) {
  return {
    dataAssetApi: {
      ...getInitDataAssetApi(),
      apiType: ApiType.CUSTOM_SQL,
      apiSql: cmp?.apiObj?.apiSql || null,
      dataSourceId: cmp?.dataSource?.id || cmp?.apiObj?.dataSourceId || null,
      dataAssetApiId: cmp?.apiObj?.dataAssetApiId || null,
    },
    parameters: [],
    results: [],
    publicKind: ApiPublicKind.SHARE,
    requestParamMappings: [],
  };
};
mapApiForm[ApiType.NORMAL_TABLE] = function (item, cmp) {
  return {
    dataAssetApi: {
      ...getInitDataAssetApi(),
      apiType: ApiType.NORMAL_TABLE,
      dataAssetName: null,
      dataSourceId: cmp?.dataSource?.id || cmp?.apiObj?.dataSourceId || null,
      dataAssetApiId: cmp?.apiObj?.dataAssetApiId || null,
    },
    parameters: [],
    results: [],
    publicKind: ApiPublicKind.SHARE,
    requestParamMappings: [],
  };
};
/**
 * 初始化数据结构
 */
mapApiForm[ApiType.EXTERNAL_HTTP] = function (item, cmp) {
  return {
    dataAssetApi: {
      ...getInitDataAssetApi(),
      apiType: ApiType.EXTERNAL_HTTP,
      apiAttr: getDefaultHttpApiAttr(),
      dataAssetApiId: cmp?.apiObj?.dataAssetApiId || null,
    },
    parameters: [],
    results: [],
    publicKind: ApiPublicKind.SHARE,
    requestParamMappings: [],
  };
};

mapApiForm[ApiType.WEB_SERVICE] = function (item, cmp) {
  return {
    dataAssetApi: {
      ...getInitDataAssetApi(),
      apiType: ApiType.WEB_SERVICE,
      apiAttr: getDefaultHttpApiAttr(),
      dataAssetApiId: cmp?.apiObj?.dataAssetApiId || null,
    },
    parameters: [],
    results: [],
    publicKind: ApiPublicKind.SHARE,
    requestParamMappings: [],
  };
};

export default {
  initForm(item, cmp) {
    /**
     * cmp:
     * {
     *    dataSource:存放api数据源相关的内容,
     *    apiObj:存放api相关内容
     * }
     */
    const { uniqueName } = item;
    const formKey = uniqueName.replace(/^api_/g, '').toUpperCase();
    const method = mapApiForm[formKey];
    let obj = {};
    if (method) {
      obj = method(item, cmp);
    }
    return obj;
  },
  toNode(node, operator) {
    const { component = {}, requestParamMappings = [] } = operator;
    node.cmp = {
      clazzName: component.clazzName,
      type: component.type,
      componentId: component.componentId,
      name: component.name,
      desc: component.desc,
    };
    node.form = {
      ...component,
      name: operator.name,
      desc: operator.desc,
      requestParamMappings,
    };
    node.data = {
      ...node.data,
      name: operator.name,
      desc: operator.desc,
    };
    return node;
  },
  toParam(nodeData, edges) {
    const { form, id, data, comboId } = nodeData;
    const finalForm = Object.assign({}, form);
    delete finalForm.requestParamMappings;
    const param = {
      component: {
        ...finalForm,
      },
      name: form.name,
      clazzName: data.className,
      operatorId: id,
      desc: form.desc,
      uniqueName: data.uniqueName,
      requestParamMappings: form.requestParamMappings,
      parentOperatorId: helperParam.filterAbnormalStr(comboId),
      ...helperParam.getOutOperators(data.typeOfEdge, id, edges),
    };
    return param;
  },
  /**
   * setForm: toApiNode使用
   */
  setForm(item, cmp) {
    return this.initForm(item, cmp);
  },
};
