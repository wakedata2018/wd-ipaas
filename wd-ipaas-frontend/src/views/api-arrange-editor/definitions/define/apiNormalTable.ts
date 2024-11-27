import { initModelForm } from '../helper';
import { definitionRegistry } from '../registry';
import { ApiModelForm } from '../types';

/**
 * 数据表
 */
definitionRegistry.register('api_normal_table', {
  type: 'api_normal_table',

  beforeCreate(model) {
    return initModelForm(model);
  },
  async validate(model) {
    const { name, desc } = model.form as ApiModelForm;
    return !!name && !!desc;
  },

  removable() {
    return true;
  },

  renameable() {
    return true;
  },

  copyable() {
    return true;
  },
});
