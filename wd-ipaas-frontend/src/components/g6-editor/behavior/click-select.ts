/**
 * 选择节点、线、combo
 * 基于g6原有 click-select 进行拓展，修改上报事件
 * https://vscode.dev/github.com/antvis/G6/blob/57b11a93f7e9695d357e485284af709bfa875408/packages/pc/src/behavior/click-select.ts#L3
 */
import { each } from '@antv/util';
import { G6Event, IG6GraphEvent } from '@antv/g6-core';
import { DEFAULT_HOVER, DEFAULT_SELECTED } from '../constants';
import eventBus from '../event-bus';

const DEFAULT_TRIGGER = 'shift';
const ALLOW_EVENTS = ['shift', 'ctrl', 'alt', 'control'];

export default {
  getDefaultCfg(): object {
    return {
      multiple: true,
      trigger: DEFAULT_TRIGGER,
      hoverState: DEFAULT_HOVER,
      selectedState: DEFAULT_SELECTED,
      selectNode: true,
      selectEdge: true,
      selectCombo: true,
    };
  },
  getEvents(): { [key in G6Event]?: string } {
    const self = this as any;
    // 检测输入是否合法
    if (!ALLOW_EVENTS.includes(self.trigger.toLowerCase())) {
      self.trigger = DEFAULT_TRIGGER;
      console.warn("Behavior click-select 的 trigger 参数不合法，请输入 'drag'、'shift'、'ctrl' 或 'alt'");
    }
    if (!self.multiple) {
      return {
        'node:click': 'onClick',
        'combo:click': 'onClick',
        'edge:click': 'onClick',
        'canvas:click': 'onCanvasClick',
      };
    }
    return {
      'node:click': 'onClick',
      'combo:click': 'onClick',
      'edge:click': 'onClick',
      'node:dblclick': 'onDbClick',
      'combo:dblclick': 'onDbClick',
      'edge:dblclick': 'onDbClick',
      'canvas:click': 'onCanvasClick',
      keyup: 'onKeyUp',
      keydown: 'onKeyDown',
    };
  },
  onClick(evt: IG6GraphEvent) {
    const self = this as any;
    const { item } = evt;
    if (!item || item.destroyed) {
      return;
    }

    const type = item.getType();
    const { graph, keydown, multiple, shouldUpdate, shouldBegin } = self;
    if (!shouldBegin(evt, self)) {
      return;
    }

    if (!keydown || !multiple) {
      const selected = graph
        .findAllByState('node', self.selectedState)
        .concat(graph.findAllByState('edge', self.selectedState))
        .concat(graph.findAllByState('combo', self.selectedState));
      each(selected, selectedItem => {
        if (selectedItem !== item) {
          graph.setItemState(selectedItem, self.hoverState, false);
          graph.setItemState(selectedItem, self.selectedState, false);
        }
      });
    }

    const itemSelectable = (() => {
      switch (type) {
        case 'node':
          return self.selectNode;
        case 'edge':
          return self.selectEdge;
        case 'combo':
          return self.selectCombo;
        default:
          return false;
      }
    })();

    if (!itemSelectable) {
      const selectedNodes = graph.findAllByState('node', self.selectedState);
      const selectedEdges = graph.findAllByState('edge', self.selectedState);
      const selectedCombos = graph.findAllByState('combo', self.selectedState);
      eventBus.$emit('nodeselectchange', {
        selectedItems: {
          nodes: selectedNodes,
          edges: selectedEdges,
          combos: selectedCombos,
        },
        select: false,
      });
      return;
    }

    if (item.hasState(self.selectedState)) {
      if (shouldUpdate(evt, self)) {
        graph.setItemState(item, self.selectedState, false);
      }
      const selectedNodes = graph.findAllByState('node', self.selectedState);
      const selectedEdges = graph.findAllByState('edge', self.selectedState);
      const selectedCombos = graph.findAllByState('combo', self.selectedState);
      eventBus.$emit('nodeselectchange', {
        target: item,
        selectedItems: {
          nodes: selectedNodes,
          edges: selectedEdges,
          combos: selectedCombos,
        },
        select: false,
      });
    } else {
      if (shouldUpdate(evt, self)) {
        graph.setItemState(item, self.selectedState, true);
      }
      const selectedNodes = graph.findAllByState('node', self.selectedState);
      const selectedEdges = graph.findAllByState('edge', self.selectedState);
      const selectedCombos = graph.findAllByState('combo', self.selectedState);
      eventBus.$emit('nodeselectchange', {
        target: item,
        selectedItems: {
          nodes: selectedNodes,
          edges: selectedEdges,
          combos: selectedCombos,
        },
        select: true,
      });
    }
  },
  onDbClick(evt: IG6GraphEvent) {
    const type = evt.item?.getType();
    eventBus.$emit(`${evt.type}${type}`, evt);
  },
  onCanvasClick(evt: IG6GraphEvent) {
    const { graph, shouldBegin, selectedState, hoverState } = this as any;
    if (!shouldBegin(evt, this)) {
      return;
    }

    const selected = graph
      .findAllByState('node', selectedState)
      .concat(graph.findAllByState('edge', selectedState))
      .concat(graph.findAllByState('combo', selectedState));
    each(selected, item => {
      graph.setItemState(item, selectedState, false);
      graph.setItemState(item, hoverState, false);
    });

    eventBus.$emit('nodeselectchange', {
      selectedItems: { nodes: [], edges: [], combos: [] },
      select: false,
    });
  },
  onKeyDown(e: IG6GraphEvent) {
    const self = this as any;
    const code = (e.key ?? '').toLowerCase();
    if (!code) {
      return;
    }
    if (code === self.trigger.toLowerCase() || code === 'control') {
      self.keydown = true;
    } else {
      self.keydown = false;
    }
  },
  onKeyUp() {
    (this as any).keydown = false;
  },
};
