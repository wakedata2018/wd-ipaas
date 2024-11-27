import { BBox } from '@antv/g-base';
import G6, { ComboConfig, IGroup, ShapeOptions, IShape } from '@antv/g6';
import type { ModelConfig } from '@antv/g6-core';
import textUtil from '@/utils/text-utils.js';
import { DEFAULT_HOVER, DEFAULT_SELECTED } from '../constants';
import { getIsDetailGraph } from '../event-bus';

const MARKER = 'combo-marker-shape';
const HEAD = 'head-shape';

function addPoints(group: IGroup, points: number[][], bbox: BBox) {
  if (!points || !points.length) {
    return;
  }
  for (let i = 0; i < points.length; i++) {
    // 0为顶 1为底
    const x = bbox.x + bbox.width * points[i][0];
    const y = bbox.y + bbox.height * points[i][1];

    /// 用来展示锚点状态
    group.addShape('circle', {
      attrs: {
        x,
        y,
        r: 8,
        stroke: 'transparent',
        opacity: 0,
        lineWidth: 8,
      },
      name: 'anchor-point',
      draggable: true,
    });
    group.addShape('circle', {
      attrs: {
        x,
        y,
        r: 4,
        hoverChange: true,
        opacity: 0,
        fill: '#ffffff',
        stroke: '#CDCDCD',
      },
      name: 'anchor-point',
      draggable: true,
    });
  }
}

export function redraw(cfg: ComboConfig, group: IGroup, self: any, isUpdate?: boolean) {
  if (isUpdate) {
    // 锚点 折叠标记
    group
      .findAll(item => item.get('name') === 'anchor-point' || item.get('name') === HEAD || item.get('name') === MARKER)
      .forEach(child => group.removeChild(child));
  }

  const bbox = group.getBBox();
  const x = bbox.x + bbox.width;
  const y = bbox.y + bbox.height;
  const isDetail = getIsDetailGraph();

  // group.addShape('marker', {
  //   attrs: {
  //     fill: '#fff',
  //     stroke: '#CAD4DD',
  //     opacity: 1,
  //     x,
  //     y,
  //     r: 8,
  //     cursor: 'pointer',
  //     symbol: cfg.collapsed ? G6.Marker.expand : G6.Marker.collapse,
  //   },
  //   name: MARKER,
  // });

  // 头部
  group.addShape('rect', {
    attrs: {
      x: bbox.x,
      y: bbox.y,
      width: bbox.width,
      height: 40,
      radius: [4, 4, 0, 0],
      fill: cfg.color,
    },
    name: HEAD,
    draggable: true,
  });
  // 头部标题图标
  if (cfg.image) {
    group.addShape('image', {
      attrs: {
        x: bbox.x + 12,
        y: bbox.y + 12,
        width: 16,
        height: 16,
        img: cfg.image,
      },
      name: HEAD,
      draggable: true,
    });
  }
  // 头部标题
  const title = (cfg?.form as any).name ?? cfg.title;
  if (title) {
    group.addShape('text', {
      attrs: {
        x: bbox.x + 34,
        y: bbox.y + 20,
        textAlign: 'left',
        textBaseline: 'middle',
        text: textUtil.fittingString(title, 140, 14),
        fill: '#fff',
        fontSize: 14,
        tooltipContent: title,
      },
      name: HEAD,
      draggable: true,
    });
  }
  // 头部状态图标
  if (cfg?.stateImage) {
    group?.addShape('image', {
      attrs: {
        x: bbox.x + bbox.width - 32,
        y: bbox.y + 12,
        width: 16,
        height: 16,
        img: cfg.stateImage,
      },
      name: HEAD,
      draggable: true,
    });
  }
  // 内容
  const content = (cfg?.form as any).desc ?? cfg.label;
  if (content && isDetail) {
    group.addShape('text', {
      attrs: {
        x: bbox.x + 20,
        y: bbox.y + 60,
        textAlign: 'left',
        textBaseline: 'middle',
        text: textUtil.fittingString(content, 120, 12),
        fill: '#565758',
        fontSize: 12,
        tooltipContent: content,
      },
      name: HEAD,
      draggable: true,
    });
  }

  addPoints(group, cfg?.inPoints as number[][], bbox);
  addPoints(group, cfg?.outPoints as number[][], bbox);
}

export interface CustomComboOptions {
  name?: string;
  draw?: (cfg?: ModelConfig, group?: IGroup, item?: any) => IShape;
  // draw?: ShapeOptions['draw'];
  setState?: ShapeOptions['setState'];
}

/**
 * 自定义编排组合
 * @param options
 * @returns
 */
export const setCustomCombo = (options?: CustomComboOptions) => {
  const { name = 'customCombo', draw, setState } = options ?? {};
  return {
    init() {
      G6.registerCombo(
        name,
        {
          options: {
            labelCfg: {
              style: {
                opacity: 0,
              },
            },
          },
          drawShape(cfg, group) {
            if (draw) {
              return draw(cfg, group, this);
            }
            const isDetail = getIsDetailGraph();
            const _cfg = cfg as ComboConfig;
            if (cfg?.padding as number[]) {
              _cfg.padding = cfg?.padding as number[];
            } else {
              _cfg.padding = isDetail ? [80, 60, 60, 60] : [70, 60, 60, 60];
            }
            const style = this.getShapeStyle(_cfg);
            // 外边框
            const shape = (group as IGroup).addShape('rect', {
              attrs: {
                ...style,
                stroke: '#CAD4DD',
                fill: '#fff',
                radius: 5,
              },
              name: 'combo-shape',
              draggable: true,
            });

            redraw(_cfg, group as IGroup, this);
            return shape;
          },
          afterUpdate(cfg, combo) {
            const group = combo?.get('group') as IGroup;
            redraw(cfg as ComboConfig, group, this, true);
          },
          // 设置状态
          setState(state, value, item) {
            if (setState) {
              setState(state, value, item);
              return;
            }

            const group = item?.getContainer();
            const groupChildren = group?.get('children');
            if (!groupChildren || groupChildren.length === 0) {
              return;
            }
            const shape = groupChildren[0]; // 顺序根据 draw 时确定
            const children = group?.findAll(g => {
              return g.attrs.parent === shape.attrs.id;
            });
            const circles = group?.findAll(circle => circle.attrs.hoverChange);

            const selectStyles = () => {
              const color = item?.getModel().color;
              shape.attr({ fill: '#f3f9ff', stroke: color, cursor: 'move' });
              children?.forEach(child => {
                child.attr('cursor', 'move');
              });
              circles?.forEach(circle => {
                circle.attr({ fill: '#fff', opacity: 1 });
              });
            };
            const unSelectStyles = () => {
              shape.attr({ fill: '#fff', stroke: '#CAD4DD' });
              circles?.forEach(circle => {
                circle.attr('opacity', 0);
              });
            };

            switch (state) {
              case DEFAULT_SELECTED:
              case DEFAULT_HOVER:
                if (value) {
                  selectStyles();
                } else {
                  unSelectStyles();
                }
                break;
              case 'combo-active':
                if (value) {
                  selectStyles();
                } else {
                  unSelectStyles();
                }
                break;
              default:
                break;
            }
          },
          getAnchorPoints(cfg) {
            return [...((cfg?.inPoints as number[][]) || []), ...((cfg?.outPoints as number[][]) || [])];
          },
        },
        'rect'
      );
    },
  };
};
