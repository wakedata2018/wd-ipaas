<template>
  <div class="source-list">
    <div class="header">数据源列表</div>
    <div class="options">
      <div class="btn">
        <el-button type="primary"
                   size="small"
                   icon="el-icon-plus"
                   @click="newSource">新增数据源</el-button>
      </div>
      <div class="db">
        <span>数据库类型</span>
        <el-select v-model="params.type"
                   placeholder="请选择数据库类型"
                   size="small"
                   clearable
                   v-loading="sourceType.loading">
          <el-option v-for="item in sourceType.list"
                     :key="item.key"
                     :label="item.value"
                     :value="item.key"></el-option>
        </el-select>
      </div>
      <div class="search">
        <el-input placeholder="搜索数据源"
                  v-model.trim="params.name"
                  size="small"
                  clearable
                  @keyup.native.enter="onSearch">
          <i slot="suffix"
             class="el-input__icon el-icon-search icon-search"
             @click="onSearch"></i>
        </el-input>
      </div>
    </div>
    <div class="list">
      <list :list="filterList"
            v-loading="sourceList.loading"
            v-if="filterList.length"
            @edit="onEdit"
            @delete="onDelete"
            @source-selected="onSourceSelected"></list>
      <div class="nodata"
           v-else>暂无数据</div>
    </div>
  </div>
</template>

<script>
export default {
  data () {
    return {
      sourceType: {
        loading: false,
        list: []
      },
      params: {
        type: '',
        name: ''
      },
      sourceList: {
        loading: false,
        list: []
      },
      filterList: [],
      showNewSource: false,
      sourceInfo: {}
    }
  },
  methods: {
    newSource () {
      this.$emit('newSource');
    },
    getSourceList(){},
    onSearch() {
      this.getSourceList();
    },
  }
}
</script>

<style lang="less" scoped>
.source-list {
  width: 300px;
  height: 100%;
  background: #ffffff;
  .header {
    padding-left: 20px;
    line-height: 60px;
    font-size: 16px;
    font-weight: 600;
    border-bottom: 1px solid #e6e6e6;
  }
  .options {
    .btn {
      text-align: center;
      padding: 20px 0;
      .el-button {
        i {
          font-size: 12px;
        }
        span {
          margin-left: 0;
        }
      }
    }
    .db {
      display: flex;
      padding: 0 15px 20px;
      & > span {
        flex: none;
        padding-right: 15px;
        line-height: 32px;
        font-size: 12px;
      }
    }
    .search {
      padding: 0 20px 20px;
      .icon-search {
        font-size: 24px;
        cursor: pointer;
      }
    }
  }
  .list {
    height: calc(100% - 237px);
    padding-bottom: 20px;
    .nodata{
      text-align: center;
    }
  }
}
</style>