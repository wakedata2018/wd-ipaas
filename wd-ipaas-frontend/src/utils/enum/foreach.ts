export enum LoopConditionType {
  'COUNT',
  'CONDITION',
}

type ParamsType = 'HEAD' | 'QUERY' | 'BODY';

export interface ApiRespParamDTOS {
  id: string;
  assetColumns: string;
  assetDataType: string;
  nodeId: string;
  type: ParamsType;
  expression: string;
  label: string;
  description: string;
  responsePostData?: string;
  childApiRespParams?: ApiRespParamDTOS[];
  children?: ApiRespParamDTOS[];
}

export interface AllOperatorParams {
  nodeName: string;
  nodeId: string;
  apiRespParamDTOS: ApiRespParamDTOS[];
}

export interface JsonSchemaItem {
  name: string;
  title: string;
  description: string;
  type: string;
  properties: Record<string, JsonSchemaItem>;
}

export interface JsonSchema extends JsonSchemaItem {
  items?: JsonSchemaItem;
}

export interface MethodItem {
  description: string;
  example: string;
  method: string;
  param: string;
  paramDesc: string;
  returnType: string;
}

export interface MethodList {
  method: string;
  description: string;
  list: MethodItem[];
}
