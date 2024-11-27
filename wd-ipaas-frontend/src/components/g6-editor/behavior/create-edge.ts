/**
 * 创建连线
 * 基于g6原有 create-edge 进行拓展，增加连线的撤销及恢复操作，并且显示隐藏可连接点
 * https://vscode.dev/github.com/antvis/G6/blob/57b11a93f7e9695d357e485284af709bfa875408/packages/pc/src/behavior/create-edge.ts#L4
 */
import { G6Event, IG6GraphEvent } from '@antv/g6-core';
import { isFunction } from '@antv/util';
import { IGraph } from '@antv/g6';
import eventBus, { EventName } from '../event-bus';
import { uniqueId } from '../utils';

const DEFAULT_TRIGGER = 'drag';
const ALLOW_EVENTS = ['click', 'drag'];
const DEFAULT_KEY = undefined;
const ALLOW_KEYS = ['shift', 'ctrl', 'control', 'alt', 'meta', undefined];

export default {
  getDefaultCfg(): object {
    return {
      trigger: DEFAULT_TRIGGER,
      key: DEFAULT_KEY,
      edgeConfig: {},
      getEdgeConfig: undefined,
    };
  },
  getEvents(): { [key in G6Event]?: string } {
    const self = this as any;
    // 检测输入是否合法
    if (!ALLOW_EVENTS.includes(self.trigger.toLowerCase())) {
      self.trigger = DEFAULT_TRIGGER;
      console.warn("Behavior create-edge 的 trigger 参数不合法，请输入 'click'，'drag'");
    }
    // 检测输入是否合法
    if (self.key && !ALLOW_KEYS.includes(self.key.toLowerCase())) {
      self.trigger = DEFAULT_KEY;
      console.warn("Behavior create-edge 的 key 参数不合法，请输入 'shift'，'ctrl'，'alt'，'control'，或 undefined");
    }
    let events: Record<string, string> = {};
    if (self.trigger === 'drag') {
      events = {
        'node:dragstart': 'onClick',
        'combo:dragstart': 'onClick',
        drag: 'updateEndPoint',
        'node:drop': 'onClick',
        'combo:drop': 'onClick',
        dragend: 'onDragEnd',
      };
    } else if (self.trigger === 'click') {
      events = {
        'node:click': 'onClick', // The event is node:click, the responsing function is onClick
        mousemove: 'updateEndPoint', // The event is mousemove, the responsing function is onMousemove
        'edge:click': 'cancelCreating', // The event is edge:click, the responsing function is onEdgeClick
        'canvas:click': 'cancelCreating',
        'combo:click': 'onClick',
      };
    }
    if (self.key) {
      events.keydown = 'onKeyDown';
      events.keyup = 'onKeyUp';
    }
    return events;
  },
  shouldBegin(e?: IG6GraphEvent) {
    return e?.target.get('name') === 'anchor-point';
  },
  onDragEnd(ev: IG6GraphEvent) {
    const self = this as any;
    const graph: IGraph = self.graph;
    if (self.key && !self.keydown) {
      return;
    }
    const { item } = ev;
    if (!item || item.getID() === self.source || item.getType() !== 'node') {
      self.cancelCreating({
        item: self.edge,
        x: ev.x,
        y: ev.y,
      });
    }
    // 将可连接点隐藏
    graph.find('node', node => {
      const group = node.get('group');
      group.cfg.children.forEach((child: any) => {
        const attrs = child.attrs;
        if (attrs.hoverChange) {
          child.attr({ fill: '#fff', opacity: '0' });
        }
      });
      return true;
    });
    graph.find('combo', node => {
      const group = node.get('group');
      group.cfg.children.forEach((child: any) => {
        const attrs = child.attrs;
        if (attrs.hoverChange) {
          child.attr({ fill: '#fff', opacity: '0' });
        }
      });
      return true;
    });
    self.edgeConfig = {};
  },
  // 如果边的起点没有指定，则根据起点创建新边；如果起点已经指定而终点未指定，则指定终点
  onClick(ev: IG6GraphEvent) {
    const self = this as any;
    if (self.key && !self.keydown) {
      return;
    }
    const graph: IGraph = self.graph;
    const item = ev.item as any;
    const model = item.getModel();
    const getEdgeConfig = self.getEdgeConfig;
    // 如果起点已经指定而终点未指定，则指定终点
    if (self.addingEdge && self.edge) {
      // 判断是否可以继续连线，且无法自循环
      if (!self.shouldEnd(ev, self) || self.source === model.id) {
        return;
      }

      let edgeConfig;
      if (getEdgeConfig && isFunction(getEdgeConfig)) {
        edgeConfig = getEdgeConfig({ source: self.source, target: model.id }, self);
      } else {
        edgeConfig = self.edgeConfig;
      }

      edgeConfig = {
        type: 'customEdge',
        ...edgeConfig,
        id: 'edge' + uniqueId(),
        target: model.id,
        style: { lineDash: [0] },
      };

      graph.emit('beforecreateedge', {});

      self.cancelCreating({ item: self.edge });
      eventBus.$emit(EventName.addItem, edgeConfig);

      self.edge = null;
      self.addingEdge = false;
    } else {
      // 如果边的起点没有指定，则根据起点创建新边
      if (!self.shouldBegin(ev, self)) {
        return;
      }

      // 显示可连接点
      graph.find('node', node => {
        if (item === node || !node.getModel().inPoints) {
          return true;
        }
        const group = node.get('group');
        group.cfg.children.forEach((child: any) => {
          if (child.attrs.hoverChange) {
            child.attr({ fill: '#1890ff', opacity: '0.3' });
          }
        });
        return true;
      });
      graph.find('combo', combo => {
        if (item === combo || !combo.getModel().inPoints) {
          return true;
        }
        const group = combo.get('group');
        group.cfg.children.forEach((child: any) => {
          if (child.attrs.hoverChange) {
            child.attr({ fill: '#1890ff', opacity: '0.3' });
          }
        });
        return true;
      });

      // 获取自定义 edge 配置
      let edgeConfig;
      if (getEdgeConfig && isFunction(getEdgeConfig)) {
        edgeConfig = getEdgeConfig({ source: model.id, target: model.id }, self);
      } else {
        edgeConfig = self.edgeConfig;
      }
      edgeConfig = {
        source: model.id,
        target: model.id,
        style: { stroke: '#1890FF', strokeOpacity: 0.9, lineDash: [5, 5] },
        itemType: 'edge',
        ...edgeConfig,
      };

      self.edge = graph.addItem('edge', edgeConfig, false);
      self.source = model.id;
      self.addingEdge = true;
      self.edgeConfig = edgeConfig;
      // 暂时将该边的 capture 设置为 false，这样可以拾取到后面的元素
      self.edge.getKeyShape().set('capture', false);
    }
  },
  // 边的起点已经确定，边的末端跟随鼠标移动
  updateEndPoint(ev: IG6GraphEvent) {
    const self = this as any;
    if (self.key && !self.keydown) {
      return;
    }
    if (self.edge?.destroyed) {
      self.cancelCreating({ item: self.edge });
    }

    // 若此时 source 节点已经被移除，结束添加边
    if (!self.graph.findById(self.source)) {
      self.addingEdge = false;
      return;
    }

    const point = { x: ev.x, y: ev.y };
    if (self.addingEdge && self.edge) {
      // 更新边的终点为鼠标位置
      self.graph.updateItem(self.edge, { target: point }, false);
    }
  },
  // 取消增加边，删除该边；或指定终点
  cancelCreating(ev: IG6GraphEvent) {
    const self = this as any;
    if (self.key && !self.keydown) {
      return;
    }
    const graph: IGraph = self.graph;
    const currentEdge = ev.item;
    if (self.addingEdge && (self.edge === currentEdge || ev.target?.isCanvas?.())) {
      if (self.edge && !self.edge.destroyed) {
        graph.removeItem(self.edge, false);
      }
      self.edge = null;
      self.addingEdge = false;
    }
  },

  onKeyDown(e: IG6GraphEvent) {
    const self = this as any;
    const code = e.key;
    if (!code) {
      return;
    }
    if (code.toLowerCase() === self.key.toLowerCase()) {
      self.keydown = true;
    } else {
      self.keydown = false;
    }
  },
  onKeyUp() {
    const self = this as any;
    if (self.addingEdge && self.edge) {
      // 清除正在增加的边
      self.graph.removeItem(self.edge, false);
      self.addingEdge = false;
      self.edge = null;
    }
    self.keydown = false;
  },
};
