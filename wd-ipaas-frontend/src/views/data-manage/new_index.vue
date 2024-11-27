<template>
  <div class="data-source">
    <div class="header">数据源管理</div>
    <div class="body">
      <source-list @newSource="onAdd"></source-list>
      <div class="table-list">
        <table-list></table-list>
      </div>
    </div>
    <!-- <div class="bd-container">
      <div class="bd-search page-search">
        <el-form size="mini"
                 label-position="right"
                 class="bd-form"
                 inline>
          <el-form-item label="数据源类型">
            <el-select v-model="dataBaseType"
                       filterable
                       placeholder="请选择"
                       @focus="showItem">
              <el-option v-for="item in options"
                         :key="item.value"
                         :label="item.databaseName"
                         :value="item.databaseName"></el-option>
            </el-select>
          </el-form-item>
          <div style="display:inline-block;"
               class="bd-search-group">
            <el-button type="primary"
                       @click="onSearch"
                       class="bd-button bd-strong">查询</el-button>
            <el-button type="primary"
                       plain
                       size="mini"
                       class="bd-button bd-strong"
                       @click="onReset">重置</el-button>
          </div>
        </el-form>
      </div>
      <div class="type">
        <info-card-list :card-list="filterSource"
                        v-loading="loading"
                        card-type="datasource"
                        @command="onCommand" /> -->
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
      <!-- </div> -->
      <data-add ref="dialog"
                :visible.sync="dialog.add"
                @addSuccess="addSuccess"
                :appInfo="appInfo" />
      <data-manage-detail :visible.sync="dialog.detail"
                          :source="dataSourceItem"></data-manage-detail>
    <!-- </div> -->
  </div>
</template>

<script>
import DataAdd from './data-add.vue';
// import dataSource from '@/api/datasource.js';
import dataBase from '../../api/data-base.js';
import dataSource from '../../api/data-source';
import IPagesize from '../../mixins/i-pagesize.js';
import InfoCardList from '../../bz-components/info-card-list';
import DataManageDetail from './data-manage-detail/index';
import sourceList from "./source-list";
import tableList from "./table-list";
export default {
  mixins: [IPagesize],
  components: { DataAdd, InfoCardList, DataManageDetail,sourceList,tableList },
  data () {
    return {
      flag: false,
      dataBaseType: null, //接入源名id
      options: [], //搜索数据名选项
      appInfo: null,
      dialog: {
        add: false,
        detail: false
      },
      dataSourceItem: null,
      loading: false,
      dataList: [], //存放已经添加的数据库
      cardArr: [], //存放数据源卡片数组
      dataSourceId: null, //存放要删除的卡片id
      query: {
        dataBaseType: ''
      }
    };
  },
  computed: {
    filterSource () {
      if (!this.query.dataBaseType) {
        return this.cardArr;
      }
      return this.cardArr.filter(item => item.dbType === this.query.dataBaseType);
    }
  },
  created () {
    this.getDataSourceList();
  },
  methods: {
    onSearch () {
      this.query.dataBaseType = this.dataBaseType;
    },
    getDataSourceList () {
      this.loading = true;
      dataSource
        .list()
        .done(res => {
          // console.log(res);
          this.cardArr = res.data;
        })
        .always(_ => {
          this.loading = false;
        });
    },
    onReset () {
      this.dataBaseType = '';
      this.query.dataBaseType = '';
    },
    onAdd () {
      this.appInfo = null;
      this.dialog.add = true;
    },
    showItem () {
      dataBase.showDataName().done(res => {
        this.options = res.data;
      });
    },
    onCommand (operation, source) {
      const method = this['on' + operation];
      if (method) {
        method(source);
      }
    },
    onEdit (cardData) {
      // console.log(+e.target.dataset.id);
      this.appInfo = cardData.id;
      this.dialog.add = true;
    },
    onDelete (cardData) {
      this.$confirm('确定删除该数据源吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        dataSource.deleteDataSource({ dataSourceId: cardData.id }).done(res => {
          this.getDataSourceList();
          // console.log(res)
        });
      });
    },
    addSuccess () {
      this.getDataSourceList();
    },
    onShowDetail (sourceItem) {
      this.dataSourceItem = sourceItem;
      this.dialog.detail = true;
    },
  }
};
</script>

<style scoped lang="less">
.data-source {
  width: 100%;
  height: 100%;
  background-color: #f3f6f8;
  color: #333333;
  font-size: 14px;
  .header {
    padding-left: 20px;
    line-height: 60px;
    background: #ffffff;
    font-size: 16px;
    font-weight: 600;
  }
  .body {
    padding: 20px;
    width: 100%;
    height: calc(100vh - 112px);
    position: relative;
    .table-list {
      position: absolute;
      top: 20px;
      left: 340px;
      right: 20px;
      bottom: 20px;
      background-color: #ffffff;
    }
  }
  * {
    box-sizing: border-box;
  }
}

.page-search {
  /deep/.el-form-item--mini {
    margin-bottom: 0 !important;
  }
}

.page-data-source {
  // height: @full-height;
  // overflow: auto;
  box-sizing: border-box;
  padding-top: 80px;

  .bd-container {
    background: white;
  }
}
</style>