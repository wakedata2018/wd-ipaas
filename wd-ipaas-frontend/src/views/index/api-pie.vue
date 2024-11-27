<template>
  <div class="box">
    <emptyInfo v-show="!loading && (!err || err.length === 0)">
      <div v-text="'哎呀，暂时没有数据，稍候再来吧'"></div>
    </emptyInfo>
    <div v-show="!!err && err.length > 0" id="api-pie"></div>
  </div>
</template>

<script>
  import emptyInfo from '@/components/empty-info.vue';
  export default {
    name: 'Home',
    components: { emptyInfo },
    props: {
      err: {
        type: Array,
      },
      loading: {
        type: Boolean,
        default: false,
      },
    },
    watch: {
      err(val) {
        const arr = [];
        const arrName = [];
        // console.log(val)
        val.forEach(item => {
          const obj = {};
          obj.name = item.primaryName;
          obj.value = item.resultValue;
          arrName.push(item.primaryName);
          arr.push(obj);
        });
        this.data = arr;
        this.dataName = arrName;
        this.SetEchart();
        this.resizeChart();
      },
    },
    mounted() {
      this.SetEchart();
      // $(window).on("resize", this.resizeChart);
    },
    beforeDestroy() {
      // $(window).off("resize", this.resizeChart);
      this.apiPie = null;
    },
    data() {
      return {
        dataShadow: [],
        data: [],
        dataName: [],
        apiPie: null,
      };
    },
    methods: {
      SetEchart() {
        // 基于准备好的dom，初始化echarts实例
        this.apiPie = this.$echarts.init(document.getElementById('api-pie'));
        // 绘制图表
        let total = 0;
        if (this.data) {
          this.data.forEach(item => {
            total += item.value;
          });
        }
        const option = {
          color: ['#509EFF', '#FF7E77', '#FFC37A', '#BF70FF', '#65ceff', '#eba7fd'],
          tooltip: {
            trigger: 'item',
            appendToBody: true,
          },
          title: {
            show: true,
            text: '近30天接口调用错误类型分布',
            textStyle: {
              color: '#333',
              fontSize: '14',
            },
          },
          graphic: [
            {
              // 图形中间文字
              type: 'text',
              left: 'center',
              top: '45%',
              style: {
                text: total,
                textAlign: 'center',
                fill: 'black',
                fontFamily: 'DINPro-Medium, DINPro, Bahnschrift',
                fontSize: 40,
              },
            },
            {
              // 图形中间文字
              type: 'text',
              left: 'center',
              top: '60%',
              style: {
                text: '错误总数',
                textAlign: 'center',
                fill: 'black',
                fontSize: 12,
              },
            },
          ],

          // legend: {
          //   type: 'scroll',
          //   orient: 'vertical',
          //   left: 10,
          //   bottom: 0,
          //   data: this.dataName,
          //   orient: 'horizontal',
          //   formatter(name) {
          //     if (name && name.length > 8) {
          //       return name.substring(0, 7) + '...';
          //     }
          //     return name;
          //   }
          // },
          series: [
            {
              name: '近30天接口调用错误类型分布',
              type: 'pie',
              radius: ['50%', '70%'],
              center: ['50%', '55%'],
              avoidLabelOverlap: true,
              data: this.data,
              itemStyle: {
                emphasis: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)',
                },
              },
              label: {
                formatter: params => {
                  if (params.name && params.name.length > 15) {
                    return params.name.substr(0, 15) + '...';
                  }
                  return params.name;
                },
              },
              tooltip: {
                padding: 10,
                formatter: function (params) {
                  const { value, percent } = params;
                  let name = params.data.name;
                  if (name && name.length > 100) {
                    name = name.substring(0, 100) + '...';
                  }
                  return `<span>标题：${name}<br/>值：${value}<br/>百分比：${percent}% </span>`;
                },
              },
            },
          ],
        };

        this.apiPie.setOption(option);
      },
      resizeChart() {
        this.$nextTick(() => {
          if (this.apiPie) {
            this.apiPie.resize();
          }
        });
      },
    },
  };
</script>

<style scoped lang="less">
  #api-pie {
    width: 100%;
    height: 365px;
    padding: 20px;

    box-sizing: border-box;

    & > div {
      display: none;
      width: 20% !important;
    }
  }
  .box {
    padding: 0 !important;
    // box-shadow: 0px 5px 9px 0px rgba(0, 0, 0, 0.1);
    box-sizing: border-box;
    position: relative;
    height: 365px;
    border-radius: 8px;
    background-color: #fff;
    overflow: hidden;
  }
</style>
