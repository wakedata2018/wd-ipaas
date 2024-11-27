import Vue from 'vue';

let isDropedGraph = false;

// 繁 简切换
let isDetailGraph = false;

function setIsDropGraph(isDroped = false) {
  isDropedGraph = isDroped;
}

function getIsDropGraph() {
  return isDropedGraph;
}

function triggerIsDetail() {
  isDetailGraph = !isDetailGraph;
}

function getIsDetailGraph() {
  return isDetailGraph;
}

const EventName = {
  addItem: 'addItem',
  contextmenuClick: 'contextmenuClick',
  mousedown: 'mousedown',
  canvasClick: 'canvasClick',
  updateItem: 'updateItem',
  nodeselectchange: 'nodeselectchange',
  deleteItem: 'deleteItem',
  muliteSelectEnd: 'muliteSelectEnd',
  afterAddEdge: 'afterAddEdge',
  afterAddNode: 'afterAddNode',
  afterAddMultiCombo: 'afterAddMultiCombo',
  add: 'add',
  update: 'update',
  delete: 'delete',
  layout: 'layout',
  beforelayout: 'beforelayout',
  afterlayout: 'afterlayout',
  clearStack: 'clearStack',
  removeLastStack: 'removeLastStack',
  queryTab: 'queryTab',
  nodeEdit: 'nodeEdit',
  nodeRename: 'nodeRename',
  nodeViewDetail: 'nodeViewDetail',
  refreshData: 'refreshData',
  showTestResult: 'showTestResult',
  saveApi: 'onSave',
  showNodeDialog: 'dblclicknode',
  showEdgeDialog: 'dblclickedge',
  showComboDialog: 'dblclickcombo',
  /**
   * 顶栏事件
   */
  toolbarCommand: {
    toolbarCommand: 'toolbarCommand',
    test: 'test',
    grant: 'grant',
    recover: 'recover',
  },

  changeG6Size: 'changeG6Size',
  returnToOriginalMode: 'returnToOriginalMode',
  showOrHideTooltip: 'showOrHideTooltip',
  fitViewAutomatically: 'fitViewAutomatically',
  updateTaskInfo: 'updateTaskInfo',
};

export { EventName, setIsDropGraph, getIsDropGraph, triggerIsDetail, getIsDetailGraph };

export default new Vue();
