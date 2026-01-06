<template>
  <component :is="cmpName" :index="menu.path" :disabled="disabled" class="dss-menu-item" popper-class="dss-menu-popup">
    <template v-if="hasChildren">
      <template slot="title">
        <img v-if="isShowIcon" :src="menu.icon" />
        <img v-if="isShowIconActive" :src="menu.icon_active" active />
        <i v-if="isShowIconFont" :class="menu.icon_font" icon-font></i>
        <span>{{ menu.name }}</span>
      </template>
      <menu-item
        v-for="m in menu.children"
        :key="m.path"
        :menu="m"
        :disabled="m.disabled ? true : false"
        :collapse="collapse"
      ></menu-item>
    </template>
    <template v-else>
      <img v-if="isShowIcon" :src="menu.icon" />
      <img v-if="isShowIconActive" :src="menu.icon_active" active />
      <i v-if="isShowIconFont" :class="menu.icon_font" icon-font></i>
      <span>{{ menu.name }}</span>
    </template>
  </component>
</template>

<script>
export default {
  name: 'MenuItem',
  props: {
    menu: {
      type: Object,
      default: () => {},
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    collapse: {
      type: Boolean,
      default: false,
    },
    activeIndex: {
      type: String,
    },
  },
  data() {
    return {};
  },
  computed: {
    hasChildren() {
      const children = this.menu.children;
      return !!(children && children.length > 0);
    },
    cmpName() {
      return this.hasChildren ? 'el-submenu' : 'el-menu-item';
    },
    isActive() {
      return this.menu.path.split('/')[1] === this.activeIndex.split('/')[1];
    },
    isShowIcon() {
      return (!!this.menu.icon && !this.isActive) || (!!this.menu.icon && !this.menu.icon_active);
    },
    isShowIconActive() {
      return (!!this.menu.icon_active && this.isActive) || (!this.menu.icon && !!this.menu.icon_active);
    },
    isShowIconFont() {
      return !!this.menu.icon_font && !this.menu.icon && !this.menu.icon_active;
    },
  },
};
</script>
