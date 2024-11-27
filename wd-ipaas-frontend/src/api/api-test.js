import service from '@/common/services';
import axios from 'axios';

const controller = '/dw/open/business';
export default {
  getTestInfo(params) {
    return service.get(`${controller}/asset_detail`, {
      action: '获取测试时的参数',
      params,
    });
  },
  testApi(apiPath, list, reqMethod, enableLog = false) {
    const bodyList = list.filter(item => item.httpParamKind === 'BODY') || [];
    const headList = list.filter(item => item.httpParamKind === 'HEAD') || [];
    // let queryList = list.filter(item => item.httpParamKind !='HEAD' || item.httpParamKind!='BODY') || [];
    const headers = {};
    let body = null;
    for (const item of headList) {
      if (item.sample) {
        headers[item.assetColumns] = item.sample;
      }
    }
    for (const item of bodyList) {
      if (item.sample) {
        if (!body) {
          body = {};
        }
        body[item.assetColumns] = item.sample;
      }
    }
    let enableLogStr = '';
    if (apiPath.indexOf('?') !== -1) {
      enableLogStr = `&__enable_log__=${enableLog}`;
    } else {
      enableLogStr = `?__enable_log__=${enableLog}`;
    }

    if (reqMethod === 'POST' && headers['Content-Type']?.indexOf('application/x-www-form-urlencoded') !== -1) {
      return axios({
        url: `/dw/open/api/${apiPath}${enableLogStr}`,
        method: 'post',
        data: body, // body参数,
        transformRequest: [
          function (data) {
            let ret = '';
            for (const y in data) {
              ret += encodeURIComponent(y) + '=' + encodeURIComponent(data[y]) + '&';
            }
            return ret;
          },
        ],
        headers,
      }).then(res => {
        return res;
      });
    } else {
      return axios({
        url: `/dw/open/api/${apiPath}${enableLogStr}`,
        method: reqMethod === 'POST' ? 'post' : 'get',
        data: body, // body参数,
        headers,
      }).then(res => {
        return res;
      });
    }
  },
  /**
   * api测试接口请求参数传参修改
   * @param {请求方式} method
   * @param {拼接了query的url} url
   * @param {POST请求传body参数，GET请求传不传} params
   * @param {请求头} headers
   */
  request(method, url, params, headers = { 'Content-Type': 'application/json' }) {
    return axios({
      url,
      method,
      data: params, // body参数,
      headers,
    }).then(res => {
      return res;
    });
  },
  getSignByAppId(params) {
    return service.get(`${controller}/getSign`, {
      action: '获取Sign值',
      // hidden: true,
      params,
    });
  },
  /**
   * 选择测试应用
   */
  chooseTestAppKey(params) {
    return service.get(`${controller}/data_access_app/chooseAppTest`, {
      action: '选择测试应用',
      params,
    });
  },
};
