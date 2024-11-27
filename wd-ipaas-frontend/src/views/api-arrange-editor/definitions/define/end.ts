import { definitionRegistry } from '../registry';

/**
 * 结束节点
 */
definitionRegistry.register('end', {
  type: 'end',

  beforeCreate(model, graph) {
    const node = graph.findById('end');
    if (node != null) {
      throw new Error('只能拖拽一个结束节点');
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
