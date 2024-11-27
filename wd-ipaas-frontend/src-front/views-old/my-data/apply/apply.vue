<template>
  <div class="page-data-source bd-page">
    <div class="bd-search condition">
      <el-form size="mini" label-position="right" class="bd-form" inline label-width="68px">
        <!-- <el-form-item label="接入名称">
          <el-input type="text" placeholder="申请人" v-model.trim="query.keyword" />
        </el-form-item>-->
        <br />
        <el-form-item label="审批状态">
          <dss-select v-model="query.statusEnum" :data="statusArray"></dss-select>
        </el-form-item>
      </el-form>
      <div class="bd-search-group">
        <el-button type="primary" @click="onSearch" class="bd-button bd-strong">查询</el-button>
        <el-button type="primary" plain size="mini" class="bd-button bd-strong" @click="onReset">重置</el-button>
      </div>
    </div>
    <div class="assets-table">
      <el-table :data="tableData" v-loading="loading.list" style="width: 100%" class="dss-table bd-table">
        <el-table-column prop="apiName" label="API名称">
          <template slot-scope="scope">
            <el-button type="text" :title="scope.row.apiName" @click="onView(scope.row)" class="name">{{
              scope.row.apiName
            }}</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="dataAssetApiUri" label="API Path"></el-table-column>
        <el-table-column prop="submitTime" label="申请日期"></el-table-column>
        <el-table-column prop="status" label="申请状态" align="center">
          <template slot-scope="scope">
            <span :style="{ color: ApprovalStatus.toColor(scope.row.status) }">{{
              ApprovalStatus.getDesc(scope.row.status)
            }}</span>
          </template>
        </el-table-column>
        <!-- <el-table-column label="操作" align="left" width="220">
          <template slot-scope="scope">
            <div class="operation">
              <el-button @click="onTest(scope.row)" class="bd-button bd-table-primary">测试</el-button>
              <el-button @click="onDelete(scope.row)" class="bd-button bd-table-danger">删除</el-button>
            </div>
          </template>
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
    <!-- <UpdateApply :appInfo="appInfo" :visible.sync="dialog.app" @saved="onSaved"></UpdateApply> -->
    <!-- <apply-dialog :visible.sync="dialog.apply" :asset-info="assetInfo" :app-id="appId" /> -->
  </div>
</template>

<script>
  import DssSelect from '../../../components/dss-select/index.vue';
  import Pager from '../../../bz-components/pager.vue';
  import UpdateApply from './update-apply.vue';
  import ApplyDialog from './apply-dialog.vue';
  import IPagesize from '../../../mixins/i-pagesize.js';
  import mydata from '../../../api/mydata.js';
  import { ApprovalStatus } from '../../../enum.js';
  export default {
    components: { DssSelect, Pager, UpdateApply, ApplyDialog },
    mixins: [IPagesize],
    data() {
      return {
        ApprovalStatus,
        tableData: [],
        tableProp: 'tableData',
        totalCount: 0,
        query: {
          keyword: '',
          statusEnum: '',
        },
        loading: {
          list: false,
        },
        appInfo: null,
        assetInfo: null,
        appId: null,
        dialog: {
          app: false,
          apply: false,
        },
        statusArray: [
          { label: '所有', value: '' },
          { label: '待审批', value: 'CREATED' },
          // { label: '已审批', value: 'IN_APPROVAL' },
          { label: '已通过', value: 'APPROVAL' },
          { label: '已拒绝', value: 'FAILURE_APPROVAL' },
        ],
      };
    },
    created() {
      this.getApplyList();
    },
    methods: {
      getApplyList() {
        mydata
          .getApplyList({
            ...this.query,
            ...this.page,
            approvalType: 'DATA_ACCESS',
          })
          .done(res => {
            this.tableData = res.data;
            this.totalCount = res.totalCount;
            // console.log(res);
          });
      },
      onSearch() {
        this.page.pageNo = 1;
        this.getApplyList();
      },
      onReset() {
        this.query.keyword = '';
        this.query.statusEnum = '';
        this.page.pageNo = 1;
        this.onSearch();
      },
      // onApply(val){
      //   this.assetInfo = val;
      //   this.dialog.apply = true;
      //   this.appId=val.approvalId
      //   console.log(this.appId)
      // },
      // onAmend(val){
      //   this.appInfo=val;
      //   console.log(this.appInfo)
      //   this.dialog.app=true
      //   console.log(this.dialog.app)
      // },
      onDelete(val) {
        this.$confirm('确定删除该接入吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          // center: true
        })
          .then(() => {
            // console.log(val)
          })
          .catch(res => {
            console.log('用户取消操作', res);
          });
      },
      onView(item) {
        // console.log(item)
      },
      onSizeChange(val) {
        this.page.pageSize = val;
        this.getApplyList();
      },
      onCurrentChange(val) {
        this.page.pageNo = val;
        this.getApplyList();
      },
    },
  };
</script>

<style></style>
