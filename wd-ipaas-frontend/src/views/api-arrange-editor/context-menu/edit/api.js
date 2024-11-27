// @ts-check
import eventBus, { EventName } from '@/components/g6-editor/event-bus';

/**
 * @type {import('./type').ContextMenuDefinition}
 */
export default {
  getMenus(item, cmp) {
    const curModel = item?.getModel && !item.destroyed ? item.getModel() : null;
    if (curModel) {
      const { form } = curModel;
      if (form && form.dataAssetApi && form.dataAssetApi.dataAssetApiId) {
        return [
          { cmd: 'edit', name: '编辑', divided: true },
          { cmd: 'viewDetail', name: '查看详情', divided: true },
        ];
      }
    }
    return [{ cmd: 'edit', name: '编辑', divided: true }];
  },

  on_edit(command, node) {
    eventBus.$emit(EventName.nodeEdit, node);
  },
  on_viewDetail(command, node) {
    eventBus.$emit(EventName.nodeViewDetail, node);
  },
};
