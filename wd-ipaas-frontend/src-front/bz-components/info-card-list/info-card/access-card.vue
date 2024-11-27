<template>
  <info-card
    :card-data="cardData"
    :card-style="cardStyle"
    :buttom-btn-position="buttomBtnPosition"
    @show-detail="showDetail"
  >
    <template slot="status">
      <span class="status-block" :style="`background-color:#2776fb`">{{ cardData.inCharge }}</span>
    </template>
    <template slot="top-btn">
      <span>
        <!-- <el-button
          icon="el-icon-refresh"
          :title="$t('重置')"
          type="text"
          @click.stop="resetKey"
        ></el-button> -->
        <el-button icon="el-icon-edit" :title="$t('编辑')" type="text" @click.stop="editCard"></el-button>
        <el-button icon="el-icon-delete" type="text" :title="$t('删除')" @click.stop="delCard"></el-button>
      </span>
    </template>
    <template slot="main">
      <div class="title" :title="cardData.name">
        <el-button class="name" type="text">{{ cardData.dataAccessAppName }}</el-button>
        <!-- <rolling-title
            :text-str="`${cardData.name}`"
            :customizedStyle="{width:'100%', fontSize: '14px', color: 'rgba(51, 51, 51, 1)'}"
        ></rolling-title>-->
      </div>
      <div
        class="desc"
        style="margin-bottom: 5px"
        :title="`${!!cardData.dataAccessDescription ? cardData.dataAccessDescription : ''}`"
      >
        {{ cardData.dataAccessDescription }}
      </div>
      <div class="info" :title="`${!!cardData.dataAccessKey ? cardData.dataAccessKey : ''}`">
        {{ $t('接入KEY') }}: {{ !!cardData.dataAccessKey ? cardData.dataAccessKey : '' }}
      </div>

      <div class="info" :title="`${!!cardData.dataAccessSecret ? cardData.dataAccessSecret : ''}`">
        {{ $t('接入密钥') }}: {{ !!cardData.dataAccessSecret ? cardData.dataAccessSecret : '' }}
      </div>
    </template>
    <template slot="button-btn"> </template>
    <slot></slot>
  </info-card>
</template>
<i18n>
{
  "en": {
    "同步源": "DataSource",
    "写入Topic": "Sink Topic",
    "状态": "Status",
    "同步表": "Source Table"
  }
}
</i18n>
<script>
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
        default: _ => ({}),
      },

      buttomBtnPosition: {
        type: String,
        default: 'left',
      },
    },
    data() {
      return {
        // imgs
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
      delCard: function () {
        this.$emit('command', 'Delete', this.cardData);
      },
      editCard() {
        this.$emit('command', 'Edit', this.cardData);
      },
      resetKey() {
        this.$emit('command', 'Reset', this.cardData);
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
<style lang="less" scoped></style>
