<template>
  <el-row type="flex" class="api-item">
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
            v-if="item.apiType !== 3"
            type="primary"
            class="select-button"
            :disabled="!(item.approval == 0 && item.created == 0)"
            @click.stop="handleApply(item)"
            >{{ item.approvalStatus }}</el-button
          >
        </div>
      </div>

      <div class="desc" :title="item.apiDescription || '-'">描述：{{ item.apiDescription || '-' }}</div>
      <div class="type_date">
        <span>格式：{{ item.responseContentType }}</span>
        <!-- <span>更新频率：{{item.updateFrequency | updateFrequency()}}</span> -->
        <span>上架日期：{{ item.updateTime }}</span>
      </div>
    </div>
  </el-row>
</template>

<script>
  import index from '../../api';

  export default {
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
    props: {
      item: {
        type: Object,
        default: _ => ({}),
      },
      appId: {
        type: [Number, String],
        default: '',
      },
    },
    data() {
      return {};
    },
    methods: {
      handleCollect(dataAssetApiId, collected) {
        this.$emit('command', 'Collect', [dataAssetApiId, collected]);
      },
      handleApply(dataAssetApiId) {
        this.$emit('command', 'Apply', [dataAssetApiId]);
      },
      // clickItem(item) {
      //   this.$emit('command', 'ClickItem', [item]);
      // },
      getCardList() {
        index.getCardList().done(res => {
          res.data.forEach(item => {
            if (item.approval === 0 && item.created === 0) {
              item.approvalStatus = '申请';
            } else if (item.approval === 1) {
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
          for (const key in res.data) {
            const newKey = key.split('____');
            arr.push(newKey);
            arr1.push(res.data[key]);
          }
          this.apiGroupList = arr;
        });
      },
      clickItem(item) {
        console.log(item);
        this.$router.push(`/index/userdata?dataAssetApiId=${item.dataAssetApiId}&appId=${this.appId}`);
      },
    },
  };
</script>

<style scoped lang="less">
  .api-item {
    height: 140px;
    width: 100%;
    justify-content: space-between;
    box-sizing: border-box;
    padding: 20px 20px;
    background: rgba(255, 255, 255, 1);
    box-shadow: 0px 4px 16px 0px rgba(234, 238, 241, 1);
    margin-top: 20px;

    .content-l {
      display: inline-block;
      width: 100%;
      position: relative;
      // line-height: 0px !important;
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
            background-color: #5291fc;
            color: #fff;
          }
          .select-button {
            padding: 8px 20px;
          }
        }
        .info {
          display: inline-block;
          cursor: pointer;
          // width: auto;
          font-size: 16px;
          height: 30px;
          width: calc(100% - 190px);
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
          font-weight: 600;
          color: #2776fb;
        }
      }
      .desc {
        width: 100%;
        margin: 5px 0;
        line-height: 20px;
        color: #333;
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
        bottom: -10px;
        span {
          font-size: 12px;
          color: #c7c7c7;
          margin-right: 40px;
        }
      }
    }
  }
</style>
