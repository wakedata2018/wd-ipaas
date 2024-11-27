<template>
  <div class="box">
    <emptyInfo
      v-show="
        !loading &&
        (!analyze ||
          ((!analyze.groupByApiGroup || analyze.groupByApiGroup.length === 0) &&
            (!analyze.groupByApiGroupLastMonth || analyze.groupByApiGroupLastMonth.length === 0)))
      "
    >
      <div v-text="'哎呀，暂时没有数据，稍候再来吧'"></div>
    </emptyInfo>
    <div v-show="!!analyze && !!analyze.groupByApiGroup && analyze.groupByApiGroup.length > 0" id="api-analyze"></div>
  </div>
</template>

<script>
  import emptyInfo from '@/components/empty-info.vue';

  export default {
    name: 'Home',
    components: { emptyInfo },
    props: {
      analyze: {
        type: Object,
        default: null,
      },
      loading: {
        type: Boolean,
        default: false,
      },
    },
    watch: {
      analyze(val) {
        this.setEchart(val);
        this.resizeChart();
      },
    },
    data() {
      return {
        apiAnalyze: null,
      };
    },
    mounted() {
      this.setEchart();
      // $(window).on('resize', this.resizeChart);
    },
    beforeDestroy() {
      // $(window).off('resize', this.resizeChart);
      this.apiAnalyze = null;
    },
    methods: {
      toEchartData(groupByApiGroup) {
        const category = [];
        const data = [];
        const monthData = [];

        groupByApiGroup.forEach(item => {
          category.push(item.primaryName);
          data.push(item.resultValue ?? ''); // 本月
          monthData.push(item.lastResultValue ?? ''); // 上个月
        });
        return {
          category,
          data,
          monthData,
        };
      },
      getOption(val) {
        let groupByApiGroup = [];
        if (val) {
          groupByApiGroup = val.groupByApiGroup;
        }
        const { category, data, monthData } = this.toEchartData(groupByApiGroup);
        const option = {
          color: ['#FF9A7A', '#FFC37A'],
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow',
            },
            extraCssText: 'max-width:200px; word-break: break-word;word-wrap: break-word; white-space: normal',
            formatter(params) {
              const obj = !!params && params.length > 0 ? params[0] : null;
              if (obj) {
                const dataIndex = obj.dataIndex;
                let lastMonth = '';
                if (
                  !!monthData &&
                  monthData.length > dataIndex &&
                  monthData[dataIndex] !== null &&
                  monthData[dataIndex] !== undefined
                ) {
                  lastMonth = '<br/>上月环比：' + monthData[dataIndex];
                }
                return obj.name + '<br/>访问次数：' + obj.value + lastMonth;
              } else {
                return '-';
              }
            },
          },
          legend: {
            data: ['访问次数', '上月环比'],
            right: '20',
          },
          calculable: true,
          title: {
            show: true,
            text: '接口月度调用次数环比（按分类统计）',
            textStyle: {
              color: '#333',
              fontSize: '14',
            },
          },
          grid: {
            left: '1%',
            right: '1%',
            bottom: '10%',
            top: '15%',
            // containLabel: true,
          },
          xAxis: [
            {
              // show: false,
              type: 'category',
              axisTick: { show: false },
              data: category,
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
          yAxis: [
            {
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
              type: 'value',
            },
          ],
          series: [
            {
              name: '访问次数',
              type: 'bar',
              barWidth: '20%',
              barGap: 0,
              // label: labelOption,
              data,
              itemStyle: {
                normal: {
                  label: {
                    show: true,
                    position: 'top',
                    textStyle: { color: 'black' },
                    formatter(item) {
                      return item.data ?? '';
                    },
                  },
                },
              },
            },
            {
              name: '上月环比',
              type: 'bar',
              barWidth: '20%',
              // label: labelOption,
              data: monthData,
              itemStyle: {
                normal: {
                  label: {
                    show: true,
                    position: 'top',
                    textStyle: { color: 'black' },
                    formatter(item) {
                      return item.data ?? '';
                    },
                  },
                },
              },
            },
          ],
        };
        return option;
      },
      setEchart(val) {
        // 基于准备好的dom，初始化echarts实例
        this.apiAnalyze = this.$echarts.init(document.getElementById('api-analyze'));
        // 绘制图表
        const option = this.getOption(val);

        this.apiAnalyze.setOption(option);
      },
      resizeChart() {
        this.$nextTick(_ => {
          if (this.apiAnalyze) {
            this.apiAnalyze.resize();
          }
        });
      },
    },
  };
</script>

<style scoped lang="less">
  #api-analyze {
    width: 100%;
    height: 365px;
    padding: 20px;
    box-sizing: border-box;
  }
  .box {
    padding: 0 !important;
    height: 365px;
    background-color: #fff;
    // box-shadow: 0px 5px 9px 0px rgba(0, 0, 0, 0.1);
    box-sizing: border-box;
    position: relative;
    border-radius: 8px;
    overflow: hidden;
  }
</style>
