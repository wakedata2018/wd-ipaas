<template>
  <div class="bd-page">
    <div class="bd-header">
      <div class="title">{{ title }}</div>
      <div class="header-right">
        <el-button type="primary" @click="onAdd">新增监控告警</el-button>
      </div>
    </div>
    <div class="bd-container">
      <div class="bd-search">
        <el-form size="mini" label-position="right" class="bd-form" label-width="100px" inline>
          <el-form-item label="告警名称">
            <el-input v-model.trim="query.warnName" style="width: 180px" placeholder="请输入告警名称" />
          </el-form-item>
          <el-form-item label="接口分类">
            <el-select
              v-model="query.apiGroupId"
              style="width: 180px"
              filterable
              @focus="getApiGroupList"
              @change="changeApiGroupList"
            >
              <el-option
                v-for="item in apiGroupList"
                :key="item.id"
                :label="item.groupName"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="API名称">
            <el-select v-model="query.apiName" style="width: 180px" filterable @focus="getApiNameList">
              <el-option
                v-for="item in option"
                :key="item.apiName"
                :label="item.apiName"
                :value="item.apiName"
              ></el-option>
            </el-select>
          </el-form-item>
          <br />
          <!-- <el-form-item label="手机">
            <el-input style="width:180px" placeholder="请输入手机" v-model.trim="query.phone" />
          </el-form-item> -->
          <el-form-item label="邮箱">
            <el-input v-model.trim="query.email" style="width: 180px" placeholder="请输入邮箱" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="query.status" style="width: 180px" filterable>
              <el-option
                v-for="item in stateList"
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

      <div class="alarm-table theme-table">
        <el-table v-loading="loading" :data="tableData" style="width: 100%" class="dss-table bd-table">
          <el-table-column prop="apiWarnName" label="告警名称">
            <template #default="scope">
              <el-button type="text" :title="scope.row.apiWarnName" class="name" @click="onView(scope.row)">{{
                scope.row.apiWarnName
              }}</el-button>
            </template>
          </el-table-column>
          <el-table-column prop="apiGroup" label="API接口分类" show-overflow-tooltip></el-table-column>
          <el-table-column prop="apiName" label="API名称" show-overflow-tooltip></el-table-column>
          <el-table-column prop="apiMethod" label="API Path"></el-table-column>
          <el-table-column prop="status" label="状态" align="center">
            <template #default="scope">
              <span :class="{ 'dss-status-success': scope.row.status, 'dss-status-error': !scope.row.status }">{{
                scope.row.status === true ? '启用' : '未启用'
              }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="email" label="邮件列表"></el-table-column>
          <!-- <el-table-column prop="phone" label="手机号码"></el-table-column> -->
          <el-table-column label="操作" width="250px" align="center">
            <template #default="scope">
              <el-button
                v-if="scope.row.status === false"
                type="text"
                class="bd-button bd-table-primary"
                @click="onEdit(scope.row)"
                >编辑</el-button
              >
              <el-button
                v-if="scope.row.status === false"
                type="text"
                class="bd-button bd-table-primary"
                @click="onStart(scope.row)"
                >启用</el-button
              >
              <el-button v-else type="text" class="bd-button bd-table-primary" @click="onStop(scope.row)"
                >停用</el-button
              >
              <el-button
                v-if="scope.row.status === false"
                type="text"
                class="bd-button bd-table-danger"
                @click="onDelete(scope.row)"
                >删除</el-button
              >
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
    </div>
    <alarm-dialog :alarm-info="alarmInfo" :visible.sync="dialog.edit" @saved="onSave"></alarm-dialog>
    <alarm-detail-dialog :alarm-info="alarmInfo" :visible.sync="dialog.view"></alarm-detail-dialog>
  </div>
</template>

<script>
  import { mapState } from 'vuex';
  import alarmDialog from './alarm-dialog';
  import alarmDetailDialog from './alarm-detail-dialog';
  import warnApi from '@/api/warn.js';
  // import accessAPI from "@/api/app-access-times.js";
  import pageUtils from '@/utils/page.js';

  function getQuery() {
    return {
      warnName: '',
      apiWarnId: null,
      apiGroupId: null,
      apiName: '',
      phone: null,
      email: null,
      status: null,
    };
  }
  export default {
    components: { alarmDialog, alarmDetailDialog },
    data() {
      return {
        dialog: {
          edit: false,
          view: false,
        },
        query: getQuery(),
        requestParams: {
          pageSize: 10,
          pageNo: 1,
        },
        totalCount: 0,
        apiGroupList: [], // API接口分类
        option: [], // API名称
        stateList: [
          { value: true, label: '启用' },
          { value: false, label: '未启用' },
        ],
        tableData: [],
        loading: false,
        alarmInfo: null,
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
    },
    methods: {
      getList() {
        this.loading = true;
        warnApi
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
        warnApi.queryApiGroup().done(res => {
          this.apiGroupList = res.data;
          // console.log(this.apiGroupList);
        });
      },
      changeApiGroupList() {
        this.query.apiName = '';
        this.getApiNameList();
      },
      getApiNameList() {
        this.option = [];
        if (this.query.apiGroupId) {
          const groupId = this.query.apiGroupId;
          warnApi.queryApi({ groupId }).done(res => {
            this.option = res.data;
            // console.log(this.option);
          });
        }
        // accessAPI.getItemList().done(res => {
        //   this.option = res.data;
        // });
      },
      onSave() {
        this.getList();
      },
      onAdd() {
        this.alarmInfo = null;
        this.dialog.edit = true;
      },
      onSearch() {
        this.requestParams.pageNo = 1;
        this.requestParams = Object.assign(this.requestParams, this.query);
        this.getList();
      },
      onReset() {
        this.query = getQuery();
        this.onSearch();
      },
      onEdit(alarmInfo) {
        this.dialog.edit = true;
        this.alarmInfo = alarmInfo;
      },
      onView(alarmInfo) {
        this.dialog.view = true;
        this.alarmInfo = alarmInfo;
      },
      onStart(alarmInfo) {
        const id = alarmInfo.apiWarnId;
        const status = true;
        this.changeStatus(id, status);
      },
      onStop(alarmInfo) {
        const id = alarmInfo.apiWarnId;
        const status = false;
        this.changeStatus(id, status);
      },
      changeStatus(apiWarnId, status) {
        if (status) {
          this.getStatus('确定启用该监控告警吗?', '成功启用', apiWarnId, status);
        } else {
          this.getStatus('确定停用该监控告警吗?', '已停用', apiWarnId, status);
        }
      },
      getStatus(title, info, apiWarnId, status) {
        this.$confirm(title, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
          .then(() => {
            warnApi.status({ apiWarnId, status }).done(res => {
              this.$message.success(info);
              this.getList();
            });
          })
          .catch(res => {
            console.log('用户取消操作', res);
          });
      },
      onDelete(alarmInfo) {
        this.$confirm('确定删除该监控告警吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
          .then(() => {
            this.delete(alarmInfo);
          })
          .catch(res => {
            console.log('用户取消操作', res);
          });
      },
      delete(alarmInfo) {
        this.loading = true;
        warnApi
          .delete({ apiWarnId: alarmInfo.apiWarnId })
          .done(res => {
            this.getList();
            this.$message('删除成功');
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
  .bd-container {
    background: white;
    position: relative;
    .alarm-table {
      background: white;
      padding-bottom: 20px;
    }
  }
</style>
