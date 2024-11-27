import { STATIC_PAGE_ID, STATIC_PAGE_NAME } from '../../enums.js';

export const GUIDE_DATA = {
  groupName: STATIC_PAGE_NAME[STATIC_PAGE_ID.GUIDE],
  id: STATIC_PAGE_ID.GUIDE,
  dataAssetApiId: STATIC_PAGE_ID.GUIDE,
  apiList: [
    // {
    //   apiName: STATIC_PAGE_NAME[STATIC_PAGE_ID.QUICK_START],
    //   id: STATIC_PAGE_ID.QUICK_START,
    //   dataAssetApiId: STATIC_PAGE_ID.QUICK_START,
    // },
    {
      apiName: STATIC_PAGE_NAME[STATIC_PAGE_ID.INTERFACE_CALL],
      id: STATIC_PAGE_ID.INTERFACE_CALL,
      dataAssetApiId: STATIC_PAGE_ID.INTERFACE_CALL,
    },
    {
      apiName: STATIC_PAGE_NAME[STATIC_PAGE_ID.ACCESS_TOKEN],
      id: STATIC_PAGE_ID.ACCESS_TOKEN,
      dataAssetApiId: STATIC_PAGE_ID.ACCESS_TOKEN,
    },
  ],
};

/** 获取accessToken数据 */
export const GetAccessTokenData = {
  request: [
    {
      reqMethod: 'GET',
      consumes: 'application/json',
      produces: '*/*',
      httpsUrl: '/dw/open/api/auth/get.access.token',
    },
  ],
  requestParams: [
    {
      name: 'appKey',
      desc: '应用appKey',
      requestType: 'query',
      required: 'false',
      dataType: 'string',
    },
    {
      name: 'timestamp',
      desc: '时间戳',
      requestType: 'query',
      required: 'false',
      dataType: 'string',
    },
    {
      name: 'sign',
      desc: '签名',
      requestType: 'query',
      required: 'false',
      dataType: 'string',
    },
  ],
  responseStatus: [
    {
      code: 100,
      desc: '操作成功!',
      schema: 'ResultDTO«AccessTokenGenerateDTO»',
    },
    {
      code: 1006,
      desc: '验证签名失败',
      schema: '',
    },
    {
      code: 1007,
      desc: '时间戳校验失败，超出了请求可重放时长',
      schema: '',
    },
    {
      code: 500001,
      desc: 'appKey不存在或未发布',
      schema: '',
    },
  ],
  responseParams: [
    {
      name: 'code',
      desc: '状态码',
      type: 'integer(int32)',
      schema: 'integer(int32)',
    },
    {
      name: 'data',
      desc: '数据',
      type: 'AccessTokenGenerateDTO',
      schema: 'AccessTokenGenerateDTO',
    },
    {
      name: 'msg',
      desc: '描述',
      type: 'string',
      schema: '',
    },
    {
      name: 'success',
      desc: '响应结果',
      type: 'boolean',
      schema: '',
    },
  ],
  schemaInfo: [
    {
      name: 'accessToken',
      desc: 'accessToken',
      type: 'string',
      schema: '',
    },
    {
      name: 'refreshToken',
      desc: 'refreshToken有效期为30天，可⽤来刷新access_token	',
      type: 'string',
      schema: '',
    },
    {
      name: 'expireIn',
      desc: 'access_token过期时间，单位秒',
      type: 'integer(int32)',
      schema: '',
    },
  ],
  sample: [
    {
      code: 0,
      data: {
        accessToken: '',
        expireIn: 0,
        refreshToken: '',
      },
      msg: '',
      success: true,
    },
  ],
};

/** 刷新accessToken数据 */
export const RefreshAccessTokenData = {
  request: [
    {
      reqMethod: 'GET',
      consumes: 'application/json',
      produces: '*/*',
      httpsUrl: '/dw/open/api/auth/refresh.token',
    },
  ],
  requestParams: [
    {
      name: 'appKey',
      desc: '应用appKey',
      requestType: 'query',
      required: 'false',
      dataType: 'string',
    },
    {
      name: 'refreshToken',
      desc: '获取到的refreshToken	',
      requestType: 'query',
      required: 'false',
      dataType: 'string',
    },
    {
      name: 'timestamp',
      desc: '时间戳',
      requestType: 'query',
      required: 'false',
      dataType: 'string',
    },
    {
      name: 'sign',
      desc: '签名',
      requestType: 'query',
      required: 'false',
      dataType: 'string',
    },
  ],
  responseStatus: [
    {
      code: 100,
      desc: '操作成功!',
      schema: 'ResultDTO«AccessTokenRefreshDTO»',
    },
    {
      code: 1006,
      desc: '验证签名失败',
      schema: '',
    },
    {
      code: 1007,
      desc: '时间戳校验失败，超出了请求可重放时长',
      schema: '',
    },
    {
      code: 1009,
      desc: 'refreshToken无效或已过期',
      schema: '',
    },
    {
      code: 500001,
      desc: 'appKey不存在或未发布',
      schema: '',
    },
  ],
  responseParams: [
    {
      name: 'code',
      desc: '状态码',
      type: 'integer(int32)',
      schema: 'integer(int32)',
    },
    {
      name: 'data',
      desc: '数据',
      type: '	AccessTokenRefreshDTO',
      schema: '	AccessTokenRefreshDTO',
    },
    {
      name: 'msg',
      desc: '描述',
      type: 'string',
      schema: '',
    },
    {
      name: 'success',
      desc: '响应结果',
      type: 'boolean',
      schema: '',
    },
  ],
  schemaInfo: [
    {
      name: 'accessToken',
      desc: 'accessToken',
      type: 'string',
      schema: '',
    },
    {
      name: 'expireIn',
      desc: '过期时间，单位秒',
      type: 'integer(int32)',
      schema: '',
    },
  ],
  sample: [
    {
      code: 0,
      data: {
        accessToken: '',
        expireIn: 0,
      },
      msg: '',
      success: true,
    },
  ],
};

/** 校验accessToken数据 */
export const CheckAccessTokenData = {
  request: [
    {
      reqMethod: 'GET',
      consumes: 'application/json',
      produces: '*/*',
      httpsUrl: '/dw/open/api/auth/check.token',
    },
  ],
  requestParams: [
    {
      name: 'appKey',
      desc: '应用appKey',
      requestType: 'query',
      required: 'false',
      dataType: 'string',
    },
    {
      name: 'accessToken',
      desc: '获取到的accessToken',
      requestType: 'query',
      required: 'false',
      dataType: 'string',
    },
    {
      name: 'timestamp',
      desc: '时间戳',
      requestType: 'query',
      required: 'false',
      dataType: 'string',
    },
    {
      name: 'sign',
      desc: '签名',
      requestType: 'query',
      required: 'false',
      dataType: 'string',
    },
  ],
  responseStatus: [
    {
      code: 100,
      desc: '操作成功!',
      schema: 'ResultDTO«boolean»',
    },
    {
      code: 1006,
      desc: '验证签名失败',
      schema: '',
    },
    {
      code: 1007,
      desc: '时间戳校验失败，超出了请求可重放时长',
      schema: '',
    },
    {
      code: 500001,
      desc: 'appKey不存在或未发布',
      schema: '',
    },
  ],
  responseParams: [
    {
      name: 'code',
      desc: '状态码',
      type: 'integer(int32)',
      schema: 'integer(int32)',
    },
    {
      name: 'data',
      desc: '数据',
      type: 'AccessTokenGenerateDTO',
      schema: 'AccessTokenGenerateDTO',
    },
    {
      name: 'msg',
      desc: '描述',
      type: 'string',
      schema: '',
    },
    {
      name: 'success',
      desc: '响应结果',
      type: 'boolean',
      schema: '',
    },
  ],
  sample: [
    {
      code: 0,
      data: true,
      msg: '',
      success: true,
    },
  ],
};
