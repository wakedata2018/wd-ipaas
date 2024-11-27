<template>
  <div v-loading="loading" class="box">
    <div class="top">
      <!-- <div class="page-title">今日数据</div> -->
      <div class="search">
        <!-- <span>时间：</span>
        <el-date-picker v-model="queryDate" type="date" placeholder="选择日期"></el-date-picker>
        <el-button class="search-btn" @click="onSearch">查询</el-button> -->
        <!-- <div class="show-frontend" @click="toFrantend">查看API发布前端</div> -->
      </div>
    </div>
    <div class="contant">
      <div class="contant-top">
        <el-row :gutter="15">
          <el-col :span="19">
            <div class="statistics">
              <!-- <div class="common visit">
                <div class="num">{{times.platformTotal}}</div>
                <div class="title">平台访问应用数</div>
              </div> -->
              <!-- <img src="../../assets/images/home/one.png" alt=""> -->

              <div class="common call">
                <div class="num">{{ times.apiTotal }}</div>
                <div class="title">今日接口调用次数</div>
              </div>
              <!-- <img src="../../assets/images/home/two.png" alt /> -->

              <div class="common open">
                <div class="num">{{ times.published }}</div>
                <div class="title">今日新增接口数</div>
              </div>
              <!-- <img src="../../assets/images/home/three.png" alt /> -->

              <div class="common replay">
                <div class="num">{{ times.apps }}</div>
                <div class="title">今日申请接口应用数</div>
              </div>
              <!-- <img src="../../assets/images/home/four.png" alt /> -->

              <div class="common err">
                <div class="num">{{ times.errors }}</div>
                <div class="title">今日接口调用异常数</div>
              </div>
              <!-- <img src="../../assets/images/home/five.png" alt /> -->
            </div>
            <div class="form">
              <api-num class="apiNum" :loading="loading" :group-app="groupByAccessApp" />
              <api-num-time class="apiTime" :loading="loading" :group-time="groupByElapsed" />
            </div>
          </el-col>
          <el-col :span="5">
            <div class="right">
              <div class="title">
                <div>近7天接口调用次数排名</div>
                <!-- <span>TOP10</span> -->
              </div>

              <div class="list">
                <emptyInfo v-show="(!groupByAccessMethod || groupByAccessMethod.length <= 0) && !loading">
                  <div v-text="'哎呀，暂时没有数据，稍候再来吧'"></div>
                </emptyInfo>
                <span v-show="!!groupByAccessMethod && groupByAccessMethod.length > 0">
                  <div
                    v-for="(item, index) in groupByAccessMethod"
                    :key="index"
                    class="item"
                    :title="`${item.primaryName}  ${item.secondaryName}`"
                  >
                    <span class="item-text">{{ item.primaryName }}&nbsp;&nbsp;{{ item.secondaryName }}</span>
                    <span style="float: right">{{ item.resultValue }}</span>
                  </div>
                </span>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
      <div class="contant-bottom">
        <api-analyze class="analyze" :loading="loading" :analyze="themeApiData" />
        <api-pie class="pie" :loading="loading" :err="groupByResultCode" />
      </div>
    </div>
  </div>
</template>

<script>
  import ApiNum from './api-num.vue';
  import ApiNumTime from './api-num-time.vue';
  import ApiAnalyze from './api-analyze.vue';
  import ApiPie from './api-pie.vue';
  import apiIndex from '@/api/api-index.js';
  import emptyInfo from '@/components/empty-info.vue';
  import { date } from 'dss-common';
  const defaultTimes = {
    platformTotal: 0,
    apiTotal: 0,
    published: 0,
    apps: 0,
    errors: 0,
  };
  export default {
    components: { ApiNum, ApiNumTime, ApiAnalyze, ApiPie, emptyInfo },
    data() {
      return {
        queryDate: null, // 时间
        list: [],
        times: window.$.extend(true, {}, defaultTimes), // 存放五张卡片数据
        groupByAccessApp: null, // api访问次数排名
        groupByAccessMethod: [], // top10
        themeApiData: null, // 不同接口分类api分析
        groupByElapsed: null, // 调用时长
        groupByResultCode: null, // 错误类型,
        query: {
          date: '',
        },
        loading: false,
      };
    },
    created() {
      this.getData();
    },
    methods: {
      getData() {
        this.loading = true;
        apiIndex
          .getData({
            ...this.query,
          })
          .done(res => {
            //  console.log(res)
            const data = res.data || {};
            this.times = window.$.extend(true, {}, defaultTimes, res.data);
            this.groupByAccessApp = res.data.groupByAccessApp;
            this.groupByAccessMethod = res.data.groupByAccessMethod;
            // console.log(this.groupByAccessMethod);
            this.themeApiData = {
              groupByApiGroup: data.groupByApiGroup || [],
              groupByApiGroupLastMonth: data.groupByApiGroupLastMonth || [],
            };
            this.groupByElapsed = res.data.groupByElapsed;
            this.groupByResultCode = res.data.groupByResultCode;
          })
          .always(() => {
            this.loading = false;
          });
      },
      onSearch() {
        this.query.date = '';
        if (this.queryDate) {
          this.query.date = date.format(this.queryDate, 'YYYY-MM-DD');
        }
        this.getData();
      },
      toFrantend() {
        window.location.href = `/open.html`;
      },
    },
  };
</script>

<style scoped lang="less">
  .box {
    padding: 10px 20px;
    box-sizing: border-box;
    .top {
      // display: flex;
      margin-bottom: 10px;
      // justify-content: space-between;
      position: relative;
      .page-title {
        font-size: 20px;
        color: #333;
        font-weight: 600;
      }
      .search-btn {
        padding: 7px 25px;
        background-color: #2776fb;
        color: #fff;
        font-size: 14px;
        box-sizing: border-box;
        margin-left: 10px;
      }
      /deep/.el-input__inner {
        max-width: 226px;
        height: 30px;
      }
      .show-frontend {
        padding: 5px 10px;
        border: 1px solid #2776fb;
        border-radius: 2px;
        font-size: 14px;
        color: #2776fb;
        cursor: pointer;
        position: absolute;
        right: 0;
      }
    }
    .statistics {
      display: inline-block;
      height: 100px;
      margin-bottom: 15px;
      width: 100%;
      font-size: 0px;
      white-space: nowrap;
      display: flex;
      flex-direction: row;
      justify-content: space-around;
      align-items: stretch;
      flex-wrap: nowrap;

      .common {
        background-color: white;
        background-position: center left;
        background-repeat: no-repeat;
        background-size: 200px;
        height: 100px;
        flex-basis: 25%;
        // display: inline-block;
        // width: calc(25% - 12px);

        // box-shadow: 0px 5px 9px 0px rgba(0, 0, 0, 0.1);
        border-radius: 8px;
        overflow: hidden;
        .title {
          font-size: 12px;
          font-weight: 400;
          padding: 5px 0;
          color: #333;
          text-align: center;
        }
        .num {
          text-align: center;
          padding-top: 22px;
          font-size: 30px;
          font-weight: 500;
          color: #333;
          font-family: DINPro-Medium, DINPro, Bahnschrift;
        }
        &.active {
          background: linear-gradient(to right, #a2d6ff, #46a4ef);
          .num {
            color: white;
          }
          .title {
            color: white;
          }
        }

        & + .common {
          margin-left: 15px;
        }
        // &.visit {
        //   background-image: url('../../assets/images/home/one.png');
        // }
        // &.call {
        //   background-image: url('../../assets/images/home/two.png');
        // }
        // &.open {
        //   background-image: url('../../assets/images/home/three.png');
        // }
        // &.replay {
        //   background-image: url('../../assets/images/home/four.png');
        // }
        // &.err {
        //   background-image: url('../../assets/images/home/five.png');
        // }

        text-align: center;
      }
      img {
        width: 90%;
        min-height: 100px;
        position: absolute;
        top: 0;
        z-index: 0;
      }
    }
    .form {
      display: flex;
      box-sizing: border-box;
      width: 100%;
      .apiNum {
        flex: 1;
      }
      .apiTime {
        flex: 1;
        margin-left: 15px;
      }
    }
    .right {
      // flex: 1;
      width: 100%;
      height: 460px;
      padding: 20px;
      background-color: #fff;
      box-sizing: border-box;
      // box-shadow: 0px 5px 9px 0px rgba(0, 0, 0, 0.1);
      border-radius: 8px;
      .title {
        font-size: 14px;
        width: 100%;
        color: #333;
        font-weight: 800;
        display: flex;
        justify-content: space-between;
        & > div {
          width: 80%;
          white-space: nowrap;
          // text-overflow: ellipsis;
          // overflow: hidden;
        }
        & > span {
          font-weight: 300;
          font-size: 12px;
          width: 35px;
          height: 15px;
          // padding: 2px 5px;
          color: white;
          background-color: #fa6b46;
          border-radius: 20px;
        }
      }
      .list {
        width: 100%;
        margin-top: 12px;
        height: calc(100% - 20px);
        box-sizing: border-box;
        position: relative;
        display: inline-block;
        overflow: hidden;
        .item {
          width: 100%;
          height: 28px;
          font-size: 12px;
          color: #999999;
          border-bottom: 1px solid #f3f6f8;
          // border-radius: 5px;
          box-sizing: border-box;
          padding: 3px 0px;
          margin-bottom: 7px;
          overflow: hidden;
          white-space: nowrap;
          text-overflow: ellipsis;
          &:nth-child(-n + 3) {
            color: #f68a42;
          }
          .item-text {
            display: inline-block;
            width: 80%;
            overflow: hidden;
            white-space: nowrap;
            text-overflow: ellipsis;
          }
        }
      }
    }
    .contant-bottom {
      margin-top: 15px;
      width: 100%;
      display: flex;
      .analyze {
        flex: 3;
      }
      .pie {
        margin-left: 15px;
        flex: 2;
      }
    }
  }
</style>
