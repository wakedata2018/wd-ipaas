/**
 * 拖动 combo
 * 基于g6原有 drag-combo 进行拓展，增加对 combo 的撤销及恢复操作
 * https://vscode.dev/github.com/antvis/G6/blob/57b11a93f7e9695d357e485284af709bfa875408/packages/pc/src/behavior/drag-combo.ts#L5
 */
import { IGroup } from '@antv/g-base';
import { IGraph } from '@antv/g6';
import { G6Event, IG6GraphEvent, Item, ComboConfig, ICombo, INode, IEdge, Util, ModelConfig } from '@antv/g6-core';
import { each } from '@antv/util';
import { DEFAULT_SELECTED } from '../constants';
import eventBus, { EventName } from '../event-bus';
import Global from '../global';

const { calculationItemsBBox } = Util;

/**
 * 遍历拖动的 Combo 下的所有 Combo
 * @param data 拖动的 Combo
 * @param fn
 */
const traverseCombo = (data: Item, fn: (param: any) => boolean) => {
  if (!fn(data)) {
    return;
  }

  if (data) {
    const combos = data.get('combos');
    if (combos.length === 0) {
      return false;
    }
    each(combos, child => {
      traverseCombo(child, fn);
    });
  }
};

export default {
  getDefaultCfg() {
    return {
      enableDelegate: true,
      delegateStyle: {},
      selectedState: DEFAULT_SELECTED,
      enableStack: true,
    };
  },
  getEvents(): { [key in G6Event]?: string } {
    return {
      'combo:dragstart': 'onDragStart',
      'combo:drag': 'onDrag',
      'combo:dragend': 'onDragEnd',
      'combo:drop': 'onDrop',
      'node:drop': 'onNodeDrop',
      'combo:dragenter': 'onDragEnter',
      'combo:dragleave': 'onDragLeave',
    };
  },
  shouldUpdate(e?: IG6GraphEvent) {
    return e?.target.get('name') !== 'anchor-point';
  },
  validationCombo(evt: IG6GraphEvent) {
    const self = this as any;
    const { item } = evt;
    const model = item?.getModel();
    /**
     * 判断是否为锁定的算子
     */
    if (model?.locked) {
      return false;
    }

    if (!item || item.destroyed) {
      return false;
    }

    if (!self.shouldUpdate(evt, self)) {
      return false;
    }

    const type = item.getType();

    if (type !== 'combo') {
      return false;
    }
    return true;
  },
  onDragStart(evt: IG6GraphEvent) {
    const self = this as any;
    const graph: IGraph = self.graph;
    const { item } = evt;

    self.currentShouldEnd = true;

    if (!self.validationCombo(evt)) {
      return;
    }

    self.targets = [];

    // 获取所有选中的 Combo
    const combos = graph.findAllByState('combo', self.selectedState);

    const currentCombo = item?.get('id');

    const dragCombos = combos.filter(combo => {
      const comboId = combo.get('id');
      return currentCombo === comboId;
    });

    if (dragCombos.length === 0) {
      self.targets.push(item);
    } else {
      self.targets = combos;
    }

    const beforeDragItems: ModelConfig[] = [];
    self.targets.forEach((t: ICombo) => {
      const { x, y, id } = t.getModel();
      beforeDragItems.push({ x, y, id });
    });
    self.set('beforeDragItems', beforeDragItems);

    if (self.activeState) {
      self.targets.forEach((combo: ICombo) => {
        const model = combo.getModel() as ComboConfig;
        if (model.parentId) {
          const parentCombo = graph.findById(model.parentId);
          if (parentCombo) {
            graph.setItemState(parentCombo, self.activeState, true);
          }
        }
      });
    }

    self.point = {};
    self.originPoint = {};

    self.origin = {
      x: evt.x,
      y: evt.y,
    };

    self.currentItemChildCombos = [];

    traverseCombo(item!, param => {
      if (param.destroyed) {
        return false;
      }
      const model = param.getModel();
      self.currentItemChildCombos.push(model.id);
      return true;
    });
  },
  onDrag(evt: IG6GraphEvent) {
    const self = this as any;
    if (!self.origin) {
      return;
    }

    if (!self.validationCombo(evt)) {
      return;
    }

    if (self.enableDelegate) {
      self.updateDelegate(evt);
    } else {
      if (self.activeState) {
        const graph: IGraph = self.graph;
        const item: Item = evt.item!;
        const model = item.getModel();
        // 拖动过程中实时计算距离
        const combos = graph.getCombos();
        const sourceBBox = item.getBBox();

        const { centerX, centerY, width } = sourceBBox;

        // 参与计算的 Combo，需要排除掉：
        // 1、拖动 combo 自己
        // 2、拖动 combo 的 parent
        // 3、拖动 Combo 的 children

        const calcCombos = combos.filter(combo => {
          const cmodel = combo.getModel() as ComboConfig;
          // 被拖动的是最外层的 Combo，无 parent，排除自身和子元素
          if (!model.parentId) {
            return cmodel.id !== model.id && !self.currentItemChildCombos.includes(cmodel.id);
          }
          return cmodel.id !== model.id && !self.currentItemChildCombos.includes(cmodel.id);
        });

        calcCombos.forEach(combo => {
          const { centerX: cx, centerY: cy, width: w } = combo.getBBox();

          // 拖动的 combo 和要进入的 combo 之间的距离
          const disX = centerX! - cx!;
          const disY = centerY! - cy!;
          // 圆心距离
          const distance = 2 * Math.sqrt(disX * disX + disY * disY);

          if (width + w - distance > 0.8 * width) {
            graph.setItemState(combo, self.activeState, true);
          } else {
            graph.setItemState(combo, self.activeState, false);
          }
        });
      }

      each(self.targets, item => {
        self.updateCombo(item, evt);
      });
    }
  },
  updatePositions(evt: IG6GraphEvent, restore: boolean) {
    const self = this as any;
    // 当启用 delegate 时，拖动结束时需要更新 combo
    if (self.enableDelegate || restore) {
      each(self.targets, item => {
        const { locked = false } = item.getModel();
        if (locked) {
          return;
        }
        self.updateCombo(item, evt, restore);
      });
    }
  },
  onDrop(evt: IG6GraphEvent) {
    const self = this as any;
    // 被放下的目标 combo
    const { item } = evt;
    self.currentShouldEnd = self.shouldEnd(evt, item, self);
    self.updatePositions(evt, !self.currentShouldEnd);
    if (!self.currentShouldEnd || !item || !self.targets || item.destroyed) {
      return;
    }

    const graph: IGraph = self.graph;

    const targetModel = item.getModel();

    self.targets.forEach((combo: ICombo) => {
      const model = combo.getModel();
      if (model.parentId !== targetModel.id) {
        if (self.activeState) {
          graph.setItemState(item, self.activeState, false);
        }
        // 将 Combo 放置到某个 Combo 上面时，只有当 onlyChangeComboSize 为 false 时候才更新 Combo 结构
        if (!self.onlyChangeComboSize) {
          graph.updateComboTree(combo, targetModel.id, false);
        } else {
          graph.updateCombo(combo);
        }
      } else {
        graph.updateCombo(item as ICombo);
      }
    });

    self.end(item, evt);

    // 如果已经拖放下了，则不需要再通过距离判断了
    self.endComparison = true;
  },
  onNodeDrop(evt: IG6GraphEvent) {
    const self = this as any;
    if (!self.targets || self.targets.length === 0) {
      return;
    }
    const graph: IGraph = self.graph;

    const item = evt.item as INode;
    const comboId = item.getModel().comboId as string;

    const newParentCombo = comboId ? graph.findById(comboId) : undefined;
    self.currentShouldEnd = self.shouldEnd(evt, newParentCombo, self);
    self.updatePositions(evt, !self.currentShouldEnd);
    if (!self.currentShouldEnd) {
      return;
    }

    let droppedCombo;
    // 如果被放置的的节点有 comboId，且这个 comboId 与正在被拖拽的 combo 的父 id 不相同，则更新父亲为 comboId
    if (comboId) {
      if (self.activeState) {
        const combo = graph.findById(comboId);
        graph.setItemState(combo, self.activeState, false);
      }
      self.targets.forEach((combo: ICombo) => {
        if (!self.onlyChangeComboSize) {
          if (comboId !== combo.getID()) {
            droppedCombo = graph.findById(comboId);
            if (comboId !== combo.getModel().parentId) {
              graph.updateComboTree(combo, comboId, false);
            }
          }
        } else {
          graph.updateCombo(combo);
        }
      });
    } else {
      // 如果被放置的节点没有 comboId，且正在被拖拽的 combo 有父 id，则更新父亲为 undefined
      self.targets.forEach((combo: ICombo) => {
        if (!self.onlyChangeComboSize) {
          const model = combo.getModel();
          if (model.comboId) {
            graph.updateComboTree(combo, undefined, false);
          }
        } else {
          graph.updateCombo(combo);
        }
      });
    }

    // 如果已经拖放下了，则不需要再通过距离判断了
    self.endComparison = true;
    self.end(droppedCombo, evt);
  },
  onDragEnter(evt: IG6GraphEvent) {
    const self = this as any;
    if (!self.origin) {
      return;
    }

    if (!self.validationCombo(evt)) {
      return;
    }

    const { item } = evt;
    const graph: IGraph = self.graph;
    if (self.activeState) {
      graph.setItemState(item!, self.activeState, true);
    }
  },
  onDragLeave(evt: IG6GraphEvent) {
    const self = this as any;
    if (!self.origin) {
      return;
    }

    if (!self.validationCombo(evt)) {
      return;
    }

    const item = evt.item as ICombo;
    const graph: IGraph = self.graph;
    if (self.activeState) {
      graph.setItemState(item, self.activeState, false);
    }
  },
  handleUpdateStack() {
    const self = this as any;
    // 拖动结束后，入操作记录栈
    const oldItems = self.get('beforeDragItems');
    const data = self.targets.map((item: INode, index: number) => {
      const { x, y, id } = item.getModel();
      return {
        item,
        oldModel: oldItems[index],
        newModel: { x, y, id },
      };
    });
    eventBus.$emit(EventName.updateItem, data);
  },
  onDragEnd(evt: IG6GraphEvent) {
    const self = this as any;
    if (!self.targets || self.targets.length === 0) {
      return;
    }
    const item = evt.item;
    const model = item?.getModel();
    if (model?.locked) {
      return;
    }
    if (self.currentShouldEnd) {
      self.updatePositions(evt);
    }
    const parentCombo = self.getParentCombo(model?.parentId);
    const graph: IGraph = self.graph;
    if (parentCombo && self.activeState) {
      graph.setItemState(parentCombo, self.activeState, false);
    }
    self.end(undefined, evt);
  },
  end(comboDropedOn: ICombo | undefined, evt: IG6GraphEvent) {
    const self = this as any;
    if (!self.origin) {
      return;
    }
    const graph: IGraph = self.graph;

    // 删除delegate shape
    if (self.delegateShape) {
      const delegateGroup = graph.get('delegateGroup');
      delegateGroup.clear();
      self.delegateShape = null;
    }

    if (comboDropedOn && self.activeState) {
      graph.setItemState(comboDropedOn, self.activeState, false);
    }
    // 若没有被放置的 combo，则是被放置在画布上
    if (!comboDropedOn) {
      const stack = graph.get('enabledStack') && self.enableStack;

      const stackData: any = {
        before: { nodes: [], edges: [], combos: [].concat(self.get('beforeDragItems')) },
        after: { nodes: [], edges: [], combos: [] },
      };

      self.targets.forEach((combo: ICombo) => {
        // 将 Combo 放置到某个 Combo 上面时，只有当 onlyChangeComboSize 为 false 时候才更新 Combo 结构
        if (!self.onlyChangeComboSize) {
          graph.updateComboTree(combo, undefined, stack);
        } else {
          graph.updateCombo(combo);
          const { x, y, id } = combo.getModel();
          stackData.after.combos.push({ x, y, id });
          graph.pushStack('update', stackData);
        }
      });
    }
    self.handleUpdateStack();

    self.point = [];
    self.origin = null;
    self.originPoint = null;
    self.targets.length = 0;
  },

  /**
   * 遍历 comboTree，分别更新 node 和 combo
   * @param data
   * @param fn
   */
  traverse<T extends Item>(data: T, fn: (param: T, cacheMap: any) => boolean, edgesToBeUpdate = {}) {
    if (!fn(data, edgesToBeUpdate)) {
      return;
    }

    if (data) {
      const combos = data.get('combos');
      each(combos, child => {
        this.traverse(child, fn, edgesToBeUpdate);
      });

      const nodes = data.get('nodes');
      each(nodes, child => {
        this.traverse(child, fn, edgesToBeUpdate);
      });
    }
  },

  updateCombo(item: ICombo, evt: IG6GraphEvent, restore: boolean) {
    this.updateSingleItem(item, evt, restore);
    const edgesToBeUpdate: { [id: string]: IEdge } = {};
    this.traverse(
      item,
      (paramItem, paramEdgesMap) => {
        if (paramItem.destroyed) {
          return false;
        }
        paramItem.getEdges().forEach(edge => (paramEdgesMap[edge.getID()] = edge));
        return true;
      },
      edgesToBeUpdate
    );
    Object.values(edgesToBeUpdate).forEach(edge => edge.refresh());
  },

  /**
   *
   * @param item 当前正在拖动的元素
   * @param evt
   */
  updateSingleItem(item: INode | ICombo, evt: IG6GraphEvent, restore: boolean) {
    const self = this as any;
    const { origin } = self;
    const graph: IGraph = self.graph;
    const model = item.getModel() as ComboConfig;
    const itemId = item.get('id');

    if (!self.point[itemId]) {
      self.point[itemId] = {
        x: model.x,
        y: model.y,
      };
    }

    let x: number = evt.x - origin.x + self.point[itemId].x;
    let y: number = evt.y - origin.y + self.point[itemId].y;

    if (restore) {
      x += origin.x - evt.x;
      y += origin.y - evt.y;
    }

    graph.updateItem(item, { x, y }, false);
    // item.getEdges()?.forEach(edge => edge.refresh());
  },

  /**
   * 根据 ID 获取父 Combo
   * @param parentId 父 Combo ID
   */
  getParentCombo(parentId: string): ICombo | undefined {
    const self = this as any;
    const graph: IGraph = self.graph;
    if (!parentId) {
      return undefined;
    }

    const parentCombo = graph.findById(parentId) as ICombo;
    if (!parentCombo) {
      return undefined;
    }

    return parentCombo;
  },

  updateDelegate(evt: IG6GraphEvent) {
    const self = this as any;
    const graph: IGraph = self.graph;
    // 当没有 delegate shape 时创建
    if (!self.delegateShape) {
      const delegateGroup: IGroup = graph.get('delegateGroup');

      let bbox = null;
      if (self.targets.length > 1) {
        bbox = calculationItemsBBox(self.targets);
      } else {
        bbox = self.targets[0].getBBox();
      }

      const { x, y, width, height, minX, minY } = bbox;

      self.originPoint = { x, y, width, height, minX, minY };

      const attrs = { ...Global.delegateStyle, ...self.delegateStyle };

      self.delegateShape = delegateGroup.addShape('rect', {
        attrs: {
          width: bbox.width,
          height: bbox.height,
          x: bbox.x,
          y: bbox.y,
          ...attrs,
        },
        name: 'combo-delegate-shape',
      });
      self.delegateShape.set('capture', false);
      self.delegate = self.delegateShape;
    } else {
      const clientX = evt.x - self.origin.x + self.originPoint.minX;
      const clientY = evt.y - self.origin.y + self.originPoint.minY;

      self.delegateShape.attr({
        x: clientX,
        y: clientY,
      });
    }
  },
};
