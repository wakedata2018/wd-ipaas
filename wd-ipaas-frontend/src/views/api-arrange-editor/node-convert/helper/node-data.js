/**
 * 节点的数据属性配置
 */
/**
 * node节点信息
{
  id,
  label,
  size: '80*84',
  type: 'node',
  x: 0,
  y: 0,
  shape: 'customNode', // 节点形状
  color: '#1890ff',  // 装饰颜色
  image: 'https://gw.alipayobjects.com/zos/rmsportal/czNEJAmyDpclFaSucYWB.svg', // 装饰图片
  inPoints, // 连线入点
  outPoints // 连线出点，
  data, // 节点的描述信息，附加身后端传输的部分数据
  cmp, // 当前组件信息(Kafka输入算子，是连接数据源信息，输出算子，也是连接数据源信息)
  form, //节点可修改信息，表单信息
 }
 *
 */
import taskComponentAPI from '@/api/task-component.js';
import { EdgeType } from '@/utils/enum/index.js';
import warnImg from '@/assets/images/task/warn.png';
import startImg from '@/assets/images/task/editor/start.svg';
import endImg from '@/assets/images/task/editor/end.svg';
import networkImg from '@/assets/images/task/editor/network.svg';
import sourceImg from '@/assets/images/task/editor/source.svg';
import sqlImg from '@/assets/images/task/editor/sql.svg';
import tableImg from '@/assets/images/task/editor/table.svg';
import functionImg from '@/assets/images/task/editor/function.svg';
import defaultImg from '@/assets/images/task/editor/default.svg';
import judgeImg from '@/assets/images/task/editor/judge.svg';
import timingImg from '@/assets/images/task/editor/timing.svg';
import eventImg from '@/assets/images/task/editor/event.svg';
import pollingImg from '@/assets/images/task/editor/polling.svg';
import tryCatchImg from '@/assets/images/task/editor/try-catch.svg';
import { convertMap } from '..';

// 初始化算子 图标基础信息
const extraInfoMap = {
  start: { color: '#2776fb', img: startImg },
  end: { color: '#2776fb', img: endImg },
  api_normal_table: { color: '#2776fb', img: tableImg },
  api_external_http: { color: '#FF7F47', img: networkImg },
  api_web_service: { color: '#FF7F47', img: networkImg },
  api_custom_sql: { color: '#2776fb', img: sourceImg },
  api_lite_flow: { color: '#7A9BBD', img: sqlImg },
  transform_sql_union: { color: '#7A9BBD', img: sqlImg },
  transform_flow_switch: { color: '#7A9BBD', img: tableImg },
  transform_groovery_script: { color: '#7A9BBD', img: functionImg },
  transform_select_column: { color: '#7A9BBD', img: tableImg },
  transform_select_row: { color: '#7A9BBD', img: tableImg },
  branch: { color: '#2776fb', img: judgeImg },
  judge: { color: '#2776fb', img: judgeImg },
  crontab: { color: '#2776fb', img: timingImg },
  event_send: { color: '#2776fb', img: eventImg },
  event_receive: { color: '#2776fb', img: eventImg },
  // api: { color: '#2776fb', img: sqlImg },
  default: { color: '#2776fb', img: defaultImg },
  api_connector: { color: '#FF7F47', img: networkImg },
  foreach: { color: '#2776fb', img: pollingImg },
  sql_execute: { color: '#2776fb', img: sqlImg },
  try_catch: { color: '#2776fb', img: tryCatchImg },
};

export const NODE_TYPE_MAP = {
  branch: 'branchNode',
  judge: 'judgeNode',
  foreach: 'pollingCombo',
  try_catch: 'abnormalCombo',
};

export const NODE_ITEM_TYPE_MAP = {
  foreach: 'combo',
  try_catch: 'multiCombo',
};

export const NODE_SIZE_MAP = {
  foreach: [260, 100],
  try_catch: [500, 500],
};

const liteFlowApiData = {
  className: 'com.wakedata.dw.open.model.api.flow.operator.api.ApiOperator',
  defaultName: '编排API',
  desc: '编排API',
  layer: 'API',
  typeOfEdge: ['multiOutput', 'withoutInput'],
  uniqueName: 'api_lite_flow',
};

let cmpPromise = null;

// 节点基本信息
function getBasicNode(config, uniqueName, type) {
  const extraInfo = extraInfoMap[uniqueName] || extraInfoMap.default;
  const node = Object.assign(
    {
      size: [200, 100],
      itemType: 'node',
      x: 0,
      y: 0,
      type,
      color: extraInfo.color, // 初始化算子颜色
      image: extraInfo.img, // 图片
      stateImage: warnImg, // 初始化算子图标
      children: [],
    },
    config
  );
  return node;
}

function format(list) {
  const map = {};
  let groupIndex = 1;
  let index = 1;
  list.forEach(item => {
    const layer = item.layer;
    const uniqueName = item.uniqueName;
    const extraInfo = extraInfoMap[uniqueName];
    const layerNames = layer.split('/');
    let layerChildren = [];
    let layerLevel = 1;
    for (const layerName of layerNames) {
      const newItem = {
        label: layerName,
        class: 'category',
        id: `layer-${groupIndex}`,
        color: extraInfo ? extraInfo.color : undefined,
        // image: extraInfo ? extraInfo.img : undefined,
        children: [],
      };
      if (layerLevel === 1) {
        if (!map[layerName]) {
          // 分类
          map[layerName] = newItem;
          groupIndex++;
        }
        layerChildren = map[layerName].children;
      } else {
        let child = layerChildren.find(layerChildrenItem => layerChildrenItem.label === layerName);
        if (!child) {
          child = newItem;
          layerChildren.push(child);
          groupIndex++;
        }
        layerChildren = child.children;
      }
      if (layerLevel === layerNames.length) {
        // 获取输入输出边
        toNode(layerChildren, item, `${groupIndex}_${index}`);
      }
      layerLevel++;
      index++;
    }
    // if (!map[layer]) {
    //   // 分类
    //   map[layer] = {
    //     label: layer,
    //     class: 'category',
    //     id: `layer-${groupIndex}`,
    //     color: !!extraInfo ? extraInfo.color : undefined,
    //     image: !!extraInfo ? extraInfo.img : undefined,
    //     children: []
    //   };
    //   groupIndex++;
    // }

    // // 获取输入输出边
    // toNode(map[layer].children, item, `${groupIndex}_${index}`);
    // index++;
  });

  const nodes = [];
  for (const key in map) {
    nodes.push(map[key]);
  }
  return nodes;
}
/**
 * otherConfigs: 用来铺在第一层的属性值
 */
function createNode(label, data, inPoints, outPoints, id, cmp, extra, otherConfigs) {
  const { uniqueName } = data;
  const convertObj = convertMap[uniqueName];
  const obj = {
    ...getBasicNode(
      {
        id,
        label,
        title: data.defaultName,
        inPoints,
        outPoints,
        extra,
        itemType: NODE_ITEM_TYPE_MAP[uniqueName] || 'node',
        size: NODE_SIZE_MAP[uniqueName] || [200, 100],
        ...otherConfigs,
      },
      uniqueName,
      NODE_TYPE_MAP[uniqueName] || 'customNode'
    ),
    data,
    cmp,
  };

  if (convertObj) {
    obj.form = convertObj.initForm(data, cmp);
  }
  return obj;
}

function toNode(list, item, key) {
  try {
    const { typeOfEdge, uniqueName } = item;
    const { inPoints, outPoints } = EdgeType.getPoints(typeOfEdge);
    const extraInfo = extraInfoMap[uniqueName];
    if (item.components) {
      const subGroup = {
        id: key,
        label: item.defaultName,
        color: extraInfo ? extraInfo.color : undefined,
        image: extraInfo ? extraInfo.img : undefined,
      };

      subGroup.children = Object.keys(item.components).map((cmpName, index) => {
        const cmp = item.components[cmpName];

        const extra = {};
        // if (uniqueName === 'sink') {
        //   extra.desc = cmp.sinkChannelType;
        // }
        let label = cmp.defaultName;
        /**
         * 为数组时，是数据源列表
         */
        if (cmp instanceof Array) {
          label = cmpName;
          return {
            id: key + '_' + index,
            isDatasource: true,
            dbType: label,
            label,
            color: subGroup.color,
            image: subGroup.image,
            children: cmp.map((_item, _index) => {
              /**
               * item：数据源的信息
               * _item：impl返回的节点的信息，包含节点类型名等
               */
              return createNode(
                _item.connectionName,
                item,
                inPoints,
                outPoints,
                `${key}_${index}_${_index}`,
                {
                  dataSource: _item,
                },
                extra
              );
            }),
          };
        } else {
          cmp.clazzName = item.className;
          return createNode(label, item, inPoints, outPoints, `${key}_${index}`, cmp, extra);
        }
      });

      list.push(subGroup);
    } else {
      list.push(createNode(item.defaultName, item, inPoints, outPoints, key));
    }
  } catch (e) {
    console.log(e);
  }
}

export { extraInfoMap };

export default {
  destroy() {
    cmpPromise = null;
  },
  fetchNodes() {
    if (cmpPromise) {
      return cmpPromise;
    }
    cmpPromise = new Promise((resolve, rejects) => {
      taskComponentAPI.list().done(
        res => {
          resolve(res.data || []);
          cmpPromise = null;
        },
        res => {
          // eslint-disable-next-line prefer-promise-reject-errors
          rejects([]);
          cmpPromise = null;
        }
      );
    });
    return cmpPromise;
  },

  fetchTreeNode() {
    return this.fetchNodes().done(res => {
      return format(res);
    });
  },
  /**
   *
   * @param {*} apiList API列表
   * @param {*} list 所有算子列表信息 用于初始化拖入算子的图形属性（主要是算子类型和出入边信息）
   * @returns
   */
  toApiNode(apiList, list) {
    try {
      // if (!data) {
      //   return [];
      // }

      const nodes = [];
      apiList.forEach(apiObj => {
        const { apiType } = apiObj;
        const uniqueName = `api_${apiType.toLowerCase()}`;
        let data = list.find(item => item.uniqueName === uniqueName);
        if (!data) {
          data = Object.assign({}, liteFlowApiData);
        }
        const { inPoints, outPoints } = EdgeType.getPoints(data.typeOfEdge);
        // if (!source) {
        //   return;
        // }
        // 初始化算子图形标题、颜色等

        const node = getBasicNode(
          {
            id: apiObj.dataAssetApiId,
            label: apiObj.apiName,
            title: data.defaultName,
            inPoints,
            outPoints,
          },
          uniqueName,
          NODE_TYPE_MAP[uniqueName] || 'customNode'
        );
        node.data = data;
        const cmp = {
          // ...source,
          apiObj,
        };
        /**
         * 列表中获取的算子数据
         */
        node.cmp = cmp;
        /**
         * 初始化提交的数据结构
         */
        node.form = convertMap.api.setForm(data, cmp);
        nodes.push(node);
      });

      return nodes;
    } catch (e) {
      console.log(e);
    }
  },
  toConnectorNode(item, basic) {
    // 这里把连接器数据转换成g6 需要的
    const uniqueName = `api_connector`;
    const data = Object.assign({}, item, {
      uniqueName,
      className: 'com.wakedata.dw.open.model.api.flow.operator.connector.ConnectorOperator',
    });
    const { inPoints, outPoints } = EdgeType.getPoints(basic.typeOfEdge);
    const node = getBasicNode(
      {
        id: data.id,
        connectorId: data.connectorId,
        label: data.apiName,
        title: basic.defaultName,
        inPoints,
        outPoints,
      },
      uniqueName,
      NODE_TYPE_MAP[uniqueName] || 'customNode'
    );
    // 节点图形基础信息
    node.data = Object.assign({}, basic, {
      connectorApiAuthType: data.connectorApiAuthType,
    });
    // 节点数据信息
    const cmp = {
      item,
    };
    node.cmp = cmp;
    // 初始化节点表单信息
    node.form = convertMap[uniqueName].setForm(data, cmp); // 最后保存的时候数据从这里来
    return node;
  },
};
