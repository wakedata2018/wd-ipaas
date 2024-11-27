import G6,{LayoutConfig } from '@antv/g6';
import { getCurrentEditorContext } from '../context';
import { EventName, getIsDetailGraph } from '../event-bus';

export function onChangeLayout(layoutCfg: LayoutConfig) {
  const { graph, command } = getCurrentEditorContext() ?? {};
  command?.executeCommand(EventName.layout, [{ before: { ...graph?.get('layout') }, after: layoutCfg }]);
}

export function changeVerticalLayout() {
  const isDetail = getIsDetailGraph();

  const config = {
    type: 'dagre',
    rankdir: 'TB', // 可选，默认为图的中心
    nodesep: 100, // 可选
    ranksep: isDetail ? 20 : 10, // 可选
    sortByCombo: true,
    workerEnabled: true, // 可选，开启 web-worker
  }

  onChangeLayout({
    type: 'comboCombined',
    comboPadding:30,
    spacing:10,
    innerLayout:new G6.Layout.dagre(config),
    outerLayout:new G6.Layout.dagre(config)
  });
}

export function changeHorizontalLayout() {
  const isDetail = getIsDetailGraph();
  const config = {
    type: 'dagre',
    rankdir: 'LR', // 可选，默认为图的中心
    nodesep: isDetail ? 30 : 10, // 可选
    ranksep: 20, // 可选
    sortByCombo: true,
    workerEnabled: true, // 可选，开启 web-worker

  }

  onChangeLayout({
    type: 'comboCombined',
    comboPadding:30,
    spacing:10,
    innerLayout:new G6.Layout.dagre(config),
    outerLayout:new G6.Layout.dagre(config)
  });
}

export function changeGridLayout(nodeSize?: number | number[]) {
  onChangeLayout({
    type: 'grid',
    begin: [0, 0], // 可选，
    preventOverlap: true, // 可选，必须配合 nodeSize
    nodeSpacing: 20, // 可选
    comboSpacing: 20,
    nodeSize, // 可选
    condense: false, // 可选
    sortBy: 'degree', // 可选
    workerEnabled: true, // 可选，开启 web-worker
  });
}

export function changeLayout(key: 'vertical' | 'horizontal' | 'grid') {
  switch (key) {
    case 'horizontal':
      changeHorizontalLayout();
      break;
    case 'grid':
      changeGridLayout();
      break;
    default:
      changeVerticalLayout();
      break;
  }
}
