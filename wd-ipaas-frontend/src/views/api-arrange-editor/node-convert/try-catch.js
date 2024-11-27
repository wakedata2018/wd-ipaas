import helperParam from './helper/param';
import { ApiType, ApiPublicKind } from '@/utils/enum/index.js';
import { v1 as uniqueId } from 'uuid';

export default {
  initForm(item, cmp) {
    const { uniqueName, desc } = item;
    return { name: uniqueName ?? '', desc: desc ?? '' };
  },
  toNode(node, operator) {
    const { name, desc, component = {}, subFirstCatchOperators, subFirstTryOperators } = operator;
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

  makeTryCatchNode(type = 'try', id, x, y) {
    const isTryCombo = type === 'try';
    return {
      type: 'combo',
      uniqueName: type,
      layer: 0,
      model: {
        id: `${id}_${type}`,
        parentId: id,
        label: type,
        type: 'rect',
        itemType: type,
        x,
        y: isTryCombo ? y - 155 : y + 135,
        style: {
          fill: isTryCombo ? '#F0FFF0' : '#FFF0F5',
          stroke: isTryCombo ? '#F0FFF0' : '#FFF0F5',
          radius: 5,
        },

        size: [500, 250],
        locked: true,
      },
    };
  },
};
