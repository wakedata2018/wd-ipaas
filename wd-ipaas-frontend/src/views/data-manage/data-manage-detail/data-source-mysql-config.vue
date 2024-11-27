<template>
  <div>
    <el-form-item key="dbHost"  label="数据源地址">
          <span>{{config.dbHost}}</span>
        </el-form-item>
        <el-form-item key="dbPort"  label="端口">
          <span>{{config.dbPort}}</span>
        </el-form-item>
        <el-form-item key="dbName"  label="数据库名称">
          <span>{{config.dbName}}</span>
        </el-form-item>
        <el-form-item key="dbUsername"  label="用户名">
          <span>{{config.dbUsername}}</span>
        </el-form-item>
  </div>
</template>

<script>
  import DataSourceMixins from './data-source-mixins';

  export default {
    name: 'data-source-mysql-config',

    mixins: [DataSourceMixins]
  };
</script>

<style scoped>

</style>
