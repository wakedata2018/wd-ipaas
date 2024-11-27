import { definitionRegistry } from '../registry';
import { ForEachModelForm } from '../types';
/**
 * 循环节点
 */
definitionRegistry.register('foreach', {
  type: 'foreach',

  beforeCreate(model, graph) {
    return model;
  },

  async validate(model) {
    const { name, desc, loopCondition } = model.form as ForEachModelForm;
    return !!name && !!desc && !!loopCondition?.type && (!!loopCondition?.expression || !!loopCondition?.fixedValue);
  },

  removable() {
    return true;
  },

  renameable() {
    return false;
  },

  copyable() {
    return true;
  },
});
