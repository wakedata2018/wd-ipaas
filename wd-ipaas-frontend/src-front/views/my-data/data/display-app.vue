<template>
  <el-dialog class="bd-dialog" :visible.sync="isShow" title="查看应用">
    <el-form label-width="100px" v-if="!!appInfo">
      <el-form-item label="接入名称：">
        <span>{{ appInfo.dataAccessAppName }}</span>
      </el-form-item>
      <el-form-item label="APP ID：">
        <span>{{ appInfo.dataAccessKey }}</span>
      </el-form-item>
      <el-form-item label="接入密钥：">
        <span>{{ appInfo.dataAccessSecret }}</span>
      </el-form-item>
      <el-form-item label>
        <!-- <div>界面关闭后将无法再获取到密钥，请妥善保存</div> -->
      </el-form-item>
    </el-form>
    <div class="dialog-footer" slot="footer">
      <el-button type="primary" @click="isShow = false">关闭</el-button>
    </div>
  </el-dialog>
</template>

<script>
  export default {
    props: {
      appInfo: {
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
    computed: {
      isShow: {
        get() {
          return this.visible;
        },
        set(val) {
          this.$emit('update:visible', val);
        },
      },
    },
    methods: {},
  };
</script>
