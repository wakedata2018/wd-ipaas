<template>
  <div class="bd-page">
    <div class="bd-search">
      <el-form size="mini" label-position="right" class="bd-form" inline>
        <el-form-item label="接口">
          <el-select v-model="dataAssetApiId" filterable placeholder="请选择" @focus="getItem">
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
    <div class="publish-table">
      <el-table :data="tableData" style="width: 100%" class="dss-table bd-table">
        <el-table-column prop="apiName" label="API名称">
          <template slot-scope="scope">
            <el-button type="text" :title="scope.row.apiName" @click="onView(scope.row)" class="name">{{
              scope.row.apiName
            }}</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="dataAssetName" label="资产表名称"></el-table-column>
        <el-table-column prop="apiDescription" show-overflow-tooltip label="API描述"></el-table-column>
        <el-table-column prop="dataAssetApiMethod" label="API Path">
          <!-- <template
                  slot-scope="scope"
          >{{isPublish(scope.row.dataAssetPublishStatus)? scope.row.dataAssetApiMethod:''}}</template>-->
        </el-table-column>
        <el-table-column prop="inCharge" label="发布人"></el-table-column>
        <el-table-column prop="updateTime" label="发布时间">
          <template slot-scope="scope">{{
            isPublish(scope.row.dataAssetPublishStatus) ? scope.row.updateTime : ''
          }}</template>
        </el-table-column>
        <el-table-column prop="dataAssetPublishStatus" label="发布状态" align="center" width="120">
          <template slot-scope="scope">
            <div :style="{ color: getColor(scope.row.dataAssetPublishStatus) }">
              {{ getStatus(scope.row.dataAssetPublishStatus) }}
            </div>
          </template>
        </el-table-column>
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
  import { APIStatus } from '@/enum.js';
  export default {
    components: { Pager },
    mixins: [IPagesize],
    data() {
      return {
        option: [],
        dataAssetApiId: null,
        tableData: [],
        tableProp: 'tableData',
        totalCount: 0,
        loading: {
          list: false,
        },
      };
    },
    created() {
      this.getCollectList();
    },
    methods: {
      getCollectList() {
        mydata
          .getCollectList({
            ...this.page,
            dataAssetId: this.dataAssetApiId,
          })
          .done(res => {
            this.tableData = res.data;
            this.totalCount = res.totalCount;
            // console.log(res);
          });
      },
      onDelete(val) {
        this.$confirm('确定取消该收藏吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          // center: true
        })
          .then(() => {
            mydata.deleteCollect({ collectId: val.dataAssetApiId }).done(res => {
              this.$message({
                type: 'success',
                message: '取消收藏成功',
              });
              this.getCollectList();
            });
          })
          .catch(res => {
            console.log('用户取消操作', res);
          });
      },
      isPublish(status) {
        return APIStatus.getPublish().value === status;
      },
      getColor(status) {
        return APIStatus.toColor(status);
      },
      getStatus(status) {
        return APIStatus.getDesc(status);
      },
      getItem() {
        mydata.getItemList().done(res => {
          // console.log(res)
          this.option = res.data;
        });
      },
      onSearch() {
        this.getCollectList();
      },
      onReset() {
        this.dataAssetApiId = null;
        this.getCollectList();
      },
      onView(item) {
        // console.log(item)
        // this.$router.push(
        //   `/index/userdata?dataAssetApiId=${item.dataAssetApiId}&collected=${item.collected}&approval=${item.approval}&created=${item.created}`
        // )
      },
      onSizeChange(val) {
        this.page.pageSize = val;
        //   this.getList();
      },
      onCurrentChange(val) {
        this.page.pageNo = val;
        //   this.getList();
      },
    },
  };
</script>

<style></style>
