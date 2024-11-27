import { each } from '@antv/util';

import { uniqueId, getBox } from '../utils';
import eventBus, { EventName } from '../event-bus';
import config from '../global';

export default {
  getEvents() {
    return {
      'canvas:mouseenter': 'onCanvasMouseenter',
      'canvas:mousedown': 'onCanvasMousedown',
      mousemove: 'onMousemove',
      mouseup: 'onMouseup',
    };
  },
  onCanvasMouseenter() {
    this.graph.$el.style.cursor = 'crosshair';
    this.graph.paint();
  },

  onCanvasMousedown(e) {
    const attrs = config.delegateStyle;
    const width = 0;
    const height = 0;
    const x = e.x;
    const y = e.y;
    const parent = this.graph.get('group');
    this.shape = parent.addShape('rect', {
      attrs: {
        id: 'rect' + uniqueId(),
        width,
        height,
        x,
        y,
        ...attrs,
      },
    });
  },
  onMousemove(e) {
    if (this.shape) {
      const width = e.x - this.shape.attrs.x;
      const height = e.y - this.shape.attrs.y;
      this.shape.attr({
        width,
        height,
      });
      this.graph.paint();
    }
  },
  onMouseup() {
    const canvas = this.graph.$el;
    canvas.style.cursor = 'default';
    const selected = this.graph.findAllByState('node', 'selected');
    each(selected, node => {
      this.graph.setItemState(node, 'selected', false);
      eventBus.$emit(EventName.nodeselectchange, { target: node, select: false });
    });
    if (this.shape) {
      this.addTeam();
      this.shape.remove();
      this.shape = null;
    }
    this.graph.paint();
    eventBus.$emit(EventName.muliteSelectEnd);
    // this.graph.setMode('default');
    eventBus.$emit(EventName.returnToOriginalMode);
  },
  addTeam() {
    const { x, y, width, height } = this.shape.attrs;
    const { x1, y1, x2, y2 } = getBox(x, y, width, height);
    this.graph.findAll('node', node => {
      const { x: nodeX, y: nodeY, width: nodeWidth, height: nodeHeight } = node.getBBox();
      const nodeBox = getBox(nodeX, nodeY, nodeWidth, nodeHeight);
      if (
        x2 >= nodeBox.x1 &&
        nodeBox.x1 >= x1 &&
        x2 >= nodeBox.x2 &&
        nodeBox.x2 >= x1 &&
        y2 >= nodeBox.y1 &&
        nodeBox.y1 >= y1 &&
        y2 >= nodeBox.y2 &&
        nodeBox.y2 >= y1
      ) {
        this.graph.setItemState(node, 'selected', true);
      }
    });
  },
};
