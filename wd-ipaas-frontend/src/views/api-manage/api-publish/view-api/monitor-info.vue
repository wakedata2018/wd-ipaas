<template>
  <div style="padding-bottom: 20px">
    <el-row :gutter="20" style="padding: 6px;">
      <el-col :span="6" v-for="(val, key) in overviewPanel" :key="key">
        <div class="overview-block">
          <span>{{val.count}}</span>
          <span>{{val.label}}</span>
        </div>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12" style="position: relative">
        <h3>调用次数分布</h3>
        <div class="date-picker">
          <el-select v-model="date.callCount" placeholder="请选择" @change="getCallCount">
            <el-option
              v-for="item in dateOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </div>
        <line-chart
          :chart-data="callCountChart.data"
          :loading="callCountChart.loading"
          :active="active"
          :type="'count'"
          :dateType="date.callCount"
        ></line-chart>
      </el-col>
      <el-col :span="12" style="position: relative">
        <h3>QPS</h3>
        <div class="date-picker">
          <el-select v-model="date.QPS" placeholder="请选择" @change="getQPS">
            <el-option
              v-for="item in dateOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </div>
        <line-chart
          :chart-data="qpsChart.data"
          :loading="qpsChart.loading"
          :active="active"
          :type="'qps'"
          :dateType="date.QPS"
        ></line-chart>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="12" style="position: relative">
        <h3>调用耗时</h3>
        <div class="date-picker">
          <el-select v-model="date.callConsume" placeholder="请选择" @change="getCallConsume">
            <el-option
              v-for="item in dateOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </div>
        <line-chart
          :chart-data="callConsumeChart.data"
          :loading="callConsumeChart.loading"
          :active="active"
          :type="'consume'"
          :dateType="date.callConsume"
        ></line-chart>
      </el-col>
      <el-col :span="12" style="position: relative">
        <h3>应用调用排行</h3>
        <div class="date-picker">
          <el-select v-model="date.callRank" placeholder="请选择" @change="getCallRank">
            <el-option
              v-for="item in dateOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </div>
        <el-table :data="callRank.list" class="dss-table bd-table" v-loading="callRank.loading" height="260px">
          <el-table-column type="index" label="排名"></el-table-column>
          <el-table-column prop="appName" label="接入应用名称"></el-table-column>
          <el-table-column prop="inCharge" label="创建者"></el-table-column>
          <el-table-column prop="num" label="调用次数"></el-table-column>
        </el-table>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import LineChart from "./line-chart.vue";
import API from "@/api/api-monitor.js";
export default {
  components: { LineChart },
  props: {
    dataAssetId: Number,
    active: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      dateOptions: [
        {
          value: 'DAY',
          label: '近24小时'
        },
        {
          value: 'WEEK',
          label: '近7天'
        }
      ],
      date: {
        callCount: 'DAY',
        QPS: 'DAY',
        callConsume: 'DAY',
        callRank: 'DAY'
      },
      overviewPanel: {
        historyCount: {
          count: 0,
          label: '历史调用总次数'
        },
        todayCount: {
          count: 0,
          label: '今日调用总次数'
        },
        todaySuccess: {
          count: 0,
          label: '今日调用成功次数'
        },
        todayFailed: {
          count: 0,
          label: '今日调用失败次数'
        }
      },
      callCountChart: {
        data: {
          xAxis: [],
          count: {
            total: [],
            error: []
          }
        },
        loading: false
      },
      qpsChart: {
        data: {
          xAxis: [],
          count: []
        },
        loading: false
      },
      callConsumeChart: {
        data: {
          xAxis: [],
          count: []
        },
        loading: false
      },
      callRank: {
        list: [],
        loading: false
      }
    };
  },
  watch: {
    active (val) {
      if (val) {
        this.init();
      }
    }
  },
  methods: {
    init() {
      this.getOverviewPanel();
      this.getCallRank();
      this.getQPS();
      this.getCallConsume();
      this.getCallCount();
    },
    getOverviewPanel() {
      API.assetMonitor({ dataAssetId: this.dataAssetId }).done(res => {
        const data = res.data || {};
        for (const key in data) {
          this.overviewPanel[key].count = data[key] || 0;
        }
      });
    },
    getCallRank() {
      const params = {
        dataAssetId: this.dataAssetId,
        type: this.date.callRank
      };
      this.callRank.loading = true;
      API.monitorAppTimes(params)
        .done(res => {
          this.callRank.list = (res.data || []).filter((item, index) => {
            return index < 5;
          });
        })
        .always(() => {
          this.callRank.loading = false;
        });
    },
    getCallCount() {
      const params = {
        dataAssetId: this.dataAssetId,
        type: this.date.callCount
      };
      this.callCountChart.loading = true;
      API.monitorTimes(params)
        .done(res => {
          let data = res.data || {};
          this.callCountChart.data = this.handleChartData(data, true);
        })
        .always(() => {
          this.callCountChart.loading = false;
        });
    },
    getCallConsume() {
      const params = {
        dataAssetId: this.dataAssetId,
        type: this.date.callConsume,
        qps: false
      };
      this.callConsumeChart.loading = true;
      API.monitorQps(params)
        .done(res => {
          let data = res.data || {};
          this.callConsumeChart.data = this.handleChartData(data);
        })
        .always(() => {
          this.callConsumeChart.loading = false;
        });
    },
    getQPS() {
      const params = {
        dataAssetId: this.dataAssetId,
        type: this.date.QPS,
        qps: true
      };
      this.qpsChart.loading = true;
      API.monitorQps(params)
        .done(res => {
          let data = res.data || {};
          this.qpsChart.data = this.handleChartData(data);
        })
        .always(() => {
          this.qpsChart.loading = false;
        });
    },
    handleChartData(data, double = false) {
      let arr = [];
      for (const key in data) {
        let obj = {
          timestamp: new Date(key + ':').getTime(),
          xAxis: key.substr(-2),
          count: data[key]
        };
        if (double) {
          obj.count = {
            total: data[key].totalNum,
            error: data[key].errorNum
          };
        }
        arr.push(obj);
      }
      arr.sort((a, b) => {
        return a.timestamp - b.timestamp;
      });
      let chart = {
        xAxis: [],
        count: []
      };
      if (double) {
        chart.count = {
          total: [],
          error: []
        };
      }
      for (const item of arr) {
        chart.xAxis.push(item.xAxis);
        if (double) {
          chart.count.total.push(item.count.total);
          chart.count.error.push(item.count.error);
        } else {
          chart.count.push(item.count);
        }
      }
      return chart;
    }
  }
};
</script>

<style lang="less" scoped>
.overview-block {
  height: 114px;
  background-color: #fff;
  box-shadow: 0px 2px 6px 0px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  & > span {
    &:first-child {
      margin-bottom: 10px;
      font-size: 20px;
      font-weight: 700;
      color: #333;
    }
    &:last-child {
      font-size: 14px;
      font-weight: 400;
      color: #8d939d;
    }
  }
}
.date-picker {
  position: absolute;
  top: 12px;
  right: 10px;
  width: 120px;
  /deep/ .el-input__inner {
    font-size: 14px;
    color: #8d939d;
  }
}
</style>
