<template>
  <el-drawer
    :title="(info.dataSourceType ? '修改' : '新增') + '数据源'"
    :visible.sync="show"
    direction="rtl"
    size="75%"
    :close-on-press-escape="false"
    @open="openDrawer"
  >
    <div class="drawer" v-loading="loading.save || testInfo.loading">
      <el-scrollbar v-if="show">
        <div class="body">
          <div class="title" style="margin-top: 0">选择数据库类型</div>
          <source-select :data="sourceType" v-model="params.dataSourceType" @type-change="typeChange"></source-select>
          <div class="title">配置数据库参数</div>
          <source-form
            ref="sourceForm"
            :refresh="refresh"
            :params="params"
            :fields="fields"
            v-loading="loading.params"
            @password-edited="addEditedPassword"
          ></source-form>
        </div>
      </el-scrollbar>
      <div class="footer">
        <el-button @click="closeDrawer">取消</el-button>
        <el-button
          :type="testInfo.type"
          :disabled="getPublicKeyPrepared()"
          :title="getDisabledMessage()"
          @click="onTest"
          v-show="testInfo.visible"
          >{{ testInfo.text }}</el-button
        >
        <el-button
          type="primary"
          @click="onSave"
          :disabled="saveDisabled || getPublicKeyPrepared()"
          :title="saveDisabled ? '测试连接成功才可保存' : getPublicKeyPrepared() ? getDisabledMessage() : ''"
          >保存</el-button
        >
      </div>
    </div>
  </el-drawer>
</template>

<script>
import SourceSelect from './source-select/index.vue';
import SourceForm from './source-form';
import sourceApi from '../../../api/dataSource.js';
import store from '../../store.js';
import CONSTANTS from '../../constants.js';
import { Drawer as ElDrawer } from 'element-ui';
import { rsa } from 'dss-common';

function defaultTestInfo() {
  return {
    type: 'primary',
    text: '测试连接',
    loading: false,
    visible: false,
  };
}

export default {
  name: 'NewSource',
  components: { SourceSelect, SourceForm, ElDrawer },
  inject: ['getHasPublicKey', 'enableEncrypt'],
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    info: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      sourceType: [],
      defaultParams: {
        dataSourceName: '', // 数据源名称
        dataSourceType: '', // 数据库类型
      },
      params: {
        dataSourceType: null,
        dataSourceConf: {},
      },
      paramsMap: CONSTANTS.paramsMap,
      loading: {
        params: false,
        save: false,
      },
      supportTestTypes: CONSTANTS.supportTestTypes,
      testInfo: defaultTestInfo(),
      fields: [],
      refresh: false,
      editedPasswordArr: [],
      prependMap: {} // 控件前置内容
    };
  },
  provide() {
    return {
      getEditedPasswordArr: this.getEditedPasswordArr,
    };
  },
  watch: {
    'params.dataSourceType'(val) {
      // this.getSourceParams(val);
      this.testInfo = defaultTestInfo();
      this.setCanTest(val, this.sourceType);
      this.editedPasswordArr = [];
    },
    sourceType(val) {
      this.setCanTest(this.params.dataSourceType, val);
    },
    params: {
      handler() {
        this.testInfo.type = 'primary';
        this.testInfo.text = '测试连接';
      },
      deep: true,
    },
  },
  computed: {
    show: {
      get() {
        return this.visible;
      },
      set(val) {
        this.$emit('update:visible', val);
      },
    },
    saveDisabled() {
      return this.testInfo.type !== 'success' && this.testInfo.visible;
    },
    hasPublicKey() {
      return this.getHasPublicKey();
    },
  },
  methods: {
    // 打开抽屉时
    openDrawer() {
      this.sourceType = store.state.sourceType || [];
      this.clearEditedPassword();
      this.initParams();
    },
    // 初始化参数
    initParams(type) {
      this.params = { ...this.defaultParams };
      this.$set(
        this.params,
        'dataSourceType',
        type || this.info.dataSourceType || (this.sourceType[0] || {}).dataSourceType
      );
      this.$set(this.params, 'dataSourceId', this.info.id || null);
      this.$set(this.params, 'additionalParam', this.info.additionalParam || null);
      if ((this.info.dataSourceType && type === undefined) || this.info.dataSourceType === type) {
        this.$set(this.params, 'businessSystem', this.info.businessSystem || null);
        this.$set(this.params, 'remark', this.info.remark || null);
      }
      this.clearValidate();
      this.getSourceParams(this.params.dataSourceType);
    },
    getPublicKeyPrepared() {
      return this.enableEncrypt && this.editedPasswordArr.length > 0 && !this.hasPublicKey;
    },
    getDisabledMessage() {
      return this.getPublicKeyPrepared() && '未获取到公钥';
    },
    clearEditedPassword() {
      this.editedPasswordArr = [];
    },
    addEditedPassword(prop) {
      !this.willBeEncrypted(prop) && this.editedPasswordArr.push(prop);
    },
    willBeEncrypted(prop) {
      return this.editedPasswordArr.indexOf(prop) !== -1;
    },
    setCanTest(dbType, sourceTypes) {
      let typeObj = sourceTypes.find(item => item.dataSourceType === dbType);
      let canTest = false;
      if (typeObj && typeObj.jarPath) {
        canTest = typeObj.dataSourceType;
      }
      if (canTest) {
        this.testInfo.visible = true;
      } else {
        this.testInfo.visible = false;
      }
    },
    typeChange(val) {
      this.initParams(val.dataSourceType);
    },
    getSourceParams(type) {
      this.loading.params = true;
      sourceApi
        .queryDataSourceParamsArray(type)
        .done(res => {
          this.fields = res.data || [];
          for (let i = 0; i < this.fields.length; i++) {
            let tempKey = this.fields[i].paramKey;
            // if (type === 'redis' || type === 'mingYuanCloudApi') {
            // for (const k in CONSTANTS.paramsMap) {
            //   if (this.fields[i].paramKey === CONSTANTS.paramsMap[k]) {
            //     tempKey = k;
            //   }
            // }
            var rep = /[\.]/;
            if (rep.test(tempKey)) {
              tempKey = tempKey.replace(/\./g, '_$');
            }
            // }
            this.fields[i].prop = tempKey;
            this.$set(this.params, tempKey, this.fields[i] && this.fields[i].paramDefaultValue);
            // 提取控件前置内容
            if (this.fields[i].prepend) {
              this.$set(this.params, this.fields[i].prependKey, this.fields[i].prepend);
            }
          }

          // mysqlssh有些参数接口没返回，手动增加
          if (type === 'mysqlssh') {
            this.params = { ...this.params, ...CONSTANTS.sshParams };
          }
          if (this.info.dataSourceType && this.info.dataSourceType === type) {
            this.loadParamsByEditInfo();
          }
          this.refresh = !this.refresh;
          this.clearValidate();
        })
        .always(() => {
          this.loading.params = false;
        });
    },
    loadParamsByEditInfo() {
      // 如果是编辑，则从info中读取数据
      let dataSourceConf = {};
      for (const key in this.info.dataSourceConf) {
        let tempKey = key;
        var rep = /[\.]/;
        if (rep.test(key)) {
          tempKey = key.replace(/\./g, '_$');
          this.$set(dataSourceConf, tempKey, this.info.dataSourceConf[key]);
        } else {
          this.$set(dataSourceConf, key, this.info.dataSourceConf[key]);
        }
      }
      for (const key in this.params) {
        if (key === 'ssh') continue;
        if (key === 'dataSourceId') {
          this.params[key] = this.info.id;
        } else {
          this.$set(this.params, key, this.info[key] || (dataSourceConf && dataSourceConf[key]));
        }
      }
    },
    clearValidate() {
      this.$nextTick(() => {
        this.$refs.sourceForm.clearValidate();
      });
    },
    saveValidate() {
      return this.$refs.sourceForm.validate() && this.params.dataSourceType;
    },
    getFinalParams() {
      const params = {};
      Object.keys(this.params).map(prop => {
        const item = this.params[prop];
        item && (params[prop] = this.enableEncrypt && this.willBeEncrypted(prop) ? rsa.getEncryCode(item) : item);
      });
      return params;
    },
    onTest() {
      if (this.saveValidate()) {
        this.testInfo.loading = true;
        const data = this.getFinalParams();
        let params = this.paramsChange(data);
        sourceApi
          .connectTesting(params)
          .done(
            res => {
              this.testInfo.type = 'success';
              this.testInfo.text = '连接成功';
            },
            res => {
              // this.$message.error('连接失败');
              this.testInfo.type = 'danger';
              this.testInfo.text = '点击重试';
            }
          )
          .always(() => {
            this.testInfo.loading = false;
          });
      }
    },
    onSave() {
      if (this.saveValidate()) {
        // this.loading.save = true;
        let data = this.getFinalParams();
        let params = this.paramsChange(data);

        if (params.dataSourceType === 'redis') {
          params.dataSourceConf.redis = true;
        }
        if (['mysqlssh', 'ftp', 'sftp'].includes(params.dataSourceType)) {
          params.dataSourceConf.ssh = true;
        }
        if (this.info.dataSourceType) {
          sourceApi
            .updateDataSource(params)
            .done(res => {
              this.$message.success('保存成功');
              this.closeDrawer();
              this.$emit('save');
            })
            .always(() => {
              this.loading.save = false;
            });
        } else {
          sourceApi
            .saveDataSource(params)
            .done(res => {
              this.$message.success('保存成功');
              this.closeDrawer();
              this.$emit('save');
            })
            .always(() => {
              this.loading.save = false;
            });
        }
      }
    },
    paramsChange(data) {
      let param = {
        dataSourceConf: {},
      };
      for (const key in data) {
        if (key === 'dataSourceName' || key === 'dataSourceType' || key === 'businessSystem' || key === 'remark') {
          this.$set(param, key, data[key]);
        } else if (key === 'dataSourceId') {
          this.$set(param, 'id', data[key]);
        } else {
          var rep = /[\_\$]/;
          if (rep.test(key)) {
            let tempKey = key.replace(/\_\$/g, '.');
            this.$set(param.dataSourceConf, tempKey, data[key]);
          } else {
            this.$set(param.dataSourceConf, key, data[key]);
          }
        }
      }
      if (data.dataSourceType === 'mysqlssh') {
        for (const k in CONSTANTS.paramsMap) {
          if (data[k] && k !== 'dataSourceId') {
            this.$set(param.dataSourceConf, k, data[k]);
          }
        }
      }
      if (param.dataSourceConf['ssh.type'] === 'password') {
        param.dataSourceConf['ssh.passphrase'] = '';
        param.dataSourceConf['ssh.private.key.path'] = '';
      } else if (param.dataSourceConf['ssh.type'] === 'key') {
        param.dataSourceConf['ssh.pwd'] = '';
      }
      return param;
    },
    closeDrawer() {
      this.show = false;
    },
    getEditedPasswordArr() {
      return this.editedPasswordArr;
    },
  },
};
</script>

<style lang="less" scoped>
@import '../../style.less';
.body {
  padding: 0 40px 30px;
  .title {
    margin: 20px 0;
    font-size: 16px;
    font-weight: 600;
    color: #333333;
  }
}
</style>