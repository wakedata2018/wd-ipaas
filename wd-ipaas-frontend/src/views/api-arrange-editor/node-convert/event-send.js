import { ApiType, ApiPublicKind } from '@/utils/enum/index.js';
import { getInitDataAssetApi } from '../global/task-conf.js';
import helperParam from './helper/param.js';
import { v1 as uniqueId } from 'uuid';
import { SERVICE_TYPE } from '@/utils/enum';

const mapApiForm = {};

function getDefaultEventSendAttr() {
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
    clazzName: ApiType._event_send.clazzName,
  };
}

mapApiForm[ApiType.EVENT_SEND] = function (item, cmp) {
  return {
    name: 'event_send',
    desc: '发送事件',
    dataAssetApi: {
      ...getInitDataAssetApi(),
      apiType: ApiType.EVENT_SEND,
      apiAttr: getDefaultEventSendAttr(),
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
    return mapApiForm[ApiType.EVENT_SEND]?.(item, cmp) || {};
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
    return node;
  },
  toParam(nodeData, edges) {
    const { form, id, data, comboId } = nodeData;
    const finalForm = Object.assign({}, form);
    delete finalForm.requestParamMappings;
    const param = {
      component: {
        ...finalForm,
      },
      name: form.name ?? 'event_send',
      clazzName: data.className,
      operatorId: id,
      desc: form.desc ?? '发送事件',
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
