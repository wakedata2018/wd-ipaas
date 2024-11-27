import { ApiType, ApiPublishStatus } from '@/utils/enum';
/** 画布编排 类型API专用 */
function getApiAttr() {
  return {
    apiId: null,
    clazzName: ApiType._lite_flow.clazzName,
    id: null,
    locationJson: '{}',
    operators: {},
  };
}
/**
 * CUSTOM_SQL的参数
 */
function getCustomSqlParams() {
  return {
    dataSourceId: null,
    apiSql: null,
  };
}
/**
 * NORMAL_TABLE的参数
 */
function getNormalTableParams() {
  return {
    dataSourceId: null,
    dataAssetName: null,
  };
}
function getBranchAttr() {
  return {
    apiId: null,
    clazzName: ApiType._flow_branch.clazzName,
    id: null,
    operators: {},
  };
}
function getJudgeAttr() {
  return {
    apiId: null,
    clazzName: ApiType._flow_judge.clazzName,
    id: null,
    operators: {},
  };
}
function getInitDataAssetApi(apiType) {
  return {
    apiGroupId: null,
    apiName: null,
    enName: null,
    apiType,
    apiDescription: null,
    dataAssetApiId: null,
    dataAssetApiMethod: null,
    dataAssetPublishStatus: ApiPublishStatus.UN_PUBLISH,
    protocol: 'HTTP',
    reqMethod: 'GET',
    responseContentType: 'JSON',
    secret: 'PRIVATE',
    updateFrequency: 'DAY',
    /**
     * 初始化各类型专有参数
     */
    ...(apiType === ApiType.EXTERNAL_HTTP
      ? { apiAttr: getDefaultHttpApiAttr() }
      : apiType === ApiType.LITE_FLOW || ApiType.FLOW_BRANCH
      ? { apiAttr: getApiAttr() }
      : {}),
    ...(apiType === ApiType.CUSTOM_SQL ? getCustomSqlParams() : {}),
    ...(apiType === ApiType.NORMAL_TABLE ? getNormalTableParams() : {}),
    ...(apiType === ApiType.FLOW_BRANCH ? getBranchAttr() : {}),
    ...(apiType === ApiType.FLOW_JUDGE ? getJudgeAttr() : {}),
  };
}
function getInitForm(apiType = ApiType.LITE_FLOW) {
  return {
    name: '',
    desc: '',
    dataAssetApi: getInitDataAssetApi(apiType),
    parameters: [],
    results: [],
    subscribeRecord: {},
    resutRespParamDTOS: [],
  };
}
/** http 类型API专用 */
function getDefaultHttpApiAttr() {
  return {
    clazzName: ApiType._external_http.clazzName,
    errorExample: null,
    host: null,
    id: null,
    path: null,
    resultExample: null,
    timeout: null,
    httpCodes: [],
  };
}

export { getInitForm, getInitDataAssetApi, getApiAttr, getDefaultHttpApiAttr };
