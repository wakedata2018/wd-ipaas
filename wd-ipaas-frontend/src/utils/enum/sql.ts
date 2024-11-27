export interface DataSource {
  id: number;
  dbType: string;
  connectionName: string;
}
export interface RequestOrParamsData {
  alias: string;
  datasourceTableColumnName: string;
  datasourceTableColumnType: string;
  required: boolean;
  sample: string;
  datasourceTableColumnDesc: string;
  allowEmpty: boolean;
}

export interface ResponseParams {
  assetColumns: string;
  assetDatatype: string;
  required: boolean;
  sample: string;
  descriptions: string;
}

export interface RequestParams extends ResponseParams {
  allowEmpty: boolean;
  type: string;
  httpParamKind: string;
  paramValueType: string;
  expression: string;
  fixedValue: string;
}

export interface RequestParamMappingsType {
  field: string;
  operatorId: string;
  httpParamKind: string;
  type: string;
  expression?: string;
  fixedValue?: string;
}

export enum ParamsType {
  REQUEST,
  RESPONSE,
}
