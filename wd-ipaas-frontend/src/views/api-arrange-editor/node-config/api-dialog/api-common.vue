<!-- eslint-disable vue/no-mutating-props -->
<template>
  <el-form
    ref="form"
    :model="config"
    :rules="rules"
    label-position="right"
    label-width="140px"
    style="max-width: 900px"
    inline
    @submit.native.prevent
  >
    <el-form-item label="步骤名称" prop="name">
      <template slot="label">
        <tips-icon content="英文字母开头，仅包含数字、字母和下划线"></tips-icon>
        步骤名称
      </template>
      <el-input v-model.trim="config.name" size="mini" :disabled="isReadonly" maxlength="50" />
    </el-form-item>
    <el-form-item label="步骤描述" prop="desc">
      <template slot="label"> 步骤描述 </template>
      <el-input v-model.trim="config.desc" size="mini" :disabled="isReadonly" maxlength="50"> </el-input>
    </el-form-item>
    <el-form-item prop="dataAssetApi.apiName">
      <template slot="label">
        <tips-icon :content="$t('validator.nameWithChineseValidateDesc')"></tips-icon>
        API名称
      </template>
      <el-input v-model="dataAssetApi.apiName" size="mini" :disabled="disabled" maxlength="50"></el-input>
    </el-form-item>

    <el-form-item v-if="false" label="可见范围" prop="publicKind">
      <el-select v-model="config.publicKind" placeholder="可见范围" :disabled="disabled">
        <el-option v-for="item in ApiPublicKind._list" :key="item.value" :label="item.label" :value="item.value">
        </el-option>
      </el-select>
    </el-form-item>

    <el-form-item label="API接口分类" prop="dataAssetApi.apiGroupId">
      <api-tree-select
        :refresh="refresh"
        :value.sync="dataAssetApi.apiGroupId"
        :disabled="disabled"
        @change="changeApiGroup"
      />
    </el-form-item>
    <el-form-item label="API Path" prop="dataAssetApi.dataAssetApiMethod">
      <template slot="label">
        <tips-icon :content="$t('validator.nameWithoutChineseValidateDesc')"></tips-icon>
        API Path
      </template>
      <el-input v-model="dataAssetApiMethod" size="mini" :disabled="disabled">
        <template slot="prepend">
          <span :title="'/' + (path ? path + '' : '')">
            {{ '/' + (path ? path + '' : '') }}
          </span>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item label="请求协议" prop="dataAssetApi.protocol">
      <el-select v-model="dataAssetApi.protocol" placeholder="请求协议" :disabled="disabled">
        <el-option v-for="item in ProtocolType._list" :key="item.value" :label="item.label" :value="item.value">
        </el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="请求方式" prop="dataAssetApi.reqMethod">
      <el-select v-model="dataAssetApi.reqMethod" placeholder="请求方式" :disabled="disabled">
        <el-option v-for="item in RequestMethod._list" :key="item.value" :label="item.label" :value="item.value">
        </el-option>
      </el-select>
    </el-form-item>
    <el-form-item v-if="false" label="返回格式" prop="dataAssetApi.responseContentType">
      <el-select v-model="dataAssetApi.responseContentType" :disabled="true" placeholder="返回格式">
        <el-option v-for="item in ResponseContentType._list" :key="item.value" :label="item.label" :value="item.value">
        </el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="描述" prop="dataAssetApi.apiDescription">
      <el-input
        v-model="dataAssetApi.apiDescription"
        size="mini"
        type="textarea"
        maxlength="256"
        :rows="2"
        :disabled="disabled"
      ></el-input>
    </el-form-item>
  </el-form>
</template>

<script>
  import TipsIcon from '@/components/tips-icon';
  import {
    ProtocolType,
    RequestMethod,
    ResponseContentType,
    SecretType,
    UpdateFrequency,
    ApiPublicKind,
  } from '@/utils/enum/index.js';
  import ApiTreeSelect from '@/bz-components/api-tree-select';
  import Validator from '@/utils/validator.js';

  export default {
    components: { ApiTreeSelect, TipsIcon },
    props: {
      config: {
        type: Object,
        default: null,
      },
      refresh: {
        type: Boolean,
        default: false,
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
          desc: [{ required: true, message: '步骤描述不能为空', trigger: 'blur' }],
          // 'dataAssetApi.apiName': [{ required: true, validator: this.nameValidator, trigger: 'blur' }],
          'dataAssetApi.dataAssetApiMethod': [
            {
              required: true,
              validator: this.dataMethodValidator,
              trigger: 'change',
            },
          ],
          publicKind: [
            {
              required: true,
              message: '请选择可见范围',
              trigger: 'change',
            },
          ],
          'dataAssetApi.updateFrequency': [
            {
              required: true,
              message: '请选择更新频率',
              // trigger: 'change'
            },
          ],
          'dataAssetApi.protocol': [
            {
              required: true,
              message: '请输入请求协议',
              // trigger: 'change'
            },
          ],
          'dataAssetApi.reqMethod': [
            {
              required: true,
              message: '请选择请求方式',
              trigger: 'change',
            },
          ],
          'dataAssetApi.responseContentType': [
            {
              required: true,
              message: '请选择返回格式',
              trigger: 'change',
            },
          ],
          'dataAssetApi.secret': [
            {
              required: true,
              message: '请选择保密方式',
              trigger: 'change',
            },
          ],
          'dataAssetApi.apiGroupId': [
            {
              required: true,
              message: '请选择接口分类',
              trigger: 'change',
            },
          ],
          'dataAssetApi.apiType': [
            {
              required: true,
              message: '请选择API类型',
              // trigger: 'change'
            },
          ],
        },
        ProtocolType,
        RequestMethod,
        ResponseContentType,
        SecretType,
        UpdateFrequency,
        ApiPublicKind,
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
      dataAssetApiMethod: {
        get() {
          const reg = new RegExp(`^${this.path}`, 'g');
          const dataAssetApiMethod = this.dataAssetApi.dataAssetApiMethod;
          if (!dataAssetApiMethod) {
            return null;
          }
          return dataAssetApiMethod.replace(reg, '');
        },
        set(val) {
          this.dataAssetApi.dataAssetApiMethod = this.path + val;
        },
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
      dataMethodValidator(rule, value, callback) {
        // const Rule = /^[_\-0-9a-zA-Z]+$/;
        const regRule = new RegExp(`^${this.path}[_\\-0-9a-zA-Z.\\/]+$`, 'g');
        const propLabel = 'API路径';
        if (!value || value === this.path) {
          return callback(new Error(`${propLabel}不能为空`));
        } else if (value.length > 200) {
          callback(new Error(`${propLabel}过长,不能超过200个字符`));
        } else if (!regRule.test(value)) {
          return callback(new Error(`${propLabel}格式不正确`));
        }

        return callback();
      },

      changeApiGroup(group, isInit = true) {
        const path = group && group.path ? group.path : '';
        if (!isInit) {
          if (this.dataAssetApiMethod) {
            // eslint-disable-next-line vue/no-mutating-props
            this.config.dataAssetApi.dataAssetApiMethod = path + this.dataAssetApiMethod;
          } else {
            // eslint-disable-next-line vue/no-mutating-props
            this.config.dataAssetApi.dataAssetApiMethod = path;
          }
        }
        this.path = path;
        this.clearValidate(['dataAssetApi.dataAssetApiMethod']);
      },
      getEnName() {
        return this.dataAssetApiMethod;
      },
    },
  };
</script>

<style lang="less" scoped>
  /deep/ .el-input,
  /deep/ .el-input-number,
  /deep/ .el-select {
    width: 250px;
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
