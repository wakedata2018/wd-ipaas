<template>
  <div>
    <el-dialog
      :visible.sync="isShow"
      center
      title="更新API"
      class="update-api-dialog"
      :close-on-click-modal="false"
      v-loading="loading"
      width="400px"
    >
      <div>确定更新API吗？</div>
      <div class="bd-dialog-footer" slot="footer">
        <el-button size="medium" type="primary" class="bd-button" @click="isShow = false">取消</el-button>
        <el-button size="medium" type="primary" class="bd-button" @click="onSave">确定</el-button>
      </div>
    </el-dialog>
    <el-dialog
      :visible.sync="success.visible"
      center
      title="更新成功"
      class="update-api-dialog"
      :close-on-click-modal="false"
      v-loading="loading"
      width="400px"
    >
      <div>
        成功更新了{{ apiAmount }}个API
        <router-link :to="{ path: '/api-publish/apipublish', query: { apiGroupId } }" class="link">查看API</router-link>
      </div>
      <div class="bd-dialog-footer" slot="footer">
        <el-button size="medium" type="primary" class="bd-button" @click="onOk">我知道了</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import swaggerManage from '@/api/swagger-manage.js';

  export default {
    props: {
      visible: {
        type: Boolean,
        default: false,
      },
      swaggerInfoId: {
        type: Number,
        default: 0,
      },
    },
    data() {
      return {
        num: 0,
        loading: '',
        apiAmount: 0,
        apiGroupId: 0,
        success: {
          visible: false,
          num: 0,
        },
      };
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
    methods: {
      onSave() {
        this.loading = true;
        swaggerManage.updateSwaggerApi({ swaggerInfoId: this.swaggerInfoId }).then(
          res => {
            this.apiAmount = res.data.apiAmount;
            this.apiGroupId = res.data.apiGroupId;
            this.loading = false;
            this.isShow = false;
            this.success.visible = true;
          },
          err => {
            console.log(err);
            this.loading = false;
            this.isShow = false;
          }
        );
      },
      onOk() {
        this.success.visible = false;
        this.$emit('saved');
      },
    },
  };
</script>

<style lang="less" scoped>
  .content {
    margin-left: 30px;
    color: #bbb;
  }
  .link {
    color: #2776fb;
    text-decoration: none;
    margin-left: 10px;
  }
</style>
