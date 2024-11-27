import { RequestMethod } from '@/utils/enum/index';

export const TABLE_TYPE = {
  HEAD: 'HEAD',
  QUERY: 'QUERY',
};

export const BODY_TYPE = {
  DEFAULT: 'default',
  JSON: 'json',
  XML: 'xml',
};

export const METHOD_COLOR = {
  [RequestMethod.GET]: '#41ca9d',
  [RequestMethod.POST]: '#ed8936',
  [RequestMethod.PUT]: '#1890ff',
  [RequestMethod.DELETE]: '#fa541c',
};
