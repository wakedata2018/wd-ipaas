<template>
  <el-row type="flex" class="new_data">
    <div class="content-l">
      <div class="top">
        <div class="info" @click="clickItem(item)">{{ item.apiName }}</div>
        <div class="content-r">
          <el-button
            type="primary"
            class="select-button bd-button bd-strong"
            :class="{ iscollected: item.collected == 1 }"
            @click.stop="handleCollect(item.dataAssetApiId, item.collected)"
            >{{ item.collected == 0 ? '收藏' : '取消收藏' }}</el-button
          >
          <el-button
            type="primary"
            class="select-button"
            @click.stop="handleApply(item.dataAssetApiId)"
            :disabled="!(item.approval == 0 && item.created == 0)"
            >{{ item.approvalStatus }}</el-button
          >
        </div>
      </div>

      <div class="desc" :title="item.apiDescription">描述：{{ item.apiDescription }}</div>
      <div class="type_date">
        <span>格式：{{ item.responseContentType }}</span>
        <!-- <span>更新频率：{{item.updateFrequency | updateFrequency()}}</span> -->
        <span>上架日期：{{ item.updateTime }}</span>
      </div>
    </div>
  </el-row>
</template>

<script>
  import index from '../../api/index.js';
  export default {
    props: {
      item: {
        type: Object,
        default: _ => ({}),
      },
    },
    data() {
      return {};
    },
    filters: {
      updateFrequency(text) {
        if (text == 'HOUR') {
          return '小时';
        } else if (text == 'DAY') {
          return '日';
        } else if (text == 'WEEK') {
          return '周';
        } else if (text == 'MONTH') {
          return '月';
        } else if (text == 'YEAR') {
          return '年';
        }
      },
    },
    computed: {},
    mounted() {
      //请求轮播图数据
      // this.$axios({
      //     url:"/xxx"
      // }).then(res => {
      //     const {data} = res.data
      //     this.picsData = data
      // })
    },
    created() {},
    methods: {
      handleCollect(dataAssetApiId, collected) {
        this.$emit('command', 'Collect', [dataAssetApiId, collected]);
      },
      handleApply(dataAssetApiId) {
        this.$emit('command', 'Apply', [dataAssetApiId]);
      },
      clickItem(item) {
        this.$emit('command', 'ClickItem', [item]);
      },
      getCardList() {
        index.getCardList().done(res => {
          res.data.forEach(item => {
            if (item.approval == 0 && item.created == 0) {
              item.approvalStatus = '申请';
            } else if (item.approval == 1) {
              item.approvalStatus = '已申请';
              // console.log(this.approvalStatus);
            } else {
              item.approvalStatus = '待审批';
            }
          });
          this.card = res.data;
          // console.log(this.card);
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
      clickItem(item) {
        this.$router.push(
          `/index/userdata?dataAssetApiId=${item.dataAssetApiId}&collected=${item.collected}&approval=${item.approval}&created=${item.created}`
        );
      },
    },
  };
</script>

<style scoped lang="less">
  .new_data {
    height: 140px;
    width: 100%;
    justify-content: space-between;
    box-sizing: border-box;
    padding: 20px 20px;
    background: rgba(255, 255, 255, 1);
    box-shadow: 0px 4px 16px 0px rgba(85, 88, 90, 0.1);
    margin-bottom: 20px;

    .content-l {
      display: inline-block;
      width: 100%;
      position: relative;
      .top {
        width: 100%;
        display: inline-block;
        margin-bottom: 10px;
        .content-r {
          display: inline-block;
          width: auto;
          position: absolute;
          right: 0px;
          .iscollected {
            background-color: #dddddd;
            color: #fff;
          }
          .select-button {
            padding: 8px 20px;
          }
        }
        .info {
          display: inline-block;
          cursor: pointer;
          width: auto;
          font-size: 16px;
          font-weight: 600;
          color: #2776fb;
        }
      }
      .desc {
        width: 100%;
        margin: 10px 0;
        font-size: 14px;
        text-overflow: -o-ellipsis-lastline;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
      }
      .type_date {
        position: absolute;
        bottom: 0px;
        span {
          font-size: 12px;
          color: #c7c7c7;
          margin-right: 40px;
        }
      }
    }
  }
</style>
