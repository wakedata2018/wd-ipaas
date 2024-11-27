import { uniqueId } from './utils';
import eventBus, { EventName } from './event-bus';
import { MULTI_COMBO } from './types';

import { Graph, INode, IEdge, IItemBase, ITEM_TYPE, NodeConfig, EdgeConfig, LayoutConfig, Item } from '@antv/g6';

export interface EditorParams {
  /**
   * 用于拦截元素创建, 可以抛出错误来阻止创建
   */
  beforeCreateElement?: (item: IItemBase) => Promise<void> | void;
}

export default class Editor {
  graph?: Graph;
  id: string;
  params: EditorParams;

  constructor(params: EditorParams) {
    this.params = params;
    this.id = uniqueId();
  }

  getGraph() {
    return this.graph;
  }

  setGraph(graph: Graph) {
    this.graph = graph;
  }

  emit(event: string, params: any) {
    eventBus.$emit(event, params);
  }

  on(event: string) {
    switch (event) {
      case 'changeNodeData':
        this.graph?.refresh();
        break;
      default:
      // do nothing
    }
  }

  getItemById(id: string) {
    return this.graph?.findById(id);
  }

  add(type: ITEM_TYPE | 'multiCombo', item: any, isRedo?: boolean) {
    const typeItem: Item | boolean | undefined = type !== MULTI_COMBO ? this.graph?.add(type, item) : false;

    // 查找是否重叠 算子拖拽到Combos的范围内，如果是则将它加入到Combos中
    // 当前节点的位置
    const x = item.x;
    const y = item.y;
    const id = item.id;

    this.graph?.getCombos().forEach(field => {
      const model = field.getModel();
      // 异常算子和循环算子
      const types = ['try', 'catch', 'combo'];
      if (types.includes(model.itemType)) {
        const { minX, minY, maxX, maxY } = field.getBBox();
        // 判断节点是否在范围内
        if (x > minX && x < maxX && y > minY && y < maxY) {
          // 重叠则将节点加入Combo中
          this.graph?.updateComboTree(id, model.id);
        }
      }
    });

    const eventMap = {
      edge: () => {
        eventBus.$emit(EventName.afterAddEdge, typeItem, this.graph, isRedo);
      },
      multiCombo: () => {
        eventBus.$emit(EventName.afterAddMultiCombo, item, this.graph, isRedo);
      },
      node: () => {
        eventBus.$emit(EventName.afterAddNode, typeItem, this.graph, isRedo);
      },
    };
    if (eventMap?.[type as keyof typeof eventMap]) {
      eventMap[type as keyof typeof eventMap]();
    }
  }

  update(item: any, model: any) {
    if (item.destroyed) {
      item = this.getItemById(model.id);
    }
    this.graph?.update(item, model);
  }

  /**
   * 更新节点或者线
   * @param item
   * @param cfg
   * @param stack
   */
  updateItem(item: string | INode | IEdge, cfg: Partial<NodeConfig> | EdgeConfig, stack?: boolean) {
    this.graph?.updateItem(item, cfg, stack);
  }

  updateCombo(nodeId: string, parentId?: string, comboId?: string) {
    this.graph?.updateComboTree(nodeId, parentId);
    if (comboId) {
      this.graph?.updateCombo(comboId);
    }
  }

  remove(item: any) {
    const node = this.getItemById(item.id);
    this.graph?.remove(node as any);
  }

  updateLayout(layoutCfg: LayoutConfig) {
    this.graph?.updateLayout(layoutCfg, 'center');
  }
}
