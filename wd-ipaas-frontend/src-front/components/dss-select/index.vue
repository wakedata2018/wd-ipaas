<template>
  <div class="dss-select" :class="{ 'dss-select-border': border }">
    <div
      v-for="item in data"
      :key="item[keyProp]"
      class="dss-select-item"
      :class="{ actived: isActive(item), disabled: disabled }"
      @click="onActive(item)"
    >
      {{ item[labelProp] }}
    </div>
  </div>
</template>

<script>
  export default {
    props: {
      value: {
        type: String | Number | Array,
        default: null,
      },
      data: {
        type: Array,
        default() {
          return [];
        },
      },
      keyProp: {
        type: String,
        default: 'value',
      },
      labelProp: {
        type: String,
        default: 'label',
      },
      multiple: {
        type: Boolean,
        default: false,
      },
      border: {
        type: Boolean,
        default: true,
      },
      disabled: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {};
    },
    methods: {
      onActive(item) {
        if (!!this.disabled) return;
        const itemValue = item[this.keyProp];
        if (this.multiple) {
          const index = this.value.findIndex(v => v === itemValue);
          if (index > -1) {
            this.value.splice(index, 1);
          } else {
            this.value.push(itemValue);
          }
          this.$emit('change', this.value);
        } else {
          this.$emit('input', itemValue);
          this.$emit('change', itemValue, item);
        }
      },
      isActive(item) {
        const itemValue = item[this.keyProp];
        if (this.multiple) {
          return this.value.findIndex(v => v === itemValue) > -1;
        }
        return itemValue === this.value;
      },
    },
  };
</script>

<style lang="less" scoped>
  .dss-select {
    &.dss-select-border {
      .dss-select-item {
        border: 1px solid transparent;
        padding: 0 12px;
        background: rgba(243, 246, 248, 1);
        color: #a8a8a8;

        &.actived {
          background: rgba(33, 150, 243, 0.2);
          border: 1px solid rgba(33, 150, 243, 1);
          color: #2776fb;
        }
        &.disabled {
          background: #f5f7fa;
          color: rgb(197, 197, 197);
          cursor: not-allowed;
          &.actived {
            background: rgba(149, 182, 209, 0.2);
            border: 1px solid rgb(195, 204, 212);
            color: #b5bfc7;
          }
        }
      }
    }

    .dss-select-item {
      height: 29px;
      line-height: 29px;
      display: inline-block;
      color: #333333;
      font-size: 12px;
      margin-right: 10px;
      cursor: pointer;
      box-sizing: border-box;
      &.actived {
        color: #2776fb;
      }
      &.disabled {
        background: #f5f7fa;
        color: rgb(197, 197, 197);
        cursor: not-allowed;
        &.actived {
          background: rgba(149, 182, 209, 0.2);
          color: #b5bfc7;
        }
      }
    }
  }
</style>
