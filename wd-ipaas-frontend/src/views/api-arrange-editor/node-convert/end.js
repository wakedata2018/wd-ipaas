import helperParam from './helper/param.js';
import { v1 as uniqueId } from 'uuid';

export default {
  initForm(item, cmp) {
    return {};
  },
  toNode(node, operator) {
    const { component = {}, requestParamMappings = [], name, desc } = operator;
    node.cmp = {
      clazzName: component.clazzName,
      type: component.type,
      componentId: component.componentId,
      name,
      desc,
    };
    node.form = {
      ...component,
      name,
      desc,
      requestParamMappings,
    };
    node.data = {
      ...node.data,
      name,
      desc,
    };
    node.label = desc;
    return node;
  },
  toParam(nodeData, edges) {
    const { form, id, data } = nodeData;
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
