// 可视化viewConfig(编辑)

import types from '../view-config-types';
import Vue from 'vue';
/**返回嵌套在最底层的字段指针
 *
 * @param {string} key 对象的key
 * @param {object} item 该对象
 * @returns {value} item[key] 对应的指针
 */
function changeEachSeries(newSeries, { keyList, value }) {
  keyList.reduce((old, cur, idx, arr) => {
    if (idx === arr.length - 1) {
      return old[cur] = value;
    }
    return old[cur];
  }, newSeries);
}
const _state = {
  /**编辑时初始化状态 true初始化完成，false初始化未完成 */
  editInited: true,
  type: '', // 可视化块类型
  vertical: false, // 图表布局 是否垂直
  topNum: 5,
  category: {}, // 气泡图、散点图维度字段
  bubbleSize: {}, // 气泡图，控制气泡大小字段
  series: {},
  seriesKey: [],
  xAxis: [
    /* X 轴、类目轴信息
        {
            name: String, // 数据名称
            type: String  // 数据类型
        }
        */
  ],
  yAxis: [
    /* Y轴、数值轴列表
        {
            name: String,           // 数据名称
            custom: {               // 对应的series配置
                type: String,       // 生成Echarts图形类型
                itemStyle: {
                    normal: {       // 普通状态
                    },
                    emphasis: {     // 高亮模式
                    }
                },
                stack: String       // 堆积参数
            }
        },
        */
  ],
  custom: {
    // chart全局配置
    theme: 'default',
    title: {
      // 标题
      textStyle: {}
    },
    legend: {
      // 图例组件
    },
    tooltip: {
      // 提示框组件
    },
    grid: {
      // 网格组件
    },
    toolbox: {
      // 工具栏属性
    },
    xAxis: [
      // chart X轴配置
    ],
    yAxis: [
      // chart Y轴配置
    ],
    series: [
      // 数值轴series默认配置
    ]
  }
};

const mutations = {
  [types.EDIT_INIT_STATE_CHANGE](state, payload) {
    state.editInited = payload;
  },
  // 可视化视图配置
  [types.TYPE_CHANGE](state, payload) {
    state.type = payload.type;
  },
  [types.VERTICAL_CHANGE](state, payload) {
    state.vertical = payload.vertical;
  },
  [types.TOPNUM_CHANGE](state, payload) {
    state.topNum = payload.topNum;
  },
  [types.CUSTOM_CHANGE](state, payload) {
    state.custom = Vue.plain(payload.custom);
  },
  [types.XAXIS_CHANGE](state, payload) {
    if (payload.index >= 0) {
      state.xAxis[payload.index] = payload.xAxis;
    } else {
      state.xAxis = payload.xAxis;
    }
  },
  [types.YAXIS_CHANGE](state, payload) {
    if (payload.index >= 0) {
      state.yAxis[payload.index] = payload.yAxis;
    } else {
      state.yAxis = payload.yAxis;
    }
  },
  [types.YAXIS_SERIES_CHANGE](state, payload) {
    if (payload.index >= 0) {
      state.yAxis[payload.index].series = payload.series;
    } else {
      state.yAxis = state.yAxis.map(item => {
        return {
          name: item.name,
          series: payload.series
        };
      });
    }
  },
  [types.SERIES_CHANGE] (state, payload) {
    if (payload.name && payload.type) {
        return state.series[payload.type][payload.name] = payload.series;
    }
    if (payload.name) {
        return state.series[payload.name] = payload.series;
    }
    if (payload.type) {
        return state.series[payload.type] = payload.series;
    }
    state.series = payload.series;
  },
  // [types.G_SERIES_CHANGE](state, payload) {
  //   if (state.custom.series[0]) {
  //     state.custom.series[0] = payload.gSeries;
  //   } else {
  //     state.custom.series = [payload.gSeries];
  //   }
  // },
  [types.G_SERIES_CHANGE] (state, payload) {
    if (state.custom.series[0]) {
        state.custom.series.splice(0, 1, payload.gSeries);
    } else {
        state.custom.series = [payload.gSeries];
    }
    Object.keys(state.series).map(seriesName => {
        changeEachSeries(state.series[seriesName], payload.newItemInfo)
    });
  },
  [types.CATEGORY_CHANGE](state, payload) {
    state.category = payload.category
  },
  [types.BUBBLE_SIZE_CHANGE](state, payload) {
    state.bubbleSize = payload.bubbleSize
  },
  [types.SERIES_KEY_CHANGE](state, payload) {
    state.seriesKey = payload.seriesKey
  }
};

const actions = {
  editInitedChange({ commit }, val) {
    commit(types.EDIT_INIT_STATE_CHANGE, val);
  },
  // 可视化类型变化
  typeChange({ commit }, type) {
    commit(types.TYPE_CHANGE, { type });
  },
  // 布局变化
  verticalChange({ commit }, vertical) {
    commit(types.VERTICAL_CHANGE, { vertical });
  },
  topNumChange({ commit }, topNum) {
    commit(types.TOPNUM_CHANGE, { topNum });
  },
  // 气泡图、散点图 维度
  categoryChange({ commit }, category) {
    commit(types.CATEGORY_CHANGE, { category });
  },
  bubbleSizeChange({ commit }, bubbleSize) {
    commit(types.BUBBLE_SIZE_CHANGE, { bubbleSize });
  },
  // chart全局配置变化
  customChange({ commit }, custom) {
    commit(types.CUSTOM_CHANGE, { custom });
  },
  xAxisChange({ commit }, { xAxis, index = -1 }) {
    commit(types.XAXIS_CHANGE, { xAxis, index });
  },
  yAxisChange({ commit }, { yAxis, index = -1 , type = undefined}) {
    commit(types.YAXIS_CHANGE, { yAxis, index, type});
  },
  yAxisSeriesChange({ commit }, { series, index = -1 }) {
    commit(types.YAXIS_SERIES_CHANGE, { series, index });
  },
  seriesChange({ commit }, { series, name, type }) {
    commit(types.SERIES_CHANGE, { series, name, type});
  },
  gSeriesChange({commit}, {gSeries, newItemInfo}) {
    commit(types.G_SERIES_CHANGE, {gSeries, newItemInfo});
  },
  seriesKeyChange({commit}, {seriesKey}) {
    commit(types.SERIES_KEY_CHANGE, {seriesKey});
  }
};

const getters = {
  getEditInited: state => state.editInited,
  getType: state => state.type,
  getTopNum: state => state.topNum,
  getVertical: state => state.vertical,
  stateSeries: state => state.series,
  stateXAxis: state => state.xAxis,
  stateYAxis: state => state.yAxis,
  custom: state => state.custom,
  getViewConfig: state => state,
  getCategory: state => state.category,
  getBubbleSize: state => state.bubbleSize,
  seriesKey: state => state.seriesKey
};

export default {
  state: _state,
  mutations,
  actions,
  getters
};
