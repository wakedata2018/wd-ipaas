import { definitionRegistry } from '../registry';
import { ApiModelForm } from '../types';

/**
 * groovery_script算子节点
 */
definitionRegistry.register('transform_groovery_script', {
  type: 'transform_groovery_script',

  beforeCreate(model, graph) {
    return model;
  },

  async validate(model) {
    const { groovy, resultData, name, desc } = model.form as ApiModelForm;
    return !!(name && desc && groovy && resultData);
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
