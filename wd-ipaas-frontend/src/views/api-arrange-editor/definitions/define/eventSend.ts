import { definitionRegistry } from '../registry';
import { ApiModelForm } from '../types';

/**
 * 发送算子节点
 */
definitionRegistry.register('event_send', {
  type: 'event_send',

  beforeCreate(model, graph) {
    return model;
  },

  async validate(model) {
    const { dataAssetApi, name, desc } = model.form as ApiModelForm;
    const { mqType, clusterAddress, topic, messageTemplate } = dataAssetApi.apiAttr as any;
    return !!(name && desc && mqType && clusterAddress && topic && messageTemplate);
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
