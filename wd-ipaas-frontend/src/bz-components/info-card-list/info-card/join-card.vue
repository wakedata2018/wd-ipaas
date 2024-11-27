<template>
  <info-card
    :card-data="cardData"
    :card-style="cardStyle"
    :buttom-btn-position="buttomBtnPosition"
    @click.native.stop="onShowApiDetail"
  >
    <template slot="status">
      <span v-if="isPass" class="api-total">
        已授权API数：
        <el-button class="api-button" :title="$t('common.ShowApiDetail')" type="text">
          {{ cardData.apiNum || 0 }}
        </el-button>
      </span>
    </template>
    <template slot="top-btn">
      <span>
        <el-button
          v-if="isPass"
          icon="el-icon-edit"
          :title="$t('common.edit')"
          type="text"
          @click.stop="onEdit"
        ></el-button>
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
      <div class="title" :title="cardData.dataAccessAppName">
        <span>{{ cardData.dataAccessAppName || cardData.appName }}</span>
      </div>
      <div class="content-info">
        <div v-if="!isMy" class="info" :title="cardData.inCharge">
          <div class="info-label">创建人：</div>
          {{ cardData.inCharge || cardData.createUser }}
        </div>
        <div v-else class="info" :title="cardData.dataAccessKey">
          <div class="info-label">APP ID：</div>
          {{ cardData.dataAccessKey }}
        </div>
        <div v-if="!cardData.appType" class="info" :title="getAuthTypeLabel(cardData.authType)">
          <div class="info-label">鉴权方式：</div>
          {{ getAuthTypeLabel(cardData.authType) }}
        </div>
        <div v-if="cardData.appType" class="info" :title="cardData.appType">
          <div class="info-label">应用类型：</div>
          {{
            cardData.appType === appTypeEnum.THIRD_AUTH_APP.value ? appTypeEnum.THIRD_AUTH_APP.label : cardData.appType
          }}
        </div>
        <div class="info" :title="cardData.createTime">
          <div class="info-label">创建时间：</div>
          {{ cardData.createTime }}
        </div>
        <div class="info" :title="cardData.dataAccessDescription">
          <div class="info-label">描述：</div>
          {{ cardData.dataAccessDescription || cardData.description }}
        </div>
      </div>
    </template>
    <template v-if="!cardData.appType" slot="button-btn">
      <div :style="{ color: getStatusColor(cardData.status) }" class="auth-status">
        {{ getStatusDesc(cardData.status) }}
      </div>
      <el-button style="visibility: hidden"></el-button>
      <template v-if="isPass">
        <el-button class="bd-button bd-table-primary" @click.stop="onReset"> 重置秘钥 </el-button>
        <el-button class="bd-button bd-table-primary" @click.stop="onShowWhiteIP"> 设置IP </el-button>
      </template>
    </template>
    <slot></slot>
  </info-card>
</template>

<script>
  import infoCard from './index.vue';
  import authType from '@/utils/auth-type';
  import appTypeEnum from '@/utils/enum/app-type';

  export default {
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
      isMy: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        appTypeEnum,
        authTypeList: [],
      };
    },
    computed: {
      cardImage() {
        return null;
      },
      isPass() {
        return this.cardData && this.cardData.status === 'PASS';
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
      onShowWhiteIP() {
        this.$emit('command', 'ShowWhiteIP', this.cardData);
      },
      onResetKey() {
        this.$emit('command', 'ResetKey', this.cardData);
      },
      onShowApiDetail() {
        if (this.cardData.apiNum || this.cardData.appType) {
          this.$emit('command', 'ShowApiDetail', this.cardData);
        }
        this.$emit('command', 'CardClick', this.cardData);
      },
      // errorImage: function (event) {
      //   let currentTarget = event.currentTarget;
      //   window.$(currentTarget).attr({ src: cardImg, onerror: null });
      // },
      showDetail() {
        this.$emit('command', 'ShowDetail', this.cardData);
      },
      getAuthTypeLabel(val) {
        const typeItem = this.authTypeList.find(item => item.value === val);
        if (typeItem) {
          return typeItem.label;
        }
        return val;
      },
      getStatusColor(status) {
        switch (status) {
          case 'CREATED':
            return '#00c6f7';
          case 'PASS':
            return '#33e1cb';
          case 'REFUSE':
            return '#fb4938';
          default:
            return '#ffc43d';
        }
      },
      getStatusDesc(status) {
        switch (status) {
          case 'CREATED':
            return '待审批';
          case 'PASS':
            return '通过';
          case 'REFUSE':
            return '未通过';
          default:
            return '';
        }
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
  .auth-status {
    position: absolute;
    top: 4px;
    font-size: 12px;
    left: 10px;
  }
</style>
