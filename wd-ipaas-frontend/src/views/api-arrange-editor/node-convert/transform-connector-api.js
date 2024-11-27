import helperParam from './helper/param.js';
import { v1 as uniqueId } from 'uuid';
import { ApiType } from '@/utils/enum';

export function getInitForm(item = {}) {
  // 连接器算子 config 表单需要的数据
  const res = {
    connectorApi: {
      // 连接器接口信息
      ...item,
    },
    requestParams: [],
    responseParams: [],
    connectorApiAuthType: item.connectorApiAuthType, // 必填，从接口树那里带入
    environmentId: '', // 选择了密钥才有，没有可以保存但是流程跑不通
    connectorSecretKey: {}, // 设置里选择的密钥
    requestParamMappings: [], // 参数值映射
  };
  return res;
}

export default {
  initForm(item) {
    return getInitForm(item);
  },

  toNode(node, operator) {
    const { component = {}, requestParamMappings = [] } = operator;
    node.cmp = {
      clazzName: operator.clazzName,
      type: component.type,
      componentId: component.componentId,
      name: component.name,
      desc: component.desc,
    };
    node.form = {
      ...component,
      name: operator.name,
      desc: operator.desc,
      requestParamMappings,
    };
    node.data = {
      ...node.data,
      uniqueName: 'api_connector',
      typeOfEdge: ['multiOutput', 'withoutInput'],
      name: operator.name,
      desc: operator.desc,
    };
    node.label = operator.desc || node.label; // node卡片描述
    // console.log('toNode res ==>', {
    //   node,
    //   operator,
    //   component,
    // })
    return node;
  },

  /**
   * 保存时会掉这个方法把连接器算子的node 转成 接口需要的params,
   * 然后插入 dataAssetApi.apiAttr.operators
   */
  toParam(nodeData, edges) {
    const {
      id, // 算子的唯一标识
      data, // g6 画图里节点数据
      form, // 双击弹出设置时表单里的数据
    } = nodeData;
    const finalForm = Object.assign({}, form, {
      connectorApiAuthType: form.connectorApiAuthType,
    });
    delete finalForm.requestParamMappings;
    const param = {
      component: {
        ...data,
        ...finalForm,
      },
      operatorId: id,
      name: form.name,
      desc: form.desc,
      clazzName: ApiType._connector_api.clazzName,
      uniqueName: data.uniqueName,
      requestParamMappings: form.requestParamMappings,
      ...helperParam.getOutOperators(data.typeOfEdge, id, edges),
    };
    // console.log('===> toParam', id, {param,form, data, nodeData})
    return param;
  },

  setForm(item, cmp) {
    return this.initForm(item);
  },
};
