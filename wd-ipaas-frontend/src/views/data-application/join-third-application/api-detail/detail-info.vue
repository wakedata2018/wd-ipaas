<template>
  <el-scrollbar class="page-scrollbar">
    <h1 v-if="!infoData.baseInfo" class="blank-container">该应用还没有授权接口，请先授权接口</h1>
    <div v-if="infoData.baseInfo" v-loading="loading" class="page-content">
      <div class="page-title">
        <div
          class="publish-box"
          :style="{
            backgroundColor:
              infoData && infoData.baseInfo && infoData.baseInfo.dataAssetPublishStatus === 'UN_PUBLISH'
                ? '#fb4938'
                : '#2776fb',
          }"
        >
          {{
            infoData && infoData.baseInfo && infoData.baseInfo.dataAssetPublishStatus === 'PUBLISH'
              ? '已发布'
              : '未发布'
          }}
        </div>
        <div class="title-text">
          {{ infoData ? infoData.baseInfo.apiName : '' }}
        </div>
        <div v-if="$route.query.id" class="relieve-button">
          <el-button type="primary" @click="onRelieve">解除授权</el-button>
        </div>
      </div>
      <div class="page-dec">
        <div>描述：</div>
        <div class="dec-text">
          {{ infoData.baseInfo.apiDescription || '-' }}
        </div>
      </div>
      <div class="info-box">
        <div class="info-title">调用信息</div>
        <span class="url">{{ url }}</span>
      </div>
      <div class="info-box">
        <div class="info-title">API基本信息</div>
        <el-row :gutter="20" class="el-row">
          <el-col :span="7">
            <div class="grid-content">
              <div class="row-item">API名称：{{ infoData.baseInfo.apiName || '' }}</div>
              <div class="row-item">创建人：{{ infoData.baseInfo.publisher || '' }}</div>
              <div class="row-item">创建时间：{{ infoData.baseInfo.createTime || '' }}</div>
            </div>
          </el-col>
          <el-col :span="7">
            <div class="grid-content">
              <div class="row-item">API路径：{{ infoData.baseInfo.dataAssetApiMethod || '' }}</div>

              <div class="row-item">最近一次修改时间：{{ infoData.baseInfo.updateTime || '' }}</div>
            </div>
          </el-col>
          <el-col :span="7">
            <div class="grid-content">
              <div class="row-item">HTTP请求方式：{{ infoData.baseInfo.reqMethod || '' }}</div>
              <div class="row-item">授权时间：{{ authDate || '' }}</div>
            </div>
          </el-col>
        </el-row>
      </div>
      <div class="info-box">
        <div class="info-title">请求参数</div>
        <el-tabs v-model="activeName">
          <el-tab-pane label="参数" name="param">
            <div class="params-title">公共参数</div>
            <el-table :data="infoData.requiredInput" class="dss-table bd-table">
              <el-table-column prop="required" label="必填" width="60">
                <template #default="scope">
                  <span>{{ scope.row.required == true ? '是' : '否' }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="assetColumns" label="参数名"></el-table-column>
              <el-table-column prop="assetDatatype" label="类型" width="80"></el-table-column>
              <el-table-column prop="sample" label="示例"></el-table-column>
              <el-table-column prop="descriptions" label="描述"></el-table-column>
            </el-table>
          </el-tab-pane>
          <el-tab-pane label="返回数据" name="data">
            <el-table :data="infoData.response" class="dss-table bd-table">
              <el-table-column prop="assetColumns" label="参数名"></el-table-column>
              <el-table-column prop="assetDatatype" label="类型"></el-table-column>
              <el-table-column prop="descriptions" label="描述"></el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </el-scrollbar>
</template>

<script>
  import { Message } from 'element-ui';
  import accessApi from '@/api/data-access-app.js';

  export default {
    props: {
      infoData: {
        type: Object,
        default: () => {
          return {
            baseInfo: {},
          };
        },
      },
      loading: {
        type: Boolean,
        default: false,
      },
      authDate: {
        type: String,
        default: '',
      },
      id: {
        type: Number,
        default: null,
      },
      filteredApiGroup: {
        type: Number,
        default: null,
      },
    },
    data() {
      return {
        activeName: 'param',
      };
    },
    computed: {
      url() {
        let url = '';
        const str = window.location.host + '/dw/open/api/';
        if (this.infoData.baseInfo && this.infoData.baseInfo.dataAssetApiMethod) {
          url = str + this.infoData.baseInfo.dataAssetApiMethod;
        }
        return url;
      },
      blankContainerClassName() {
        if (!this.infoData.baseInfo) {
          return 'blank-container';
        }
        return 'page-content';
      },
    },
    watch: {
      id(val) {
        if (val) {
          this.activeName = 'param';
        }
      },
    },
    mounted() {},
    methods: {
      copy(e) {
        if (e.text) {
          Message.closeAll();
          this.$message({
            message: '复制成功！',
            type: 'success',
          });
        }
      },
      onRelieve() {
        this.$confirm('是否确定移除该API?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
          .then(() => {
            accessApi
              .relieveThirdAuth({
                apiId: this.infoData.baseInfo.dataAssetApiId,
                authId: parseInt(this.$route.query.id),
              })
              .then(res => {
                this.$emit('update:filtered-api-group', null);
                this.$emit('get-api-list');
                this.$message({
                  type: 'success',
                  message: '删除成功!',
                });
              });
          })
          .catch(() => {
            console.log('取消移除');
          });
      },
    },
  };
</script>

<style lang="less" scoped>
  .blank-container {
    height: 50vh;
    width: 65vw;
    text-align: center;
    transform: translateY(55%);
  }
  .page-content {
    background: white;
    .page-title {
      padding: 0px 17px;
      display: flex;
      position: relative;
      align-items: center;
      .relieve-button {
        position: absolute;
        right: 50px;
      }
      .publish-box {
        width: 56px;
        height: 58px;
        background: #2776fb;
        border-radius: 0px 0px 2px 2px;
        font-size: 14px;
        font-weight: 400;
        color: #ffffff;
        text-align: center;
        line-height: 58px;
      }
      .title-text {
        font-size: 16px;
        font-weight: bold;
        color: #333333;
        margin-left: 10px;
      }
    }
    .page-dec {
      padding: 10px 17px;
      display: flex;
      font-size: 12px;
      .dec-text {
        word-wrap: break-word;
        word-break: break-all;
        overflow: hidden;
        width: 70%;
        color: #333333;
      }
    }
    .info-box {
      padding: 0px 17px;
      .info-title {
        width: 100%;
        height: 30px;
        font-size: 15px;
        font-weight: bold;
        border-bottom: 1px solid #e6e6e6;
      }
      .url {
        display: inline-block;
        max-width: 541px;
        width: 100%;
        // height: 30px;
        line-height: 30px;
        padding: 10px 0;
        // border: 1px solid #e6e6e6;
        font-size: 12px;
        color: #8d939d;
      }
      .info-input {
        width: 30%;
        margin: 20px 10px 20px 0px;
        // font-size: 16px;
        color: #bdbdbd;
      }
      .el-row {
        margin-bottom: 20px;
        .row-item {
          margin-top: 20px;
          font-size: 12px;
          word-break: break-all;
        }
      }
      /deep/ .el-tabs__nav-wrap::after {
        background: transparent;
      }
      .params-title {
        font-size: 14px;
      }
      .bd-table {
        margin-top: 10px;
      }
    }
    .page-scrollbar {
      overflow-x: hidden;
      overflow-y: hidden;
      /deep/ .el-scrollbar__wrap {
        overflow-x: hidden;
        max-height: calc(100vh - 170px);
      }
    }
  }
</style>
