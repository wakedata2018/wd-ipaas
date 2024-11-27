<template>
  <div class="box" v-loading="loading">
    <emptyInfo v-show="!chartData.xAxis || !chartData.xAxis.length">
      <div v-text="'哎呀，暂时没有数据，稍候再来吧'"></div>
    </emptyInfo>
    <div
      v-show="chartData.xAxis && chartData.xAxis.length"
      ref="myChart"
      style="width: 100%; height: 100%"
    ></div>
  </div>
</template>

<script>
import echarts from "echarts";
import emptyInfo from "@/components/empty-info.vue";
export default {
  components: { emptyInfo },
  props: {
    chartData: {
      type: Object,
      default: () => {},
    },
    loading: {
      type: Boolean,
      default: false,
    },
    active: {
      type: Boolean,
      default: false,
    },
    type: String,
    dateType: {
      type: String,
      default: "",
    },
  },
  watch: {
    chartData (val) {
      this.setEchart(val);
      this.resizeChart();
    },
    // active(val) {
    //   if (val) {
    //     this.$nextTick(() => {
    //       this.setEchart(this.chartData);
    //       this.resizeChart();
    //     });
    //   }
    // },
  },
  data() {
    return {
      myChart: null,
    };
  },
  mounted() {
    // this.setEchart();
    $(window).on("resize", this.resizeChart);
  },
  beforeDestroy() {
    $(window).off("resize", this.resizeChart);
    this.myChart = null;
  },
  methods: {
    getOption(val) {
      const { xAxis, count } = this.chartData;
      const option = {
        color: ["#5B8FF9", "#FA4745"],
        tooltip: {
          trigger: "axis",
          formatter: (params) => {
            let html = "";
            params.forEach((v) => {
              html = html += `${
                v.seriesIndex === 0
                  ? `${params[0].name}${this.dateType == "DAY" ? "时" : "日"}`
                  : ""
              }<div style="font-size: 14px">
                <span style="display:inline-block;margin-right:5px;border-radius:10px;width:10px;height:10px;background-color:${
                  v.color
                };"></span>
                ${v.seriesName}：
                <span>${v.value}</span>
                `;
            });
            return html;
          },
          axisPointer: {
            type: "shadow",
            shadowStyle: {
              color: {
                type: "linear",
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [
                  {
                    offset: 0,
                    color: "rgba(238,238,238,0)", // 0% 处的颜色
                  },
                  {
                    offset: 1,
                    color: "rgba(33,150,243,0.13)", // 100% 处的颜色
                  },
                ],
                global: false, // 缺省为 false
              },
            },
          },
        },
        legend: {
          show: false,
          left: "2%",
        },
        grid: {
          left: "3%",
          right: "4%",
          bottom: "4%",
          top: "20%",
          containLabel: true,
        },
        xAxis: {
          type: "category",
          data: xAxis,
          axisTick: {
            show: false,
            alignWithLabel: true,
          },
          axisLine: {
            lineStyle: {
              color: "#CCCCCC",
            },
          },
          axisLabel: {
            color: "#A8A8A8",
            formatter: (params) => {
              let str = "";
              if (this.dateType === "DAY") {
                str = params + "时";
                return str;
              } else if (this.dateType === "WEEK") {
                str = params + "日";
                return str;
              }
            },
          },
        },
        yAxis: {
          type: "value",
          axisTick: {
            show: false,
          },
          axisLine: {
            show: false,
          },
          axisLabel: {
            color: "#A8A8A8",
          },
          splitLine: {
            lineStyle: {
              color: "#E6E6E6",
            },
          },
        },
        series: {
          name: "调用次数",
          type: "line",
          symbolSize: 0,
          data: count,
        },
      };
      if (this.type === "consume") {
        option.series.name = "调用耗时(ms)";
      }
      if (this.type === "count") {
        option.legend.show = true;
        option.series = [
          {
            name: "调用总次数",
            type: "line",
            symbolSize: 0,
            data: count.total,
          },
          {
            name: "调用失败次数",
            type: "line",
            symbolSize: 0,
            data: count.error,
          },
        ];
      }
      return option;
    },
    setEchart(val) {
      // 基于准备好的dom，初始化echarts实例
      this.myChart = this.$echarts.init(this.$refs.myChart);
      // 绘制图表
      this.myChart.setOption(this.getOption(val));
    },
    resizeChart() {
      this.$nextTick(() => {
        if (this.myChart) {
          this.myChart.resize();
        }
      });
    },
  },
};
</script>

<style scoped lang="less">
.box {
  height: 260px;
}
</style>
