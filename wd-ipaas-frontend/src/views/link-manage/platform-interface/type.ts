export enum InterfaceStatusEnum {
  Disable,
  Enable,
}

export const InterfaceStatus = [
  {
    label: '已下线',
    value: InterfaceStatusEnum.Disable,
  },
  {
    label: '已上线',
    value: InterfaceStatusEnum.Enable,
  },
];

export enum ApiTypeEnum {
  CUSTOM_SQL = 'CUSTOM_SQL',
  NORMAL_TABLE = 'NORMAL_TABLE',
  EXTERNAL_HTTP = 'EXTERNAL_HTTP',
  LITE_FLOW = 'LITE_FLOW',
  EVENT_SEND = 'EVENT_SEND',
  EVENT_RECEIVE = 'EVENT_RECEIVE',
  FLOW_BRANCH = 'FLOW_BRANCH',
  WEB_SERVICE = 'WEB_SERVICE',
  FLOW_JUDGE = 'FLOW_JUDGE',
  CONNECTOR_API = 'CONNECTOR_API',
}

export enum ParamPosition {
  HEAD = 'HEAD',
  QUERY = 'QUERY',
  BODY = 'BODY',
  FILTER = 'FILTER',
}

export enum ReqMethodEnum {
  GET = 'GET',
  POST = 'POST',
}

export interface RequestOrResponseParams {
  assetColumn: string;
  assetDataType: string;
  httpParamKind: ParamPosition;
  reqMethod: ReqMethodEnum;
  required?: boolean;
  sample?: string;
  description?: string;
  jsonSchema?: string;
  responsePostData?: string;
}

export interface ApiBasicInfo {
  id: number;
  apiName: string;
  connectorName: string;
  enable: boolean;
  groupName: string;
  connectorId: number;
  apiGroupId: number;
  apiMethod: string;
  reqMethod: string;
  apiType: ApiTypeEnum;
  apiDescription: string;
}
export interface PlatFormInterfaceInfo {
  connectorApi: ApiBasicInfo;
  requestParams: RequestOrResponseParams[];
  responseParams: RequestOrResponseParams[];
}

export interface PlatFormInterfaceList {
  id: number;
  apiName: string;
  connectorName: string;
  connectorId: number;
  enableStatus: number;
  apiGroupId: number;
}

export interface InterfaceGroup {
  id?: number;
  connectorId?: number;
  groupName?: string;
  connectorName?: string;
  connectorApiGroupDTOList?: InterfaceGroup[];
  children?: InterfaceGroup[];
}

export interface PlatFormOption {
  label: string;
  value: string;
}

export enum ParamsType {
  'REQUEST' = 'request',
  'RESPONSE' = 'response',
}

export interface CurSelectGroupInfo {
  id: number;
  parentId?: number;
}

export const enableStatusEnum = {
  ON_LINE: {
    label: '已上线',
    value: 'ON_LINE',
  },
  OFF_LINE: {
    label: '已下线',
    value: 'OFF_LINE',
  },
};
