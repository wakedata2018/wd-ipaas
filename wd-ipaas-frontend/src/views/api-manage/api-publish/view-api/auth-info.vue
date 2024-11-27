<template>
  <div>
    <el-table :data="table.list" class="dss-table bd-table" v-loading="table.loading">
      <el-table-column prop="accessAppName" label="接入名称"></el-table-column>
      <el-table-column prop="approvalInCharge" label="授权人"></el-table-column>
      <el-table-column prop="approvalTime" label="授权时间"></el-table-column>
      <!-- <el-table-column label="操作" width="100">
        <template slot-scope="scope">
          <el-button @click="onDelete(scope.row)" class="bd-button bd-table-danger">回收</el-button>
        </template>
      </el-table-column> -->
    </el-table>
    <div style="text-align:right;margin-top: 20px;">
      <el-pagination
        layout="total, sizes, prev, pager, next"
        :total="table.totalCount"
        :current-page="params.pageNo"
        :page-size="params.pageSize"
        @size-change="onSizeChange"
        @current-change="onCurrentChange"
      ></el-pagination>
    </div>
    <br/>
  </div>
</template>

<script>
import API from "@/api/api-monitor.js";
export default {
  props: {
    dataAssetId: Number,
    active: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      table: {
        list: [
          {
            name: '惟客宝',
            user: 'admin',
            time: '2020-03-03 11:00:00'
          }
        ],
        totalCount: 0,
        loading: false
      },
      params: {
        pageNo: 1,
        pageSize: 10
      }
    };
  },
  watch: {
    active (val) {
      if (val) {
        this.init();
      }
    }
  },
  methods: {
    init() {
      this.getApproveList();
    },
    getApproveList() {
      this.table.loading = true;
      const params = {
        dataAssetId: this.dataAssetId,
        ...this.params
      };
      API.appApproveInfo(params)
        .done(res => {
          this.table.list = res.data || [];
          this.table.totalCount = res.totalCount || 0;
        })
        .always(() => {
          this.table.loading = false;
        });
    },
    onDelete(row) {
      this.$confirm('确定删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.delete(row);
        })
        .catch(res => {
          // console.log('用户取消操作', res);
        });
    },
    delete(row) {
      this.table.loading = true;
      API.deleteMyApi({ approvalId: row.approvalId })
        .done(res => {
          this.$message.success('删除成功');
          this.onCurrentChange(1);
        })
        .always(() => {
          this.table.loading = false;
        });
    },
    onSizeChange(val) {
      this.page.pageNo = 1;
      this.params.pageSize = val;
      this.getApproveList();
    },
    onCurrentChange(val) {
      this.params.pageNo = val;
      this.getApproveList();
    }
  }
};
</script>

<style>
</style>
