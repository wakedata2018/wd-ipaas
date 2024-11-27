import helperParam from './helper/param';
import { v1 as uniqueId } from 'uuid';
import timeTypeEnum from '@/utils/enum/time-type';

export default {
  initForm() {
    const form = {
      name: '',
      desc: '',
      taskDescription: '',
      cycleValue: '',
      cycleUnit: timeTypeEnum.TIME_TYPE.second.value,
    };
    return form;
  },
  toNode(node, param) {
    node.form = {
      name: param.name,
      desc: param.desc,
      taskDescription: param.taskDescription,
      cronExpression: param.cronExpression,
      cycleUnit: param.cycleUnit,
      cycleValue: param.cycleValue,
    };
    return node;
  },
  toParam(nodeData, edges) {
    const { form, id, data } = nodeData;
    const param = {
      clazzName: data.className,
      operatorId: id,
      desc: form.desc,
      uniqueName: data.uniqueName,
      name: form.name,
      taskDescription: form.taskDescription,
      cycleUnit: form.cycleUnit,
      cycleValue: form.cycleValue,
      ...helperParam.getOutOperators(data.typeOfEdge, id, edges),
    };
    return param;
  },
};
