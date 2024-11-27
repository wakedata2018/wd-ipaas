<template>
  <div class="page-api-join bd-page">
    <div class="bd-header">
      <div class="title">{{ title }}</div>
      <div class="header-right">
        <WKAuthButton code="add" type="primary" @click="onAdd">新增接口分类</WKAuthButton>
      </div>
    </div>
    <div class="bd-container">
      <div class="bd-search page-search">
        <el-form ref="form" size="mini" label-position="right" class="bd-form" inline label-width="110px">
          <el-form-item label="接口分类名称" prop="keyword">
            <el-input v-model.trim="form.keyword" type="text" maxlength="64" placeholder="请输入接口分类名称" />
          </el-form-item>
          <el-form-item label="接口分类编码" prop="groupCode">
            <el-input v-model.trim="form.groupCode" type="text" maxlength="64" placeholder="请输入接口分类编码" />
          </el-form-item>
        </el-form>
        <div class="bd-search-group">
          <el-button type="primary" class="bd-button bd-strong" @click="onSearch">查询</el-button>
          <el-button type="primary" plain size="mini" class="bd-button bd-strong" @click="onReset">重置</el-button>
        </div>
      </div>

      <div class="theme-table">
        <el-table
          v-loading="loading.list"
          :data="filteredTableData"
          row-key="id"
          style="width: 100%"
          :tree-props="{ children: 'children' }"
          class="dss-table bd-table"
        >
          <el-table-column prop="groupName" label="接口分类名称" show-overflow-tooltip></el-table-column>
          <el-table-column prop="groupCode" label="接口分类编码" show-overflow-tooltip></el-table-column>
          <el-table-column prop="groupPath" label="公共路径" show-overflow-tooltip></el-table-column>
          <el-table-column prop="groupDesc" label="描述" show-overflow-tooltip></el-table-column>
          <el-table-column prop="createUser" label="创建人"></el-table-column>
          <el-table-column prop="createTime" label="创建时间"></el-table-column>
          <el-table-column label="操作" align="center" width="150px">
            <template #default="scope">
              <WKAuthButton code="edit" type="text" class="bd-button bd-table-primary" @click="onEdit(scope.row)"
                >编辑</WKAuthButton
              >
              <WKAuthButton code="delete" type="text" class="bd-button bd-table-danger" @click="onDelete(scope.row)"
                >删除</WKAuthButton
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
      </div>
    </div>
    <new-group :app-info="appInfo" :visible.sync="dialog.app" @saved="onSaved" />
  </div>
</template>

<script>
  // import IPager from '../../../mixins/i-pager.js';
  import { mapState } from 'vuex';
  import IPageSize from '../../../mixins/i-pagesize.js';
  import apiGroup from '@/api/api-group.js';
  import NewGroup from './new-group.vue';
  import WKAuthButton from '@/components/wk-auth-button/index.vue';
  import pageUtils from '@/utils/page.js';

  export default {
    components: { NewGroup, WKAuthButton },
    mixins: [IPageSize],
    data() {
      return {
        form: {
          keyword: '', // 接口分类名
          groupCode: '', // 接口分类编码
        },
        keyword: '',
        apiGroupId: null, // 接口分类id
        appInfo: null,
        loading: {
          app: false,
          list: false,
        },
        dialog: {
          app: false,
          // display: false,
          // whiteIP: false,
          // approve: false
        },
        tableData: [
          // {
          //   groupName: '',
          //   descript: '',
          //   createUser: '',
          //   createTime: ''
          // }
        ],
        theme: '', // 接口分类名称
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
        console.log(this.permitList);
        return pageUtils.findPageName(this.permitList, this.$route.name);
      },
      filteredTableData() {
        return this.filterGroup(this.tableData || []);
      },
    },
    created() {
      this.getList();
    },
    methods: {
      filterGroup(item) {
        return item.filter(
          _item =>
            !this.keyword ||
            _item.groupDesc.indexOf(this.keyword) !== -1 ||
            _item.groupPath.indexOf(this.keyword) !== -1 ||
            _item.groupName.indexOf(this.keyword) !== -1 ||
            this.filterGroup(_item.children).length > 0
        );
      },
      getList() {
        // console.log('获取接口分类列表');
        this.loading.list = true;
        const { keyword, groupCode } = this.form;
        const params = { groupName: keyword, groupCode, ...this.page };
        apiGroup
          .apiGroupList(params)
          .then(res => {
            this.tableData = res.data;
            this.totalCount = res.totalCount;
          })
          .finally(() => {
            this.loading.list = false;
          });
      },
      onSearch() {
        this.keyword = this.form.keyword;
        this.page.pageNo = 1;
        this.getList();
      },
      onReset() {
        this.form.keyword = '';
        this.form.groupCode = '';
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
        this.appInfo = null;
        this.dialog.app = true;
      },
      onSaved(type, parmas, app) {
        if (type === 'create') {
          this.displayApp = app;
          this.dialog.display = true;
        }
        this.getList();
      },
      onDelete(appInfo) {
        this.$confirm('确定删除该接口分类吗?', '提示', {
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
        apiGroup
          .deleteTree({ apiGroupId: appInfo.id })
          .done(res => {
            this.getList();
            this.$message.success('删除成功');
          })
          .always(() => {
            this.loading.app = false;
          });
      },
      onEdit(appInfo) {
        // console.log(appInfo)
        this.dialog.app = true;
        this.appInfo = appInfo;
      },
    },
  };
</script>

<style scoped lang="less">
  .page-api-join {
    box-sizing: border-box;
    margin-bottom: 50px;
    .theme-table {
      background: white;
      padding-bottom: 15px;
    }
  }
</style>
