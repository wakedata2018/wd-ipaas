<template>
  <div id="app" class="app" :class="'theme-' + theme">
    <DataHeader :is-collapse.sync="isCollapse" class="app-header" :system-info="info"></DataHeader>
    <!-- <sider-bar :is-collapse.sync="isCollapse"></sider-bar> -->
    <dss-menu
      :class="`dss-menu-box ${!showSidemenu ? 'hide' : ''} custom-menu`"
      :style="{ position: 'absolute' }"
      :menus="menus"
      :collapse.sync="isCollapse"
      :router-map="routerMap"
      show-collapse-btn
      @select="selectMenu"
    ></dss-menu>
    <div class="main-container" :class="`${!showSidemenu ? 'full' : ''} ${isCollapse ? 'collapse' : ''}`">
      <el-scrollbar ref="mainScroll" :key="$route.path" class="main-scroll">
        <div class="main">
          <FatConfigurableProvider :value="config">
            <router-view class="app-page"></router-view>
          </FatConfigurableProvider>
        </div>
      </el-scrollbar>
    </div>
  </div>
</template>

<script>
  import DataHeader from './bz-components/data-header-self.vue';
  import ITheme from '@/mixins/i-theme.js';
  import { mapState } from 'vuex';
  import watermark from 'dss-common/lib/watermark';
  import overallEventBus, { overallEventName } from '@/utils/overall-event-bus.js';
  import { FatConfigurableProvider } from '@wakeadmin/components';
  import SettingApi from '@/api/setting';

  export default {
    components: { DataHeader, FatConfigurableProvider },
    mixins: [ITheme],

    data() {
      return {
        isCollapse: false,
        showSidemenu: true,
        routerMap: [
          {
            path: ['/data-application/api-detail'],
            alias: '/data-application/join-application',
          },
          {
            path: ['/data-application/third-auth-api-detail'],
            alias: '/data-application/join-third-application',
          },
          {
            path: ['/data-application/low-code-application-detail'],
            alias: '/data-application/low-code-application',
          },
          {
            path: ['/api-arrange-editor'],
            alias: '/api-publish/apipublish',
          },
          {
            path: ['/api-swagger/swagger/api-import'],
            alias: '/api-swagger/swagger',
          },
        ],
        menus: [],
        config: {
          undefinedPlaceholder: '-',
          pagination: {
            layout: 'total, sizes, prev, pager, next',
          },
          aFilesProps: {
            action: '/dw/open/upload/temp.file',
            name: 'uploadFile',
            accept: ['.json'],
            limit: 1,
          },
          // 统一处理 images 原件上传
          aImageProps: {
            action: '/dw/open/upload/image',
            sizeLimit: 1024 * 5 * 1024,
            // 从
            filter: item => {
              console.log({ item });
              if (item.response) {
                if (!item.response.success) {
                  const message = item.response.msg ?? item.response.errorMessage;
                  throw new Error(message);
                }
                item.url = item.response.data;
              }
            },
          },
        },
        info: {
          systemName: '', // 系统名称
          miniLogoUrl: '', // 系统logo
        },
      };
    },

    computed: {
      ...mapState({
        theme: state => state.theme,
        user: state => state.user,
        permitList: state => state.permitList,
      }),
      content() {
        const { displayName, loginName } = this.user;
        return `${displayName}${loginName}`;
      },
    },
    watch: {
      isCollapse() {
        window.dispatchEvent(new Event('resize'));
      },
      // "$route.path"() {
      //   this.$refs.mainScroll.wrap.scrollTop = 0;
      // },
      // $route(val) {
      //   this.getPagePermission(val, this.permitList);
      // },
    },
    async created() {
      /**
       * 获取系统名称和logo
       */
      await this.getNameAndLogoUrl();
      localStorage.setItem('systemName', this.info.systemName);

      const listName = window.document.title.split('-');
      if (listName[1] !== this.info.systemName) {
        window.document.title = listName[0] + '-' + this.info.systemName;
      }
    },
    async mounted() {
      overallEventBus
        .$off(overallEventName.showOrHideSidemenu)
        .$on(overallEventName.showOrHideSidemenu, this.event_showOrHideSidemenu);
      window
        .$(window)
        .off('resize')
        .on('resize', _ => {
          overallEventBus.$emit(overallEventName.windowResize);
        });
      this.getMenu();
      watermark.init({
        watermark_txt: this.content,
        watermark_width: 200,
        watermark_alpha: 0.08,
      });
    },
    beforeDestroy() {
      overallEventBus.$off(overallEventName.showOrHideSidemenu);
    },
    methods: {
      async getNameAndLogoUrl() {
        const { systemDetailInfo } = await SettingApi.fetchSetting();
        this.info = systemDetailInfo;
      },
      event_showOrHideSidemenu(obj) {
        this.showSidemenu = !!obj.value;
        setTimeout(() => {
          overallEventBus.$emit(overallEventName.windowResize);
        }, 500);
      },
      selectMenu(index) {
        this.$router.push(index);
      },
      getMenu() {
        const loop = filed => {
          return filed
            ? filed
                .filter(item => item.isMenu)
                .map(item => {
                  return {
                    name: item.name,
                    path: item.url,
                    key: item.identifier,
                    children: loop(item.childMenu),
                  };
                })
            : [];
        };
        const menu = this.permitList.map(item => {
          return {
            name: item.name,
            path: item.url,
            key: item.identifier,
            icon_font: item.icon,
            children: loop(item.childMenu),
          };
        });
        this.menus = menu;
      },
    },
  };
</script>

<style lang="less" scoped>
  @import './common.less';
  @import './css/var.less';

  @media screen and (max-width: 750px) {
    html {
      font-size: 100px;
    }
  }

  .dss-menu-box {
    transition: left 0.4s;

    &.hide {
      left: -@sider-bar-width;
    }
  }

  .app {
    @sider-bar-width: 180px;
    @sider-bar-collapse-width: 64px;
    @header-height: 52px;

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
      left: 190px;
      right: 0;
      bottom: 0;
      transition: all 0.4s;
      &.collapse {
        left: @narrow-sider-bar-width;
      }
      &.full {
        left: 0px;
        width: 100%;
      }
      .main-scroll {
        width: 100%;
        transition: all 0.4s;
        height: calc(~'100vh - ' @header-height);
        /deep/ .el-scrollbar__wrap {
          overflow-x: hidden;
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
  /deep/.el-scrollbar__wrap {
    box-sizing: border-box;
    margin: 0;
  }
</style>
