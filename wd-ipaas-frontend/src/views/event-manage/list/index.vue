<template>
  <div class="bd-page">
    <div class="bd-header">
      <div class="title">事件列表</div>
      <div class="header-right">
        <el-button type="primary" @click="onAdd">新增事件</el-button>
      </div>
    </div>
    <list-page-layout class="bd-container">
      <template #filters>
        <el-form ref="query" @submit.prevent.native="$refs.table.search()" :model="filterForm" inline>
          <el-form-item label="事件编码/名称" prop="code">
            <el-input
              prefix-icon="el-icon-search"
              placeholder="请输入事件编码/名称"
              v-model.trim="filterForm.code"
            ></el-input>
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select placeholder="请选择" v-model="filterForm.status" clearable>
              <el-option v-for="item in eventStatis" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="事件类型" prop="eventType">
            <el-select placeholder="请选择" v-model="filterForm.eventType" clearable>
              <el-option v-for="item in addressTypeList.list" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
        <div class="bd-search-group">
          <el-button type="primary" @click="reset" class="bd-button bd-strong">重置</el-button>
          <el-button type="primary" plain native-type="submit" class="bd-button bd-strong">查询</el-button>
        </div>
      </template>
      <fat-table ref="table" :fetch-handler="handleFetch" row-key="id">
        <el-table-column prop="code" label="事件编码" align="center" show-overflow-tooltip> </el-table-column>
        <el-table-column prop="name" label="事件名称" align="center"></el-table-column>
        <el-table-column prop="remark" label="事件描述" align="center" min-width="160"></el-table-column>
        <el-table-column prop="status" label="状态" align="center" min-width="50">
          <template slot-scope="scope">
            <span :style="{ color: scope.row.status ? '#67c23a' : '#f56c6c' }">
              {{ scope.row.status ? '启用' : '禁用' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="updateUser" label="最后修改人" align="center"></el-table-column>
        <el-table-column
          prop="updateTime"
          label="最后修改时间"
          min-width="130"
          align="center"
          sortable
        ></el-table-column>
        <el-table-column label="操作" min-width="180px" v-slot="scope" fixed="right" align="center">
          <table-actions :options="tableActions(scope.row)" />
        </el-table-column>
      </fat-table>
    </list-page-layout>
  </div>
</template>

<script>
  import { ListPageLayout, FatTable, TableActions } from '@/bz-components/list-page/index';
  import requester from '@/api/event-manage';
  import { mapState } from 'vuex';
  import { addressTypeList, eventStatis } from '@/enum';

  export default {
    components: {
      ListPageLayout,
      TableActions,
      FatTable,
    },
    data() {
      return {
        addressTypeList,
        eventStatis,
        filterForm: {
          name: null, // 事件名称/编码
          status: null,
          eventType: null,
        },
        show: false,
        currentInfo: null,
      };
    },
    computed: {
      ...mapState({
        user: state => state.user,
      }),
    },
    methods: {
      async handleFetch(params) {
        const query = {
          ...this.filterForm,
          pageNo: params.pagination.currentPage,
          pageSize: params.pagination.pageSize,
        };
        const { data, totalCount } = await requester.getEventList(query);
        return {
          list: data || [],
          total: totalCount || 0,
        };
      },
      tableActions(row) {
        return [
          {
            name: '删除',
            onClick: async () => {
              this.cancelSubscribr(row);
            },
          },
          {
            name: '查看',
            onClick: () => {
              this.$router.push({ path: '/event-manage/list/edit', query: { id: row.id, isEdit: 0 } });
            },
          },
          {
            name: '编辑',
            onClick: () => {
              this.$router.push({ path: '/event-manage/list/edit', query: { id: row.id } });
            },
          },
        ].filter(Boolean);
      },
      sortChange(row) {
        this.filterForm.sortingFields = [
          {
            asc: row.order === 'ascending',
            column: row.prop,
          },
        ];
        this.$refs.table.debouncedSearch();
      },
      reset() {
        this.$refs.query.resetFields();
        this.$refs.table.debouncedSearch();
      },
      onAdd() {
        this.$router.push({ path: '/event-manage/list/edit' });
      },
      cancelSubscribr(row) {
        this.$confirm('是否删除', '提示').then(async () => {
          const res = await requester.deleteEvent({ id: row.id });
          if (res.success) {
            this.$message.success('删除成功');
            this.$refs.table.debouncedSearch();
          } else {
            this.$message.error(res.msg);
          }
        });
      },
    },
  };
</script>
<style lang="less" scoped>
  .bd-container {
    margin-top: 80px;
    background: white;
    position: relative;
  }
</style>
