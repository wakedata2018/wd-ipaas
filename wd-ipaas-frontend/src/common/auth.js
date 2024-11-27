/**
 * 单点登录
 * @module 'lib/auth'
 * @see module:utils/process-response/auth
 */
import services from '@/common/services';

/**
 *  @alias lib/auth
 * 大数据平台，单点登录处理逻辑
 *
 * @returns {any}
 */
function process() {
  return (res, showMessage) => {
    const { data, config } = res;
    // const { code, msg, value } = data;

    const code = data.code || data.errorCode;
    const msg = data.msg || data.errorMessage;
    const isSuccess = code === 200 || data.success === true;
    if (!isSuccess) {
      // 未手动配置 隐藏 消息提示时，公共提醒错误
      if (!config.hidden) {
        showMessage(`${config.action}失败：${msg || '未知错误'}`);
      }
      // 登录权限跳转
      if (code === 401) {
        setTimeout(() => {
          window.top.location.href = '/login.html';
        }, 200);
      }
      // eslint-disable-next-line prefer-promise-reject-errors
      return Promise.reject({ code, message: msg });
    }
    return data || {};
  };
}

export { process };

services.setProcess(process());
