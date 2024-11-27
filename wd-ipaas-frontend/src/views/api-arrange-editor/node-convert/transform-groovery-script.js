import helperParam from './helper/param.js';
import { v1 as uniqueId } from 'uuid';

export function getInitForm(item = {}) {
  const { template = '' } = item;
  return {
    name: 'groovy_script',
    desc: 'Groovy 脚本',
    groovy: template,
    resultData: null,
  };
}
export default {
  initForm(item) {
    // console.log('initForm: ', item);
    return getInitForm(item);
  },
  toNode(node, param) {
    // console.log('toNode: ', node, param);
    const { groovy = '', resultData = null } = param;
    node.form = {
      desc: param.desc,
      name: param.name,
      groovy,
      resultData,
    };
    return node;
  },
  toParam(nodeData, edges) {
    const { form, id, data, comboId } = nodeData;
    const param = {
      groovy: (form && form.groovy) || '',
      resultData: (form && form.resultData) || '',
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
