import Mock from 'mockjs';

export function MockSetup(params, type) {
  return Mock.mock(`/mock-${type}`, 'get', params);
}

// 查找子节点是否有设置Mock规则，若一个子节点设置了则根节点开启Mock
export const loopFindChildMockRule = field => {
  let find = false;
  if (field.type === 'array') {
    // 数组对象
    if (field.items?.properties) {
      Object.values(field.items?.properties).forEach(i => {
        find = find || loopFindChildMockRule(i);
      });
    } else {
      // 其他数组类型
      find = find || (!!field?.mockRule && field.mockRule !== '');
    }
    return find;
  }

  if (field.type === 'object') {
    if (field?.properties) {
      Object.values(field.properties).forEach(i => {
        find = find || loopFindChildMockRule(i);
      });
    }
    return find;
  }

  return find || (!!field?.mockRule && field.mockRule !== '');
};
