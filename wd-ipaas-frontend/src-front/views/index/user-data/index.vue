<template>
  <div v-loading="loading" class="detail-page bd-page">
    <div class="bd-header">
      <el-breadcrumb class="title">
        <el-breadcrumb-item :to="{ path: router.path }">
          {{ router.name }}
        </el-breadcrumb-item>
        <el-breadcrumb-item>{{ groupName }}</el-breadcrumb-item>
        <el-breadcrumb-item>{{ apiDetail.apiName }}</el-breadcrumb-item>
      </el-breadcrumb>
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
          <div v-if="appId" class="content-r">
            <el-button
              v-if="collectedStatus"
              type="primary"
              class="select-button bd-button bd-strong"
              @click="handleCollect"
              >{{ collectedStatus }}</el-button
            >
            <el-button
              v-if="approvalStatus"
              type="primary"
              class="select-button"
              :disabled="
                apiDetailInfo && apiDetailInfo.length
                  ? !(apiDetailInfo[0].approval == 0 && apiDetailInfo[0].created == 0)
                  : false
              "
              @click="handleApply(apiDetailInfo)"
              >{{ approvalStatus }}</el-button
            >
          </div>
        </el-row>
      </div>
      <el-row>
        <el-form ref="elForm" label-width="100px" style="color: #333; margin-left: 20px; font-size: 12px">
          <!-- <el-form-item label="数据更新频率:">
            <span>{{ apiDetail.updateFrequency | updateFrequency() }}</span>
          </el-form-item> -->
          <el-form-item label="数据发布时间:">
            <span>{{ apiDetail.updateTime }}</span>
          </el-form-item>
          <!-- <el-form-item>
            <span slot="label">使用范例:</span>
            <span>{{ apiDetail.dataAssetApiMethod }}</span>
          </el-form-item> -->
        </el-form>
      </el-row>
      <div class="list">
        <CodeExample ref="dialog" :api-info="apiInfo" />
      </div>
    </div>
    <apply-card :api-info="appInfo" :visible.sync="dialog.app" :app-id="appId" @saved="onSaved" />
  </div>
</template>

<script>
  import CodeExample from './code-example.vue';
  import applyCard from '../../../bz-components/apply-card.vue';
  import index from '../../../api';
  import myApp from '../../../api/my-app';

  export default {
    components: { CodeExample, applyCard },
    filters: {
      updateFrequency(text) {
        if (text === 'HOUR') {
          return '小时';
        } else if (text === 'DAY') {
          return '日';
        } else if (text === 'WEEK') {
          return '周';
        } else if (text === 'MONTH') {
          return '月';
        } else if (text === 'YEAR') {
          return '年';
        }
      },
    },
    data() {
      return {
        loading: false,
        activeName: 'second',
        dataAssetApiId: null,
        collected: null,
        approval: null,
        apiGroupId: null,
        groupName: null,
        // approvalStatus: null, //存放申请状态
        // collectedStatus: null, //存放收藏状态
        created: null,
        apiDetail: {},
        apiInfo: {},
        appInfo: null,
        apiDetailInfo: null, // 存放api的申请收藏状态信息
        dialog: {
          app: false,
        },
        appList: [],
      };
    },
    computed: {
      approvalStatus() {
        if (this.apiDetailInfo && this.apiDetailInfo.length) {
          if (this.apiDetailInfo[0].approval === 0 && this.apiDetailInfo[0].created === 0) {
            return '申请';
          } else if (this.apiDetailInfo[0].approval === 1) {
            return '已申请';
          } else {
            return '待审批';
          }
        } else {
          return null;
        }
      },
      collectedStatus() {
        if (this.apiDetailInfo && this.apiDetailInfo.length) {
          if (this.apiDetailInfo[0].collected === 0) {
            return '收藏';
          } else {
            return '取消收藏';
          }
        } else {
          return null;
        }
      },
      router() {
        const path = this.$route.path;
        let currentPath = '';
        let backPath = '';
        if (path === '/index/userdata') {
          currentPath = '首页';
          backPath = '/';
        } else if (path === '/apply/detail') {
          currentPath = '我的申请';
          backPath = '/mydata/apply';
        } else if (path === '/collect/detail') {
          currentPath = '我的收藏';
          backPath = '/mydata/collect';
        } else if (path === '/statistics/detail') {
          currentPath = '我的API统计';
          backPath = '/mydata/statistics';
        }
        return {
          path: backPath,
          name: currentPath,
        };
      },
      appId() {
        return this.$route.query.appId;
      },
      dataAccessApp() {
        return this.appList.find(item => item.dataAccessAppId + '' === this.appId);
      },
    },
    async created() {
      this.appList = await myApp.getAppList();
      this.dataAssetApiId = +this.$route.query.dataAssetApiId;
      this.getApiDetail();
      this.getApiInfo();
    },
    methods: {
      onSaved() {
        this.getApiInfo();
      },
      getApiDetail() {
        this.loading = true;
        index
          .getApiDetail({
            dataAssetApiId: this.dataAssetApiId,
            appId: this.appId,
          })
          .done(res => {
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
        this.loading = true;
        index
          .getApiGroupName({ apiGroupId: this.apiGroupId })
          .done(res => {
            this.groupName = res.data.groupName;
          })
          .always(() => {
            this.loading = false;
          });
      },
      getApiInfo() {
        index.getCardList({ dataAssetId: this.dataAssetApiId }).done(res => {
          this.apiDetailInfo = res.data;
        });
      },
      handleCollect() {
        if (this.apiDetailInfo[0].collected === 0) {
          index
            .addCollect({
              dataAccessAppId: this.appId,
              dataAssetApiId: this.dataAssetApiId,
            })
            .done(() => {
              this.$message({
                type: 'success',
                message: '收藏成功',
              });
              this.getApiInfo();
            });
        } else {
          this.$confirm('确定取消该收藏吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
            // center: true
          })
            .then(() => {
              index.deleteCollect({ appId: this.appId, collectId: this.dataAssetApiId }).done(() => {
                this.$message({
                  type: 'success',
                  message: '取消收藏成功',
                });
                this.getApiInfo();
              });
            })
            .catch(res => {
              console.log('用户取消操作', res);
            });
        }
      },
      handleApply(val) {
        if (!this.dataAccessApp) {
          console.log('没有接入');
          this.$confirm('您还没有接入秘钥，请先在我的数据-我的接入中申请接入秘钥!', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
            // center: true
          })
            .then(res => {
              console.log('用户取消操作', res);
            })
            .catch(res => {
              console.log('用户取消操作', res);
            });
        } else {
          if (this.dataAccessApp.status === 'CREATED') {
            this.$confirm('您的接入还没有审批!', '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning',
              // center: true
            })
              .then(res => {
                console.log('用户取消操作', res);
              })
              .catch(res => {
                console.log('用户取消操作', res);
              });
          } else {
            this.dialog.app = true;
            this.appInfo = val[0];
          }
        }
      },
    },
  };
</script>

<style scoped lang="less">
  .detail-page {
    box-sizing: border-box;
    padding-top: 80px;
    .bd-container {
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
      /deep/.el-form-item {
        margin-bottom: 0px;
        .el-form-item__label {
          font-size: 12px;
        }
        .el-form-item__content {
          font-size: 12px;
        }
      }
      .list {
        padding: 20px;
        // background-color: #ccc;
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
  }
</style>
