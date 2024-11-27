<template>
  <div class="box">
    <div class="assets-table">
      <el-table
        :data="tableData"
        v-loading="loading.list"
        style="width: 100%;"
        class="dss-table bd-table"
      >
        <el-table-column prop="apiName" label="交易代码"></el-table-column>
        <el-table-column prop="dataAssetApiUri" label="交易日期"></el-table-column>
        <el-table-column prop="dataAccessAppName" label="应付账款周转天数"></el-table-column>
        <el-table-column prop="dataAccessAppName" label="应付账款周转率"></el-table-column>
        <el-table-column prop="dataAccessAppName" label="管理费用与营业总数"></el-table-column>
        <el-table-column prop="dataAccessAppName" label="应收账款周转天数"></el-table-column>
        <el-table-column prop="dataAccessAppName" label="应付账款周转率"></el-table-column>
        <el-table-column prop="dataAccessAppName" label="对数总资产"></el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      tableData: [],
      tableProp: 'tableData',
      totalCount: 0,
      loading: {
        list: false
      }
    };
  }
};
</script>

<style>
</style>