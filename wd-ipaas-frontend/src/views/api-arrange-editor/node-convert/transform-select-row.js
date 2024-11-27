import helperParam from './helper/param.js';
import { v1 as uniqueId } from 'uuid';

export function getInitForm(item = {}) {
  // const { template = '' } = item;
  return {
    name: '',
    desc: '',
    rowExpression: '',
  };
}

export default {
  initForm(item) {
    return getInitForm(item);
  },
  toNode(node, param) {
    // console.log('toNode: ', node, param);
    const { rowExpression = '' } = param;
    node.form = {
      desc: param.desc,
      name: param.name,
      rowExpression,
    };
    return node;
  },
  toParam(nodeData, edges) {
    const { form, id, data, comboId } = nodeData;
    const param = {
      rowExpression: (form && form.rowExpression) || '',
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
