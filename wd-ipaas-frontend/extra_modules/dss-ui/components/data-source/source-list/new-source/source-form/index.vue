<template>
  <el-form :model="params" :rules="rules" ref="form" label-width="150px" class="bd-form" size="mini">
    <el-form-item label="数据源名称" prop="dataSourceName">
      <el-input v-model.trim="params.dataSourceName" placeholder="数据源名称" maxlength="50"></el-input>
    </el-form-item>
    <el-form-item label="业务系统" prop="businessSystem">
      <el-input v-model.trim="params.businessSystem" placeholder="业务系统" maxlength="128"></el-input>
    </el-form-item>
    <el-form-item label="描述" prop="remark">
      <el-input
        v-model.trim="params.remark"
        type="textarea"
        :autosize="{ minRows: 2 }"
        :rows="2"
        placeholder="描述"
        maxlength="250"
        style="font-size: 13px; color: #606266"
      ></el-input>
    </el-form-item>
    <!-- 生成表单属性 -->
    <template v-for="(value) in fields">
      <!-- 此处的prop 只为触发校验方法，具体校验以校验函数为准 -->
      <!-- {{ value }} -->
      <el-form-item
        :rules="[
          {
            required: value.notNull,
            validator: validatorMap[value.paramKey]
              ? validatorMap[value.paramKey]
              : (rule, v, callback) => {
                  return fieldValidator(value, callback, value.prop);
                },
            trigger: 'change',
          },
        ]"
        :prop="value.prop"
        :label="value.paramName"
        :key="value.prop"
      >
        <template v-if="value.prop === 'redis.type'">
          <el-radio-group v-model="params.redisType">
            <el-radio-button label="client">{{ redisType.client }}</el-radio-button>
            <el-radio-button label="cluster">{{ redisType.cluster }}</el-radio-button>
          </el-radio-group>
        </template>
        <template v-else-if="value.controlType === 'NUMBER' || value.prop === 'port'">
          <el-input-number
            v-model="params[value.prop]"
            :max="value.maxValue || undefined"
            :precision="0"
            :placeholder="value.paramTip"
            :min="value.minValue || undefined"
          >
          </el-input-number>
        </template>
        <api-table
          v-else-if="value.controlType === 'TABLE'"
          :params="params"
          :refresh="refresh"
          :item="value"
          @clear-validate="clearValidate([value.prop])"
        />
        <div v-else-if="value.inputType === 'TEXTAREA'" class="textarea-container">
          <el-input
            v-if="value.prepend"
            :value="params[value.prependKey]"
            :maxlength="value.maxValue"
            :minlength="value.minValue"
            :disabled="true"
            class="textarea-prepend"
            type="text"
          />
          <el-input
            v-model="params[value.prop]"
            :maxlength="value.maxValue"
            :type="value.inputType"
            :placeholder="getPlaceholder(value)"
            :minlength="value.minValue"
            :disabled="value.readonly"
            :class="{'textarea-prepend-val': value.prepend}"
          >
          <template slot="prepend">{{ value.prepend }}</template>
          </el-input>
        </div>
        <template v-else-if="value.prop === 'db_$params_$additional'">
          <el-input
            v-model="params[value.prop]"
            type="textarea"
            :autosize="{ minRows: 2 }"
            :rows="2"
            :placeholder="getPlaceholder(value)"
            maxlength="512"
            style="font-size: 13px; color: #606266"
          >
          </el-input>
        </template>
        <template v-else>
          <el-input
            v-model="params[value.prop]"
            :maxlength="value.maxValue"
            :type="getControlType(value.controlType)"
            :placeholder="getPlaceholder(value)"
            :minlength="value.minValue"
            @focus="judgePasswordAndClear(value.controlType, value.prop)"
            v-bind="getOtherAttrs(value.candidateValues)"
          >
          </el-input>
        </template>
      </el-form-item>
    </template>
    <!-- mysqlssh类型 -->
    <template v-if="showSSH">
      <el-form-item label>
        <el-checkbox v-model="params.ssh" disabled>使用SSH通道</el-checkbox>
      </el-form-item>
      <template v-if="params.ssh">
        <el-form-item label="SSH主机" prop="ssh_$host">
          <el-input v-model="params.ssh_$host"></el-input>
        </el-form-item>
        <el-form-item label="SSH端口" prop="ssh_$port">
          <el-input-number v-model="params.ssh_$port" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="SSH用户名" prop="ssh_$user">
          <el-input v-model="params.ssh_$user"></el-input>
        </el-form-item>
        <el-form-item label="本地端口" prop="ssh_$local_$port">
          <el-input-number v-model="params.ssh_$local_$port" :min="0" :precision="0"></el-input-number>
        </el-form-item>
        <el-form-item label="验证方法">
          <el-select v-model="params.ssh_$type">
            <el-option v-for="type in sshTypeMap" :key="type.id" :label="type.label" :value="type.id"></el-option>
          </el-select>
        </el-form-item>
        <template v-if="params.ssh_$type === 'key'">
          <el-form-item label="私钥文件" prop="ssh_$private_$key_$path" key="ssh_$private_$key_$path">
            <el-upload
              class="upload-demo"
              action="/dw/datasource/data/ssh/key/upload"
              accept=".pem, .key, .crt, .cer, .csr"
              name="multipartFile"
              :show-file-list="true"
              :on-change="handleChange"
              :file-list="fileList"
              :on-success="keyPathUploadSuccess"
              :on-error="keyPathUploadError"
              :on-remove="keyPathUploadRemove"
            >
              <el-button size="small" type="primary">点击上传</el-button>
              <el-popover
                placement="top-start"
                title=""
                width="200"
                trigger="hover"
                content="支持私钥文件格式为：pem, key, crt, cer, csr。"
              >
                <i class="el-icon-question" slot="reference"></i>
              </el-popover>
            </el-upload>
            <el-input v-model="params.ssh_$private_$key_$path" disabled style="margin-top: 10px"></el-input>
          </el-form-item>
          <el-form-item label="密码短语" key="ssh_$passphrase">
            <el-input
              type="password"
              v-model="params.ssh_$passphrase"
              @focus="judgePasswordAndClear('PASSWORD', 'ssh_$passphrase')"
            ></el-input>
          </el-form-item>
        </template>
        <el-form-item label="密码" v-show="params.ssh_$type === 'password'" prop="ssh_$pwd" key="ssh_$pwd">
          <el-input
            type="password"
            v-model="params.ssh_$pwd"
            @focus="judgePasswordAndClear('PASSWORD', 'ssh_$pwd')"
          ></el-input>
        </el-form-item>
      </template>
    </template>
  </el-form>
</template>

<script>
import CONSTANTS from '../../../constants.js';
import ApiTable from './api-table';
const sshTypeMap = [
  { id: 'key', label: '公钥' },
  { id: 'password', label: '密码' },
];
export default {
  name: 'SourceForm',
  components: { ApiTable },
  inject: ['enableEncrypt', 'getEditedPasswordArr'],
  computed: {
    showSSH() {
      return this.params && this.params.dataSourceType === 'mysqlssh';
    },
    editedPasswordArr() {
      return this.getEditedPasswordArr();
    },
  },
  props: {
    params: {
      type: Object,
      default: () => {
        return {};
      },
    },
    fields: { type: Array },
    refresh: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      rules: {
        dataSourceName: [
          { required: true, message: '请输入数据源名称', trigger: 'blur' },
          { min: 3, max: 50, message: '长度在 3 到 50 个字符', trigger: 'blur' },
        ],
        // mysqlssh
        ssh_$host: [{ required: true, message: '请输入SSH主机地址', trigger: 'blur' }],
        ssh_$port: [{ required: true, message: '请输入SSH端口', trigger: 'blur' }],
        ssh_$user: [{ required: true, message: '请输入SSH用户名', trigger: 'blur' }],
        ssh_$local_$port: [{ required: true, message: '请输入本地端口', trigger: 'blur' }],
        ssh_$private_$key_$path: [{ required: true, message: '请上传私钥文件', trigger: 'blur' }],
        ssh_$pwd: [{ required: true, message: '请输入密码', trigger: 'blur' }],
      },
      redisType: CONSTANTS.redisType,
      sshTypeMap,
      validatorMap: CONSTANTS.validatorMap,
      fileList: [],
    };
  },
  methods: {
    transRulesMsg(item = {}) {
      let msg = '';
      if (item.paramName) {
        msg += `请输入${item.paramName}`;
      } else {
        msg = '当前字段不允许为空';
      }
      return msg;
    },
    mappingFieldsKey(key = '') {
      let prop = key;
      for (const k in CONSTANTS.paramsMap) {
        if (key === CONSTANTS.paramsMap[k]) {
          return (prop = k);
        }
      }
      return prop;
    },
    getControlType(type) {
      // return CONSTANTS.passwordKey.indexOf(field) === -1 ? 'text' : 'password';
      return type !== 'PASSWORD' ? 'text' : 'password';
    },
    getPlaceholder(field = {}) {
      return (
        (field.paramKey === 'redis.ip.port' && "请以127.0.0.1:6379 的格式输入，多个主机之间用 ' , ' 隔开") ||
        field.paramTip
      );
    },
    clearValidate(arr) {
      this.$refs.form.clearValidate(arr);
    },
    validate() {
      let val = false;
      this.$refs.form.validate(valid => {
        if (valid) {
          val = true;
        } else {
          this.$message({
            message: '有必填项未填写或填写不符合要求，请检查',
            type: 'error',
            customClass: 'data-source-message',
          });
          this.setMessageZindex();
          return false;
        }
      });
      return val;
    },
    fieldValidator(field, callback, key) {
      if (field && field.notNull && !this.params[key]) {
        return callback(new Error(this.transRulesMsg(field)));
      }
      return callback();
    },
    // 解决message弹窗在el-drawer图层下方的问题
    setMessageZindex() {
      this.$nextTick(() => {
        document.querySelector('.data-source-message').style.zIndex = 2100;
      });
    },
    keyPathUploadSuccess(response, file, fileList) {
      if (response.errorCode === 200 && response.data) this.params.ssh_$private_$key_$path = response.data;
      else this.$message.error(`${response.errorMessage || response.errorCode}`);
    },
    keyPathUploadError(err, file, fileList) {
      this.$message.error(`请重试，错误：${err.errorMessage || err.errorCode}`);
    },
    keyPathUploadRemove(file, fileList) {
      console.log(file, fileList);
      if (file.response.errorCode === 200)
        (this.params.ssh_$private_$key_$path = ''), (this.fileList = fileList.slice(-1));
      else this.$message.error(`${file.response.errorMessage || file.response.errorCode}`);
    },
    handleChange(file, fileList) {
      this.fileList = fileList.slice(-1);
    },
    judgePasswordAndClear(propType, prop) {
      if (
        this.enableEncrypt &&
        this.getControlType(propType) === 'password' &&
        (!this.editedPasswordArr || this.editedPasswordArr.indexOf(prop) === -1)
      ) {
        this.$set(this.params, prop, '');
        this.clearValidate([prop]);
        this.$emit('password-edited', prop);
      }
    },
    // text输入框类型使用candidateValues作为控件的其它attrs传入
    getOtherAttrs(candivateValus) {
      try {
        return candivateValus ? JSON.parse(candivateValus): {};
      } catch {
        return {};
      }
    }
  },
};
</script>

<style lang="less" scoped>
@import '../../../style.less';
.el-icon-question {
  margin-left: 5px;
  color: #808080;
}
.textarea-container{
  max-width: 800px;
}
.textarea-prepend{
  /deep/ .el-input__inner{
    border-bottom: 0px;
    border-bottom-left-radius: 0px;
    border-bottom-right-radius: 0;
  }
}
.textarea-prepend-val{
  /deep/ .el-textarea__inner{
    border-top: 0px;
    border-top-left-radius: 0px;
    border-top-right-radius: 0;
  }
}
</style>