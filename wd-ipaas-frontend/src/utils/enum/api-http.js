const POSITION = {
  QUERY: {
    key: 'query',
    name: 'QUERY',
  },
  HEAD: {
    key: 'head',
    name: 'HEAD',
  },
  // BODY: {
  //   key: 'body',
  //   name: 'BODY',
  // },
};

// POSITION.BODY.name
const POSITION_LIST = [POSITION.QUERY.name, POSITION.HEAD.name];

const TYPES = {
  STRING: {
    key: 'string',
    name: 'string',
  },
  NUMBER: {
    key: 'number',
    name: 'number',
  },
  LONG: {
    key: 'long',
    name: 'long',
  },
  DOUBLE: {
    key: 'double',
    name: 'double',
  },
  JSON: {
    key: 'json',
    name: 'json',
  },
  ARRAY: {
    key: 'array',
    name: 'array',
  },
  BOOLEAN: {
    key: 'boolean',
    name: 'boolean',
  },
  INTEGER: {
    key: 'integer',
    name: 'integer',
  },
  OBJECT: {
    key: 'object',
    name: 'object',
  },
};

const TYPES_LIST = [TYPES.STRING.name, TYPES.NUMBER.name, TYPES.JSON.name];
const QUERY_TYPES_LIST = [TYPES.STRING.name, TYPES.BOOLEAN.name,TYPES.LONG.name, TYPES.DOUBLE.name, TYPES.INTEGER.name];
const RESPONSE_TYPE_LIST = Object.values(TYPES).map(o => o.name);

const TYPE_ELE = {
  JSON_OBJECT: {
    key: 1,
    assetColumns: 'jsonObject',
    childApiRespParams: [],
  },
  HEAD: {
    key: 2,
    assetColumns: 'HEAD',
    childApiRespParams: [],
    type: 'HEAD',
  },
  BODY: {
    key: 3,
    assetColumns: 'BODY',
    childApiRespParams: [],
    type: 'BODY',
  },
};

const INIT_ELE = [
  {
    ...TYPE_ELE.JSON_OBJECT,
    childApiRespParams: [TYPE_ELE.HEAD, TYPE_ELE.BODY],
  },
];

const PARAMS_TYPE = {
  REQUEST_HEAD: '请求头',
  QUERY: 'QUERY请求参数',
  REQUEST_BODY: '请求体',
  RESPONSE_HEAD: '响应头',
  RESPONSE_BODY: '响应体',
};

export { POSITION_LIST, TYPES_LIST, QUERY_TYPES_LIST, TYPES, INIT_ELE, TYPE_ELE, RESPONSE_TYPE_LIST, PARAMS_TYPE };
