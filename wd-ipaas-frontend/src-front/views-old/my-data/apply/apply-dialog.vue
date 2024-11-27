<template>
  <el-dialog :visible.sync="isShow" class="view-api" title="API申请" v-loading="loading" width="100%">
    <div class="assets-details-bar">
      <el-row :gutter="20" class="assets-details-row">
        <el-col :span="12">
          <div class="grid-content">
            <span class="label-text">资产名称：</span>
            <span class="value-text">{{curAsset.dataAssetName}}</span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="grid-content">
            <span class="label-text">API Path：</span>
            <span class="value-text">{{curAsset.dataAssetApiUri}}</span>
          </div>
        </el-col>
      </el-row>
    </div>
    <access-table :data="curAsset.dataAccessRuleDetailList || []" />
    <div class="dialog-footer">
      <el-button @click="isShow = false">取消</el-button>
      <el-button type="primary" @click="onApply">申请</el-button>
    </div>
  </el-dialog>
</template>

<script>
import approvalAPI from "../../../api/approval.js";
import AccessTable from "../../../bz-components/access-table.vue";

export default {
  components: { AccessTable },
  props: {
    appId: {
      type: Number,
      default: null
    },
    assetInfo: {
      type: Object,
      default() {
        return {};
      }
    },
    visible: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      loading: false
    };
  },
  computed: {
    isShow: {
      get() {
        return this.visible;
      },
      set(val) {
        this.$emit('update:visible', val);
      }
    },
    curAsset() {
      return this.assetInfo || {};
    }
  },
  methods: {
    onApply() {
      const cols = this.getCheckedApply(this.filterAssetList);
      if (cols.length === 0) {
        this.$message('请选择要申请的资产。');
        return;
      }
      const applyList = [
        {
          dataAccessAppId: this.appId,
          dataAccessRuleFieldList: cols.map(col => col.datasourceTableColumnName),
          dataAssetApiId: this.curAsset.dataAssetApiId
        }
      ];
      this.loading = true;
      approvalAPI
        .dataAccessRule(applyList)
        .done(res => {
          this.$message({
            message: '您已申请成功',
            type: 'success'
          });
          this.$emit('applied');
          this.isShow = false;
        })
        .always(() => {
          this.loading = false;
        });
    },
    getCheckedApply(list) {
      const rules = this.curAsset.dataAccessRuleDetailList;
      if (!rules || rules.length === 0) {
        return [];
      }
      return rules.filter(rule => rule.apply && !rule.authorize);
    }
  }
};
</script>

<style lang="less" scoped>
.view-api {
  /deep/ .el-dialog {
    max-width: 1000px;
  }
  .grid-content {
    line-height: 28px;

    .label-text {
      display: inline-block;
      width: 90px;
      text-align: right;
      padding-right: 2px;
      box-sizing: border-box;
      color: #606266;
    }
  }
  .assets-details-bar {
    width: 100%;
    margin-bottom: 20px;
    // border: 1px solid #ebeef5;
    box-sizing: border-box;

    .assets-details-row {
      text-align: left;
    }
  }
  .dialog-footer {
    margin-top: 15px;
    text-align: right;
  }
}
</style>
