<template>
  <div class="page-index bd-page" v-loading="loading">
    <!-- <div class="bd-header">
      <div class="index-title"> -->
    <div class="bd-header header-box">
      <div class="title">首页</div>
      <div class="show-frontend" v-if="admin" @click="toBackend">查看后端管理</div>
    </div>
    <div class="bd-container">
      <indexContent :data="index" />
    </div>
  </div>
</template>

<script>
  import indexContent from './index-content.vue';
  import adminApi from '../../api/user';

  export default {
    components: { indexContent },
    // mixins: [IPagesize],
    data() {
      return {
        // 轮播图数据
        // picsData: [banner1, banner2],
        card: [], // 卡片存放数组
        apiGroupList: [], // 测导航的存放数组
        apiGroupItemList: [], // 每个接口分类下的api
        dataAssetApiId: null, // 用于存放当前选择的apiid
        keyword: '',
        loading: false,
        filter: 'TIME', // 存放过滤条件
        totalCount: 0,
        appInfo: null,
        dialog: {
          app: false,
        },
        activeName: 'index',
        index: null,
        mydata: null,
        admin: false,
      };
    },
    computed: {},
    mounted() {
      // this.updateType()
      this.isAdmin();
    },
    methods: {
      // updateType(){
      //   // console.log(12)
      //   let type=location.search.type
      //   if(type==1){
      //     this.activeName='index'
      //     console.log(121)
      //   }else if(type==2){
      //     this.activeName='mydata'
      //   }
      // },
      handleClick(tab) {
        // let index;
        // let mydata;
        if (tab.name == 'index') {
          this.index = 'reload';
          this.mydata = '';
        } else if (tab.name == 'mydata') {
          this.index = '';
          this.mydata = 'reload';
        }
        // this.$router.push(`/index?type=${queryType}`)
      },
      toBackend() {
        window.location.href = `/`;
      },
      isAdmin() {
        adminApi.admin().done(res => {
          this.admin = res.data;
        });
      },
    },
  };
</script>

<style scoped lang="less">
  .page-index {
    width: 100%;
    // .bd-header {
    //   background-color: #282828;
    //   border: 0px;
    //   z-index: 15;

    // .index-title {
    //   margin: 0 auto;
    //   position: relative;

    //   .first_data {
    //     width: 100% !important;
    //     color: #dedede;
    //     display: inline-block;
    //     width: auto;
    //     a {
    //       &:nth-child(1) {
    //         margin-right: 30px;
    //       }
    //     }
    //   }
    .header-box {
      display: flex;
      align-items: center;
      .show-frontend {
        position: absolute;
        right: 20px;
        width: 127px;
        height: 32px;
        line-height: 32px;
        text-align: center;
        cursor: pointer;
        font-size: 14px;
        font-weight: 400;
        color: #2776fb;
        border: 1px solid #2776fb;
        border-radius: 2px;
      }
    }
    .bd-container {
      margin-top: 80px;
      background: white;
      position: relative;
      .search {
        display: inline-block;
        width: auto;
        right: 62px;
        left: auto;
        // position: absolute;
        z-index: 5;

        .el-icon-search {
          color: #dedede;
          font-size: 26px;
          cursor: pointer;
          margin-left: 8px;
        }
        // }
        // }
      }
    }

    .nav-guide {
      /deep/ .el-tabs__header > .el-tabs__nav-wrap {
        padding-left: 28%;
        margin-left: -240px;
        box-sizing: border-box;
        max-width: 1692px;
      }
      /deep/ .el-tabs__nav-wrap::after {
        background: transparent;
      }
      /deep/.el-tabs__active-bar {
        background: transparent;
      }
      /deep/.el-tabs__item {
        color: #fff;
      }
      /deep/.is-active {
        color: #2776fb !important;
        border-bottom: none;
      }
      /deep/ .el-tabs__header {
        margin-bottom: 1px;
      }
    }
  }

  @media screen and (max-width: 940px) {
    .page-index {
      .nav-guide {
        /deep/ .el-tabs__header > .el-tabs__nav-wrap {
          padding-left: 20px;
          margin-left: 0px;
        }
      }
    }
  }

  @media screen and (min-width: 1454px) {
    .page-index {
      .search {
        left: 1172px !important;
        right: auto !important;
      }
    }
  }
</style>
