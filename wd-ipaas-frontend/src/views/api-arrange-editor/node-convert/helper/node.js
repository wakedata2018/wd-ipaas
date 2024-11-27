import successImg from '@/assets/images/task/success.png';
import cloneDeep from 'lodash/cloneDeep';
import { convertMap } from '..';
import { extraInfoMap, NODE_TYPE_MAP } from './node-data.js';
import tryCatchNode from '../try-catch';

export default {
  getNodes(nodeInfos, operators, datas) {
    return nodeInfos.map(nodeInfo => {
      const { id, uniqueName } = nodeInfo;
      const convertObj = convertMap[uniqueName];
      const operator = operators[id];
      const extraInfo = extraInfoMap[uniqueName] || extraInfoMap.default;
      const data = datas.find(d => d.uniqueName === uniqueName);
      const node = {
        ...nodeInfo,
        comboId: operator.parentOperatorId,
        color: extraInfo.color,
        image: extraInfo.img,
        size: [200, 100],
        type: NODE_TYPE_MAP[uniqueName] || 'customNode',
        itemType: 'node',
        stateImage: successImg,
      };
      if (data) {
        node.data = cloneDeep(data);
        node.data.defaultName = operator.name;
        node.title = data.defaultName;
      }
      convertObj.toNode(node, operator);

      return node;
    });
  },
  getEdges(edgeInfos) {
    return edgeInfos.map(info => {
      return {
        ...info,
        type: 'customEdge',
        itemType: 'edge',
        source: info.source || info.sourceId,
        target: info.target || info.targetId,
      };
    });
  },
  getCombos(comboInfos, operators, datas) {
    const extArray = [];
    const comboArray = comboInfos.map(info => {
      const { id, uniqueName } = info;
      const convertObj = convertMap[uniqueName];
      const operator = operators[id];
      const extraInfo = extraInfoMap[uniqueName] || extraInfoMap.default;
      const data = datas.find(d => d.uniqueName === uniqueName);
      const isTryCatchCombo = uniqueName === 'try_catch';
      /** 异常算子另外处理 */
      if (isTryCatchCombo) {
        /** push try算子 */
        let tryNode = tryCatchNode.makeTryCatchNode('try', id, 0, 500);
        tryNode = { ...tryNode, ...tryNode.model };
        extArray.push(tryNode);
        /** push catch 算子 */
        let catchNode = tryCatchNode.makeTryCatchNode('catch', id, 0, 500);
        catchNode = { ...catchNode, ...catchNode.model };
        extArray.push(catchNode);
      }
      const combo = {
        ...info,
        color: extraInfo.color,
        image: extraInfo.img,
        size: [260, 100],
        type: 'pollingCombo',
        itemType: 'combo',
        stateImage: successImg,
        ...(isTryCatchCombo && { padding: [40, 2, 2, 2], type: 'abnormalCombo', itemType: 'multiCombo' }),
      };

      if (data) {
        combo.data = cloneDeep(data);
        combo.data.defaultName = operator.name;
        combo.title = data.defaultName;
      }
      combo.children = [];
      Object.values(operators).forEach(item => {
        if (item?.parentOperatorId === id) {
          const param = {
            comboId: id,
            id: item.id,
            itemType: item.itemType,
          };
          combo.children.push(param);
        }
      });
      convertObj.toNode(combo, operator);
      return combo;
    });
    return comboArray.concat(extArray);
  },
};
