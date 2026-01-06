import { services } from 'dss-common';
// const dataSourceContr = '/dw/rest/meta';
const dataSourceContr = '/dw/datasource';
// services.interceptors.request.use(
//   config => {
//     config.headers.platformKey = `flow`;
//     return config;
//   },
//   err => {
//     return Promise.reject(err);
//   }
// );

export default {
  queryDataSourceType(params) {
    return services.get(`${dataSourceContr}/type/list`, {
      params,
      action: '查询数据源类型'
    });
  },

  queryDataSource(params) {
    return services.get(`${dataSourceContr}/data/query`, {
      params,
      action: '查询数据源列表'
    });
  },

  queryDataSourceParams(params) {
    return services.get(`${dataSourceContr}/datasource/params`, {
      params,
      action: '查询数据源参数'
    });
  },

  queryDataSourceParamsArray(params) {
    return services.get(`${dataSourceContr}/type-param/params/${params}`, {
      // params,
      action: '查询数据源参数'
    });
  },

  uploadSshKey(params) {
    return services.post(`${dataSourceContr}/data/ssh/key/upload`, params, {
      action: '上传SSH密钥文件',
    });
  },

  saveDataSource(params) {
    // saveDataSource(params, platformKey) {
    return services.json(`${dataSourceContr}/data/create`, params, {
      action: '保存数据源',
      // header:{platformKey}
    });
  },

  updateDataSource(params) {
    return services.put(`${dataSourceContr}/data/update`, params, {
      action: '更新数据源',
      headers: {
        "Content-Type": "application/json",
      },
      type: "json",
      transformRequest: function (data) {
        return JSON.stringify(data);
      },
    });
  },

  deleteDataSource(params) {
    return services.delete(`${dataSourceContr}/data/delete/${params}`, {
      action: '删除数据源'
    });
  },

  connectTesting(params) {
    return services.json(`${dataSourceContr}/operator/test/connection`, params, {
      action: '测试连接'
    });
  },

  queryTables(params) {
    return services.get(`${dataSourceContr}/operator/query/tables`, {
      params,
      action: '查询表'
    });
  },

  queryTableColumn(params) {
    return services.get(`${dataSourceContr}/operator/get/table/columns`, {
      params,
      action: '查询表详情'
    });
  },

  previewTable(params) {
    return services.get(`${dataSourceContr}/operator/get/table/preview`, {
      params,
      action: '数据源预览'
    });
  },

  // 查询数据源的文件列表数据
  queryFile(params) {
    return services.get(`${dataSourceContr}/operator/query/files`, {
      params,
      action: '查询文件列表'
    });
  },
};
