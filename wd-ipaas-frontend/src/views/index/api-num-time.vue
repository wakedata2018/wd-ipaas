<template>
  <div class="box">
    <emptyInfo v-show="!loading && (!groupTime || groupTime.length <= 0)">
      <div v-text="'哎呀，暂时没有数据，稍候再来吧'"></div>
    </emptyInfo>
    <div id="myChart2" v-show="!!groupTime && groupTime.length > 0"></div>
    <!-- <span v-show="!!groupTime && groupTime.length > 0">TOP10</span> -->
  </div>
</template>

<script>
  import emptyInfo from '@/components/empty-info.vue';
  export default {
    name: 'Home',
    components: { emptyInfo },
    props: {
      groupTime: {
        type: Array,
      },
      loading: {
        type: Boolean,
        default: false,
      },
    },
    watch: {
      groupTime(val) {
        const arr = [];
        const catelogry = [];
        val.forEach(item => {
          const obj = {};
          obj.name = item.primaryName;
          obj.value = item.secondaryName;
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
      // return
      this.myChart = this.$echarts.init(document.getElementById('myChart2'));
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
        dataShadow: [],
        data: [],
        myChart: null,
      };
    },
    methods: {
      SetEchart() {
        // 基于准备好的dom，初始化echarts实例

        // 绘制图表
        const option = {
          color: ['#7BD4DF'],
          title: {
            show: true,
            text: '近7天接口调用时长排名（单位：毫秒）',
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
          xAxis: {
            type: 'category',
            data: this.dataAxis,
            // type:'category',
            // axisLine:{
            //   show:false
            // },
            // axisLabel:{
            //   interval:0,
            //   rotate:'15'
            // }
            axisLabel: {
              formatter: params => {
                if (params && params.length > 2) {
                  return params.substr(0, 2) + '...';
                }
                return params;
              },
            },
            axisTick: {
              show: false,
            },
            axisLine: {
              show: false,
            },
            // axisLine: {
            //   show: true,
            //   lineStyle: {
            //     type: 'solid'
            //   }
            // }
          },
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
              name: '访问时长 单位：毫秒',
              type: 'bar',
              barWidth: '40%',
              data: this.data,
              itemStyle: {
                normal: {
                  label: {
                    show: true,
                    position: 'top',
                    textStyle: { color: 'black' },
                  },
                },
              },
            },
          ],
        };
        this.myChart.setOption(option);
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
  #myChart2 {
    height: 345px;
    padding: 20px;
    box-sizing: border-box;
  }
  .box {
    padding: 0 !important;
    // box-shadow: 0px 5px 9px 0px rgba(0, 0, 0, 0.1);
    box-sizing: border-box;
    position: relative;
    height: 345px;
    border-radius: 8px;
    background-color: #fff;
    overflow: hidden;
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
