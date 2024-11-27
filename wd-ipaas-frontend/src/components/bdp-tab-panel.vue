<template>
  <div class="bdp-tab-panel" :class="{'is-active': name === active}" @click="onClick">
    <div class="bdp-tab-item">
      <span>{{label}}</span>
      <el-dropdown>
        <span class="el-dropdown-link">
          <i class="el-icon-arrow-down el-icon--right"></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item icon="el-icon-plus">黄金糕</el-dropdown-item>
          <el-dropdown-item icon="el-icon-circle-plus">狮子头</el-dropdown-item>
          <el-dropdown-item icon="el-icon-circle-plus-outline">螺蛳粉</el-dropdown-item>
          <el-dropdown-item icon="el-icon-check">双皮奶</el-dropdown-item>
          <el-dropdown-item icon="el-icon-circle-check-outline">蚵仔煎</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    name: {
      type: String | Number,
      default: ''
    },
    label: {
      type: String,
      default: ''
    }
  },
  computed: {
    active() {
      return this.$parent.value;
    }
  },
  methods: {
    onClick() {
      this.$parent.setActive(this.name);
    }
  }
};
</script>

<style lang="less" scoped>
.bdp-tab-panel {
  padding: 0 1px;

  .bdp-tab-item {
    height: 35px;
    line-height: 35px;
    min-width: 100px;
    max-width: 150px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    background: white;
    border-radius: 8px 8px 0px 0px;
    padding: 0 20px;
    box-sizing: border-box;
    cursor: pointer;
  }

  &.is-active {
    color: white;
    .bdp-tab-item {
      background: #00abd5;
    }
  }
}
</style>
