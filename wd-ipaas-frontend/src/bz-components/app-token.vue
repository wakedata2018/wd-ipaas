<template>
  <div>
    <el-dialog
      class="bd-dialog token not-lock-scroll"
      :close-on-click-modal="false"
      :visible.sync="isShow"
      v-loading="loading.saving"
      title="获取token"
      :lock-scroll="false"
      width="800px"
      :modal-append-to-body="false"
    >
      <el-form style="padding-bottom: 30px" :model="form" :rules="rules">
        <el-form-item label="token过期时间" prop="issuedAt">
          <el-date-picker
            type="datetime"
            popper-class="token-popper-datepicker"
            v-model="form.issuedAt"
            :picker-options="pickerOptions"
          />
        </el-form-item>
      </el-form>
      <div class="bd-dialog-footer" style="bottom: auto">
        <el-button size="medium" @click="onCancle()">取消</el-button>
        <el-button type="primary" size="medium" class="bd-button" @click="onSave"> 确定 </el-button>
      </div>
    </el-dialog>

    <el-dialog
      class="bd-dialog api-info not-lock-scroll"
      :close-on-click-modal="false"
      v-loading="loading.saving"
      title="token信息"
      :lock-scroll="false"
      :visible.sync="apiVisible"
      width="800px"
    >
      <el-form
        style="padding-bottom: 0px"
        :model="form"
        :rules="rules"
        class="api-form"
        label-width="90px"
        v-if="apiInfo && apiInfo.app"
      >
        <el-form-item label="APP ID：">
          <div>{{ apiInfo.app.dataAccessKey }}</div>
        </el-form-item>
        <el-form-item label="API名称：">
          <div>{{ apiInfo.apiName }}</div>
        </el-form-item>
        <el-form-item label="API Path：">
          <div>{{ apiInfo.dataAssetApiUri }}</div>
        </el-form-item>
        <el-form-item label="token：">
          <div>{{ token }}</div>
        </el-form-item>
        <el-form-item label="过期时间：">
          <div>{{ getDate() }}</div>
        </el-form-item>
      </el-form>
      <div class="tip">界面关闭后将无法获知到该token，请妥善保存!</div>
      <div class="bd-dialog-footer" style="bottom: auto">
        <el-button size="medium" @click="apiVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import daaAPI from '@/api/data-access-app';
  import { date } from 'dss-common';

  export default {
    props: {
      visible: {
        type: Boolean,
        default: false,
      },
      apiInfo: {
        type: Object,
        default: null,
      },
    },
    data() {
      return {
        loading: {
          saving: false,
        },
        form: {
          issuedAt: date.addDays(new Date(), 1),
        },
        rules: {
          issuedAt: [
            {
              required: true,
              message: '请选择过期时间',
            },
          ],
        },
        pickerOptions: {
          disabledDate(time) {
            return time.getTime() <= Date.now();
          },
        },
        apiVisible: false,
        token: '',
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
        const time = this.getDate();
        this.$confirm(`确定生成token?该token生效后将在${time}过期。`, '提示').then(
          () => {
            this.fetchToken(time);
          },
          () => {
            this.isShow = false;
          }
        );
      },
      async fetchToken(time) {
        try {
          const { dataAssetApiUri, app } = this.apiInfo;
          this.loading.saving = true;
          const res = await daaAPI.getTokenInternal(app.dataAccessKey, dataAssetApiUri, time);
          this.token = res.data;
          this.$emit('saved', res.data, this.apiInfo);
          this.isShow = false;
          this.apiVisible = true;
        } finally {
          this.loading.saving = false;
        }
      },
      onCancle() {
        this.isShow = false;
      },
      getDate() {
        return date.format(this.form.issuedAt, 'YYYY-MM-DD HH:mm:ss');
      },
    },
  };
</script>

<style lang="less" scoped>
  .token {
    ::v-deep .el-dialog {
      max-width: 400px;
    }
  }

  .tip {
    padding-bottom: 20px;
    color: #8d939d;
    font-size: 12px;
    text-align: center;
  }
  .api-form {
    ::v-deep .el-form-item__label {
      color: #333333;
      font-weight: bold;
    }
  }
</style>
