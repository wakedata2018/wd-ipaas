/* eslint-disable */
export enum DATA_TYPE {
  STRING = 'string',
  INT = 'integer',
  DOUBLE = 'double',
  BOOLEAN = 'boolean',
  LONG = 'integer',
  ARRAY_STRING = 'array[string]',
  ARRAY_INT = 'array[integer]',
  ARRAY_DOUBLE = 'array[double]',
  ARRAY_BOOLEAN = 'array[boolean]',
  ARRAY_LONG = 'array[integer]',
}

export const MockRuleOptions = [
  /** Basic */
  {
    label: 'Basic',
    options: [
      {
        label: '@integer',
        value: '@integer(60,9999)',
        desc: '整数',
        type: 'integer',
      },
      {
        label: '@string',
        value: '@string',
        desc: '字符串',
        type: 'string',
      },
      {
        label: '@float',
        value: '@float(100, 9999, 1, 2)',
        desc: '浮点数',
        type: 'double',
      },
      {
        label: '@character',
        value: '@character',
        desc: '单字符',
        type: 'string',
      },
      {
        label: '@boolean',
        value: '@boolean',
        desc: '布尔',
        type: 'boolean',
      },
      {
        label: '@boolean(true)',
        value: '@boolean(1, 0, true)',
        desc: '布尔',
        type: 'boolean',
      },
      {
        label: '@boolean(false)',
        value: '@boolean(0, 1, true)',
        desc: '布尔',
        type: 'boolean',
      },
      {
        label: '@datetime',
        value: '@datetime',
        desc: '日期时间',
        type: 'string',
      },
      {
        label: '@date',
        value: '@date',
        desc: '日期',
        type: 'string',
      },
      {
        label: '@time',
        value: '@time',
        desc: '时间',
        type: 'string',
      },
      {
        label: '@date("yyyy-MM-dd")',
        value: '@date("yyyy-MM-dd")',
        desc: '日期',
        type: 'string',
      },
      {
        label: '@datetime("yyyy-MM-dd A HH:mm:ss")',
        value: '@datetime("yyyy-MM-dd A HH:mm:ss")',
        desc: '日期时间',
        type: 'string',
      },
    ],
  },
  /** Image */
  {
    label: 'Image',
    options: [
      {
        label: '@image',
        value: '@image',
        desc: '图片链接',
        type: 'string',
      },
      {
        label: '@dataImage',
        value: '@dataImage',
        desc: '图片data',
        type: 'string',
      },
    ],
  },
  /** Color */
  {
    label: 'Color',
    options: [
      {
        label: '@color',
        value: '@color',
        desc: '颜色',
        type: 'string',
      },
      {
        label: '@hex',
        value: '@hex',
        desc: '颜色hex',
        type: 'string',
      },
      {
        label: '@rgba',
        value: '@rgba',
        desc: '颜色rgba',
        type: 'string',
      },
      {
        label: '@rgb',
        value: '@rgb',
        desc: '颜色rgb',
        type: 'string',
      },
      {
        label: '@hsl',
        value: '@hsl',
        desc: '颜色hsl',
        type: 'string',
      },
    ],
  },
  /** Text */
  {
    label: 'Text',
    options: [
      {
        label: '@ctitle',
        value: '@ctitle',
        desc: '中文标题',
        type: 'string',
      },
      {
        label: '@cword',
        value: '@cword',
        desc: '中文词组',
        type: 'string',
      },
      {
        label: '@cparagraph',
        value: '@cparagraph',
        desc: '中文大段文本',
        type: 'string',
      },
      {
        label: '@csentence',
        value: '@csentence',
        desc: '中文句子',
        type: 'string',
      },
      {
        label: '@cname',
        value: '@cname',
        desc: '中文姓名',
        type: 'string',
      },
      {
        label: '@cfirst',
        value: '@cfirst',
        desc: '中文姓',
        type: 'string',
      },
      {
        label: '@clast',
        value: '@clast',
        desc: '中文名',
        type: 'string',
      },
      {
        label: '@paragraph',
        value: '@paragraph',
        desc: '大段文本',
        type: 'string',
      },
      {
        label: '@sentence',
        value: '@sentence',
        desc: '句子',
        type: 'string',
      },
      {
        label: '@word',
        value: '@word',
        desc: '单词',
        type: 'string',
      },
      {
        label: '@title',
        value: '@title',
        desc: '标题',
        type: 'string',
      },
    ],
  },
  /** Name */
  {
    label: 'Name',
    options: [
      {
        label: '@name',
        value: '@name',
        desc: '姓名',
        type: 'string',
      },
      {
        label: '@first',
        value: '@first',
        desc: '英文人名',
        type: 'string',
      },
      {
        label: '@last',
        value: '@last',
        desc: '英文姓',
        type: 'string',
      },
    ],
  },
  /** Web */
  {
    label: 'Web',
    options: [
      {
        label: '@url("http")',
        value: '@url("http")',
        desc: '网址',
        type: 'string',
      },
      {
        label: '@email',
        value: '@email',
        desc: 'email',
        type: 'string',
      },
      {
        label: '@domain',
        value: '@domain',
        desc: '域名',
        type: 'string',
      },
      {
        label: '@domain("com")',
        value: '@domain("com")',
        desc: '域名',
        type: 'string',
      },
    ],
  },
  /** Address */
  {
    label: 'Address',
    options: [
      {
        label: '@region',
        value: '@region',
        desc: '区域',
        type: 'string',
      },
      {
        label: '@province',
        value: '@province',
        desc: '省份',
        type: 'string',
      },
      {
        label: '@city',
        value: '@city',
        desc: '城市',
        type: 'string',
      },
      {
        label: '@city(true)',
        value: '@city(true)',
        desc: '城市（含省）',
        type: 'string',
      },
      {
        label: '@zip',
        value: '@zip',
        desc: '邮编',
        type: 'string',
      },
    ],
  },
  /** Helper */
  {
    label: 'Helper',
    options: [
      {
        label: '@upper("hello")',
        value: '@upper("hello")',
        desc: '转为大写',
        type: 'string',
      },
      {
        label: '@lower("HELLO")',
        value: '@lower("HELLO")',
        desc: '转为小写',
        type: 'string',
      },
      {
        label: '@protocol',
        value: '@protocol',
        desc: '协议',
        type: 'string',
      },
    ],
  },
  /** Miscellaneous */
  {
    label: 'Miscellaneous',
    options: [
      {
        label: '@id',
        value: '@id',
        desc: '数字ID',
        type: 'integer',
      },
      {
        label: '@increment',
        value: '@increment',
        desc: '自增ID',
        type: 'integer',
      },
      {
        label: '@guid',
        value: '@guid',
        desc: 'GUID',
        type: 'string',
      },
    ],
  },
];

/** 根据数据类型过滤Mock选项 */
export const filterMockType = (type: DATA_TYPE) => {
  switch (type) {
    case DATA_TYPE.STRING:
    case DATA_TYPE.INT:
    case DATA_TYPE.DOUBLE:
    case DATA_TYPE.BOOLEAN:
    case DATA_TYPE.LONG:
    case DATA_TYPE.ARRAY_STRING:
    case DATA_TYPE.ARRAY_INT:
    case DATA_TYPE.ARRAY_DOUBLE:
    case DATA_TYPE.ARRAY_BOOLEAN:
    case DATA_TYPE.ARRAY_LONG:
      return MockRuleOptions.map(item => {
        return {
          label: item.label,
          options: item.options.filter(i => {
            return type.includes(i.type);
          }),
        };
      });
    default:
      return MockRuleOptions;
  }
};
/* eslint-enable */
