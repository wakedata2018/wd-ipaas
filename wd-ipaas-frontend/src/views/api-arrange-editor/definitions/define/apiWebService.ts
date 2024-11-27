import { initModelForm } from '../helper';
import { definitionRegistry } from '../registry';

/**
 * webservice
 */
definitionRegistry.register('api_web_service', {
  type: 'api_web_service',

  beforeCreate(model) {
    return initModelForm(model);
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
