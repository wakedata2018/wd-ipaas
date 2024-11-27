<template>
  <div class="theme-select">
    <div class="title" :title="dataAccessAppName">{{ dataAccessAppName }}</div>
    <div class="theme-list">
      <el-button type="text" class="third-auth-btn" v-if="$route.query.appId" @click="onShowDialog">授权API</el-button>
      <el-scrollbar style="height: 100%">
        <div class="operator">
          <div class="theme-search">
            <el-input
              class="filter-input"
              v-model.trim="keyword"
              suffix-icon="el-icon-search"
              placeholder="搜索"
              maxlength="50"
            ></el-input>
          </div>
          <div
            class="theme-item"
            :class="{ actived: isActive(item.apiId) }"
            v-for="item in filterList"
            :key="item.apiId"
            :title="item.apiName"
            @click="onActive(item.apiId)"
          >
            <div class="card-text card-content">{{ item.apiName }}</div>
          </div>
        </div>
      </el-scrollbar>
    </div>
  </div>
</template>

<script>
  import $ from 'jquery';

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
      return {
        filterList: [],
        keyword: null,
      };
    },
    watch: {
      keyword(val) {
        const arr = Object.assign({}, { array: this.list }).array;
        this.filterList = arr.filter(
          item => !val || (item.apiName || '').toUpperCase().indexOf(val.toUpperCase()) !== -1
        );
      },
      list: {
        immediate: true,
        deep: true,
        handler(val) {
          this.$nextTick(() => {
            this.filterList = Object.assign({}, { array: val }).array;
          });
        },
      },
    },
    computed: {
      dataAccessAppName() {
        return this.$route.query.appName;
      },
    },
    methods: {
      isActive(apiId) {
        return this.value === apiId;
      },
      onActive(apiId) {
        // if (!this.isActive(apiId)) this.$emit('input', apiId);
        // else this.$emit('input', null);
        this.$emit('input', apiId);
      },
      onAddTheme() {
        this.$emit('add-theme');
      },
      onShowDialog() {
        this.$emit('update:addThirdAuthVisable', true);
      },
    },
  };
</script>

<style lang="less" scoped>
  .theme-select {
    width: 140px;

    .title {
      color: #333333;
      line-height: 40px;
      box-sizing: border-box;
      padding-left: 10px;
      border-bottom: 1px solid #e6e6e6;
      position: absolute;
      font-weight: 600;
      font-size: 14px;
      left: 0;
      right: 0;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .theme-list {
      box-sizing: border-box;
      height: 100%;
      padding-top: 41px;
      .third-auth-btn {
        font-size: 14px;
        width: 100%;
        margin: 5px auto 0px auto;
      }
    }

    .operator {
      box-sizing: border-box;
      padding: 0 10px 10px;

      /deep/ .el-button {
        padding: 7px 0;
        width: 100%;
        text-align: center;
        margin-top: 10px;
        border-radius: 0;
      }
    }
    .theme-search {
      margin-top: 10px;
    }
    .theme-item {
      width: 100%;
      text-align: center;
      box-sizing: border-box;
      line-height: 30px;
      overflow: hidden;
      margin-top: 10px;

      padding: 0;
      font-size: 12px;
      background: #f3f6f8;
      color: #333333;
      box-sizing: border-box;
      display: inline-block;
      cursor: pointer;

      &.create {
        background: transparent;
        border: 1px solid #2776fb;
        color: #2776fb;
        padding: 0px;
      }

      &:hover {
        background: rgba(227, 243, 255, 0.5);
      }

      &.actived {
        background: #e3f3ff;
        color: #2776fb;
      }
      &.create {
        background: transparent;
        border: 1px solid #2776fb;
        color: #2776fb;
        padding: 0px;
        &:hover {
          background: rgba(33, 150, 243, 0.2);
        }
      }
      .card-content {
        vertical-align: middle;
        display: inline-block;
        &.card-img {
          // margin: 10px;
          // padding: 10px;
          border-radius: 0px;
          width: 18px;
          height: 18px;
          border: 1px solid #f5f5f5;
          background-size: auto 100% !important;
          background-position-x: 50% !important;
        }
        &.card-text {
          width: 100%;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }
      }
    }
  }
</style>
