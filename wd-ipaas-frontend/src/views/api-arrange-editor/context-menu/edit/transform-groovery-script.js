import eventBus, { EventName } from '@/components/g6-editor/event-bus';

export default {
  getMenus(item, cmp) {
    return [{ cmd: 'edit', name: '编辑', divided: true }];
  },

  on_edit(command, node) {
    eventBus.$emit(EventName.nodeEdit, node);
  },
};
