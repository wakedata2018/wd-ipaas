<template>
  <div class="api-response-viewer-toolbar">
    <slot></slot>
    <div class="toolbar">
      <div class="right">
        <i v-show="loading" class="el-icon-loading"></i>
        &nbsp;&nbsp;
        <span class="separator"></span>
        <i v-if="!isFullscreen" class="bar-button el-icon-full-screen" title="全屏显示" @click="onFull"></i>
        <i v-else class="bar-button el-icon-full-screen" title="向下还原" @click="onMin"></i>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    props: {
      isFullscreen: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        loading: false,
      };
    },
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
  .api-response-viewer-toolbar {
    position: relative;
    padding-top: 35px;
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
    .toolbar {
      position: absolute;
      top: 0;
      width: 100%;
      height: 30px;
      line-height: 30px;
      box-shadow: 0px 10px 16px rgba(26, 66, 117, 0.1);
      .right {
        float: right;
        margin-right: 15px;
      }
    }
  }
</style>
