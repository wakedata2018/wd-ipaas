<template>
  <el-dialog
    fullscreen
    custom-class="anim-left"
    lock-scroll
    append-to-body
    class="bd-dialog data-source-edit-dialog"
    :title="title"
    :visible.sync="show"
    :close-on-click-modal="false"
    @open="loadForm"
  >
    <el-scrollbar ref="scrollbar" v-loading="loading" class="page-scrollbar">
      <el-form :model="form" :rules="rules" ref="ruleForm" label-width="160px" class="demo-ruleForm">
        <div class="main-title">选择数据源类型</div>
        <el-form-item label=" " prop="dbType">
          <datasource-select
            :data="typeArr"
            v-model="form.dbType"
            :disabled="!!appInfo"
            style="width: 100%"
            @change="changeDatasourceType"
          ></datasource-select>
        </el-form-item>
        <!--
        PHOENIX: connectionName , dbType ,url
         v-if="form.dbType !== 'PHOENIX'"
        -->
        <el-form-item label="数据源名称" prop="connectionName">
          <el-input v-model="form.connectionName" maxlength="64"></el-input>
        </el-form-item>
        <el-form-item v-if="form.dbType === DATA_TYPE_PHOENIX" label="Url" prop="url">
          <el-input v-model="form.url" maxlength="256"></el-input>
        </el-form-item>
        <el-form-item
          v-if="form.dbType !== DATA_TYPE_PHOENIX"
          :label="form.dbType !== DATA_TYPE_HBASE ? '数据库地址' : 'zk 主机名'"
          prop="dbHost"
        >
          <el-input v-model="form.dbHost" maxlength="256"></el-input>
        </el-form-item>
        <el-form-item
          v-if="form.dbType !== DATA_TYPE_PHOENIX"
          :label="form.dbType !== DATA_TYPE_HBASE ? '端口' : 'zk 端口'"
          prop="dbPort"
        >
          <el-input-number :min="0" maxlength="64" v-model="form.dbPort" controls-position="right"></el-input-number>
        </el-form-item>
        <el-form-item v-if="form.dbType !== DATA_TYPE_PHOENIX" label="数据库名称" prop="dbName">
          <el-input v-model="form.dbName" maxlength="64"></el-input>
        </el-form-item>
        <el-form-item
          v-if="form.dbType !== DATA_TYPE_HBASE && form.dbType !== DATA_TYPE_PHOENIX"
          label="用户名"
          prop="dbUsername"
        >
          <el-input v-model="form.dbUsername" maxlength="64"></el-input>
        </el-form-item>
        <el-form-item
          v-if="form.dbType !== DATA_TYPE_PHOENIX && form.dbType !== DATA_TYPE_HBASE"
          label="密码"
          prop="dbPassword"
        >
          <el-input
            v-model="form.dbPassword"
            type="password"
            maxlength="64"
            :placeholder="!!form.id ? '编辑需重新填写密码' : ''"
          ></el-input>
        </el-form-item>
        <el-form-item
          v-if="form.dbType !== DATA_TYPE_PHOENIX && form.dbType === DATA_TYPE_HBASE"
          label="zk节点名称"
          prop="zkNode"
        >
          <el-input v-model="form.zkNode" disabled maxlength="64"></el-input>
        </el-form-item>
        <el-form-item v-if="form.dbType !== DATA_TYPE_PHOENIX" label="描述" prop="description">
          <el-input v-model="form.description" maxlength="128" type="textarea"></el-input>
        </el-form-item>
        <el-form-item>
          <div class="comment">* 通过连接测试之后才能保存</div>
        </el-form-item>
      </el-form>
    </el-scrollbar>
    <div class="bd-dialog-footer">
      <el-button @click="onCancel" size="medium" class="cancel">取消</el-button>
      <el-button type="primary" @click="submitForm('ruleForm')" size="medium" class="bd-button bd-info">测试</el-button>
      <el-button type="primary" :disabled="!save" size="medium" @click="onSaveOrEdit" class="bd-button">保存</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import datasourceSelect from '@/bz-components/datasource-select';
  import dataSource from '@/api/data-source.js';
  import dataBase from '@/api/data-base.js';
  import { Message } from 'element-ui';
  import { DATA_TYPE_PHOENIX, DATA_TYPE_HBASE } from '@/enum';

  const $ = window.jQuery;
  const initSource = {
    connectionName: null, // 连接名
    dbType: 'MYSQL', // 数据类型id
    dbName: null, // 数据源名称
    dbHost: null, // 数据库地址
    dbPort: null, // 端口
    dbUsername: null, // 用户名
    dbPassword: null, // 密码
    description: null,
    id: null,
    zkNode: null,
  };
  export default {
    props: {
      appInfo: {
        type: Number,
      },
      visible: {
        type: Boolean,
        default: false,
      },
    },
    components: { datasourceSelect },
    computed: {
      show: {
        get() {
          return this.visible;
        },
        set(val) {
          if (!val) {
            this.hide();
          }
        },
      },
    },
    watch: {
      form: {
        deep: true,
        immediate: true,
        handler() {
          this.save = false;
        },
      },
    },
    data() {
      const connection = (rule, value, callback) => {
        const Rule = /^[0-9a-zA-Z\u4e00-\u9fa5]{3,32}$/;
        if (!value) {
          return callback(new Error('数据源名称不能为空'));
        }
        setTimeout(() => {
          if (!Rule.test(value)) {
            return callback(new Error('数据源名称格式错误'));
          }
        }, 1000);
        callback();
      };
      const Name = (rule, value, callback) => {
        const Rule = /^\w{1,32}$/;
        if (!value) {
          return callback(new Error('数据库名不能为空'));
        }
        setTimeout(() => {
          if (!Rule.test(value)) {
            return callback(new Error('数据库名格式错误'));
          }
        }, 1000);
        callback();
      };
      const Host = (rule, value, callback) => {
        const Rule = /^[0-9a-zA-Z]{8,64}$/;
        if (!value) {
          return callback(new Error('数据库地址不能为空'));
        }
        setTimeout(() => {
          if (!Rule.test(value)) {
            return callback(new Error('数据库地址格式错误'));
          }
        }, 1000);
        callback();
      };
      const Port = (rule, value, callback) => {
        const Rule = /^[0-9]{1,11}$/;
        if (!value) {
          return callback(new Error('端口号不能为空'));
        }
        setTimeout(() => {
          if (!Rule.test(value)) {
            return callback(new Error('端口号格式错误'));
          }
        }, 1000);
        callback();
      };

      return {
        title: '新增数据源',
        curIdx: '',
        typeArr: [],
        DATA_TYPE_PHOENIX,
        DATA_TYPE_HBASE,
        form: {
          connectionName: '', // 连接名
          dbType: '', // 数据类型id
          dbName: '', // 数据源名称
          dbHost: '', // 数据库地址
          dbPort: '', // 端口
          dbUsername: '', // 用户名
          dbPassword: '', // 密码
          description: '',
          url: '', // PHOENIX url
        },
        rules: {
          dbType: [{ required: true, message: '数据源类型不能为空', trigger: 'blur' }],
          connectionName: [{ required: true, validator: connection, trigger: 'blur' }],
          dbName: [{ required: true, validator: Name, trigger: 'blur' }],
          dbHost: [{ required: true, validator: Host, trigger: 'blur' }],
          dbPort: [{ required: true, validator: Port, trigger: 'blur' }],
          zkNode: [{ required: true, message: 'zkNode节点名称不能为空' }],
          dbUsername: [{ required: true, message: '用户名不能为空', trigger: 'blur' }],
          dbPassword: [{ required: true, message: '密码不能为空', trigger: 'blur' }],
          url: [{ required: true, message: 'Url不能为空', trigger: 'blur' }],
        },
        loading: false,
        save: false, // 保存按钮
      };
    },
    created() {
      dataBase.showDataName().done(res => {
        // console.log(res);
        this.typeArr = res.data;
      });
    },
    methods: {
      loadForm() {
        if (this.appInfo) {
          this.title = '编辑数据源';
          dataSource.showItem({ dataSourceId: this.appInfo }).done(res => {
            this.form = Object.assign({}, initSource, res.data);
          });
        } else {
          this.title = '新建数据源';
          this.form = Object.assign({}, initSource);
          this.changeDatasourceType(this.form.dbType);
        }
        this.$nextTick(() => {
          this.$refs.ruleForm.clearValidate();
        });
      },
      hide() {
        this.form.connectionName = '';
        this.form.dbType = '';
        this.form.dbName = '';
        this.form.dbHost = '';
        this.form.dbPort = '';
        this.form.dbUsername = '';
        this.form.password = '';
        this.form.description = '';
        this.$emit('update:visible', false);
        // this.appInfo = '';
      },
      submit() {
        this.$emit('submit');
      },
      submitForm(formName) {
        this.$refs[formName].validate(valid => {
          if (valid) {
            this.onTest();
          } else {
            return false;
          }
        });
      },
      resetForm(formName) {
        this.$refs[formName].resetFields();
      },
      onTest() {
        this.loading = true;
        if (!this.form.dbType) {
          this.$message({ type: 'error', message: '请选择数据源类型' });
        } else {
          dataSource
            .testDataSource({ ...this.filterPhoenixSubmitForm(this.form) })
            .done(() => {
              this.save = true;
              this.$message({ type: 'success', message: '测试成功' });
            })
            .always(() => {
              this.loading = false;
            });
        }
      },
      filterPhoenixSubmitForm(form) {
        const { url, dbType, connectionName } = form;
        if (dbType === DATA_TYPE_PHOENIX) return { url, dbType, connectionName };
        return form;
      },
      onSaveOrEdit() {
        if (!this.appInfo) {
          this.onSave();
        } else {
          this.onEdit();
        }
      },
      onSave() {
        this.loading = true;
        dataSource
          .addNewdata({ ...this.filterPhoenixSubmitForm(this.form) })
          .done(() => {
            Message.closeAll();
            this.$message({
              type: 'success',
              message: '保存成功',
            });
            this.show = false;
            this.$emit('addSuccess');
          })
          .always(() => {
            this.loading = false;
          });
      },
      onEdit() {
        this.loading = true;
        dataSource
          .edit({ ...this.form, id: this.appInfo })
          .done(() => {
            Message.closeAll();
            this.$message({
              type: 'success',
              message: '保存成功',
            });
            this.show = false;
            this.$emit('addSuccess');
          })
          .always(() => {
            this.loading = false;
          });
      },
      onCancel() {
        this.show = false;
      },
      typeId(e) {
        this.form.dbType = +e.target.dataset.id;
      },
      changeDatasourceType(val) {
        const selectedDatasourceTypes = this.typeArr.filter(item => {
          return item.databaseName === val;
        });
        const defaultPort =
          !!selectedDatasourceTypes && selectedDatasourceTypes.length > 0
            ? selectedDatasourceTypes[0].defaultPort
            : null;
        this.form = Object.assign({}, this.form, {
          dbType: val,
          dbHost: null,
          dbPort: defaultPort,
          dbName: val === DATA_TYPE_HBASE ? 'default' : null,
          dbUsername: null,
          dbPassword: null,
          zkNode: val === DATA_TYPE_HBASE ? '/hbase-unsecure' : null,
        });
        this.$nextTick(() => {
          this.$refs.ruleForm.clearValidate();
        });
      },
    },
  };
</script>

<style scoped lang="less">
  .data-source-edit-dialog {
    .page-scrollbar {
      overflow-x: hidden;
      overflow-y: hidden;
      margin-bottom: 10px;
      /deep/ .el-scrollbar__wrap {
        overflow-x: hidden;
        max-height: calc(100vh - 210px);
      }
      // .new-task-form {
      //   padding: 0 10px 17px 0;
      // }
    }

    .main-title {
      height: 22px;
      font-size: 16px;
      margin: 10px 40px;
      font-weight: 600;
      color: rgba(51, 51, 51, 1);
      line-height: 22px;
    }
    /deep/ .el-input,
    /deep/ .el-textarea {
      max-width: 400px;
    }
    .comment {
      font-size: 13px;
      color: #2776fb;
    }
  }
</style>
