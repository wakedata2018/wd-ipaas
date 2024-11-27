<template>
  <div class="page-data-source bd-page">
    <div class="bd-header">
      <div class="header-content">
        <el-breadcrumb separator-class="el-icon-arrow-right">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>我的数据</el-breadcrumb-item>
        </el-breadcrumb>
        <!-- <div class="header-right">
          <el-button type="primary" @click="onAdd">+API申请</el-button>
        </div> -->
      </div>
    </div>
    <div class="bd-container">
      <el-tabs v-model="activeName" @tab-click="handleClick">
        <el-tab-pane label="我的接入" name="join">
          <Data />
        </el-tab-pane>
        <el-tab-pane label="我的申请" name="apply">
          <Apply />
        </el-tab-pane>
        <el-tab-pane label="我的收藏" name="collect">
          <Collect />
        </el-tab-pane>
        <el-tab-pane label="我的API统计" name="statistic">
          <Statistics />
        </el-tab-pane>
      </el-tabs>
    </div>
    <new-join :app-info="appInfo" :visible.sync="dialog.app" @saved="onSaved" />
  </div>
</template>

<script>
import Data from './data/data.vue';
import Apply from './apply/apply.vue';
import Collect from './collect/collect.vue';
import Join from './join/join.vue';
import Statistics from './statistics/statistics.vue';
import NewJoin from './new-join.vue';

export default {
  components: { Data, Apply, Collect, Statistics, Join ,NewJoin},

  data() {
    return {
      activeName: 'join',
      appInfo:null,
      dialog:{
        app:false
      }
    };
  },
  methods: {
    handleClick(tab, event) {
      //   console.log(tab, event);
    },
    onAdd(){
      this.appInfo=null
      this.dialog.app=true
      console.log('新增')
    },
    onSaved() {
      console.log('确定');
    },

    showItem() {
    }
  }
};
</script>

<style scoped lang="less">
.page-data-source {
  width: 100%;
  .bd-header {
    background-color: #fff;
    display: flex;
    align-items: center;
    z-index: 12;
    .header-content {
      width: calc(100% - 80px);
      max-width: 1200px;
      margin: 0 auto;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }
  .bd-container {
    width: calc(100% - 80px);
    max-width: 1200px;
    margin: 80px auto;
    background-color: #fff;
    padding: 20px;
  }
  /deep/ .el-tabs__nav-wrap::after {
    background: transparent;
  }
}
</style>