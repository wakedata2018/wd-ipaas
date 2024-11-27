<template>
  <el-dialog
    class="bd-dialog not-lock-scroll"
    width="700px"
    :visible.sync="isShow"
    v-loading="loading"
    append-to-body
    top="50px"
    title="请选择数据表"
  >
    <div class="bd-search page-search" style="padding: 0 0 20px 0">
      <el-form size="mini" label-position="right" class="bd-form" label-width="68px" inline>
        <el-form-item label="关键词">
          <el-input type="text" maxlength="64" v-model.trim="form.keyword" />
        </el-form-item>
        <div class="bd-search-group">
          <el-button type="primary" @click="onSearch" class="bd-button bd-strong">查询</el-button>
          <el-button type="primary" plain size="mini" class="bd-button bd-strong" @click="onReset">重置</el-button>
        </div>
      </el-form>
    </div>
    <div class="selected-div" v-if="!!selectedTable">已选中： {{ selectedTable }}</div>
    <div class="theme-table">
      <el-table
        highlight-current-row
        @current-change="handleCurrentChange"
        :data="tableData.list"
        v-loading="tableData.loading"
        style="width: 100%"
        class="dss-table bd-table"
      >
        <el-table-column width="55" align="center">
          <template slot-scope="scope">
            <el-radio v-model="selectedTable" :label="scope.row.datasourceTableName" @click.native.prevent="">
              <i></i>
            </el-radio>
          </template>
        </el-table-column>
        <el-table-column prop="datasourceTableName" label="数据表名称" show-overflow-tooltip></el-table-column>
        <el-table-column prop="datasourceTableDesc" label="数据表描述" show-overflow-tooltip>
          <template slot-scope="scope">
            {{ scope.row.datasourceTableDesc || '-' }}
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
    <div class="bd-dialog-footer" slot="footer">
      <el-button size="medium" @click="onCancle">取消</el-button>
      <el-button size="medium" type="primary" class="bd-button" @click="onSave">保存</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import dataSource from '@/api/data-source.js';

  export default {
    props: {
      sourceId: {
        type: Number,
        default: null,
      },
      value: {
        type: String,
        default: null,
      },
      visible: {
        type: Boolean,
        default: false,
      },
    },
    components: {},
    data() {
      return {
        loading: false,
        form: {
          keyword: '',
        },
        totalCount: 0,
        page: {
          pageSize: 10,
          pageNo: 1,
        },
        tableData: {
          loading: false,
          list: [],
        },
        selectedTable: null,
      };
    },
    computed: {
      isShow: {
        get() {
          return this.visible;
        },
        set(val) {
          this.$emit('update:visible', val);
        },
      },
      isEdit() {
        return !!this.appInfo;
      },
      title() {
        return this.isEdit ? '编辑接口分类' : '新增接口分类';
      },
      selectedValue: {
        get() {
          return this.value;
        },
        set(val) {
          this.$emit('update:value', val);
        },
      },
    },
    watch: {
      isShow(val) {
        this.selectedTable = null;
        this.resetFields();
        if (val) {
          this.getTableNameList();
          this.selectedTable = this.value;
        }
      },
    },
    methods: {
      resetFields() {
        this.totalCount = 0;
        this.page = {
          pageSize: 10,
          pageNo: 1,
        };
        this.form.keyword = '';
        this.tableData = {
          loading: false,
          list: [],
        };
      },
      onSearch() {
        this.page.pageNo = 1;
        this.getTableNameList();
      },
      onReset() {
        this.resetFields();
        this.getTableNameList();
      },
      getTableNameList() {
        this.tableData.list = [];

        if (this.sourceId !== null && this.sourceId !== '') {
          this.tableData.loading = true;
          dataSource
            .showDataTablePage({
              ...this.page,
              ...this.form,
              dataSourceId: this.sourceId,
            })
            .done(res => {
              this.tableData.list = res.data;
              this.totalCount = res.totalCount;
            })
            .always(() => {
              this.tableData.loading = false;
            });
        }
      },
      onSizeChange(val) {
        this.page.pageNo = 1;
        this.page.pageSize = val;
        this.getTableNameList();
      },
      onCurrentChange(val) {
        this.page.pageNo = val;
        this.getTableNameList();
      },
      onCancle() {
        this.isShow = false;
      },
      handleCurrentChange(val) {
        if (val) {
          this.selectedTable = val.datasourceTableName;
        }
      },
      onSave() {
        this.selectedValue = this.selectedTable;
        this.$emit('change', this.selectedValue);
        this.isShow = false;
      },
    },
  };
</script>
<style lang="less" scoped>
  /deep/.el-dialog__body {
    padding-bottom: 60px;
  }
  .selected-div {
    padding: 10px;
    font-size: 12px;
  }
</style>
