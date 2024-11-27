<template>
  <div class="page-data-source bd-page">
    <div class="bd-header">
      <div class="title">{{ title }}</div>
    </div>
    <div class="bd-container">
      <div class="bd-search page-search">
        <el-form size="mini" label-position="right" class="bd-form" label-width="68px" inline>
          <el-form-item label="关键词">
            <el-input v-model.trim="connectionName" type="text" style="width: 250px" max-length="32" />
          </el-form-item>
          <br />
          <el-form-item label="类型">
            <dss-select
              v-model="dbType"
              :border="true"
              :data="options"
              key-prop="databaseName"
              label-prop="databaseName"
            />
            <!-- <el-select
              v-model="dataBaseType"
              style="width: 250px"
              filterable
              placeholder="请选择"
              @focus="showItem"
            >
              <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.databaseName"
                :value="item.databaseName"
              ></el-option>
            </el-select> -->
          </el-form-item>
        </el-form>
        <div class="bd-search-group">
          <el-button type="primary" class="bd-button bd-strong" @click="onSearch">查询</el-button>
          <el-button type="primary" plain size="mini" class="bd-button bd-strong" @click="onReset">重置</el-button>
        </div>
      </div>
      <div class="type">
        <info-card-list v-loading="loading" :card-list="cardArr" card-type="datasource" @command="onCommand" />
        <!-- <div class="add" @click="add">+</div>
        <div class="card" v-for="(item,index) in cardArr" :key="index">
          <div class="top">
            <div class="card-title">{{item.dbType}}</div>
            <div class="editor">
              <span @click="onEdit($event)">
                <img
                  src="../../assets/images/editor.png"
                  alt
                  class="common-editor"
                  :data-id="item.id"
                />
              </span>
              <span @click="onDelete">
                <img
                  src="../../assets/images/icon-delete.png"
                  alt
                  class="common-editor"
                  :data-id="item.id"
                />
              </span>
            </div>
          </div>
          <div class="title">{{item.dbName}}</div>
          <div class="descript">{{item.description}}</div>
        </div>-->
      </div>
      <data-add ref="dialog" :visible.sync="dialog.add" :app-info="appInfo" @addSuccess="addSuccess" />
      <data-manage-detail :visible.sync="dialog.detail" :source="dataSourceItem"></data-manage-detail>
    </div>
  </div>
</template>

<script>
  import { mapState } from 'vuex';
  import DataAdd from './data-add.vue';
  // import dataSource from '@/api/datasource.js';
  import dataBase from '../../api/data-base.js';
  import dataSource from '../../api/data-source';
  import IPagesize from '../../mixins/i-pagesize.js';
  import InfoCardList from '../../bz-components/info-card-list';
  import DataManageDetail from './data-manage-detail';
  import dssSelect from '@/components/dss-select';

  import pageUtils from '@/utils/page.js';

  export default {
    components: { DataAdd, InfoCardList, DataManageDetail, dssSelect },
    mixins: [IPagesize],
    data() {
      return {
        flag: false,
        dbType: null, // 接入源名id
        connectionName: null,
        options: [], // 搜索数据名选项
        appInfo: null,
        dialog: {
          add: false,
          detail: false,
        },
        dataSourceItem: null,
        loading: false,
        dataList: [], // 存放已经添加的数据库
        cardArr: [], // 存放数据源卡片数组
        dataSourceId: null, // 存放要删除的卡片id
        query: {
          dbType: '',
          connectionName: null,
        },
      };
    },
    computed: {
      ...mapState({
        permitList: state => state.permitList,
      }),
      title() {
        console.log(this.$route.name);
        return pageUtils.findPageName(this.permitList, this.$route.name);
      },
      // filterSource() {
      //   if (!this.query.dbType) {
      //     return this.cardArr;
      //   }
      //   return this.cardArr.filter(
      //     (item) => item.dbType === this.query.dbType
      //   );
      // },
    },
    created() {
      this.getDatasourceType();
      this.getDataSourceList();
    },
    methods: {
      onSearch() {
        this.query.dbType = this.dbType;
        this.query.connectionName = this.connectionName;
        this.getDataSourceList();
      },
      getDataSourceList() {
        this.loading = true;
        dataSource
          .list(this.query)
          .done(res => {
            // console.log(res);
            this.cardArr = res.data;
          })
          .always(() => {
            this.loading = false;
          });
      },
      onReset() {
        this.dbType = null;
        this.query.dbType = null;
        this.connectionName = '';
        this.query.connectionName = '';
        this.onSearch();
      },
      onAdd() {
        this.appInfo = null;
        this.dialog.add = true;
      },
      getDatasourceType() {
        dataBase.showDataName().done(res => {
          this.options = res.data;
        });
      },
      onCommand(operation, source) {
        console.log('onCommand(operation, source)', operation, source);
        const method = this['on' + operation];
        if (method) {
          method(source);
        }
      },
      onEdit(cardData) {
        // console.log(+e.target.dataset.id);
        this.appInfo = cardData.id;
        this.dialog.add = true;
      },
      onDelete(cardData) {
        this.$confirm('确定删除该数据源吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }).then(() => {
          dataSource.deleteDataSource({ dataSourceId: cardData.id }).done(() => {
            this.getDataSourceList();
            // console.log(res)
          });
        });
      },
      addSuccess() {
        this.getDataSourceList();
      },
      onShowDetail(sourceItem) {
        this.dataSourceItem = sourceItem;
        this.dialog.detail = true;
      },
    },
  };
</script>

<style scoped lang="less">
  // .page-search {
  //   /deep/.el-form-item--mini {
  //     margin-bottom: 0 !important;
  //   }
  // }

  .page-data-source {
    // height: @full-height;
    // overflow: auto;
    box-sizing: border-box;

    .bd-container {
      background: white;
    }
  }
</style>
