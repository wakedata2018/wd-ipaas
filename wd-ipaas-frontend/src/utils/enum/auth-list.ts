import { FatFormMode } from '@wakeadmin/components';

export interface AuthList {
  id: number;
  connectorName: string;
  connectorEnvironmentName: string;
  connnectorApiName: string;
  updateTime: string;
}

export interface ConditionValue {
  dataType: string;
  expression: string;
  type: string;
}

export interface AuthConfigParamCondition {
  desc: string;
  id: string;
  operator: string;
  value1: ConditionValue;
  value2?: ConditionValue;
}

export interface AuthConfigParam {
  condition: AuthConfigParamCondition;
}

export interface AuthInfo {
  connectorName: string;
  authConfigParam: AuthConfigParam;
  connectorEnvironmentId: number;
  connectorApiGroupId: number;
  connectorApiId: number;
  connectorId: number;
}

export interface EnvParams {
  connectorId: number;
}

export interface EnvInfo {
  id: number;
  addressName: string;
}

export interface PlatformGroup {
  id: number;
  groupName: string;
}

export interface InterfaceList {
  id: number;
  apiName: string;
}

export interface AuthListDialogRef {
  open(detail: AuthInfo, mode: FatFormMode): void;
}

export enum HttpParamKind {
  HEAD = 'HEAD',
  QUERY = 'QUERY',
  BODY = 'BODY',
}

export interface InterfaceParams {
  id: number | string;
  assetColumns: string;
  assetDataType: string;
  required: boolean;
  sample: string;
  description: string;
  httpParamKind?: HttpParamKind;
  type?: HttpParamKind;
  responsePostData?: string;
  jsonSchema?: string;
  expression: string;
  children?: InterfaceParams[];
}

export enum ParamsType {
  requestParams = 'requestParams',
  responseParams = 'responseParams',
}

export enum ReqMethod {
  'POST' = 'POST',
  'GET' = 'GET',
}
export interface RequestAndResponseParams {
  reqMethod: ReqMethod;
  requestParams: Omit<InterfaceParams[], 'responsePostData'>;
  responseParams: Omit<InterfaceParams[], 'jsonSchema'>;
}

export enum ParamsTabsKey {
  RequestHeader = 'request-header',
  RequestQuery = 'request-query',
  RequestBody = 'request-body',
  ResponseHeader = 'response-header',
  ResponseBody = 'response-body',
}

export enum AuthType {
  NO_AUTH = 'NO_AUTH',
  TOKEN_AUTH = 'TOKEN_AUTH',
  CONNECTOR_AUTH = 'CONNECTOR_AUTH',
}

export const AuthTypeOptions = [
  {
    label: 'APP鉴权',
    value: AuthType.NO_AUTH,
  },
  {
    label: 'TOKEN鉴权',
    value: AuthType.TOKEN_AUTH,
  },
  {
    label: '连接器鉴权',
    value: AuthType.CONNECTOR_AUTH,
  },
];
