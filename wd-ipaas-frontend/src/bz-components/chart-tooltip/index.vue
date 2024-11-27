<template>
  <div ref="chartTooltip" class="chart-tooltip" v-if="visible" v-html="tooltipHtml"></div>
</template>
<i18n>
{
  "en": {
    
  }
}
</i18n>
<script>
export default {
  components: {},
  mixins: [],
  props: {
    tooltipHtml: {
      type: String,
      default: ''
    },
    visible: {
      type: Boolean,
      default: false
    },
    dark: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {};
  },
  computed: {},
  watch: {
    visible(val) {
      if (!val) {
        this.unbindEvent();
      } else {
        this.bindEvent();
      }
    }
  },
  created() {},
  mounted() {
  },
  beforeDestroy() {
    this.unbindEvent();
  },
  methods: {
    bindEvent() {
      // $(this.$parent)[0].$el.addEventListener('mouseenter', this.onMouseenter);
      $(this.$parent)[0].$el.addEventListener('mousemove', this.onMousemove);
      $(this.$parent)[0].$el.addEventListener('mouseleave', this.onMouseleave);
    },
    unbindEvent() {
      // $(this.$parent)[0].$el.removeEventListener('mouseenter', this.onMouseenter);
      $(this.$parent)[0].$el.removeEventListener('mousemove', this.onMousemove);
      $(this.$parent)[0].$el.removeEventListener('mouseleave', this.onMouseleave);
    },
    onMousemove(event) {
      if (!!this.$refs.chartTooltip) {
        let top = event.clientY;
        let left = event.clientX;
        let offset = 15;
        let pageWidth = window.innerWidth,
          pageHeight = window.innerHeight;
        let tooltipHeight = $(this.$refs.chartTooltip).outerHeight();
        let tooltipWidth = $(this.$refs.chartTooltip).outerWidth();
        let finalTop = top + offset;
        let finalBottom = finalTop + tooltipHeight;
        if (finalBottom > pageHeight) {
          finalTop = top - tooltipHeight - offset;
        }
        let finalLeft = left + offset;
        let finalright = finalLeft + tooltipWidth;
        if (finalright >= pageWidth) {
          finalLeft = left - tooltipWidth - offset;
        }
        $(this.$refs.chartTooltip).css({ top: finalTop, left: finalLeft, display: 'inline-block' });
      } else {
        $(this.$refs.chartTooltip).css({ top: 0, left: 0, display: 'none' });
      }
    },
    // onMouseenter() {
    //   $(this.$refs.chartTooltip).css({ display: 'inline-block' });
    // },
    onMouseleave() {
      $(this.$refs.chartTooltip).css({ display: 'none' });
    }
  }
};
</script>

<style lang="less" scoped>
@import '../../css/var.less';
.chart-tooltip {
  position: fixed;
  background-color: rgba(255, 255, 255, 0.85);
  max-width: 250px;
  padding: 10px;
  z-index: 3000;
  box-sizing: border-box;
  -webkit-backdrop-filter: blur(10px);
  backdrop-filter: blur(10px);
  
  font-size: 12px;
  border-radius: 5px;
  border: 1px solid #e0e0e0;
  display: none;
  top: 0px;
  left: 0px;
  box-shadow: 0 2px 10px #00000015;
  color: rgb(53, 53, 53);
  word-break: break-all;
}
</style>
