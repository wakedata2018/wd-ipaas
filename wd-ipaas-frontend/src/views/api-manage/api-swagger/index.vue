<template>
  <div class="page-api-join bd-page">
    <div class="bd-header">
      <div class="title">{{ title }}</div>
      <div class="header-right">
        <el-button type="primary" @click="onAdd">新增Swagger</el-button>
      </div>
    </div>
    <div class="bd-container">
      <div class="bd-search">
        <el-form
          ref="filterForm"
          label-width="100px"
          size="mini"
          :model="query"
          label-position="right"
          class="bd-form"
          inline
        >
          <el-form-item label="Swagger名称" prop="swaggerName">
            <el-input v-model.trim="query.swaggerName" type="text" maxlength="64" placeholder="请输入Swagger名称" />
          </el-form-item>
          <el-form-item label="更新时间">
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
          <el-form-item label="接口分类">
            <el-select v-model.trim="query.apiGroupId" class="w200" placeholder="请选择接口分类" prop="apiGroupId">
              <el-option v-for="item in apiGroupList" :key="item.id" :label="item.groupName" :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
        <div class="bd-search-group">
          <el-button type="primary" class="bd-button bd-strong" @click="onSearch">查询</el-button>
          <el-button type="primary" plain size="mini" class="bd-button bd-strong" @click="onReset">重置</el-button>
        </div>
      </div>

      <div class="swagger-table theme-table">
        <el-table
          v-loading="loading.list"
          :data="tableData"
          row-key="id"
          style="width: 100%"
          :tree-props="{ children: 'children' }"
          class="dss-table bd-table"
        >
          <el-table-column prop="swaggerName" label="Swagger名称" show-overflow-tooltip></el-table-column>
          <el-table-column prop="swaggerUrl" label="SwaggerURL" show-overflow-tooltip></el-table-column>
          <el-table-column prop="apiGroupName" label="接口分类"></el-table-column>
          <!-- <el-table-column prop="apiAmount" label="API总数量"></el-table-column> -->
          <el-table-column prop="executeStatus" label="导入状态">
            <template #default="scope">
              {{ importStatus(scope.row.executeStatus) }}
            </template>
          </el-table-column>
          <el-table-column prop="description" label="描述" show-overflow-tooltip></el-table-column>
          <el-table-column prop="createUser" label="创建人"></el-table-column>
          <el-table-column prop="updateTime" label="最后更新时间"></el-table-column>
          <el-table-column label="操作" align="center" width="200">
            <template #default="scope">
              <!-- <WKAuthButton code="update">测试</WKAuthButton> -->
              <!-- <el-button type="text" class="bd-button bd-table-primary" @click="onUpdate(scope.row)">更新API</el-button> -->
              <el-button type="text" class="bd-button bd-table-primary" @click="goToImport(scope.row)"
                >导入确认</el-button
              >
              <el-button type="text" class="bd-button bd-table-primary" @click="onEdit(scope.row)">编辑</el-button>
              <el-button type="text" class="bd-button bd-table-danger" @click="onDelete(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="table-pagination">
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
    <edit-swagger-dialog :app-info="appInfo" :visible.sync="dialog.edit" @saved="onSaved" />
    <update-api-dialog :visible.sync="dialog.update" :swagger-info-id="swaggerInfoId" @saved="onSaved" />
  </div>
</template>

<script>
  import { mapState } from 'vuex';
  import IPageSize from '@/mixins/i-pagesize.js';
  import swaggerManage from '@/api/swagger-manage.js';
  import apiGroup from '@/api/api-group.js';
  import EditSwaggerDialog from './edit-swagger-dialog.vue';
  import UpdateApiDialog from './update-api-dialog.vue';
  import { IMPORT_STATUS } from '@/utils/enum/api-swagger.js';
  import pageUtils from '@/utils/page.js';

  export default {
    components: { EditSwaggerDialog, UpdateApiDialog },
    mixins: [IPageSize],
    data() {
      return {
        totalCount: 0,
        page: {
          pageNo: 1,
          pageSize: 10,
        },
        dateRange: [],
        dialog: {
          edit: false,
          update: false,
        },
        appInfo: null,
        loading: {
          app: false,
          list: false,
        },
        tableData: [],
        apiAmount: 0,
        swaggerInfoId: 0,
        apiGroupList: [],
        query: {
          swaggerName: '', // Swagger名称
          apiGroupId: -1, // 接口分类ID
          updateTimeStart: '', // 更新时间开始节点
          updateTimeEnd: '', // 更新时间结束节点
        },
        importStatusEnum: IMPORT_STATUS,
      };
    },
    computed: {
      ...mapState({
        permitList: state => state.permitList,
      }),
      title() {
        console.log(this.$route.name);
        console.log(this.permitList);
        return pageUtils.findPageName(this.permitList, this.$route.name);
      },
      importStatus() {
        return (status = this.importStatusEnum.NONE.value) => {
          return Object.values(this.importStatusEnum).find(item => item.value === status).label;
        };
      },
    },
    created() {
      this.getList();
      this.getApiGroupList();
    },
    methods: {
      goToImport(row) {
        // 跳转到导入确认页面
        this.$router.push({ path: '/api-swagger/swagger/api-import', query: { id: row.id } });
      },
      onUpdate(param) {
        this.apiAmount = param.apiAmount;
        this.swaggerInfoId = param.id;
        this.dialog.update = true;
      },
      onAdd() {
        this.appInfo = null;
        this.dialog.edit = true;
      },
      filterGroup(item) {
        return item;
      },
      getList() {
        const { apiGroupId, ...otherParams } = this.query;
        swaggerManage
          .getSwaggerParamList({
            apiGroupId: (apiGroupId !== -1 && apiGroupId) || '',
            ...otherParams,
            ...this.page,
          })
          .done(res => {
            this.tableData = res.data;
            this.totalCount = res.totalCount;
          })
          .always(() => {
            this.loading.list = false;
          });
      },
      onSearch() {
        this.getList();
      },
      onReset() {
        this.$refs.filterForm.resetFields();
        this.dateRange = [];
        this.query.apiGroupId = -1;
        this.query.updateTimeStart = '';
        this.query.updateTimeEnd = '';
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

      onSaved(type, data) {
        if (type === 'create') {
          // 跳转到导入确认页面
          this.$router.push({ path: '/api-swagger/swagger/api-import', query: { id: data.id } });
        }
        this.getList();
      },
      onDelete(appInfo) {
        this.$confirm('确定删除该swagger吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
          .then(() => {
            this.delete(appInfo);
          })
          .catch(res => {
            console.log('用户取消操作', res);
          });
      },
      delete(appInfo) {
        this.loading.app = true;
        swaggerManage
          .deleteSwagger({ swaggerInfoId: appInfo.id })
          .done(res => {
            this.getList();
            this.$message.success('删除成功');
          })
          .always(() => {
            this.loading.app = false;
          });
      },
      onEdit(appInfo) {
        this.appInfo = appInfo;
        this.dialog.edit = true;
      },
      handleDate(val) {
        if (val) {
          this.query.updateTimeStart = val[0];
          this.query.updateTimeEnd = val[1];
        } else {
          this.query.updateTimeEnd = '';
          this.query.updateTimeEnd = '';
        }
      },
      /**
       * 获取接口分类列表
       */
      async getApiGroupList() {
        await apiGroup.getThemeList().done(res => {
          this.apiGroupList = [{ id: -1, groupName: '全部' }, ...(res.data || [])];
        });
      },
    },
  };
</script>

<style scoped lang="less">
  .page-search {
    /deep/.el-form-item--mini {
      margin-bottom: 0 !important;
    }

    .bd-search-group {
      margin: 0 0 0 100px;
    }
  }

  .page-api-join {
    box-sizing: border-box;
    margin-bottom: 50px;

    .swagger-table {
      background: white;
      padding-bottom: 15px;
    }
  }

  .link {
    color: #2776fb;
    text-decoration: none;
    margin-left: 10px;
  }
</style>
