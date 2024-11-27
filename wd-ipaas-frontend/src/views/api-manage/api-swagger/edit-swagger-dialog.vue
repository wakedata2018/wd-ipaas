<template>
  <el-dialog
    v-loading="loading.saving"
    class="bd-dialog"
    width="600px"
    :visible.sync="isShow"
    :close-on-click-modal="false"
    :title="title"
  >
    <el-form ref="elForm" label-width="120px" style="padding-bottom: 20px" :model="form" :rules="rules">
      <el-form-item label="Swagger 名称" prop="swaggerName">
        <el-input v-model="form.swaggerName" placeholder="请输入Swagger名称" maxlength="20"></el-input>
      </el-form-item>
      <el-form-item label="接口分类" prop="apiGroupId">
        <el-select v-model="form.apiGroupId" :disabled="!!appInfo" filterable placeholder="请选择">
          <el-option v-for="item in apiGroupList" :key="item.id" :label="item.groupName" :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="导入方式" prop="importType">
        <el-radio-group v-model="form.importType">
          <el-radio v-for="item in IMPORT_TYPE_OPTIONS" :key="item.value" :label="item.value">{{
            item.label
          }}</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item v-if="isImportUrlType" label="Swagger URL" prop="swaggerUrl">
        <el-input
          v-model="form.swaggerUrl"
          placeholder="请输入Swagger URl"
          type="textarea"
          show-word-limit
          row="5"
          maxlength="1024"
        ></el-input>
      </el-form-item>
      <el-form-item v-else label="导入文件" prop="tempFilePath">
        <el-upload
          ref="upload"
          action="/dw/open/upload/temp.file"
          name="uploadFile"
          accept="json"
          :limit="1"
          :before-remove="onBeforeRemove"
          :on-success="onUploadSuccess"
        >
          <el-button type="text">选择文件</el-button>
        </el-upload>
      </el-form-item>
      <el-form-item label="接口外部域名" prop="apiDomainName">
        <el-input v-model="form.apiDomainName" placeholder="例:https://xxx.xxxx.xxx" maxlength="100"></el-input>
      </el-form-item>
      <el-form-item label="参数映射规则" prop="respMappingRule">
        <el-select v-model="form.respMappingRule" placeholder="默认" clearable>
          <el-option v-for="item in ParamsMappingOptions" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="Swagger 描述" prop="description">
        <el-input v-model="form.description" placeholder="Swagger描述" maxlength="80" show-word-limit type="textarea">
        </el-input>
      </el-form-item>
    </el-form>
    <div slot="footer" class="bd-dialog-footer">
      <el-button size="medium" @click="onCancle">取消</el-button>
      <el-button size="medium" type="primary" class="bd-button" @click="onSave">{{ handleBtn }}</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import swaggerManage from '@/api/swagger-manage.js';
  import apiGroup from '@/api/api-group';
  import ParamsMappingApi from '@/api/params-mapping';

  import { IMPORT_FORM_TYPE, IMPORT_TYPE_OPTIONS } from '@/utils/enum/api-swagger';

  export default {
    props: {
      appInfo: {
        type: Object,
        default() {
          return null;
        },
      },
      visible: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      const pathTest = (rule, value, callback) => {
        const rules = /^(http|https):\/\/([\w.]+\/?)\S*/;
        if (value === '') {
          callback(new Error('路径不能为空'));
        } else if (value.length > 1024) {
          callback(new Error('路径过长,不能超过1024个字符'));
        } else if (!rules.test(value)) {
          callback(new Error('请输入正确格式的URL'));
        } else {
          callback();
        }
      };
      const swaggerName = (rule, value, callback) => {
        if (value === '') {
          callback(new Error('swagger名不能为空'));
        } else if (value.length < 2) {
          callback(new Error('swagger名称不能小于两位'));
        } else if (value.length > 20) {
          callback(new Error('swagger名称不能大于20位'));
        } else {
          callback();
        }
      };
      const apiDomainName = (rule, value, callback) => {
        const rules = /^(http|https):\/\/([\w.]+\/?)\S*/;
        // 校验IP地址
        const ip =
          /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
        // 校验域名
        const url = /^(([-\u4E00-\u9FA5a-z0-9]{1,63})\.)+([\u4E00-\u9FA5a-z]{2,63})\.?$/;
        // 校验端口号
        const port = /^(([0-9]|[1-9]\d{1,3}|[1-5]\d{4}|6[0-5]{2}[0-3][0-5]))$/;

        const valueSplit = (value || '').split(':');
        const second = valueSplit.length >= 2 ? valueSplit[1] : '';
        const domain = (second || '').substring(2);

        if (!value) {
          callback(new Error('域名不能为空'));
        } else if (value.length > 100) {
          callback(new Error('域名过长,不能超过100个字符'));
        } else if (!rules.test(value) || (valueSplit.length !== 2 && valueSplit.length !== 3)) {
          callback(new Error('正确的IP地址或域名及端口格式!例如：http://www.baidu.com:80'));
        } else if (!(ip.test(domain) || url.test(domain))) {
          if (domain.split('/').length === 1) {
            callback(new Error('请输入正确的IP地址例如：127.0.0.1或者域名例如：www.baidu.com'));
          } else {
            callback(new Error('请去掉域名后面的路径'));
          }
        } else if (valueSplit.length === 3 && !port.test(valueSplit[2])) {
          const third = valueSplit[2];
          if (third.split('/').length === 1) {
            callback(new Error('请输入正确的端口号例如：80'));
          } else {
            callback(new Error('请去掉端口后面的路径'));
          }
        } else {
          callback();
        }
      };
      return {
        loading: {
          saving: false,
        },
        apiGroupList: [],
        form: {
          swaggerName: '',
          importType: IMPORT_FORM_TYPE.URL,
          tempFilePath: '',
          swaggerUrl: '',
          apiGroupId: '',
          description: '',
          apiDomainName: '',
          respMappingRule: null,
        },
        rules: {
          swaggerName: [{ required: true, validator: swaggerName, trigger: 'blur' }],
          importType: [{ required: true, trigger: 'blur' }],
          tempFilePath: [{ required: true, trigger: 'change', message: '导入文件不能为空' }],
          swaggerUrl: [{ required: true, validator: pathTest, trigger: 'blur' }],
          apiDomainName: [{ required: true, validator: apiDomainName, trigger: 'blur' }],
          apiGroupId: [{ required: true, message: '请选择接口分类', trigger: 'change' }],
        },
        IMPORT_TYPE_OPTIONS,
        ParamsMappingOptions: [], // 参数映射规则
        IMPORT_FORM_TYPE,
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
      isEdit() {
        return !!this.appInfo;
      },
      title() {
        return this.isEdit ? '编辑Swagger' : '新增Swagger';
      },
      handleBtn() {
        return this.isEdit ? '保存' : '开始处理';
      },
      // URL导入方式
      isImportUrlType() {
        return this.form.importType === this.IMPORT_FORM_TYPE.URL;
      },
    },
    watch: {
      isShow: {
        immediate: true,
        async handler(val) {
          this.resetFields();
          if (val) {
            const p1 = await this.queryApiGroup();
            const p2 = await this.getParamsMappingOptions();
            Promise.all([p1, p2]);
            this.setValues();
          }
        },
      },
      'form.swaggerUrl': {
        handler(val) {
          if ((val || '').length > 1024) {
            this.form.swaggerUrl = val.substring(0, 1024);
          }
        },
      },
      'form.swaggerName': {
        handler(val) {
          if ((val || '').length > 20) {
            this.form.swaggerName = val.substring(0, 20);
          }
        },
      },
      'form.description': {
        handler(val) {
          if ((val || '').length > 80) {
            this.form.description = val.substring(0, 80);
          }
        },
      },
      'form.apiDomainName': {
        handler(val) {
          if ((val || '').length > 100) {
            this.form.apiDomainName = val.substring(0, 100);
          }
        },
      },
      'form.importType': {
        handler(val) {
          this.form.swaggerUrl = '';
          this.form.tempFilePath = '';
          this.$refs.elForm?.clearValidate();
          this.$refs.upload?.clearFiles();
        },
      },
    },
    mounted() {
      window.scrollTo(0, 0);
    },
    methods: {
      resetFields() {
        const elForm = this.$refs.elForm;
        if (elForm && elForm.resetFields) {
          elForm.resetFields();
        }
        this.form.swaggerName = '';
        this.form.swaggerUrl = '';
        this.form.description = '';
        this.form.apiDomainName = '';
        this.form.apiGroupId = '';
      },
      queryApiGroup() {
        apiGroup.getThemeList().then(({ success, data }) => {
          if (success && !!data) {
            this.apiGroupList = data;
          }
        });
      },
      setValues() {
        if (!this.appInfo) {
          return;
        }
        for (const key in this.form) {
          let value = this.appInfo[key];
          // 若参数映射规则字段为0则将value置空
          if (key === 'respMappingRule' && !this.appInfo[key]) {
            value = null;
          }
          this.$set(this.form, key, value);
        }
      },
      onCancle() {
        this.isShow = false;
      },
      // 编辑保存
      onSave() {
        const elForm = this.$refs.elForm;
        elForm.validate(valid => {
          if (!valid) {
            return;
          }

          if (!!this.appInfo && !!this.appInfo.id) {
            this.update();
          } else {
            this.$confirm('后台开始解析Swagger数据，需要您进入‘确认导入’页面，确认最后需要导入到系统的数据！', '提示', {
              confirmButtonText: '现在进入',
              cancelButtonText: '取消',
              type: 'warning',
            })
              .then(res => {
                this.add();
              })
              .catch(() => {
                this.$message({
                  type: 'info',
                  message: '已取消',
                });
              });
          }
        });
      },
      add() {
        swaggerManage
          .addSwagger(
            {
              ...this.toParams(this.form),
            },
            !this.isImportUrlType
          )
          .done(res => {
            this.$message.success('保存成功');
            this.$emit('saved', 'create', res.data);
            this.isShow = false;
          })
          .always(() => {
            this.loading.saving = false;
          });
      },
      update() {
        this.loading.saving = true;
        const parmas = {
          id: this.appInfo.id,
          ...this.toParams(this.form),
        };
        swaggerManage
          .updateSwagger(parmas, !this.isImportUrlType)
          .done(() => {
            this.$emit('saved', 'edit', this.form, this.appInfo);
            this.$message.success('保存成功');
            this.isShow = false;
          })
          .always(() => {
            this.loading.saving = false;
          });
      },
      toParams() {
        const params = this.$plain(this.form);
        if (!this.form.respMappingRule) {
          params.respMappingRule = 0;
        }
        return params;
      },
      onUploadSuccess(response) {
        this.form.tempFilePath = response.data;
        this.$refs.elForm.validateField('tempFilePath');
      },
      onBeforeRemove() {
        this.form.tempFilePath = null;
      },
      // 获取参数映射规则
      async getParamsMappingOptions() {
        try {
          const res = await ParamsMappingApi.getParamsMappingList();
          this.ParamsMappingOptions = res.map(item => {
            return {
              label: item.respParamMappingRuleName,
              value: item.id,
            };
          });
        } catch (err) {
          this.$message({
            type: 'error',
            message: err.message,
          });
        }
      },
    },
  };
</script>
<style lang="less" scoped>
  .bd-dialog {
    /deep/ .el-textarea__inner {
      height: 100px;
    }

    /deep/ .el-select {
      width: 100%;
    }

    /deep/ .el-select-dropdown__wrap {
      height: 200px;
    }
  }

  .tree-option {
    padding: 0px !important;
  }

  /deep/.el-dialog__body {
    padding: 20px 60px;
  }

  .tips {
    font-size: 12px;
    color: #dcdfe6;
    line-height: 20px;
  }
</style>
