export enum IMPORT_TYPE {
  /** 常规导入 */
  NORMAL = 'NORMAL',
  /** 覆盖导入 */
  COVER = 'COVER',
}

export enum IMPORT_RESULT_STATUS {
  /** 未导入 */
  UN_IMPORT = 0,
  /** 导入成功 */
  SUCCESS_IMPORT = 1,
  /** 导入失败 */
  FAIL_IMPORT = 2,
}

export const IMPORT_RESULT_STATUS_MAP = {
  [IMPORT_RESULT_STATUS.UN_IMPORT]: '未导入',
  [IMPORT_RESULT_STATUS.SUCCESS_IMPORT]: '导入成功',
  [IMPORT_RESULT_STATUS.FAIL_IMPORT]: '导入失败',
};
