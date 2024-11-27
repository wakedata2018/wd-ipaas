import { Graph, IItemBase, ModelConfig } from '@antv/g6';
import { LoopConditionType } from '@/utils/enum';
export interface ElementDefinition {
  /**
   * 元素类型
   */
  type: string;

  /**
   * 创建前的验证
   */
  beforeCreate?: (model: ModelConfig, graph: Graph) => ModelConfig | Promise<ModelConfig>;

  /**
   * 提交前的表单验证
   */
  validate?: (model: ModelConfig) => Promise<boolean>;

  /**
   * 是否可删除, 默认 true
   */
  removable?: (node: IItemBase) => boolean;

  /**
   * 是否可重命名, 默认 true
   */
  renameable?: (node: IItemBase) => boolean;

  /**
   * 是否可编辑
   */
  editable?: (node: IItemBase) => boolean;

  /**
   * 是否可复制, 默认 true
   */
  copyable?: (node: IItemBase) => boolean;
}

export interface ParamType {
  id: number;
  dataAssetId: number; // 参数自身id
  assetColumns: string; // 参数名称
  assetDatatype: string; // 参数类型
  descriptions: string; // 参数描述
  type: string; // 固定值 PARAMETERS参数
  typeAttr: string;
  httpParamKind: string; // 固定值 参数类型  QUERY请求头
  sample: string; // 默认值
  required: boolean; // 是否必填
  multiValue: any;
  jsonSchema: boolean;
  isSchema: boolean;
  defaultInputParam: any;
  autoPare: boolean;
  allowEmpty?: boolean;
}

interface ResoponseParamType {
  id: number;
  assetColumns: string; // 参数名称
  assetDatatype: string; // 参数类型
  description: string; // 参数描述
  type: 'BODY' | 'HEAD'; // 固定值  BODY响应体   HEAD响应头
  childApiRespParams: object[]; // http 响应参数
  businessType: 'LITEFLOW_RESULT'; // 固定值
  responsePostData: string; // 响应体
}

export interface DataAssetApiData {
  lesseeId: number; // 授权的应用
  dataAssetApiId: number;
  inCharge: string; // 开发人
  dataAssetPublishStatus: string; // 发布状态  UN_PUBLISH未发布
  dataAssetApiMethod: string; // api 路径
  apiName: string; // 编排名称
  updateFrequency: string; // 更新频率
  protocol: string; // 请求协议
  apiGroupId: number;
  apiDescription: string; // 编排描述
  responseContentType: string;
  reqMethod: string; // 请求方式
  apiType: string; // api类型， LITE_FLOW 编排  EXTERNAL_HTTP http算子
  operationType: string;
  /**
   * nodeConvert.paramsToNode(data.dataAssetApi.apiAttr, cmps) 转换成g6节点与边
   */
  apiAttr: {
    clazzName: string; //
    id: number;
    apiId: number;
    locationJson: string; // 画布节点及线的配置信息
    operators?: {
      clazzName: string;
      operatorId: string; // 算子id
      name: string; // 算子名称
      desc: string; // 算子描述
      outputOperators?: string[]; // 出边算子id列表
      component?: {
        name: string;
        parameters: object[];
        dataAssetApi: DataAssetApiData; // 算子具体的配置信息 DataAssetApiData不固定，根据每个算子所需要的配置而定
      };
      requestParamMappings?: object[]; // 算子请求参数列表
    }[]; // 所有算子配置
  };
  apiAttrs?: object[];
  // 判断算子特有属性
  comparisonValue?: {
    value1?: { expression?: string; type?: string };
    value2?: { expression?: string; type?: string };
    operator?: string;
  };
}

export interface ApiModelForm {
  /**
   * 订阅记录
   * 跟事件中心对接有关
   */
  subscribeRecord: Record<string, any>;
  /**
   * 编排信息配置
   */
  dataAssetApi: DataAssetApiData;
  /**
   * 编排请求参数
   **/
  parameters: ParamType[];
  /**
   * 表单相关
   */
  results: ParamType[];
  /**
   * 与编排无关参数
   * http请求响应参数
   */
  responseParams: any[];
  /**
   * 编排响应参数
   */
  resutRespParamDTOS: ResoponseParamType[];
  publicKind?: string;

  name?: string;
  desc?: string;

  // transform_groovery_script 特有
  groovy?: string;
  resultData?: string;
  connectorSecretKey: {
    secretKey: string;
  };
}

export interface ForEachModelForm {
  name?: string;
  desc?: string;
  loopCondition: {
    // 循环条件，引用数据
    operatorId: string;
    field: string;
    dataType: string;
    type: string;
    fixedValue: string | null;
    expression: string | null;
    httpParamKind: string;
    expressionIsJson: string;
    parentFiled: string;
  };
  loopCount: number; // 指定循环次数，指定次数之后条件无效>0&&<10000
  loopType: LoopConditionType; // 循环类型 0：指定次数 1：指定条件
  breakCondition: {
    // 退出条件
    type: LoopConditionType; // 退出类型 0：指定次数 1：指定条件
    count: number; // 指定退出次数
    condition: {
      // 指定条件
      id: string;
      desc: string;
      value1: {
        type: string;
        dataType: string;
        expression: string;
      };
      operator: string;
      value2?: {
        type?: string;
        dataType?: string;
        expression?: string;
      };
    };
  };
}

export interface SqlModelForm {
  name?: string;
  desc?: string;
  /**
   * 编排请求参数
   **/
  parameters: ParamType[];
  results: ParamType[];
  responseParams: any[];
  sqlOperationType: string;
  sqlOperatorParam: {
    sql: string;
    dataSourceId: number;
  };
  requestParamMappings: [];
}

export interface TryCatchModelForm {
  name?: string;
  desc?: string;
}
