<template>
  <div class="bd-page my-collect">
    <div class="bd-header">
      <div class="title">我的收藏</div>
    </div>
    <div class="bd-container">
      <div class="bd-search">
        <el-form size="mini" label-position="right" class="bd-form" inline>
          <el-form-item label="API名称">
            <!-- <el-select
              v-model="dataAssetApiId"
              filterable
              placeholder="请选择"
              @focus="getItem"
            >
              <el-option
                v-for="item in option"
                :key="item.apiName"
                :label="item.apiName"
                :value="item.dataAssetApiId"
              ></el-option>
            </el-select> -->
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
      <div class="publish-table">
        <el-table :data="tableData" style="width: 100%" class="dss-table bd-table" v-loading="loading.list">
          <el-table-column prop="accessAppName" label="应用名"> </el-table-column>
          <el-table-column prop="apiName" label="API名称">
            <template slot-scope="scope">
              <el-button type="text" :title="scope.row.apiName" @click="onView(scope.row)" class="name">
                {{ scope.row.apiName }}
              </el-button>
            </template>
          </el-table-column>
          <el-table-column prop="dataAssetApiMethod" label="API Path"> </el-table-column>
          <el-table-column prop="createTime" show-overflow-tooltip label="收藏时间"></el-table-column>

          <el-table-column label="操作" align="left" width="220">
            <template slot-scope="scope">
              <div class="operation">
                <el-button
                  @click="onTest(scope.row)"
                  v-if="scope.row.approvalStatus === 'APPROVAL'"
                  class="bd-button bd-table-primary"
                  >测试</el-button
                >
                <el-button
                  @click="onGetToken(scope.row)"
                  v-if="scope.row.approvalStatus === 'APPROVAL' && scope.row.app.authType === 'TOKEN_AUTH'"
                  class="bd-button bd-table-primary"
                >
                  获取token
                </el-button>
                <el-button @click="onDelete(scope.row)" class="bd-button bd-table-danger">删除</el-button>
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
    <api-test :api-info="apiInfo" :visible.sync="dialog.apitest" ref="dialogF" />
    <app-token :api-info="apiInfo" :visible.sync="dialog.token" />
  </div>
</template>

<script>
  import IPagesize from '../../../mixins/i-pagesize';
  import mydata from '../../../api/mydata';
  import indexApi from '../../../api';
  import ApiTest from '../apply/api-test.vue';
  import { APIStatus } from '../../../enum';
  import { mapState } from 'vuex';
  import myApp from '../../../api/my-app';
  import AppToken from '@/bz-components/app-token.vue';

  export default {
    components: { ApiTest, AppToken },
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
        apiInfo: {},
        query: {
          keyword: '',
          accessAppId: '',
        },
        dialog: {
          apitest: false,
          token: false,
        },
        appList: [],
      };
    },
    props: {
      current: {
        type: String,
        default: null,
      },
      data: {
        type: String,
        default: null,
      },
    },
    watch: {
      data() {
        this.getCollectList();
      },
      current(val) {
        if (val === 'collect') {
          this.getCollectList();
        }
      },
    },
    computed: {
      ...mapState({
        cardList: state => state.cardList,
        dataAccessApp: state => state.dataAccessApp || [],
      }),
    },
    async created() {
      this.appList = await myApp.getAppList();
      // if (this.appList.length === 0) {
      //   return;
      // }
      // this.query.accessAppId = this.appList[0].dataAccessAppId;
      this.getCollectList();
    },
    methods: {
      getCollectList() {
        this.loading.list = true;
        mydata
          .getCollectList({
            ...this.page,
            ...this.query,
            // dataAssetId: this.dataAssetApiId,
          })
          .done(res => {
            this.tableData = res.data.map(item => {
              const app = this.appList.find(apItem => apItem.dataAccessAppId + '' === item.accessAppId + '');
              item.dataAssetApiUri = item.dataAssetApiMethod;
              item.app = app || {};
              return item;
            });
            this.totalCount = res.totalCount;
            // console.log(res);
          })
          .always(() => {
            this.loading.list = false;
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
            console.log(val);
            indexApi.deleteCollect({ appId: val.accessAppId, collectId: val.dataAssetApiId }).done(() => {
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
          // console.log(res);
          this.option = res.data;
        });
      },
      onSearch() {
        this.getCollectList();
      },
      onReset() {
        this.query.keyword = '';
        // this.query.accessAppId = '';
        // this.dataAssetApiId = null;
        this.getCollectList();
      },
      onView(val) {
        this.cardList.forEach(item => {
          if (item.apiName === val.apiName) {
            val.dataAssetApiId = item.dataAssetApiId;
          }
        });
        this.$router.push(`/collect/detail?dataAssetApiId=${val.dataAssetApiId}&appId=${val.accessAppId}`);
      },
      onTest(val) {
        if (this.dataAccessApp.length === 0) {
          this.$confirm('您还没有接入秘钥，请先在我的数据-我的接入中申请接入秘钥!', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
            // center: true
          })
            .then(res => {
              console.log('用户取消操作', res);
            })
            .catch(res => {
              console.log('用户取消操作', res);
            });
        } else {
          if (this.dataAccessApp[0].status === 'CREATED') {
            this.$confirm('您的接入还没有审批!', '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning',
              // center: true
            })
              .then(res => {
                console.log('用户取消操作', res);
              })
              .catch(res => {
                console.log('用户取消操作', res);
              });
          } else {
            this.apiInfo = val;
            this.dialog.apitest = true;
          }
        }
      },
      onSizeChange(val) {
        this.page.pageSize = val;
        this.getCollectList();
      },
      onCurrentChange(val) {
        this.page.pageNo = val;
        this.getCollectList();
      },
      getAppName(id) {
        const app = this.appList.find(item => item.dataAccessAppId === id);
        if (app) {
          return app.dataAccessAppName;
        }
        return '';
      },
      onGetToken(row) {
        if (!row.app || !row.app.dataAccessAppId) {
          this.$message('没有对应的应用信息。');
          return;
        }
        this.apiInfo = row;
        this.dialog.token = true;
      },
    },
  };
</script>

<style scoped lang="less">
  .my-collect {
    box-sizing: border-box;
    padding-top: 80px;
    .publish-table {
      background-color: white;
      padding-bottom: 20px;
    }
  }
</style>
