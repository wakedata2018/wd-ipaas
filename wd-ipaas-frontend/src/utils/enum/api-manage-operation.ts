export enum OperationType {
  IMPORT,
  EXPORT,
  EXPORT_DOC,
}

export const OperationTypeOptions = [
  {
    label: '导入接口',
    value: OperationType.IMPORT,
  },
  {
    label: '导出接口',
    value: OperationType.EXPORT,
  },
  {
    label: '导出文档',
    value: OperationType.EXPORT_DOC,
  },
];

export enum InterFaceType {
  SQL,
  DATABASE,
  HTTP,
  ARRANGE,
}

export const InterFaceTypeOptions = [
  {
    label: 'HTTP接口',
    value: InterFaceType.HTTP,
  },
  {
    label: 'SQL接口',
    value: InterFaceType.SQL,
  },
  {
    label: '数据库接口',
    value: InterFaceType.DATABASE,
  },
  {
    label: '编排接口',
    value: InterFaceType.ARRANGE,
  },
];

export enum DocType {
  MD = 'md',
  HTML = 'html',
}

export const DocTypeOptions = [
  {
    label: 'MD',
    value: 'md',
  },
  {
    label: 'HTML',
    value: 'html',
  },
];

export interface InterfaceClassify {
  id: number;
  groupName: string;
}

interface ImportOrExportCommon {
  operationType: OperationType;
  apiType: InterFaceType;
}

export interface ImportData extends ImportOrExportCommon {
  apiGroupId: number;
  uploadFile: string;
}

export interface ExportData extends ImportOrExportCommon {
  apiGroupIds?: number[];
  apiGroupIdList: number[] | number;
  apiTypeList: number[] | number;
}
