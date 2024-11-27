/* eslint-disable no-prototype-builtins */
/**
 * 这里是ajax的通用访问接口处理
 */
import axios from 'axios';
import Qs from 'qs';
import './expromise';
import Message from './msgbox';

const toString = Object.prototype.toString;
const service = axios.create({
  baseURL: '',
  timeout: 60000,
  transformRequest: function (data, config, test) {
    // JSON 转换
    if (config['Content-Type'] && config['Content-Type'].includes('json')) {
      return JSON.stringify(data);
    }

    return Qs.stringify(data);
  },
  headers: {
    'Cache-Control': 'no-cache',
    'Content-Type': 'application/x-www-form-urlencoded',
    'X-Requested-With': 'XMLHttpRequest',
  },
});

const showMessage = (message, type = 'error') => {
  Message({
    showClose: true,
    type,
    duration: 4000,
    message,
  });
};

/**
 * 服务端接口empty字符串跟null返回的结果不同，过滤掉empty字符串
 * @param params
 * @param emptyString 是否过滤空字符串
 */
function filterEmptyKey(params, emptyString) {
  if (Array.isArray(params) || params == null) {
    return params;
  }

  Object.keys(params).forEach(key => {
    if (params[key] === null || (emptyString && params[key] === '')) {
      delete params[key];
    }
  });
}

service.interceptors.request.use(
  config => {
    /* var xtoken = getXtoken()
      if (xtoken != null) {
      config.headers['X-Token'] = xtoken
      } */

    if (config.method === 'post') {
      const params = {
        ...config.data,
      };
      filterEmptyKey(params, config?.filterEmptyString ? config.filterEmptyString : false); // 过滤空字符串
      const lowerUrl = (config.url + '').toLocaleLowerCase();
      const hasAbsoluteSchema = config.url.indexOf('http') === 0;
      if (!hasAbsoluteSchema) {
        if (lowerUrl.indexOf('/dsp/') > -1 || lowerUrl.indexOf('/dmp/') > -1) {
          for (const key in params) {
            const type = toString.call(params[key]);
            if (type === '[object Object]' || type === '[object Array]') {
              params[key] = JSON.stringify(params[key]);
            }
          }
        }
      }

      // params._t = Date.parse(new Date()) / 1000;
      config.data = params;
    } else if (config.method === 'get') {
      config.params = {
        _t: Date.parse(new Date()) / 1000,
        ...config.params,
      };
      filterEmptyKey(config.params, config.hasOwnProperty('filterEmptyString') ? config.filterEmptyString : true); // get过滤空字符串
    }
    return config;
  },
  function (error) {
    return Promise.reject(error);
  }
);

service.interceptors.response.use(
  res => {
    const { data, config } = res;
    // Blob 类型处理
    if (data instanceof Blob) {
      // 兼容自定义下载文件名
      if (config.withFileName) {
        return res;
      }
      return data;
    }
    // manualProcessing 开发手动处理错误信息， 默认不用传，则走自动处理流程
    if ((!data.success || data.success === 'false') && data.code !== 200 && !config.manualProcessing) {
      const msg = data.errorMessage || data.message || data.msg || '未知错误';
      const code = data.errorCode || data.code || -1000;

      // 未手动配置 隐藏 消息提示时，公共提醒错误
      if (!config.hidden) {
        setTimeout(() => {
          showMessage(`${config.action}失败：${msg}`);
        }, 0);
      }

      // 登录权限跳转
      if (code === 401) {
        setTimeout(() => {
          const url = encodeURIComponent(window.location.href);
          window.top.location.href = `/login.html?gotoUrl=${url}`;
        }, 1000);
      }

      // return Promise.reject(new Error('请求失败'))
      // eslint-disable-next-line prefer-promise-reject-errors
      return Promise.reject({
        code,
        message: msg,
      });
    }

    return data || {};
  },
  error => {
    if (!error.config.hidden) {
      let message = error.message;
      if (message.includes('timeout')) {
        message = '系统超时，请稍后再试';
      } else if (message.includes('Network Error')) {
        message = '网络链接失败，请稍后再试';
      } else if (error.response && error.response.status === 403) {
        message = '您没有该权限';
      } else if (error.response && error.response.status === 500) {
        message = '服务器内部错误，请稍后再试';
      } else if (error.response && error.response.status === 502) {
        message = '系统升级中，请稍后重试';
      } else if (error.response && error.response.status === 503) {
        message = '服务不可用，请稍后再试';
      } else if (error.response && error.response.status === 504) {
        message = '系统超时，请稍后再试';
      }
      setTimeout(() => {
        showMessage(`${error.config.action}失败：${message}`);
      }, 0);
      return Promise.reject(new Error(message));
    }
    return Promise.reject(error);
  }
);

/**
 * 以json格式向后端提交数据
 *
 * @param {String} url 请求的url
 * @param {Object} params 参数
 * @param {Object} config 配置
 *
 * @returns promise对象
 */
service.json = (url, params, config) => {
  const isArray = Object.prototype.toString.call(params) === '[object Array]';
  const defaultConfig = {
    headers: {
      'Content-Type': 'application/json',
    },
    transformRequest: function (data, requestConfig) {
      if (isArray) {
        return JSON.stringify(params);
      }
      return JSON.stringify(data);
    },
  };
  const newConfig = Object.assign(defaultConfig, config);
  return service.post(url, params, newConfig);
};

/**
 * 带有数组参数的get请求，需要对数组参数转换，如ids: [1, 2, 3]转换成ids=1&ids=2&ids=3
 * @param url
 * @param config
 * @return {AxiosPromise<any>}
 */
service.arrayGet = (url, config) => {
  if (config) {
    config.paramsSerializer = function (params) {
      return Qs.stringify(params, { arrayFormat: 'repeat' });
    };
  }
  return service.get(url, config);
};

/**
 * 带有数组参数的post请求，需要对数组参数转换，如ids: [1, 2, 3]转换成同个参数名多次传递
 * @param url
 * @param params
 * @param config
 * @return {AxiosPromise<any>}
 */
service.arrayPost = (url, params, config) => {
  const defaultConfig = {
    transformRequest: function (data, requestConfig) {
      return Qs.stringify(params, { arrayFormat: 'repeat' });
    },
  };
  const newConfig = Object.assign(defaultConfig, config);
  return service.post(url, params, newConfig);
};

export default service;
