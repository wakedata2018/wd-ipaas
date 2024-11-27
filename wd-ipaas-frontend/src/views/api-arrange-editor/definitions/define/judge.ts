import { definitionRegistry } from '../registry';
import { ApiModelForm } from '../types';

/**
 * 判断节点
 */
definitionRegistry.register('judge', {
  type: 'judge',

  beforeCreate(model, graph) {
    return model;
  },

  async validate(model) {
    const { dataAssetApi, name, desc } = model.form as ApiModelForm;
    if (dataAssetApi?.comparisonValue) {
      const val = dataAssetApi.comparisonValue;
      return !!(val.value1?.expression && val.operator && name && desc);
    }
    return false;
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
