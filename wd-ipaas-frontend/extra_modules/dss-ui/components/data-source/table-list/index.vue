<template>
  <el-scrollbar>
    <div class="content">
      <div class="bd-search">
        <el-form size="mini" style="padding-bottom: 4px" label-width="80px" class="bd-form">
          <el-row>
            <el-col :span="8">
              <el-form-item label="表名称">
                <el-input
                  v-model.trim="params.tableName"
                  maxlength="50"
                  clearable
                  @clear="onSearch"
                  @keyup.native.enter="onSearch"
                />
              </el-form-item>
            </el-col>
            <el-col :span="16" style="padding-left: 10px">
              <div class="bd-search-group">
                <el-button type="primary" size="mini" class="bd-button bd-strong" @click="onSearch">查询</el-button>
                <el-button plain type="primary" size="mini" class="bd-button bd-strong" @click="onReset"
                  >重置</el-button
                >
              </div>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <el-table v-loading="table.loading" :data="table.list" style="width: 100%" class="dss-table bd-table">
        <el-table-column prop="tblName" label="表名称" show-overflow-tooltip></el-table-column>
        <el-table-column prop="comment" label="描述" show-overflow-tooltip></el-table-column>
        <el-table-column label="操作" width="200">
          <div class="operation" slot-scope="scope">
            <el-button class="bd-button bd-table-primary" size="mini" @click="onDrawer(scope.row, 'detail')"
              >表详情</el-button
            >
            <el-button class="bd-button bd-table-primary" size="mini" @click="onDrawer(scope.row, 'preview')"
              >预览数据</el-button
            >
          </div>
        </el-table-column>
      </el-table>
      <div class="bd-pagination">
        <el-pagination
          layout="prev, pager, next, sizes, total"
          :total="table.totalCount"
          :current-page="params.pageNo"
          :page-size="params.pageSize"
          @size-change="onSizeChange"
          @current-change="onCurrentChange"
        />
      </div>
      <table-drawer :visible.sync="showDrawer" :info="drawerInfo"></table-drawer>
    </div>
  </el-scrollbar>
</template>

<script>
import sourceApi from '../../api/dataSource.js';
import TableDrawer from './table-drawer/index.vue';

export default {
  components: { TableDrawer },
  props: {
    sourceId: Number,
    refresh: Boolean,
  },
  data() {
    return {
      params: {
        tableName: '',
        pageNo: 1,
        pageSize: 10,
      },
      table: {
        list: [],
        totalCount: 0,
        loading: false,
      },
      showDrawer: false,
      drawerInfo: {
        type: '',
        params: {
          dataSourceId: null,
          tableName: '',
        },
      },
    };
  },
  watch: {
    sourceId: {
      handler() {
        this.onReset();
      },
      immediate: true,
    },
    refresh() {
      this.onReset();
    },
  },
  methods: {
    onReset() {
      this.params = {
        table: '',
        pageNo: 1,
        pageSize: 10,
      };
      this.getTableList();
    },
    onSearch() {
      this.onSizeChange(10);
    },
    getTableList() {
      this.table.list = [];
      if (this.sourceId) {
        this.table.loading = true;
        let sourceId = this.sourceId;
        // this.table.totalCount = 0;
        const params = { ...this.params, dataSourceId: sourceId };
        sourceApi
          .queryTables(params)
          .done(
            res => {
              if (this.sourceId === sourceId) {
                this.table.list = res.data || [];
                this.table.totalCount = res.totalCount || 0;
              }
            },
            res => {
              if (this.sourceId === sourceId) {
                this.table.totalCount = 0;
              }
            }
          )
          .always(() => {
            if (this.sourceId === sourceId) {
              this.table.loading = false;
            }
          });
      } else {
        this.table.totalCount = 0;
      }
    },
    onSizeChange(val) {
      this.params.pageSize = val;
      this.onCurrentChange(1);
    },
    onCurrentChange(val) {
      this.params.pageNo = val;
      this.getTableList();
    },
    onDrawer(row, type) {
      this.drawerInfo = {
        type,
        params: {
          dataSourceId: this.sourceId,
          tableName: row.tblName,
        },
      };
      this.showDrawer = true;
    },
  },
};
</script>

<style lang="less" scoped>
@import '../style.less';
.el-scrollbar {
  height: 100%;
  /deep/ .el-scrollbar__wrap {
    height: calc(100% + 17px);
  }
}
.content {
  padding-bottom: 20px;
}
</style>