
## 集成开发平台api调用sdk

> 代码介绍

### 1、com.wakedata.openapi.sdk.accesstoken
> 提供获取、刷新、校验accessToken方法，帮助开发者快速实现api调用

### 2、com.wakedata.openapi.sdk.common
> 提供签名验证、类型转换、api接口地址、http请求调用工具类

### 3、com.wakedata.openapi.sdk.exception
> 提供api异常信息类

### 4、com.wakedata.openapi.sdk.result
> 提供api返回包装对象

### 5、com.wakedata.openapi.sdk.api
> 按照api分组及api信息进行分包，提供api的请求参数、返回参数、以及调用api工具类封装

### 6、com.wakedata.openapi.sdk.MainTest及com.wakedata.openapi.sdk.OpenApiExample
> api调用示例

### 7、com.wakedata.openapi.sdk.generator
> 根据api生成sdk工具类
> 目前只支持两级参数生成，比如Object中嵌套Object/List<Object>，在下一层还有对象嵌套则都会使用Map<String,Object>/List<Map<String,Object>>处理

### 8、使用说明
> 可在idea开发工具中打开sdk，使用mvn进行打包构建，生成的带jar-with-dependencies.jar的jar即可导入到项目中使用