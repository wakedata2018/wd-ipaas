<template>
  <div class="page-api-approve bd-page">
    <div class="bd-header">
      <div class="title">{{ title }}</div>
    </div>
    <div class="bd-container">
      <div class="bd-search condition">
        <el-form size="mini" label-position="right" class="bd-form" inline label-width="100px">
          <el-form-item label="API名称">
            <el-input v-model.trim="query.keyword" type="text" maxlength="32" placeholder="请输入API名称" />
          </el-form-item>
          <el-form-item prop="approvalStatus" label="审批状态">
            <el-select v-model="query.approvalStatus" placeholder="请选择审批状态">
              <el-option
                v-for="item in Object.entries(ApprovalStatusEnums)"
                :key="item[1].value"
                :value="item[0]"
                :label="item[1].label"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="租户账号">
            <el-input v-model.trim="query.lesseeName" type="text" maxlength="32" placeholder="请输入租户账号" />
          </el-form-item>
        </el-form>
        <div class="bd-search-group">
          <el-button type="primary" class="bd-button bd-strong" @click="onSearch">查询</el-button>
          <el-button type="primary" plain size="mini" class="bd-button bd-strong" @click="onReset">重置</el-button>
        </div>
      </div>
      <div class="assets-table theme-table">
        <el-table v-loading="loading.list" :data="tableData" style="width: 100%" class="dss-table bd-table">
          <el-table-column prop="apiName" label="API名称" show-overflow-tooltip></el-table-column>
          <el-table-column prop="apiPath" label="API Path" show-overflow-tooltip></el-table-column>
          <el-table-column prop="lesseeName" label="租户账号"></el-table-column>
          <!-- <el-table-column prop="dataAccessAppName" label="接入名称" show-overflow-tooltip></el-table-column> -->
          <el-table-column prop="submitTime" label="申请时间" show-overflow-tooltip></el-table-column>
          <el-table-column prop="submitter" label="申请人" align="center" show-overflow-tooltip></el-table-column>
          <el-table-column prop="approvalMessage" label="审批意见" show-overflow-tooltip></el-table-column>
          <el-table-column prop="status" label="审批状态" align="center" width="80">
            <template #default="scope">
              <span>
                {{ approvalStatus(scope.row.approvalStatus) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column v-if="authorize(permitList, 'passing')" label="操作" align="center" width="100">
            <template #default="scope">
              <el-button
                type="text"
                :disabled="scope.row.approvalStatus !== ApprovalStatusEnums['IN_APPROVAL'].value"
                @click="onApproval(scope.row, true)"
                >通过</el-button
              >
              <el-button
                type="text"
                :disabled="scope.row.approvalStatus !== ApprovalStatusEnums['IN_APPROVAL'].value"
                @click="onApproval(scope.row, false)"
                >拒绝</el-button
              >
            </template>
          </el-table-column>
        </el-table>
        <!-- <pager
            :page-size.sync="page.pageSize"
            :current-page.sync="page.pageNo"
            :total="total"
            :requestFn="getList"
        />-->
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
        <div class="page">
          <!-- <el-pagination
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              style="float:right;"
              :current-page.sync="page.pageNo"
              :page-sizes="[7,14,21]"
              :page-size.sync="page.pageSize"
              layout="total, sizes, prev, pager, next, jumper"
              :total="total"
              :requestFn="getList"
          ></el-pagination>-->
          <!-- <pager
            :page-size.sync="page.pageSize"
            :current-page.sync="page.pageNo"
            :total="total"
            :requestFn="getList"
            style="float:right;"
          />-->
        </div>
        <approval-dialog
          :visible.sync="dialog.approve"
          :approval-id="approvalId"
          :action="approvalAction"
          @saved="onSaved"
        ></approval-dialog>
      </div>
    </div>
  </div>
</template>

<script>
  import { mapState } from 'vuex';
  import approvalAPI from '@/api/approval.js';
  import { APPROVAL_STATUS } from '@/enum.js';
  import IPagesize from '../../../mixins/i-pagesize.js';
  import ApprovalDialog from './approval-dialog.vue';
  import { authorize } from '@/utils/authorize';
  // import DssSelect from "@/components/dss-select/index.vue";

  import pageUtils from '@/utils/page.js';

  export default {
    components: { ApprovalDialog },
    mixins: [IPagesize],
    data() {
      return {
        tableData: [],
        tableProp: 'tableData',
        ApprovalStatusEnums: APPROVAL_STATUS, // 审批状态
        loading: {
          list: false,
        },
        authorize,
        totalCount: 0,
        page: {
          pageSize: 10,
          pageNo: 1,
        },
        query: {
          keyword: '',
          approvalStatus: 'ALL',
          lesseeName: '',
        },
        dialog: {
          approve: false,
        },
        approvalId: null,
        approvalAction: null, // 审批操作 通过 / 拒绝
      };
    },
    computed: {
      ...mapState({
        permitList: 'permitList',
      }),
      title() {
        return pageUtils.findPageName(this.permitList, this.$route.name);
      },
    },
    created() {
      this.getList();
    },
    methods: {
      approvalStatus(approvalStatus) {
        const field = Object.values(this.ApprovalStatusEnums).find(item => {
          return item.value === approvalStatus;
        });
        return field.label;
      },
      getList() {
        this.loading.list = true;
        const params = {
          ...this.page,
          ...this.query,
          approvalType: 'DATA_ACCESS',
        };

        if (this.query.approvalStatus === 'ALL') {
          params.approvalStatus = '';
        }
        approvalAPI
          .queryApprovalList(params)
          .done(res => {
            this.tableData = res.data;
            this.totalCount = res.totalCount;
          })
          .always(() => {
            this.loading.list = false;
          });
      },
      getGroup(id) {
        // console.log(id);
      },
      onApproval(applyInfo, status) {
        this.approvalId = applyInfo.approvalId;
        this.approvalAction = status;
        this.dialog.approve = true;
      },
      onSaved() {
        this.getList();
      },
      onSearch() {
        this.page.pageNo = 1;
        this.getList();
      },
      onReset() {
        this.query.keyword = '';
        this.query.approvalStatus = 'ALL';
        this.query.lesseeName = '';
        this.page.pageNo = 1;
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
    },
  };
</script>

<style scoped lang="less">
  .page-api-approve {
    box-sizing: border-box;
    margin-bottom: 50px;

    .assets-table {
      background: white;
      padding-bottom: 15px;
    }
  }
</style>
