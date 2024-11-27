<template>
  <div class="api-import-list bd-page">
    <div class="bd-header">
      <el-breadcrumb separator="/" class="title">
        <el-breadcrumb-item :to="{ path: `/api-swagger/swagger` }" class="uncheck">Swagger管理</el-breadcrumb-item>
        <el-breadcrumb-item class="check">导入确认</el-breadcrumb-item>
      </el-breadcrumb>
      <div v-if="isShowPullBtn" class="header-right">
        <el-button type="primary" @click="onPull">重新拉取</el-button>
      </div>
    </div>
    <div class="bd-container">
      <div class="bd-search split">
        <el-form
          ref="filterForm"
          size="mini"
          :model="filterForm"
          label-position="right"
          class="bd-form"
          inline
          label-width="100px"
        >
          <el-form-item label="API查找" prop="apiFind">
            <el-input
              v-model.trim="filterForm.apiFind"
              type="text"
              maxlength="50"
              placeholder="请输入API名称/API Path"
              style="width: 200px"
            />
          </el-form-item>
          <el-form-item label="导入结果" prop="importStatus">
            <el-select v-model="filterForm.importStatus">
              <el-option label="全部" :value="-1"> </el-option>
              <el-option v-for="(item, index) of IMPORT_RESULT_STATUS_MAP" :key="index" :label="item" :value="index">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
        <div class="bd-search-group">
          <el-button type="primary" class="bd-button bd-strong" @click="onSearch">查询</el-button>
          <el-button type="primary" plain size="mini" class="bd-button bd-strong" @click="onReset">重置</el-button>
        </div>
      </div>
      <div class="expand">
        <span class="tip">解析结果：共解析 {{ count }} 条接口；{{ parseTime ? `解析时间：${parseTime}` : '' }}</span>
        <div class="action">
          <el-button type="primary" @click="onImport()">批量导入</el-button>
          <el-button type="primary" @click="onImport(null, IMPORT_TYPE.COVER)">批量覆盖导入</el-button>
          <el-button type="danger" plain @click="onDeleteApi()">批量删除</el-button>
        </div>
      </div>
      <div class="theme-table">
        <el-table
          v-loading="loading"
          :data="apiList"
          class="dss-table bd-table"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55"> </el-table-column>
          <el-table-column prop="apiName" label="API名称" show-overflow-tooltip></el-table-column>
          <el-table-column prop="apiType" label="API 类型" width="150">
            <template #default="scope">
              {{ apiType(scope.row.apiType) }}
            </template>
          </el-table-column>
          <el-table-column prop="dataAssetApiMethod" label="API Path"></el-table-column>
          <el-table-column prop="apiDescription" label="API 描述"></el-table-column>
          <el-table-column prop="parseStatus" label="导入结果" width="150">
            <template #default="scope">
              <span
                v-if="scope.row.importStatus"
                :class="[scope.row.importStatus === IMPORT_RESULT_STATUS.SUCCESS_IMPORT ? 'success' : 'danger']"
                >{{ IMPORT_RESULT_STATUS_MAP[scope.row.importStatus] }}</span
              >
              <span v-else>{{ IMPORT_RESULT_STATUS_MAP[scope.row.importStatus] }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="errorDetail" label="描述"></el-table-column>
          <el-table-column label="操作" width="200" align="center">
            <template #default="scope">
              <el-button class="bd-button bd-table-primary" type="text" @click="onImport(scope.row.id)">导入</el-button>
              <el-button
                class="bd-button bd-table-primary"
                type="text"
                @click="onImport(scope.row.id, IMPORT_TYPE.COVER)"
                >覆盖导入</el-button
              >
              <el-button class="bd-button bd-table-danger" type="text" @click="onDeleteApi(scope.row.id)"
                >删除</el-button
              >
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="table-pagination">
        <el-pagination
          layout="total, sizes, prev, pager, next"
          :total="totalCount"
          :current-page="pageParams.pageNo"
          :page-size="pageParams.pageSize"
          @size-change="onSizeChange"
          @current-change="onPageChange"
        >
        </el-pagination>
      </div>
    </div>
  </div>
</template>
<script>
  import swaggerManage from '@/api/swagger-manage.js';
  import { PARSE_RESULT, IMPORT_STATUS, API_TYPE, IMPORT_FORM_TYPE } from '@/utils/enum/api-swagger.js';
  import { IMPORT_TYPE, IMPORT_RESULT_STATUS, IMPORT_RESULT_STATUS_MAP } from './types';

  export default {
    data() {
      return {
        count: 0,
        filterForm: {
          apiFind: null,
          importStatus: -1,
        },
        selectList: [],
        apiList: [],
        totalCount: 0,
        pageParams: {
          pageNo: 1,
          pageSize: 10,
        },
        loading: false,
        parseEnum: PARSE_RESULT,
        importStatus: -1,
        importEnums: IMPORT_STATUS,
        apiTypeEnum: API_TYPE,
        IMPORT_TYPE,
        IMPORT_FORM_TYPE,
        IMPORT_RESULT_STATUS,
        IMPORT_RESULT_STATUS_MAP,
        parseTime: null,
        importType: null,
      };
    },

    computed: {
      /**
       * API类型
       */
      apiType() {
        return (type = this.apiTypeEnum.SQL.value) => {
          return Object.values(this.apiTypeEnum).find(item => item.value === type).label;
        };
      },
      /**
       * 选中行数量
       */
      selectedNum() {
        return this.selectList.length;
      },
      /**
       * 解析结果
       */
      parseResult() {
        return result => {
          return result === this.parseEnum.SUCCESS.value ? this.parseEnum.SUCCESS.label : this.parseEnum.ERROR.label;
        };
      },
      apiGroupList() {
        return Object.values(this.parseEnum);
      },
      /**
       *获取路由参数id
       */
      queryParamsId() {
        return this.$route.query?.id;
      },
      /**
       * 全部导入完成 不允许再导入
       */
      allImported() {
        return this.importStatus === this.importEnums.ALL_SUCCESS.value;
      },
      /**
       * 当前swagger没有导入完并且本条数据解析成功才显示
       */
      showImportBtn() {
        return status => {
          return !this.allImported && status === IMPORT_RESULT_STATUS.UN_IMPORT;
        };
      },
      /**
       * 导入来源为URL则显示重新拉取按钮
       */
      isShowPullBtn() {
        return this.importType === this.IMPORT_FORM_TYPE.URL;
      },
    },

    async mounted() {
      await this.init();
      await this.getApiList();
    },

    methods: {
      async init() {
        this.loading = true;
        try {
          await swaggerManage.importSwaggerResult({
            swaggerId: Number(this.queryParamsId),
          });
        } catch (err) {
          console.log(err);
        } finally {
          this.loading = false;
        }
      },
      async getSwaggerInfo() {
        try {
          const { data } = await swaggerManage.showSwaggerParam({
            swaggerInfoId: this.queryParamsId,
          });
          if (data) {
            this.count = data.apiAmount || 0;
            this.importStatus = data.executeStatus;
            this.parseTime = data.parseTime;
            this.importType = data.importType;
          }
        } catch (err) {
          console.log(err);
        }
      },
      async getApiList() {
        try {
          this.loading = true;
          const res = await swaggerManage.getSwaggerTempList({
            swaggerId: this.queryParamsId,
            ...this.filterForm,
            ...this.pageParams,
          });
          await this.getSwaggerInfo();
          this.totalCount = res.totalCount;
          this.apiList = res.data;
        } catch (err) {
          console.log(err);
        } finally {
          this.loading = false;
        }
      },
      onSearch() {
        this.getApiList();
      },
      onReset() {
        this.$refs.filterForm.resetFields();
        this.filterForm.importStatus = -1;
        this.getApiList();
      },
      onDeleteApi(id = null) {
        // 删除单行提示
        let tip = '删除之后将不导入此行数据，确定删除此行吗？';
        if (!id) {
          // 批量删除
          if (this.selectedNum) {
            tip = `当前选中 ${this.selectedNum} 行数据，删除之后将不导入，确定都删除吗？`;
          } else {
            tip = '当前未选中数据，请先勾选数据';
          }
        }
        const params = (id && [id]) || this.selectList;
        this.$confirm(tip, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
          .then(async res => {
            await swaggerManage.deleteApi(params);
            this.getApiList();
            this.$message({
              type: 'success',
              message: '删除成功',
            });
          })
          .catch(() => {
            this.$message({
              type: 'info',
              message: '已取消删除',
            });
          });
      },
      onImport(id = null, type = IMPORT_TYPE.NORMAL) {
        this.loading = true;
        let tipMessage = '';
        if (id) {
          tipMessage = `将导入当前行数据到系统中，确定吗？`;
        } else {
          // 点击批量导入
          tipMessage = this.selectedNum
            ? `当前选中 ${this.selectedNum} 行数据，将导入 ${this.selectedNum} 行数据到系统中，如需全部导入，请勿勾选数据，确定吗？`
            : '当前未选中数据，将导入全部数据到系统中，如需部分导入，请先勾选数据，确定吗？';
        }
        this.$confirm(tipMessage, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
          .then(async res => {
            const params = {
              swaggerId: this.queryParamsId,
              ids: (id && [id]) || this.selectList,
            };
            const { data: msg } =
              type === IMPORT_TYPE.NORMAL
                ? await swaggerManage.importApi(params)
                : await swaggerManage.coverImport(params);
            // 如果最后一页全部导入，会导致重新请求数据显示不正常
            // this.pageParams.pageNo = 1;
            await this.getApiList();
            this.$message({
              type: 'success',
              message: msg,
            });
          })
          .finally(() => {
            this.loading = false;
          });
      },
      /**
       * 重新拉取
       */
      async onPull() {
        const { success } = await swaggerManage.pullSwagger({
          swaggerId: Number(this.queryParamsId),
        });
        if (success) {
          this.getApiList();
        }
      },
      onPageChange(pageNo) {
        this.pageParams.pageNo = pageNo;
        this.getApiList();
      },
      onSizeChange(size) {
        this.pageParams.pageSize = size;
        this.pageParams.pageNo = 1;
        this.getApiList();
      },

      handleSelectionChange(val) {
        this.selectList = val.map(item => item.id);
      },
    },
  };
</script>
<style lang="less" scoped>
  @import '@/css/var.less';
  .expand {
    display: flex;
    justify-content: space-between;
    background: #fff;
    padding: 0px 20px 0px 20px;

    .tip {
      color: @dss--danger-color;
    }
  }
  .danger {
    color: @dss--danger-color;
  }
  .success {
    color: @dss--success-color;
  }
</style>
