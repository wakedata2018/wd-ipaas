<template>
  <div class="api-select">
    <div class="title">{{ dataAccessAppName }}</div>
    <el-scrollbar v-loading="loading" class="el-scrollbar">
      <div class="api-list">
        <div
          v-for="item in list"
          :key="item.apiId"
          class="api-item"
          :class="{ actived: item.apiId == value }"
          @click="onActive(item.apiId)"
        >
          {{ item.apiName }}
        </div>
      </div>
    </el-scrollbar>
  </div>
</template>

<script>
  export default {
    props: {
      value: {
        type: Number,
        default: 1,
      },
      list: {
        type: Array,
        default() {
          return [];
        },
      },
      loading: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {};
    },
    computed: {
      dataAccessAppName() {
        return this.$route.query.appName;
      },
    },
    methods: {
      onActive(id) {
        this.$emit('input', id);
      },
    },
  };
</script>

<style lang="less" scoped>
  .api-select {
    position: absolute;
    left: 0;
    width: 140px;
    background: #3f3f3f;
    height: 100%;
    padding-top: 70px;
    box-sizing: border-box;
    .el-scrollbar {
      height: 100%;
    }
    .title {
      font-size: 16px;
      font-family: PingFangSC, PingFangSC-Medium;
      font-weight: 500;
      color: #ffffff;
      text-align: center;
    }
    .api-list {
      box-sizing: border-box;
      height: 100%;
      padding-top: 10px;
      margin: 0 auto;
      .api-item {
        margin: 0 6px 10px 6px;
        height: 25px;
        background: #4f4f4f;
        font-size: 14px;
        font-family: PingFangSC, PingFangSC-Regular;
        font-weight: 400;
        color: #ffffff;
        text-align: center;
        line-height: 25px;
        cursor: pointer;
        &.actived {
          background: #2776fb;
        }
      }
    }
  }
</style>
