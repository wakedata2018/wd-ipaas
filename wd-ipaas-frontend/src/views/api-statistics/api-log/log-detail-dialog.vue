<template>
  <el-dialog
    v-loading="loading"
    class="bd-dialog log-detail"
    fullscreen
    :visible.sync="dialogVisible"
    title="调用详情"
    custom-class="anim-left"
    append-to-body
    :close-on-click-modal="false"
    lock-scroll
  >
    <el-scrollbar ref="scrollbar" class="page-scrollbar">
      <el-form :model="form" class="form-detail" label-width="auto">
        <el-form-item label="接口名称">
          {{ form?.dataAssetName ?? '-' }}
        </el-form-item>
        <el-form-item label="接口地址">
          {{ form?.dataAssetMethod ?? '-' }}
        </el-form-item>
        <el-row>
          <el-col :span="8">
            <el-form-item label="调用时间">
              {{ form?.createTime ?? '-' }}
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="返回代码">
              {{ form?.resultCode ?? '-' }}
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="应用名称">
              {{ form?.dataAccessName ?? '-' }}
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="请求参数" class="params-title"> </el-form-item>
        <json-viewer
          :value="requestParams(form?.dataRequestParam ?? '')"
          :expand-depth="5"
          copyable
          boxed
          sort
        ></json-viewer>
        <el-form-item label="返回结果" class="params-title"> </el-form-item>
        <json-viewer
          :value="responseParams(form?.dataResponseParam ?? '')"
          :expand-depth="5"
          copyable
          boxed
          sort
        ></json-viewer>
      </el-form>
    </el-scrollbar>
    <div slot="footer" class="bd-dialog-footer">
      <el-button type="primary" class="dss-btn-circle" @click="handleClose">关闭</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import JsonViewer from 'vue-json-viewer';

  import apiLog from '@/api/api-log.js';

  export default {
    name: 'LogDetailDialog',
    components: { JsonViewer },
    props: {
      value: {
        type: Number,
        default: null,
      },
      visible: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        isShow: false,
        loading: false,
        form: {},
      };
    },
    computed: {
      dialogVisible: {
        get() {
          return this.visible;
        },
        set(val) {
          this.$emit('update:visible', val);
        },
      },
      requestParams() {
        return val => {
          return (val && JSON.parse(val)) || {};
        };
      },
      responseParams() {
        return val => {
          return (val && JSON.parse(val)) || {};
        };
      },
    },
    created() {
      this.getDetail();
    },
    methods: {
      handleClose() {
        this.$emit('update:visible', false);
      },
      async getDetail() {
        try {
          this.loading = true;
          const res = await apiLog.getLogDetail({ id: this.value });
          this.form = res.data;
        } catch (err) {
          this.$message.error(err);
        } finally {
          this.loading = false;
        }
      },
    },
  };
</script>
<style scoped lang="less">
  .log-detail {
    ::v-deep .el-dialog.is-fullscreen {
      width: 50%;
    }
    .page-scrollbar.mac {
      /deep/ .el-scrollbar__wrap {
        max-height: calc(100vh - 230px);
      }
    }

    .page-scrollbar {
      overflow-x: hidden;
      overflow-y: hidden;
      margin-bottom: 20px;

      /deep/ .el-scrollbar__wrap {
        overflow-x: hidden;
        max-height: calc(100vh - 210px);
      }
    }
    .form-detail {
      padding: 10px;
      margin-bottom: 40px;

      .params-title {
        margin: 5px 0px;
      }
    }
  }
</style>
