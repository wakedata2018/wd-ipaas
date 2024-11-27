/**
 * 定时任务启用状态
 */
export const TASK_STATUS = {
  /** 启用 */
  ON: 1,
  /** 禁用 */
  OFF: 0,
};

/**
 * 定时任务状态选择框数据
 */
export const TASK_STATUS_OPTIONS = [
  {
    value: TASK_STATUS.ON,
    label: '启用',
  },
  {
    value: TASK_STATUS.OFF,
    label: '禁用',
  },
];
