<template>
  <div id="app" class="app" :class="'theme-' + theme">
    <DataHeader
      :is-collapse.sync="isCollapse"
      :canChangeProject="true"
      class="app-header"
    ></DataHeader>
    <dss-menu
      class="dss-menu-box"
      :menus="menus"
      @select="selectMenu"
      :collapse.sync="isCollapse"
      :routerMap="routerMap"
      show-collapse-btn
    ></dss-menu>
    <div class="main-container" :class="isCollapse ? 'collapse' : ''">
      <el-scrollbar ref="mainScroll" :key="$route.path" class="main-scroll">
        <div class="main">
          <router-view class="app-page"></router-view>
        </div>
      </el-scrollbar>
    </div>
  </div>
</template>

<script>
import DataHeader from "./bz-components/data-header.vue";
import ITheme from "@/mixins/i-theme.js";
import { mapState } from "vuex";
import watermark from "dss-common/lib/watermark";
export default {
  mixins: [ITheme],
  components: { DataHeader },
  data() {
    return {
      isCollapse: false,
      menus: [],
      originalMenus: [
        {
          name: "首页",
          key: "front-index",
          path: "/index",
          icon_font: "el-icon-pie-chart",
        },
        {
          name: "我的数据",
          key: "front-mydata",
          path: "/mydata",
          icon_font: "el-icon-tickets",
          children: [
            {
              name: "我的接入",
              key: "front-mydata-join",
              path: "/mydata/join",
            },
            {
              name: "我的申请",
              key: "front-mydata-apply",
              path: "/mydata/apply",
            },
            {
              name: "我的收藏",
              key: "front-mydata-collect",
              path: "/mydata/collect",
            },
            {
              name: "我的API统计",
              key: "front-mydata-statistics",
              path: "/mydata/statistics",
            },
          ],
        },
      ],
      routerMap: [
        { path: ["/index/company", "/index/userdata"], alias: "/index" },
        { path: ["/apply/detail"], alias: "/mydata/apply" },
        { path: ["/collect/detail"], alias: "/mydata/collect" },
        { path: ["/statistics/detail"], alias: "/mydata/statistics" },
      ],
    };
  },
  computed: {
    ...mapState({
      theme: (state) => state.theme,
      user: (state) => state.user,
      permitList: (state) => state.permitList,
    }),
    content() {
      const { displayName, loginName } = this.user;
      return `${displayName}${loginName}`;
    },
  },
  watch: {
    isCollapse() {
      window.dispatchEvent(new Event("resize"));
    },
    $route(val) {
      this.getPagePermission(val, this.permitList);
    },
  },
  mounted() {
    this.getMenu();
    watermark.init({
      watermark_txt: this.content,
      watermark_width: 200,
      watermark_alpha: 0.08,
    });
  },
  methods: {
    selectMenu(index) {
      this.$router.push(index);
    },
    getPagePermission(router, permitList) {
      const { path, name } = router;
      if (name !== "noPermission") {
        if (!!permitList) {
          let finded = permitList.find((item) => item.code === name);
          if (!finded) {
            this.$router.push({ path: "/noPermission" });
          }
        } else {
          this.$router.push({ path: "/noPermission" });
        }
      }
    },
    getMenu() {
      // const primaryMenu = this.permitList.filter((item) => {
      //   return item.pid == 0;
      // });
      const menu = this.originalMenus.map((item) => {
        // item.children = [];
        // item.children = this.permitList.filter(
        //   (subItem) => subItem.pid == item.id
        // );
        // this.permitList.map(node => {
        //   if (node.pname != node.name) {
        //     if (item.pname == node.pname) {
        //       item.children.push(node);
        //     }
        //   }
        // })
        let parentObjIndex = this.permitList.findIndex(
          (primaryItem) => primaryItem.code === item.key
        );
        let parentDisabled =
          parentObjIndex !== undefined &&
          parentObjIndex !== null &&
          parentObjIndex >= 0
            ? false
            : true;
        return {
          name: item.name,
          path: item.path,
          icon_font: item.icon_font,
          key: item.key,
          code: item.key,
          disabled: parentDisabled,
          children: item.children
            ? item.children.map((i) => {
                let childDisabled = parentDisabled;
                if (!parentDisabled) {
                  let childObjIndex = this.permitList.findIndex(
                    (childItem) => childItem.code === i.key
                  );
                  childDisabled =
                    childObjIndex != undefined &&
                    childObjIndex != null &&
                    childObjIndex >= 0
                      ? false
                      : true;
                }

                return {
                  name: i.name,
                  path: i.path,
                  key: i.key,
                  code: i.key,
                  disabled: childDisabled,
                };
              })
            : [],
        };
      });
      this.menus = menu;
    },
  },
};
</script>

<style lang="less" scoped>
@import "./css/var.less";
@media screen and (max-width: 750px) {
  html {
    font-size: 100px;
  }
}
a {
  text-decoration: none;
  color: inherit;
}

.app {
  @sider-bar-width: 180px;
  @sider-bar-collapse-width: 64px;
  @header-height: 52px;

  // // position: relative;
  // height: 100%;

  .sider-bar {
    position: absolute;
    top: @header-height;
    bottom: 0px;
    left: 0px;
    width: @sider-bar-width;
    z-index: 5;
    box-sizing: border-box;

    &.sider--collapse {
      width: @sider-bar-collapse-width;
    }
  }

  .app-header {
    height: @header-height;
    // line-height: @header-height;
  }

  .main-container {
    position: absolute;
    top: @header-height;
    left: @sider-bar-width;
    right: 0;
    bottom: 0;
    transition: all 0.4s;
    background-color: @body-backgroud;
    &.collapse {
      left: @narrow-sider-bar-width;
    }
    /deep/.main-scroll {
      height: calc(~"100vh - " @header-height);
      width: 100%;
      transition: width 0.4s;
      .el-scrollbar__wrap {
        overflow-x: hidden;
      }

      .el-scrollbar__bar {
        z-index: 6;
      }
    }

    &.sider--collapse {
      left: @sider-bar-collapse-width + 30px;
    }

    .main {
      width: 100%;
      height: 100%;
    }
  }
}
// /deep/.el-scrollbar__wrap {
//   box-sizing: border-box;
//   margin: 0;
// }
</style>
