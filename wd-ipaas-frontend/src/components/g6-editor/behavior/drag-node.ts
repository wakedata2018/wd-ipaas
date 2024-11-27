/**
 * 拖动节点
 * 基于g6原有 drag-node 进行拓展，修改上报事件
 * https://vscode.dev/github.com/antvis/G6/blob/57b11a93f7e9695d357e485284af709bfa875408/packages/pc/src/behavior/drag-node.ts#L13
 */
import { Point } from '@antv/g-base';
import { IGraph } from '@antv/g6';
import { G6Event, IG6GraphEvent, Item, NodeConfig, INode, ICombo } from '@antv/g6-core';
import { deepMix, debounce } from '@antv/util';
import eventBus, { EventName } from '../event-bus';

const DEFAULT_SELECTED = 'selected';

export default {
  getDefaultCfg() {
    return {
      updateEdge: true,
      delegateStyle: {},
      // 是否开启delegate
      enableDelegate: true,
      // 拖动节点过程中是否只改变 Combo 的大小，而不改变其结构
      onlyChangeComboSize: false,
      // 拖动过程中目标 combo 状态样式
      comboActiveState: 'combo-active',
      selectedState: DEFAULT_SELECTED,
      enableOptimize: false,
      enableDebounce: false,
      enableStack: true,
    };
  },
  getEvents(): { [key in G6Event]?: string } {
    return {
      'node:mousedown': 'onMouseDown', // G's dragstart event is not triggered sometimes when the drag events are not finished properly. Listen to mousedown and drag instead of dragstart
      drag: 'onDragMove', // global drag, mouseup, and dragend to avoid mouse moving too fast to go out of a node while draging
      dragend: 'onDragEnd',
      'combo:dragenter': 'onDragEnter',
      'combo:dragleave': 'onDragLeave',
      'combo:drop': 'onDropCombo',
      'node:drop': 'onDropNode',
      'canvas:drop': 'onDropCanvas',
      touchstart: 'onTouchStart',
      touchmove: 'onTouchMove',
      touchend: 'onDragEnd',
    };
  },
  shouldBegin(e?: IG6GraphEvent) {
    return e?.target.get('name') !== 'anchor-point';
  },
  shouldEnd(e: IG6GraphEvent, item: Item, self: any) {
    if (self.mousedown?.item && item) {
      // 当 forbidInCombo = true 时，禁止继续执行
      return !self.mousedown.item.getModel().forbidInCombo;
    }
    return true;
  },
  validationCombo(item: ICombo) {
    const self = this as any;
    if (!self.origin || !item || item.destroyed) {
      return false;
    }

    const type = item.getType();
    if (type !== 'combo') {
      return false;
    }
    return true;
  },
  onTouchStart(evt: IG6GraphEvent) {
    if (!evt.item) {
      return;
    }
    const self = this as any;
    try {
      const touches = (evt.originalEvent as TouchEvent).touches;
      const event1 = touches[0];
      const event2 = touches[1];

      if (event1 && event2) {
        return;
      }

      evt.preventDefault();
    } catch (e) {
      console.warn('Touch original event not exist!');
    }
    self.mousedown = {
      item: evt.item,
      target: evt.target,
    };
    self.dragstart = true;
    self.onDragStart(evt);
  },
  onTouchMove(e: IG6GraphEvent) {
    const self = this as any;
    try {
      const touches = (e.originalEvent as TouchEvent).touches;
      const event1 = touches[0];
      const event2 = touches[1];

      if (event1 && event2) {
        self.onDragEnd(e);
        return;
      }

      e.preventDefault();
    } catch (error) {
      console.warn('Touch original event not exist!');
    }
    self.onDrag(e);
  },
  /**
   * cache the manipulated item and target, since drag and dragend are global events but not node:*
   * @param evt event param
   */
  onMouseDown(evt: IG6GraphEvent) {
    (this as any).mousedown = {
      item: evt.item,
      target: evt.target,
    };
  },
  /**
   * trigger dragstart/drag by mousedown and drag events
   * @param evt event param
   */
  onDragMove(evt: IG6GraphEvent) {
    const self = this as any;
    if (evt.item?.getType?.() !== 'node') {
      return;
    }
    if (!self.mousedown) {
      return;
    }
    if (!self.dragstart) {
      // dragstart
      self.dragstart = true;
      self.onDragStart(evt);
    } else {
      // drag
      self.onDrag({
        ...evt,
        ...self.mousedown,
      });
    }
  },
  /**
   * 开始拖动节点
   * @param evt
   */
  onDragStart(evt: IG6GraphEvent) {
    const self = this as any;
    self.currentShouldEnd = true;
    if (!self.shouldBegin({ ...evt, ...self.mousedown }, self)) {
      return;
    }
    const { item, target } = self.mousedown;
    if (!item || item.destroyed || item.hasLocked()) {
      return;
    }

    // 拖动时，设置拖动元素的 capture 为false，则不拾取拖动的元素
    const group = item.getContainer();
    group.set('capture', false);
    if (!self.cachedCaptureItems) {
      self.cachedCaptureItems = [];
    }
    self.cachedCaptureItems.push(item);

    // 如果拖动的target 是linkPoints / anchorPoints 则不允许拖动
    if (target) {
      const isAnchorPoint = target.get('isAnchorPoint');
      if (isAnchorPoint) {
        return;
      }
    }

    const graph: IGraph = (this as any).graph;
    self.targets = [];
    // 将节点拖入到指定的 Combo
    self.targetCombo = null;

    // 获取所有选中的元素
    const nodes = graph.findAllByState('node', self.selectedState);
    const currentNodeId = item.get('id');

    // 当前拖动的节点是否是选中的节点
    const dragNodes = nodes.filter((node: Item) => {
      return currentNodeId === node.get('id');
    });

    // 只拖动当前节点
    if (dragNodes.length === 0) {
      self.targets.push(item);
    } else if (nodes.length > 1) {
      // 拖动多个节点
      nodes.forEach((node: any) => {
        const locked = node.hasLocked();
        if (!locked) {
          self.targets.push(node);
        }
      });
    } else {
      self.targets.push(item);
    }

    // 缓存拖拽节点信息
    {
      const beforeDragNodes: NodeConfig[] = [];
      self.targets.forEach((t: Item) => {
        const { x, y, id, comboId } = t.getModel() as NodeConfig;
        beforeDragNodes.push({ x, y, id, comboId });
      });
      self.beforeDragNodes = beforeDragNodes;
    }

    self.hidenEdge = {};
    if (self.updateEdge && self.enableOptimize && !self.enableDelegate) {
      self.targets.forEach((node: INode) => {
        const edges = node.getEdges();
        edges.forEach(edge => {
          if (!edge.isVisible()) {
            return;
          }
          self.hidenEdge[edge.getID()] = true;
          edge.hide();
        });
      });
    }

    self.origin = {
      x: evt.x,
      y: evt.y,
    };
    self.point = {};
    self.originPoint = {};
  },

  /**
   * 持续拖动节点
   * @param evt
   */
  onDrag(evt: IG6GraphEvent) {
    const self = this as any;
    if (!self.mousedown || !self.origin) {
      return;
    }
    if (!self.shouldUpdate(evt, this)) {
      return;
    }

    if (self.enableDelegate) {
      this.updateDelegate(evt);
    } else {
      if (self.enableDebounce) {
        self.debounceUpdate({
          targets: self.targets,
          graph: self.graph,
          point: self.point,
          origin: self.origin,
          evt,
          updateEdge: self.updateEdge,
        });
      } else {
        self.targets.forEach((target: any) => {
          self.update(target, evt);
        });
      }
    }
  },
  /**
   * 拖动结束，设置拖动元素capture为true，更新元素位置，如果是拖动涉及到 combo，则更新 combo 结构
   * @param evt
   */
  onDragEnd(evt: IG6GraphEvent) {
    const self = this as any;
    self.mousedown = false;
    self.dragstart = false;

    // 拖动结束后，设置拖动元素 group 的 capture 为 true，允许拾取拖动元素
    self.cachedCaptureItems?.forEach((item: Item) => {
      const group = item.getContainer();
      group.set('capture', true);
    });
    self.cachedCaptureItems = [];

    if (self.delegateRect) {
      self.delegateRect.remove();
      self.delegateRect = null;
    }

    if (!self.origin || !self.currentShouldEnd) {
      return;
    }

    if (self.updateEdge && self.enableOptimize && !self.enableDelegate) {
      self.targets.forEach((node: INode) => {
        const edges = node.getEdges();
        edges.forEach(edge => {
          if (self.hidenEdge[edge.getID()]) {
            edge.show();
          }
          edge.refresh();
        });
      });
    }
    self.hidenEdge = {};

    // 拖动结束后，入操作记录栈
    {
      const data = self.targets.map((item: INode, index: number) => {
        const { x, y, id, comboId } = item.getModel();
        return {
          item,
          oldModel: self.beforeDragNodes[index],
          newModel: { x, y, id, comboId },
        };
      });
      eventBus.$emit(EventName.updateItem, data);
    }

    // 拖动结束后emit事件，将当前操作的节点抛出去，目标节点为null
    self.graph.emit('dragnodeend', {
      items: self.targets,
      targetItem: null,
    });

    self.point = {};
    self.origin = null;
    self.originPoint = {};
    self.targets.length = 0;
    self.targetCombo = null;
  },
  /**
   * 拖动过程中将节点放置到 combo 上
   * @param evt
   */
  onDropCombo(evt: IG6GraphEvent) {
    const self = this as any;
    const item = evt.item as ICombo;
    self.currentShouldEnd = self.shouldEnd(evt, item, self);
    // 若不允许结束，则将节点位置设置回初识位置。后面的逻辑仍需要执行
    self.updatePositions(evt, !self.currentShouldEnd);
    if (!self.currentShouldEnd || !self.validationCombo(item)) {
      return;
    }
    const graph: IGraph = self.graph;

    if (self.comboActiveState) {
      graph.setItemState(item, self.comboActiveState, false);
    }

    self.targetCombo = item;

    // 拖动结束后是动态改变 Combo 大小还是将节点从 Combo 中删除
    if (self.onlyChangeComboSize) {
      // 拖动节点结束后，动态改变 Combo 的大小
      graph.updateCombos();
    } else {
      const targetComboModel = item.getModel();
      self.targets.forEach((node: INode) => {
        const nodeModel = node.getModel();
        if (nodeModel.comboId !== targetComboModel.id) {
          graph.updateComboTree(node, targetComboModel.id);
          eventBus.$emit('updateTaskInfo');
        }
      });
      graph.updateCombo(item);
    }

    // 将节点拖动到 combo 上面，emit事件抛出当前操作的节点及目标 combo
    graph.emit('dragnodeend', {
      items: self.targets,
      targetItem: self.targetCombo,
    });
  },
  /**
   * 拖动过程中将节点放置到 画布Canvas 上
   * @param evt
   */
  onDropCanvas(evt: IG6GraphEvent) {
    const self = this as any;
    const graph: IGraph = self.graph;
    self.currentShouldEnd = self.shouldEnd(evt, undefined, self);
    // 若不允许结束，则将节点位置设置回初识位置。后面的逻辑仍需要执行
    self.updatePositions(evt, !self.currentShouldEnd);
    if (!self.targets || self.targets.length === 0 || !self.currentShouldEnd) {
      return;
    }
    if (self.onlyChangeComboSize) {
      // 拖动节点结束后，动态改变 Combo 的大小
      graph.updateCombos();
    } else {
      self.targets.forEach((node: INode) => {
        // 拖动的节点有 comboId，即是从其他 combo 中拖出时才处理
        const model = node.getModel();
        if (model.comboId) {
          graph.updateComboTree(node);
          eventBus.$emit('updateTaskInfo');
        }
      });
    }
  },

  /**
   * 拖动放置到某个 combo 中的子 node 上
   * @param evt
   */
  onDropNode(evt: IG6GraphEvent) {
    const self = this as any;
    if (!self.targets || self.targets.length === 0) {
      return;
    }
    const item = evt.item as INode;
    const graph: IGraph = self.graph;

    const comboId = item.getModel().comboId as string;

    const newParentCombo = comboId ? graph.findById(comboId) : undefined;
    self.currentShouldEnd = self.shouldEnd(evt, newParentCombo, self);
    // 若不允许结束，则将节点位置设置回初识位置。后面的逻辑仍需要执行
    self.updatePositions(evt, !self.currentShouldEnd);
    if (!self.currentShouldEnd) {
      return;
    }

    if (self.onlyChangeComboSize) {
      graph.updateCombos();
    } else if (comboId) {
      const combo = graph.findById(comboId);
      if (self.comboActiveState) {
        graph.setItemState(combo, self.comboActiveState, false);
      }
      self.targets.forEach((node: INode) => {
        const nodeModel = node.getModel();
        if (comboId !== nodeModel.comboId) {
          graph.updateComboTree(node, comboId);
        }
      });
      graph.updateCombo(combo as ICombo);
    } else {
      self.targets.forEach((node: INode) => {
        const model = node.getModel();
        if (model.comboId) {
          graph.updateComboTree(node);
        }
      });
    }

    // 将节点拖动到另外个节点上面，emit 事件抛出当前操作的节点及目标节点
    graph.emit('dragnodeend', {
      items: self.targets,
      targetItem: item,
    });
  },
  /**
   * 将节点拖入到 Combo 中
   * @param evt
   */
  onDragEnter(evt: IG6GraphEvent) {
    const self = this as any;
    const item = evt.item as ICombo;
    if (!self.validationCombo(item)) {
      return;
    }

    if (self.comboActiveState) {
      self.graph.setItemState(item, self.comboActiveState, true);
    }
  },
  /**
   * 将节点从 Combo 中拖出
   * @param evt
   */
  onDragLeave(evt: IG6GraphEvent) {
    const self = this as any;
    const item = evt.item as ICombo;
    if (!self.validationCombo(item)) {
      return;
    }

    if (self.comboActiveState) {
      self.graph.setItemState(item, self.comboActiveState, false);
    }
  },

  updatePositions(evt: IG6GraphEvent, restore: boolean) {
    const self = this as any;
    if (!self.targets || self.targets.length === 0) {
      return;
    }
    // 当开启 delegate 时，拖动结束后需要更新所有已选中节点的位置
    if (self.enableDelegate) {
      if (self.enableDebounce) {
        self.debounceUpdate({
          targets: self.targets,
          graph: self.graph,
          point: self.point,
          origin: self.origin,
          evt,
          updateEdge: self.get('updateEdge'),
          updateFunc: self.update,
        });
      } else if (!restore) {
        self.targets.map((node: INode) => self.update(node, evt));
      }
    } else {
      self.targets.map((node: INode) => self.update(node, evt, restore));
    }
  },
  /**
   * 更新节点
   * @param item 拖动的节点实例
   * @param evt
   */
  update(item: Item, evt: IG6GraphEvent, restore: boolean) {
    const self = this as any;
    const { origin } = self;
    const model: NodeConfig = item.get('model');
    const nodeId: string = item.get('id');
    if (!self.point[nodeId]) {
      self.point[nodeId] = {
        x: model.x || 0,
        y: model.y || 0,
      };
    }

    let x: number = evt.x - origin.x + self.point[nodeId].x;
    let y: number = evt.y - origin.y + self.point[nodeId].y;

    if (restore) {
      x += origin.x - evt.x;
      y += origin.y - evt.y;
    }

    const pos: Point = { x, y };

    if (self.updateEdge) {
      self.graph.updateItem(item, pos, false);
    } else {
      item.updatePosition(pos);
    }
  },

  /**
   * 限流更新节点
   * @param item 拖动的节点实例
   * @param evt
   */
  debounceUpdate: debounce(
    (event: any) => {
      const { targets, graph, point, origin, evt, updateEdge } = event;
      targets.forEach((item: Item) => {
        const model: NodeConfig = item.get('model');
        const nodeId: string = item.get('id');
        if (!point[nodeId]) {
          point[nodeId] = {
            x: model.x || 0,
            y: model.y || 0,
          };
        }

        const x: number = evt.x - origin.x + point[nodeId].x;
        const y: number = evt.y - origin.y + point[nodeId].y;

        const pos: Point = { x, y };

        if (updateEdge) {
          graph.updateItem(item, pos, false);
        } else {
          item.updatePosition(pos);
        }
      });
    },
    50,
    true
  ),

  /**
   * 更新拖动元素时的delegate
   * @param {Event} evt 事件句柄
   * @param {number} x 拖动单个元素时候的x坐标
   * @param {number} y 拖动单个元素时候的y坐标
   */
  updateDelegate(evt: IG6GraphEvent) {
    const self = this as any;
    const { graph } = self;
    if (!self.delegateRect) {
      // 拖动多个
      const parent = graph.get('group');
      const attrs = deepMix(
        {},
        {
          fill: '#F3F9FF',
          fillOpacity: 0.5,
          stroke: '#1890FF',
          strokeOpacity: 0.9,
          lineDash: [5, 5],
        },
        self.delegateStyle
      );

      const { x: cx, y: cy, width, height, minX, minY } = self.calculationGroupPosition(evt);
      self.originPoint = { x: cx, y: cy, width, height, minX, minY };
      // model上的x, y是相对于图形中心的，delegateShape是g实例，x,y是绝对坐标
      self.delegateRect = parent.addShape('rect', {
        attrs: {
          width,
          height,
          x: cx,
          y: cy,
          ...attrs,
        },
        name: 'rect-delegate-shape',
      });
      self.delegate = self.delegateRect;
      self.delegateRect.set('capture', false);
    } else {
      const clientX = evt.x - self.origin.x + self.originPoint.minX;
      const clientY = evt.y - self.origin.y + self.originPoint.minY;
      self.delegateRect.attr({
        x: clientX,
        y: clientY,
      });
    }
  },
  /**
   * 计算delegate位置，包括左上角左边及宽度和高度
   * @memberof ItemGroup
   * @return {object} 计算出来的delegate坐标信息及宽高
   */
  calculationGroupPosition(evt: IG6GraphEvent) {
    const nodes: INode[] = (this as any).targets;
    if (nodes.length === 0) {
      nodes.push(evt.item as INode);
    }

    let minx = Infinity;
    let maxx = -Infinity;
    let miny = Infinity;
    let maxy = -Infinity;

    // 获取已节点的所有最大最小x y值
    for (let i = 0; i < nodes.length; i++) {
      const element = nodes[i];
      const bbox = element.getBBox();
      const { minX, minY, maxX, maxY } = bbox;
      if (minX < minx) {
        minx = minX;
      }

      if (minY < miny) {
        miny = minY;
      }

      if (maxX > maxx) {
        maxx = maxX;
      }

      if (maxY > maxy) {
        maxy = maxY;
      }
    }

    const x = Math.floor(minx);
    const y = Math.floor(miny);
    const width = Math.ceil(maxx) - Math.floor(minx);
    const height = Math.ceil(maxy) - Math.floor(miny);

    return {
      x,
      y,
      width,
      height,
      minX: minx,
      minY: miny,
    };
  },
};
