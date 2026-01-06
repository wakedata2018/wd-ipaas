
<template>
<div class="empty-info">
  <i v-if="!!icon" :class="'fa '+icon" aria-hidden="true"></i>
  <div v-else><img :style="{width: type==='empty'?'80px':'184px'}" :src="src"/></div>
  <div class="error-text"><slot></slot></div>
</div>
</template>

<script>
import empty from "./images/empty.png";
import error from "./images/error.png";
export default {
  name: 'DssEmptyInfo',
  props: {
    icon: {
      type: String,
      default: null
    },
    // style: {
    //   type: String,
    //   default: null
    // },
    type: {
      type: String,
      default: 'error'
    }
  },
  computed: {
    src() {
      switch(this.type) {
        case 'empty':
          return empty;
        default:
          return error;
      }

    }
  }
};
</script>

<style scoped lang="less">
@import './style.less';
</style>
