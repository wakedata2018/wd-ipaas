<template>
  <div v-loading="loading" class="bd-page">
    <div class="bd-header">
      <div class="header-content">
        <el-breadcrumb separator-class="el-icon-arrow-right">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item :to="{ path: `/index/company?groupId=${apiGroupId}&groupName=${groupName}` }">{{
            groupName
          }}</el-breadcrumb-item>
          <el-breadcrumb-item>{{ apiDetail.apiName }}</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
    </div>
    <div class="bd-container">
      <div class="card">
        <el-row type="flex" class="new_data">
          <div class="content-l">
            <div class="info">{{ apiDetail.apiName }}</div>
            <div class="describe">
              <span style="font-weight: 600">数据描述</span>
              <div class="des-content">{{ apiDetail.apiDescription }}</div>
            </div>
          </div>
          <div class="content-r">
            <el-button type="primary" class="select-button bd-button bd-strong" @click="handleCollect">{{
              collected == 0 ? '收藏' : '取消收藏'
            }}</el-button>
            <el-button
              type="primary"
              class="select-button"
              :disabled="!(approval == 0 && created == 0)"
              @click="handleApply"
              >{{ approvalStatus }}</el-button
            >
          </div>
        </el-row>
      </div>
      <div class="list">
        <el-tabs v-model="activeName" class="custom-tabs" @tab-click="handleClick">
          <el-tab-pane label="数据预览" name="first">
            <DataPreview />
          </el-tab-pane>
          <el-tab-pane label="API文档" name="second">
            <CodeExample ref="dialog" :api-info="apiInfo" />
          </el-tab-pane>
          <el-tab-pane label="调用及返回说明" name="third">
            <emptyInfo>
              <div v-text="'哎呀，暂时没有数据'"></div>
            </emptyInfo>
          </el-tab-pane>
          <el-tab-pane label="代码样例" name="fourth">
            <emptyInfo>
              <div v-text="'哎呀，暂时没有数据'"></div>
            </emptyInfo>
          </el-tab-pane>
          <el-tab-pane label="常见问题" name="fivth">
            <Err />
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script>
  import DataPreview from './data-preview.vue';
  import Err from './err.vue';
  import CodeExample from './code-example.vue';
  import emptyInfo from '@/components/empty-info.vue';
  import index from '../../../api';
  export default {
    components: { DataPreview, Err, CodeExample, emptyInfo },
    data() {
      return {
        loading: false,
        activeName: 'first',
        dataAssetApiId: null,
        collected: null,
        approval: null,
        apiGroupId: null,
        groupName: null,
        approvalStatus: null,
        created: null,
        apiDetail: {},
        apiInfo: {},
      };
    },
    computed: {
      // collected() {
      //   if (this.approval == 0 && this.created == 0) {
      //     this.approvalStatus = '申请';
      //   } else if (this.approval == 1) {
      //     this.approvalStatus = '已申请';
      //     console.log(this.approvalStatus);
      //   } else {
      //     this.approvalStatus = '未审批';
      //   }
      // }
      // approval(){
      //   console.log('申请改变了')
      // }
    },
    created() {
      this.dataAssetApiId = +this.$route.query.dataAssetApiId;
      this.collected = +this.$route.query.collected;
      this.approval = +this.$route.query.approval;
      this.created = +this.$route.query.created;
      if (this.approval == 0 && this.created == 0) {
        this.approvalStatus = '申请';
      } else if (this.approval == 1) {
        this.approvalStatus = '已申请';
        // console.log(this.approvalStatus);
      } else {
        this.approvalStatus = '待审批';
      }
      this.getApiDetail();
    },
    methods: {
      getApiDetail() {
        this.loading = true;
        index
          .getApiDetail({
            dataAssetApiId: this.dataAssetApiId,
          })
          .done(res => {
            // console.log(res);
            this.apiDetail = res.data.dataAssetApi;
            this.apiGroupId = res.data.dataAssetApi.apiGroupId;
            this.getGroupName();
            this.apiInfo = res.data.dataAssetApi;
          })
          .always(() => {
            this.loading = false;
          });
      },
      getGroupName() {
        index.getApiGroupName({ apiGroupId: this.apiGroupId }).done(res => {
          this.groupName = res.data.groupName;
        });
      },
      handleCollect() {
        if (this.collected == 0) {
          index
            .addCollect({
              dataAssetApiId: this.dataAssetApiId,
            })
            .done(res => {
              this.$message({
                type: 'success',
                message: '收藏成功',
              });
              this.collected = 1;
              // this.getCardList();
              // 避免刷新没效果
              this.$router.push(
                `/index/userdata?dataAssetApiId=${this.dataAssetApiId}&collected=${this.collected}&approval=${this.approval}&created=${this.created}`
              );
            });
        } else {
          this.$confirm('确定取消该收藏吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
            // center: true
          })
            .then(() => {
              index.deleteCollect({ collectId: this.dataAssetApiId }).done(res => {
                this.$message({
                  type: 'success',
                  message: '取消收藏成功',
                });
                this.collected = 0;
                // this.getCardList();
                this.$router.push(
                  `/index/userdata?dataAssetApiId=${this.dataAssetApiId}&collected=${this.collected}&approval=${this.approval}&created=${this.created}`
                );
              });
            })
            .catch(res => {
              console.log('用户取消操作', res);
            });
        }
      },
      handleApply() {
        index.apply([this.dataAssetApiId]).done(res => {
          this.$message({
            type: 'success',
            message: '申请成功',
          });
          this.created = 1;
          this.approvalStatus = '待审批';
          // 避免刷新没效果
          this.$router.push(
            `/index/userdata?dataAssetApiId=${this.dataAssetApiId}&collected=${this.collected}&approval=${this.approval}&created=${this.created}`
          );
        });
      },
      handleClick(tab, event) {
        //   console.log(tab, event);
      },
    },
  };
</script>

<style scoped lang="less">
  .bd-header {
    height: 60px;
    background-color: #fff;
    display: flex;
    align-items: center;
    position: fixed;
    top: 52px;
    .header-content {
      width: calc(100% - 80px);
      max-width: 1200px;
      margin: 80px auto;
      background-color: #fff;
    }
  }
  .bd-container {
    width: calc(100% - 80px);
    max-width: 1200px;
    margin: 80px auto;
    background-color: #fff;
    position: relative;

    .card {
      padding: 20px;
      .new_data {
        height: 140px;
        background: #f3f6f8;
        box-sizing: border-box;
        padding: 25px 42px 20px 42px;
        margin-bottom: 20px;

        .content-l {
          .info {
            font-size: 18px;
            font-weight: 600;
            color: #49a9f6;
          }

          .describe {
            width: 100%;
            box-sizing: border-box;
            color: #333333;
            font-size: 14px;
            margin-top: 20px;

            .des-content {
              color: #333333;
              margin-top: 10px;
              font-size: 14px;
              overflow: hidden;
              text-overflow: ellipsis;
              display: -webkit-box;
              -webkit-line-clamp: 2; /*指定文字显示的行数*/
              -webkit-box-orient: vertical;
            }
          }
        }
        .content-r {
          position: absolute;
          right: 30;
          top: 20px;
          right: 42px;
          .btnOne {
            width: 80px;
            background-color: #2776fb;
          }
          .btnTwo {
            width: 80px;
            background-color: #2776fb;
            margin-left: 25px;
          }
        }
      }
    }
  }

  .custom-tabs {
    /deep/ .el-tabs__nav-scroll {
      padding-left: 60px;
      padding-right: 60px;
    }
    /deep/ .el-tabs__nav-wrap::after {
      background: transparent;
    }
  }
</style>
