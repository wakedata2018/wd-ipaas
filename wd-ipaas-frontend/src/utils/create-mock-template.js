export function createMockRequestTemp(data) {
  const source = Array.isArray(data) ? data : Object.values(data);
  return source.reduce((pre, cur) => {
    const name = cur?.name ?? cur.assetColumns;
    const rule = cur?.mockRule ?? '';
    const type = cur?.assetDatatype ?? cur.type;

    if (type === 'object' && cur.properties) {
      return {
        ...pre,
        [name]: createMockRequestTemp(cur.properties),
      };
    }

    if (type === 'array') {
      // 数组对象
      if (cur.items?.properties) {
        return {
          ...pre,
          [name]: [createMockRequestTemp(cur.items.properties)],
        };
      }
      // 数组嵌套数组
      if (cur.items?.items) {
        return {
          ...pre,
          [name]: [createMockRequestTemp(cur.items.items)],
        };
      }

      // 数组字符串 数组boolean 数组integer
      const result = [];
      const itemsType = cur.items.type;

      // 根据参数类型使用对应mock数据
      result.push(rule);

      return {
        ...pre,
        [name]: result,
      };
    }

    return {
      ...pre,
      [name]: rule,
    };
  }, {});
}
