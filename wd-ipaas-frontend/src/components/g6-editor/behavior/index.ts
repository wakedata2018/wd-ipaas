import G6, { BehaviorOption } from '@antv/g6';
import hoverNode from './hover-node';
import keyboard from './keyboard';
import mulitSelect from './mulit-select';
import addMenu from './add-menu';
import clickCombo from './click-combo';
import clickSelect from './click-select';
import dragCombo from './drag-combo';
import dragNode from './drag-node';
import createEdge from './create-edge';

const behavors: Record<string, BehaviorOption> = {
  'drag-node': dragNode as any,
  'drag-combo': dragCombo,
  'hover-node': hoverNode,
  'click-select': clickSelect,
  keyboard,
  'mulit-select': mulitSelect,
  'add-menu': addMenu,
  'click-combo': clickCombo,
  'create-edge': createEdge,
};

export function initBehavors() {
  for (const key in behavors) {
    G6.registerBehavior(key, behavors[key]);
  }
}
