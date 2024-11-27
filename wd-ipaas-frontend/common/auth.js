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
function process(callbackUrl = '/dw/redirect') {
  return (res, showMessage, loginUrl) => {
    const { data, config } = res;
    // const { code, msg, value } = data;

    const code = data.code || data.errorCode;
    const msg = data.msg || data.errorMessage;
    const value = data.value || data.data;
    const isSuccess = code === 200 || data.success === true;
    if (!isSuccess) {
      // 未手动配置 隐藏 消息提示时，公共提醒错误
      if (!config.hidden) {
        showMessage(`${config.action}失败：${msg || '未知错误'}`);
      }
      // 登录权限跳转
      if (code === 401) {
        setTimeout(() => {
          const href = window.location.href;
          // const href = 'http://10.187.14.171:8080/jsp/index.html#/query';
          // console.log(`${value.redirect}${encodeURIComponent(href)}`);
          const { host, protocol } = window.location;
          const redirect = `${protocol}//${host}${callbackUrl}`;
          let temRedirect = value;
          if (value.redirect) {
            temRedirect = value.redirect;
          }
          window.top.location.href = `${temRedirect}${encodeURIComponent(href)}&redirect_uri=${redirect}`;
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
