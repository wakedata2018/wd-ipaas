# dss-ui

*大数据组件库*

## 安装

```bash
  npm set registry http://172.26.59.72:4873
  # npm设置为wakedata镜像
  npm i dss-ui 
  # 或 yarn add dss-ui
```

## 使用

### 在 `main.js` 文件中引入dss-ui并注册

~~样式文件需单独引入~~

*v0.2.2更新：样式已内联，不再需要单独引入。请移除import样式文件，以免报错*

```js
  # main.js
  
  // v0.2.2: 不再需要引入，请移除
  // 引入样式文件
  // import 'dss-ui/lib/dss-ui.css';

  // 引入组件库
  import DssUI from 'dss-ui';
  // 注册组件库
  Vue.use(DssUI);
```

### 在项目中使用dss组件

```html
  <dss-component></dss-component>
```

## 已支持的dss组件

- [dss-menu](http://gitlab.wakedata-inc.com/fecookie/bigdata/dss-UI/blob/dev/examples/docs/dss-menu.md)
- [dss-header](http://gitlab.wakedata-inc.com/fecookie/bigdata/dss-UI/blob/dev/examples/docs/dss-header.md)
  
## 数据源(data-source)组件的使用

*数据源组件内包含了业务逻辑，使用时只需引入data-source，并且保证后端接入了数据源相关接口*

```bash
# 引入
import { DataSource } from 'dss-ui/lib/components/index.umd.min.js';

# 注册
components: { DataSource }
```

## 更新日志
> v0.3.26
修复数据源预览数据分页不清0的问题

> v0.3.25
权限系统菜单隐藏和禁用统一处理，跳转无权限页面统一处理；hdfs文件列表查询接口修改

> v0.3.24
数据源添加ftp、sftp

> v0.3.23
数据源后端重构版本的联调

> v0.3.22
明源云类型问题修复；保存时密码可能置空的问题修复

> v0.3.21
添加apachedoris类型；明源云类型前端报错问题修复

> v0.3.20
添加starrocks、presto类型数据源；添加密码加密；点击顶部菜单只跳转不刷新

> v0.3.19
mac下，数据源表列表、右侧表详情和表预览滚动条内容显示不全的问题

> v0.3.18
数据源表列表不显示描述的问题修复

> v0.3.17
去掉数据源测试失败多余提示

> v0.3.16
数据源列表添加数据库名

> v0.3.15
修复数据源报错不弹窗的问题

> v0.3.14
顶部菜单栏默认图标取根目录相对路径

> v0.3.13
数据源增加advancedParams和additionalParams字段的适配，数据源控件添加表格类型

> v0.3.12
菜单图标透明度改为100%

> v0.3.11
修复修改数据源时，右侧查看表不刷新的问题

> v0.3.10
构建时，不把第三方模块打包进去

> v0.3.9
数据源列表为空时会查询空数据源的问题；删除后不刷新预览的问题

> v0.3.8
数据源列表为空时会查询空数据源的问题；删除后不刷新预览的问题

> v0.3.7
查询数据源参数接口调整

> v0.3.6
添加influxdb类型；是否可以测试添加后端判断

> v0.3.5
无权限页面和空数据组件

> v0.3.4
表数据预览和表详情窗格显示表名

> v0.3.3
侧栏滚动条高度问题；高度变化后有一段空白动画的问题

> v0.3.2
右侧添加插槽；更改项目后，是否刷新页面的配置项

> v0.3.1
添加新权限平台的项目切换选择

> v0.3.0
数据源快速切换时数据预览结果错误覆盖的问题修复

> v0.2.12
添加关于页面

> v0.2.9
数据源添加postgresql类型

> v0.2.7
fix: 打包之后...扩展符，在一些浏览器无法运行，去掉扩展符。

> v0.2.6
增加host（默认http://priv.wakedata.com:8080）属性配置，配置从那个服务器获取获用户配置信息
npm
> v0.2.5
增加从http://priv.wakedata.com:8080获取用户配置信息（包含logo,menu等）

> v0.2.4

fix: 数据源的一些bug

> v0.2.3
 
dss-header：添加适配portal配置项

> v0.2.2

1、发布数据源(独立打包)   
2、样式内联，不再生成单独的css文件(原先引入的样式文件请删除，以免报错)

> v0.2.0

dss-header：菜单加入sort排序字段

> v0.1.9
> v0.1.8
> v0.1.7

> v0.1.6

dss-header：修复测试环境dss-common打包的问题

> v0.1.5

dss-header：修复hover样式

> v0.1.4

dss-header：修复京东云跳转问题，修复退出登录的样式

> v0.1.3

dss-header：修复跳转问题

> v0.1.2

dss-header：加入京东云的账号判断，加入获取顶部菜单的接口

> v0.1.1

dss-header：添加帮助中心和语言切换，平台列表项的唯一标识符由 `link` 改为 `hrefName`

> v0.1.0

dss-ui发布，支持dss-menu、dss-header组件
