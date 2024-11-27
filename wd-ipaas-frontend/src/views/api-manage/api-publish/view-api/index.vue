<template>
  <el-dialog
    v-loading="loading"
    fullscreen
    custom-class="anim-left"
    lock-scroll
    append-to-body
    class="bd-dialog data-source-edit-dialog"
    title="API详情"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <el-scrollbar ref="scrollbar" class="page-scrollbar">
      <div class="api-publish-view-api">
        <div v-if="baseInfo" v-loading="loading" class="assets-content">
          <DetailInfo :api-type="baseInfo.apiType" :api-detail="apiDetail"></DetailInfo>

          <el-tabs v-model="activeName" type="card">
            <el-tab-pane label="接口参数" name="callInfo">
              <call-info :api-detail="apiDetail"></call-info>
            </el-tab-pane>
            <el-tab-pane label="监控信息" name="monitorInfo">
              <monitor-info
                ref="monitorInfo"
                :data-asset-id="baseInfo.dataAssetApiId"
                :active="activeName === 'monitorInfo'"
              ></monitor-info>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </el-scrollbar>
  </el-dialog>
</template>

<script>
  import accessAPI from '../../../../api/app-access-times.js';
  import DetailInfo from './detail-info.vue';
  import CallInfo from './call-info.vue';
  import MonitorInfo from './monitor-info.vue';

  export default {
    name: 'ApiManageApiPublishViewApi',
    components: { DetailInfo, CallInfo, MonitorInfo },
    props: {
      dataAssetApiId: {
        type: String,
        default: null,
      },
      visible: {
        type: Boolean,
        default: false,
      },
    },

    data: () => ({
      loading: false,
      activeName: 'callInfo',
      apiDetail: null, // 接口的所有信息
      baseInfo: null,
    }),

    computed: {
      dialogVisible: {
        get() {
          return this.visible;
        },
        set(val) {
          this.$emit('update:visible', val);
        },
      },
    },

    watch: {
      dialogVisible(val) {
        if (val) {
          this.init();
        } else {
          this.activeName = 'callInfo';
        }
      },
    },

    methods: {
      hide() {
        this.dialogVisible = false;
      },
      show() {
        this.dialogVisible = true;
      },

      handleToFlow() {
        this.$router.push({
          path: '/api-arrange-editor',
          query: { id: this.baseInfo.dataAssetApiId, readonly: true },
        });
      },

      async init() {
        try {
          this.loading = true;
          const params = {
            timestamp: new Date().getTime(),
            dataAssetId: this.dataAssetApiId,
          };
          const { data } = await accessAPI.assetDetail(params);
          if (!data) {
            return;
          }
          this.apiDetail = data;
          this.baseInfo = data.baseInfo;
        } catch (error) {
          console.error(error);
        } finally {
          this.loading = false;
        }
      },
    },
  };
</script>

<style scoped lang="less">
  @import './style.less';
  .bd-header {
    .title {
      padding: 0 25px;
      line-height: 60px;
    }
  }
  .bd-container {
    background: white;
    padding: 20px;
  }
</style>
