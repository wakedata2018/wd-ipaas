<template>
  <info-card
    :card-data="cardData"
    :card-style="cardStyle"
    :buttom-btn-position="buttomBtnPosition"
    @show-detail="showDetail"
  >
    <!-- <template slot="status">
      <span class="status-block" :style="`backgroundColor:#2776fb`">{{cardData.dbType}}</span>
    </template> -->
    <template slot="top-btn">
      <span>
        <el-button icon="el-icon-edit" :title="$t('common.edit')" type="text" @click.stop="onEdit"></el-button>
        <el-button icon="el-icon-delete" type="text" :title="$t('common.delete')" @click.stop="onDelete"></el-button>
      </span>
    </template>
    <template slot="main">
      <div class="card-img-div" :title="!!cardData.dbType ? cardData.dbType.toUpperCase() : ''">
        <div
          class="card-img"
          :style="{ background: `url(${!!cardData.dbType ? imgs[cardData.dbType.toLowerCase()] : ''})` }"
        ></div>
      </div>
      <div class="card-text">
        <div class="title" :title="cardData.connectionName">{{ cardData.connectionName }}</div>
        <div class="info" :title="`${!!cardData.description ? cardData.description : ''}`">
          {{ cardData.description }}
        </div>
        <!-- <div class="info">{{referenceCount}}{{$t('个引用')}}</div> -->
      </div>
    </template>
    <slot></slot>
  </info-card>
</template>

<script>
  import imgs from '@/utils/datasource-images.js';
  import infoCard from './index.vue';
  import { mapState } from 'vuex';
  export default {
    components: { infoCard },
    props: {
      cardData: {
        type: Object,
        default: _ => ({}),
      },

      cardType: {
        type: String,
        default: 'datain',
      },

      cardStyle: {
        type: Object,
        default: _ => {},
      },

      buttomBtnPosition: {
        type: String,
        default: 'left',
      },
    },
    data() {
      return {
        imgs,
      };
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
      onDelete: function () {
        this.$emit('command', 'Delete', this.cardData);
      },
      onEdit() {
        this.$emit('command', 'Edit', this.cardData);
      },

      errorImage: function (event) {
        const currentTarget = event.currentTarget;
        $(currentTarget).attr({ src: cardImg, onerror: null });
      },
      showDetail() {
        this.$emit('command', 'ShowDetail', this.cardData);
      },
    },
  };
</script>
<style lang="less" scoped>
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
  .card-text {
    height: auto;
    padding: 10px 14px;
    left: 0;
    bottom: 5px;
    width: 100%;
    box-sizing: border-box;
    position: absolute;
    display: inline-block;
    text-align: center;
  }
</style>
