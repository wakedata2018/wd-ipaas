<template>
  <el-dialog title="数据源详情" :visible.sync="show" top="50vh" width="30%" @open="openDialog" v-loading="loading">
    <div class="content">
      <div class="rows" v-for="(val, key) in detailObj" :key="key">
        <span> </span>
        <span class="label" v-if="key === 'reference'"
          >{{ platformKeyEnum[platformKey] }}<span>平台</span>{{ sourceDetailLabel[key] }}：</span
        >
        <span class="label" v-else>{{ sourceDetailLabel[key] || key }}：</span>
        <span class="value ellipsis" :title="val">{{
          val === 'client' ? '单机' : val === 'cluster' ? '集群' : val
        }}</span>
      </div>
    </div>
  </el-dialog>
</template>

<script>
import CONSTANTS from '../../constants.js';
import sourceApi from '../../../api/dataSource.js';
export default {
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    detail: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      sourceDetailLabel: CONSTANTS.sourceDetailLabel,
      platformKeyEnum: CONSTANTS.platformKeyEnum,
      defaultDetailObj: {
        name: '',
        type: '',
      },
      detailObj: {},
      platformKey: '',
      loading: false,
      fields: [],
    };
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
  },
  methods: {
    openDialog() {
      this.refresh();
    },
    refresh() {
      this.getSourceParams(this.detail.dataSourceType);
    },
    getSourceParams(type) {
      this.loading = true;
      sourceApi
        .queryDataSourceParamsArray(type)
        .done(res => {
          this.fields = res.data || [];
          this.initDetail();
        })
        .always(() => {
          this.loading = false;
        });
    },
    initDetail() {
      this.detailObj = {};
      this.platformKey = this.detail.platformKey;
      for (let item in this.detail) {
        if (this.sourceDetailLabel[item]) {
          this.detailObj[item] = this.detail[item];
        }
      }
      for (let val = 0; val < this.fields.length; val++) {
        if (this.detail.dataSourceConf[this.fields[val].paramKey]) {
          if (this.fields[val].controlType !== 'TABLE' && this.fields[val].controlType !== 'PASSWORD') {
            this.detailObj[this.fields[val].paramName] = this.detail.dataSourceConf[this.fields[val].paramKey];
          }
        }
      }
      const displayProp = ['ssh.host', 'ssh.port'];
      for (let val = 0; val < displayProp.length; val++) {
        if (this.detail.dataSourceConf[displayProp[val]]) {
          this.detailObj[this.sourceDetailLabel[displayProp[val]]] = this.detail.dataSourceConf[displayProp[val]];
        }
      }
    },
  },
};
</script>

<style lang="less" scoped>
// @import '../../style.less';
.el-dialog__wrapper {
  /deep/ .el-dialog {
    margin: auto;
    transform: translateY(-50%);
    border-radius: 4px;
    .el-dialog__header {
      padding: 20px;
      border-bottom: 1px solid #e6e6e6;
      .el-dialog__title {
        font-size: 16px;
        font-weight: 600;
        color: #333333;
      }
    }
    .el-dialog__body {
      padding: 20px 20px 10px;
    }
  }
}
.content {
  width: fit-content;
  width: -webkit-fit-content;
  width: -moz-fit-content;
  margin: auto;
  max-width: 100%;
  // background-color:cadetblue;
  .rows {
    width: 100%;
    height: auto;
    display: flex;
    margin-bottom: 10px;
    // background-color: burlywood;
    .label {
      display: block;
      width: 160px;
      text-align: right;
      color: #a8a8a8;
      // vertical-align: bottom;
      // background-color: cornflowerblue;
    }
    .value {
      display: inline-block;
      margin-left: 5px;
      width: calc(100% - 125px);
      // background-color:coral;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      //（行数）
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
    }
  }
}
// .content {
//   width: fit-content;
//   width: -webkit-fit-content;
//   width: -moz-fit-content;
//   max-width: 100%;
//   height: auto;
//   margin: auto;
//   background-color: burlywood;
//   .rows {
//     width: 100%;
//     height: auto;
//     display: flex;
//     margin-bottom: 10px;
//     .label {
//       display: block;
//       width: 115px;
//       height: auto;
//       background-color: cadetblue;
//       text-align: right;
//       color: #a8a8a8;
//     }
//     .value {
//       display: inline-block;
//       width: calc(100% - 120px);
//       height: auto;
//       background-color: coral;
//       margin-left: 5px;

//     }
//   }
// }
</style>