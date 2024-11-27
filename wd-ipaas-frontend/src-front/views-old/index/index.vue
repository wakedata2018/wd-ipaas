<template>
  <div class="page-index bd-page" v-loading="loading">
    <div class="bd-header">
      <div class="index-title">
        <div class="first_data">
          <router-link to="/">首页</router-link>
          <router-link to="/mydata">我的数据</router-link>
        </div>
        <div class="search">
          <el-input v-model="keyword" type="text" suffix-icon="el-icon-search" placeholder="请输入搜索内容"></el-input>
        </div>
      </div>
    </div>
    <div class="bd-container">
      <el-row type="flex" class="nav_pics">
        <div class="nav">
          <el-scrollbar class="left-scroll">
            <div class="left-content">
              <span v-for="(item, index) in apiGroupList" :key="index" :title="item[0]">
                <router-link :to="`/index/company?groupId=${item[1]}&groupName=${item[0]}`">{{ item[0] }}</router-link>
              </span>
            </div>
          </el-scrollbar>
        </div>

        <div class="pics">
          <el-carousel :interval="5000" arrow="hover">
            <el-carousel-item v-for="(item, index) in picsData" :key="index">
              <img :src="item" alt />
            </el-carousel-item>
          </el-carousel>
        </div>
      </el-row>

      <div class="title">最新数据</div>
      <div v-for="(item, index) in filteredCard" :key="index">
        <list-item :item="item" @command="handleCommand"></list-item>
      </div>
    </div>
  </div>
</template>

<script>
  import index from '../../api/index.js';
  import listItem from './list-item.vue';
  import banner1 from '../../assets/images/banner1.jpg';
  import banner2 from '../../assets/images/banner2.jpg';
  export default {
    data() {
      return {
        //轮播图数据
        picsData: [banner1, banner2],
        card: [], //卡片存放数组
        apiGroupList: [], //测导航的存放数组
        dataAssetApiId: null, //用于存放当前选择的apiid
        keyword: '',
        loading: false,
      };
    },
    components: { listItem },
    computed: {
      filteredCard() {
        return this.card.filter(item => item.apiName.toUpperCase().indexOf(this.keyword.toUpperCase()) !== -1);
      },
    },
    mounted() {
      //请求轮播图数据
      // this.$axios({
      //     url:"/xxx"
      // }).then(res => {
      //     const {data} = res.data
      //     this.picsData = data
      // })
    },
    created() {
      this.getCardList();
      this.getApiGroup();
    },
    methods: {
      handleCommand(operation, obj) {
        if (!!this['on' + operation]) this['on' + operation](...obj);
      },
      onCollect(dataAssetApiId, collected) {
        // console.log(collected);
        if (collected == 0) {
          this.$confirm('确认收藏吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
            // center: true
          })
            .then(() => {
              index
                .addCollect({
                  dataAssetApiId,
                })
                .done(res => {
                  this.$message({
                    type: 'success',
                    message: '收藏成功',
                  });
                  this.getCardList();
                });
            })
            .catch(res => {
              console.log('用户取消操作', res);
            });
        } else {
          this.$confirm('确定取消该收藏吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
            // center: true
          })
            .then(() => {
              index.deleteCollect({ collectId: dataAssetApiId }).done(res => {
                this.$message({
                  type: 'success',
                  message: '取消收藏成功',
                });
                this.getCardList();
              });
            })
            .catch(res => {
              console.log('用户取消操作', res);
            });
        }
      },
      onApply(dataAssetApiId) {
        this.$confirm('确认申请吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          // center: true
        })
          .then(() => {
            index.apply([dataAssetApiId]).done(res => {
              this.$message({
                type: 'success',
                message: '申请成功',
              });
              this.getCardList();
            });
          })
          .catch(res => {
            console.log('用户取消操作', res);
          });
      },
      getCardList() {
        this.loading = true;
        index
          .getCardList()
          .done(res => {
            res.data.forEach(item => {
              if (item.approval == 0 && item.created == 0) {
                item.approvalStatus = '申请';
              } else if (item.approval == 1) {
                item.approvalStatus = '已申请';
              } else {
                item.approvalStatus = '待审批';
              }
            });
            this.card = res.data;
            // console.log(this.card);
          })
          .always(() => {
            this.loading = false;
          });
      },
      getApiGroup() {
        index.getApiGroup().done(res => {
          // console.log(res)
          const arr = [];
          const arr1 = [];
          for (let key in res.data) {
            let newKey = key.split('____');
            arr.push(newKey);
            arr1.push(res.data[key]);
          }
          this.apiGroupList = arr;
        });
      },
      onClickItem(item) {
        this.$router.push(
          `/index/userdata?dataAssetApiId=${item.dataAssetApiId}&collected=${item.collected}&approval=${item.approval}&created=${item.created}`
        );
      },
    },
  };
</script>

<style scoped lang="less">
  .page-index {
    width: 100%;
    .bd-header {
      background-color: #282828;
      border: 0px;
      z-index: 15;
      .index-title {
        width: calc(100% - 80px);
        max-width: 1200px;
        margin: 0 auto;
        position: relative;

        .first_data {
          font-size: 16px;
          font-weight: 600;
          color: #dedede;
          display: inline-block;
          width: auto;
          a {
            &:nth-child(1) {
              margin-right: 30px;
            }
          }
        }
        .search {
          display: inline-block;
          width: auto;
          right: 0px;
          position: absolute;
          right input {
            width: 195px;
            height: 26px;
            text-indent: 10px;
            outline: none;
          }
          .el-icon-search {
            color: #dedede;
            font-size: 26px;
            cursor: pointer;
            margin-left: 8px;
          }
        }
      }
    }

    .bd-container {
      padding-top: 80px;
      width: calc(100% - 80px);
      max-width: 1200px;
      margin: 0 auto;
      // height: 60px;
      justify-content: space-between;
      align-items: center;
      box-shadow: 0px 0px 0px !important;
    }

    .nav_pics {
      justify-content: space-between;
      margin-bottom: 20px;

      .nav {
        width: 240px;
        height: 320px;
        background-color: #ffffff;
        box-sizing: border-box;
        padding: 10px 0;
        display: flex;
        flex-direction: column;
        .left-scroll {
          height: 100%;
          /deep/ .el-scrollbar__wrap {
            overflow-x: hidden;
          }
          span {
            display: inline-block;
            cursor: pointer;
            width: 100%;
            height: 40px;
            line-height: 40px;

            font-size: 14px;
            color: #333333;
            &:hover {
              background-color: #f5f7fa;
            }
            a {
              width: 100%;
              display: inline-block;
              box-sizing: border-box;
              padding-left: 40px;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }
          }
        }
      }
      .pics {
        /deep/.el-carousel__container {
          height: 320px !important;
        }
        width: calc(100% - 240px);
        height: 320px;
      }
    }

    .title {
      font-size: 20px;
      font-weight: 600;
      margin-bottom: 20px;
    }
  }
</style>
