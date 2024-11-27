import { IG6GraphEvent } from '@antv/g6';
import { DEFAULT_HOVER, DEFAULT_SELECTED } from '../constants';
import eventBus, { EventName } from '../event-bus';

export default {
  getDefaultCfg(): object {
    return {
      hoverState: DEFAULT_HOVER,
      selectedState: DEFAULT_SELECTED,
    };
  },
  getEvents() {
    return {
      'node:mouseover': 'onMouseover',
      'node:mouseleave': 'onMouseleave',
      'combo:mouseover': 'onMouseover',
      'combo:mouseleave': 'onMouseleave',
      'edge:mouseover': 'onMouseover',
      'edge:mouseleave': 'onMouseleave',
    };
  },
  onMouseover(e: IG6GraphEvent) {
    const self = this as any;
    const graph = self.graph;
    const { item, shape } = e;
    const group = item?.getContainer();

    const { tooltipContent } = (shape as any).attrs;
    if (tooltipContent) {
      eventBus.$emit(EventName.showOrHideTooltip, true, tooltipContent);
    } else {
      eventBus.$emit(EventName.showOrHideTooltip, false);
    }

    if (graph.getCurrentMode() !== 'view' && !!item?.getModel().outPoints) {
      if (e.target.get('name') === 'anchor-point') {
        const childs = group?.findAll(g => g.attrs.hoverChange);
        childs?.forEach(child => {
          child.attr({ fill: '#1890ff', opacity: 0.3 });
        });
        e.target.attr('cursor', 'crosshair');
      }
    }

    if (self.shouldUpdate(e, self) && !item?.hasState(self.selectedState)) {
      graph.setItemState(item, self.hoverState, true);
    }
  },
  onMouseleave(e: IG6GraphEvent) {
    const self = this as any;
    const item = e.item;
    const graph = self.graph;
    eventBus.$emit(EventName.showOrHideTooltip, false);

    if (self.shouldUpdate(e, self) && !item?.hasState(self.selectedState)) {
      graph.setItemState(item, self.hoverState, false);
    }
  },
};
