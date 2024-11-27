const webpack = require('webpack');
const MonacoWebpackPlugin = require('monaco-editor-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const path = require('path');
// 后台服务器地址
const SERVER = process.env.SERVER || 'http://ywzt-gwopen.wakedata.com';

module.exports = {
  assetsDir: 'ipaas',
  lintOnSave: false,
  pages: {
    login: {
      entry: './login/index.js',
      template: './public/login.html',
    },
    'api-doc': {
      entry: './apiDoc/index.js',
      template: './public/api-doc.html',
    },
    index: './src/index.js',
    open: './src-front/index.js',
  },
  css: {
    loaderOptions: {
      sass: {
        implementation: require('sass'),
        sassOptions: {
          // 生效代码
          outputStyle: 'expanded',
        },
      },
    },
  },
  publicPath: process.env.NODE_ENV === 'production' ? `/` : '/',
  devServer: {
    host: '0.0.0.0',
    port: 80,
    allowedHosts: 'all',
    proxy: ['/wd/employee', '/permission', '/wd-decorate', '/wd', '/dw'].reduce((prev, cur) => {
      prev[cur] = {
        changeOrigin: true,
        secure: false,
        target: SERVER,
        // 修改 cookie
        onProxyRes(proxyRes) {
          const cookies = proxyRes.headers['set-cookie'];
          if (cookies) {
            const newCookie = cookies.map(function (cookie) {
              return cookie.replace(/Domain=.*?(\.\w+\.\w+);/i, 'Domain=$1;');
            });
            // 修改cookie path
            delete proxyRes.headers['set-cookie'];
            proxyRes.headers['set-cookie'] = newCookie;
          }
        },
      };
      return prev;
    }, {}),
  },
  configureWebpack: {
    devtool: 'source-map',
    plugins: [
      new webpack.ProvidePlugin({
        $: 'jquery',
        jQuery: 'jquery',
        'window.jQuery': 'jquery',
        'window.$': 'jquery',
        'window.echarts': 'echarts',
      }),
      new MonacoWebpackPlugin({
        languages: ['sql', 'mysql', 'json', 'xml'],
        features: ['coreCommands', 'find'],
      }),
      new CopyWebpackPlugin([
        {
          from: path.resolve(__dirname, './logo.ico'),
          to: path.resolve(__dirname, './dist'),
        },
        {
          from: path.resolve(__dirname, './logo.png'),
          to: path.resolve(__dirname, './dist'),
        },
      ]),
    ],
  },
  chainWebpack: config => {
    config.optimization.set('realContentHash', true);
  },
};
