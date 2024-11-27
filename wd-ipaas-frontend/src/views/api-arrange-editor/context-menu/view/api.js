import eventBus, { EventName } from '@/components/g6-editor/event-bus';

export default {
  getMenus(item, cmp) {
    return [{ cmd: 'viewDetail', name: '查看详情', divided: true }];
  },
  on_viewDetail(command, node) {
    eventBus.$emit(EventName.nodeViewDetail, node);
  },
};
