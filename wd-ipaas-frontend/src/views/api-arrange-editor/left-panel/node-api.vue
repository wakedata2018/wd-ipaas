<template>
  <div class="topic-node">
    <div class="operator-search">
      <el-input v-model="keyword" @change="onChange">
        <i slot="prefix" class="el-input__icon el-icon-search"></i>
      </el-input>
      <api-tree-select :value.sync="groupId" :refresh="true" :can-clear="true" class="group-select" />
    </div>

    <node-item
      v-for="topic in filterApiList"
      :key="topic.id"
      :label="topic.label"
      :node-data="topic"
      @dragstart.native="handleDragstart($event, topic)"
    />
    <!-- @dragend.native="handleDragEnd($event,topic)" -->
  </div>
</template>

<script>
  import NodeItem from './component/node-item.vue';
  // import topicApi from '@/api/topic.js';
  import ApiTreeSelect from '@/bz-components/api-tree-select';
  import eventBus, { EventName, setIsDropGraph } from '../../../components/g6-editor/event-bus';
  import nodeData from '../node-convert/helper/node-data.js';
  import { ApiPublishStatus, ApiType } from '@/utils/enum';
  import apiControllerApi from '@/api/api-controller.js';

  const watchValue = {
    refresh: false,
  };

  function refresh() {
    watchValue.refresh = !watchValue.refresh;
  }

  export { refresh };

  export default {
    components: { NodeItem, ApiTreeSelect },
    data() {
      return {
        offsetX: 0,
        offsetY: 0,
        ApiList: [],
        loading: false,
        watchValue,
        keyword: '',
        groupId: null,
        query: {
          keyword: '',
        },
      };
    },
    computed: {
      filterApiList() {
        const keyword = this.query.keyword;
        if (!keyword.trim() && !this.groupId) {
          return this.ApiList;
        }

        const lowerKeyword = keyword.toLowerCase();
        return this.ApiList.filter(item => {
          if (this.groupId && !keyword.trim()) {
            return item.cmp.apiObj.apiGroupId === this.groupId;
          } else if (!this.groupId && keyword.trim()) {
            return item.label.toLowerCase().indexOf(lowerKeyword) > -1;
          } else {
            return item.label.toLowerCase().indexOf(lowerKeyword) > -1 && item.cmp.apiObj.apiGroupId === this.groupId;
          }
        });
      },
    },
    watch: {
      'watchValue.refresh'() {
        this.fetchComponent();
      },
    },
    created() {
      this.bindEvent();
      this.fetchComponent();
    },
    beforeDestroy() {
      this.unbindEvent();
    },
    methods: {
      bindEvent() {
        eventBus.$on(EventName.refreshData, this.fetchComponent);
      },
      unbindEvent() {
        eventBus.$off(EventName.refreshData, this.fetchComponent);
      },
      /**
       * 从左栏面板拖入算子，获取信息
       */
      handleDragstart(event, topic) {
        this.offsetX = event.offsetX;
        this.offsetY = event.offsetY;
        setIsDropGraph(true);
        // 保存拖入的算子信息
        event.dataTransfer.setData('nodeInfo', JSON.stringify(topic));
        // 节点类型为API列表
        event.dataTransfer.setData('nodeType', 'apiList');
      },
      fetchComponent() {
        this.ApiList = [];
        this.isLoading = true;
        // 获取算子列表
        nodeData
          .fetchNodes()
          .done(nodes => {
            return this.getApiList(nodes);
          })
          .always(() => {
            this.loading = false;
          });
      },
      // 获取API列表 过滤已发布的API接口
      getApiList(nodes) {
        return apiControllerApi
          .getList({ dataAssetPublishStatus: ApiPublishStatus.PUBLISH, pageSize: 999999999 })
          .done(res => {
            const finalRes = res.data.filter(item => item.apiType !== ApiType.LITE_FLOW);
            console.log('请求的数据', res, nodes);
            /**
             * 重新构造数据
             * 从算子列表中拿取基本配置信息赋值给API
             */
            this.ApiList = nodeData.toApiNode(finalRes || [], nodes);
          });
      },
      onChange() {
        this.query.keyword = this.keyword;
      },
    },
  };
</script>

<style lang="less" scoped>
  .group-select {
    margin: 10px 0 20px;
    z-index: 10000;
  }
</style>
