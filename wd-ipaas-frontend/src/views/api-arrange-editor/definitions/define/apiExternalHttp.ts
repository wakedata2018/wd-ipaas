import { initModelForm } from '../helper';
import { definitionRegistry } from '../registry';
import { ApiModelForm } from '../types';

/**
 * http api
 */
definitionRegistry.register('api_external_http', {
  type: 'api_external_http',

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
