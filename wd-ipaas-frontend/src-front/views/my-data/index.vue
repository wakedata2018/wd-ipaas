<template>
  <div class="bd-left-right my-data">
    <div class="layout">
      <div class="bd-left">
        <div class="bd-tabs">
          <el-scrollbar class="left-scroll">
            <div
              class="bd-tab-item"
              :class="{'is-active': activeTab === item.value}"
              v-for="item in tabs"
              :key="item.value"
              :title="item.label"
              @click="activeTab = item.value"
            >
              <span class="theme-name">{{item.label}}</span>
            </div>
          </el-scrollbar>
        </div>
      </div>

      <div class="bd-right">
        <el-scrollbar class="right-scroll">
          <Data v-show="activeTab === 'join'" :data="reload" :current="activeTab"/>
          <Apply v-show="activeTab === 'apply'" :data="reload" :current="activeTab"/>
          <Collect v-show="activeTab === 'collect'" :data="reload" :current="activeTab"/>
          <Statistics v-show="activeTab === 'statistic'" :data="reload" :current="activeTab"/>
        </el-scrollbar>
      </div>
    </div>

    <new-join :app-info="appInfo" :visible.sync="dialog.app" @saved="onSaved" />
  </div>
</template>

<script>
import Data from './data/data.vue';
import Apply from './apply/apply.vue';
import Collect from './collect/collect.vue';
// import Join from './join/join.vue';
import Statistics from './statistics/statistics.vue';
import NewJoin from './new-join.vue';

export default {
  components: { Data, Apply, Collect, Statistics, NewJoin },

  data() {
    return {
      activeName: 'join',
      appInfo: null,
      dialog: {
        app: false
      },
      tabs: [
        { label: '我的接入', value: 'join' },
        { label: '我的申请', value: 'apply' },
        { label: '我的收藏', value: 'collect' },
        { label: '我的API统计', value: 'statistic' }
      ],
      activeTab: 'join',
      reload:null,
      // current:'join',
    };
  },
  props:['data'],
  watch:{
    data(val){
      // console.log(val)
      this.reload=val
    }
  },
  methods: {
    handleClick(tab, event) {
      //   console.log(tab, event);
    },
    // handleChange(item){
    //   this.activeTab = item.value
    //   this.current=item.value
    // },
    onAdd() {
      this.appInfo = null;
      this.dialog.app = true;
      console.log('新增');
    },
    onSaved() {
      console.log('确定');
    },
    showItem() {
      // console.log(123);
    }
  }
};
</script>

<style scoped lang="less">
.my-data {
  .layout {
    height: calc(~'100vh - 112px');
  }

  .left-scroll {
    height: 100%;
  }

  .right-scroll {
    position: absolute;
    left: 0;
    right: 0;
    top: 0;
    bottom: 0;
  }

  .bd-tabs {
    height: 100%;

    .bd-tab-item:first-child {
      margin-top: 20px;
    }
  }

  .left-scroll,
  .right-scroll {
    /deep/ .el-scrollbar__wrap {
      overflow-x: hidden;
    }
  }
}
</style>