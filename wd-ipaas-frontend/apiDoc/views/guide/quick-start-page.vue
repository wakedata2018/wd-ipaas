<template>
  <div class="quick-start">
    <h1>接入指南</h1>

    <h2>成为企业开发者</h2>
    <p>使用昆仑平台-集成云任何能力和产品首先要成为开发平台企业开发者，暂不支持个人开发者。</p>
    <ul>
      <li>
        <p>
          获取开发平台账号方式：线下联系商务同学，获取集成开发平台开发者账号，打开<span class="quick-start__link"
            ><a href="/login.html">开发平台首页</a></span
          >，登录进入；
        </p>
      </li>
      <li>
        <p>提供能力：支持惟客云系统、客户自有系统、外部第三方系统的能力接入，也可按实际需求进行扩展；</p>
      </li>
    </ul>

    <h2>创建应用</h2>
    <ul>
      <li>
        <p>填写应用名称、权限管理（支持二次开发，按需指定权限校验方式；如：指定惟客云应用）。</p>
        <img src="../../assets/images/img-add-app.png" />
      </li>
    </ul>

    <h2>应用管理</h2>
    <ol>
      <li>
        <p>
          进入‘基础管理-应用详情’，在基础信息可以查看App Key（应用唯一标识，平台通过appkey鉴别应用身份）和App
          Secret（平台分配的密钥，是调用接口重要信息，在日常测试和代码使用中请注意妥善保管，如果出现泄漏可以重置App
          Secret。）。
        </p>
        <img src="../../assets/images/img-manage-app.png" />
      </li>
      <li>
        <p>
          在页面下方API授权，查看已获得的api权限的具体的介绍和API列表，可以点击【授权】，将API权限授权给到当前应用，其余未显示的API，需要开发者申请API权限获取，如何获取API权限，查看<span
            class="quick-start__link"
            >‘API权限申请’</span
          >。
        </p>
        <img src="../../assets/images/img-manage-auth.png" />
      </li>
      <li>
        <p>
          API权限申请：每个集成开发平台账号都有自身的API权限，API权限申请根据账号申请，一次申请，该账号所创建的应用都可以使用。在API申请成功之后，可以直接在
          ‘基础管理-应用管理-API授权’页面查看，或者在‘API管理’，查看申请成功状态的API数据。
        </p>
        <img src="../../assets/images/img-manage-permission.png" />
        <p>步骤一：提交API权限申请，进入‘基础管理-API管理’，点击申请，申请您所需要的API权限；</p>
        <img src="../../assets/images/img-manage-permission-step1.png" />
        <p>
          步骤二：申请成功之后，您可进入‘基础管理-审批管理’，查看平台管理员审核进度，审核通过之后，您就可以获的此API调用权限。
        </p>
        <img src="../../assets/images/img-manage-permission-step2.png" />
      </li>
    </ol>

    <h2>发布上线</h2>
    <p>
      应用开发测试完成后，进入‘基础管理-应用管理’，即可点击上线，调用应用；对于未上线的应用限制了不能给到外部调用。如果应用停止不使用，请进行应用下线操作，避免继续调用接口带来数据安全问题，如果无法操作下线，请联系服务商实施人员处理。
    </p>
  </div>
</template>

<style scoped lang="less">
  .quick-start {
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
