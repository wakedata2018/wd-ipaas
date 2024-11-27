/**
 * 转换响应参数
 */
function getReponseDataList(data, path = 'root') {
  if (!data.length) {
    return [];
  }
  return data.map(item => {
    let newObj = {
      assetColumns: item.assetColumns,
      assetDataType: item.assetDataType,
      description: item.description,
      type: item.type,
      id: path + item.id,
    };

    if (item?.childApiRespParams?.length) {
      newObj = {
        ...newObj,
        children: getReponseDataList(item.childApiRespParams, newObj.id),
      };
    }
    return newObj;
  });
}

/**
 * 数组转为树对象
 */
const loopArrToObj = (arr, obj, root = true) => {
  const required = [];
  const newObj = arr.reduce((pre, cur) => {
    const name = cur.assetColumns;
    if (cur.required) {
      required.push(name);
    }
    const result = {
      ...pre,
      [name]: {
        name: cur.assetColumns,
        type: cur.assetDatatype,
        description: cur.descriptions,
        allowEmpty: cur.allowEmpty,
        value: cur.sample,
      },
    };

    if (cur.children) {
      result[name] = loopArrToObj(cur.children, result[name], false);
    }
    return result;
  }, {});

  if (root) {
    obj.properties = newObj;
  } else {
    delete obj.value;
    obj.items = {
      type: 'object',
      name: 'items',
      properties: newObj,
    };
  }
  if (required.length) {
    obj.required = required;
  }
  return obj;
};

/**
 * 参数转换为树结构
 */
function paramsTransformToTree(data) {
  const tree = {
    root: {
      description: '根层级',
      name: 'root',
      type: 'object',
      properties: {},
    },
  };
  if (!data.length) {
    return tree;
  }
  loopArrToObj(data, tree.root);
  return tree;
}

function returnRequiredKeys(arr) {
  const res = [];
  arr?.forEach(item => {
    if (item.required) {
      res.push(item.assetColumns);
    }
  });
  return res;
}

/**
 * 数据表请求参数和过滤参数组装成jsonSchema
 */
function dataTableParametersToTree(parameters, filters, isUpdateType) {
  const tree = {
    root: {
      description: '根层级',
      name: 'root',
      type: 'object',
      properties: {},
    },
  };
  if (parameters.length) {
    /**
     * 数据表修改操作类型
     */
    if (isUpdateType) {
      tree.root.properties.parameters = {
        name: 'parameters',
        type: 'object',
        description: '请求参数',
        properties: {},
        required: returnRequiredKeys(parameters),
      };
      loopArrToObj(parameters, tree.root.properties.parameters, true);
    } else {
      tree.root.properties.required = returnRequiredKeys(parameters);
      loopArrToObj(parameters, tree.root, true);
    }
  }
  if (filters.length) {
    tree.root.properties.filters = {
      name: 'filters',
      type: 'object',
      description: '过滤条件',
      properties: {},
      required: returnRequiredKeys(filters),
    };
    loopArrToObj(filters, tree.root.properties.filters, true);
  }
  return tree;
}

/**
 * 数据表查询操作类型响应参数体构造
 */
function dataTableResponseTree(baseParam, params) {
  // 构造共参数层
  const resultTree = paramsTransformToTree(baseParam);
  // 构造data层
  const findData = Object.entries(resultTree.root.properties).find(item => item[1].name === 'data');
  delete resultTree.root.properties[findData[0]].value;

  loopArrToObj(params, resultTree.root.properties[findData[0]], false);

  return resultTree;
}

/**
 * 树结构转换为参数
 */
function treeTransformToParams(data, requiredList) {
  const loop = arr => {
    return arr.map(item => {
      const required = !!requiredList.includes(item[0]);
      let response = {
        assetColumns: item[1].name,
        assetDatatype: item[1].type,
        allowEmpty: item[1].allowEmpty,
        required,
        sample: item[1].value,
        descriptions: item[1].description,
      };
      const child = item[1].properties || (item[1].items && item[1].items.properties);
      if (child) {
        response = {
          ...response,
          children: loop(Object.entries(child)),
        };
      }
      return response;
    });
  };
  return loop(data);
}

// 对比新旧值 保留部分参数
function handleFilter(_old, _new) {
  if (!_old) {
    return _new;
  }
  const _oldObj = {};
  const _newObj = {};
  _old?.forEach(item => {
    _oldObj[item.assetColumns] = item;
  });
  _new?.forEach(item => {
    _newObj[item.assetColumns] = item;
  });

  const res = _new.map(item => {
    const _assetColumns = item.assetColumns;
    if (_assetColumns === 'data') {
      return {
        ..._newObj[_assetColumns],
        children: handleFilter(_oldObj[_assetColumns]?.children, _newObj[_assetColumns]?.children),
      };
    } else {
      // 只保留描述和示例值
      const { descriptions, sample, fixedValue, expression, paramValueType } =
        _oldObj[_assetColumns] || _newObj[_assetColumns];

      return {
        ..._newObj[_assetColumns],
        descriptions,
        sample,
        fixedValue,
        expression,
        paramValueType,
      };
    }
  });
  return res;
}

export {
  getReponseDataList,
  paramsTransformToTree,
  treeTransformToParams,
  dataTableParametersToTree,
  dataTableResponseTree,
  handleFilter,
};
