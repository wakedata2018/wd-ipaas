<template>
  <div class="prosession-table">
    <el-table :data="appAuthList" style="width: 100%" class="dss-table bd-table" border v-loading="appAuthListLoading">
      <el-table-column label="步骤名称" prop="stepChineseName" align="center">
        <template slot-scope="scope">
          <div>{{ scope.row.stepChineseName }}</div>
          <el-button type="text" @click="addAuthApp(scope.row)">授权应用</el-button>
        </template>
      </el-table-column>
      <el-table-column label="应用名称" prop="authName" align="center">
        <template slot-scope="scope">
          <div v-for="item in scope.row.authInfoVos" :key="item.id" class="app-info-container">
            <div class="appLogo"><img :src="item.appLogo" class="appLogo" v-if="item.appLogo" /></div>
            <div>{{ item.appName }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="应用描述" prop="description" align="center">
        <template slot-scope="scope">
          <div v-for="item in scope.row.authInfoVos" :key="item.id" class="app-info-container">
            <div>{{ item.description }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="最后授权时间" prop="authorizationTime" align="center">
        <template slot-scope="scope">
          <div v-for="item in scope.row.authInfoVos" :key="item.id" class="app-info-container">
            <div>{{ item.authorizationTime }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center">
        <template slot-scope="scope">
          <div v-for="item in scope.row.authInfoVos" :key="item.id" class="app-info-container">
            <el-button type="text" @click="onRelieve(item.id, scope.row.apiId)">移除授权</el-button>
          </div>
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
      operatorId: {
        type: Number,
        default: -1,
      },
      appAuthListLoading: {
        type: Boolean,
        default: false,
      },
    },
    methods: {
      addAuthApp(param) {
        this.$emit('update:appAuthVisible', true);
        this.$emit('update:operatorId', param.apiId);
      },
      onRelieve(authId, apiId) {
        dataAccessApp
          .relieveThirdAuth({
            apiId,
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
  .appLogo {
    width: 50px;
    height: 50px;
  }
  .app-info-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 60px;
  }
</style>
