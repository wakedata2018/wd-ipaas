import G6, { ShapeOptions, IPoint } from '@antv/g6';
import { DEFAULT_HOVER, DEFAULT_SELECTED } from '../constants';
import { getControlPath } from '../utils';

export interface CustomEdgeOptions {
  name?: string;

  draw?: ShapeOptions['draw'];
  afterDraw?: ShapeOptions['afterDraw'];
  setState?: ShapeOptions['setState'];
}

/**
 * 自定义编排连线
 * @param options
 * @returns
 */
export const setCustomEdge = (options?: CustomEdgeOptions) => {
  const { name = 'customEdge', setState } = options ?? {};
  return {
    init() {
      G6.registerEdge(
        name,
        {
          options: {
            style: {
              stroke: '#CAD4DD',
              lineAppendWidth: 4,
              opacity: 1,
              cursor: 'pointer',
              endArrow: {
                path: G6.Arrow.vee(),
                stroke: '#CAD4DD',
                fill: '#CAD4DD',
              },
            },
          },
          minCurveOffset: [0, 0],
          getPath(points: IPoint[]) {
            return [['M', points[0].x, points[0].y], getControlPath(points), ['L', points[3].x, points[3].y]];
          },
          // 注：已删除draw，使用内置的getPath，不使用 draw ,否则 combo 更新后，连线无法得到更新
          setState(state, value, item) {
            if (setState) {
              setState(state, value, item);
              return;
            }
            const group = item?.getContainer();
            const shape = group?.get('children')[0];
            const changeBorderStyle = ({ stroke = 'transparent', lineWidth = 1 }) => {
              shape.attr({
                lineWidth,
                stroke,
                endArrow: {
                  path: G6.Arrow.vee(),
                  stroke,
                  fill: stroke,
                },
              });
            };

            const hoverStyles = () => {
              changeBorderStyle({ stroke: '#CAD4DD', lineWidth: 3 });
            };
            const selectStyles = () => {
              changeBorderStyle({ stroke: '#a7b1bb', lineWidth: 3 });
            };
            const originalStyles = () => {
              changeBorderStyle({ stroke: '#CAD4DD', lineWidth: 1 });
              if (item?._cfg?.model?.isDashing) {
                item._cfg.model.isDashing = false;
              }
              shape.stopAnimate();
              shape.attr('lineDash', null);
            };

            if (value) {
              switch (state) {
                case DEFAULT_SELECTED:
                  selectStyles();
                  break;
                case DEFAULT_HOVER:
                  hoverStyles();
                  break;

                default:
                  break;
              }
            } else {
              originalStyles();
            }
          },
        },
        'cubic'
      );
    },
  };
};
