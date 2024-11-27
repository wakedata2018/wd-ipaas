<template>
  <div class="bd-page page-data-source">

      <SearchCard :query.sync="query" @search="onSearch" />
      <div class="app-table">
        <el-table :data="tableData" v-loading="loading.list" class="dss-table bd-table">
          <el-table-column prop="dataAccessAppName" label="接入名称"></el-table-column>
          <el-table-column prop="dataAccessKey" label="接入KEY"></el-table-column>
          <el-table-column prop="dataAccessDescription" label="描述"></el-table-column>
          <el-table-column prop="inCharge" label="申请人" align="center" width="80"></el-table-column>
          <el-table-column prop="createTime" label="接入时间" width="140"></el-table-column>
        </el-table>
        <div style="text-align:right;margin-top: 20px;">
          <el-pagination
            layout="total, sizes, prev, pager, next"
            :total="totalCount"
            :current-page="page.pageNo"
            :page-size="page.pageSize"
            @size-change="onSizeChange"
            @current-change="onCurrentChange"
          ></el-pagination>
        </div>
      </div>

  </div>
</template>

<script>
import Pager from "../../../bz-components/pager.vue";
import SearchCard from "../../../bz-components/search-card.vue";
import IPagesize from "../../../mixins/i-pagesize.js";
import daaAPI from "../../../api/data-access-app.js";
export default {
  components: { Pager, SearchCard },
  mixins: [IPagesize],
  data() {
    return {
      tableData: [],
      totalCount: 0,
      query: {
        keyword: '',
        statusEnum: ''
      },
      loading: {
        list: false
      },
    };
  },
  created(){
      this.getAppList()
  },
  methods: {
    getAppList() {
      this.loading.list = true;
      daaAPI
        .getList({
          ...this.page,
          ...this.form
        })
        .done(res => {
          this.tableData = res.data;
          this.totalCount = res.totalCount;
        })
        .always(() => {
          this.loading.list = false;
        });
    },
    onSearch(val) {
      this.loading.list = true;
      this.page.pageNo = 1;
      this.form = { ...this.form, ...val };
      this.getAppList();
    },
    onSizeChange(val) {
      this.page.pageSize = val;
      this.getAppList();
    },
    onCurrentChange(val) {
      this.page.pageNo = val;
      this.getAppList();
    }
  }
};
</script>

<style>
</style>