<template>
  <div ref="cardList" class="card-list" v-loading="loading">
    <add-btn
      :card-style="{
        width: cardWidth + 'px',
        height: cardHeight + 'px',
        margin: minCardMargin + 'px',
        ...cardStyle,
      }"
      v-if="showOperation"
      @click-add="onCommand('Add')"
    />

    <component
      v-for="(cardData, index) of cardList"
      :is="componentObj"
      :key="index"
      :index="index"
      :card-type="cardType"
      :card-data="cardData"
      :buttom-btn-position="buttomBtnPosition"
      :show-operation="showOperation"
      :is-selector="isSelector"
      :is-my="isMy"
      @click.native="onSelect(cardData)"
      :card-style="{
        width: cardWidth + 'px',
        height: cardHeight + 'px',
        margin: minCardMargin + 'px',
        ...cardStyle,
      }"
      @command="onCommand"
    >
      <div class="mask" v-if="!!isSelector && cardData.id === selectedId">
        <img class="checked" src="../../assets/images/task/check.png" />
      </div>
    </component>
  </div>
</template>

<script>
  import infoCard from './info-card';
  import datasourceCard from './info-card/datasource-card.vue';
  import joinCard from './info-card/join-card.vue';
  import lowCodeCard from './info-card/low-code-card.vue';
  import addBtn from './info-card/add-btn';
  export default {
    name: 'CardList',
    components: {
      infoCard,
      addBtn,
      datasourceCard,
      joinCard,
      lowCodeCard,
    },
    data() {
      return {
        loading: false,
        page: 1,
        pageSize: 10,
        cardStyle: {},
        componentList: {
          infoCard,
          datasourceCard,
          joinCard,
          lowCodeCard,
        },
        query: {
          keyword: '',
        },
      };
    },
    props: {
      cardType: {
        type: String,
        default: 'datasource',
      },
      buttomBtnPosition: {
        type: String,
        default: 'left',
      },
      cardList: {
        type: Array,
        default: () => [],
      },
      searchKey: {
        type: String,
        default: '',
      },
      dataSourceTypeOptionsMap: {
        type: Object,
        default: () => ({}),
      },
      cardWidth: {
        type: Number,
        default: 240,
      },
      cardHeight: {
        type: Number,
        default: 200,
      },
      minCardMargin: {
        type: Number,
        default: 10,
      },
      showOperation: {
        type: Boolean,
        default: true,
      },
      isSelector: {
        type: Boolean,
        default: false,
      },
      selectedId: {
        type: Number,
        default: () => null,
      },
      isMy: {
        type: Boolean,
        default: false,
      },
    },
    watch: {
      cardList: {
        immediate: true,
        handler() {
          this.$nextTick(() => {
            this.changeCardMargin();
          });
        },
      },
    },
    computed: {
      componentObj() {
        switch (this.cardType) {
          case 'datasource':
            return this.componentList.datasourceCard;
          case 'join':
            return this.componentList.joinCard;
          case 'lowcode':
            return this.componentList.lowCodeCard;
          default:
            return this.componentList.infoCard;
        }
      },
    },
    mounted() {
      this.changeCardMargin();
      window
        .$(window)
        .off('resize')
        .on('resize', () => {
          // 当浏览器大小变化时
          this.changeCardMargin();
        });
    },
    beforeDestroy() {
      window.$(window).off('resize');
    },
    methods: {
      // 改变卡片间距以自适应
      changeCardMargin() {
        const cardWidth = this.cardWidth + this.minCardMargin * 2;
        const cardListWidth = window.$(this.$refs.cardList).outerWidth();
        const cardLineCount = parseInt(cardListWidth / cardWidth);
        const cardAllMargin = (cardListWidth % cardWidth) / 2;
        const isOverLine = cardLineCount < this.cardList.length + 1;
        this.cardStyle = {
          marginLeft: (isOverLine ? cardAllMargin / cardLineCount : 0) + this.minCardMargin + 'px',
          marginRight: (isOverLine ? cardAllMargin / cardLineCount : 0) + this.minCardMargin + 'px',
        };
      },

      onCommand(operation, cardInfo) {
        this.$emit('command', operation, cardInfo);
      },
      onSelect(cardInfo) {
        if (this.isSelector) this.$emit('select', cardInfo);
      },
    },
  };
</script>
<style scoped lang="less">
  .card-list {
    display: inline-block;
    margin: 20px;
    font-size: 0px;
    &.selector {
      cursor: pointer;
    }
    .mask {
      position: absolute;
      top: 0;
      left: 0;
      bottom: 0;
      right: 0;
      background: rgba(33, 150, 243, 0.69);

      .checked {
        position: absolute;
        width: 32px;
        height: 32px;
        right: 10px;
        bottom: 10px;
      }
    }
  }
</style>
