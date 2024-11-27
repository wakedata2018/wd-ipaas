import { ContextMenuDefinition } from './type';
import eventBus, { EventName } from '@/components/g6-editor/event-bus';

const definition: ContextMenuDefinition = {
  getMenus() {
    return [{ cmd: 'edit', name: '编辑' }];
  },

  on_edit(command, node) {
    eventBus.$emit(EventName.nodeEdit, node);
  },
};

export default definition;
