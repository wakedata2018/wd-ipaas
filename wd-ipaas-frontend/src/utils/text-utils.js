import { ModeEnum } from './enum';
const calcStrLen = function (str) {
  let len = 0;

  for (let i = 0; i < str.length; i++) {
    if (str.charCodeAt(i) > 0 && str.charCodeAt(i) < 128) {
      len++;
    } else {
      len += 2;
    }
  }

  return len;
};
const fittingString = function (str, maxWidth, fontSize, maxLineCount = 1) {
  const fontWidth = fontSize * 1.3; // 字号+边距

  const nowMaxWidth = maxWidth * 2; // 需要根据自己项目调整

  // var width = calcStrLen(str) * fontWidth;
  const ellipsis = '…';
  let result = '';
  let restStr = str;
  for (let nowLine = 0; nowLine < maxLineCount; nowLine++) {
    const restWidth = calcStrLen(restStr) * fontWidth;
    if (restWidth > nowMaxWidth) {
      const actualLen = Math.floor((nowMaxWidth - 10) / fontWidth);
      result += restStr.substring(0, actualLen);
      if (nowLine === maxLineCount - 1) {
        result += ellipsis;
      } else {
        result += '\n';
        restStr = restStr.substring(actualLen);
      }
    } else {
      result += restStr;
      break;
    }
  }
  return result;
};

/**
 * 随机生产指定长度的字符串
 * @param {指定长度} len
 * @returns
 */
function randomString(len) {
  len = len || 32;
  const $chars =
    'ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678'; /** **默认去掉了容易混淆的字符oOLl,9gq,Vv,Uu,I1****/
  const maxPos = $chars.length;
  let pwd = '';
  for (let i = 0; i < len; i++) {
    pwd += $chars.charAt(Math.floor(Math.random() * maxPos));
  }
  return pwd;
}
/**
 * query参数拼接到url上
 * @param {请求地址} url
 * @param {query参数} query
 * @returns
 */
function handlerQuery(url, query) {
  if (query) {
    const queryArr = [];
    for (const key in query) {
      queryArr.push(`${key}=${query[key]}`);
    }
    if (url.indexOf('?') !== -1) {
      url = `${url}&${queryArr.join('&')}`;
    } else {
      url = `${url}?${queryArr.join('&')}`;
    }
  }
  if (url.slice(-1) === '&') {
    url = url.substring(0, url.length - 1);
  }
  return url;
}

function formatXmlStr(text) {
  // 去掉多余的空格
  text =
    '\n' +
    text
      .replace(/(<\w+)(\s.*?>)/g, function ($0, name, props) {
        return name + ' ' + props.replace(/\s+(\w+=)/g, ' $1');
      })
      .replace(/>\s*?</g, '>\n<');

  // 把注释编码
  text = text
    .replace(/\n/g, '\r')
    .replace(/<!--(.+?)-->/g, function ($0, str) {
      const ret = '<!--' + escape(str) + '-->';
      // alert(ret);
      return ret;
    })
    .replace(/\r/g, '\n');

  // 调整格式
  // const rgx = /\n(<(([^\?]).+?)(?:\s|\s*?>|\s*?(\/)>)(?:.*?(?:(?:(\/)>)|(?:<(\/)\2>)))?)/gm;
  const rgx = /\n(<(([^?]).+?)(?:\s|\s*?>|\s*?(\/)>)(?:.*?(?:(?:(\/)>)|(?:<(\/)\2>)))?)/gm;
  const nodeStack = [];
  const output = text.replace(rgx, function ($0, all, name, isBegin, isCloseFull1, isCloseFull2, isFull1, isFull2) {
    const isClosed = isCloseFull1 === '/' || isCloseFull2 === '/' || isFull1 === '/' || isFull2 === '/';
    // alert([all,isClosed].join('='));
    let prefix = '';
    if (isBegin === '!') {
      prefix = getPrefix(nodeStack.length);
    } else {
      if (isBegin !== '/') {
        prefix = getPrefix(nodeStack.length);
        if (!isClosed) {
          nodeStack.push(name);
        }
      } else {
        nodeStack.pop();
        prefix = getPrefix(nodeStack.length);
      }
    }
    const ret = '\n' + prefix + all;
    return ret;
  });

  // const prefixSpace = -1;
  let outputText = output.substring(1);
  // alert(outputText);

  // 把注释还原并解码，调格式
  outputText = outputText.replace(/\n/g, '\r').replace(/(\s*)<!--(.+?)-->/g, function ($0, prefix, str) {
    // alert(['[',prefix,']=',prefix.length].join(''));
    if (prefix.charAt(0) === '\r') prefix = prefix.substring(1);
    str = unescape(str).replace(/\r/g, '\n');
    const ret = '\n' + prefix + '<!--' + str.replace(/^\s*/gm, prefix) + '-->';
    // alert(ret);
    return ret;
  });

  return outputText.replace(/\s+$/g, '').replace(/\r/g, '\r\n');
}

function getPrefix(prefixIndex) {
  const span = '    ';
  const output = [];
  for (let i = 0; i < prefixIndex; ++i) {
    output.push(span);
  }

  return output.join('');
}

/**
 * schema树转json数组
 * @param {*} tree
 * @returns
 */
function tree2json(tree, child = true) {
  const array = [];
  const keys = Object.keys(tree);
  for (let i = 0; i < keys.length; i++) {
    const key = keys[i];
    const item = tree[key];
    const obj = {
      // assetColumns: key,
      assetColumns: item.name,
      assetDataType: item.type,
      description: item.description,
      type: 'BODY',
      paramValueType: item.paramValueType,
      businessType: 'LITEFLOW_RESULT',
      resultColumnType: item.type,
      responsePostData: item.responsePostData,
    };
    if (item.paramValueType === ModeEnum.reference || item.paramValueType === ModeEnum.method) {
      obj.expression = item.expression;
    } else {
      obj.defaultValue = item.defaultValue;
    }
    if (item.properties && child) {
      obj.childApiRespParams = tree2json(item.properties);
    }
    array.push(obj);
  }
  return array;
}

/**
 * json数组转schema树
 * @param {*} list
 * @param {*} deep
 * @returns
 */
function json2tree(list, deep = 0) {
  const tree = {};
  if (!list) return {};
  for (let i = 0; i < list.length; i++) {
    const treeKey = `field_${deep}_${i + 1}`;
    const obj = list[i];
    tree[treeKey] = {
      name: obj.assetColumns,
      type: obj.resultColumnType,
      paramValueType: obj.paramValueType,
      defaultValue: obj.defaultValue,
      expression: obj.expression,
      description: obj.description,
    };
    if (obj.childApiRespParams && obj.childApiRespParams.length > 0) {
      tree[treeKey].properties = json2tree(obj.childApiRespParams, deep + 1);
    }
  }
  return tree;
}

/**
 * 判断树中是否包含key为空的
 * @param {*} params
 */
function hasEmptyOrMultiName(tree) {
  if (!tree || !Object.keys(tree).length) {
    return { success: true };
  }
  const fields = [];
  const keys = Object.keys(tree);
  for (let i = 0; i < keys.length; i++) {
    const obj = tree[keys[i]];
    if (!obj.name) {
      return {
        success: false,
        message: '不能为空',
      };
    }
    if (fields.indexOf(obj.name) !== -1) {
      return {
        success: false,
        message: '不能重复',
      };
    }
    fields.push(obj.name);
    if (obj.properties) {
      const res = hasEmptyOrMultiName(obj.properties);
      if (!res.success) {
        return res;
      }
    }
    if (obj.items) {
      const res = hasEmptyOrMultiName(obj.items?.properties);
      if (!res.success) {
        return res;
      }
    }
  }
  return { success: true };
}

/**
 * 根据json-schema树获取参数映射值
 * @param {从properties开始取值} tree
 */
function tree2mapValue(tree, deep = 0) {
  const obj = {};
  const keys = Object.keys(tree);
  for (let i = 0; i < keys.length; i++) {
    const key = keys[i];
    const item = tree[key];
    if (item.type === 'object') {
      obj[item.name] = {
        dataType: item.type,
        operatorId: null,
        type: item.paramValueType,
        value: tree2mapValue(item.properties),
      };
    } else {
      if (item.paramValueType && (item.expression || item.defaultValue)) {
        obj[item.name] = {
          dataType: item.type,
          type: item.paramValueType,
          operatorId: item.operatorId || 'start',
          value:
            item.paramValueType === ModeEnum.reference || item.paramValueType === ModeEnum.method
              ? item.expression
              : item.defaultValue,
        };
      }
    }
  }
  return obj;
}

export default {
  calcStrLen,
  fittingString,
  randomString,
  handlerQuery,
  formatXmlStr,
  tree2json,
  json2tree,
  hasEmptyOrMultiName,
  tree2mapValue,
};
