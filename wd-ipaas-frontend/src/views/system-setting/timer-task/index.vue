<template>
  <div class="timer-task bd-page">
    <div class="bd-header">
      <div class="title">{{ title }}</div>
      <div class="header-right">
        <el-button type="primary" @click="onAdd">新增定时任务</el-button>
      </div>
    </div>
    <div class="bd-container">
      <div class="bd-search timer-task-search">
        <el-form label-position="right" class="bd-form" inline label-width="100px">
          <el-form-item label="任务名称" prop="taskName">
            <el-input v-model.trim="form.taskName" type="text" maxlength="64" placeholder="请输入任务名称" />
          </el-form-item>
          <el-form-item label="状态" prop="taskType">
            <el-select v-model="form.taskType" placeholder="请选择" clearable>
              <el-option
                v-for="item in TASK_STATUS_OPTIONS"
                :key="item.value"
                :value="item.value"
                :label="item.label"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-form>
        <div class="bd-search-group">
          <el-button type="primary" class="bd-button bd-strong" @click="onSearch">查询</el-button>
          <el-button type="primary" plain size="mini" class="bd-button bd-strong" @click="onReset">重置</el-button>
        </div>
      </div>

      <div class="timer-task-table theme-table">
        <el-table
          v-loading="loading.list"
          :data="tableData"
          row-key="id"
          style="width: 100%"
          :tree-props="{ children: 'children' }"
          class="dss-table bd-table"
        >
          <el-table-column prop="taskName" label="任务名称" show-overflow-tooltip></el-table-column>
          <el-table-column prop="dataAssetAppName" label="所属应用" show-overflow-tooltip></el-table-column>
          <el-table-column prop="taskCron" label="执行规则" show-overflow-tooltip></el-table-column>
          <el-table-column label="执行时间" min-width="140px" show-overflow-tooltip>
            <template #default="scope">
              {{ excuteTime(scope.row) }}
            </template>
          </el-table-column>
          <el-table-column label="状态" show-overflow-tooltip align="center">
            <template #default="scope">
              <span :class="{ 'dss-status-success': scope.row.taskType, 'dss-status-error': !scope.row.taskType }">{{
                TASK_STATUS_OPTIONS.find(item => item.value === scope.row.taskType)?.label
              }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="taskExecuteAmount" label="已执行次数"></el-table-column>
          <el-table-column prop="executeTime" label="最后一次执行时间"></el-table-column>
          <el-table-column prop="updateBy" label="更新人"></el-table-column>
          <el-table-column prop="updateTime" label="更新时间"></el-table-column>

          <el-table-column label="操作" align="center" width="200">
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
            :current-page="page.pageNo"
            :page-size="page.pageSize"
            @size-change="onSizeChange"
            @current-change="onCurrentChange"
          ></el-pagination>
        </div>
      </div>
    </div>
    <EditDialog :visible.sync="dialog.add" :data="selectTask" @cancel="onCancel"></EditDialog>
  </div>
</template>

<script>
  import { mapState } from 'vuex';
  import IPageSize from '@/mixins/i-pagesize';
  import api from '@/api/api-timer-task';

  import EditDialog from './edit-dialog';
  import { TASK_STATUS_OPTIONS, TASK_EXECUTE_TYPE, dateTimeToDate } from './common';

  import pageUtils from '@/utils/page.js';

  export default {
    components: { EditDialog },

    mixins: [IPageSize],

    data() {
      return {
        TASK_STATUS_OPTIONS,
        TASK_EXECUTE_TYPE,
        form: {
          taskName: '',
          taskType: '',
        },
        keyword: '',
        selectTask: null,
        loading: {
          app: false,
          list: false,
        },
        dialog: {
          add: false,
        },
        tableData: [],
        totalCount: 0,
        page: {
          pageSize: 10,
          pageNo: 1,
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
      excuteTime() {
        return item => {
          return item.taskExecuteType === TASK_EXECUTE_TYPE.FOREVER
            ? '永久'
            : `${dateTimeToDate(item.taskStartTime)}~${dateTimeToDate(item.taskEndTime)}`.replace(/-/g, '/');
        };
      },
    },

    created() {
      this.getList();
    },

    methods: {
      getList() {
        this.loading.list = true;
        const { taskName, taskType } = this.form;
        const params = { taskName, taskType, ...this.page };
        for (const key in params) {
          if (params[key] === '' || params[key] == null) {
            Reflect.deleteProperty(params, key);
          }
        }
        api
          .getTaskList(params)
          .then(res => {
            this.tableData = res.data || [];
            this.totalCount = res.totalCount;
          })
          .finally(() => {
            this.loading.list = false;
          });
      },

      onSearch() {
        this.page.pageNo = 1;
        this.getList();
      },

      onReset() {
        this.form.taskName = '';
        this.form.taskType = '';
        this.onSearch();
      },

      onSizeChange(val) {
        this.page.pageNo = 1;
        this.page.pageSize = val;
        this.getList();
      },

      onCurrentChange(val) {
        this.page.pageNo = val;
        this.getList();
      },

      onAdd() {
        this.selectTask = null;
        this.dialog.add = true;
      },

      onCancel(auto) {
        this.dialog.add = false;
        if (!auto) {
          this.getList();
        }
      },

      onDelete(item) {
        this.$confirm('确定删除该定时任务吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
          .then(() => {
            this.delete(item.id);
          })
          .catch(res => {
            console.log('用户取消操作', res);
          });
      },

      delete(id) {
        this.loading.app = true;
        api
          .deleteTask({ id })
          .done(res => {
            this.getList();
            this.$message.success('删除成功');
          })
          .always(() => {
            this.loading.app = false;
          });
      },

      onEdit(item) {
        this.dialog.add = true;
        this.selectTask = item;
      },
    },
  };
</script>

<style scoped lang="less">
  .timer-task {
    box-sizing: border-box;
    margin-bottom: 50px;

    &-table {
      background: white;
      padding-bottom: 15px;
    }
    &-search {
      ::v-deep {
        .el-form-item--mini {
          margin-bottom: 0 !important;
        }
      }
      &__btn {
        display: inline-block;
        margin-left: 68px;
      }
    }
  }
</style>
