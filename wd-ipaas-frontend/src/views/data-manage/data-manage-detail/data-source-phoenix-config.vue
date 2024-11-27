<template>
  <div>
    <el-form-item label="数据源地址" prop="dbHost">
      <span> {{config.url}}</span>
    </el-form-item>
  </div>
</template>

<script>
  import DataSourceMixins from './data-source-mixins';

  export default {
    name: 'data-source-phoenix-config',

    mixins: [DataSourceMixins],

    data() {
      return {
      };
    }
  };
</script>

<style scoped>

</style>
