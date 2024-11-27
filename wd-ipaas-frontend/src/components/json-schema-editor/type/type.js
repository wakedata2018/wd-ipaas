import _object from './object';
import _string from './string';
import _array from './array';
import _boolean from './boolean';
import _integer from './integer';
import _number from './number';
// 'integer',
const TYPE_NAME = ['string', 'integer', 'number', 'object', 'array', 'boolean'];

const TYPE = {
  object: _object,
  string: _string,
  array: _array,
  boolean: _boolean,
  integer: _integer,
  number: _number,
};

/**
 * jsonschema数据类型枚举
 */
const DATA_TYPE_ENUM = {
  STRING: 'string',
  INTEGER: 'integer',
  LONG: 'long',
  DOUBLE: 'double',
  BOOLEAN: 'boolean',
  OBJECT: 'object',
  ARRAY: 'array',
};

/**
 * 数据类型级联选择器label展示文案
 */
const ARRAY_SUB_LABEL = {
  STRING: 'array<string>',
  INTEGER: 'array<integer>',
  LONG: 'array<long>',
  DOUBLE: 'array<double>',
  BOOLEAN: 'array<boolean>',
  OBJECT: 'array<object>',
};

/**
 * 数据类型级联选择器数据源
 */
const CASCADER_DATA_TYPE_OPTIONS = [
  {
    value: DATA_TYPE_ENUM.STRING,
    label: DATA_TYPE_ENUM.STRING,
  },
  {
    value: DATA_TYPE_ENUM.INTEGER,
    label: DATA_TYPE_ENUM.INTEGER,
  },
  {
    value: DATA_TYPE_ENUM.LONG,
    label: DATA_TYPE_ENUM.LONG,
  },
  {
    value: DATA_TYPE_ENUM.DOUBLE,
    label: DATA_TYPE_ENUM.DOUBLE,
  },
  {
    value: DATA_TYPE_ENUM.BOOLEAN,
    label: DATA_TYPE_ENUM.BOOLEAN,
  },
  {
    value: DATA_TYPE_ENUM.OBJECT,
    label: DATA_TYPE_ENUM.OBJECT,
  },
  {
    value: DATA_TYPE_ENUM.ARRAY,
    label: DATA_TYPE_ENUM.ARRAY,
    children: [
      {
        value: DATA_TYPE_ENUM.STRING,
        label: ARRAY_SUB_LABEL.STRING,
      },
      {
        value: DATA_TYPE_ENUM.INTEGER,
        label: ARRAY_SUB_LABEL.INTEGER,
      },
      {
        value: DATA_TYPE_ENUM.LONG,
        label: ARRAY_SUB_LABEL.LONG,
      },
      {
        value: DATA_TYPE_ENUM.DOUBLE,
        label: ARRAY_SUB_LABEL.DOUBLE,
      },
      {
        value: DATA_TYPE_ENUM.BOOLEAN,
        label: ARRAY_SUB_LABEL.BOOLEAN,
      },
      {
        value: DATA_TYPE_ENUM.OBJECT,
        label: ARRAY_SUB_LABEL.OBJECT,
      },
    ],
  },
];

/**
 * 请求体根节点参数类型
 */
const REQUEST_ROOT_TYPE_OPTIONS = [
  {
    value: DATA_TYPE_ENUM.OBJECT,
    label: DATA_TYPE_ENUM.OBJECT,
  },
  {
    value: ARRAY_SUB_LABEL.OBJECT,
    label: ARRAY_SUB_LABEL.OBJECT,
  },
];

/**
 * 参数类型 request 请求体 | response 响应体
 */
const PARAMS_TYPE = {
  REQUEST: 'request',
  RESPONSE: 'response',
};

export {
  TYPE,
  TYPE_NAME,
  CASCADER_DATA_TYPE_OPTIONS,
  DATA_TYPE_ENUM,
  REQUEST_ROOT_TYPE_OPTIONS,
  PARAMS_TYPE,
  ARRAY_SUB_LABEL,
};
