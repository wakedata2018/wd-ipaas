import { initModelForm } from '../helper';
import { definitionRegistry } from '../registry';
import { ApiModelForm } from '../types';

/**
 * 数据表
 */
definitionRegistry.register('api_custom_sql', {
  type: 'api_custom_sql',

  beforeCreate(model) {
    return initModelForm(model);
  },

  removable() {
    return true;
  },

  async validate(model) {
    const { name, desc } = model.form as ApiModelForm;
    return !!name && !!desc;
  },

  renameable() {
    return true;
  },

  copyable() {
    return true;
  },
});
