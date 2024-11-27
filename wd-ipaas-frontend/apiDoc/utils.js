/**
 * 转换jsonSchema格式为请求参数表格格式数据
 */

function jsonSchemaToRequestParams(data) {
  return Object.entries(data?.properties || []).map(item => {
    const required = data.required?.includes(item[0]) || false;
    // 递归查找
    const other = {};
    if (item[1].properties) {
      other.children = jsonSchemaToRequestParams(item[1]);
    }
    if (item[1].items) {
      other.children = jsonSchemaToRequestParams(item[1].items);
    }
    const fieldItem = {
      id: Math.random(),
      assetColumns: item[1]?.name ?? item[0],
      assetDatatype: item[1].items ? `${item[1]?.type}(${item[1].items.type || ''})` : item[1]?.type ?? '',
      required,
      sample: item[1]?.sample || item[1]?.value || '',
      description: item[1]?.description ?? '',
      ...other,
    };
    return fieldItem;
  });
}

export { jsonSchemaToRequestParams };
