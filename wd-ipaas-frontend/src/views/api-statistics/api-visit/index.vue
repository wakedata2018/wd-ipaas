<template>
  <div class="page-api-err bd-page">
    <div class="bd-header">
      <div class="title">{{ title }}</div>
    </div>
    <div class="bd-container">
      <div class="bd-search page-search">
        <el-form size="mini" label-position="right" class="bd-form" inline label-width="100px">
          <el-form-item label="应用名称">
            <el-input v-model="form.appName" placeholder="请输入应用名称" />
            <!-- <el-select v-model="form.appName" filterable @focus="getItem">
              <el-option
                v-for="item in option"
                :key="item.dataAccessAppId"
                :label="item.dataAccessAppName"
                :value="item.dataAccessAppId"
              ></el-option>
            </el-select> -->
          </el-form-item>
          <el-form-item label="调用时间" class="form-item">
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              :picker-options="pickerOptions"
              @change="handleDate"
              @focus="resetDate"
            ></el-date-picker>
            <!-- <el-date-picker
              v-model="dateRange[0]"
              type="date"
              placeholder="开始日期"
              :picker-options="pickerOptions0">
            </el-date-picker>
            <el-date-picker
              v-model="dateRange[1]"
              type="date"
              placeholder="结束日期"
              :picker-options="pickerOptions1">
            </el-date-picker> -->
          </el-form-item>
          <el-form-item label="调用状态" class="form-item">
            <el-select v-model="form.status" placeholder="请选择" clearable>
              <el-option
                v-for="item in statusOptions"
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

      <div class="err-table theme-table">
        <el-table v-loading="loading.list" :data="tableData" style="width: 100%" class="dss-table bd-table">
          <el-table-column prop="appName" label="应用名称" show-overflow-tooltip></el-table-column>
          <el-table-column prop="apiGroup" label="API接口分类" show-overflow-tooltip></el-table-column>
          <el-table-column prop="apiName" label="调用API" show-overflow-tooltip></el-table-column>
          <el-table-column prop="apiInvokeTime" label="调用时间" width="160"></el-table-column>
          <el-table-column prop="takeTime" label="调用耗时(s)" show-overflow-tooltip align="center"></el-table-column>
          <el-table-column prop="status" label="调用状态" show-overflow-tooltip align="center">
            <template #default="scope">
              <span :style="{ color: scope.row.status === '1' ? '#228B22' : '#fb4938' }">{{
                scope.row.status === '1' ? '成功' : '失败'
              }}</span>
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
      <!-- <div class="page">
        <pager
          :page-size.sync="page.pageSize"
          :current-page.sync="page.pageNo"
          :total="total"
          :requestFn="getApiList"
          style="float:right;"
        />
      </div>-->
    </div>
  </div>
</template>

<script>
  import { mapState } from 'vuex';
  import IPagesize from '../../../mixins/i-pagesize.js';
  import accessAPI from '../../../api/app-access-times.js';
  import { date, date as ExDate } from 'dss-common';

  import pageUtils from '@/utils/page.js';

  function getForm() {
    return {
      startTime: '',
      endTime: '',
      status: '',
      appName: null,
    };
  }
  export default {
    mixins: [IPagesize],
    data() {
      return {
        option: [], // API名称
        // appName: null, //选择的API
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
        dateRange: [],
        form: getForm(),
        statusOptions: [
          {
            value: true,
            label: '成功',
          },
          {
            value: false,
            label: '失败',
          },
        ],
        pickerOptions: this.limitDate(),
        curDate: '',
        minDate: '',
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
      this.getApiList();
    },
    methods: {
      handleDate(val) {
        const time1 = date.format(val ? val[0] : null, 'YYYY-MM-DD 00:00:00');
        const time2 = date.format(val ? val[1] : null, 'YYYY-MM-DD 23:59:59');
        this.form.startTime = time1;
        this.form.endTime = time2;
      },
      limitDate() {
        const _this = this;
        return {
          disabledDate(time) {
            if (!_this.minDate) {
              return false;
            }
            const curDate = _this.curDate;
            const three = 90 * 24 * 3600 * 1000;
            const minMonths = curDate - three;
            const maxDate = ExDate.addDays(_this.minDate, 90);
            return !(time.getTime() > minMonths && time.getTime() < maxDate.getTime() && time.getTime() <= Date.now());
          },
          onPick(e) {
            _this.curDate = e.minDate.getTime();
            _this.minDate = e.minDate;
          },
        };
      },
      resetDate() {
        this.minDate = '';
      },
      getApiList() {
        this.loading.list = true;
        accessAPI
          .getAppInvoke(this.page)
          .done(res => {
            this.tableData = res.data;
            this.totalCount = res.totalCount;
          })
          .always(_ => {
            this.loading.list = false;
          });
      },
      getGroup(id) {
        // console.log(id);
      },
      getItem() {
        accessAPI.getAppList().done(res => {
          if (res.data.length === 0) {
            this.option = [];
          }
          this.option = res.data;
        });
      },
      onSearch() {
        this.page.pageNo = 1;
        this.page = Object.assign(this.page, this.form);
        this.getApiList();
      },
      onReset() {
        // this.appName = '';
        this.dateRange = [];
        this.form = getForm();
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
      // handleChange(){
      //   let res=this.option.filter(item=>{
      //     if(item.apiName==this.apiName){
      //       return item
      //     }
      //   })
      //   this.apiGroupId=res[0].apiGroupId
      //   // console.log(this.apiGroupId)
      // }
    },
  };
</script>

<style scoped lang="less">
  .page-search {
    /deep/.el-form-item--mini {
      margin-bottom: 0 !important;
    }
    .form-item {
      margin-bottom: 20px;
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
