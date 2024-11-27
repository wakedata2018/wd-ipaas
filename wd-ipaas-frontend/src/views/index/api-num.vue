<template>
  <div class="box">
    <emptyInfo v-show="!loading && (!groupApp || groupApp.length <= 0)">
      <div v-text="'哎呀，暂时没有数据，稍候再来吧'"></div>
    </emptyInfo>
    <div id="myChart" v-show="!!groupApp && groupApp.length > 0"></div>
    <!-- <span v-show="!!groupApp && groupApp.length > 0">TOP10</span> -->
  </div>
</template>

<script>
  import emptyInfo from '@/components/empty-info.vue';
  export default {
    name: 'Home',
    components: { emptyInfo },
    props: {
      groupApp: {
        type: Array,
      },
      loading: {
        type: Boolean,
        default: false,
      },
    },
    watch: {
      groupApp(val) {
        const arr = [];
        const catelogry = [];
        val.forEach(item => {
          const obj = {};
          obj.name = item.primaryName;
          obj.value = item.resultValue;
          arr.push(obj);
          catelogry.push(obj.name);
        });
        this.dataAxis = catelogry;
        this.data = arr;
        this.SetEchart();
        this.resizeChart();
      },
    },
    mounted() {
      this.SetEchart();
      // $(window).on('resize', this.resizeChart);
    },
    beforeDestroy() {
      // $(window).off('resize', this.resizeChart);
      this.myChart = null;
    },
    data() {
      return {
        dataAxis: [],
        data: [],
        myChart: null,
      };
    },
    methods: {
      SetEchart() {
        // 基于准备好的dom，初始化echarts实例
        this.myChart = this.$echarts.init(document.getElementById('myChart'));
        // 绘制图表
        const option = {
          color: ['#84C8FF'],
          title: {
            show: true,
            text: '近7天接口调用数据统计（按分类）',
            textStyle: {
              color: '#333',
              fontSize: '14',
            },
          },
          tooltip: {
            trigger: 'axis',
            extraCssText: 'max-width:200px; word-break: break-word;word-wrap: break-word; white-space: normal',
            formatter: '{b}: {c} ',
            axisPointer: {
              // 坐标轴指示器，坐标轴触发有效
              type: 'shadow', // 默认为直线，可选为：'line' | 'shadow'
            },
          },
          grid: {
            // left: '3%',
            // right: '4%',
            // bottom: '1%',
            // top: '25%'
            left: '3%',
            right: '4%',
            bottom: '8%',
            top: '25%',
            // containLabel: true,
          },
          // xAxis: {
          //   data: this.dataAxis,
          //   show: false,
          //   axisLabel: {
          //     show: 'false',
          //     inside: true
          //   },
          //   axisTick: {
          //     show: false
          //   },
          //   type:'category',
          //   axisLine: {
          //     show: true,
          //     lineStyle: {
          //       type: 'solid'
          //     }
          //   }
          // },
          xAxis: [
            {
              // show: false,
              type: 'category',
              axisTick: { show: false },
              data: this.dataAxis,
              axisLine: {
                show: false,
              },
              axisLabel: {
                formatter: params => {
                  if (params && params.length > 3) {
                    return params.substr(0, 3) + '...';
                  }
                  return params;
                },
              },
            },
          ],
          yAxis: {
            axisLine: {
              show: false,
            },
            axisTick: {
              show: false,
            },
            axisLabel: {
              show: false,
              textStyle: {
                color: '#999',
              },
            },
            splitLine: {
              lineStyle: {
                type: 'dashed',
                color: '#EAECED',
              },
            },
          },
          series: [
            {
              name: '访问次数',
              type: 'bar',
              barWidth: '40%',
              data: this.data,
              itemStyle: {
                normal: {
                  label: {
                    show: true,
                    position: 'top',
                    textStyle: { color: 'black' },
                    // formatter(item) {
                    //   return !!item.data ? item.data : '';
                    // }
                  },
                },
              },
            },
          ],
        };
        this.myChart.setOption(option);
        // $(window).resize(_ => {
        //   this.resizeChart();
        // });
      },
      resizeChart() {
        this.$nextTick(_ => {
          if (this.myChart) {
            this.myChart.resize();
          }
        });
      },
    },
  };
</script>

<style scoped lang="less">
  #myChart {
    height: 345px;
    padding: 20px;
    box-sizing: border-box;
  }
  .box {
    padding: 0 !important;
    // box-shadow: 0px 5px 9px 0px rgba(0, 0, 0, 0.1);
    box-sizing: border-box;
    position: relative;
    border-radius: 8px;
    height: 345px;
    overflow: hidden;
    background-color: #fff;
    span {
      font-weight: 300;
      font-size: 12px;
      width: 35px;
      height: 15px;
      // padding: 2px 5px;
      color: #fff;
      background-color: #fa6b46;
      border-radius: 20px;
      position: absolute;
      top: 20px;
      right: 40px;
    }
  }
</style>
