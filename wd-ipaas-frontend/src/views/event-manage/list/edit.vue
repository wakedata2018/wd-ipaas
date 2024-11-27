<template>
  <div class="event-edit">
    <el-tabs v-model="activeName" class="event-edit-content">
      <el-tab-pane label="基本信息" name="1">
        <base-info ref="baseInfo" :info="info" :is-edit="isEdit" v-loading="loadInfo" :is-using="isUsing"></base-info>
      </el-tab-pane>
      <el-tab-pane label="仓库地址" name="2">
        <origin-info ref="originInfo" :info="info" :is-edit="isEdit" :is-using="isUsing"></origin-info>
      </el-tab-pane>
    </el-tabs>
    <div class="event-edit-footer">
      <el-button type="primary" plain @click="$router.go(-1)">返回</el-button>
      <el-button type="primary" @click="validForm()" :loading="loading" v-if="isEdit && !isUsing">保存</el-button>
    </div>
  </div>
</template>

<script>
  import requester from '@/api/event-manage';
  import BaseInfo from './baseInfo.vue';
  import OriginInfo from './originInfo.vue';

  export default {
    components: {
      BaseInfo,
      OriginInfo,
    },
    data() {
      return {
        activeName: '1',
        loading: false,
        info: null,
        status: '',
        isEdit: true,
        loadInfo: false,
        id: this.$route.query.id,
        isUsing: false,
      };
    },
    mounted() {
      const { id, isEdit } = this.$route.query;
      if (id) this.initDetail(id);
      if (isEdit === '0') this.isEdit = false;
      else this.isEdit = true;
    },
    methods: {
      validForm() {
        const _this = this;
        const baseInfo = this.$refs.baseInfo.getFormData();
        const originInfo = this.$refs.originInfo.getFormData();

        Promise.all([baseInfo, originInfo])
          .then(result => {
            const params = {
              ...result[0],
              eventSourceAddress: [...result[1]],
            };
            if (this.id) params.id = parseInt(this.id);
            _this.onSubmit(params);
          })
          .catch(error => {
            console.log('检验错误', error);
          });
      },
      async onSubmit(data) {
        try {
          this.loading = true;
          const res = !this.id ? await requester.addEvent(data) : await requester.updateEvent(data);
          if (res.success) {
            this.$message.success('操作成功');
            setTimeout(() => {
              this.$router.go(-1);
            }, 100);
          } else {
            this.$message.error(res.msg);
          }
        } finally {
          this.loading = false;
        }
      },
      async initDetail(id) {
        try {
          this.loadInfo = true;
          const { data } = await requester.eventDetail({ id: id });
          this.info = this.$plain(data);
          this.isUsing = this.info?.subscribeStatus;
        } finally {
          this.loadInfo = false;
        }
      },
    },
  };
</script>
<style lang="less" scoped>
  .event-edit {
    &-content {
      padding: 30px;
    }
    .event-edit-footer {
      position: fixed;
      left: 200px;
      bottom: 0;
      width: 100%;
      height: 80px;
      line-height: 80px;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
      text-align: center;
      background: #fff;
    }
  }
</style>
