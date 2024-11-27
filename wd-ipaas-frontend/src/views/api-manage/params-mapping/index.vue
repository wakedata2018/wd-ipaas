<template>
  <div class="params-mapping bd-page">
    <div class="bd-header">
      <div class="title">{{ title }}</div>
      <div class="header-right">
        <el-button code="add" type="primary" @click="onAdd">新增参数映射规则</el-button>
      </div>
    </div>
    <div class="bd-container">
      <div class="bd-search page-search">
        <el-form ref="queryForm" size="mini" label-position="right" class="bd-form" inline label-width="100px">
          <el-form-item label="规则名称" prop="respParamMappingRuleName">
            <el-input
              v-model.trim="query.respParamMappingRuleName"
              type="text"
              maxlength="64"
              placeholder="请输入规则名称"
            />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="query.status" placeholder="请选择状态" clearable>
              <el-option v-for="item in StatusOptions" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="更新时间" prop="updateTime">
            <el-date-picker
              v-model="dateRange"
              type="datetimerange"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              range-separator="至"
              value-format="yyyy-MM-dd HH:mm:ss"
              placeholder="请选择时间范围"
              @change="handleDate"
            ></el-date-picker>
          </el-form-item>
        </el-form>
        <div class="bd-search-group">
          <el-button type="primary" class="bd-button bd-strong" @click="onSearch">查询</el-button>
          <el-button type="primary" plain size="mini" class="bd-button bd-strong" @click="onReset">重置</el-button>
        </div>
      </div>

      <div class="theme-table">
        <el-table
          v-loading="loading"
          :data="listData"
          row-key="id"
          style="width: 100%"
          :tree-props="{ children: 'children' }"
          class="dss-table bd-table"
        >
          <el-table-column prop="respParamMappingRuleName" label="规则名称" show-overflow-tooltip></el-table-column>
          <el-table-column prop="description" label="规则描述" show-overflow-tooltip></el-table-column>
          <el-table-column prop="apiCount" label="接口数量" show-overflow-tooltip align="center"></el-table-column>
          <el-table-column prop="createTime" label="创建时间"></el-table-column>
          <el-table-column prop="updateTime" label="最后更新时间"></el-table-column>
          <el-table-column prop="status" label="状态" align="center">
            <template #default="scope">
              <span :class="{ 'dss-status-success': scope.row.status, 'dss-status-error': !scope.row.status }">{{
                StatusOptions[scope.row.status].label
              }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="isDefaultRule" label="默认规则" align="center">
            <template #default="scope">
              <span>{{ scope.row.isDefaultRule ? '是' : '否' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center">
            <template #default="scope">
              <el-button code="edit" type="text" class="bd-button bd-table-primary" @click="onPreview(scope.row.id)"
                >查看</el-button
              >
              <el-button
                v-if="!scope.row.isDefaultRule"
                code="edit"
                type="text"
                class="bd-button bd-table-primary"
                @click="onEdit(scope.row.id)"
                >编辑</el-button
              >
              <el-button
                v-if="!scope.row.isDefaultRule"
                code="delete"
                type="text"
                class="bd-button bd-table-danger"
                @click="onDelete(scope.row.id)"
                >删除</el-button
              >
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
        <create-edit-show
          :id="infoId"
          :visible.sync="dialogShow"
          :is-readonly="preview"
          @save="onSave"
        ></create-edit-show>
      </div>
    </div>
  </div>
</template>

<script>
  import { mapState } from 'vuex';

  import CreateEditShow from './create-edit-show.vue';

  import pageUtils from '@/utils/page.js';
  import { DefaultRuleStatusOptions, StatusOptions, RuleStatus } from '@/utils/enum/params-mapping';
  import ParamsMappingApi from '@/api/params-mapping';

  export default {
    components: { CreateEditShow },
    data() {
      return {
        query: {
          respParamMappingRuleName: '',
          status: '',
          startTime: '',
          endTime: '',
        },
        totalCount: 0,
        page: {
          pageSize: 10,
          pageNo: 1,
        },
        dateRange: [],
        StatusOptions,
        DefaultRuleStatusOptions,
        listData: [],
        loading: false,
        infoId: null,
        dialogShow: false,
        preview: false,
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
      this.getList();
    },
    methods: {
      getList() {
        this.loading = true;
        const params = { ...this.query, ...this.page };
        ParamsMappingApi.fetchParamsMappingList(params)
          .then(res => {
            this.listData = res.data;
            this.totalCount = res.totalCount;
          })
          .finally(() => {
            this.loading = false;
          });
      },
      onSearch() {
        this.getList();
      },
      onReset() {
        this.query = {
          respParamMappingRuleName: '',
          status: '',
          startTime: '',
          endTime: '',
        };
        this.dateRange = [];

        this.getList();
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

      onDelete(id) {
        this.$confirm('确定删除该规则吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
          .then(() => {
            this.delete(id);
          })
          .catch(res => {
            console.log('用户取消操作', res);
          });
      },
      delete(id) {
        this.loading = true;
        ParamsMappingApi.delParamsMapping(id)
          .done(res => {
            this.getList();
            this.$message.success('删除成功');
          })
          .always(res => {
            if (!res.success) {
              this.$message.error(res.message);
            }
            this.loading = false;
          });
      },
      onAdd() {
        this.infoId = null;
        this.preview = false;
        this.dialogShow = true;
      },
      onPreview(id) {
        this.infoId = id;
        this.preview = true;
        this.dialogShow = true;
      },
      onEdit(id) {
        this.infoId = id;
        this.preview = false;
        this.dialogShow = true;
      },
      handleDate(val) {
        if (val) {
          this.query.startTime = val[0];
          this.query.endTime = val[1];
        } else {
          this.query.startTime = '';
          this.query.endTime = '';
        }
      },
      onSave() {
        this.getList();
      },
    },
  };
</script>

<style scoped lang="less">
  @import '@/css/var.less';
  .page-search {
    /deep/.el-form-item--mini {
      margin-bottom: 0 !important;
    }
  }
  .params-mapping {
    box-sizing: border-box;

    .theme-table {
      background: white;
      padding-bottom: 15px;
    }
  }
</style>
