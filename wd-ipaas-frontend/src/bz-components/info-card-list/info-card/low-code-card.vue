<template>
  <info-card :card-data="cardData" :card-style="cardStyle" :buttom-btn-position="buttomBtnPosition">
    <template slot="status">
      <span class="api-total">
        已授权API数：
        <el-button class="api-button" :title="$t('common.ShowApiDetail')" type="text">
          {{ cardData.apiNum || 0 }}
        </el-button>
      </span>
    </template>
    <template slot="top-btn">
      <span>
        <el-button icon="el-icon-edit" :title="$t('common.edit')" type="text" @click.stop="onEdit"></el-button>
        <el-button
          icon="el-icon-delete"
          type="text"
          :title="$t('common.delete')"
          :disabled="cardData.apiNum > 0"
          @click.stop="onDelete"
        ></el-button>
      </span>
    </template>
    <template slot="main">
      <div class="low_code_main" @click.stop="toAppLowCode(cardData)">
        <div class="low_title">
          <img :src="cardData.lowCodeLogo" class="logo" />
          <span>{{ cardData.dataAccessAppName }}</span>
        </div>
        <div class="content-info">
          {{ cardData.dataAccessDescription || '暂无描述' }}
        </div>
      </div>
    </template>
    <template slot="button-btn">
      <el-button class="bd-button bd-table-primary" @click.stop="onShowApiDetail"> 授权接口 </el-button>
    </template>
    <slot></slot>
  </info-card>
</template>

<script>
  import infoCard from './index.vue';
  import authType from '@/utils/auth-type';
  import { API_STATUS_NAME } from '@/utils/enum/third-app';

  export default {
    data() {
      return {
        API_STATUS_NAME,
        authTypeList: [],
      };
    },
    components: { infoCard },
    props: {
      cardData: {
        type: Object,
        default: () => ({}),
      },
      cardType: {
        type: String,
        default: 'datain',
      },
      cardStyle: {
        type: Object,
        default: () => null,
      },
      buttomBtnPosition: {
        type: String,
        default: 'left',
      },
    },
    created() {
      authType.getAuthTypes().done(res => {
        this.authTypeList = res || [];
      });
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
      onReset() {
        this.$emit('command', 'Reset', this.cardData);
      },
      onResetKey() {
        this.$emit('command', 'ResetKey', this.cardData);
      },
      onShowApiDetail() {
        this.$emit('command', 'ShowApiDetail', this.cardData);
      },
      showDetail() {
        this.$emit('command', 'ShowDetail', this.cardData);
      },
      toAppLowCode(row) {
        const { dataAccessAppId } = row || {};
        const origin = window.location.origin;
        window.top.location.href = `${origin}/wakeda/index.html?appId=${dataAccessAppId}`;
      },
    },
  };
</script>

<style lang="less" scoped>
  .api-total {
    font-size: 12px;
    color: #333333;
    .api-button {
      padding: 5px;
    }
  }
  .low_code_main {
    font-size: 12px;
    font-weight: 400;
    color: #bdbdbd;
    line-height: 17px;
    .logo {
      width: 60px;
      height: 60px;
      margin-right: 20px;
      vertical-align: middle;
    }
    .low_title {
      display: flex;
      justify-content: start;
      align-items: center;
      color: #333333;
      margin-bottom: 15px;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      -webkit-line-clamp: 1;
      overflow: hidden;
      vertical-align: middle;
      font-size: 14px;
    }

    .content-info {
      margin-top: 5px;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      -webkit-line-clamp: 3;
      overflow: hidden;
    }
  }
</style>
