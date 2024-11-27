
<template>
<div class="empty-info" :style="customedStyle">
  <i v-if="!!icon" :class="'fa '+icon" aria-hidden="true"></i>
  <div v-else><img src="../assets/images/error1.png"/></div>
  <div class="error-text"><slot></slot></div>
</div>
</template>

<script>
export default {
  props: {
    icon: {
      type: String,
      default: null
    },
    customedStyle: {
      type: String,
      default: null
    }
  }
};
</script>

<style scoped lang="less">
.empty-info {
    display: flex;
    flex-direction: column;
    align-content: stretch;
    justify-content: center;
    height: 100%;
    text-align: center;
    text-shadow: 1px 1px 1px white;
    color: #c8ccd3;
    vertical-align: top;
    &>i {
      font-size: 60px;
    }
    &>div {
      &>img {
        width: 80px;
      }
      
    }
    .error-text {
      display: inline-block;
      word-break:normal; 
      word-wrap:break-word;
      // width: calc(100% - 20px);
      // overflow: hidden;
      padding: 10px;
      font-size: 13px;
    }
}

</style>
