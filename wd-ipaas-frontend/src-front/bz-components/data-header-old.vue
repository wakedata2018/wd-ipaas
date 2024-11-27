<template>
  <div class="box">
    <div class="logo">
      <router-link to="/">
        <div class="header-icon">
          <img src="../../src-front/assets/images/logo2.png" />
        </div>

        <div class="header-text">
          <div class="main-text">数据开放服务平台</div>
          <div class="sub-text">Data Open Service Platform</div>
        </div>
      </router-link>
      <!-- <img src="../../src/assets/images/logo1.png" alt />
      <div class="title">
        <div class="chinese">数据开放服务平台</div>
        <div class="english">Data open service Platform</div>
      </div>-->
    </div>
    <div class="contant">
      <!-- <div class="help">帮助中心</div>
      <el-dropdown class="language">
        <span class="el-dropdown-link">
          中文
          <i class="el-icon-arrow-down el-icon--right"></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item>English</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>-->
      <el-menu
        mode="horizontal"
        background-color="#333333"
        class="header-menu"
        text-color="#c2c5cc"
        active-text-color="#c2c5cc"
      >
        <el-submenu index="1" class="user-info-area">
          <template slot="title">
            <i class="icomfont el-icon-s-custom"></i>
            <span>{{user && user.displayName}}</span>
          </template>
          <el-menu-item index="1-1" class="logout" @click.native="logout">
            <i class="fa fa-sign-out"></i>
            退出
          </el-menu-item>
        </el-submenu>
      </el-menu>

      <!-- <el-dropdown class="language" @command="handleOut">
        <span class="el-dropdown-link" style="cursor:pointer">
          <i class="icomfont el-icon-s-custom"></i>
          {{user && user.displayName}}
          <i class="el-icon-arrow-down el-icon--right"></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="quit">退出</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>-->

      <!-- <div class="userInfo">
        <div class="username">
          <i class="icomfont el-icon-s-custom"></i>
          {{user && user.displayName}}
        </div>
      </div>-->
      <!-- <el-popover ref="popover" placement="bottom" title width="200" trigger="hover" content="退出登录" @click="handleOut"></el-popover> -->
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";
import baseApi from "../api/base.js";
export default {
  data() {
    return {
      menus: []
    };
  },
  computed: {
    ...mapState({
      user: state => state.user
    })
  },
  created() {
    this.userinfo = this.$root.userinfo || this.userinfo;
  },
  methods: {
    handleClick() {
      alert('button click');
    },
    handleOut() {
      baseApi.logout();
    },
    logout() {
      window.top.location.href = '/dw/open/business/logout';
    }
  }
};
</script>

<style scoped lang='less'>
.box {
  overflow: hidden;
  height: 52px;
  width: 100%;
  background-color: #333;
  display: flex;
  justify-content: space-between;
  .logo {
    .header-icon {
      height: 100%;
      display: inline-block;
      // padding: 6px 0 0 5px;
      padding: 12px 0 0 5px;
      img {
        // height: 26px;
        height: 30px;
      }
    }

    // display: none;
    .header-text {
      vertical-align: top;
      display: inline-block;
      margin: 8px 5px;
      padding-left: 10px;
      border-left: 1px solid #575757;
      .main-text {
        font-size: 14px;
        margin-bottom: 2px;
        font-family: PingFangSC-Regular, PingFang SC;
        font-weight: 600;
        color: rgba(255, 255, 255, 1);
        line-height: 20px;
      }
      .sub-text {
        font-size: 12px;
        font-family: PingFangSC-Regular, PingFang SC;
        color: #757575;
        line-height: 8px;
        height: 12px;
      }
    }
    position: absolute;
    top: 0px;
    left: 0px;
    width: 120px;
    // float: left;
    padding-right: 40px;
    font-size: 16px;
    text-align: left;
    padding-left: 10px;
    box-sizing: border-box;
    white-space: nowrap;
    color: rgb(194, 197, 204);
    &.en-logo {
      width: 260px;
    }
    /deep/ a {
      color: rgb(194, 197, 204);
      margin-left: 5px;
      text-decoration: none;
    }

    .icon {
      top: 2px;
      cursor: pointer;
      border-radius: 50%;
      padding: 10px;
      transition: 0.2s;

      &:active {
        background: rgba(0, 0, 0, 0.7);
      }
    }
  }
  .contant {
    position: absolute;
    right: 30px;
    display: flex;
    font-size: 12px;
    line-height: 52px;
    color: #bbb;
    .language {
      margin: 0 12px 0 12px;
      /deep/.el-dropdown-link {
        color: #bbb;
      }
    }
  }
  /deep/.el-popover {
    background: #000 !important;
    cursor: pointer !important;
  }

  .header-menu {
    /deep/ .el-submenu .el-submenu__title {
      height: 52px;
      line-height: 52px;
      color: #bbbbbb;
      border-bottom: 0 !important;
    }

    /deep/ .el-menu-item {
      height: 52px;
      line-height: 52px;
      color: #bbbbbb;
    }

    &::after {
      display: none;
    }
  }
}
</style>