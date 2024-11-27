/* eslint-disable camelcase */
import helperParam from './helper/param.js';
import helperNode from './helper/node.js';
import transform_sql_union from './transform-sql-union';
import transform_select_column from './transform-select-column';
import transform_select_row from './transform-select-row';
import transform_groovery_script from './transform-groovery-script';
import transform_connector_api from './transform-connector-api.js';
import crontab from './crontab';
import event_send from './event-send';
import event_receive from './event-receive';
import branch from './branch';
import judge from './judge';
import api from './api';
import start from './start';
import end from './end';
import foreach from './polling';
import create_variable from './create-variable';
import update_variable from './update-variable';
import sql_execute from './sql-execute';
import try_catch from './try-catch';
import { ApiType } from '@/utils/enum/index.js';

const convertMap = {
  transform_sql_union,
  transform_select_column,
  transform_select_row,
  transform_groovery_script,
  crontab,
  event_receive,
  api,
  api_normal_table: api,
  api_external_http: api,
  api_custom_sql: api,
  api_lite_flow: api,
  api_web_service: api,
  api_connector: transform_connector_api,
  event_send,
  branch,
  judge,
  start,
  end,
  create_variable,
  update_variable,
  foreach,
  sql_execute,
  try_catch,
};

export { convertMap };

export default {
  // 把图表里的数据转换成后端接口要的
  nodeToParams(data) {
    const { nodes, edges, combos } = data;
    const paramObj = {}; // 这里的换变成 dataAssetApi.apiAttr.operators 里的数据，后端需要的
    const locations = {
      nodes: [],
      combos: [],
      edges: helperParam.getEdgeInfos(edges),
      count: edges.length,
    };
    nodes.forEach(nodeData => {
      const convertObj = convertMap[nodeData.data.uniqueName]; // 获取转换对象
      if (convertObj) {
        const param = convertObj.toParam(nodeData, edges);
        paramObj[param.operatorId] = param;
      }
      locations.nodes.push(helperParam.getNodeParam(nodeData));
    });
    combos.forEach(combo => {
      if (!combo?.data?.uniqueName) {
        return;
      }
      const convertObj = convertMap[combo.data.uniqueName]; // 获取转换对象
      if (convertObj) {
        const param = convertObj.toParam(combo, edges);
        paramObj[param.operatorId] = param;
      }
      locations.combos.push(helperParam.getComboParam(combo));
    });
    return {
      clazzName: ApiType._lite_flow.clazzName,
      operators: paramObj,
      locationJson: JSON.stringify(locations),
    };
  },
  // 将流程编排数据源转为G6图表里的数据
  paramsToNode(componentOperator, datas) {
    const { locationJson, operators } = componentOperator;
    let nodeInfos = [];
    let edgeInfos = [];
    let comboInfos = [];
    if (locationJson) {
      try {
        const temLocations = JSON.parse(locationJson);
        nodeInfos = temLocations.nodes;
        edgeInfos = temLocations.edges;
        comboInfos = temLocations.combos;
      } catch (e) {
        console.log(e);
      }
    }

    return {
      edges: helperNode.getEdges(edgeInfos),
      nodes: helperNode.getNodes(nodeInfos, operators, datas),
      combos: helperNode.getCombos(comboInfos, operators, datas),
    };
  },
  test(componentOperator, preEdges) {
    const { locations } = componentOperator;
    let edgeInfos = [];
    let count = 0;
    if (locations) {
      try {
        const temLocations = JSON.parse(locations);
        edgeInfos = temLocations.edges;
        count = temLocations.count;
      } catch (e) {
        console.log(e);
      }
    }

    const edges = helperNode.getEdges(edgeInfos);
    if (count !== edges.length || edges.length !== preEdges.length) {
      alert(JSON.stringify(componentOperator));
      alert(JSON.stringify(preEdges));
    }
  },
};
