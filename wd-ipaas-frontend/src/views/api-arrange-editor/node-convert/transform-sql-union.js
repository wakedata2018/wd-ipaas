import helperParam from './helper/param.js';
import { v1 as uniqueId } from 'uuid';

export function getInitForm() {
  return {
    name: '',
    desc: '',
  };
}
export default {
  initForm() {
    return getInitForm();
  },
  toNode(node, param) {
    // console.log('toNode: ', node, param);
    node.form = {
      desc: param.desc,
      name: param.name,
    };
    return node;
  },
  toParam(nodeData, edges) {
    const { form, id, data, comboId } = nodeData;
    const param = {
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
