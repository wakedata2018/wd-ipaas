<template>
  <el-scrollbar ref="scrollbar" v-loading="loading" class="page-scrollbar">
    <div class="assets-content">
      <el-tabs v-model="activeName" @tab-click="hanldeChange">
        <el-tab-pane label="参数" name="param">
          <el-table :data="params" class="dss-table bd-table" v-loading="loading">
            <el-table-column prop="required" label="必填" width="60"></el-table-column>
            <el-table-column prop="assetColumns" label="参数名"></el-table-column>
            <el-table-column prop="assetDatatype" label="类型" width="80"></el-table-column>
            <el-table-column prop="sample" label="示例"></el-table-column>
            <el-table-column prop="descriptions" label="描述"></el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="返回数据" name="data">
          <el-table :data="cols" class="dss-table bd-table" v-loading="loading">
            <el-table-column prop="datasourceTableColumnName" label="字段名称"></el-table-column>
            <el-table-column prop="datasourceTableColumnType" label="字段类型"></el-table-column>
            <el-table-column prop="datasourceTableColumnDesc" label="描述"></el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </div>
  </el-scrollbar>
</template>

<script>
  import assetAPI from '@/api/data-asset-api-old.js';
  import accessAPI from '@/api/app-access-times.js';
  export default {
    props: {
      apiInfo: {
        type: Object,
        default() {
          return {};
        },
      },
      id: {
        type: Number,
        default: null,
      },
      // visible: {
      //   type: Boolean,
      //   default: false
      // }
    },
    data: () => ({
      // dialogVisible: false,
      loading: false,
      cols: null,
      params: [],
      activeName: 'param',
      dataAssetId: 1,
      baseInfo: null,
    }),
    watch: {
      apiInfo() {
        this.init();
      },
    },
    created() {
      this.init();
    },
    methods: {
      init() {
        this.dataAssetId = this.apiInfo.dataAssetApiId || 1;
        const p2 = this.getParams();
        this.loading = true;
        Promise.all([p2]).always(() => {
          this.loading = false;
        });
      },
      hanldeChange() {
        if (!this.cols) {
          this.getColumns();
        }
      },
      getColumns() {
        this.cols = [];
        assetAPI
          .columnList({
            dataSourceId: this.apiInfo.dataSourceId,
            tableName: this.apiInfo.dataAssetName,
          })
          .done(res => {
            this.cols = res.data;
            // console.log(this.cols);
          });
      },
      getParams() {
        this.params = [];
        const params = {
          timestamp: new Date().getTime(),
          dataAssetId: this.dataAssetId,
        };
        return accessAPI.assetDetail(params).done(res => {
          // console.log(res);
          const data = res.data;
          if (!data) {
            return;
          }
          const requiredParams = data.requiredInput || [];
          const optionalParams = data.optionalInput || [];
          requiredParams.forEach(item => {
            item.required = '是';
          });
          optionalParams.forEach(item => {
            item.required = '否';
          });
          this.params = requiredParams.concat(optionalParams);
        });
      },
    },
  };
</script>

<style scoped lang="less">
  .grid-content {
    line-height: 30px;
  }
  .assets-details-bar {
    width: 100%;
    margin-bottom: 20px;
    // border: 1px solid #ebeef5;
    box-sizing: border-box;
    overflow: hidden;
    .assets-details-row {
      text-align: left;
      .label-text {
        padding-right: 30px;
        color: #2776fb;
      }
    }
  }
  .page-scrollbar {
    overflow-x: hidden;
    overflow-y: hidden;
    margin-bottom: 10px;
    /deep/ .el-scrollbar__wrap {
      overflow-x: hidden;
      max-height: calc(100vh - 180px);
      .assets-content {
        padding: 20px;
        box-sizing: border-box;
        /deep/ .el-tabs__nav-wrap::after {
          background: transparent;
        }
      }
    }
    // .new-task-form {
    //   padding: 0 10px 17px 0;
    // }
  }
</style>
