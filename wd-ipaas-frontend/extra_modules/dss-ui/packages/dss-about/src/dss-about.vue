<template>
  <el-dialog class="bd-dialog not-lock-scroll" width="824px" :visible.sync="isShow" append-to-body>
    <template slot="title"><img src="./images/logo.png" style="width: 130px; height: 36px" /></template>
    <div class="title-div">
      <span class="title">{{ versionTitle }}</span>
    </div>
    <div class="content">
      <div class="content-item">
        <img class="content-img" src="./images/icon2.png" /><span class="content-text"
          >售后服务电子邮件：{{contactInfo.email}}</span
        >
      </div>
      <div class="content-item">
        <img class="content-img" src="./images/icon3.png" /><span class="content-text"
          >惟客数据网站：{{contactInfo.website}}</span
        >
      </div>
    </div>
    <div class="title-div">
      <span class="title">已授权的产品</span>
    </div>
    <div class="content">
      <div class="product" v-loading="platforms.loading">
        <span class="product-item" v-for="platform in menusList" :key="platform.id" @click="goto(platform)">{{
          platform.name
        }}</span>
      </div>
      <div class="product-warning">
        警告：本计算机程序受版权法和国际条约保护。如未经授权而擅自复制或传播本程序（或其中任何部分），将受到严厉的民事和刑事制裁并将在法律许可的最大限度内受到起诉。
      </div>
    </div>
  </el-dialog>
</template>

<script>
import api from '../../api/api.js';

export default {
  name: 'DssAbout',
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    userInfo: {
      type: Object,
      required: true,
    },
    host: {
      type: String,
      default: '',
    },
    versionTitle: {
      type: String,
      default: '惟数平台V5.0',
    },
    contactInfo: {
      type: Object,
      default: _ => ({
        email: 'media@wakedata.com',
        website: 'https://www.wakedata.com'
      })
    }
  },
  components: {},
  data() {
    return {
      platforms: {
        loading: false,
        list: [],
      },
    };
  },
  computed: {
    isShow: {
      get() {
        return this.visible;
      },
      set(val) {
        this.$emit('update:visible', val);
      },
    },
    loginName() {
      const account = this.userInfo.loginName || this.userInfo.account;
      return account;
    },
    menusList() {
      let result = this.platforms.list
        ? this.platforms.list.filter(item => item.code !== 'portal').map(item => this.translateUrl(item))
        : [];
      result = result.sort((a, b) => (a.sort ? a.sort : 0) - (b.sort ? b.sort : 0));
      return result;
    },
  },
  watch: {
    isShow(val) {
      if (!!val) {
        this.init(this.loginName);
      }
    },
  },
  methods: {
    init(account) {
      this.platforms.loading = true;
      api
        .getUserItem(account, this.host)
        .done(
          res => {
            if (res.data && res.data.length) {
              const data = res.data[0];
              this.platforms.list = data.topMenuList || [];
            } else {
              this.platforms.list = [];
            }
          },
          () => {
            this.platforms.list = [];
          }
        )
        .always(_ => {
          this.platforms.loading = false;
        });
    },
    goto(menu) {
      window.location.href = menu.url;
    },
    translateUrl(menuObj) {
      if (!this.domainObj) {
        this.domainObj = this.getDomain();
      }

      let type = this.domainObj.type;
      let domain = this.domainObj.domain;
      let result = {};
      try {
        if (menuObj) {
          result = JSON.parse(JSON.stringify(menuObj));
        }
      } catch (e) {
        console.log(e);
      }
      // let result = { ...menuObj };
      if ((domain && domain !== '.jd.com') || this.ipValidator(menuObj.url)) {
        return result;
      } else {
        if (menuObj.url !== '/' && !!menuObj.hrefName) {
          let reg = new RegExp('/$');
          let preReg = new RegExp('^/');
          if (type === 'domain') {
            let preAddress = `http://${menuObj.hrefName}${domain}${menuObj.port ? ':' + menuObj.port : ''}`;
            let path = menuObj.path
              ? preReg.test(menuObj.path) || reg.test(preAddress)
                ? menuObj.path
                : '/' + menuObj.path
              : '';
            result.url = `${preAddress}${path}`;
          }
        }

        return result;
      }
    },
    ipValidator(value) {
      const patt = /((25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d)))\.){3}(25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d)))/i;
      if (value == null || value === '') {
        return false;
      } else if (!patt.test(value)) {
        return false;
      } else {
        return true;
      }
    },
    getDomain() {
      let result = null;
      const host = window.location.hostname;
      const ipPatt = new RegExp(`^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$`);
      if (ipPatt.test(host) === true) {
        result = {
          type: 'ip',
          domain: host,
        };
      } else if (host === 'localhost') {
        result = {
          type: 'localhost',
          domain: 'localhost',
        };
      } else if (host.indexOf('.') !== -1) {
        const domains = host.split('.');
        let hrefName = domains[0];
        const domainPatt = new RegExp(`^${hrefName}\\.`);
        let domain = host.replace(domainPatt, '.');
        result = {
          type: 'domain',
          domain: domain,
        };
      } else {
        result = {
          type: 'unknown',
          domain: host,
        };
      }
      return result;
    },
  },
};
</script>
<style lang="less" scoped>
.selected-div {
  padding: 10px;
  font-size: 12px;
}
.is-all {
  opacity: 0.5;
}
/deep/.el-dialog {
  background: url('./images/about-back.png');
  background-color: white;
  border-radius: 10px;
  height: 505px;
  .el-dialog__body {
    padding: 0px 40px;
  }
  .el-dialog__header {
    text-align: center;
    padding: 20px 40px 12px 40px;
  }
}
.title-div {
  padding: 0 5px 15px;
  border-bottom: 1px solid #ebeef5;
  margin: 5px 0;
  .title {
    font-weight: 600;
    font-size: 16px;
    padding-left: 10px;
    border-left: 4px solid #00bcd4;
    color: #000;
  }
}
.content {
  padding: 14px 20px;
  .content-item {
    padding: 7px 0;
    .content-img {
      height: 24px;
      width: 24px;
      vertical-align: middle;
    }
    .content-text {
      vertical-align: middle;
      padding-left: 10px;
      font-size: 13px;
      font-family: PingFangSC, PingFangSC-Regular;
      font-weight: 400;
      text-align: left;
      color: #333333;
    }
  }
  .product {
    background: #f5f7fa;
    border-radius: 4px;
    min-height: 70px;
    padding: 10px;
    box-sizing: border-box;
    width: 555px;
    margin: 10px 0;
    .product-item {
      font-size: 12px;
      font-family: PingFangSC, PingFangSC-Regular;
      font-weight: 400;
      text-align: left;
      color: #333333;
      line-height: 17px;
      padding: 4px 8px;
      cursor: pointer;
      display: inline-block;
      transition: all 0.3s;
      border-radius: 15px;
      &:hover {
        background-color: white;
        color: #00bcd4;
      }
    }
  }
  .product-warning {
    margin-top: 25px;
    font-size: 12px;
    font-family: PingFangSC, PingFangSC-Regular;
    font-weight: 400;
    text-align: left;
    color: #8d939d;
    width: 555px;
    box-sizing: border-box;
  }
}
</style>