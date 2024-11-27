import BaseEnum from './enum-base';

class ApiType extends BaseEnum {
  constructor() {
    super();
    this._custom_sql = {
      value: 'CUSTOM_SQL',
      label: '自定义SQL',
      type: 'original',
    };
    this._normal_table = {
      value: 'NORMAL_TABLE',
      label: '表单API',
      type: 'original',
    };
    this._external_http = {
      value: 'EXTERNAL_HTTP',
      label: '注册HTTP API',
      type: 'original',
      clazzName: 'com.wakedata.dw.open.model.api.external.http.HttpExternalApiAttr',
    };
    this._connector_api = {
      value: 'CONNECTOR_API',
      label: '连接器',
      type: 'original',
      clazzName: 'com.wakedata.dw.open.model.api.flow.operator.connector.ConnectorOperator',
    };
    this._web_service = {
      value: 'WEB_SERVICE',
      label: 'WebService',
      type: 'original',
      clazzName: 'com.wakedata.dw.open.model.api.external.http.HttpExternalApiAttr',
    };
    this._event_receive = {
      value: 'EVENT_RECEIVE',
      label: '接收事件',
      type: 'original',
      clazzName: 'com.wakedata.dw.open.model.api.event.EventReceiveApiAttr',
    };
    this._lite_flow = {
      value: 'LITE_FLOW',
      label: '服务编排',
      type: 'canvas',
      clazzName: 'com.wakedata.dw.open.model.api.flow.ApiFlowAttr',
    };
    this._event_send = {
      value: 'EVENT_SEND',
      label: '发送事件',
      type: 'original',
      clazzName: 'com.wakedata.dw.open.model.api.event.EventSendApiAttr',
    };
    this._flow_branch = {
      value: 'FLOW_BRANCH',
      label: '分支算子',
      type: 'original',
      clazzName: 'com.wakedata.dw.open.model.api.flow.operator.branch.BranchOperator',
    };
    this._flow_judge = {
      value: 'FLOW_JUDGE',
      label: '判断算子',
      type: 'original',
      clazzName: 'com.wakedata.dw.open.model.api.flow.operator.judge.JudgeOperator',
    };

    this._list.push(
      this._custom_sql,
      this._normal_table,
      this._external_http,
      this._connector_api,
      this._web_service,
      this._lite_flow,
      this._event_send,
      this._event_receive,
      this._flow_branch,
      this._flow_judge
    );
  }

  get CUSTOM_SQL() {
    return this._custom_sql.value;
  }

  get NORMAL_TABLE() {
    return this._normal_table.value;
  }

  get EXTERNAL_HTTP() {
    return this._external_http.value;
  }

  get CONNECTOR_API() {
    return this._connector_api.value;
  }

  get EVENT_RECEIVE() {
    return this._event_receive.value;
  }

  get LITE_FLOW() {
    return this._lite_flow.value;
  }

  get EVENT_SEND() {
    return this._event_send.value;
  }

  get FLOW_BRANCH() {
    return this._flow_branch.value;
  }

  get FLOW_JUDGE() {
    return this._flow_judge.value;
  }

  get WEB_SERVICE() {
    return this._web_service.value;
  }
}
export default new ApiType();
