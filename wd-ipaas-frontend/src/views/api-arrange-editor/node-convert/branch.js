import helperParam from './helper/param';
import { ApiType, ApiPublicKind } from '@/utils/enum/index.js';
import { v1 as uniqueId } from 'uuid';

const mapApiForm = {};
mapApiForm[ApiType.FLOW_BRANCH] = function (item, cmp) {
  return {
    name: 'branch',
    desc: '分支选择',
    dataAssetApi: {
      apiAttrs: [],
      apiType: ApiType.FLOW_BRANCH,
      dataSourceId: cmp && cmp.dataSource ? cmp.dataSource.id : null,
      dataAssetApiId: cmp && cmp.apiObj ? cmp.apiObj.dataAssetApiId : null,
    },
    parameters: [],
    results: [],
    publicKind: ApiPublicKind.SHARE,
    requestParamMappings: [],
  };
};
export default {
  initForm(item, cmp) {
    const { uniqueName } = item;
    const formKey = 'FLOW_' + uniqueName.toUpperCase();
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
    node.label = operator.desc || node.label; // node卡片描述
    // console.log('toNode', {
    //   operator,
    //   node,
    // })
    return node;
  },
  toParam(nodeData, edges) {
    const { form, id, data, comboId } = nodeData;
    const param = {
      component: {
        ...form,
      },
      clazzName: data.className,
      operatorId: id,
      desc: form.desc,
      uniqueName: data.uniqueName,
      name: form.name,
      parentOperatorId: helperParam.filterAbnormalStr(comboId),
      ...helperParam.getOutOperators(data.typeOfEdge, id, edges),
    };
    return param;
  },
  setForm(item, cmp) {
    return this.initForm(item, cmp);
  },
};
