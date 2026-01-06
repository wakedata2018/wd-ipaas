<template>
  <div class="source-list" v-loading="loading || sourceList.loading">
    <div class="header">数据源列表</div>
    <div class="options">
      <div class="btn">
        <el-button type="primary" size="small" icon="el-icon-plus" @click="newSource">新增数据源</el-button>
      </div>
      <div class="db">
        <span>数据库类型</span>
        <el-select
          v-model="params.type"
          placeholder="请选择数据库类型"
          size="small"
          clearable
          filterable
          v-loading="sourceType.loading"
        >
          <el-option
            v-for="item in sourceType.list"
            :key="item.dataSourceType"
            :label="item.displayName"
            :value="item.dataSourceType"
          ></el-option>
        </el-select>
      </div>
      <div class="search">
        <el-input
          placeholder="搜索数据源"
          v-model.trim="params.name"
          size="small"
          clearable
          @keyup.native.enter="onSearch"
        >
          <i slot="suffix" class="el-input__icon el-icon-search icon-search" @click="onSearch"></i>
        </el-input>
      </div>
    </div>
    <div class="list">
      <list
        ref="sourceList"
        :list="filterList"
        :typeList="typeList"
        v-loading="sourceList.loading"
        v-if="filterList.length"
        @edit="onEdit"
        @delete="onDelete"
        @source-selected="onSourceSelected"
      ></list>
      <div class="nodata" v-else>暂无数据</div>
    </div>
    <new-source :visible.sync="showNewSource" :info="sourceInfo" @save="onSave"></new-source>
  </div>
</template>

<script>
import List from './list/index.vue';
import NewSource from './new-source/index.vue';
import dataSourceApi from '../../api/dataSource.js';
import store from '../store.js';
import { rsa } from 'dss-common';

export default {
  components: { List, NewSource },
  inject: ['enableEncrypt'],
  data() {
    return {
      loading: false,
      sourceType: {
        loading: false,
        list: [],
      },
      params: {
        type: '',
        name: '',
      },
      sourceList: {
        loading: false,
        list: [],
      },
      filterList: [],
      showNewSource: false,
      sourceInfo: {},
      hasPublicKey: false,
      typeList: [],
    };
  },
  watch: {
    'params.type'(val) {
      this.params.name = '';
      this.onSearch();
    },
    'params.name'(val) {
      // this.onFilter(val);
      if (!val) {
        this.onSearch();
      }
    },
  },
  provide() {
    return {
      getHasPublicKey: this.getHasPublicKey,
    };
  },
  created() {
    this.getPublicKey();
    this.getSourceType();
  },
  methods: {
    getPublicKey() {
      if (!this.enableEncrypt) return;
      this.loading = true;
      rsa.setPublicKey().always(() => {
        this.loading = false;
        this.hasPublicKey = rsa.hasPublicKey();
      });
    },
    getHasPublicKey() {
      return this.hasPublicKey;
    },
    getSourceType() {
      this.sourceType.loading = true;
      dataSourceApi
        .queryDataSourceType()
        .done(res => {
          this.sourceType.list = res.data || [];
          this.typeList = this.sourceType.list;
          this.getSourceList();
          store.setSourceTypeAction(this.sourceType.list);
        })
        .always(() => {
          this.sourceType.loading = false;
        });
    },
    newSource() {
      this.sourceInfo = {};
      this.showNewSource = true;
    },
    onFilter(val) {
      this.filterList = this.sourceList.list.filter(item => {
        const name = item.name || '';
        return name.includes(val);
      });
    },
    onSearch() {
      this.getSourceList();
    },
    getSourceList() {
      this.sourceList.loading = true;
      dataSourceApi
        .queryDataSource(this.params)
        .done(res => {
          this.sourceList.list = res.data || [];
          this.filterList = this.sourceList.list;
        })
        .always(() => {
          this.sourceList.loading = false;
        });
    },
    onEdit(item) {
      this.sourceInfo = item;
      this.showNewSource = true;
    },
    onDelete(item) {
      this.$confirm(`确认删除${item.dataSourceName}数据源吗？`, `提示`, {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(() => {
          this.delete(item);
        })
        .catch(() => {});
    },
    delete(item) {
      dataSourceApi.deleteDataSource(item.id).done(res => {
        // let { errorMessage, success } = res;
        // if (success) {
        this.$message.success('删除成功');
        // } else {
        //   this.$confirm(
        //     `<div style="font-size: 15px;">当前数据源存在引用，请删除相关引用后再删除！</div><div style="font-size: 12px;color:#FF7F56;background:#FFF2EE;margin-top:5px;">其中，引用该数据源的平台：离线开发、实施开发、数据质量平台。</div>`,
        //     '提示',
        //     {
        //       dangerouslyUseHTMLString: true,
        //       confirmButtonText: '确定',
        //       cancelButtonText: '取消',
        //       type: 'warning',
        //     }
        //   )
        //     .then(() => {})
        //     .catch(() => {});
        // }
        this.onSearch();
        this.$refs.sourceList.afterDelete(item);
      });
    },
    onSave() {
      this.params.type = '';
      this.params.name = '';
      this.onSearch();
      this.$emit('refresh');
    },
    onSourceSelected(item) {
      this.$emit('source-selected', item);
    },
  },
};
</script>
<style lang="less" scoped>
@import '../style.less';

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
  }
}
</style>