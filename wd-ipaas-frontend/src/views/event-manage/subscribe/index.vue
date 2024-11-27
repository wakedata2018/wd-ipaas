<template>
  <div class="bd-page">
    <list-page-layout class="bd-container">
      <fat-table ref="table" :fetch-handler="handleFetch" row-key="id" hide-on-single-page>
        <el-table-column prop="addressTypeName" label="地址名称" align="center"></el-table-column>
        <el-table-column prop="addressType" label="方式" align="center" min-width="160">
          <template slot-scope="scope">
            {{ type[scope.row.addressType] }}
          </template>
        </el-table-column>
        <el-table-column prop="bootstrapServers" label="地址" align="center" min-width="160"></el-table-column>
        <el-table-column prop="lastTime" label="最后修改时间" min-width="130" align="center" sortable>
          <!-- <template slot-scope="scope">
            {{ scope.row.createTime | timeFilter }}
          </template> -->
        </el-table-column>
        <el-table-column prop="lastPeople" label="最后修改人" align="center"></el-table-column>
        <el-table-column label="操作" min-width="180px" v-slot="scope" fixed="right" align="center">
          <table-actions :options="tableActions(scope.row)" />
        </el-table-column>
      </fat-table>
    </list-page-layout>
  </div>
</template>

<script>
  import requester from '@/api/event-manage';
  import { ListPageLayout, FatTable, TableActions } from '@/bz-components/list-page/index';
  // import { mapState } from 'vuex';

  export default {
    components: {
      ListPageLayout,
      TableActions,
      FatTable,
    },
    data() {
      return {
        type: [null, 'Http', 'Kafka', 'MQ'],
        show: false,
      };
    },
    // computed: {
    //   ...mapState({
    //     userInfo: 'userInfo',
    //   }),
    // },
    mounted() {
      this.getSubscribeList();
    },
    methods: {
      async getSubscribeList() {},
      async handleFetch(e) {
        const { data, success } = await requester.subscribeAddressList();
        if (success) {
          return { list: data };
        }
      },
      tableActions(row) {
        return [
          {
            name: '查看',
            onClick: () => {
              this.$router.push({ path: '/event-manage/subscribe/edit', query: { addressType: row.addressType } });
            },
          },
        ].filter(Boolean);
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
