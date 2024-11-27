import { definitionRegistry } from '../registry';
import { ApiModelForm } from '../types';

/**
 * 接收事件节点
 */
definitionRegistry.register('event_receive', {
  type: 'event_receive',

  beforeCreate(model, graph) {
    const node = graph.findById('event_receive');
    if (node != null) {
      throw new Error('只能拖拽一个接收事件节点');
    }
    /** 赋予识别的唯一id */
    model.id = 'event_receive';
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
    return false;
  },
});
