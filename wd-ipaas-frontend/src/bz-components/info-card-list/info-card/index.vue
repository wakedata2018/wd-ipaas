<template>
  <div class="card-item bd-hover" :style="cardStyle" v-if="display">
    <div class="card-image-container">
      <span class="status-span">
        <slot name="status"></slot>
      </span>
      <div class="card-btn-div">
        <slot name="top-btn" v-if="!!showOperation"></slot>
      </div>
      <div class="card-content" @click="showDetail">
        <slot name="main"></slot>
      </div>
      <div :class="`button-group ${buttomBtnPosition}`">
        <slot name="button-btn" v-if="!!showOperation"></slot>
      </div>
    </div>
    <slot></slot>
  </div>
</template>

<script>
  import { RollingTitle } from 'dss-common/lib/components';

  import { mapState } from 'vuex';
  export default {
    name: 'CardItem',
    components: { RollingTitle },
    data() {
      return {};
    },
    props: {
      cardData: {
        type: Object,
        default: _ => ({}),
      },
      index: {
        type: Number,
        default: 1,
      },
      display: {
        type: Boolean,
        default: true,
      },
      cardStyle: {
        type: Object,
        default: _ => {},
      },
      buttomBtnPosition: {
        type: String,
        default: 'left',
      },
      showOperation: {
        type: Boolean,
        default: true,
      },

      ////选择器属性
      isSelector: {
        type: Boolean,
        default: false,
      },
    },
    computed: {
      cardImage() {
        return null;
      },
    },
    methods: {
      isIn(list, type) {
        return list.findIndex(item => item === type) > -1;
      },

      errorImage: function (event) {
        let currentTarget = event.currentTarget;
        $(currentTarget).attr({ src: cardImg, onerror: null });
      },

      showDetail() {
        this.$emit('show-detail');
      },
    },
  };
</script>
<style lang="less" scoped>
  .card-item {
    // padding: 10px;
    width: 240px;
    height: 180px;
    display: inline-block;
    position: relative;
    box-shadow: 0px 4px 16px 0px rgba(234, 238, 241, 1);
    margin: 10px;
    box-sizing: border-box;
    transition: border 0.3s;
    vertical-align: top;
    overflow: hidden;
    .card-image-container {
      width: 100%;
      height: 100%;
      overflow: hidden;
      position: relative;
      text-align: center;
      .card-image-btn-container {
        width: 100%;
        height: 100%;
        position: absolute;
        top: 0px;
        left: 0px;
        background-color: rgba(174, 210, 238, 0.24);
        border: 1px solid #2776fb;
        box-sizing: border-box;
        display: flex;
        flex-direction: column;
        justify-content: center;
        opacity: 0;
        text-align: center;
        transition: all 0.5s;
        div {
          padding: 5px;
        }
      }
      .card-btn-div {
        text-align: right;
        font-size: 0px;
        top: 10px;
        right: 10px;
        position: absolute;
        z-index: 10;
        /deep/ .el-button {
          padding: 5px;
          color: rgb(40, 46, 58);
          font-size: 13px;
          &.is-disabled {
            color: rgba(40, 46, 58, 0.3);
          }
          & + .el-button {
            margin-left: 0px;
          }
        }
      }
      .status-span {
        position: absolute;
        top: 10px;
        left: 10px;

        height: 22px;

        /deep/ .status-block {
          font-size: 12px;
          min-width: 44px;
          padding: 3px 8px;
          line-height: 22px;
          color: white;
          text-align: center;
          // & + .status-block {
          //   margin-left: 5px;
          // }
        }
      }
      .card-img-div {
        display: flex;
        flex-direction: row;
        justify-content: center;
        align-content: center;
        .card-img {
          margin: 10px;
          // padding: 10px;
          border-radius: 2px;
          width: 48px;
          height: 48px;
          border: 1px solid rgba(245, 245, 245, 1);
          background-size: auto 100% !important;
          background-position-x: 50% !important;
        }
      }
      .button-group {
        position: absolute;
        right: 10px;
        text-align: right;
        bottom: 5px;
        &.left {
          left: 5px;
        }
        &.right {
          right: 5px;
        }
        z-index: 10;
        // right: 8px;
        // float: right;
        // text-align: right;

        /deep/ .el-button {
          margin: 2px;
        }

        .bd-button {
          padding: 0px 5px;
          height: 22px;
          line-height: 22px;
        }
      }
    }
    /* .card-image-container .card-image-btn-image:hover{
  opacity: 1;
} */
    .card-content {
      margin-top: 25px;
      height: calc(100% - 25px);
      padding: 10px 15px;
      text-align: left;
      width: 100%;
      box-sizing: border-box;
      position: relative;
      display: inline-block;
      display: flex;
      flex-direction: column;
      // justify-content: center;
      cursor: pointer;
      /deep/ .title {
        display: inline-block !important;
        // white-space: nowrap ;
        // text-overflow: ellipsis;
        margin-top: 10px;
        overflow: hidden;
        width: 100%;
        font-size: 14px;
        font-weight: 400;
        color: rgba(51, 51, 51, 1);
        line-height: 20px;
        white-space: nowrap;
        font-weight: 600;
        text-overflow: ellipsis;
        text-align: center;
        .name {
          margin-bottom: 6px;
          overflow: hidden;
          height: 18px;
          white-space: nowrap;
          text-overflow: ellipsis;
          max-width: 100%;
          text-align: left;
          font-weight: bold;
          padding: 0;
          font-size: 14px;

          color: #2e8dff;
        }
      }
      /deep/ .desc {
        height: 20px;
        white-space: nowrap;
        text-overflow: ellipsis;
        overflow: hidden;
        font-size: 12px;
        font-weight: 400;
        color: black;
        line-height: 17px;
      }
      /deep/ .info {
        height: 20px;
        white-space: nowrap;
        text-overflow: ellipsis;
        overflow: hidden;
        font-size: 12px;
        font-weight: 400;
        color: rgba(189, 189, 189, 1);
        line-height: 17px;
        margin-top: 5px;
        .info-label {
          display: inline-block;
          min-width: 60px;
          text-align: right;
        }
      }
    }

    .screen-status-container {
      span {
        vertical-align: middle;
        color: #8cacc3;
        &:nth-child(1) {
          border-radius: 100%;
          width: 8px;
          height: 8px;
          background-color: #8cacc3;
          margin-right: 3px;
        }
      }
      &.published-card {
        span {
          color: #2776fb;
        }
      }
      .screen-status-container {
        &.published-card {
          span {
            &:nth-child(1) {
              background-color: #2776fb;
            }
          }
        }
      }
    }

    .view {
      cursor: pointer;
    }
  }
</style>
