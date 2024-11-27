<template>
  <el-scrollbar>
    <div class="bd-search">
      <el-form size="mini" style="padding-bottom: 4px" label-width="80px" class="bd-form">
        <el-row>
          <el-col :span="8">
            <el-form-item label="表名称">
              <el-input
                v-model.trim="params.table"
                maxlength="50"
                clearable
                @clear="onSearch"
                @keyup.native.enter="onSearch"
              />
            </el-form-item>
          </el-col>
          <el-col :span="16" style="padding-left:10px;">
            <div class="bd-search-group">
              <el-button type="primary" size="mini" class="bd-button bd-strong" @click="onSearch">查询</el-button>
              <el-button
                plain
                type="primary"
                size="mini"
                class="bd-button bd-strong"
                @click="onReset"
              >重置</el-button>
            </div>
          </el-col>
        </el-row>
      </el-form>
    </div>
    <el-table
      v-loading="table.loading"
      :data="table.list"
      style="width: 100%"
      class="dss-table bd-table"
    >
      <el-table-column prop="tblName" label="表名称" show-overflow-tooltip></el-table-column>
      <el-table-column prop="desc" label="描述" show-overflow-tooltip></el-table-column>
      <el-table-column label="操作" width="200">
        <div class="operation" slot-scope="scope">
          <el-button
            class="bd-button bd-table-primary"
            size="mini"
            @click="onDrawer(scope.row, 'detail')"
          >表详情</el-button>
          <el-button
            class="bd-button bd-table-primary"
            size="mini"
            @click="onDrawer(scope.row, 'preview')"
          >预览数据</el-button>
        </div>
      </el-table-column>
    </el-table>
    <div class="bd-pagination">
      <el-pagination
        layout="total, sizes, prev, pager, next"
        :total="table.totalCount"
        :current-page="params.pageNo"
        :page-size="params.pageSize"
        @size-change="onSizeChange"
        @current-change="onCurrentChange"
      />
    </div>
  </el-scrollbar>

</template>

<script>
export default {
  data(){
    return{
      params: {
        table: '',
        pageNo: 1,
        pageSize: 10
      },
      table: {
        list: [],
        totalCount: 0,
        loading: false
      },
      showDrawer: false,
      drawerInfo: {
        type: '',
        params: {
          dataSourceId: null,
          table: ''
        }
      }
    }
  },
  methods:{
    onSearch(){},
    onReset(){},
    onDrawer(){},
    onSizeChange(){},
    onCurrentChange(){}
  }
}
</script>

<style lang="less" scoped>
.el-scrollbar {
  height: 100%;
  /deep/ .el-scrollbar__wrap {
    height: calc(100% + 17px);
  }
}

</style>