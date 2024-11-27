<template>
  <el-dialog lock-scroll
             append-to-body
             class="bd-dialog data-manage-dialog"
             title="数据源详情"
             :visible.sync="show"
             :close-on-click-modal="true"
             @open="loadForm"
             width="600px">
    <el-scrollbar ref="scrollbar" class="page-scrollbar">
      <el-form v-loading="loading" :model="sourceForm" ref="sourceForm" label-width="150px">
        <el-form-item key="connectionName" label="数据源名称"  prop="connectionName">
          <span>{{sourceForm.connectionName}}</span>
        </el-form-item>
        <el-form-item key="dbType" label="数据源类型"  prop="dbType">
          <span>{{sourceForm.dbType}}</span>
        </el-form-item>
        <component v-bind:is="configComponent" :config="sourceForm" />
        <el-form-item key="description" v-if="sourceForm.dbType !== 'PHOENIX'"  label="描述">
          <span>{{sourceForm.description}}</span>
        </el-form-item>
      </el-form>
    </el-scrollbar>
  </el-dialog>
</template>

<script>
import mixSourceType from '../../../mixins/mix-source-type';
import DataSourceHbaseConfig from './data-source-hbase-config';
import DataSourceMysqlConfig from './data-source-mysql-config';
import DataSourcePhoenixConfig from './data-source-phoenix-config.vue';
const initSourceForm = {
  dbType: 'mysql',
  connectionName: null,
  dbHost: null,
  dbPort: 3306,
  dbName: null,
  dbUsername: '',
  dbPassword: '',
  timeOffset: 0
};
export default {
  mixins: [mixSourceType],
  components:{
    DataSourceHbaseConfig,
    DataSourceMysqlConfig,
    DataSourcePhoenixConfig
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    source: {
      type: Object,
      default: function () {
        return null;
      }
    },
  },
  data () {
    return {
      loading: false,
      sourceForm: {},
      sourceType: null,
    }
  },
  watch: {
    sourceForm: {
      deep: true,
      immediate: true,
      handler(val) {
        if (!!this.sourceForm.dbPassword) this.sourceForm.hiddenPwd = this.getHiddenPwd(this.sourceForm.dbPassword);
      }
    }
  },
  computed: {
    show: {
      get () {
        return this.visible;
      },
      set (val) {
        if (!val) {
          this.onDialogClose();
        }
      }
    },
    configComponent() {
      const cmpMap = {
        HBASE: DataSourceHbaseConfig,
        PHOENIX: DataSourcePhoenixConfig
      };
      const cmp = cmpMap[this.sourceForm.dbType];
      return cmp || DataSourceMysqlConfig;
    }
  },
  methods: {
    getHiddenPwd(pwd) {
      return ''.padEnd(pwd.length, '*');
    },
    loadForm() {
      // this.loading = true;
      // this.mix_getDataSourceTypes(this.functionType).always(() => {
      //   this.loading = false;
      //   let finded = this.mix_dataSourceType.filter(item => item.value === this.sourceForm.dbType);
      //   this.sourceType = !!finded && finded.length > 0 ? finded[0].label : this.sourceForm.dbType;
      // });
      this.sourceType = this.sourceForm.dbType;
      if (this.source) {
        this.sourceForm = Object.assign({}, initSourceForm, this.source);
        //删除createTime字段，更新时不需要带该字段
        // console.log(this.sourceForm);
        delete this.sourceForm.createTime;
      } else {
        this.sourceForm = Object.assign({}, initSourceForm);
      }
    },
    onDialogClose () {
      this.$emit('update:visible', false);
      this.$emit('close');
    }
  }
}
</script>

<style lang="less" scoped>
.data-manage-dialog {
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
  /deep/ .el-form-item__label {
    color: #2e8dff;
    font-weight: 600;
  }
}
</style>
