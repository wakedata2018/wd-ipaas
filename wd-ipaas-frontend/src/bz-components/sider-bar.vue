<template>
  <div :class="`sider-bar`">
    <el-scrollbar class="sider-bar-scroller">
      <!-- <div class="sider-bar"> -->
      <!-- <el-button
        type="text"
        icon="el-icon-s-fold"
        class="btn-collapse"
        @click="$emit('update:isCollapse', !isCollapse)"
      />-->
      <el-menu
        :default-active="activeMenu"
        router
        class="sider-menu"
        background-color="#3F3F3F"
        text-color="#BBBBBB"
        :collapse="isCollapse"
        @select="selectMenu"
      >
        <el-menu-item index="/index">
          <i class="el-icon-pie-chart" />
          <!-- <img src="../assets/images/icon-per.png" alt class="icon-img" /> -->
          首页
        </el-menu-item>
        <el-menu-item index="/data-manage">
          <i class="el-icon-tickets" />
          <!-- <img src="../assets/images/icon-com.png" alt class="icon-img" /> -->
          数据源管理
        </el-menu-item>

        <el-submenu index="/api-manage" class="api-manage">
          <template slot="title">
            <i class="el-icon-data-line" />
            <!-- <img src="../assets/images/icon-line.png" alt class="icon-img" /> -->
            <span slot="title">API管理</span>
          </template>
          <el-menu-item index="/api-manage/apijoin">接入管理</el-menu-item>
          <el-menu-item index="/join-approve/joinapprove">接入审批</el-menu-item>
          <el-menu-item index="/api-publish/apipublish">发布管理</el-menu-item>
          <el-menu-item index="/api-approve/apiapprove">审批管理</el-menu-item>
          <el-menu-item index="/api-theme/theme">接口分类管理</el-menu-item>
        </el-submenu>

        <el-submenu index="/api-statistics" class="statistic">
          <template slot="title">
            <i class="el-icon-data-analysis" />
            <!-- <img src="../assets/images/icon-line2.png" alt class="icon-img" /> -->
            <span slot="title">统计分析</span>
          </template>
          <el-menu-item index="/api-statistics/apicall">API调用统计</el-menu-item>
          <el-menu-item index="/api-statistics/apierr">API错误原因统计</el-menu-item>
          <el-menu-item index="/api-statistics/apivisit">平台访问应用统计</el-menu-item>
        </el-submenu>

        <!-- <el-submenu index="/apidata" class="system-manage">
        <template slot="title">
          <img src="../assets/images/icon-set.png" alt="" class="icon-img">
          <span slot="title">系统管理</span>
        </template>
        <el-menu-item index="/apidata/applist">用户管理</el-menu-item>
        <el-menu-item index="/apidata/apipublish">角色管理</el-menu-item>
        <el-menu-item index="/apidata/myapi">权限管理</el-menu-item>
        <el-menu-item index="/apidata/apply">告警管理</el-menu-item>
        </el-submenu>-->
      </el-menu>

      <!-- </div> -->
    </el-scrollbar>
    <div v-if="!isJDCloud" class="footer">
      <span>深圳市惟客数据科技有限公司</span>
      <span>All right reserved</span>
    </div>
  </div>
</template>
<script>
  import baseApi from '@/api/base.js';
  import domainUtils from '../../common/domain-utils.js';
  import { mapState, mapActions } from 'vuex';

  export default {
    props: {
      isCollapse: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        // activeMenu: '/index'
      };
    },
    watch: {
      $route() {
        this.setActiveMenu();
      },
    },
    computed: {
      ...mapState({
        user: state => state.user,
      }),
      isJDCloud() {
        return true;
        // let domainObj = domainUtils.getDomain();
        // if (domainObj.type === 'domain' && domainObj.domain === '.jd.com') {
        //   return true;
        // } else {
        //   return !!this.user && !!this.user.loginName && this.user.loginName === 'jingdongyun';
        // }
      },
      activeMenu: {
        get() {
          return this.getTopRoute();
        },
        set() {},
      },
    },
    methods: {
      setActiveMenu() {
        const url = this.$route.path.toLowerCase();
        this.activeMenu = url;
      },
      getTopRoute() {
        const { path } = this.$route;
        let final = path.toLowerCase();
        if (path === '/') {
          final = '/index';
        }
        return final;
      },
      onLogout() {
        baseApi.logout();
      },
      selectMenu(index) {
        const { path } = this.$route;
        if (path === index) {
          window.location.reload();
        }
      },
    },
  };
</script>

<style lang="less" scoped>
  .sider-bar {
    box-sizing: border-box;
    background-color: #3f3f3f;
    // box-shadow: 8px 5px 20px 0px rgba(3,3,3,0.06);
    /deep/ .sider-bar-scroller {
      width: 100%;
      position: relative;
      height: calc(100% - 50px);
      .el-scrollbar__wrap {
        overflow-x: hidden;

        .el-scrollbar__view {
          position: relative;
        }
      }
    }
    .icon-img {
      margin-right: 10px;
    }
    .btn-collapse {
      position: absolute;
      right: 16px;
      font-size: 18px;
      top: 0;
      z-index: 2;
      color: #fff;
      /deep/ i {
        transition: all 0.5;
      }
    }
    /deep/ .el-menu-item {
      background-color: #fff;
      color: #333;
    }
    /deep/ .el-submenu__title {
      background-color: #fff;
      color: #333;
      height: 35px;
      line-height: 35px;
    }
    /deep/ .el-submenu__icon-arrow {
      // color: #333;
      right: 40px;
    }
    /deep/ .el-menu {
      box-sizing: border-box;
    }

    .sider-menu {
      border-right: none;
      // margin-top: 5px;
      width: 100%;
      overflow: hidden;
      /deep/ .el-menu {
        background: transparent;
      }

      & > .el-menu-item {
        height: 44px;
        line-height: 44px;

        &.is-active {
          color: #2776fb;
          font-weight: bold;
          // i {
          //   color: #2776fb;
          // }
          &::after {
            content: ' ';
            position: absolute;
            left: 0;
            border-radius: 1px;
            bottom: 0;
            top: 0;
            width: 0px;
            border: 2px solid #2776fb;
          }
        }
      }

      &.el-menu--collapse {
        & > .el-menu-item {
          // padding-left: 20px !important;
        }

        /deep/ .el-submenu .el-submenu__title {
          // padding-left: 20px !important;
        }
      }
    }

    /deep/ .el-submenu {
      .el-submenu__title {
        height: 44px;
        line-height: 44px;
        font-size: 13px;
        &:hover {
          background-color: rgba(0, 198, 247, 0.1);
        }
      }

      &.is-active {
        .el-submenu__title {
          // color: #2776fb;
          // font-weight: bold;
          // i {
          //   color: #2776fb;
          // }

          &::after {
            content: ' ';
            position: absolute;
            left: 0;
            border-radius: 1px;
            bottom: 0;
            top: 0;
            width: 0px;
            border: 2px solid #2776fb;
          }
        }
      }

      .el-menu-item {
        padding-left: 50px !important;
        height: 44px;
        line-height: 44px;
        position: relative;
        font-size: 13px;
        &:hover {
          background-color: rgba(0, 198, 247, 0.1);
        }

        &:focus {
          background-color: transparent;
        }

        &.is-active {
          color: #2776fb;
          font-weight: bold;
        }
        .el-menu--inline {
          .el-menu-item {
            height: 34px;
            line-height: 34px;
          }
        }
      }
    }

    .footer {
      font-size: 12px;
      color: #6a6a6a;
      position: absolute;
      bottom: 20px;
      padding: 0 10px 0 12px;
    }
  }
</style>
