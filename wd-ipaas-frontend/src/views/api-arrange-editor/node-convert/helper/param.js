import { EdgeType } from '@/utils/enum/index.js';
export default {
  /**
   * 保存节点在G6的一些属性
   *
   * @param {model}} nodeData
   * @returns
   */
  getNodeParam(nodeData) {
    return {
      x: nodeData.x,
      y: nodeData.y,
      outPoints: nodeData.outPoints,
      inPoints: nodeData.inPoints,
      uniqueName: nodeData.data.uniqueName,
      label: nodeData.label,
      title: nodeData.title,
      id: nodeData.id,
      forbidInCombo: nodeData.forbidInCombo, // 是否禁止移入 combo
      component: nodeData.cmp,
    };
  },
  getComboParam(combo) {
    return {
      x: combo.x,
      y: combo.y,
      outPoints: combo.outPoints,
      inPoints: combo.inPoints,
      uniqueName: combo.data.uniqueName,
      label: combo.label,
      title: combo.title,
      id: combo.id,
    };
  },
  getOutNodeIds(nodeId, edges) {
    const outIds = [];
    edges.forEach(edge => {
      if (edge.source === nodeId) {
        outIds.push(edge.target);
      }
    });
    return outIds;
  },
  getRootOutNodeIds(nodes, edges) {
    const nodeIds = nodes.map(nodeData => nodeData.id);
    edges.forEach(edge => {
      const { targetId } = edge;
      const index = nodeIds.findIndex(nodeId => nodeId === targetId);
      if (index > -1) {
        nodeIds.splice(index, 1);
      }
    });
    return nodeIds;
  },
  getEdgeInfos(edges) {
    return edges.map(edge => ({
      id: edge.id,
      source: edge.source,
      target: edge.target,
      itemType: edge.itemType,
      label: edge.label,
      type: edge.type,
    }));
  },
  getRoot(nodes, edges) {
    return {
      clazzName: 'com.wakedata.dw.open.model.api.flow.operator.VertexOperator',
      operatorId: 'start',
      name: 'start',
      desc: 'start',
      outputOperators: this.getRootOutNodeIds(nodes, edges),
    };
  },
  getOutOperators(typeOfEdge, nodeId, edges) {
    const obj = {};
    const prop = EdgeType.getProp(typeOfEdge);
    if (!prop) {
      return obj;
    }

    const outEdges = this.getOutNodeIds(nodeId, edges);

    if (prop === 'outputOperators') {
      obj[prop] = outEdges;
    } else if (outEdges.length > 0) {
      obj[prop] = outEdges[0];
    }
    return obj;
  },
  /**
   *  因异常算子会多出两个过渡的combo
   *  这里先包一层方法 方便改动
   */
  filterAbnormalStr(str) {
    return str;
    // return str?.replace('_try', '')?.replace('_catch', '') ?? '';
  },
};
