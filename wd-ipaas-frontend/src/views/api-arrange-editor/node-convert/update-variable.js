import helperParam from './helper/param.js';
import { v1 as uniqueId } from 'uuid';

export default {
  initForm(item) {
    return {
      name: 'update_variable',
      desc: '更新变量',
      variableParams: {
        variableJsonSchema: null,
      },
    };
  },
  toNode(node, param) {
    const { component = {} } = param;
    node.form = {
      desc: param.desc,
      name: param.name,
      variableParams: component?.variableParams,
    };
    return node;
  },
  toParam(nodeData, edges) {
    const { form, id, data, comboId } = nodeData;
    const param = {
      component: {
        name: form.name,
        variableParams: form.variableParams,
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
};
