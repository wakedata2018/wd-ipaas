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
 * 任务执行类型
 */
export const TASK_EXECUTE_TYPE = {
  /** 永久执行 */
  FOREVER: 1,
  /** 自定义日期执行 */
  CUSTOM: 2,
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
/**
 * yyyy-MM-dd HH:mm:ss 格式时间转 yyyy-MM-dd
 */
export function dateTimeToDate(dateTime) {
  return dateTime.substring(0, 16);
}
