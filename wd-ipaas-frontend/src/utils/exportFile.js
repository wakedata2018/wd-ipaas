import axios from 'axios';
import { OperationType } from '@/utils/enum';

/**
 * 封装导出json文件post请求
 * @param url  下载接口url
 * @param data 参数
 * @param type 操作类型
 * @returns {Promise}
 */
export const getDownload = (url, params = {}, type = OperationType.EXPORT) => {
  return new Promise((resolve, reject) => {
    axios.defaults.headers['Content-Type'] = 'application/json;charset=UTF-8';
    axios({
      method: 'post',
      url, // 请求地址
      data: params,
      responseType: 'blob', // 表明返回服务器返回的数据类型
    }).then(
      async response => {
        // 判断类型是否返回json，返回json时报错
        if (response.data.type === 'application/json') {
          const res = await response.data.text();
          if (typeof res === 'string') {
            resolve(JSON.parse(res));
          }
        } else {
          const blob = response.data;
          // 正确时导出文件
          resolve(blob);
          const now = new Date();
          // 文件后缀
          const suffix = type === OperationType.EXPORT ? '.json' : '.md';
          const fileName = now.getTime() + suffix;
          if (window.navigator.msSaveOrOpenBlob) {
            navigator.msSaveBlob(blob, fileName);
          } else {
            const link = document.createElement('a');
            link.href = window.URL.createObjectURL(blob);
            link.download = fileName;
            link.click();
          }
        }
      },
      err => {
        reject(err);
      }
    );
  });
};
