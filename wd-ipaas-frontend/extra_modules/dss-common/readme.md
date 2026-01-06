
# 安装
```
  npm set registry http://172.26.59.72:4873
  npm设置为wakedata镜像
  yarn add dss-common
```
注意：dss-common构建，虽然不打包element-ui进去，但引用element-ui的版本号会影响项目的构建的完整性。如：dss-flow项目，如果dss-common使用element-ui旧的版本号，dss-flow使用element-ui新的版本，会导致字体使用的是dss-common的element-ui字体库，出现字体丢失现象。
# import utils from 'dss-common'
## import { date, number } from 'dss-common'
对象集合 <br/>
```
{
  array // 数组相关的方法
  browser, //浏览器判断辅助函数
  date, //  日期辅助函数
  event, // 事件
  md5, // md5加密
  number, // 数字辅助函数
  object, // 对象辅助函数
  services, //ajax请求
  storage, //存储辅助函数
  string, // 字符串辅助函数
  url // url辅助函数
  cookies // js-cookie的引用
}
```
# import 'dss-common/lib/auth'

处理后端返回的数据，使用大数据平台处理逻辑，会跳转到单点登录处理

# lib/geo 地理位置相关
## import 'dss-common/lib/geo/china'

echarts注册中国地图,使用中国必须先引入一次，代码已经自动注册地图

## import { cityData, provinceData } from 'dss-common/lib/geo/location'

省份名称不带省字，城市不带市字如广东省在provinceData是广东。

cityData 城市对应的的经纬度，数据格式: { '安顺': [ 105.9462, 26.25367 ] }

provinceData 省对应的经纬度，数据格式: { 重庆: [106.54, 29.59] }

# import 'dss-common/lib/style/dss-ui.scss'
引入维客宝标准样式组件

表格例子
```
<el-table class="dss-table" /></el-table>
<el-pagination class=""  />
```

# import * as components 'dss-common/lib/components
````
{
  Chart,
  ChartEmpty,
  InnerSidebar,
  RollingTitle
}
````

# import VuePlain from 'dss-common/plugins/vue-plain'
vue-plain插件复制对象
