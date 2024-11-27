/**
 * mixins尽可能简单，多个业务，按业务分开文件编写
 * mixins至少有两个业务引用，才写mixins，变量和方法mix开头，标识这个在mixins里查找
 * 获取sink数据源数据，请不用写与其无关内容代码在此
 */

import datasourceApi from '../../src/api/data-base.js';

export default {
  data() {
    return {
      mix_dataSourceType: []
    };
  },
  methods: {
    mix_getDataSourceTypes(functionType, isTask = false) {
      return datasourceApi.showDataType({
          functionType,
          isTask
        }).done(res => {
        this.mix_dataSourceType = res.data || [];
      });
    }
  }
};
