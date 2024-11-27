<template>
  <el-dialog
    class="bd-dialog error-detail"
    fullscreen
    :visible.sync="dialogVisible"
    v-loading="loading"
    title="错误详情"
    custom-class="anim-left"
    append-to-body
    :close-on-click-modal="false"
    lock-scroll
  >
    <el-scrollbar ref="scrollbar" class="page-scrollbar">
      <el-form :model="value" class="form-detail" label-width="auto">
        <el-row>
          <el-col :span="12">
            <el-form-item label="调用时间">
              {{ value?.time ?? '' }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="应用ID">
              {{ value?.id ?? '' }}
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="接口名称">
          {{ value?.primaryName ?? '' }}
        </el-form-item>
        <el-form-item label="接口地址">
          {{ value?.secondaryName ?? '' }}
        </el-form-item>
        <el-form-item label="错误码">
          {{ value?.resultValue ?? '' }}
        </el-form-item>
        <el-form-item label="错误日志">
          {{ value?.resultDescription ?? '' }}
        </el-form-item>
      </el-form>
    </el-scrollbar>
    <div slot="footer" class="bd-dialog-footer">
      <el-button class="dss-btn-circle" @click="handleClose" type="primary">关闭</el-button>
    </div>
  </el-dialog>
</template>

<script>
  export default {
    name: 'ErrorDetailDialog',
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
    },
    methods: {
      handleClose() {
        this.$emit('update:visible', false);
      },
    },
  };
</script>
<style scoped lang="less">
  .error-detail {
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
