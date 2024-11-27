<template>
  <div class="box">
    <div class="bd-search">
      <el-form size="mini" label-position="right" class="bd-form" inline>
        <el-form-item label="API名称">
          <el-select v-model="dataAssetApiId" filterable @focus="getItem">
            <el-option
              v-for="item in option"
              :key="item.apiName"
              :label="item.apiName"
              :value="item.dataAssetApiId"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div class="bd-search-group">
        <el-button type="primary" @click="onSearch" class="bd-button bd-strong">查询</el-button>
        <el-button type="primary" plain size="mini" class="bd-button bd-strong" @click="onReset">重置</el-button>
      </div>
    </div>
    <div class="call-table">
      <el-table :data="tableData" v-loading="loading.list" style="width: 100%" class="dss-table bd-table">
        <el-table-column prop="primaryName" label="API名称" show-overflow-tooltip></el-table-column>
        <el-table-column prop="secondaryName" label="API Path" show-overflow-tooltip></el-table-column>
        <el-table-column prop="resultValue" label="调用次数"></el-table-column>
        <!-- <el-table-column prop="resultValue" label="错误率">
          <template slot-scope="scope">{{scope.row.resultValue | timesNum}}</template>
        </el-table-column> -->
      </el-table>
      <div style="text-align: right; margin-top: 20px">
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
  import Pager from '../../../bz-components/pager.vue';
  import IPagesize from '../../../mixins/i-pagesize.js';
  import mydata from '../../../api/mydata.js';
  export default {
    components: { Pager },
    mixins: [IPagesize],
    data() {
      return {
        option: [],
        tableData: [],
        tableProp: 'tableData',
        dataAssetApiId: null,
        totalCount: 0,
        loading: {
          list: false,
        },
      };
    },
    created() {
      this.getApiList();
    },
    methods: {
      getApiList() {
        mydata
          .getStatisList({
            ...this.page,
            dataAssetApiId: this.dataAssetApiId,
          })
          .done(res => {
            // console.log(res)
            this.tableData = res.data;
            this.totalCount = res.totalCount;
          });
      },
      getItem() {
        mydata.getItemList().done(res => {
          // console.log(res)
          this.option = res.data;
        });
      },
      onSearch() {
        this.page.pageNo = 1;
        this.getApiList();
      },
      onReset() {
        this.dataAssetApiId = null;
        this.getApiList();
      },
      onSizeChange(val) {
        this.page.pageSize = val;
        this.getApiList();
      },
      onCurrentChange(val) {
        this.page.pageNo = val;
        this.getApiList();
      },
    },
  };
</script>

<style></style>
