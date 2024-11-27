<template>
  <div class="subscribe-edit">
    <list-page-layout class="bd-container">
      <div class="subscribe-edit-content">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>Ipaas</el-breadcrumb-item>
          <el-breadcrumb-item>订阅地址</el-breadcrumb-item>
          <el-breadcrumb-item>{{ type[addressType] }}地址</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
      <div class="subscribe-edit-content">
        <el-button type="primary" plain @click="$router.go(-1)">返回</el-button>
        <el-button v-if="addressType !== HTTP_TYPE.value" type="primary" @click="showDialog()">新增Topic</el-button>
      </div>
      <div class="subscribe-edit-content">
        <el-form inline label-position="left" label-width="80px" :model="form" class="base-info">
          <el-form-item label="地址名称:" class="base-info-item" prop="addressTypeName">
            <div>{{ form.addressTypeName }}</div>
          </el-form-item>
          <el-form-item label="消息最大长度:" class="base-info-item" prop="messageMaxLength" label-width="120px">
            <div>{{ form.messageMaxLength }}k</div>
          </el-form-item>
          <el-form-item label="类型:" class="base-info-item" prop="addressType">
            <div>{{ form.addressType }}</div>
          </el-form-item>
          <el-form-item class="base-info-item"></el-form-item>
          <el-form-item
            v-if="addressType !== HTTP_TYPE.value"
            label="集群地址:"
            class="base-info-item"
            prop="bootstrapServers"
          >
            <div>{{ form.bootstrapServers }}</div>
          </el-form-item>
        </el-form>
      </div>
      <fat-table ref="table" :fetch-handler="handleFetch" row-key="id" hide-on-single-page class="fat-table">
        <el-table-column v-if="addressType === 1">
          <el-table-column prop="apiName" label="API名称" align="center" show-overflow-tooltip> </el-table-column>
          <el-table-column prop="dataAssetName" label="资产表名称" align="center"></el-table-column>
          <el-table-column prop="apiDescription" label="API描述" align="center" min-width="160"></el-table-column>
          <el-table-column prop="dataAssetApiMethod" label="API Path" align="center"></el-table-column>
          <el-table-column prop="dataAssetPublishStatus" label="发布状态" align="center" min-width="50">
            <!-- <template slot-scope="scope">
              <span :style="{ color: scope.row.status ? '#67c23a' : '#f56c6c' }">
                {{ scope.row.status ? '启用' : '禁用' }}
              </span>
            </template> -->
          </el-table-column>
          <el-table-column prop="publisher" label="发布人" align="center"></el-table-column>
        </el-table-column>

        <el-table-column v-else>
          <el-table-column prop="topic" label="Topic名称" align="center" show-overflow-tooltip> </el-table-column>
          <el-table-column prop="desc" label="Topic描述" align="center"></el-table-column>
          <el-table-column prop="tag" label="Tag" align="center" min-width="160"></el-table-column>

          <el-table-column prop="updateTime" label="最后修改时间" min-width="130" align="center" sortable>
            <!-- <template slot-scope="scope">
            {{ scope.row.createTime | timeFilter }}
          </template> -->
          </el-table-column>
          <el-table-column prop="updateUser" label="最后修改人" align="center"></el-table-column>

          <el-table-column label="操作" min-width="180px" v-slot="scope" fixed="right" align="center">
            <table-actions :options="tableActions(scope.row)" />
          </el-table-column>
        </el-table-column>
      </fat-table>
    </list-page-layout>
    <subscribe-dialog
      :visible.sync="dialog"
      :alarm-info="alarmInfo"
      :address-type="addressType"
      @saved="onSave"
    ></subscribe-dialog>
  </div>
</template>

<script>
  import requester from '@/api/event-manage';
  import { ListPageLayout, FatTable, TableActions } from '@/bz-components/list-page/index';
  import subscribeDialog from './subscribe-dialog.vue';
  import { mapState } from 'vuex';
  import { HTTP_TYPE } from '@/enum';

  export default {
    components: {
      ListPageLayout,
      TableActions,
      FatTable,
      subscribeDialog,
    },
    data() {
      return {
        HTTP_TYPE,
        loading: false,
        addressType: parseInt(this.$route.query.addressType),
        type: [null, 'Http', 'Kafka', 'MQ'],
        // filterForm: {
        //   nameOrCode: '', // 事件名称/编码
        //   typeId: '', // 事件类型Id
        //   status: '', // 事件状态
        //   sortingFields: [], // 排序
        // },
        alarmInfo: null,
        form: {
          addressTypeName: null,
          bootstrapServers: null,
          messageMaxLength: null,
          addressType: null,
        },
        dialog: false,
      };
    },
    mounted() {
      const { id, isEdit } = this.$route.query;
      // if (id) this.initDetail(id);
      if (isEdit === '0') this.isEdit = false;
      else this.isEdit = true;
    },
    methods: {
      onSave() {
        this.$refs.table.debouncedSearch();
      },
      async handleFetch(e) {
        const params = {
          addressType: this.addressType || 1,
          // ...this.filterForm,
          // curUserId: this.userInfo.id,
          // pageSize: e.pagination.pageSize,
          // pageNo: e.pagination.currentPage,
        };
        const { data } = await requester.subscribeAddressDetail(params);

        this.form.addressType = this.type[data.addressType];
        this.form.addressTypeName = data.addressTypeName;
        this.form.bootstrapServers = data.bootstrapServers;
        this.form.messageMaxLength = data.messageMaxLength;
        return {
          list: data.topic || data.httpAddress || [],
        };
      },
      showDialog(val) {
        this.alarmInfo = null;
        this.dialog = true;
        if (val) {
          this.alarmInfo = val;
        }
      },
      tableActions(row) {
        return [
          {
            name: '修改',
            onClick: () => {
              this.showDialog(row);
            },
          },
          {
            name: '删除',
            onClick: async () => {
              this.cancelSubscribr(row);
            },
          },
        ].filter(Boolean);
      },
      cancelSubscribr(row) {
        this.$confirm('是否删除该Topic', '提示').then(async () => {
          const res = await requester.deleteSubscribeAddress({ id: row.id });
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
  .subscribe-edit {
    &-content {
      padding: 15px 30px;
      .base-info {
        max-width: 85%;
        &-item {
          width: 450px;
        }
      }
    }
    .event-edit-footer {
      position: fixed;
      left: 200px;
      bottom: 0;
      width: 100%;
      height: 80px;
      line-height: 80px;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
      text-align: center;
      background: #fff;
    }
  }
</style>
