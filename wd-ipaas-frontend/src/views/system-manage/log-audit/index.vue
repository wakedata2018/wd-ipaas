<template>
  <div class="bd-page">
    <div class="bd-header">
      <div class="title">{{ title }}</div>
    </div>
    <div class="bd-container">
      <div class="bd-search">
        <el-form
          ref="form"
          size="mini"
          label-position="right"
          class="bd-form"
          label-width="100px"
          inline
          :model="query"
        >
          <el-form-item label="操作用户">
            <el-input v-model="query.actionUser" />
            <!-- <el-select v-model="query.actionUser" filterable placeholder="请选择操作用户" clearable @clear="onSearch">
              <el-option
                v-for="(item, index) in operatorList"
                :key="item + index"
                :label="item"
                :value="item"
              ></el-option>
            </el-select> -->
          </el-form-item>
          <el-form-item label="所属模块">
            <el-select v-model="query.pageResource" filterable placeholder="请选择所属模块" clearable @clear="onSearch">
              <el-option v-for="(item, index) in menuList" :key="item + index" :label="item" :value="item"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="操作时间">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              :picker-options="pickerOptions"
              @change="handleDate"
            ></el-date-picker>
          </el-form-item>
        </el-form>
        <div class="bd-search-group">
          <el-button type="primary" class="bd-button bd-strong" @click="onSearch">查询</el-button>
          <el-button type="primary" plain size="mini" class="bd-button bd-strong" @click="onReset">重置</el-button>
        </div>
      </div>

      <div class="log-table theme-table">
        <el-table v-loading="loading" :data="tableData" style="width: 100%" class="dss-table bd-table">
          <el-table-column prop="actionTime" label="操作时间" width="180px"></el-table-column>
          <el-table-column prop="pageResource" label="所属模块" show-overflow-tooltip></el-table-column>
          <el-table-column prop="pageEvent" label="事件类型" show-overflow-tooltip></el-table-column>
          <el-table-column prop="requestUrl" label="接口地址" show-overflow-tooltip></el-table-column>
          <el-table-column prop="actionUser" label="操作用户" width="150px" show-overflow-tooltip></el-table-column>
          <el-table-column prop="ip" label="IP地址" width="150px"></el-table-column>
          <el-table-column label="操作" align="center" width="150px">
            <template #default="scope">
              <el-button class="bd-button bd-table-primary" @click="onDetail(scope.row)">查看</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="bd-pagination">
          <el-pagination
            layout="total, sizes, prev, pager, next"
            :total="totalCount"
            :current-page="requestParams.pageNo"
            :page-size="requestParams.pageSize"
            @size-change="onSizeChange"
            @current-change="onCurrentChange"
          ></el-pagination>
        </div>
      </div>
      <log-detail-dialog v-model="currentRowDetail" :visible.sync="isShow" />
    </div>
  </div>
</template>

<script>
  import { mapState } from 'vuex';
  import reportApi from '@/api/report.js';
  import { date } from 'dss-common';
  import LogDetailDialog from './detail';
  import pageUtils from '@/utils/page.js';

  function getQuery() {
    return {
      actionUser: '',
      startTime: null,
      endTime: null,
      pageResource: '',
    };
  }
  export default {
    components: { LogDetailDialog },
    data() {
      return {
        query: getQuery(),
        tableData: [],
        loading: false,
        requestParams: {
          pageSize: 10,
          pageNo: 1,
        },
        dateRange: [],
        totalCount: 0,
        menuList: [],
        operatorList: [],
        pickerOptions: this.limitDate(),
        currentRowDetail: null,
        isShow: false,
      };
    },
    computed: {
      ...mapState({
        permitList: state => state.permitList,
      }),
      title() {
        return pageUtils.findPageName(this.permitList, this.$route.name);
      },
    },
    created() {
      this.getMenu();
      // this.getOperatorList();
    },
    mounted() {
      this.getList();
    },
    methods: {
      limitDate() {
        return {
          disabledDate(time) {
            return time.getTime() >= Date.now();
          },
        };
      },
      getList() {
        this.loading = true;
        reportApi
          .queryReport(this.requestParams)
          .done(res => {
            this.tableData = res.data;
            this.totalCount = res.totalCount;
          })
          .always(_ => {
            this.loading = false;
          });
      },
      getMenu() {
        reportApi.queryMenu().done(res => {
          this.menuList = res.data;
        });
      },
      getOperatorList() {
        reportApi.queryOperator().done(res => {
          this.operatorList = res.data;
        });
      },
      onSearch() {
        this.requestParams.pageNo = 1;
        this.requestParams = Object.assign(this.requestParams, this.query);
        this.getList();
      },
      handleDate(val) {
        const time1 = date.format(val ? val[0] : null, 'YYYY-MM-DD 00:00:00');
        const time2 = date.format(val ? val[1] : null, 'YYYY-MM-DD 23:59:59');
        this.query.startTime = time1;
        this.query.endTime = time2;
      },
      onReset() {
        this.$refs.form.resetFields();
        this.query = getQuery();
        this.dateRange = [];
        this.onSearch();
      },
      onSizeChange(val) {
        this.requestParams.pageNo = 1;
        this.requestParams.pageSize = val;
        this.getList();
      },
      onCurrentChange(val) {
        this.requestParams.pageNo = val;
        this.getList();
      },
      onDetail(row) {
        reportApi.queryLogDetail({ id: row.id }).done(res => {
          this.currentRowDetail = res.data;
          this.isShow = true;
        });
      },
    },
  };
</script>

<style lang="less" scoped>
  .bd-container {
    background: white;
    position: relative;
    .log-table {
      background: white;
      padding-bottom: 20px;
    }
  }
</style>
