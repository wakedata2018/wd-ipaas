import { v1 as uniqueId } from 'uuid';
import { BBox } from '@antv/g-base';
import G6, { IGroup, ShapeOptions } from '@antv/g6';
import textUtil from '@/utils/text-utils.js';
import { DEFAULT_HOVER, DEFAULT_SELECTED } from '../constants';
import { getIsDetailGraph } from '../event-bus';

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
        r: 4,
        stroke: 'transparent',
        opacity: 0,
        lineWidth: 4,
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
        fill: '#ffffff',
        stroke: '#CDCDCD',
        opacity: 0,
      },
      name: 'anchor-point',
      draggable: true,
    });
  }
}

export interface CustomNodeOptions {
  name?: string;
  size?: number[];

  draw?: ShapeOptions['draw'];
  setState?: ShapeOptions['setState'];
}

/**
 * 自定义编排节点
 * @param options
 * @returns
 */
export const setCustomNode = (options?: CustomNodeOptions) => {
  const { name = 'customNode', size = [170, 34], draw, setState } = options ?? {};
  return {
    init() {
      G6.registerNode(name, {
        draw(cfg, group) {
          if (draw) {
            return draw(cfg, group);
          }

          const isDetail = getIsDetailGraph();
          const _size = (cfg?.size as number[]) || size;
          // 此处必须是NUMBER 不然bbox不正常
          const width = Number(_size[0]);
          const height = Number(_size[1]);
          // 此处必须有偏移 不然drag-node错位
          const offsetX = -width / 2;
          const offsetY = -height / 2;
          const mainId = 'rect' + uniqueId();
          // 外边框
          const shape = group!.addShape('rect', {
            attrs: {
              id: mainId,
              x: offsetX,
              y: offsetY,
              width,
              height: isDetail ? height : 40,
              stroke: '#CAD4DD',
              fill: '#fff', // 此处必须有fill 不然不能触发事件
              radius: 5,
            },
            name: 'node-shape',
            draggable: true,
          });
          // border
          group!.addShape('rect', {
            attrs: {
              x: offsetX,
              y: offsetY,
              width,
              height: isDetail ? height : 40,
              stroke: 'transparent',
              strokeOpacity: 0.5,
              radius: 4,
            },
            name: 'border-shape',
            draggable: true,
          });
          // 头部
          group?.addShape('rect', {
            attrs: {
              x: offsetX,
              y: offsetY,
              width,
              height: 40,
              radius: isDetail ? [4, 4, 0, 0] : 4,
              parent: mainId,
              fill: cfg?.color, // 此处必须有fill 不然不能触发事件
            },
            draggable: true,
          });
          // 头部标题图标
          if (cfg?.image) {
            group?.addShape('image', {
              attrs: {
                x: offsetX + 12,
                y: offsetY + 12,
                width: 16,
                height: 16,
                img: cfg?.image,
                parent: mainId,
              },
              draggable: true,
            });
          }
          // 头部标题
          const title = (cfg?.form as any).name ?? cfg?.title;
          if (title) {
            group?.addShape('text', {
              attrs: {
                x: offsetX + 34,
                y: offsetY + 20,
                textAlign: 'left',
                textBaseline: 'middle',
                text: textUtil.fittingString(title, 140, 14),
                parent: mainId,
                type: 'mainTitle',
                fill: '#ffffff',
                fontSize: 14,
                tooltipContent: title,
              },
              draggable: true,
            });
          }
          // 头部状态图标
          if (cfg?.stateImage) {
            group?.addShape('image', {
              attrs: {
                x: offsetX + width - 32,
                y: offsetY + 12,
                width: 16,
                height: 16,
                parent: mainId,
                img: cfg.stateImage,
              },
              draggable: true,
            });
          }
          // 内容
          const content = (cfg?.form as any).desc ?? cfg?.label;
          if (isDetail && content) {
            group?.addShape('text', {
              attrs: {
                id: 'label' + uniqueId(),
                x: offsetX + 20,
                y: offsetY + 64,
                textAlign: 'left',
                type: 'mainTitle',
                textBaseline: 'middle',
                text: textUtil.fittingString(content, 120, 12),
                parent: mainId,
                fill: '#565758',
                fontSize: 12,
                tooltipContent: content,
              },
              draggable: true,
            });
          }

          const bbox = group!.getBBox();
          addPoints(group!, cfg?.inPoints as number[][], bbox);
          addPoints(group!, cfg?.outPoints as number[][], bbox);

          return shape;
        },
        // 设置状态
        setState(state, value, item) {
          if (setState) {
            setState(state, value, item);
            return;
          }

          const isDetail = getIsDetailGraph();
          const group = item?.getContainer();
          const groupChildren = group?.get('children');
          if (!groupChildren || groupChildren.length === 0) {
            return;
          }
          const shape = groupChildren[0]; // 顺序根据 draw 时确定
          const children = group?.findAll(g => g.attrs.parent === shape.attrs.id);
          const circles = group?.findAll(circle => circle.attrs.hoverChange);
          const border = group?.find(g => g.get('name') === 'border-shape');

          const selectStyles = () => {
            const color = item?.getModel().color;
            shape.attr({ fill: '#f3f9ff', stroke: color, cursor: 'move' });
            if (!isDetail) {
              border?.attr({ lineWidth: state === DEFAULT_HOVER ? 4 : 8, stroke: color });
            }
            children?.forEach(child => {
              child.attr('cursor', 'move');
            });
            if (item?.getModel().outPoints) {
              circles?.forEach(circle => {
                circle.attr({ fill: '#fff', opacity: 1 });
              });
            }
          };
          const unSelectStyles = () => {
            shape.attr({ fill: '#fff', stroke: '#CAD4DD', shadowBlur: 0, strokeOpacity: 1 });
            if (!isDetail) {
              border?.attr({ lineWidth: 0, stroke: 'transparent' });
            }
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
            default:
              break;
          }
        },
        getAnchorPoints(cfg) {
          return [...((cfg?.inPoints as number[][]) || []), ...((cfg?.outPoints as number[][]) || [])];
        },
      });
    },
  };
};
