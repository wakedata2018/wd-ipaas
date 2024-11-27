<template>
  <div class="page-api-err bd-page">
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
        </el-form>
        <div class="bd-search-group">
          <el-button type="primary" class="bd-button bd-strong" @click="onSearch">查询</el-button>
          <el-button type="primary" plain size="mini" class="bd-button bd-strong" @click="onReset">重置</el-button>
        </div>
      </div>

      <div class="err-table theme-table">
        <el-table v-loading="loading.list" :data="tableData" class="dss-table bd-table">
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
          <el-table-column prop="id" label="应用编号" show-overflow-tooltip></el-table-column>
          <el-table-column prop="resultValue" label="错误码"></el-table-column>
          <el-table-column prop="time" label="调用时间" show-overflow-tooltip></el-table-column>
          <el-table-column label="操作" align="center">
            <template #default="scope">
              <el-button class="bd-button bd-table-primary" @click="onDetail(scope.row.id)">查看</el-button>
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
    <error-detail-dialog v-model="currentRowDetail" :visible.sync="isShow" />
  </div>
</template>

<script>
  import { mapState } from 'vuex';
  import SmSlider from '@/bz-components/sm-slider.vue';
  import Pager from '@/bz-components/pager.vue';
  import IPagesize from '../../../mixins/i-pagesize.js';
  import ErrorDetailDialog from './error-detail.-dialog.vue';
  import apiErr from '@/api/api-err.js';
  // import accessAPI from "../../../api/app-access-times.js";
  import { date } from 'dss-common';

  import pageUtils from '@/utils/page.js';

  export default {
    components: { ErrorDetailDialog },
    mixins: [IPagesize],
    data() {
      const { startTime, endTime } = this.initDateTime();
      return {
        option: [], // API名称
        // dataAssetApiId: null, //选择的API
        apiGroupId: null,
        tableData: [],
        totalCount: 0,
        page: {
          pageSize: 10,
          pageNo: 1,
        },
        loading: {
          list: false,
        },
        dateRange: [startTime, endTime],
        form: {
          startTime,
          endTime,
          apiName: null,
        },
        pickerOptions: this.limitDate(),
        isShow: false,
        currentRowDetail: null,
      };
    },
    created() {
      this.getApiList();
    },
    computed: {
      ...mapState({
        permitList: state => state.permitList,
      }),
      title() {
        return pageUtils.findPageName(this.permitList, this.$route.name);
      },
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
        this.loading.list = true;
        const params = {
          ...this.page,
          ...this.form,
        };
        apiErr
          .getErrList(params)
          .done(res => {
            this.tableData = res.data;
            this.totalCount = res.totalCount;
          })
          .always(_ => {
            this.loading.list = false;
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
        // return
        apiErr.getErrList({ id }).done(res => {
          this.currentRowDetail = res.data[0];
          this.isShow = true;
        });
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
  .page-api-err {
    box-sizing: border-box;
    margin-bottom: 50px;
    .err-table {
      background: white;
      padding-bottom: 15px;
    }
  }
</style>
