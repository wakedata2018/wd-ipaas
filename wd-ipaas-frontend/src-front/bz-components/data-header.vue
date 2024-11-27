<template>
  <dss-header
    :logo="finalLogo"
    :default-active="defaultActive"
    :user-info="user"
    :show-help-center="false"
    :can-change-project="true"
    :about-button-clickable="false"
    @logout="logout"
  ></dss-header>
</template>

<script>
  import logoImg from '../../common/images/logo.png';
  import jdLogoImg from '../../common/images/logo_jd.png';
  import domainUtils from '../../common/domain-utils.js';
  import { mapState } from 'vuex';
  import baseApi from '@/api/base.js';
  export default {
    data() {
      return {
        logoImg,
        jdLogoImg,
        domainObj: null,
      };
    },
    computed: {
      ...mapState({
        user: state => state.user,
      }),
      homeMenuObj() {
        return {
          name: this.$t('platform.home'),
          link: '/portal',
          hrefName: 'cloud',
          href: 'https://cloud.wakedata.com/portal.html',
          path: '/portal.html', /// //url的host后面的路径
          port: null,
        };
      },
      menus() {
        return [
          {
            name: this.$t('platform.offine'),
            link: '/flow',
            hrefName: 'flow',
            href: 'http://flow.wakedata.com',
            path: null, /// //url的host后面的路径
            port: null,
          },
          {
            name: this.$t('platform.realtime'),
            link: '/streaming',
            hrefName: 'streaming',
            href: 'http://streaming.wakedata.com',
            path: null,
            port: null,
          },
          {
            name: this.$t('platform.open'),
            link: '/cloud',
            hrefName: 'cloud',
            href: '/',
            path: null,
            port: null,
          },
          {
            name: this.$t('platform.machineLearning'),
            link: '/minerva',
            hrefName: 'minerva',
            href: 'http://minerva.wakedata.com',
            path: null,
            port: null,
          },
          {
            name: this.$t('platform.dataAsset'),
            link: '/data-asset',
            href: 'http://dam.wakedata.com',
            path: null,
            hrefName: 'dam',
            port: null,
          },
          {
            name: this.$t('platform.chart'),
            link: '/chart',
            href: 'http://chart.wakedata.com',
            path: null,
            hrefName: 'chart',
            port: null,
          },
        ];
      },
      menusList() {
        const result = this.menus.map(item => this.translateUrl(item));
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
      },
    },
    created() {
      this.userinfo = this.$root.userinfo || this.userinfo;
    },
    methods: {
      handleSelect(key, keyPath) {
        const { path } = this.$route;
        const menuArr = this.menusList.filter(item => item.link === key);
        if (!!menuArr && menuArr.length > 0) {
          const menuItem = menuArr[0];
          if (path === key) {
            window.location.reload();
          } else {
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
        const type = this.domainObj.type;
        const domain = this.domainObj.domain;
        const result = { ...menuObj };
        if (menuObj.href !== '/' && !!menuObj.hrefName) {
          if (type === 'domain') {
            result.href = `http://${menuObj.hrefName}${domain}${menuObj.port ? ':' + menuObj.port : ''}${
              menuObj.path ? menuObj.path : ''
            }`;
          }
        }
        return result;
      },
      clickToPortal() {
        const homeMenuObj = this.translateUrl(this.homeMenuObj);
        window.location.href = homeMenuObj.href;
      },
      handleClick() {
        alert('button click');
      },
      handleOut() {
        baseApi.logout();
      },
      // logout() {
      //   window.top.location.href = '/dw/open/business/logout';
      // },
      logout() {
        baseApi.logout();
      },
    },
  };
</script>

<style scoped lang="less">
  @import '../css/var.less';
  .web-header {
    height: @header-height;
    line-height: @header-height;
    background: #333333;
  }
  /deep/ .el-menu {
    border-right: 0px;
  }
  .logo {
    height: @header-height;
    display: inline-block;
    width: 54px;
    text-align: center;
    box-sizing: border-box;
    padding-top: 11px;
    vertical-align: top;
    img {
      // height: 26px;
      height: 30px;
    }
  }
  .home {
    height: @header-height;
    display: inline-block;
    width: 54px;
    text-align: center;
    box-sizing: border-box;
    vertical-align: top;

    color: #2776fb;
    font-size: 0px;
    cursor: pointer;
    transition: all 0.3s;
    &:hover {
      background: rgba(0, 0, 0, 0.212);
    }
    & > span {
      font-size: 20px;
      height: 25px;
      display: inline-block;
      vertical-align: middle;
      width: 100%;
      border-right: 1px solid #4c4c4c;
      box-sizing: border-box;
      line-height: 25px;
    }
  }

  .right-menu {
    position: absolute;
    top: 0px;
    right: 0px;
    .right-select {
      position: relative;
      font-size: 12px;
      color: #bbbbbb;
      cursor: pointer;
      vertical-align: top;
      &.user-menu {
        width: auto;
        float: right;
        background: none;
        border-bottom: 0;
        font-size: 12px;
        color: #bbbbbb;
        .userName-div {
          max-width: 80px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          display: inline-block;
        }
        /deep/ .el-menu-item,
        /deep/ .el-submenu__title,
        /deep/ .el-menu-item {
          height: @header-height;
          line-height: @header-height;
          font-size: 13px;
          border-bottom: 0;
          color: #bbbbbb;
          i {
            color: #bbbbbb;
          }
          &:hover {
            background: none;
            color: #d4d3d3;
          }
        }
        /deep/ .el-badge {
          color: #bbbbbb;
        }
      }
    }
    /deep/ .login-button {
      border: 0px;
      border-radius: 34px;
      height: 30px;
      width: 75px;
      background: linear-gradient(to right, #2195f3 0%, #44c6fa 100%);
      color: white;
      vertical-align: top;
      font-weight: 400;
      margin: 12px 0 10px 10px;
      transition: all 0.3s;
      &:hover {
        filter: brightness(120%);
      }
    }
  }

  .menu-scrollbar {
    display: inline-block;
    width: calc(100% - 270px);
    height: @header-height;
    position: absolute;
    left: 108px;
    top: 0px;
    overflow: hidden;
    overflow-y: hidden;
    white-space: nowrap;
    padding-bottom: 0px;
    /deep/ .el-scrollbar__wrap {
      overflow: hidden;
      // max-height: calc(100vh - 260px);
    }
    &.en {
      /deep/.el-menu-selector {
        .el-menu-item,
        .el-submenu__title,
        .el-menu-item {
          font-family: Times, TimesNR, 'New Century Schoolbook', Georgia, 'New York', serif;
        }
      }
    }
    /deep/.el-menu-selector {
      display: flex;
      box-sizing: border-box;
      position: relative;
      border-bottom: 0;
      height: @header-height;
      margin-top: 0;
      margin-bottom: 0;
      padding: 0;
      white-space: nowrap !important;
      .el-menu-item,
      .el-submenu__title,
      .el-menu-item {
        height: @header-height;
        box-sizing: border-box;
        line-height: @header-height;
        font-weight: 400;
        font-size: 13px !important;
        a {
          // color: white;
          text-decoration: none;
        }
        &.is-active {
          a {
            color: #2776fb;
          }
          border-bottom: 4px solid #2776fb;
          background-color: transparent !important;
        }

        &:hover {
          background-color: rgba(0, 0, 0, 0.212) !important;
        }
      }
    }
    .menu-content {
      padding: 0 20px 0 0;
      display: inline-block;
      vertical-align: top;
      height: 100%;
      width: auto;
    }
  }
</style>
