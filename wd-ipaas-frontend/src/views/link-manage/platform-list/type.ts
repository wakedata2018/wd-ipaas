export enum PlatFormStatus {
  Disabled = 0,
  Enabled = 1,
}

export interface EnvList {
  addressName: string;
  environmentAddress: string;
}

export interface AuthParamsList {
  paramName: string;
  paramType: paramTypeEnum;
  isRequired: Boolean;
  hiddenType: Boolean;
  defaultValue: string | number;
  description: string;
}

export interface PlatFormBasicInfo {
  id: number;
  name: string;
  authType: string;
  groupId: number;
  groupName: string;
  version: string;
  developer: string;
  website: string;
  apiCount: number;
  updateTime: string;
  enableStatus: boolean;
  connectorEnvironmentAddressDTOList: EnvList[];
  connectorParamsDTOList: AuthParamsList[];
  platformIntroduction?: string;
  phone?: string;
  email?: string;
  helpDocument?: string;
  privacyAgreement?: string;
  usageAgreement?: string;
}

export type paramTypeEnum = 'string' | 'number';

export interface PlatFormGroupInfo {
  groupName: string;
  id: number;
  children?: PlatFormGroupInfo[];
}

export interface PlatFormGroupOption {
  label: string;
  value: number;
  children?: PlatFormGroupOption[];
}

export interface GroupOptions {
  id: number;
  label: string;
  value: string;
  children?: GroupOptions[];
}
