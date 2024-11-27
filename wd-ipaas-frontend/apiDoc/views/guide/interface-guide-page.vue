<template>
  <div class="interface-guide">
    <h1>接口调用</h1>
    <h2>API调用方法详解</h2>
    <p>
      昆仑平台-集成云的API是基于HTTPS协议来调用的，开发者（ISV）可以直接使用平台提供的官方SDK（支持多种语言，包含了请求的封装，签名加密，响应解释，性能优化等）来调用，以下主要是针对自行封装HTTPS请求进行API调用的原理进行详细解说。
    </p>
    <img src="../../assets/images/img-interface-call.png" />

    <h2>请求地址</h2>
    <el-table :data="requestAddr" header-row-class-name="table-header" border>
      <el-table-column prop="env" label="环境" width="150"></el-table-column>
      <el-table-column prop="reqMethod" label="请求方式" width="150"></el-table-column>
      <el-table-column prop="httpsUrl" label="HTTPS地址"></el-table-column>
    </el-table>

    <h2>公共参数</h2>
    <p>调用任何一个API都必须传入的参数，目前支持的公共参数有：</p>
    <el-table :data="commonReqParams" header-row-class-name="table-header" border>
      <el-table-column prop="assetColumns" label="参数名称" width="150"></el-table-column>
      <el-table-column prop="assetDatatype" label="参数类型" width="150"></el-table-column>
      <el-table-column prop="httpParamKind" label="请求类型" width="100"></el-table-column>
      <el-table-column prop="required" label="是否必须" width="100">
        <template #default="scope">
          {{ scope.row.required ? '是' : '否' }}
        </template>
      </el-table-column>
      <el-table-column prop="descriptions" label="参数说明"></el-table-column>
    </el-table>

    <h2>公共响应参数</h2>
    <el-table :data="commonResParams" header-row-class-name="table-header" border>
      <el-table-column prop="attributeName" label="参数名称" width="150"></el-table-column>
      <el-table-column prop="attributeType" label="类型" width="150"></el-table-column>
      <el-table-column prop="attributeDescribe" label="参数说明"></el-table-column>
    </el-table>

    <h2>业务参数</h2>
    <p>API调用除了必须包含公共参数外，如果API本身有业务级的参数也必须传入，每个API的业务级参数请考API文档说明。</p>

    <h2>签名算法</h2>
    <p>
      为了防止API调用过程中被黑客恶意篡改，保证用户的数据安全，集成开发平台服务端会根据请求参数，对签名进行验证，只有验证通过的请求才会被服务端处理，签名过程如下：
    </p>
    <ul>
      <li>
        <p>
          对所有API请求参数（包括公共参数和POST
          请求BODY，但除去sign参数、byte[]类型的参数和文件post内容），根据参数名称的ASCII码表的顺序排序。如：foo:1,
          bar:2, foo_bar:3, foobar:4排序后的顺序是bar:2, foo:1, foo_bar:3, foobar:4。
        </p>
      </li>
      <li>
        <p>
          将排序好的参数名和参数值拼装在一起，根据上面的示例得到的结果为：bar2foo1foo_bar3foobar4，最后再将请求BODY（如：{"orderNo":123}）添加到最后得出bar2foo1foo_bar3foobar4{"orderNo":123}。
        </p>
      </li>
      <li>
        <p>
          在上一步得到的字符串最后加上预定义的appSecret（如XYZ）得到bar2foo1foo_bar3foobar4{"orderNo":123}XYZ，再将得到的字符串采用UTF-8编码，使用签名算法对编码后的字节流使用MD5算法进行摘要，如：MD5(bar2foo1foo_bar3foobar4{"orderNo":123}XYZ)。
        </p>
      </li>
      <li>
        <p>
          再将MD5得到的字节流结果使用16进制小写字符表示，如：HEX(MD5("bar2foo1foo_bar3foobar4{"orderNo":123}XYZ".getBytes("utf-8"))
          ="546e4aa36d2d624da5ff816ca030951c"，得出的32位小写字符即为签名sign。
        </p>
      </li>
    </ul>

    <h2>请求示例</h2>
    <p>具体步骤如下：</p>
    <ol>
      <li>
        <p><b>根据获取token api获取accessToken</b></p>
        <p>例如获取到的accessToken=at690743D2C27D585E67C725EADBA34652</p>
      </li>
      <li>
        <p><b>设置接口参数值</b></p>
        <p>公共参数：</p>
        <p>accessToken=at690743D2C27D585E67C725EADBA34652</p>
        <p>timestamp=1662451881363</p>
        <p>业务参数：</p>
        <p>tenantId=390</p>
        <p>{“aaa”:”[123,12]”}</p>
      </li>
      <li>
        <p><b>生成签名</b></p>
        <p>按签名算法生成签名：</p>
        <p>
          hex(md5(accessTokenat690743D2C27D585E67C725EADBA34652tenantId390timestamp1662451881363{"aaa":"[123,12]"}34ab2d28cd4e4fc5b7d683a2ea289ad1))
        </p>
        <p>sign=08e448c140a4f95886f5f618d447a323</p>
      </li>
      <li>
        <p><b>组装HTTPS请求</b></p>
        <p>
          将所有参数名和参数值采用utf-8进行URL编码（参数顺序可随意，但必须要包括签名参数），然后通过GET或POST（含byte[]类型参数）发起请求，如：
        </p>
        <p>POST请求</p>
        <p class="interface-guide__link">
          <a :href="postSampleLink">
            {{ postSampleLink }}
          </a>
        </p>
        <p>添加请求头：Content-Type:application/json</p>
        <p>添加请求body: {“aaa”:”[123,12]”}</p>
      </li>
    </ol>

    <h2>响应示例</h2>
    <p>成功示例：</p>
    <api-json-viewer :value="successSample" />
    <p>错误示例：</p>
    <api-json-viewer :value="errorSample" />
    <h2>SDK下载地址</h2>
    <div style="margin-bottom: 100px">
      <a href="https://test-material-1259575047.cos.ap-guangzhou.myqcloud.com/ipaas-sdk/ipaas-openapi-sdk.zip"
        >https://test-material-1259575047.cos.ap-guangzhou.myqcloud.com/ipaas-sdk/ipaas-openapi-sdk.zip</a
      >
    </div>
  </div>
</template>

<script>
  import { ApiJsonViewer } from '../../components';

  export default {
    components: {
      ApiJsonViewer,
    },

    data() {
      return {
        requestAddr: [
          {
            env: '正式环境',
            reqMethod: 'POST',
            httpsUrl: 'https://vip-ipaas-test.shenyejituan.com',
          },
        ],
        commonReqParams: [
          {
            assetColumns: 'timestamp',
            assetDatatype: 'varchar',
            httpParamKind: 'QUERY',
            required: '是',
            descriptions: '当前时间戳，毫秒，例如：1543222562000。',
          },
          {
            assetColumns: 'accessToken',
            assetDatatype: 'varchar',
            httpParamKind: 'QUERY',
            required: '是',
            descriptions: '使用预先分配的appkey与appSercret通过调用授权接口获取',
          },
          {
            assetColumns: 'sign',
            assetDatatype: 'varchar',
            httpParamKind: 'QUERY',
            required: '是',
            descriptions: 'accessToken，API输入参数签名结果，签名算法参照下节的介绍。',
          },
        ],
        commonResParams: [
          {
            attributeName: 'success',
            attributeType: 'Boolean',
            attributeDescribe: '接口执行状态',
          },
          {
            attributeName: 'data',
            attributeType: 'Object',
            attributeDescribe: '返回的数据对象',
          },
          {
            attributeName: 'code',
            attributeType: 'Integer',
            attributeDescribe: '状态码',
          },
          {
            attributeName: 'pageSize',
            attributeType: 'Integer',
            attributeDescribe: '分页参数,每页显示数据的条数',
          },
          {
            attributeName: 'pageNo',
            attributeType: 'Integer',
            attributeDescribe: '分页参数,当前页码',
          },
          {
            attributeName: 'totalCount',
            attributeType: 'Long',
            attributeDescribe: '分页参数,返回数据总条数',
          },
        ],
        successSample: [
          {
            code: 100,
            msg: '',
            data: [],
            success: true,
          },
          '<xml><code>100</code><msg></msg><data></data><success>true</success></xml>',
        ],
        errorSample: [
          {
            code: 1008,
            msg: 'accessToken无效或已过期',
            data: null,
            success: false,
          },
          '<xml><code>1008</code><msg>accessToken无效或已过期</msg><data></data><success>false</success></xml>',
        ],
        postSampleLink:
          'https://ipaas.wakedt.cn/dw/open/api/app22/wd-app/rpc/app.queryAppBaseInfoByTenantId?sign=08e448c140a4f95886f5f618d447a323&accessToken=at690743D2C27D585E67C725EADBA34652&timestamp=1662451881363',
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
