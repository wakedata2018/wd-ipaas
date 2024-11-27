import { definitionRegistry } from '../registry';
import { TryCatchModelForm } from '../types';
/**
 * 循环节点
 */
definitionRegistry.register('try_catch', {
  type: 'try_catch',

  beforeCreate(model, graph) {
    return model;
  },

  async validate(model) {
    const { name, desc } = model.form as TryCatchModelForm;
    return !!name && !!desc;
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
