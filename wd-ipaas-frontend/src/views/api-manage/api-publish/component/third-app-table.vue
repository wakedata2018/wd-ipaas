<template>
  <div>
    <el-table :data="appAuthList" style="width: 100%" class="dss-table bd-table">
      <el-table-column label="应用名称" prop="appName"></el-table-column>
      <el-table-column label="应用描述" prop="description"></el-table-column>
      <el-table-column label="最后授权时间" prop="updateTime"></el-table-column>
      <el-table-column label="操作">
        <template slot-scope="{ row }">
          <el-button @click="onRelieve(row.id)" type="text">解除授权</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
  import dataAccessApp from '@/api/data-access-app.js';

  export default {
    props: {
      appAuthVisible: {
        type: Boolean,
        default: false,
      },
      appAuthList: {
        type: Array,
        default: () => {
          return [];
        },
      },
      appAuthListLoading: {
        type: Boolean,
        default: false,
      },
      apiInfo: {
        type: Object,
        default: () => {
          return {};
        },
      },
    },
    methods: {
      addAuthApp() {
        this.$emit('update:appAuthVisible', true);
      },
      onRelieve(authId) {
        dataAccessApp
          .relieveThirdAuth({
            apiId: this.apiInfo.dataAssetApiId,
            authId,
          })
          .then(res => {
            this.$message({
              type: 'success',
              message: '解除授权成功',
            });
            this.$emit('get-third-app-auth-api-list');
          });
      },
    },
  };
</script>

<style lang="less" scoped>
  .prosession-table {
    margin-top: 20px;
  }
</style>
