<template>
  <div class="store-select">
    <!-- {{data}} -->
    <div
      class="store-item"
      :class="{actived: item.dataSourceType === value}"
      :style="svgs.includes(item.dataSourceType) ? 'background: none' : ''"
      v-for="item in data"
      :key="item.dataSourceType"
      :title="item.dataSourceType"
      @click="onActive(item)"
    >
      <img
        class="store-img"
        :style="item.dataSourceType === 'file' ? 'width: 52px' : ''"
        v-if="item.dataSourceIconMid"
        :src="item.dataSourceIconMid"
      />
      <span v-else>{{item.displayName || item.dataSourceType}}</span>
      <template v-if="item.dataSourceType === value">
        <div class="actived">
          <img class="checked" src="../../../../images/data-source/check.png" />
        </div>
      </template>
    </div>
  </div>
</template>

<script>
import imgs from '../../../sourceTypeImages.js';
export default {
  props: {
    value: {
      type: String || Number,
      default: null
    },
    data: {
      type: Array,
      default() {
        return [];
      }
    }
  },
  data() {
    return {
      imgs,
      svgs: ['file'
      // , 'sybasease', 'sybaseiq'
      ]
    };
  },
  // mounted() {
  //   console.log("this.data:",this.data);
  // },
  methods: {
    onActive(item) {
      this.$emit('input', item.dataSourceType);
      this.$emit('type-change', item);
    }
  }
};
</script>

<style lang="less" scoped>
.store-select {
  margin-bottom: -20px;

  .store-item {
    position: relative;
    cursor: pointer;
    margin-bottom: 20px;
    margin-right: 20px;
    width: 120px;
    height: 80px;
    display: inline-block;
    text-align: center;
    line-height: 80px;
    border: 1px solid #f5f5f5;
    box-sizing: content-box;
    vertical-align: middle;

    .store-img {
      width: 120px;
      height: 80px;
    }

    .actived {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(33, 150, 243, 0.69);
    }

    .checked {
      position: absolute;
      width: 26px;
      height: 26px;
      right: 6px;
      bottom: 6px;
    }
  }
}
</style>
