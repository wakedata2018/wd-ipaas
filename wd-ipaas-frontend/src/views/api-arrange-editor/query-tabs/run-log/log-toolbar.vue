<template>
  <div class="log-toolbar">
    <slot></slot>
    <div class="right">
      <i v-show="loading" class="el-icon-loading"></i>
      &nbsp;&nbsp;
      <span class="separator"></span>

      <i class="bar-button el-icon-full-screen" v-if="!isFullscreen" @click="onFull" title="全屏显示"></i>
      <i class="bar-button el-icon-full-screen" v-else @click="onMin" title="向下还原"></i>
    </div>
  </div>
</template>

<script>
  export default {
    props: {
      taskId: {
        type: Number,
        default: null,
      },
      isFullscreen: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        selected: null,
        loading: false,
      };
    },
    watch: {},
    methods: {
      onFull() {
        this.$emit('update:isFullscreen', true);
      },
      onMin() {
        this.$emit('update:isFullscreen', false);
      },
    },
  };
</script>

<style lang="less" scoped>
  .log-toolbar {
    background: white;
    box-shadow: 0px 4px 16px 0px rgba(26, 66, 117, 0.1);
    height: 42px;
    line-height: 40px;
    position: absolute;
    top: 1px;
    left: 0;
    right: 0;
    z-index: 2;
    padding-left: 20px;

    .separator {
      margin: 4px;
      border-left: 1px solid #e9e9e9;
      vertical-align: middle;
    }

    .bar-button {
      box-sizing: border-box;
      width: 27px;
      height: 27px;
      margin: 0px 6px;
      border-radius: 2px;
      padding-left: 4px;
      line-height: 25px;
      display: inline-block;
      border: 1px solid rgba(2, 2, 2, 0);

      &:hover {
        cursor: pointer;
        border: 1px solid #e9e9e9;
      }
    }

    .right {
      display: inline-block;
      float: right;
    }
  }
</style>
