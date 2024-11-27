<template>
  <div>
    <div class="type">
      <info-card-list
        v-loading="loading"
        :show-operation="card.length === 0"
        :card-list="card"
        card-type="access"
        @command="onCommand"
      />
    </div>
    <new-join :app-info="appInfo" :visible.sync="dialog.join" @saved="onSaved" />
    <display-app :app-info="displayApp" :visible.sync="dialog.display" />
  </div>
</template>

<script>
  // import InfoCardList from '@/bz-components/info-card-list';
  import NewJoin from './new-join.vue';
  import DisplayApp from './display-app.vue';
  import mydata from '../../../api/mydata.js';
  import InfoCardList from '../../../bz-components/info-card-list';
  export default {
    components: { NewJoin, DisplayApp, InfoCardList },
    data() {
      return {
        card: [], // 存放数据源卡片数组
        appInfo: null,
        loading: false,
        displayApp: null,
        dialog: {
          join: false,
          display: false,
        },
      };
    },
    created() {
      this.getApiList();
    },
    methods: {
      getApiList() {
        this.loading = true;
        mydata
          .getJoinInfo()
          .done(res => {
            // console.log(res)
            this.card = res.data;
          })
          .always(_ => {
            this.loading = false;
          });
      },
      onCommand(operation, source) {
        // this['on'+operation](source);
        const method = this['on' + operation];
        if (method) {
          method(source);
        }
      },
      onDelete(appInfo) {
        this.$confirm('确定删除该接入吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          // center: true
        })
          .then(() => {
            this.delete(appInfo);
          })
          .catch(res => {
            console.log('用户取消操作', res);
          });
      },
      onEdit(access) {
        this.appInfo = access;
        this.dialog.join = true;
      },
      delete(appInfo) {
        // console.log(appInfo)
        mydata.deleteJoin({ dataAccessAppId: appInfo.dataAccessAppId }).done(res => {
          this.$message.success('删除成功');
          this.getApiList();
        });
      },
      // resetSecret(appInfo) {
      //   mydata.resetSecret({ dataAccessAppId: appInfo.dataAccessAppId }).done(res => {
      //     this.displayApp = res.data;
      //     this.dialog.display = true;
      //     this.getApiList();
      //   });
      // },
      onAdd() {
        this.appInfo = {};
        this.dialog.join = true;
      },
      onSaved() {
        this.dialog.join = false;
        this.getApiList();
      },
    },
  };
</script>

<style scoped lang="less">
  .add {
    width: 200px;
    height: 200px;
    border: 1px solid #ccc;
  }
  .card {
    width: 280px;
    height: 200px;
    margin-right: 10px;
    position: relative;
    margin-bottom: 8px;
    z-index: 2;
    box-shadow: 0px 0px 10px 0px rgba(0, 0, 0, 0.2);
    .top {
      display: flex;
      position: relative;
      justify-content: space-between;
      .card-title {
        padding: 6px 10px;
        color: #fff;
        background-color: #2776fb;
        border-radius: 5px;
      }
    }
    .title {
      font-size: 14px;
      color: #333;
      padding: 14px 12px;
    }
    .descript {
      font-size: 12px;
      color: #bdbdbd;
      padding-left: 12px;
      width: 192px;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }
    .key,
    .scret {
      font-size: 12px;
      color: #bdbdbd;
    }
  }
</style>
