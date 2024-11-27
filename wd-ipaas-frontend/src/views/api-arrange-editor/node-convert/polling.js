import helperParam from './helper/param.js';
import { v1 as uniqueId } from 'uuid';

export default {
  // 初始化算子配置信息，修改后要重新拖入算子才生效
  initForm(item, cmp) {
    return {
      name: 'foreach',
      desc: '循环算子',
      breakCondition: {
        type: 0,
        count: 0,
      },
    };
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
    node.label = desc;
    return node;
  },
  toParam(nodeData, edges) {
    const { form, id, data } = nodeData;
    const param = {
      component: form,
      name: form.name,
      clazzName: data.className,
      operatorId: id,
      desc: form.desc,
      uniqueName: data.uniqueName,
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
