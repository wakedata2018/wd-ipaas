<template>
  <div class="store-select">
    <!-- {{data}} -->
    <div
      class="store-item"
      :class="{actived: item.databaseName === value, 'is-disabled': disabled}"
      v-for="item in data"
      :key="item.id"
      :title="item.databaseName"
      @click="onActive(item)"
    >
      <img class="store-img" :src="imgs[item.databaseName.toLowerCase()]" />

      <template v-if="!!value && item.databaseName.toLowerCase() === value.toLowerCase()">
        <div class="actived">
          <img class="checked" src="../../assets/images/task/check.png" />
        </div>
      </template>
    </div>
  </div>
</template>

<script>
import imgs from "@/utils/datasource-images.js";
export default {
  props: {
    value: {
      type: String | Number,
      default: null
    },
    data: {
      type: Array,
      default() {
        return [];
      }
    },
    disabled: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      imgs
    };
  },
  methods: {
    onActive(item) {
      if (this.disabled) {
        return;
      }
      this.$emit('input', item.databaseName);
      this.$emit('change', item.databaseName);
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
    background: #bbdefb;
    border: 1px solid #f5f5f5;
    box-sizing: content-box;

    &.is-disabled {
      cursor: not-allowed;
    }

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
