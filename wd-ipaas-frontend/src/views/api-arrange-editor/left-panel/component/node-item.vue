<template>
  <div class="preset-node connection-item" :draggable="true" :title="label">
    <!-- {{label}} -->
    <div
      class="card-img card-content"
      :class="`${isDatasource(nodeData)?'full-card-image':''}`"
      :style="{background: `${nodeData.color}`}"
      v-if="!!nodeData.image"
    >
      <div :style="{background: `url(${getIcon(nodeData)})`}"></div>
    </div>
    <div class="card-text card-content">{{ label }}</div>
  </div>
</template>

<script>
import sinkImgs from '@/utils/datasource-images.js';

export default {
  props: {
    label: {
      type: String,
      default: ''
    },
    nodeData: {
      type: Object,
      default: _ => ({})
    }
  },
  methods: {
    getIcon(item) {
      let { isDatasource, dbType } = item;
      
      if (isDatasource && dbType) {
        if (sinkImgs[dbType.toLowerCase()]) {
          return sinkImgs[dbType.toLowerCase()];
        }
      }
      return item.image;
    },
    isDatasource(item) {
      let { isDatasource, dbType } = item;
      if (isDatasource && dbType) {
        if (sinkImgs[dbType.toLowerCase()]) {
          return true;
        }
      }
      return false;
    },
  }
};
</script>

<style lang="scss" scoped>
.preset-node {
  color: #333333;
  box-sizing: border-box;
  padding: 4px 24px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 12px;


  .card-content {
    vertical-align: middle;
    // display: inline-block;
    &.card-img {
      // margin: 10px;
      // padding: 10px;
      // background-color: #bbbfca;
      margin-right: 3px;
      border-radius: 100%;
      width: 16px;
      height: 16px;
      // border: 1px solid #f5f5f5;
      display: inline-block;
      overflow: hidden;
      & > div {
        width: calc(100% - 2 * 3px);
        height: calc(100% - 2 * 3px);
        margin: 3px;
        background-size: auto 100% !important;
        background-position-x: 50% !important;
      }
      &.full-card-image {
        & > div {
        width: 100%;
        height: 100%;
        margin: 0px;
        }
      }
    }
    &.card-text {
      // width: calc(100% - 33px);
      display: inline;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }
}
</style>