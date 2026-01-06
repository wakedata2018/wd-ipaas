<template>
  <div class="dss-menu-container">
    <div class="dss-menu" :class="{ 'dss-menu-collapse': isCollapse }">
      <el-scrollbar class="menu-scrollbar" :class="isShowCompany ? 'show-company' : ''">
        <el-menu
          :default-active="activeIndex"
          :router="router"
          :collapse.sync="isCollapse"
          class="el-menu-ver"
          background-color="#3F3F3F"
          active-text-color="#00BCD4"
          @select="select"
        >
          <menu-item
            v-for="menu in finalMenus"
            :key="menu.path"
            :menu="menu"
            :disabled="menu.disabled ? true : false"
            :collapse="isCollapse"
            :active-index="activeIndex"
          ></menu-item>
        </el-menu>
      </el-scrollbar>
      <div v-if="isShowCollapseBtn" class="collapse-icon" @click="isCollapse = !isCollapse">
        <i :class="isCollapse ? 'el-icon-arrow-right' : 'el-icon-arrow-left'"></i>
      </div>
      <div class="company-profile" v-show="isShowCompany">
        <div>深圳惟客数据科技有限公司</div>
        <div>All rights reserved.</div>
      </div>
    </div>
  </div>
</template>

<script>
import MenuItem from './menu-item.vue';
import './style.less';

export default {
  name: 'DssMenu',
  components: { MenuItem },
  props: {
    menus: {
      type: Array,
      required: true,
    },
    defaultActive: {
      type: String,
    },
    // 是否开启router模式，默认true；不开启传false，使用select回调自行处理跳转
    router: {
      type: Boolean,
      default: true,
    },
    collapse: {
      type: Boolean,
      default: null,
    },
    showCollapseBtn: {
      type: Boolean,
      default: false,
    },
    showCompany: {
      type: Boolean,
      default: false,
    },
    routerMap: {
      type: Array,
      default: () => {
        // [{path: [], alias: ''}]
        return [];
      },
    },
    permissionMenus: {
      type: Array,
      default: () => null,
    },
    // 自动判断并跳转无权限的页面。不传则不跳转
    autoRedirectPermissionPage: {
      type: String,
      default: null
    }
  },
  data() {
    return {
      disabled: false,
    };
  },
  computed: {
    // 当前激活菜单的index，默认取$route.path，如传了defaultActive，则取defaultActive
    activeIndex() {
      if (this.defaultActive) {
        return this.defaultActive;
      }
      // console.log('defaultActive', this.routerMap);
      if (this.routerMap && this.routerMap.length === 0) {
        return this.$route.path;
      }
      const path = this.$route.path;
      const mapItem = this.routerMap.find(item => item.path.indexOf(path) > -1);
      // console.log(mapItem);
      return mapItem ? mapItem.alias : path;
    },
    // 是否折叠菜单。如没有传入collapse，表示不开启折叠功能
    isCollapse: {
      get() {
        return this.collapse == null ? false : this.collapse;
      },
      set(val) {
        this.$emit('update:collapse', val);
      },
    },
    // 是否显示折叠按钮，只有当开启折叠功能时才生效
    isShowCollapseBtn() {
      return this.showCollapseBtn && this.collapse != null;
    },
    // 是否显示公司信息
    isShowCompany() {
      return !this.isCollapse && this.showCompany;
    },
    finalMenus() {
      const permissionMenus = this.permissionMenus ? Array.from(this.permissionMenus) : null;
      return this.filterVisibleMenus(this.menus, permissionMenus);
    },
  },
  watch: {
    $route(val) {
      if (!this.autoRedirectPermissionPage) return;

      const { path } = this.$route;
      if (this.permissionMenus && path !== this.autoRedirectPermissionPage) {
        const permit = this.permissionMenus.find(item => item.select !== false && item.uri === path);
        if (!permit) {
          this.$router.push({ path: this.autoRedirectPermissionPage });
        }
      }
    }
  },
  methods: {
    select(index, indexPath) {
      this.$emit('select', index, indexPath);
    },
    filterVisibleMenus(menus, permissionMenus) {
      if (permissionMenus) {
        let result = [];
        menus.forEach(item => {
          let findPerMenu = permissionMenus.find(_item => item.code ? _item.code === item.code : item.uri ? _item.uri === item.uri :  _item.uri === item.path);
          if (findPerMenu) {
            if (!item.children || item.children.length === 0) {
              delete item.children;
            } else {
              item.children = this.filterVisibleMenus(item.children, permissionMenus);
            }
            result.push({
              ...item,
              disabled: item.disabled !== undefined ? item.disabled : findPerMenu.select === false,
            });
          }
        });
        return result;
      } else {
        return menus || [];
      }
    },
  },
};
</script>
