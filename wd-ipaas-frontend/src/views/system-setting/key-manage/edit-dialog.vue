<template>
  <el-dialog
    class="bd-dialog edit-dialog medium"
    top="60px"
    fullscreen
    custom-class="anim-left"
    :visible.sync="dialogVisible"
    append-to-body
    :close-on-click-modal="false"
    :title="title"
    @close="handleClose"
  >
    <el-scrollbar class="edit-dialog__scrollbar">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        :disabled="disabled"
        label-width="150px"
        :validate-on-rule-change="false"
      >
        <el-form-item label="所属连接器" prop="connectorId">
          <el-select v-model="form.connectorId" placeholder="请选择" @change="handleConnect">
            <el-option v-for="item in connectNameList" :key="item.id" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="环境" prop="environmentId">
          <el-select v-model="form.environmentId" placeholder="请选择" :loading="environmentListLoading">
            <el-option
              v-for="item in environmentList"
              :key="item.id"
              :label="item.addressName"
              :value="item.id"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="密钥名称" prop="name">
          <el-input v-model="form.name" placeholder="不超过200字符" maxlength="200" show-word-limit></el-input>
        </el-form-item>

        <el-form-item label="是否启用" prop="isEnable">
          <el-switch
            v-model="form.isEnable"
            :active-value="TASK_STATUS.ON"
            :inactive-value="TASK_STATUS.OFF"
            active-color="#13ce66"
          ></el-switch>
        </el-form-item>

        <div v-if="form.params" v-loading="itemLoading">
          <el-form-item
            v-for="(item, index) in form.params"
            :key="item.key"
            class="form-item-block"
            :label="item.label"
            :prop="'params.' + index + '.defaultValue'"
            :rules="[
              { required: item.required, message: '请填写', trigger: 'blur' },
              { max: 200, message: '长度不能超过200字符', trigger: 'blur' },
            ]"
          >
            <el-input
              v-model="item.defaultValue"
              maxlength="200"
              auto-complete="off"
              :type="item && item.isText ? 'text' : 'password'"
              :placeholder="item.description"
            >
              <i
                slot="suffix"
                :class="item.isText ? 'fa fa-eye' : 'fa fa-eye-slash '"
                @click="showPass(item, index)"
              ></i>
            </el-input>
          </el-form-item>
        </div>

        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" placeholder="不超过200字符" maxlength="200" show-word-limit></el-input>
        </el-form-item>
      </el-form>
    </el-scrollbar>
    <div class="bd-dialog-footer">
      <el-button class="dss-btn-circle" @click="handleClose(false)">取消</el-button>
      <el-button v-if="!disabled" class="dss-btn-circle" type="primary" @click="save">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import api from '@/api/api-key-manage';

  import { TASK_STATUS } from './common';

  export default {
    name: 'EditKeyManageDialog',

    props: {
      disabled: {
        type: Boolean,
        default: false,
      },
      connectNameList: {
        type: Array,
        default: () => [],
      },
      visible: {
        type: Boolean,
        default: false,
      },
      data: {
        type: Object,
        default: () => null,
      },
    },

    data() {
      return {
        TASK_STATUS,
        rules: {
          connectorId: [{ required: true, trigger: 'blur', message: '请选择' }],
          environmentId: [{ required: true, trigger: 'blur', message: '请选择' }],
          name: [
            { required: true, trigger: 'blur', message: '请填写密钥名称' },
            { max: 200, message: '长度不能超过200字符', trigger: 'blur' },
          ],
        },
        form: {
          connectName: '', // 连接器名称
          connectorId: '',
          environmentId: '', // 环境地址id
          environmentName: '', // 环境地址
          isEnable: 1,
          name: '', // 密钥名称
          description: '',
          createBy: '', // todo
          createTime: '',
          secretKey: '', // 密钥唯一标识
          params: [],
        },
        environmentList: [],
        environmentListLoading: false,
        itemLoading: false,
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
      /**
       * 是否编辑模式
       */
      isEdit() {
        return !!this.data;
      },
      title() {
        return this.disabled ? '查看密钥' : this.isEdit ? `编辑密钥` : '新增密钥';
      },
    },

    watch: {
      visible: {
        immediate: true,
        handler(val) {
          if (val) {
            this.initForm(this.data);
          } else {
            this.$refs.formRef.clearValidate();
          }
        },
      },
      'form.connectorId': {
        handler(val) {
          this.form.environmentId = '';
        },
      },
    },

    methods: {
      showPass(item, index) {
        this.$set(this.form.params[index], 'isText', !this.form.params[index].isText);
      },

      handleConnect(id) {
        this.fetchEnvironmentListById(id);
        this.fetchParamsById(id);
      },

      fetchEnvironmentListById(val) {
        this.environmentListLoading = true;
        this.environmentList = [];
        const params = {
          connectorId: val,
        };
        api
          .fetchEnvironmentListById(params)
          .then(res => {
            this.environmentList = res.data;
          })
          .always(() => {
            this.environmentListLoading = false;
          });
      },

      fetchParamsById(val) {
        this.itemLoading = true;
        const params = {
          connectorId: val,
        };
        api
          .fetchParamsById(params)
          .then(res => {
            if (!res.data) {
              this.form.params = [];
              return;
            }
            this.form.params = this.initDynamicValidateForm(res.data);
          })
          .always(() => {
            this.itemLoading = false;
          });
      },

      findParams(key) {
        const params = JSON.parse(this.form.paramsJson);
        return key ? params[key] : '';
      },

      initDynamicValidateForm(arr = []) {
        const isEdit = this.isEdit;
        const res = [];
        arr.forEach(item => {
          res.push({
            ...item,
            key: Math.floor(Math.random() * 10000),
            label: item.paramName,
            required: Boolean(item.isRequired),
            description: item.description,
            defaultValue: isEdit ? this.findParams(item.paramName) : '',
          });
        });
        return res;
      },

      reset() {
        this.form = {
          connectName: '', // 连接器名称
          connectorId: '',
          environmentId: '', // 环境地址id
          environmentName: '', // 环境地址
          isEnable: 1,
          name: '', // 密钥名称
          secretKey: '', // 密钥唯一标识
          description: '',
          params: [],
        };
        this.$refs.formRef.clearValidate();
      },
      handleClose(auto) {
        this.reset();
        this.$emit('cancel', auto);
      },

      save() {
        this.$refs.formRef.validate(valid => {
          if (valid) {
            this.onSave();
          } else {
            this.$message.error('表单填写错误');
            return false;
          }
        });
      },

      joinParamJson() {
        const data = this.form.params;
        const res = {};
        data.forEach(item => {
          res[item.label] = item.defaultValue;
        });
        return JSON.stringify(res);
      },

      onSave() {
        const path = this.isEdit ? 'updateSecretPage' : 'fetchSecretAdd';
        const params = {
          ...this.form,
          paramsJson: this.joinParamJson(),
        };
        const loading = this.$loading({});
        api[path](params)
          .then(res => {
            this.$message.success('保存成功');
            this.handleClose(true);
          })
          .always(() => {
            loading.close();
          });
      },

      initForm(item) {
        // console.log('initForm', item);
        if (!item) {
          return;
        }
        this.form = {
          id: item.id,
          connectName: item.connectName, // 连接器名称
          connectorId: item.connectorId,
          environmentId: item.environmentId, // 环境地址id
          environmentName: item.environmentName, // 环境地址
          isEnable: item.isEnable,
          name: item.name, // 密钥名称
          description: item.description,
          secretKey: item.secretKey, // 密钥唯一标识
          paramsJson: item.paramsJson,
          params: [],
        };
        this.environmentList = [];
        this.handleConnect(item.connectorId);
      },
    },
  };
</script>

<style lang="less" scoped>
  .edit-dialog {
    &__scrollbar {
      box-sizing: border-box;
      overflow-x: hidden;
      overflow-y: hidden;
      margin-bottom: 10px;
      ::v-deep {
        .el-scrollbar__wrap {
          overflow-x: hidden;
          max-height: calc(100vh - 226px);
          .el-scrollbar__view {
            padding-bottom: 17px;
          }
        }
      }
    }

    &__title {
      height: 22px;
      font-size: 16px;
      margin: 10px 0;
      font-weight: 600;
      color: rgba(51, 51, 51, 1);
      line-height: 22px;
    }

    &__input {
      width: 400px;
    }

    &__select {
      width: 500px;
      margin-left: 10px;
    }

    &__picker {
      margin-left: 10px;
    }

    &__textarea {
      width: 500px;
    }

    ::v-deep {
      .el-dialog__footer {
        position: absolute;
        left: 0;
        bottom: 0;
        width: 100%;
        height: 70px;
        background: orange;
      }
      .el-form-item {
        width: 50%;
        display: inline-block;
        .el-select {
          width: 80%;
        }
      }
      .form-item-block {
        display: block;
      }
    }
  }
</style>
