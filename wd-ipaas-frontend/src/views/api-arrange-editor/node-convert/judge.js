import helperParam from './helper/param';
import { ApiType, ApiPublicKind } from '@/utils/enum/index.js';
import { v1 as uniqueId } from 'uuid';

const mapApiForm = {};
mapApiForm[ApiType.FLOW_JUDGE] = function (item, cmp) {
  return {
    name: 'judge',
    desc: '判断算子',
    dataAssetApi: {
      comparisonValue: {},
      apiAttrs: [],
    },
    publicKind: ApiPublicKind.SHARE,
  };
};
export default {
  initForm(item, cmp) {
    const { uniqueName } = item;
    const formKey = 'FLOW_' + uniqueName.toUpperCase();
    return mapApiForm[formKey]?.(item, cmp) || {};
  },
  toNode(node, operator) {
    const { name, desc, component = {} } = operator;
    node.cmp = {
      clazzName: component.clazzName,
      type: component.type,
      componentId: component.componentId,
      name: component.name,
      desc: component.desc,
    };
    node.form = {
      ...component,
      name,
      desc,
    };
    node.data = {
      ...node.data,
      name,
      desc,
    };
    return node;
  },
  toParam(nodeData, edges) {
    const {
      form: { desc, name, ...other },
      id,
      data,
      comboId,
    } = nodeData;
    const param = {
      component: {
        ...other,
      },
      clazzName: data.className,
      operatorId: id,
      desc,
      uniqueName: data.uniqueName,
      name,
      parentOperatorId: helperParam.filterAbnormalStr(comboId),
      ...helperParam.getOutOperators(data.typeOfEdge, id, edges),
    };
    return param;
  },
  setForm(item, cmp) {
    return this.initForm(item, cmp);
  },
};
