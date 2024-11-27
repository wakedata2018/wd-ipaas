<template>
  <div class="my-apply bd-page" style="line-height: normal">
    <div class="bd-header">
      <div class="title">我的申请</div>
    </div>
    <div class="bd-container">
      <div class="bd-search condition">
        <el-form size="mini" label-position="right" class="bd-form" label-width="68px">
          <el-row :gutter="20" style="max-width: 700px">
            <el-col :span="12">
              <el-form-item label="API名称">
                <el-input type="text" placeholder="请输入API名称" v-model.trim="query.keyword" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
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
            </el-col>
          </el-row>

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
          <el-table-column prop="appName" label="应用名">
            <template slot-scope="scope">
              <span>{{ scope.row.app.dataAccessAppName }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="apiName" label="API名称">
            <template slot-scope="scope">
              <el-button
                type="text"
                :title="scope.row.apiName"
                @click="onView(scope.row)"
                class="name"
                v-if="scope.row.status === 'APPROVAL'"
                >{{ scope.row.apiName }}</el-button
              >
              <span v-else>{{ scope.row.apiName }}</span>
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
          <el-table-column label="操作" align="left" width="280">
            <template slot-scope="scope">
              <div class="operation">
                <el-button
                  @click="onTest(scope.row)"
                  v-if="scope.row.status === 'APPROVAL'"
                  class="bd-button bd-table-primary"
                  >测试</el-button
                >
                <el-button
                  @click="onGetToken(scope.row)"
                  v-if="scope.row.status === 'APPROVAL' && scope.row.app.authType === 'TOKEN_AUTH'"
                  class="bd-button bd-table-primary"
                >
                  获取token
                </el-button>
                <el-button @click="onCollect(scope.row)" class="bd-button bd-table-primary">{{
                  scope.row.isCollect ? '取消收藏' : '收藏'
                }}</el-button>
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
  import DssSelect from '../../../components/dss-select/index.vue';
  import ApiTest from './api-test.vue';
  import IPagesize from '../../../mixins/i-pagesize';
  import mydata from '../../../api/mydata';
  import indexApi from '../../../api';
  import { ApprovalStatus } from '../../../enum';
  import { mapState } from 'vuex';
  import myApp from '../../../api/my-app';
  import AppToken from '@/bz-components/app-token.vue';

  export default {
    components: { DssSelect, ApiTest, AppToken },
    mixins: [IPagesize],
    data() {
      return {
        ApprovalStatus,
        tableData: [],
        tableProp: 'tableData',
        totalCount: 0,
        query: {
          keyword: '',
          accessAppId: '',
          statusEnum: '',
        },
        loading: {
          list: false,
        },
        apiInfo: null,
        assetInfo: null,
        appId: null,
        dialog: {
          apitest: false,
          token: false,
        },
        statusArray: [
          { label: '所有', value: '' },
          { label: '待审批', value: 'CREATED' },
          // { label: '已审批', value: 'IN_APPROVAL' },
          { label: '已通过', value: 'APPROVAL' },
          { label: '已拒绝', value: 'FAILURE_APPROVAL' },
        ],
        appList: [],
      };
    },
    props: {
      data: {
        type: String,
        default: null,
      },
      current: {
        type: String,
        default: null,
      },
    },
    watch: {
      data() {
        this.getApplyList();
      },
      current(val) {
        if (val === 'apply') {
          this.getApplyList();
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
      await this.getApplyList();
    },
    methods: {
      getApplyList() {
        this.loading.list = true;
        return mydata
          .getApplyList({
            ...this.query,
            ...this.page,
            approvalType: 'DATA_ACCESS',
          })
          .done(res => {
            this.tableData = res.data.map(item => {
              const app = this.appList.find(apItem => apItem.dataAccessAppId === item.accessAppId);
              item.app = app || {};
              return item;
            });
            this.totalCount = res.totalCount;
          })
          .always(() => {
            this.loading.list = false;
          });
      },
      onSearch() {
        this.page.pageNo = 1;
        this.getApplyList();
      },
      onReset() {
        this.query.keyword = '';
        this.query.statusEnum = '';
        this.query.accessAppId = '';
        this.page.pageNo = 1;
        this.onSearch();
      },
      onView(val) {
        this.cardList.forEach(item => {
          if (item.apiName === val.apiName) {
            val.dataAssetApiId = item.dataAssetApiId;
          }
        });
        this.$router.push(`/apply/detail?dataAssetApiId=${val.dataAssetApiId}&appId=${val.accessAppId}`);
      },
      onDelete(val) {
        this.$confirm('确定删除该API吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          // center: true
        })
          .then(() => {
            this.delete(val);
          })
          .catch(res => {
            console.log('用户取消操作', res);
          });
      },
      delete(val) {
        this.cardList.forEach(item => {
          if (item.apiName === val.apiName) {
            val.dataAssetApiId = item.dataAssetApiId;
          }
        });
        // console.log(val);
        mydata.deleteMyApply({ approvalId: val.approvalId }).done(() => {
          // console.log(res);
          this.$message({
            type: 'success',
            message: '删除成功',
          });
          this.getApplyList();
        });
      },
      onTest(val) {
        const app = this.appList.find(item => item.dataAccessAppId === val.accessAppId);
        if (!app) {
          this.$alert('您还没有接入秘钥，请先在我的数据-我的接入中申请接入秘钥!', '提示', {
            confirmButtonText: '确定',
            type: 'warning',
          });
          return;
        }

        if (app.status === 'CREATED') {
          this.$alert('您的接入还没有审批!', '提示', {
            confirmButtonText: '确定',
            type: 'warning',
          });
          return;
        }
        this.apiInfo = val;
        this.dialog.apitest = true;
      },
      onCollect(row) {
        if (!row.isCollect) {
          indexApi
            .addCollect({
              dataAccessAppId: row.accessAppId,
              dataAssetApiId: row.dataAssetApiId,
            })
            .done(() => {
              this.$message({
                type: 'success',
                message: '收藏成功',
              });
              this.getApplyList();
            });
        } else {
          this.$confirm('确定取消该收藏吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
            // center: true
          })
            .then(() => {
              indexApi.deleteCollect({ appId: row.accessAppId, collectId: row.dataAssetApiId }).done(() => {
                this.$message({
                  type: 'success',
                  message: '取消收藏成功',
                });
                this.getApplyList();
              });
            })
            .catch(res => {
              console.log('用户取消操作', res);
            });
        }
      },
      onSizeChange(val) {
        this.page.pageSize = val;
        this.getApplyList();
      },
      onCurrentChange(val) {
        this.page.pageNo = val;
        this.getApplyList();
      },
      getAppName(row) {
        const app = this.appList.find(item => item.dataAccessAppId === row.accessAppId);
        if (app) {
          row.dataAccessKey = app.dataAccessKey;
          return app.dataAccessAppName;
        }
        return '';
      },
      onGetToken(row) {
        const app = this.appList.find(item => item.dataAccessAppId === row.accessAppId);
        if (!app) {
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
  .my-apply {
    box-sizing: border-box;
    padding-top: 80px;
    .assets-table {
      background-color: white;
      padding-bottom: 20px;
    }
  }
</style>
