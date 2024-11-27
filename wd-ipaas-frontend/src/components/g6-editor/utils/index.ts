import G6, { Node, Graph } from '@antv/g6';

export { v1 as uniqueId } from 'uuid';
export * from './layout';
export * from './path';

export const getBox = (x: number, y: number, width: number, height: number) => {
  const xw = x + width;
  const yh = y + height;
  const x1 = xw < x ? xw : x;
  const x2 = xw > x ? xw : x;
  const y1 = yh < y ? yh : y;
  const y2 = yh > y ? yh : y;
  return { x1, x2, y1, y2 };
};

export const getBBox = (nodes: Node[]) => {
  let top = 0;
  let right = 0;
  let bottom = 0;
  let left = 0;
  nodes.forEach(node => {
    const box = node.getBBox();
    if (typeof top === 'undefined') {
      top = box.minY;
      bottom = box.maxY;
      left = box.minX;
      right = box.maxX;
    }
    top = Math.min(top, box.minY);
    bottom = Math.max(bottom, box.maxY);
    left = Math.min(left, box.minX);
    right = Math.max(right, box.maxX);
  });
  return { left, top, right, bottom };
};

export function getFormatPadding(graph: Graph) {
  return G6.Util.formatPadding(graph.get('fitViewPadding'));
}

export function getViewCenter(graph: Graph) {
  const padding = getFormatPadding(graph);
  const width = graph.get('width');
  const height = graph.get('height');
  return {
    x: (width - padding[2] - padding[3]) / 2 + padding[3],
    y: (height - padding[0] - padding[2]) / 2 + padding[0],
  };
}
