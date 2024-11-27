<template>
  <div class="interface-guide">
    <h1>accessToken文档</h1>
    <!-- 获取accessToken part -->
    <h2>获取accessToken</h2>
    <div>
      <h3>接口地址</h3>
      <el-table :data="GetAccessTokenData.request" header-row-class-name="table-header" border>
        <el-table-column prop="httpsUrl" label="地址"></el-table-column>
        <el-table-column prop="reqMethod" label="请求方式"></el-table-column>
        <el-table-column prop="consumes" label="consumes"></el-table-column>
        <el-table-column prop="produces" label="produces"></el-table-column>
      </el-table>

      <h3>请求参数</h3>
      <el-table :data="GetAccessTokenData.requestParams" header-row-class-name="table-header" border>
        <el-table-column prop="name" label="参数名称"></el-table-column>
        <el-table-column prop="desc" label="参数说明"></el-table-column>
        <el-table-column prop="requestType" label="请求类型"></el-table-column>
        <el-table-column prop="required" label="是否必须"></el-table-column>
        <el-table-column prop="dataType" label="数据类型"></el-table-column>
      </el-table>

      <h3>响应状态</h3>
      <el-table :data="GetAccessTokenData.responseStatus" header-row-class-name="table-header" border>
        <el-table-column prop="code" label="状态码"></el-table-column>
        <el-table-column prop="desc" label="说明"></el-table-column>
        <el-table-column prop="schema" label="schema"></el-table-column>
      </el-table>

      <h3>响应参数</h3>
      <el-table :data="GetAccessTokenData.responseParams" header-row-class-name="table-header" border>
        <el-table-column prop="name" label="参数名称"></el-table-column>
        <el-table-column prop="desc" label="参数说明"></el-table-column>
        <el-table-column prop="type" label="类型"></el-table-column>
        <el-table-column prop="schema" label="schema"></el-table-column>
      </el-table>

      <h3>schema属性说明</h3>
      <p>AccessTokenGenerateDTO</p>
      <el-table :data="GetAccessTokenData.schemaInfo" header-row-class-name="table-header" border>
        <el-table-column prop="name" label="参数名称"></el-table-column>
        <el-table-column prop="desc" label="参数说明"></el-table-column>
        <el-table-column prop="type" label="类型"></el-table-column>
        <el-table-column prop="schema" label="schema"></el-table-column>
      </el-table>

      <h3>响应示例</h3>
      <api-json-viewer :value="GetAccessTokenData.sample" />
    </div>

    <br />
    <!-- 刷新accessToken part -->
    <h2>刷新accessToken</h2>
    <div>
      <h3>接口地址</h3>
      <el-table :data="RefreshAccessTokenData.request" header-row-class-name="table-header" border>
        <el-table-column prop="httpsUrl" label="地址"></el-table-column>
        <el-table-column prop="reqMethod" label="请求方式"></el-table-column>
        <el-table-column prop="consumes" label="consumes"></el-table-column>
        <el-table-column prop="produces" label="produces"></el-table-column>
      </el-table>

      <h3>请求参数</h3>
      <el-table :data="RefreshAccessTokenData.requestParams" header-row-class-name="table-header" border>
        <el-table-column prop="name" label="参数名称"></el-table-column>
        <el-table-column prop="desc" label="参数说明"></el-table-column>
        <el-table-column prop="requestType" label="请求类型"></el-table-column>
        <el-table-column prop="required" label="是否必须"></el-table-column>
        <el-table-column prop="dataType" label="数据类型"></el-table-column>
      </el-table>

      <h3>响应状态</h3>
      <el-table :data="RefreshAccessTokenData.responseStatus" header-row-class-name="table-header" border>
        <el-table-column prop="code" label="状态码"></el-table-column>
        <el-table-column prop="desc" label="说明"></el-table-column>
        <el-table-column prop="schema" label="schema"></el-table-column>
      </el-table>

      <h3>响应参数</h3>
      <el-table :data="RefreshAccessTokenData.responseParams" header-row-class-name="table-header" border>
        <el-table-column prop="name" label="参数名称"></el-table-column>
        <el-table-column prop="desc" label="参数说明"></el-table-column>
        <el-table-column prop="type" label="类型"></el-table-column>
        <el-table-column prop="schema" label="schema"></el-table-column>
      </el-table>

      <h3>schema属性说明</h3>
      <p>AccessTokenRefreshDTO</p>
      <el-table :data="RefreshAccessTokenData.schemaInfo" header-row-class-name="table-header" border>
        <el-table-column prop="name" label="参数名称"></el-table-column>
        <el-table-column prop="desc" label="参数说明"></el-table-column>
        <el-table-column prop="type" label="类型"></el-table-column>
        <el-table-column prop="schema" label="schema"></el-table-column>
      </el-table>

      <h3>响应示例</h3>
      <api-json-viewer :value="RefreshAccessTokenData.sample" />
    </div>

    <br />
    <!-- 校验accessToken part -->
    <h2>校验accessToken</h2>
    <div>
      <h3>接口地址</h3>
      <el-table :data="CheckAccessTokenData.request" header-row-class-name="table-header" border>
        <el-table-column prop="httpsUrl" label="地址"></el-table-column>
        <el-table-column prop="reqMethod" label="请求方式"></el-table-column>
        <el-table-column prop="consumes" label="consumes"></el-table-column>
        <el-table-column prop="produces" label="produces"></el-table-column>
      </el-table>

      <h3>请求参数</h3>
      <el-table :data="CheckAccessTokenData.requestParams" header-row-class-name="table-header" border>
        <el-table-column prop="name" label="参数名称"></el-table-column>
        <el-table-column prop="desc" label="参数说明"></el-table-column>
        <el-table-column prop="requestType" label="请求类型"></el-table-column>
        <el-table-column prop="required" label="是否必须"></el-table-column>
        <el-table-column prop="dataType" label="数据类型"></el-table-column>
      </el-table>

      <h3>响应状态</h3>
      <el-table :data="CheckAccessTokenData.responseStatus" header-row-class-name="table-header" border>
        <el-table-column prop="code" label="状态码"></el-table-column>
        <el-table-column prop="desc" label="说明"></el-table-column>
        <el-table-column prop="schema" label="schema"></el-table-column>
      </el-table>

      <h3>响应参数</h3>
      <el-table :data="CheckAccessTokenData.responseParams" header-row-class-name="table-header" border>
        <el-table-column prop="name" label="参数名称"></el-table-column>
        <el-table-column prop="desc" label="参数说明"></el-table-column>
        <el-table-column prop="type" label="类型"></el-table-column>
        <el-table-column prop="schema" label="schema"></el-table-column>
      </el-table>

      <h3>响应示例</h3>
      <api-json-viewer :value="CheckAccessTokenData.sample" />
    </div>
    <br />
    <br />
  </div>
</template>

<script>
  import { ApiJsonViewer } from '../../components';
  import { GetAccessTokenData, RefreshAccessTokenData, CheckAccessTokenData } from './data';

  export default {
    components: {
      ApiJsonViewer,
    },

    data() {
      return {
        GetAccessTokenData,
        RefreshAccessTokenData,
        CheckAccessTokenData,
      };
    },
  };
</script>

<style scoped lang="less">
  .interface-guide {
    color: #303133;

    h1 {
      font-size: 30px;
      line-height: 38px;
      margin-top: 44px;
    }

    h2 {
      font-weight: 500;
      font-size: 24px;
      line-height: 32px;
    }

    p {
      color: #606266;
    }

    &__link {
      color: rgb(51, 102, 255);

      a {
        text-decoration-line: none;
      }
    }
  }
</style>
