function returnJsonVal(value) {
  return (value && JSON.parse(value)) || [];
}

/**
 * 将schema转成json
 */
export function schema2json(schema) {
  if (!schema) {
    return {};
  }
  const arrayList = ['array<object>', 'array<integer>', 'array<string>', 'array<boolean>'];

  if (schema.type === 'array') {
    return returnJsonVal(schema.value);
  }

  let json = {};
  for (const fields of Object.values(schema)) {
    const { type, name } = fields;
    const value = fields.value;

    if (arrayList.includes(type)) {
      const newVal = value || null;
      json = {
        ...json,
        [name]: returnJsonVal(newVal),
      };
    }

    if (type === 'string' || type === 'date') {
      const newVal = value || null;
      json = {
        ...json,
        [name]: newVal,
      };
    }

    if (type === 'boolean') {
      let newVal = value || null;
      if (value) {
        newVal = value === 'true' || (value !== 'false' && value) || false;
      }
      json = {
        ...json,
        [name]: newVal,
      };
    }

    /**
     * integer
     */
    if (type === 'integer' || type === 'number' || type === 'long' || type === 'double') {
      let nameVal = null;
      if (value) {
        const num = Number(value);
        nameVal = (isNaN(num) && value) || num;
      }
      json = {
        ...json,
        [name]: nameVal,
      };
    }

    if (type === 'array') {
      try {
        const newVal = (value && JSON.parse(value)) || [];
        json = {
          ...json,
          [name]: newVal,
        };
      } catch (err) {
        console.error(`请检查${name}参数值格式是否正确`);
      }
    }

    /**
     * object
     */
    if (type === 'object') {
      const objItems = schema2json(fields.properties);
      if (Object.keys(objItems).length) {
        json = {
          ...json,
          [name]: schema2json(fields.properties),
        };
      } else {
        json = {
          ...json,
          [name]: {},
        };
      }
    }
  }
  return json;
}
/**
 * 将json转成schema
 */
export function json2schema(json, deep = 0) {
  const tree = {};
  const keys = Object.keys(json);
  for (let i = 0; i < keys.length; i++) {
    const treeKey = `field_${deep}_${i + 1}`;
    const key = keys[i];
    const type = typeof json[key];
    let valueType = null;
    let value = json[key];

    // 区分整数和浮点数
    if (type === 'number') {
      // 整数
      if (json[key] % 1 === 0) {
        valueType = 'integer';
      }
    }

    if (type === 'boolean') {
      value = String(value);
    }

    tree[treeKey] = {
      name: key,
      type: valueType || type,
      value,
    };
    /**
     * array
     */
    if (type === 'object' && Array.isArray(value)) {
      tree[treeKey].type = 'array';
      tree[treeKey].value = JSON.stringify(tree[treeKey].value);
      tree[treeKey].items = {
        ...valIsArray(value[0]),
      };
      if (tree[treeKey].items.type === 'object') {
        delete tree[treeKey].value;
      }
    } else if (type === 'object' && !Array.isArray(value)) {
      // object
      tree[treeKey].value = '';
      tree[treeKey].properties = json2schema(value, deep + 1);
    }
  }
  return tree;
}

/**
 * 类型判断
 */
function valType(val) {
  const type = typeof val || 'string';

  if (type === 'number') {
    // 整数
    if (val % 1 === 0) {
      return 'integer';
    }
  } else if (type === 'object') {
    if (Array.isArray(val)) {
      return 'array';
    }
  }
  return type;
}

function valIsArray(val, deep) {
  console.log(val);
  let tree = {
    name: 'items',
    type: 'string',
  };
  if (val === undefined) {
    return tree;
  }
  const type = valType(val);

  /**
   * string | object
   */

  if (type === 'object') {
    tree = {
      ...tree,
      type: 'object',
      properties: json2schema(val, deep + 1),
    };
  } else if (type === 'array') {
    // array
    tree = {
      ...tree,
      type: 'array',
      items: valIsArray(val[0], deep + 1),
    };
  }

  return tree;
}
