import { OperationType } from './api-manage-operation';

export const LinkOperationTypeOptions = [
  {
    label: '导入连接器',
    value: OperationType.IMPORT,
  },
  {
    label: '导出连接器',
    value: OperationType.EXPORT,
  },
];

export interface LinkQueryData {
  id: number;
  name: string;
}
