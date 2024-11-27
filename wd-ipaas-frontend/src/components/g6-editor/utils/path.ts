import { IPoint } from '@antv/g6';

/**
 * 根据 custom-edge 线的起始点、结束点计算拐点位置
 */
export const getControlPath = (points: IPoint[]) => {
  const start = points[0];
  const end = points[3];
  const hgap = Math.abs(end.x - start.x || end.y - start.y);
  const r = hgap ? hgap / (hgap / 50) + 10 : 0;

  const map: Record<string, (string | number)[]> = {
    '00': ['C', start.x - r, start.y, end.x - r, end.y, end.x - 16, end.y],
    '01': ['C', start.x - r, start.y, end.x, end.y - r, end.x, end.y - 16],
    '02': ['C', start.x - r, start.y, end.x + r, end.y, end.x + 16, end.y],
    '03': ['C', start.x - r, start.y, end.x, end.y + r, end.x, end.y + 16],
    '10': ['C', start.x, start.y - r, end.x - r, end.y, end.x - 16, end.y],
    '11': ['C', start.x, start.y - r, end.x, end.y - r, end.x, end.y - 16],
    '12': ['C', start.x, start.y - r, end.x + r, end.y, end.x + 16, end.y],
    '13': ['C', start.x, start.y - r, end.x, end.y + r, end.x, end.y + 16],
    '20': ['C', start.x + r, start.y, end.x - r, end.y, end.x - 16, end.y],
    '21': ['C', start.x + r, start.y, end.x, end.y - r, end.x, end.y - 16],
    '22': ['C', start.x + r, start.y, end.x + r, end.y, end.x + 16, end.y],
    '23': ['C', start.x + r, start.y, end.x, end.y + r, end.x, end.y + 16],
    '30': ['C', start.x, start.y + r, end.x - r, end.y, end.x - 16, end.y],
    '31': ['C', start.x, start.y + r, end.x, end.y - r, end.x, end.y - 16],
    '32': ['C', start.x, start.y + r, end.x + r, end.y, end.x + 16, end.y],
    '33': ['C', start.x, start.y + r, end.x, end.y + r, end.x, end.y + 16],
  };
  return (
    map[`${start.anchorIndex ?? 0}${end.anchorIndex ?? 0}`] || ['C', points[1].x, points[1].y, points[2].x, points[2].y]
  );
};
