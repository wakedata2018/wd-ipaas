<template>
  <div class="doc-header">
    <div class="logo">
      <a href="/"><img :src="info.miniLogoUrl" /></a>
    </div>
    <div class="title">{{ info.systemName }}·API文档</div>
    <div class="right-action">
      <el-button v-if="!user?.displayName" type="primary" class="login" size="medium" @click="goToLogin"
        >登录</el-button
      >
      <!-- 后期实现 -->
      <!-- <el-input prefix-icon="el-icon-search" class="search" size="medium" placeholder="搜索"></el-input> -->
    </div>
  </div>
</template>
<script>
  import { mapState } from 'vuex';

  export default {
    name: 'ApiDocHeader',
    props: {
      info: {
        type: Object,
        default: () => ({}),
      },
    },
    computed: {
      ...mapState({
        user: state => state.user,
      }),
    },
    created() {
      /**
       * 获取系统名称和logo
       */
      this.getName();
    },
    methods: {
      // 跳转到登录页
      goToLogin() {
        window.location.href = '/login.html';
      },
    },
  };
</script>
<style lang="less" scoped>
  .doc-header {
    width: 100%;
    height: 56px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: fixed;
    top: 0px;
    background: #fff;
    z-index: 1;

    .logo img {
      vertical-align: middle;
      height: 30px;
      margin: 0px 5px 0px 10px;
      object-fit: cover;
    }

    .title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }

    .right-action {
      flex: 1;
      display: flex;
      padding: 10px;
      flex-direction: row-reverse;

      .search {
        width: 250px;
      }

      .login {
        margin-left: 40px;
        width: 80px;
        height: 36px;
        background: #1890ff;
        border: none;
      }
    }
  }
</style>
