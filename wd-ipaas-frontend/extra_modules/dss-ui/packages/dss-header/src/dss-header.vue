<template>
  <header class="dss-header">
    <div class="header-left" style="font-size: 0px" :style="{ width: leftWidth + 'px' }">
      <div class="logo">
        <img
          :src="logoImg"
          :style="{ cursor: aboutButtonClickable ? 'pointer' : 'default' }"
          @click="showAbout"
        />
      </div>
      <div class="home" :class="!homeMenuObj ? 'disabled' : ''" @click="clickToPortal">
        <span>
          <i class="el-icon-s-home"></i>
        </span>
      </div>
      <el-scrollbar ref="menuScrollbar" class="menu-scrollbar">
        <el-menu
          class="platform-menu"
          :default-active="defaultActive"
          mode="horizontal"
          background-color="#333333"
          active-text-color="#00BCD4"
          @select="handleSelect"
        >
          <el-menu-item v-for="menu in menusList" :key="menu.code" :index="menu.code">{{ menu.name }}</el-menu-item>
        </el-menu>
      </el-scrollbar>
    </div>
    <div class="header-right">
      <project-selector
        :apiPrefix="apiPrefix"
        :refreshAfterChangingProject="refreshAfterChangingProject"
        @change-project="changeProject"
        v-if="canChangeProject"
      />
      <slot name="right"></slot>
      <el-menu
        class="user-menu"
        mode="horizontal"
        menu-trigger="hover"
        background-color="#333333"
        active-text-color="#00BCD4"
        text-color="#bbbbbb"
      >
        <el-menu-item index="help" v-if="showHelpCenter" @click="onHelpCenter">帮助中心</el-menu-item>
        <el-menu-item index="lang" v-if="language">
          <el-dropdown placement="bottom" @command="onLanguage">
            <span class="el-dropdown-link">
              {{ lang }}
              <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="zh-CN" :disabled="language === 'zh-CN'">{{
                langMap['zh-CN']
              }}</el-dropdown-item>
              <el-dropdown-item command="en" :disabled="language === 'en'">{{ langMap.en }}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </el-menu-item>
        <!-- <el-submenu index="lang">
          <template slot="title">中文</template>
          <el-menu-item index="en">English</el-menu-item>
          <el-menu-item index="cn">中文</el-menu-item>
        </el-submenu>-->
        <el-submenu class="user-info-area" index="user">
          <template slot="title">
            <i class="el-icon-user"></i>
            <span class="username" :title="userName">{{ userName }}</span>
          </template>
          <slot name="editUserInfo"></slot>
          <el-menu-item index="logout" @click.native="logout"> <i class="fa fa-sign-out"></i> 退出登录 </el-menu-item>
        </el-submenu>
      </el-menu>
    </div>
    <dss-about
      :host="host"
      :versionTitle="versionTitle"
      :contactInfo="contactInfo"
      :userInfo="userInfo"
      :visible.sync="dialog.about"
    ></dss-about>
  </header>
</template>

<script>
import './style.less';
// import logoImg from '../../images/logo.png';
import api from '../../api/api.js';
import dssAbout from '../../dss-about/src/dss-about.vue';
import projectSelector from '../../dss-project-selector/src/dss-project-selector.vue';

const logoImg = '/logo.png';
export default {
  name: 'DssHeader',
  props: {
    logo: {
      type: String,
      default: null,
      // required: true
    },
    // portal: {
    //   type: String,
    //   required: true
    // },
    /**
     * platforms完整结构
     * {
     *  id:,
     *  name: ,
     *  code: ,
     *  url: ,
     *  path: ,
     *  port: ,
     *  hrefName: ,
     *  ischecked:
     * }
     */
    /*platforms: {
      type: Array,
      required: true
    },*/
    defaultActive: {
      type: String,
      required: true,
    },
    userInfo: {
      type: Object,
      required: true,
    },
    language: {
      type: String,
      default: null,
    },
    showHelpCenter: {
      type: Boolean,
      default: false,
    },
    autoLoad: {
      type: Boolean,
      default: true,
    },
    host: {
      type: String,
      default: '',
    },
    versionTitle: {
      type: String,
      default: '惟数平台V5.0',
    },
    aboutButtonClickable: {
      type: Boolean,
      default: true,
    },
    contactInfo: {
      type: Object,
      default: _ => ({
        email: 'media@wakedata.com',
        website: 'https://www.wakedata.com',
      }),
    },
    canChangeProject: {
      type: Boolean,
      default: false,
    },
    /**
     * 接口前缀，机器学习用
     */
    apiPrefix: {
      type: String,
      default: null,
    },
    /**
     * 更改项目后刷新页面，为否则向上抛出change-project事件
     */
    refreshAfterChangingProject: {
      type: Boolean,
      default: true,
    },
  },
  components: { dssAbout, projectSelector },
  data() {
    return {
      leftWidth: 700,
      resizeTimer: null,
      langMap: {
        'zh-CN': '中文',
        en: 'English',
      },
      domainObj: {},
      platforms: [],
      logoImg: logoImg,
      dialog: {
        about: false,
      },
    };
  },
  computed: {
    userName() {
      return this.userInfo && (this.userInfo.displayName || this.userInfo.userName || this.userInfo.loginName);
    },
    loginName() {
      const account = this.userInfo.loginName || this.userInfo.account;
      return account;
    },
    lang: {
      get() {
        return this.langMap[this.language];
      },
      set(val) {
        this.$emit('update:language', val);
      },
    },
    menusList() {
      let result = this.platforms
        ? this.platforms.filter(item => item.code !== 'portal').map(item => this.translateUrl(item))
        : [];
      result = result.sort((a, b) => (a.sort ? a.sort : 0) - (b.sort ? b.sort : 0));
      return result;
    },
    homeMenuObj() {
      let findedCloud = this.platforms ? this.platforms.find(item => item.code === 'portal') : null;
      return findedCloud;
    },
  },
  methods: {
    init(account) {
      api.getUserItem(account, this.host).done(
        res => {
          if (res.data && res.data.length) {
            const data = res.data[0];
            this.logoImg = data.logo || logoImg;
            this.platforms = data.topMenuList || [];
          } else {
            this.logoImg = logoImg;
          }
        },
        () => {
          this.logoImg = logoImg;
        }
      );
    },
    showAbout() {
      if (!this.aboutButtonClickable) {
        return;
      }
      this.dialog.about = true;
    },
    onLanguage(val) {
      this.lang = val;
    },
    clickToPortal() {
      if (this.homeMenuObj) {
        let homeMenuObj = this.translateUrl(this.homeMenuObj);
        window.location.href = homeMenuObj.url;
        // window.location.href = this.portal || '';
      }
    },
    handleSelect(key, keyPath) {
      let menuArr = this.menusList.filter(item => item.code === key);
      if (!!menuArr && menuArr.length > 0) {
        let menuItem = menuArr[0];
        if (this.defaultActive === key && !menuItem.url) {
          window.location.reload();
        } else {
          window.location.href = menuItem.url || '';
        }
      }
    },
    onHelpCenter() {
      this.$emit('on-help-center');
    },
    logout() {
      this.$emit('logout');
    },
    calcWidth() {
      if (this.resizeTimer) {
        clearTimeout(this.resizeTimer);
      }
      this.resizeTimer = setTimeout(() => {
        const width = document.querySelector('header').offsetWidth;
        const rightWidth = document.querySelector('.header-right').offsetWidth;
        this.leftWidth = width - rightWidth;
      }, 500);
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
    changeProject(projectId) {
      this.$emit('change-project', projectId);
    },
  },
  mounted() {
    this.domainObj = this.getDomain();
    window.addEventListener('resize', this.calcWidth);
    this.calcWidth();
    if (this.autoLoad) {
      this.$nextTick(() => {
        this.init(this.loginName);
      });
    }
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.calcWidth);
  },
};
</script>
<style lang="less" scoped>
.user-menu {
  display: inline-block !important;
}
</style>
