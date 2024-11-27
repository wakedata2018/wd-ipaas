import { definitionRegistry } from '../registry';
import { SqlModelForm } from '../types';

/**
 * SQL算子节点
 */
definitionRegistry.register('sql_execute', {
  type: 'sql_execute',

  beforeCreate(model) {
    return model;
  },

  async validate(model) {
    const { name, desc, sqlOperatorParam, parameters, responseParams } = model.form as SqlModelForm;
    return (
      !!name && !!desc && !!sqlOperatorParam.sql && !!sqlOperatorParam.dataSourceId && !!parameters && !!responseParams
    );
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
