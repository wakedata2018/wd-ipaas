<template>
  <div class="doc-content-menu">
    <el-breadcrumb separator="/" class="catalog">
      <el-breadcrumb-item class="uncheck">目录</el-breadcrumb-item>
      <el-breadcrumb-item class="check ellipsis" :title="curActiveName">{{ curActiveName }}</el-breadcrumb-item>
    </el-breadcrumb>
    <el-scrollbar class="menu-list">
      <el-menu :default-active="active" text-color="#606266" active-text-color="#1890FF" @select="onSelectMenu" router>
        <el-submenu v-for="menu in menuList" :key="menu.groupCode" :index="String(menu.id)">
          <template slot="title">{{ menu.groupName }}</template>
          <el-menu-item
            v-for="child in menu.apiList"
            :key="child.dataAssetApiId"
            :index="String(child.dataAssetApiId)"
            :title="child.apiName"
            >{{ child.apiName }}</el-menu-item
          >
        </el-submenu>
      </el-menu>
    </el-scrollbar>
  </div>
</template>
<script>
  export default {
    name: 'SideMenu',
    props: {
      menuList: {
        type: Array,
        default: [],
      },
      active: {
        type: String,
        default: '',
      },
    },
    computed: {
      /**
       * 当前激活页名称
       */
      curActiveName() {
        let name = '';
        this.menuList?.forEach(item => {
          item.apiList?.forEach(field => {
            if (String(field.dataAssetApiId) === this.active) {
              name = field.apiName;
            }
          });
        });
        return name;
      },
    },
    methods: {
      onSelectMenu(index) {
        const { path } = this.$route;
        if (path === index) {
          window.location.reload();
        }
      },
    },
  };
</script>
<style lang="less" scoped>
  .ellipsis {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    vertical-align: middle;
  }

  .doc-content-menu {
    width: 312px;
    position: fixed;
    top: 56px;
    padding-left: 20px;

    ::v-deep .menu-list {
      height: calc(100vh - 126px);
      margin-top: 16px;
      .el-scrollbar__wrap {
        overflow-x: hidden;
      }

      .is-horizontal {
        display: none;
      }
    }

    ::v-deep .el-menu {
      border-right: none;
    }

    ::v-deep .el-submenu__title {
      color: #303133;
      font-weight: 500;
      padding-left: 0px !important;
    }

    ::v-deep .el-menu-item {
      padding-left: 16px !important;
    }

    ::v-deep .el-submenu__title,
    .el-menu-item {
      height: 40px;
      line-height: 40px;
      .ellipsis();
    }

    ::v-deep .el-submenu__title:hover {
      background: #fff;
    }

    ::v-deep .el-menu-item:focus,
    .el-menu-item:hover,
    .el-menu-item.is-active {
      background: rgba(24, 144, 255, 0.06);
      color: #1890ff !important;
    }

    .catalog {
      padding: 16px 0px;
      height: 54px;
      box-shadow: inset 0 -1px 0 0 rgba(220, 223, 230, 0.5);
      display: flex;
      white-space: nowrap;

      ::v-deep .el-breadcrumb__item {
        white-space: nowrap;
      }

      ::v-deep .uncheck {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }

      ::v-deep .check {
        span {
          font-size: 16px;
          color: #909399;
        }
      }
    }
  }
</style>
