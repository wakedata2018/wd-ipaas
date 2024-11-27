<template>
  <el-dialog class="bd-dialog kafka-edit-dialog" :visible.sync="isShow" v-loading="loading.saving" :title="title">
    <el-form label-width="100px" ref="elForm" :model="form" :rules="rules">
      <el-form-item label="接口名称" prop="dataAssetApiUri">
        <el-input placeholder="接口名称" v-model="form.dataAssetApiUri"></el-input>
      </el-form-item>
      <el-form-item prop="apiName">
        <span slot="label">中文名称</span>
        <el-input placeholder="请输入描述" v-model="form.apiName"></el-input>
      </el-form-item>
    </el-form>
    <div class="bd-dialog-footer" style="bottom: auto;">
      <el-button size="medium" @click="onCancle()">取消</el-button>
      <el-button
        type="primary"
        size="medium"
        class="bd-button"
        @click="onSave"
      >保存</el-button>
    </div>

  </el-dialog>
</template>

<script>
export default {
  props: {
    appInfo: {
      type: Object,
      default() {
        return null;
      }
    },
    visible: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      loading: {
        saving: false
      },
      form: {
        dataAssetApiUri: '',
        apiName: '',
      },
      rules: {
        dataAssetApiUri: [
          {
            required: true,
            message: '请输入接口名称'
          }
        ],
        apiName: [
          {
            required: true,
            message: '请输入接口中文名称'
          }
        ]
      }
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
    isEdit() {
      return !!this.appInfo;
    },
    title() {
      return this.isEdit ? '修改' : '新增';
    }
  },
  watch: {
    isShow(val) {
      this.resetFields();
      if (val) {
        this.setValues();
      }
    }
  },
  methods: {
    resetFields() {
      const form = this.$refs.elForm;
      if (form && form.resetFields) {
        form.resetFields();
      }
      this.form={}
    },
    setValues() {
      if (!this.appInfo) {
        return;
      }
      this.form=this.appInfo
    },
    onCancle() {
      this.isShow = false;
    },
    onSave() {
      const form = this.$refs.elForm;
      form.validate(isVlidate => {
        if (!isVlidate) {
          return;
        }
        this.update();
      });
    },
    update() {
      console.log('更新操作')
    },
  }
};
</script>

<style lang="less" scoped>
  /deep/ .el-dialog{
    margin-top:20vh !important;
  }
</style>