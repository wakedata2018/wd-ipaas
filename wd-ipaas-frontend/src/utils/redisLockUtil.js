/**
 * redis锁相关参数
 */
function getRedisForm() {
  return {
    clazzName: 'com.wakedata.dw.open.model.api.RedisLockConfigAttr',
    lockName: null,
    lockType: 'Reentrant',
    keyPrefix: null,
    keyParams: [],
    waitTime: null,
    leaseTime: null,
    description: '',
    lesseeId: null,
    createTime: null,
    createBy: '',
    updateTime: null,
    updateBy: '',
    dataAssetApiId: null,
    configType: null,
    enableRedisLock: 0,
  };
}

export { getRedisForm };
