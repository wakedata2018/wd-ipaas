<!-- eslint-disable vue/no-mutating-props -->
<template>
  <el-form
    ref="advancedForm"
    size="mini"
    label-position="top"
    :model="taskInfo"
    :rules="rules"
    :label-width="labelWidth"
    @submit.native.prevent
  >
    <el-form-item prop="dataAssetApi.apiName">
      <template slot="label">
        <tips-icon :content="$t('validator.nameWithChineseValidateDesc')"></tips-icon>
        API名称
      </template>
      <!-- :disabled="!!taskInfo.id || isReadonly" -->
      <el-input v-model="taskInfo.dataAssetApi.apiName" size="mini" :disabled="isReadonly" maxlength="50"></el-input>
    </el-form-item>
    <el-form-item label="API接口分类" prop="dataAssetApi.apiGroupId">
      <api-tree-select
        :value.sync="taskInfo.dataAssetApi.apiGroupId"
        :refresh="refresh"
        :disabled="isReadonly"
        @change="changeApiGroup"
      />
    </el-form-item>
    <el-form-item label="API Path" prop="dataAssetApi.dataAssetApiMethod">
      <el-input v-model="dataAssetApiMethod" size="mini" :disabled="isReadonly">
        <template slot="prepend">
          <span :title="path ? path + '' : ''">
            {{ path ? path + '' : '' }}
          </span>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item label="请求协议" prop="dataAssetApi.protocol">
      <el-select v-model="taskInfo.dataAssetApi.protocol" placeholder="请求协议" :disabled="isReadonly">
        <el-option
          v-for="item in ProtocolType._list"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        ></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="请求方式" prop="dataAssetApi.reqMethod">
      <el-select v-model="taskInfo.dataAssetApi.reqMethod" placeholder="请求方式" :disabled="isReadonly">
        <el-option
          v-for="item in RequestMethod._list"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        ></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="是否公开" prop="dataAssetApi.secret">
      <el-select v-model="taskInfo.dataAssetApi.secret" placeholder="是否公开" :disabled="isReadonly">
        <el-option
          v-for="item in SecretType._list"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        ></el-option>
      </el-select>
    </el-form-item>
    <el-form-item v-if="false" label="返回格式" prop="dataAssetApi.responseContentType">
      <el-select v-model="taskInfo.dataAssetApi.responseContentType" :disabled="true" placeholder="返回格式">
        <el-option
          v-for="item in ResponseContentType._list"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        ></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="描述" prop="dataAssetApi.apiDescription">
      <el-input
        v-model="taskInfo.dataAssetApi.apiDescription"
        size="mini"
        type="textarea"
        :rows="5"
        maxlength="256"
        :disabled="isReadonly"
      ></el-input>
    </el-form-item>
  </el-form>
</template>

<!-- eslint-disable vue/no-mutating-props -->
<script>
  import { getInitForm } from '../global/task-conf';
  import TipsIcon from '@/components/tips-icon';
  import ApiTreeSelect from '@/bz-components/api-tree-select';
  import { ProtocolType, RequestMethod, ResponseContentType, SecretType, UpdateFrequency } from '@/utils/enum';
  import { ApiType } from '@/utils/enum/index';
  import Validator from '@/utils/validator.js';

  export default {
    components: {
      TipsIcon,
      ApiTreeSelect,
    },
    props: {
      taskInfo: {
        type: Object,
        default: () => getInitForm(),
      },
      isReadonly: {
        type: Boolean,
        default: false,
      },
      defaultValue: {
        type: Object,
        default: () => {
          return {};
        },
      },
    },
    data() {
      return {
        rules: {},
        isMultiTables: true,
        // 打开过一次
        opened: false,
        taskQueueList: {
          loading: false,
          list: [],
        },
        ProtocolType,
        RequestMethod,
        ResponseContentType,
        SecretType,
        UpdateFrequency,
        path: '',
        refresh: false,
      };
    },
    computed: {
      labelWidth() {
        return this.$i18n.locale === 'en' ? '280px' : '280px';
      },
      isFileOrRocks() {
        const list = ['file', 'rocks'];
        return list.indexOf(this.taskInfo.taskConf.stateBackend) > -1;
      },
      dataAssetApiMethod: {
        get() {
          const reg = new RegExp(`^${this.path}`, 'g');
          const dataAssetApiMethod = this.taskInfo.dataAssetApi.dataAssetApiMethod;
          if (!dataAssetApiMethod) {
            return null;
          }
          return dataAssetApiMethod.replace(reg, '');
        },
        set(val) {
          this.taskInfo.dataAssetApi.dataAssetApiMethod = this.path + val;
        },
      },
    },
    watch: {
      taskInfo: {
        handler() {
          this.refresh = !this.refresh;
          this.clearValidate();
        },
      },
      defaultValue: {
        handler() {
          const { apiGroupId } = this.taskInfo.dataAssetApi;
          const { apiType } = this.defaultValue;
          if (
            !apiGroupId &&
            this.defaultValue?.apiGroupId &&
            apiType &&
            (apiType === ApiType.CUSTOM_SQL || apiType === ApiType.EXTERNAL_HTTP)
          ) {
            this.initDefault();
          }
        },
      },
    },
    created() {
      this.rules = {
        'dataAssetApi.apiName': [{ required: true, validator: Validator.nameValidator, trigger: 'blur' }],
        'dataAssetApi.dataAssetApiMethod': [
          {
            required: true,
            validator: this.dataMethodValidator,
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
      };
    },
    methods: {
      initDefault() {
        if (Object.keys(this.defaultValue).length) {
          const { apiGroupId, protocol, reqMethod, dataAssetApiMethod } = this.defaultValue;
          const params = {};
          if (apiGroupId) {
            params.apiGroupId = apiGroupId;
          }
          if (protocol) {
            params.protocol = protocol;
          }
          if (reqMethod) {
            params.reqMethod = reqMethod;
          }
          if (dataAssetApiMethod) {
            this.path = `${dataAssetApiMethod.split('/')[0]}/`;
          }
          if (Object.keys(params).length) {
            Object.assign(this.taskInfo.dataAssetApi, params);
          }
        }
      },
      clearValidate() {
        this.$nextTick(() => {
          this.$refs.advancedForm.clearValidate();
        });
      },
      minNumValidator(value, minNum, callback) {
        if (value != null && value >= 0) {
          callback();
        } else {
          callback(new Error(this.$t('validator.minCount')));
        }
      },
      onSave() {
        this.$refs.advancedForm.validate(isValidate => {
          if (!isValidate) {
            return;
          }
          this.save();
        });
      },
      dataMethodValidator(rule, value, callback) {
        const regRule = new RegExp(`^${this.path}[_\\-0-9a-zA-Z\\/.]+$`, 'g');
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
      validate() {
        return this.$refs.advancedForm.validate();
      },
      changeApiGroup(group, isInit = true) {
        const path = group && group.path ? group.path : '';
        if (!isInit) {
          if (this.dataAssetApiMethod) {
            this.taskInfo.dataAssetApi.dataAssetApiMethod = path + this.dataAssetApiMethod;
          } else {
            this.taskInfo.dataAssetApi.dataAssetApiMethod = path;
          }
        }
        this.path = path;
        this.$nextTick(() => {
          this.$refs.advancedForm.clearValidate(['dataAssetApi.dataAssetApiMethod']);
        });
      },
    },
  };
</script>

<style lang="less" scoped>
  @import '../../../css/var.less';

  .task-config-title {
    padding: 5px 0;
    margin: 10px 0;
    font-weight: 600;
    font-size: 12px;
    color: black;
    border-top: 1px solid #eee;
  }
  /deep/ .el-form-item__label {
    padding-bottom: 0px;
    font-size: 12px;
  }
  /deep/ .el-switch__label * {
    font-size: 12px !important;
  }
  /deep/ .el-radio__label {
    font-size: 12px !important;
  }
  /deep/ .el-select {
    width: 100%;
  }
  /deep/ .el-input-group__prepend {
    max-width: 80px !important;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
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
