<template>
  <div class="bd-page">
    <div class="bd-header">
      <div class="title">{{ title }}</div>
      <div class="header-right">
        <el-button type="primary" @click="onAdd">新增流量控制</el-button>
      </div>
    </div>
    <div class="bd-container">
      <div class="bd-search">
        <el-form size="mini" label-position="right" class="bd-form" label-width="100px" inline>
          <el-form-item label="接口分类">
            <el-select v-model="query.groupId" filterable @change="changeApiGroupList">
              <el-option
                v-for="item in apiGroupList"
                :key="item.id"
                style="height: auto"
                :label="item.groupName"
                :value="item.id"
              >
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="API名称">
            <el-select v-model="query.apiKeyWord" filterable>
              <el-option v-for="item in option" :key="item.apiName" :label="item.apiName" :value="item.dataAssetApiId">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
        <div class="bd-search-group">
          <el-button type="primary" class="bd-button bd-strong" @click="onSearch">查询</el-button>
          <el-button type="primary" plain size="mini" class="bd-button bd-strong" @click="onReset">重置</el-button>
        </div>
      </div>

      <div class="flow-table theme-table">
        <el-table v-loading="loading" :data="tableData" style="width: 100%" class="dss-table bd-table">
          <el-table-column prop="ruleName" label="规则名称" show-overflow-tooltip>
            <template #default="scope">
              <el-button type="text" :title="scope.row.ruleName" class="name" @click="onView(scope.row)">{{
                scope.row.ruleName
              }}</el-button>
            </template>
          </el-table-column>
          <el-table-column prop="apiGroupName" label="API接口分类" show-overflow-tooltip></el-table-column>
          <el-table-column prop="apiName" label="API名称" show-overflow-tooltip></el-table-column>
          <el-table-column prop="apiPath" label="API Path"></el-table-column>
          <!-- <el-table-column prop="memory" label="内存"></el-table-column> -->
          <el-table-column prop="dayLimit" label="日调用次数"></el-table-column>
          <el-table-column prop="monthLimit" label="月调用次数"></el-table-column>
          <el-table-column prop="totalLimit" label="总调用次数">
            <template #default="{ row }">
              {{ row.totalLimit || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="qps" label="每秒最大访问次数"></el-table-column>
          <el-table-column prop="ttl" label="API缓存失效时间"></el-table-column>
          <el-table-column label="操作" align="center" width="200px">
            <template #default="scope">
              <el-button type="text" class="bd-button bd-table-primary" @click="onEdit(scope.row)">编辑</el-button>
              <el-button type="text" class="bd-button bd-table-danger" @click="onDelete(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div style="text-align: right; margin-top: 20px">
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
    </div>
    <flow-dialog :flow-info="flowInfo" :visible.sync="dialog.edit" @saved="onSave"></flow-dialog>
    <flow-detail-dialog :flow-info="flowInfo" :visible.sync="dialog.view"></flow-detail-dialog>
  </div>
</template>

<script>
  import { mapState } from 'vuex';
  import flowDialog from './flow-dialog';
  import warnApi from '@/api/warn.js';
  import flowDetailDialog from './flow-detail-dialog';
  // import accessAPI from '@/api/app-access-times.js';
  import ruleApi from '@/api/rule.js';
  import apiGroup from '@/api/api-group.js';
  import pageUtils from '@/utils/page.js';
  function getQuery() {
    return {
      groupId: null,
      apiKeyWord: '',
    };
  }
  export default {
    components: { flowDialog, flowDetailDialog },
    data() {
      return {
        tableData: [],
        loading: false,
        query: getQuery(),
        requestParams: {
          pageSize: 10,
          pageNo: 1,
        },
        apiGroupList: [], // API接口分类
        totalCount: 0,
        dialog: {
          edit: false,
          view: false,
        },
        option: [], // API名称
        flowInfo: null,
        groupName: '',
        defaultProps: {
          children: 'children',
          label: 'groupName',
        },
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
    mounted() {
      this.getList();
      this.getApiGroupList();
    },
    methods: {
      getList() {
        this.loading = true;
        ruleApi
          .query({ ...this.requestParams })
          .done(res => {
            this.tableData = res.data;
            this.totalCount = res.totalCount;
          })
          .always(() => {
            this.loading = false;
          });
      },
      getApiGroupList() {
        apiGroup.getGroupList().done(res => {
          this.apiGroupList = res.data;
        });
      },
      changeApiGroupList() {
        this.query.apiKeyWord = '';
        this.option = [];
        if (this.query.groupId) {
          const groupId = this.query.groupId;
          warnApi.queryApi({ groupId }).done(res => {
            this.option = res.data;
          });
        }
      },
      changeApiGroup() {
        const res = this.$refs.tree.getCurrentNode();
        if (res) {
          this.groupName = res.groupName;
          this.query.groupId = res.id;
        }
      },
      onSave() {
        this.getList();
      },
      onAdd() {
        this.flowInfo = null;
        this.dialog.edit = true;
      },
      onSearch() {
        this.requestParams.pageNo = 1;
        this.requestParams = Object.assign(this.requestParams, this.query);
        this.getList();
      },
      onReset() {
        this.groupName = '';
        this.option = [];
        this.query = getQuery();
        this.onSearch();
      },
      onEdit(flowInfo) {
        this.dialog.edit = true;
        this.flowInfo = flowInfo;
      },
      onView(flowInfo) {
        this.dialog.view = true;
        this.flowInfo = flowInfo;
      },
      onDelete(flowInfo) {
        this.$confirm('确定删除该流量控制吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
          .then(() => {
            this.delete(flowInfo);
          })
          .catch(res => {
            console.log('用户取消操作', res);
          });
      },
      delete(flowInfo) {
        this.loading = true;
        ruleApi
          .delete({ apiRuleId: flowInfo.id })
          .done(res => {
            this.getList();
            this.$message.success('删除成功');
          })
          .always(() => {
            this.loading = false;
          });
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
    },
  };
</script>

<style lang="less" scoped>
  .tree-option {
    padding: 0px !important;
  }

  .bd-container {
    background: white;
    position: relative;

    .flow-table {
      background: white;
      padding-bottom: 20px;
    }
  }
</style>
