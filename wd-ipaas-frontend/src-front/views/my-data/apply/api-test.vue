<template>
  <el-dialog
    fullscreen
    custom-class="anim-left"
    lock-scroll
    append-to-body
    class="bd-dialog api-test-dialog"
    title="API测试"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <el-scrollbar ref="scrollbar" v-loading="loading" class="page-scrollbar">
      <div class="scrollbar-content">
        <div class="api-info">
          <span style="font-weight: 800">API path:</span>
          <span> /{{ originalData && originalData.baseInfo ? originalData.baseInfo.dataAssetApiMethod : '' }} </span>
        </div>
        <div class="parameter">
          <div class="title">请求参数</div>
          <el-table :data="tableData" style="width: 100%">
            <el-table-column prop="assetColumns" label="参数名称"></el-table-column>
            <el-table-column prop="assetDatatype" label="参数类型"></el-table-column>
            <el-table-column prop="required" label="是否必填">
              <template slot-scope="scope">
                <span :style="`${!!scope.row.required ? 'color:#2776fb;font-weight: 600' : ''}`">{{
                  !scope.row.required ? '否' : '是'
                }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="sample" label="值">
              <template slot-scope="scope">
                <el-input
                  :type="
                    scope.row.assetDatatype.toLowerCase() === 'int' ||
                    scope.row.assetDatatype.toLowerCase() === 'float' ||
                    scope.row.assetDatatype.toLowerCase() === 'double' ||
                    scope.row.assetDatatype.toLowerCase() === 'decimal'
                      ? 'number'
                      : 'text'
                  "
                  v-model="scope.row.sample"
                  :disabled="scope.row.assetColumns == 'appId'"
                ></el-input>
              </template>
            </el-table-column>
          </el-table>
          <br />
          <!-- <el-button class="bd-button bd-strong test" @click="onTest">测试</el-button> -->
        </div>
        <div class="parameter" v-if="headersList.length > 0">
          <div class="title">Headers</div>
          <el-table :data="headersList" style="width: 100%">
            <el-table-column prop="assetColumns" label="参数名称"></el-table-column>
            <el-table-column prop="assetDatatype" label="参数类型"></el-table-column>
            <el-table-column prop="required" label="是否必填">
              <template slot-scope="scope">
                <span :style="`${!!scope.row.required ? 'color:#2776fb;font-weight: 600' : ''}`">{{
                  !scope.row.required ? '否' : '是'
                }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="sample" label="值">
              <template slot-scope="scope">
                <el-input
                  :type="
                    scope.row.assetDatatype.toLowerCase() === 'int' ||
                    scope.row.assetDatatype.toLowerCase() === 'float' ||
                    scope.row.assetDatatype.toLowerCase() === 'double' ||
                    scope.row.assetDatatype.toLowerCase() === 'decimal'
                      ? 'number'
                      : 'text'
                  "
                  v-model="scope.row.sample"
                  :disabled="scope.row.assetColumns == 'appId'"
                ></el-input>
              </template>
            </el-table-column>
          </el-table>
          <br />
          <!-- <el-button class="bd-button bd-strong test" @click="onTest">测试</el-button> -->
        </div>
        <div class="test-info">
          <div class="request-info copy-info">
            <div class="label">请求详情</div>
            <el-button
              type="text"
              class="bd-button bd-info copy"
              icon="el-icon-document-copy"
              v-clipboard:copy="requestInfo"
              v-clipboard:success="copy"
              v-clipboard:error="error"
              >复制</el-button
            >
          </div>
          <el-input
            type="textarea"
            :autosize="{ minRows: 10, maxRows: 20 }"
            v-model="requestInfo"
            :disabled="true"
          ></el-input>
          <div class="response-info">
            <div class="label">返回内容</div>
            <el-input
              type="textarea"
              :autosize="{ minRows: 10, maxRows: 20 }"
              v-model="responseInfo"
              :disabled="true"
            ></el-input>
          </div>
        </div>
      </div>
    </el-scrollbar>
    <div class="bd-dialog-footer">
      <el-button @click="dialogVisible = false" size="medium" class="cancel">取消</el-button>
      <el-button type="primary" @click="onTest" size="medium" class="bd-button">测试</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import apiTest from '../../../api/api-test';
  import daaAPI from '@/api/data-access-app';
  import { mapState } from 'vuex';
  import { Message } from 'element-ui';

  // import axios from 'axios'
  export default {
    props: {
      apiInfo: {
        type: Object,
        default() {
          return {};
        },
      },
      visible: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        tableData: [], // 接口数据信息
        headersList: [],
        requestInfo: '', // 请求信息
        responseInfo: '',
        host: '', // 存放浏览器host
        port: '', // 存放端口，
        loading: false,
        originalData: {
          baseInfo: {},
        },
      };
    },
    computed: {
      ...mapState({
        cardList: state => state.cardList,
      }),
      dialogVisible: {
        get() {
          return this.visible;
        },
        set(val) {
          this.$emit('update:visible', val);
        },
      },
    },
    watch: {
      dialogVisible() {
        this.init();
      },
    },
    methods: {
      hide() {
        this.dialogVisible = false;
        this.tableData = [];
      },
      show() {
        this.dialogVisible = true;
      },
      async init() {
        try {
          this.requestInfo = '';
          this.responseInfo = '';
          this.loading = true;
          const res = await apiTest.getTestInfo({
            dataAssetId: this.apiInfo.dataAssetApiId,
          });

          if (res.data.optionalInput) {
            res.data.optionalInput.forEach(item => {
              item.req = 'a';
            });
          }
          res.data.requiredInput.forEach(item => {
            if (item.assetColumns === 'appId') {
              item.sample = this.apiInfo.app.dataAccessKey;
            }
          });
          this.originalData = res.data;

          const list = res.data.requiredInput.concat(res.data.optionalInput);
          this.tableData = [];
          this.headersList = [];
          list.forEach(item => {
            if (item.httpParamKind === 'HEAD') {
              this.headersList.push(item);
            } else {
              this.tableData.push(item);
            }
          });

          const { app, dataAssetApiId } = this.apiInfo;
          if (app.dataAccessKey) {
            const resHeader = await daaAPI.getAuthParams(app.dataAccessKey, dataAssetApiId);
            const headCol = this.headersList.find(
              headerItem => headerItem.assetColumns === resHeader.data.assetColumns
            );
            if (headCol) {
              headCol.sample = resHeader.data.sample;
            } else if (resHeader.data) {
              this.headersList.push(resHeader.data);
            }
          }
          this.host = location.host;
          this.port = location.port;
        } finally {
          this.loading = false;
        }
      },
      onTest() {
        let api = this.originalData && this.originalData.baseInfo ? this.originalData.baseInfo.dataAssetApiMethod : '';
        let str = window.location.host + '/dw/open/api/';

        api += '?';
        let failed = false;
        this.tableData.forEach(item => {
          if (item.sample) {
            api += `${item.assetColumns}=${item.sample}&`;
          }
          if (!!item.required && (!item.sample || item.sample === '')) {
            failed = true;
          }
        });

        const headers = {};
        this.headersList.forEach(item => {
          headers[item.assetColumns] = item.sample;
        });

        if (failed) {
          Message.closeAll();
          this.$message.error('有必填参数未填写');
          return;
        }
        api = api.substring(0, api.length - 1);
        str += api;
        this.requestInfo = str;
        this.loading = true;
        apiTest
          .testApi(api, null, {
            headers,
          })
          .done(res => {
            this.responseInfo = JSON.stringify(res);
          })
          .always(() => {
            this.loading = false;
          });
      },
      copy(e) {
        if (e.text) {
          Message.closeAll();
          this.$message({
            message: '已复制到剪贴板',
            type: 'success',
          });
        }
      },
      error(e) {
        if (e.text) {
          Message.closeAll();
          this.$message({
            message: '复制失败',
            type: 'error',
          });
        }
      },
    },
  };
</script>

<style scoped lang="less">
  .api-test-dialog {
    .page-scrollbar {
      overflow-x: hidden;
      overflow-y: hidden;

      /deep/ .el-scrollbar__wrap {
        overflow-x: hidden;
        max-height: calc(100vh - 210px);
      }
      .scrollbar-content {
        padding: 10px;
        margin-bottom: 20px;
      }
      // .new-task-form {
      //   padding: 0 10px 17px 0;
      // }
    }

    .main-title {
      height: 22px;
      font-size: 16px;
      margin: 10px 40px;
      font-weight: 600;
      color: rgba(51, 51, 51, 1);
      line-height: 22px;
    }
    // /deep/ .el-input {
    //   max-width: 400px;
    // }
    /deep/ .el-textarea {
      width: 800px !important;
    }
    .comment {
      font-size: 13px;
      color: #2776fb;
    }
    .parameter {
      margin-top: 20px;
      .title {
        font-weight: 800;
        margin-bottom: 20px;
      }
    }
    .test-info {
      .copy-info {
        display: flex;
        align-items: center;
      }
      .label {
        font-weight: 800;
        margin: 20px 0;
      }
      /deep/.el-textarea__inner {
        height: 100px;
      }
    }
    .test {
      padding: 10px 20px;
    }
    .copy {
      margin-left: 20px;
    }
  }
</style>
