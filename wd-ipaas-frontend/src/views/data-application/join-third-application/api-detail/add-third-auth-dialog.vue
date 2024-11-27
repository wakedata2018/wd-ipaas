<template>
  <div>
    <el-dialog title="新增授权第三方API" :visible.sync="isShow" width="800px" center v-loading="authLoading">
      <el-form ref="elForm" :model="form" inline>
        <el-form-item prop="apiGroup">
          <el-select ref="groupSelect" v-model="groupName" placeholder="API接口分类" v-loading="apiGroupListLoading">
            <el-option :value="groupName" style="height: auto" class="tree-option">
              <el-tree
                ref="tree"
                node-key="id"
                :data="apiGroupList"
                :expand-on-click-node="false"
                :props="defaultProps"
                @node-click="changeApiGroup"
              ></el-tree>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="apiName">
          <el-input v-model="form.apiName" placeholder="请输入API名称"></el-input>
        </el-form-item>
        <el-form-item label="我创建的API" prop="inCharge">
          <el-checkbox v-model="mySearch" @change="handleMySearchChange"></el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onCurrentChange(1)">搜索</el-button>
        </el-form-item>
      </el-form>

      <el-table
        :data="tableData"
        tooltip-effect="dark"
        style="width: 100%"
        @selection-change="handleSelectionChange"
        v-loading="tableLoading"
      >
        <el-table-column type="selection" width="50" align="center"></el-table-column>
        <el-table-column label="名称" align="center" prop="apiName"></el-table-column>
        <el-table-column label="描述" align="center" prop="apiDescription"></el-table-column>
        <el-table-column label="路径" align="center" prop="dataAssetApiMethod"></el-table-column>
        <el-table-column label="发布人" align="center" prop="inCharge"></el-table-column>
      </el-table>
      <div style="text-align: right; background-color: white">
        <el-pagination
          layout="total, sizes, prev, pager, next"
          :total="totalCount"
          :current-page="page.pageNo"
          :page-size="page.pageSize"
          @size-change="onSizeChange"
          @current-change="onCurrentChange"
        ></el-pagination>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="onSave" style="width: 50%" :disabled="!selectNum"
          >确 定（已选择：{{ selectNum }}）</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>
<script>
  import apiGroup from '@/api/api-group';
  import dataAccessAppApi from '@/api/data-access-app';
  import apiCol from '@/api/api-controller';
  import { PUBLISH_STATUS } from '@/utils/enum/third-app';

  export default {
    props: {
      visible: {
        type: Boolean,
        default: false,
      },
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
      loginName() {
        return this.$store.state.user.loginName;
      },
      appId() {
        return parseInt(this.$route.query.id);
      },
    },
    watch: {
      isShow: {
        immediate: true,
        handler(val) {
          this.resetFields();
          if (val) this.getApiList();
        },
      },
    },
    data() {
      return {
        defaultProps: {
          children: 'children',
          label: 'groupName',
        },
        form: {
          apiGroupId: null,
          apiName: '',
        },
        tableData: [],
        totalCount: 0,
        page: {
          pageNo: 1,
          pageSize: 10,
        },
        mySearch: false,
        addAuthParamsList: [],
        tableLoading: false,
        inCharge: '',
        apiGroupListLoading: false,
        authLoading: false,
        apiGroupList: [],
        groupName: '',
        selectNum: 0,
      };
    },
    mounted() {
      this.getApiGroupList();
    },
    methods: {
      async getApiList() {
        const params = {
          ...this.page,
          ...this.form,
          publishStatus: PUBLISH_STATUS.PUBLISH.value,
          authId: this.appId,
          inCharge: this.inCharge,
        };
        this.tableLoading = true;
        apiCol
          .getList(params)
          .then(res => {
            const { data, totalCount } = res;
            this.tableData = data || [];
            this.totalCount = totalCount ?? 0;
          })
          .finally(() => {
            this.tableLoading = false;
          });
      },
      resetFields() {
        const form = this.$refs.elForm;
        if (form && form.resetFields) {
          form.resetFields();
        }
        this.form.apiGroupId = null;
        this.form.apiName = '';
        this.page.pageNo = 1;
        this.page.pageSize = 10;
        this.mySearch = false;
        this.groupName = '';
      },
      handleSelectionChange(val) {
        this.selectNum = val.length;
        this.addAuthParamsList = val.map(item => ({ apiId: item.dataAssetApiId }));
      },
      handleMySearchChange() {
        this.inCharge = this.mySearch ? this.loginName : null;
      },
      onSizeChange(val) {
        this.page.pageSize = val;
        this.onCurrentChange(1);
      },
      onCurrentChange(val) {
        this.page.pageNo = val;
        this.getApiList();
      },
      onSave() {
        this.authLoading = true;
        this.addAuthParamsList.forEach(item => {
          item.authInfoId = this.appId;
          item.createBy = this.loginName;
        });
        dataAccessAppApi
          .addThirdAuth(this.addAuthParamsList)
          .then(() => {
            this.$message.success('保存成功');
            this.$emit('success');
            this.isShow = false;
          })
          .finally(() => {
            this.authLoading = false;
          });
      },
      getApiGroupList() {
        this.apiGroupListLoading = true;
        apiGroup
          .apiGroupList()
          .then(res => {
            this.apiGroupList = res.data || [];
          })
          .finally(() => {
            this.apiGroupListLoading = false;
          });
      },
      changeApiGroup() {
        const res = this.$refs.tree.getCurrentNode();
        if (res) {
          this.groupName = res.groupName;
          this.form.apiGroupId = res.id;
        }
        this.$nextTick(() => {
          this.$refs.groupSelect.handleClose();
        });
      },
    },
  };
</script>
<style lang="less" scoped>
  .tree-option {
    padding: 0px !important;
  }
  .scrollbar {
    box-sizing: border-box;
    overflow-x: hidden;
    overflow-y: hidden;
    margin-bottom: 10px;
    /deep/ .el-scrollbar__wrap {
      overflow-x: hidden;
      max-height: calc(100vh - 400px);
    }
  }
</style>
