<!-- eslint-disable vue/no-mutating-props -->
<template>
  <el-form ref="form" :model="config" :rules="rules" label-width="140px" inline @submit.native.prevent>
    <el-form-item label="步骤名称" prop="name">
      <template slot="label">
        <tips-icon content="英文字母开头，仅包含数字、字母和下划线"></tips-icon>
        步骤名称
      </template>
      <el-input
        v-model.trim="config.name"
        size="mini"
        :disabled="isReadonly"
        maxlength="200"
        placeholder="英文字母开头，仅包含数字、字母和下划线"
      />
    </el-form-item>

    <el-form-item label="步骤描述" prop="desc">
      <template slot="label"> 步骤描述 </template>
      <el-input
        v-model.trim="config.desc"
        size="mini"
        :disabled="isReadonly"
        maxlength="50"
        placeholder="不超过50个字符"
      >
      </el-input>
    </el-form-item>
  </el-form>
</template>

<script>
  import TipsIcon from '@/components/tips-icon';
  import Validator from '@/utils/validator.js';

  export default {
    components: { TipsIcon },
    props: {
      config: {
        type: Object,
        default: () => ({}),
      },
      isReadonly: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        rules: {
          name: [{ required: true, validator: Validator.enNameValidator, trigger: 'blur' }],
          desc: [
            { required: true, message: '步骤描述不能为空', trigger: 'blur' },
            { max: 50, message: '不超过50个字符', trigger: 'blur' },
          ],
        },
        path: '',
      };
    },
    computed: {
      dataAssetApi() {
        return this.config?.dataAssetApi ?? {};
      },
      isEdit() {
        return this.dataAssetApi?.dataAssetApiId != null;
      },
      disabled() {
        return this.isReadonly || this.isEdit;
      },
    },
    methods: {
      clearValidate(keys) {
        this.$nextTick(() => {
          this.$refs.form?.clearValidate(keys);
        });
      },
      validate() {
        return this.$refs.form?.validate();
      },
    },
  };
</script>

<style lang="less" scoped>
  /deep/ .el-input,
  /deep/ .el-input-number,
  /deep/ .el-select {
    width: 350px;
  }

  /deep/ .el-textarea {
    width: 650px;
  }

  /deep/ .el-input {
    .el-input-group__prepend {
      max-width: 80px !important;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
    }
  }
</style>
