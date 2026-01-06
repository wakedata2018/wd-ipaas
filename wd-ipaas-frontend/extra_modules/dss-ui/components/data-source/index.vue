<template>
  <div class="data-source">
    <div class="header">数据源管理</div>
    <div class="body">
      <source-list @source-selected="onSourceSelected" @refresh="onRefresh"></source-list>
      <div class="table-list">
        <file-table-list v-if="isShowFileTable" :source-id="sourceId" :refresh="refresh" :sourceName="sourceName" :type="type" />
        <table-list v-else :source-id="sourceId" :refresh="refresh"></table-list>
      </div>
    </div>
  </div>
</template>

<script>
import SourceList from './source-list/index.vue';
import TableList from './table-list/index.vue';
import FileTableList from './file-table-list/index.vue';

export default {
  mixins: [],
  components: { SourceList, TableList, FileTableList },
  props: {
    // 是否打开加密
    enableEncrypt: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      sourceId: 0,
      refresh: false,
      type: '',
      sourceName: '' // 数据源名称
    };
  },
  provide() {
    return {
      enableEncrypt: this.enableEncrypt
    };
  },
  computed: {
    isShowFileTable() {
      return ['hdfs', 'ftp', 'sftp'].includes(this.type)
    }
  },
  watch: {},
  mounted() {},
  methods: {
    onSourceSelected(item) {
      this.sourceId = item.id;
      this.type = item.dataSourceType
      this.sourceName = item.dataSourceName
    },
    onRefresh() {
      this.refresh = !this.refresh;
    }
  }
};
</script>
<style lang="less" scoped>
.data-source {
  width: 100%;
  height: 100%;
  background-color: #f3f6f8;
  color: #333333;
  font-size: 14px;
  .header {
    padding-left: 20px;
    line-height: 60px;
    background: #ffffff;
    font-size: 16px;
    font-weight: 600;
  }
  .body {
    padding: 20px;
    width: 100%;
    height: calc(100% - 60px);
    position: relative;
    .table-list {
      position: absolute;
      top: 20px;
      left: 340px;
      right: 20px;
      bottom: 20px;
      background-color: #ffffff;
    }
  }
  * {
    box-sizing: border-box;
  }
}
</style>
