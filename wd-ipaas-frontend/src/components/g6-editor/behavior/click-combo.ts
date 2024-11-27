import { IG6GraphEvent } from '@antv/g6';

export default {
  getEvents() {
    return {
      'combo:click': 'onClick',
    };
  },
  onClick(e: IG6GraphEvent) {
    const graph = (this as any).graph;
    if (e.target.get('name') === 'combo-marker-shape') {
      graph.collapseExpandCombo(e.item);
      if (graph.get('layout')) {
        graph.layout();
      } else {
        graph.refreshPositions();
      }
    }
  },
};
