import helperParam from './helper/param.js';
import { ApiType, ApiPublicKind } from '@/utils/enum/index.js';
import { v1 as uniqueId } from 'uuid';
import { getInitDataAssetApi } from '../global/task-conf.js';
import { SERVICE_TYPE } from '@/utils/enum';

const mapApiForm = {};

function getDefaultEventReceiveAttr() {
  return {
    apiId: null,
    id: null,
    eventCode: null,
    clusterAddress: null,
    mqType: SERVICE_TYPE[0].value,
    topic: null,
    accessKey: null,
    secretKey: null,
    messageTemplate: '',
    clazzName: ApiType._event_receive.clazzName,
  };
}

mapApiForm[ApiType.EVENT_RECEIVE] = function (item, cmp) {
  return {
    name: 'event_receive',
    desc: '接收事件',
    dataAssetApi: {
      ...getInitDataAssetApi(),
      apiType: ApiType.EVENT_RECEIVE,
      apiAttr: getDefaultEventReceiveAttr(),
      dataAssetApiId: cmp && cmp.apiObj ? cmp.apiObj.dataAssetApiId : null,
    },
    parameters: [],
    results: [],
    publicKind: ApiPublicKind.SHARE,
    requestParamMappings: [],
  };
};

export default {
  initForm(item, cmp) {
    return mapApiForm[ApiType.EVENT_RECEIVE]?.(item, cmp) || {};
  },
  toNode(node, operator) {
    // console.log('toNode: ', node, operator);
    const { component = {}, requestParamMappings = [] } = operator;
    node.cmp = {
      clazzName: component.clazzName,
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
      name: operator.name,
      desc: operator.desc,
    };
    // console.log('node:::::::::', node);
    return node;
  },
  // toParam(nodeData, edges) {
  //   const { form, id, data } = nodeData;
  //   const param = {
  //     groovy: (form && form.groovy) || '',
  //     clazzName: data.className,
  //     operatorId: id,
  //     desc: form.desc,
  //     uniqueName: data.uniqueName,
  //     name: form.name,
  //     ...helperParam.getOutOperators(data.typeOfEdge, id, edges)
  //   };
  //   return param;
  // }
  toParam(nodeData, edges) {
    const { form, id, data, comboId } = nodeData;
    const finalForm = Object.assign({}, form);
    delete finalForm.requestParamMappings;
    const param = {
      component: {
        ...finalForm,
      },
      name: form.name ?? 'event_receive',
      clazzName: data.className,
      operatorId: id,
      desc: form.desc ?? '接收事件',
      uniqueName: data.uniqueName,
      requestParamMappings: form.requestParamMappings,
      parentOperatorId: helperParam.filterAbnormalStr(comboId),
      ...helperParam.getOutOperators(data.typeOfEdge, id, edges),
    };
    return param;
  },
  /**
   * setForm: toApiNode使用
   */
  setForm(item, cmp) {
    return this.initForm(item, cmp);
  },
};
