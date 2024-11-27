export enum ActionType {
  Create = 'create',
  CreateChild = 'createChild',
  Edit = 'edit',
}

export const statusType = {
  [ActionType.Create]: '新建分类',
  [ActionType.CreateChild]: '新建子类',
  [ActionType.Edit]: '编辑分类',
};

// 分类信息
export interface CategoryItemInfo {
  parentId?: number;
  sortField?: number;
  groupName?: string;
  parentName?: string;
  description?: string;
  id?: number;
  children?: CategoryItemInfo[];
}
