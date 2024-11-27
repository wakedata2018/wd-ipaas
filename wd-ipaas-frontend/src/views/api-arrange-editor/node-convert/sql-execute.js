import helperParam from './helper/param.js';
import { v1 as uniqueId } from 'uuid';
import { getRedisForm } from '@/utils/redisLockUtil';

export default {
  // 初始化算子配置信息，修改后要重新拖入算子才生效
  initForm(item, cmp) {
    return {
      apiAttr: getRedisForm(),
      name: 'sql_execute',
      desc: 'SQL算子',
      kind: 'sql_execute',
      sqlOperationType: 'QUERY',
      sqlOperatorParam: {
        sql: '',
        dataSourceId: null,
        accessRuleFields: null,
      },
      parameters: [],
      results: [],
      responseParams: [],
      requestParamMappings: [],
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
    const { form, id, data, comboId } = nodeData;
    const param = {
      component: form,
      requestParamMappings: form.requestParamMappings,
      name: form.name,
      clazzName: data.className,
      operatorId: id,
      desc: form.desc,
      uniqueName: data.uniqueName,
      parentOperatorId: helperParam.filterAbnormalStr(comboId),
      ...helperParam.getOutOperators(data.typeOfEdge, id, edges),
    };
    return param;
  },
  setForm(item, cmp) {
    return this.initForm(item, cmp);
  },
};
