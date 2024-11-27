<template>
  <div class="page-api-call bd-page">
    <div class="bd-header">
      <div class="title">{{ title }}</div>
    </div>
    <div class="bd-container">
      <div class="bd-search page-search">
        <el-form size="mini" label-position="right" class="bd-form" inline label-width="100px">
          <el-form-item label="API名称">
            <el-input v-model="form.apiName" placeholder="请输入API名称" />
            <!-- <el-select v-model="form.apiName" filterable @focus="getItem">
              <el-option
                v-for="item in option"
                :key="item.apiName"
                :label="item.apiName"
                :value="item.dataAssetApiId"
              ></el-option>
            </el-select> -->
          </el-form-item>
          <el-form-item label="统计时间">
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

      <div class="call-table theme-table">
        <el-table v-loading="loading.list" :data="tableData" style="width: 100%" class="dss-table bd-table">
          <el-table-column prop="groupName" label="API接口分类" show-overflow-tooltip></el-table-column>
          <el-table-column prop="secondaryName" label="API Path" show-overflow-tooltip></el-table-column>
          <el-table-column prop="primaryName" label="API名称" show-overflow-tooltip></el-table-column>
          <el-table-column label="调用次数" show-overflow-tooltip>
            <template #default="scope">{{ scope.row.resultValue + scope.row.errorValue }}</template>
          </el-table-column>
          <el-table-column prop="takeTime" label="平均调用耗时(s)" show-overflow-tooltip></el-table-column>
          <el-table-column label="成功率">
            <template #default="scope">{{ getSuccessRate(scope.row) }}</template>
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
  import IPagesize from '../../../mixins/i-pagesize.js';
  import accessAPI from '../../../api/app-access-times.js';
  import { mapState } from 'vuex';
  import pageUtils from '@/utils/page.js';
  import { date } from 'dss-common';

  export default {
    filters: {
      timesNum(value) {
        if (value) {
          return value + '%';
        } else {
          return '';
        }
      },
    },
    mixins: [IPagesize],
    data() {
      return {
        option: [], // API名称
        // dataAssetApiId: '', //API名称
        apiGroupId: '', // 选择的API名称id
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
        form: {
          startTime: '',
          endTime: '',
          apiName: '',
        },
        pickerOptions: this.limitDate(),
        curDate: '',
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
      limitDate() {
        return {
          disabledDate(time) {
            return time.getTime() >= Date.now();
          },
        };
      },
      getSuccessRate(row) {
        const success = row.resultValue;
        const total = row.resultValue + row.errorValue;
        return total && success ? ((success * 100) / total).toFixed(2) + '%' : '0';
      },
      handleDate(val) {
        const time1 = date.format(val ? val[0] : null, 'YYYY-MM-DD 00:00:00');
        const time2 = date.format(val ? val[1] : null, 'YYYY-MM-DD 23:59:59');
        this.form.startTime = time1;
        this.form.endTime = time2;
      },
      getApiList() {
        this.loading.list = true;
        accessAPI
          .accessTimes(this.page)
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
        accessAPI.getItemList().done(res => {
          this.option = res.data;
          // console.log(res)
        });
      },
      onSearch() {
        this.page.pageNo = 1;
        this.page = Object.assign(this.page, this.form);
        this.getApiList();
      },
      onReset() {
        // this.dataAssetApiId = '';
        this.dateRange = [];
        this.form = {
          startTime: '',
          endTime: '',
          apiName: '',
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
      // handleChange(){
      //   // let res=this.option.filter(item=>{
      //   //   if(item.apiName==this.apiName){
      //   //     return item
      //   //   }
      //   // })
      //   // this.apiGroupId=res[0].apiGroupId
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
  }
  .page-api-call {
    box-sizing: border-box;
    margin-bottom: 50px;
    .call-table {
      background: white;
      padding-bottom: 15px;
    }
  }
</style>
