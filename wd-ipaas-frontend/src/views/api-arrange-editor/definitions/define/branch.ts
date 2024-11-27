import { definitionRegistry } from '../registry';
import { ApiModelForm } from '../types';

/**
 * 分支节点
 */
definitionRegistry.register('branch', {
  type: 'branch',

  beforeCreate(model, graph) {
    return model;
  },

  async validate(model) {
    const { name, desc } = model.form as ApiModelForm;
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
