<template>
  <div class="bdp-tabs">
    <el-button-group>
      <el-button
        type="primary"
        icon="el-icon-arrow-left"
        :disabled="disabled.left"
        @click="moveLeft"
      ></el-button>
      <el-button type="primary" @click="moveRight" :disabled="disabled.right">
        <i class="el-icon-arrow-right el-icon--right"></i>
      </el-button>
    </el-button-group>
    <div class="tab-header" ref="header" @click="onTabClick">
      <slot :active="value"></slot>
    </div>
    <el-button type="text" @click="$emit('edit', 'add', value)">add</el-button>
  </div>
</template>

<script>
export default {
  props: {
    value: {
      type: String | Number,
      default: 'test'
    },
    tabs: {
      type: Array,
      default() {
        return [];
      }
    }
  },
  data() {
    return {
      offsetX: 0,
      disabled: {
        left: false,
        right: false
      }
    };
  },
  watch: {
    tabs: {
      deep: true,
      handler() {
        setTimeout(() => {
          this.setStatus();
        }, 0);
      }
    }
  },
  mounted() {
    this.setStatus();
    window.addEventListener('resize', this.onResize);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.onResize);
  },
  methods: {
    moveRight() {
      const $header = this.$refs.header;
      $header.scrollLeft += 50;
      this.setStatus();
    },
    moveLeft() {
      const $header = this.$refs.header;
      $header.scrollLeft -= 50;
      this.setStatus();
    },
    setStatus() {
      const { scrollLeft, scrollWidth, clientWidth } = this.$refs.header;
      this.disabled.left = scrollLeft <= 0.00001;
      this.disabled.right = scrollLeft >= scrollWidth - clientWidth;
      // console.log(this.disabled, scrollLeft, scrollWidth, clientWidth);
    },
    onResize() {
      this.setStatus();
    },
    onTabClick(e) {
      const $header = this.$refs.header;
      const { offsetLeft, clientWidth } = e.srcElement;

      const { scrollLeft } = $header;
      const headerWidth = scrollLeft + $header.clientWidth;
      const width = offsetLeft + clientWidth;

      if (scrollLeft > offsetLeft) {
        $header.scrollLeft -= scrollLeft - offsetLeft;
      } else if (width > headerWidth) {
        $header.scrollLeft += width - headerWidth;
      }
    },
    setActive(name) {
      this.$emit('input', name);
    }
  }
};
</script>

<style lang="less">
.bdp-tab-panel {
  display: inline-block;
}

.bdp-tabs {
  overflow: hidden;
  .tab-header {
    display: inline-block;
    max-width: calc(~'100% - 226px');
    overflow: hidden;
    white-space: nowrap;
    position: relative;
  }
}
</style>
