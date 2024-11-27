<template>
  <div class="create-application-model">
    <div class="create-application-model-item" v-for="item in appTypeList" :key="item.type">
      <i class="el-icon-s-tools logo"></i>
      <div class="text">{{ item.title }}</div>
      <div class="text">{{ item.desc }}</div>
      <el-button type="primary" @click="onCreate(item.type)" class="create-btn">创建</el-button>
    </div>
  </div>
</template>
<script>
  import { APPLICATION_TYPE } from '../../../enum.js';

  export default {
    data() {
      return {
        appTypeList: APPLICATION_TYPE,
      };
    },
    methods: {
      // 创建选定模式的应用
      onCreate(type) {
        this.$emit('on-create', type);
      },
    },
  };
</script>
<style lang="less" scoped>
  .create-application-model {
    display: flex;
    background: #fff;
    padding: 50px;
    /deep/ .el-empty__description p {
      font-size: 18px;
      color: #000;
      font-weight: 900;
    }

    /deep/ .el-button {
      width: 250px;
      cursor: pointer;
    }
    .create-btn {
      margin-top: 40px;
    }

    .logo {
      font-size: 40px;
      color: #2776fb;
    }

    &-item {
      border: 1px solid #e1e1e1;
      padding: 40px;
      display: flex;
      flex-direction: column;
      align-items: center;
      width: 300px;
      height: 300px;
      margin: 20px;
      .text {
        line-height: 40px;
      }
    }
  }
</style>
