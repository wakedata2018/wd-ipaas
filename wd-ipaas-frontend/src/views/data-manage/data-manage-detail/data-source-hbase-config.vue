<template>
  <div>
    <el-form-item :label="'zk '+'主机名'" prop="dbHost">
      <span> {{config.dbHost}}</span>
    </el-form-item>

    <el-form-item :label="'zk '+'端口'" prop="dbPort">
      <span> {{config.dbPort}}</span>
    </el-form-item>

    <el-form-item :label="'数据库名称'" prop="dbName">
      <span> {{config.dbName}}</span>
    </el-form-item>
    <el-form-item :label="'zk'+'节点名称'" prop="zkNode">
      <span> {{config.zkNode}}</span>
    </el-form-item>
  </div>
</template>

<script>
  import DataSourceMixins from './data-source-mixins';

  export default {
    name: 'data-source-hbase-config',

    mixins: [DataSourceMixins],

    data() {
      return {
      };
    }
  };
</script>

<style scoped>

</style>
