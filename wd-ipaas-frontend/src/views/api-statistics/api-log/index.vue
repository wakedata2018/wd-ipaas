<template>
  <div class="page-api-log bd-page">
    <div class="bd-header">
      <div class="title">{{ title }}</div>
    </div>
    <div class="bd-container">
      <div class="bd-search page-search">
        <el-form size="mini" label-position="right" class="bd-form" inline label-width="120px">
          <el-form-item label="接口名称/地址">
            <el-input v-model="form.apiName" placeholder="请输入接口名称/地址" />
          </el-form-item>
          <el-form-item label="调用时间">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              :picker-options="pickerOptions"
              @change="handleDate"
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="调用结果">
            <el-select v-model="form.invokeStatus" placeholder="全部数据" clearable>
              <el-option
                v-for="item in LogResultOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-form>
        <div class="bd-search-group">
          <el-button type="primary" class="bd-button bd-strong" @click="onSearch">查询</el-button>
          <el-button type="primary" plain size="mini" class="bd-button bd-strong" @click="onReset">重置</el-button>
        </div>
      </div>

      <div class="log-table theme-table">
        <el-table v-loading="loading" :data="tableData" class="dss-table bd-table">
          <el-table-column
            prop="primaryName"
            label="接口名称"
            min-width="120px"
            show-overflow-tooltip
          ></el-table-column>
          <el-table-column
            prop="secondaryName"
            label="接口地址"
            min-width="140px"
            show-overflow-tooltip
          ></el-table-column>
          <el-table-column prop="groupName" label="接口分类" show-overflow-tooltip></el-table-column>
          <el-table-column prop="appName" label="应用名称" show-overflow-tooltip></el-table-column>
          <el-table-column prop="resultValue" label="调用结果" align="center">
            <template #default="scope">
              <span
                :class="{
                  'dss-status-success': isSuccess(scope.row.resultValue),
                  'dss-status-error': !isSuccess(scope.row.resultValue),
                }"
                >{{ result(scope.row.resultValue) }}</span
              >
            </template>
          </el-table-column>
          <el-table-column prop="time" label="调用时间" show-overflow-tooltip></el-table-column>
          <el-table-column label="操作" align="center">
            <template #default="scope">
              <el-button class="bd-button bd-table-primary" @click="onDetail(scope.row.id)">查看详情</el-button>
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
    <log-detail-dialog v-if="isShow" v-model="id" :visible.sync="isShow" />
  </div>
</template>

<script>
  import { mapState } from 'vuex';

  import LogDetailDialog from './log-detail-dialog.vue';

  import { LOG_RESULT_ENUM, LogResult } from '@/utils/enum';
  import apiLog from '@/api/api-log.js';

  import { date } from 'dss-common';
  import pageUtils from '@/utils/page.js';

  export default {
    components: { LogDetailDialog },
    data() {
      const { startTime, endTime } = this.initDateTime();
      return {
        tableData: [],
        totalCount: 0,
        page: {
          pageSize: 10,
          pageNo: 1,
        },
        loading: false,
        dateRange: [startTime, endTime],
        form: {
          startTime,
          endTime,
          apiName: null,
          invokeStatus: null,
        },
        pickerOptions: this.limitDate(),
        isShow: false,
        LogResultOptions: [
          {
            label: '成功',
            value: LOG_RESULT_ENUM.SUCCESS,
          },
          {
            label: '失败',
            value: LOG_RESULT_ENUM.FAIL,
          },
        ],
        LogResult,
        id: null, // 当前日志详情id
      };
    },
    computed: {
      ...mapState({
        permitList: state => state.permitList,
      }),
      title() {
        return pageUtils.findPageName(this.permitList, this.$route.name);
      },
      result() {
        return val => {
          return val === 200 ? LogResult[LOG_RESULT_ENUM.SUCCESS] : LogResult[LOG_RESULT_ENUM.FAIL];
        };
      },
      isSuccess() {
        return val => {
          return val === 200;
        };
      },
    },
    created() {
      this.getApiList();
    },
    methods: {
      limitDate() {
        return {
          disabledDate(time) {
            return time.getTime() >= Date.now();
          },
        };
      },
      handleDate(val) {
        const time1 = date.format(val ? val[0] : null, 'YYYY-MM-DD 00:00:00');
        const time2 = date.format(val ? val[1] : null, 'YYYY-MM-DD 23:59:59');
        this.form.startTime = time1;
        this.form.endTime = time2;
      },
      getApiList() {
        this.loading = true;
        const params = {
          ...this.page,
          ...this.form,
        };
        apiLog
          .getLogList(params)
          .done(res => {
            this.tableData = res.data;
            this.totalCount = res.totalCount;
          })
          .always(_ => {
            this.loading = false;
          });
      },
      onSearch() {
        this.page.pageNo = 1;
        this.getApiList();
      },
      onReset() {
        const { startTime, endTime } = this.initDateTime();
        this.dateRange = [startTime, endTime];
        this.form = {
          startTime,
          endTime,
          apiName: null,
          invokeStatus: null,
        };
        this.onSearch();
      },
      onSizeChange(val) {
        this.page.pageNo = 1;
        this.page.pageSize = val;
        this.getApiList();
      },
      onCurrentChange(val) {
        this.page.pageNo = val;
        this.getApiList();
      },
      onDetail(id) {
        this.id = id;
        this.isShow = true;
      },
      initDateTime() {
        const startTime = date.format(new Date(), 'YYYY-MM-DD 00:00:00');
        const endTime = date.format(new Date(), 'YYYY-MM-DD 23:59:59');
        return { startTime, endTime };
      },
    },
  };
</script>

<style scoped lang="less">
  .page-search {
    /deep/.el-form-item--mini {
      margin-bottom: 0 !important;
    }
  }
  .page-api-log {
    box-sizing: border-box;
    margin-bottom: 50px;
    .log-table {
      background: white;
      padding-bottom: 15px;
    }
  }
</style>
