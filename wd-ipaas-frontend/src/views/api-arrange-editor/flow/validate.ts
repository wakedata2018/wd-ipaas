import { Message } from 'element-ui';
import { Graph, INode, IEdge, IGraph } from '@antv/g6';
import { EdgeType } from '@/utils/enum/index.js';
import eventBus, { EventName } from '@/components/g6-editor/event-bus';

function isCycleGraph(itemId?: string, edges: IEdge[] = []) {
  for (const edge of edges) {
    const target = edge.getTarget();

    const targetModel = target.getModel();

    if (targetModel.id === itemId) {
      return true;
    }

    const targetEdges = target.getEdges().filter(temEdge => {
      const sourceModel = temEdge.getSource().getModel();
      return sourceModel.id === targetModel.id;
    });
    if (!targetEdges.length) {
      continue;
    }
    if (isCycleGraph(itemId, targetEdges)) {
      return true;
    }
  }
  return false;
}

function isCycle(target: INode) {
  const targetModel = target.getModel();
  const edges = target.getEdges().filter(temEdge => {
    const sourceModel = temEdge.getSource().getModel();
    return sourceModel.id === targetModel.id;
  });
  if (!edges.length) {
    return false;
  }
  return isCycleGraph(targetModel.id, edges);
}

function validateTypeOfEdge(source: INode, target: INode) {
  const targetModel = target.getModel();
  const typeOfEdge = (targetModel.data as any)?.typeOfEdge;
  const inEdges = target.getInEdges();
  if (EdgeType.isInput(typeOfEdge) && inEdges.length >= 2) {
    return false;
  }
  const outEdges = source.getOutEdges();
  if (EdgeType.isOutput(typeOfEdge) && outEdges.length >= 2) {
    return false;
  }
  return true;
}

function isInCommonCombo(source: INode, target: INode) {
  const sourceModel = source.getModel();
  const targetModel = target.getModel();
  const sourceComboId = sourceModel.comboId || sourceModel.parentId;
  const targetComboId = targetModel.comboId || targetModel.parentId;
  return sourceComboId === targetComboId;
}

function onAddEdgeItem(item: IEdge, graph: Graph) {
  const source = item.getSource();
  const target = item.getTarget();
  const itemModel = item.getModel();
  const edges = target.getEdges();

  const remove = () => {
    graph.removeItem(item);
    setTimeout(() => {
      eventBus.$emit(EventName.removeLastStack);
    }, 0);
  };

  if (edges.length === 0) {
    return;
  }

  for (const edge of edges) {
    const edgeModel = edge.getModel();
    if (itemModel.id === edgeModel.id) {
      continue;
    }
    if (itemModel.source === edgeModel.source) {
      // 已经存在连线，移除重复新增的连线
      Message.info('已经存在连接线');
      remove();
      return;
    }
  }

  const sourceModel = source.getModel();
  const targetModel = target.getModel();
  if (!sourceModel.outPoints) {
    Message.info(`不允许将${sourceModel.label}作为起始端`);
    remove();
  } else if (!targetModel.inPoints) {
    Message.info(`不允许将${targetModel.label}作为结束端`);
    remove();
  } else if (!validateTypeOfEdge(source, target)) {
    Message.info('只能有一条连接线');
    remove();
  } else if (isCycle(target)) {
    Message.info('不允许存在闭环');
    remove();
  } else if (!isInCommonCombo(source, target)) {
    Message.info('不允许连接不同combo外节点');
    remove();
  }
}

function checkLinks(graph: IGraph) {
  const edges = graph.getEdges();
  const valid = edges.every(edge => {
    const source = edge.getSource();
    const target = edge.getTarget();
    return isInCommonCombo(source, target);
  });
  if (!valid) {
    Message.info('不允许连接不同combo外节点');
  }
  return valid;
}

const validate = {
  init() {
    eventBus.$on(EventName.afterAddEdge, onAddEdgeItem);
  },
  destroy() {
    eventBus.$off(EventName.afterAddEdge, onAddEdgeItem);
  },
  checkLinks,
};

export default validate;
