import { definitionRegistry } from '../registry';

/**
 * 开始节点
 */
definitionRegistry.register('start', {
  type: 'start',

  beforeCreate(model, graph) {
    const node = graph.findById('start');
    if (node != null) {
      throw new Error('只能拖拽一个开始节点');
    }
    return model;
  },

  removable() {
    return false;
  },

  renameable() {
    return false;
  },

  copyable() {
    return false;
  },
});
