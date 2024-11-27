<template>
  <div class="key-manage bd-page">
    <div class="bd-header">
      <div class="title">{{ title }}</div>
      <div class="header-right">
        <el-button type="primary" @click="onAdd">新增密钥</el-button>
      </div>
    </div>

    <div class="bd-container">
      <div class="bd-search key-manage-search">
        <el-form label-position="right" class="bd-form" inline label-width="100px">
          <el-form-item label="连接器名称" prop="connectorId">
            <el-select v-model="form.connectorId" placeholder="请选择" clearable>
              <el-option v-for="item in connectNameList" :key="item.id" :label="item.name" :value="item.id"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="状态" prop="isEnable">
            <el-select v-model="form.isEnable" placeholder="请选择" clearable>
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
          <el-button type="primary" plain class="bd-button bd-strong" @click="onReset">重置</el-button>
        </div>
      </div>

      <list-page-layout class="key-manage-table theme-table">
        <fat-table ref="table" :fetch-handler="handleFetch" :state.sync="form" row-key="id" class="dss-table bd-table">
          <el-table-column prop="name" label="密钥名称" show-overflow-tooltip></el-table-column>

          <el-table-column prop="connectName" label="所属连接器" show-overflow-tooltip></el-table-column>

          <el-table-column prop="environmentName" label="环境名称" show-overflow-tooltip></el-table-column>

          <el-table-column prop="description" label="描述"></el-table-column>

          <el-table-column label="状态" show-overflow-tooltip align="center">
            <template #default="scope">
              <span :class="{ 'dss-status-success': scope.row.isEnable, 'dss-status-error': !scope.row.isEnable }">{{
                TASK_STATUS_OPTIONS.find(item => item.value === scope.row.isEnable)?.label
              }}</span>
            </template>
          </el-table-column>

          <el-table-column label="操作" align="center" width="200px">
            <template #default="scope">
              <el-button type="text" class="bd-button bd-table-primary" @click="onCheck(scope.row)">查看</el-button>
              <el-button type="text" class="bd-button bd-table-primary" @click="onEdit(scope.row)">编辑</el-button>
              <el-button type="text" class="bd-button bd-table-danger" @click="onDelete(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </fat-table>
      </list-page-layout>
    </div>

    <EditDialog
      v-if="dialog.add"
      :connect-name-list="connectNameList"
      :visible.sync="dialog.add"
      :disabled="checkKey"
      :data="selectKey"
      @cancel="onCancel"
    ></EditDialog>
  </div>
</template>

<script>
  import { mapState } from 'vuex';
  import { ListPageLayout, FatTable } from '@/bz-components/list-page/index';
  import api from '@/api/api-key-manage';

  import EditDialog from './edit-dialog';
  import { TASK_STATUS_OPTIONS } from './common';

  import pageUtils from '@/utils/page.js';

  export default {
    components: {
      ListPageLayout,
      FatTable,
      EditDialog,
    },

    data() {
      return {
        TASK_STATUS_OPTIONS,
        connectNameList: [],
        form: {
          connectorId: '',
          isEnable: '',
        },
        selectKey: null,
        checkKey: false,
        loading: {
          list: false,
        },
        dialog: {
          add: false,
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

    created() {
      this.getConnectList();
    },

    methods: {
      getConnectList() {
        api.fetchConnectList().then(res => {
          this.connectNameList = res.data;
        });
      },

      async handleFetch(params) {
        this.loading.list = true;
        const { pagination } = params;
        const query = {
          ...this.form,
          pageNo: pagination.currentPage,
          pageSize: pagination.pageSize,
        };
        for (const key in query) {
          if (query[key] === '' || query[key] == null) {
            Reflect.deleteProperty(query, key);
          }
        }

        let res = {};
        const defaultTotalCount = 0; // fix 后端没有数据返回0
        const {
          data = [],
          pageNo,
          pageSize,
          totalCount = defaultTotalCount,
          success,
        } = await api.fetchSecretPage(query);
        this.loading.list = false;
        if (success) {
          res = {
            list: data,
            total: totalCount,
            pagination: {
              currentPage: pageNo,
              pageSize,
              total: totalCount,
            },
          };
        }
        return res;
      },

      onSearch() {
        this.$refs.table.refresh();
      },

      onReset() {
        this.form.connectorId = '';
        this.form.isEnable = '';
      },

      onAdd() {
        this.selectKey = null;
        this.dialog.add = true;
      },

      onCancel(auto) {
        this.dialog.add = false;
        if (auto && !this.checkKey) {
          this.onSearch();
        }
        this.checkKey = false;
      },

      onDelete(item) {
        this.$confirm('确定删除该密钥吗?', '提示', {
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
        api.deleteSecretById({ id }).then(res => {
          this.onSearch();
          this.$message.success('删除成功');
        });
      },

      onCheck(item) {
        this.checkKey = true;
        this.dialog.add = true;
        this.selectKey = item;
      },

      onEdit(item) {
        this.dialog.add = true;
        this.selectKey = item;
      },
    },
  };
</script>

<style scoped lang="less">
  .key-manage {
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
