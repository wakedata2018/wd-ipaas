import helperParam from './helper/param.js';
import { v1 as uniqueId } from 'uuid';

export function getInitForm() {
  return {
    name: '',
    desc: '',
    responseParamMappings: [],
  };
}

export default {
  initForm() {
    return getInitForm();
  },
  toNode(node, param) {
    // console.log('toNode: ', node, param);
    const { responseParamMappings = [] } = param;
    node.form = {
      desc: param.desc,
      name: param.name,
      responseParamMappings,
    };
    return node;
  },
  toParam(nodeData, edges) {
    const { form, id, data, comboId } = nodeData;
    const param = {
      responseParamMappings: (form && form.responseParamMappings) || [],
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
