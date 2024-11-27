<template>
  <div class="theme-select">
    <div class="title">数据分类</div>
    <div class="theme-list">
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
          <el-tree
            v-if="list.length > 0"
            :data="list"
            node-key="id"
            highlight-current
            ref="vuetree"
            :filter-node-method="filterNode"
            :expand-on-click-node="false"
            :default-expanded-keys="[-1]"
            :props="defaultProps"
            @node-click="handleNodeClick"
          >
            <span class="custom-tree-node" slot-scope="{ node, data }">
              <span class="data-name" :title="data.groupName">{{ data.groupName }}</span>
            </span>
          </el-tree>
          <div class="no-data" v-else>暂无数据</div>
        </div>
      </el-scrollbar>
    </div>
  </div>
</template>

<script>
  export default {
    props: {
      value: {
        type: [Number, String],
      },
      list: {
        type: Array,
        default() {
          return [];
        },
      },
    },
    data() {
      return {
        filterList: [],
        keyword: null,
        defaultProps: {
          children: 'children',
          label: 'groupName',
        },
        id: null,
      };
    },
    watch: {
      keyword(val) {
        this.$refs.vuetree.filter(val);
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
    methods: {
      filterNode(value, data) {
        if (!value) return true;
        return data.groupName.indexOf(value) !== -1;
      },
      isActive(gid) {
        return this.value === gid;
      },
      handleNodeClick(data) {
        if (data) {
          this.id = data.id;
          this.$emit('input', this.id, data.path);
        } else {
          this.$emit('input', this.id);
        }
      },
      onAddTheme() {
        this.$emit('add-theme');
      },
    },
  };
</script>

<style lang="less" scoped>
  .theme-select {
    width: 100%;
    height: 100%;
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
    }

    .theme-list {
      box-sizing: border-box;
      height: 100%;
      padding-top: 41px;
    }

    .operator {
      box-sizing: border-box;
      padding: 0 10px 10px;
      /deep/.el-tree {
        .custom-tree-node {
          width: calc(100%);
          padding-right: 20px;
          white-space: nowrap;
          font-size: 12px;
          display: flex;
          align-items: center;
        }
        .el-tree-node__content {
          font-size: 12px;
          .el-tree-node__label {
            width: 100%;
            overflow: hidden;
            white-space: nowrap;
            text-overflow: ellipsis;
          }
        }
        .data-name {
          width: 100%;
          text-align: left;
          white-space: nowrap;
          display: inline-block;
          overflow: hidden;
          text-overflow: ellipsis;
        }
      }
      /deep/ .el-button {
        padding: 7px 0;
        width: 100%;
        text-align: center;
        margin-top: 10px;
        border-radius: 0;
      }
      .no-data {
        text-align: center;
        margin-top: 10px;
        color: #a8a8a8;
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
