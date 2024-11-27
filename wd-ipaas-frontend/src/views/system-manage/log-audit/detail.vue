<template>
  <el-dialog
    class="bd-dialog log-detail"
    fullscreen
    :visible.sync="dialogVisible"
    v-loading="loading"
    title="日志详情"
    custom-class="anim-left"
    append-to-body
    :close-on-click-modal="false"
    lock-scroll
  >
    <el-scrollbar ref="scrollbar" class="page-scrollbar">
      <el-form :model="value" class="form-detail" label-width="auto">
        <el-row>
          <el-col :span="12">
            <el-form-item label="操作时间">
              {{ value?.actionTime ?? '' }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="操作人">
              {{ operatorDesc }}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="所属模块">
              {{ value?.pageResource ?? '' }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="事件类型">
              {{ value?.pageEvent ?? '' }}
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="接口地址">
          {{ value?.requestUrl ?? '' }}
        </el-form-item>
        <el-form-item label="请求参数"> </el-form-item>
        <json-viewer
          :value="requestParams(value?.requestParams ?? '')"
          :expand-depth="5"
          copyable
          boxed
          sort
        ></json-viewer>
      </el-form>
    </el-scrollbar>
    <div slot="footer" class="bd-dialog-footer">
      <el-button class="dss-btn-circle" @click="handleClose" type="primary">关闭</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import JsonViewer from 'vue-json-viewer';

  export default {
    name: 'LogDetailDialog',
    components: { JsonViewer },
    props: {
      value: {
        type: Object,
        default: () => ({}),
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
        form: this.value,
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
      operatorDesc() {
        const user = this.value?.actionUser ?? '';
        const ip = this.value?.ip ?? '';
        return (user && ip && `${user}[${ip}]`) || '';
      },
    },
    methods: {
      handleClose() {
        this.$emit('update:visible', false);
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
    }
  }
</style>
