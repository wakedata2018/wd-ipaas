<template>
  <dss-header
    :logo="finalLogo"
    :default-active="defaultActive"
    :user-info="user"
    :canChangeProject="true"
    :show-help-center="false"
    :aboutButtonClickable="false"
    @logout="logout"
  ></dss-header>
</template>

<script>
import { mapState } from "vuex";
import logoImg from "../../common/images/logo.png";
import jdLogoImg from "../../common/images/logo_jd.png";
import domainUtils from "../../common/domain-utils.js";
import baseApi from "@/api/base.js";
export default {
  data() {
    return {
      logoImg,
      jdLogoImg,
      domainObj: null
    };
  },
  computed: {
    ...mapState({
      user: state => state.user
    }),
    homeMenuObj() {
      return {
        name: this.$t('platform.home'),
        link: '/portal',
        hrefName: 'cloud',
        href: 'https://cloud.wakedata.com/portal.html',
        path: '/portal.html', /////url的host后面的路径
        port: null
      };
    },
    menus() {
      return [
        {
          name: this.$t('platform.offine'),
          link: '/flow',
          hrefName: 'flow',
          href: 'http://flow.wakedata.com',
          path: null, /////url的host后面的路径
          port: null
        },
        {
          name: this.$t('platform.realtime'),
          link: '/streaming',
          hrefName: 'streaming',
          href: 'http://streaming.wakedata.com',
          path: null,
          port: null
        },
        {
          name: this.$t('platform.open'),
          link: '/cloud',
          hrefName: 'cloud',
          href: '/',
          path: null,
          port: null
        },
        {
          name: this.$t('platform.machineLearning'),
          link: '/minerva',
          hrefName: 'minerva',
          href: 'http://minerva.wakedata.com',
          path: null,
          port: null
        },
        {
          name: this.$t('platform.dataAsset'),
          link: '/data-asset',
          href: 'http://dam.wakedata.com',
          path: null,
          hrefName: 'dam',
          port: null
        },
        {
          name: this.$t('platform.chart'),
          link: '/chart',
          href: 'http://chart.wakedata.com',
          path: null,
          hrefName: 'chart',
          port: null
        }
      ];
    },
    menusList() {
      let result = this.menus.map(item => this.translateUrl(item));
      return result;
    },
    finalLogo() {
      if (
        (!!this.user && !!this.user.loginName && this.user.loginName === 'jingdongyun') ||
        (!!this.domainObj && this.domainObj.type === 'domain' && this.domainObj.domain === '.jd.com')
      ) {
        return this.jdLogoImg;
      } else {
        return this.logoImg;
      }
    },
    defaultActive: {
      get() {
        return 'cloud';
      },
      set(val) {}
    }
  },
  created() {
    this.userinfo = this.$root.userinfo || this.userinfo;
  },
  methods: {
    handleSelect(key, keyPath) {
      const { path } = this.$route;
      let menuArr = this.menusList.filter(item => item.link === key);
      if (!!menuArr && menuArr.length > 0) {
        let menuItem = menuArr[0];
        if (path === key) window.location.reload();
        else {
          if (!menuItem.href) {
            this.$router.push({ path: key });
          } else {
            window.location.href = menuItem.href;
          }
        }
      }
      this.$nextTick(_ => {
        this.defaultActive = this.menusList[2].link;
      });
    },
    translateUrl(menuObj) {
      if (!this.domainObj) {
        this.domainObj = domainUtils.getDomain();
      }
      let type = this.domainObj.type;
      let domain = this.domainObj.domain;
      let result = { ...menuObj };
      if (menuObj.href !== '/' && !!menuObj.hrefName) {
        if (type === 'domain') {
          result.href = `http://${menuObj.hrefName}${domain}${!!menuObj.port ? ':' + menuObj.port : ''}${
            !!menuObj.path ? menuObj.path : ''
          }`;
        }
      }
      return result;
    },
    clickToPortal() {
      let homeMenuObj = this.translateUrl(this.homeMenuObj);
      window.location.href = homeMenuObj.href;
    },
    handleClick() {
      alert('button click');
    },
    handleOut() {
      baseApi.logout();
    },
    logout() {
      baseApi.logout();
    }
  }
};
</script>

<style scoped lang='less'>

</style>