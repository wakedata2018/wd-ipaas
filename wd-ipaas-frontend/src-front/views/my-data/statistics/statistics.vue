<template>
  <div class="box my-statistics bd-page">
    <div class="bd-header">
      <div class="title">我的API统计</div>
    </div>
    <div class="bd-container">
      <div class="bd-search">
        <el-form size="mini" label-position="right" class="bd-form" inline>
          <el-form-item label="API名称">
            <el-input type="text" placeholder="请输入API名称" style="width: 200px" v-model.trim="query.keyword" />
          </el-form-item>

          <el-form-item label="应用名">
            <el-select placeholder="请选择应用名" filterable clearable v-model="query.accessAppId">
              <el-option
                v-for="item in appList"
                :key="item.dataAccessAppId"
                :value="item.dataAccessAppId"
                :label="item.dataAccessAppName"
              />
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
          <el-table-column prop="appName" label="应用名"> </el-table-column>
          <el-table-column prop="primaryName" label="API名称" show-overflow-tooltip>
            <template slot-scope="scope">
              <el-button type="text" :title="scope.row.primaryName" @click="onView(scope.row)" class="name"
                >{{ scope.row.primaryName }}
              </el-button>
            </template>
          </el-table-column>
          <el-table-column prop="secondaryName" label="API Path" show-overflow-tooltip></el-table-column>
          <el-table-column prop="resultValue" label="调用次数"></el-table-column>
          <!-- <el-table-column prop="resultValue" label="错误率">
          <template slot-scope="scope">{{scope.row.resultValue | timesNum}}</template>
        </el-table-column>-->
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
  </div>
</template>

<script>
  import IPagesize from '../../../mixins/i-pagesize';
  import mydata from '../../../api/mydata';
  import { mapState } from 'vuex';
  import myApp from '../../../api/my-app';

  export default {
    components: {},
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
        query: {
          keyword: '',
          accessAppId: '',
        },
        appList: [],
      };
    },
    computed: {
      ...mapState({
        cardList: state => state.cardList,
      }),
    },
    props: {
      data: {
        type: String,
        default: null,
      },
      current: {
        type: String,
        default: '',
      },
    },
    // ['data', 'current'],
    watch: {
      data() {
        this.getApiList();
      },
      current(val) {
        if (val === 'statistic') {
          this.getApiList();
        }
      },
    },
    async created() {
      this.appList = await myApp.getAppList();
      this.getApiList();
    },
    methods: {
      getApiList() {
        this.loading.list = true;
        mydata
          .getStatisList({
            ...this.page,
            ...this.query,
            // dataAssetApiId: this.dataAssetApiId,
          })
          .done(res => {
            // console.log(res)
            this.tableData = res.data;
            this.totalCount = res.totalCount;
          })
          .always(() => {
            this.loading.list = false;
          });
      },
      getItem() {
        mydata.getItemList().done(res => {
          // console.log(res);
          this.option = res.data;
        });
      },
      onSearch() {
        this.page.pageNo = 1;
        this.getApiList();
      },
      onReset() {
        this.query.keyword = '';
        this.query.accessAppId = '';
        // this.dataAssetApiId = null;
        this.getApiList();
      },
      onView(val) {
        console.log(val);
        this.$router.push(`/statistics/detail?dataAssetApiId=${val.id}&appId=${val.accessAppId}`);
      },
      onSizeChange(val) {
        this.page.pageSize = val;
        this.getApiList();
      },
      onCurrentChange(val) {
        this.page.pageNo = val;
        this.getApiList();
      },
      getAppName(id) {
        const app = this.appList.find(item => item.dataAccessAppId === id);
        if (app) {
          return app.dataAccessAppName;
        }
        return '';
      },
    },
  };
</script>

<style scoped lang="less">
  .my-statistics {
    box-sizing: border-box;
    padding-top: 80px;
    .call-table {
      background-color: white;
      padding-bottom: 20px;
    }
  }
</style>
