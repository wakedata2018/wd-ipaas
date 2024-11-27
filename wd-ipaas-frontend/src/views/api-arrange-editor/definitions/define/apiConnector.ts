import { initConnectorForm } from '../helper';
import { definitionRegistry } from '../registry';
import { ApiModelForm } from '../types';

/**
 * 连接器
 */
definitionRegistry.register('api_connector', {
  type: 'api_connector',

  beforeCreate(model) {
    return initConnectorForm(model);
  },

  async validate(model) {
    const { name, desc, connectorSecretKey } = model.form as ApiModelForm;
    return !!name && !!desc && !!connectorSecretKey?.secretKey;
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
