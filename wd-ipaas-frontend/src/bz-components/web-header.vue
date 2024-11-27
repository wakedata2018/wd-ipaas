<!--顶部导航条-->

<template>
  <div class="web-header">
    <div class="logo">
      <img class="logo-img" src="../assets/images/white/logo.png" />
      <div class="header-title" style="display:inline-block;">
        <span>数据开放服务平台</span>
      </div>
    </div>
    <div class="header-menu">
      <div style="float:right;">
        <el-menu mode="horizontal" @select="onSelect">
          <!-- <el-submenu index="2">
            <template slot="title">接口分类换肤</template>
            <el-menu-item index="changeThemeWhite">清新谈雅</el-menu-item>
            <el-menu-item index="changeThemeBlack">商务酷黑</el-menu-item>
            <el-menu-item index="changeThemeBlue">炫蓝科技</el-menu-item>
          </el-submenu>-->
          <el-menu-item class="user-info">
            <i class="bdp-icon icon-user"></i>
            {{user && user.displayName}}
          </el-menu-item>
          <el-menu-item index="logout">
            <i class="bdp-icon icon-logout"></i>
            <span>退出</span>
          </el-menu-item>
        </el-menu>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from "vuex";
import baseApi from "@/api/base.js";

export default {
  props: {
    isCollapse: {
      type: Boolean,
      default: false
    }
  },
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
    onSelect(e) {
      const method = this[e];
      if (method) {
        method();
      }
    },
    logout() {
      baseApi.logout();
    },
    changeThemeWhite() {
      this.changeTheme('white');
    },
    changeThemeBlack() {
      this.changeTheme('black');
    },
    changeThemeBlue() {
      this.changeTheme('blue');
    },
    changeTheme(theme) {
      this.$store.commit('changeTheme', theme);
    }
  }
};
</script>

<style lang="less" scoped>
.web-header {
  position: fixed;
  width: 100%;

  .logo {
    width: 200px;
    float: left;
    height: 60px;
    overflow: hidden;
  }

  .logo-img {
    margin-left: 10px;
    height: 30px;
    vertical-align: middle;
  }

  .header-title {
    position: absolute;
    color: #383d47;
    font-size: 18px;
    left: 200px;
    font-weight: 500;
    border-left: 2px solid #dee2e7;
    height: 28px;
    line-height: 28px;
    top: 18px;
    padding-left: 12px;
    font-weight: bold;
  }

  .header-menu {
    margin-left: 200px;
  }
}

.theme-white .web-header {
  background: white;
  box-shadow: 0px 0px 10px 0px rgba(0, 0, 0, 0.2);

  .el-submenu .el-submenu__title,
  .el-menu-item {
    color: #383d47;

    &.user-info {
      color: #383d47 !important;

      .bdp-icon {
        color: #383d47 !important;
      }
    }

    &:hover {
      color: #00abd5;
      .bdp-icon {
        color: #00abd5;
      }
    }
  }

  .bdp-icon {
    color: #9ab0c6;
  }
}

.theme-blue .web-header {
  background: linear-gradient(
    90deg,
    rgba(28, 36, 80, 1) 0%,
    rgba(19, 31, 77, 1) 100%
  );
  box-shadow: 0px 4px 10px 0px rgba(0, 0, 0, 0.1);
}

.theme-black .web-header {
  background: rgba(57, 62, 68, 1);
  box-shadow: 0px 4px 10px 0px rgba(0, 0, 0, 0.1);
}
</style>

