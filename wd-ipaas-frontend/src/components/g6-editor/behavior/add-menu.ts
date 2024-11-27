import { IG6GraphEvent } from '@antv/g6';
import eventBus, { EventName } from '../event-bus';

export default {
  getEvents() {
    return {
      'node:contextmenu': 'onContextmenu',
      'combo:contextmenu': 'onContextmenu',
    };
  },
  onContextmenu(e: IG6GraphEvent) {
    e.originalEvent.preventDefault();
    eventBus.$emit(EventName.contextmenuClick, e);
  },
};
