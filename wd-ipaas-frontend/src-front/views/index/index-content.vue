<template>
  <div class="home" v-loading="loading">
    <div class="nav">
      <theme-select
        class="left-container"
        ref="themeSelect"
        v-loading="apiGroupList.loading"
        :list="apiGroupList.list"
        :value="activeThemeId"
        @input="handleGroup"
      ></theme-select>
    </div>

    <div class="card-list">
      <div class="bd-search">
        <div class="search">
          <span class="search-label">关键字</span>
          <el-input v-model.trim="keyword" type="text" style="width: 250px" placeholder="请输入关键字">
            <el-button slot="append" icon="el-icon-search"></el-button>
          </el-input>

          <span class="search-label" style="margin-left: 20px">应用名</span>
          <el-select v-model="dataAccessAppId">
            <el-option
              v-for="item in appList"
              :key="item.dataAccessAppId"
              :value="item.dataAccessAppId"
              :label="item.dataAccessAppName"
            />
          </el-select>
        </div>
      </div>
      <div class="filter" v-show="false">
        <el-radio-group v-model="filter">
          <el-radio-button label="TIME">
            <el-dropdown @command="handleTime">
              <span class="el-dropdown-link"> 更新时间 </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="EARLY">近-远</el-dropdown-item>
                <el-dropdown-item command="LATE">远-近</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </el-radio-button>
          <el-radio-button label="COUNT">
            <el-dropdown @command="handleTimes">
              <span class="el-dropdown-link"> 调用次数 </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="HEIGHT">高-低</el-dropdown-item>
                <el-dropdown-item command="LOW">低-高</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </el-radio-button>

          <el-radio-button label="TYPE">
            <el-dropdown @command="handleType">
              <span class="el-dropdown-link"> 接口格式 </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="">全部</el-dropdown-item>
                <el-dropdown-item command="JSON">JSON</el-dropdown-item>
                <el-dropdown-item command="XML">XML</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </el-radio-button>
          <el-radio-button label="STATUS">
            <el-dropdown @command="handleStatus">
              <span class="el-dropdown-link"> 状态 </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="ALL">全部</el-dropdown-item>
                <el-dropdown-item command="NOTAPPLIED">未申请</el-dropdown-item>
                <el-dropdown-item command="APPLIED">已申请</el-dropdown-item>
                <el-dropdown-item command="NOTCOLLECTED">未收藏</el-dropdown-item>
                <el-dropdown-item command="COLLECTED">已收藏</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </el-radio-button>
        </el-radio-group>
      </div>
      <el-scrollbar class="right-scroll">
        <div class="list-content">
          <div v-for="(item, index) in filteredCard" :key="index">
            <list-item :item="item" @command="handleCommand" :app-id="dataAccessAppId"></list-item>
          </div>
          <div style="text-align: right; margin-top: 20px">
            <el-pagination
              layout="total, sizes, prev, pager, next"
              :total="totalCount"
              :current-page="page.pageNo"
              :page-size="page.pageSize"
              @size-change="onSizeChange"
              @current-change="onCurrentChange"
            ></el-pagination>
          </div>
        </div>
      </el-scrollbar>
    </div>
    <apply-card :api-info="apiInfo" :app-id="dataAccessAppId" :visible.sync="dialog.app" @saved="onSaved" />
  </div>
</template>

<script>
  import index from '../../api';
  import IPagesize from '../../mixins/i-pagesize';
  import applyCard from '../../bz-components/apply-card.vue';
  import listItem from './list-item.vue';
  import store from '../../store';
  import ThemeSelect from './theme-select.vue';
  import myApp from '../../api/my-app';

  export default {
    components: { listItem, applyCard, ThemeSelect },
    mixins: [IPagesize],
    data() {
      return {
        // 轮播图数据
        // picsData: [banner1, banner2],
        card: [], // 卡片存放数组
        cardFilter: [], // 用接口分类筛选过的api
        themes: [], // 测导航的存放数组
        activeThemeId: '',
        activeThemePath: [],
        dataAssetApiId: null, // 用于存放当前选择的apiid
        loading: false,
        filter: 'TIME', // 存放过滤条件
        apiInfo: null,
        groupId: null, // 存放接口分类id
        dialog: {
          app: false,
        },
        keyword: '',
        apiGroupList: {
          list: [],
          loading: false,
        },
        appList: [],
        dataAccessAppId: '',
      };
    },
    props: {
      data: {
        type: String,
        default: null,
      },
    },
    computed: {
      filteredCard() {
        const pageData = this.filterList.slice(
          (this.page.pageNo - 1) * this.page.pageSize,
          this.page.pageNo * this.page.pageSize
        );
        return pageData;
      },
      filterList() {
        const list = this.cardFilter || [];
        if (!this.keyword) {
          return list;
        }
        const lowKeyword = this.keyword.toUpperCase();
        return list.filter(item => item.apiName.toUpperCase().indexOf(lowKeyword) !== -1);
      },
      totalCount() {
        return this.filterList.length;
      },
      dataAccessApp() {
        return this.appList.find(item => item.dataAccessAppId === this.dataAccessAppId);
      },
    },
    watch: {
      data() {
        if (this.data) {
          this.getCardList();
        }
      },
      dataAccessAppId() {
        this.getCardList().done(() => {
          this.handleGroup(this.activeThemeId, this.activeThemePath);
        });
      },
    },
    async created() {
      this.appList = await myApp.getAppList();
      if (this.appList.length > 0) {
        this.dataAccessAppId = this.appList[0].dataAccessAppId;
      } else {
        this.getCardList();
      }

      this.getApiGroup();
    },
    methods: {
      handleTime(command) {
        if (command === 'EARLY') {
          this.card.sort(function (a, b) {
            // 按照时间降序
            return Date.parse(b.updateTime) - Date.parse(a.updateTime);
          });
        } else {
          this.card.sort(function (a, b) {
            // 按照时间降序
            return Date.parse(a.updateTime) - Date.parse(b.updateTime);
          });
        }
        this.cardFilter = [...this.card];
      },
      handleTimes(command) {
        if (command === 'HEIGHT') {
          this.card.sort(function (a, b) {
            // 按照调用次数升序
            const x = a.resultValue;
            const y = b.resultValue;
            return y > x ? 1 : y < x ? -1 : 0;
          });
        } else {
          this.card.sort(function (a, b) {
            // 按照调用次数降序
            const x = a.resultValue;
            const y = b.resultValue;
            return y > x ? -1 : y < x ? 1 : 0;
          });
        }
        this.cardFilter = [...this.card];
      },
      handleFrequent(command) {
        this.cardFilter = this.card.filter(item => !command || item.updateFrequency === command);
        this.page.pageNo = 1;
      },
      handleType(command) {
        this.cardFilter = this.card.filter(item => !command || item.responseContentType === command);
        this.page.pageNo = 1;
      },
      handleStatus(command) {
        this.cardFilter = this.card.filter(item => {
          switch (command) {
            case 'ALL':
              return true;
            case 'NOTAPPLIED':
              return item.approval === 0;
            case 'APPLIED':
              return item.approval !== 0;
            case 'NOTCOLLECTED':
              return item.collected === 0;
            case 'COLLECTED':
              return item.collected !== 0;
          }
          return false;
        });
        this.page.pageNo = 1;
      },
      handleCommand(operation, obj) {
        if (this['on' + operation]) this['on' + operation](...obj);
      },
      handleGroup(id, path) {
        this.activeThemeId = id;
        this.activeThemePath = path;
        this.cardFilter = [...this.card];
        if (path) {
          this.cardFilter = this.card.filter(item => {
            if (path.length === 0 || path.indexOf(item.apiGroupId) !== -1) {
              return item;
            }
            return false;
          });
        }
        this.page.pageNo = 1;
      },
      handleAll() {
        this.getCardList();
      },
      addCollect(dataAssetApiId) {
        this.$confirm('确认收藏吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        })
          .then(() => {
            index
              .addCollect({
                dataAccessAppId: this.dataAccessAppId,
                dataAssetApiId,
              })
              .done(() => {
                this.$message({
                  type: 'success',
                  message: '收藏成功',
                });
                this.handleGroup('');
                this.getCardList();
              });
          })
          .catch(res => {
            console.log('用户取消操作', res);
          });
      },
      deleteCollect(dataAssetApiId) {
        this.$confirm('确定取消该收藏吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          // center: true
        })
          .then(() => {
            index.deleteCollect({ appId: this.dataAccessAppId, collectId: dataAssetApiId }).done(() => {
              this.$message({
                type: 'success',
                message: '取消收藏成功',
              });
              this.handleGroup('');
              this.getCardList();
            });
          })
          .catch(res => {
            console.log('用户取消操作', res);
          });
      },
      hasApp() {
        const existApp = this.appList.length > 0;
        if (!existApp) {
          this.$message.info('您还没有接入秘钥，请先在我的数据-我的接入中申请接入秘钥!');
        }
        return existApp;
      },
      onCollect(dataAssetApiId, collected) {
        if (!this.hasApp()) {
          return;
        }
        if (collected === 0) {
          this.addCollect(dataAssetApiId);
        } else {
          this.deleteCollect(dataAssetApiId);
        }
      },
      onApply(item) {
        if (!this.hasApp()) {
          return;
        }

        if (this.dataAccessApp.status === 'CREATED') {
          this.$confirm('您的接入还没有审批!', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          })
            .then(res => {
              console.log('用户取消操作', res);
            })
            .catch(res => {
              console.log('用户取消操作', res);
            });
        } else {
          this.dialog.app = true;
          this.apiInfo = item;
        }
      },
      getApiUseTimes() {
        return index.getApiUseTimes({ accessAppId: this.dataAccessAppId }).done(res => {
          this.card.forEach(cardItem => {
            const resultItem = res.data.find(item => item.primaryName === cardItem.apiName);
            if (resultItem) {
              cardItem.resultValue = resultItem.resultValue || 0;
            } else {
              cardItem.resultValue = 0;
            }
          });
          this.cardFilter = [...this.card];
        });
      },
      getApiGroup() {
        index
          .getApiGroup()
          .done(res => {
            this.apiGroupList.list = this.setTreePath([
              {
                children: res.data || [],
                groupName: '全部',
                id: -1,
              },
            ]);
          })
          .always(() => {
            this.apiGroupList.loading = false;
          });
      },
      setTreePath(tree = []) {
        return tree.map(item => {
          const children = this.setTreePath(item.children);
          const path = this.getPath(item);
          return {
            ...item,
            children,
            path,
          };
        });
      },
      getPath(item) {
        const childrenPath = [];
        item.children.forEach(_item => {
          childrenPath.push(...this.getPath(_item));
        });
        return [...childrenPath, ...(item.id > 0 ? [item.id] : [])];
      },
      getCardList() {
        this.loading = true;
        return index
          .getCardList({ appId: this.dataAccessAppId })
          .done(res => {
            res.data.forEach(item => {
              if (item.approval === 0 && item.created === 0) {
                item.approvalStatus = '申请';
              } else if (item.approval === 1) {
                item.approvalStatus = '已申请';
              } else {
                item.approvalStatus = '待审批';
              }
            });
            store.commit('setCardList', res.data);
            this.card = res.data;
            this.card.sort(function (a, b) {
              // 按照时间降序
              return Date.parse(b.updateTime) - Date.parse(a.updateTime);
            });
            this.cardFilter = [...this.card];
            return this.getApiUseTimes();
          })
          .always(() => {
            this.loading = false;
          });
      },
      onSaved() {
        this.handleGroup('');
        this.getCardList();
      },
      onSizeChange(val) {
        this.page.pageSize = val;
      },
      onCurrentChange(val) {
        this.page.pageNo = val;
      },
    },
  };
</script>

<style scoped lang="less">
  .bd-page {
    line-height: 0 !important;
  }
  .home {
    width: 100%;
    display: flex;
    font-size: 14px;
    color: #333333;

    .left-container {
      height: calc(100vh - 162px);
    }

    .nav {
      box-sizing: border-box;
      border-right: 1px solid #e6e6e6;
      width: 245px;
      position: relative;
      overflow: hidden;

      .list-theme {
        padding-top: 20px;
        right: 0;
      }
      .theme-title {
        font-size: 16px;
        padding: 20px 0px;
        text-align: center;
        font-weight: bold;
      }
      .theme-item {
        cursor: pointer;
        height: 50px;
        line-height: 50px;
        font-size: 13px;
        width: 220px;
        position: relative;
        margin: 0 10px 10px 10px;
        background-color: #f3f6f8;

        .theme-name {
          display: inline-block;
          width: 100%;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          padding: 0 60px 0 40px;
          box-sizing: border-box;
        }

        .num {
          position: absolute;
          right: 30px;
          top: 0;
          text-overflow: ellipsis;
          white-space: nowrap;
          overflow: hidden;
          box-sizing: border-box;
          width: 27px;
          text-align: center;
        }

        &:hover {
          background-color: #f5f7fa;
        }

        &.is-active {
          background-color: #e1f2ff;
          color: #2776fb;
          font-weight: bold;
        }
      }
    }

    .card-list {
      width: calc(100% - 245px);
      .bd-search {
        background: #f3f6f8;
        padding: 20px;
        margin: 20px 20px 0px 20px;
        .search-label {
          margin-right: 10px;
        }
      }
      /deep/.el-scrollbar__wrap {
        margin-bottom: 0 !important;
        /deep/.el-pagination {
          margin-bottom: 30px;
        }
      }

      .list-content {
        margin: 20px;
        box-sizing: border-box;
      }
      .filter {
        margin-top: 20px;
        height: 40px;
        line-height: 40px;
        padding-left: 20px;
        /deep/.el-radio-group {
          .is-active {
            color: #fff !important;
            .el-dropdown {
              color: #fff !important;
            }
          }
        }
      }
    }

    .right-scroll {
      height: calc(~'100vh - 250px');
      /deep/ .el-scrollbar__wrap {
        overflow-x: hidden;
      }
    }
    .left-scroll {
      height: calc(~'100vh - 252px');
      /deep/ .el-scrollbar__wrap {
        overflow-x: hidden;
      }
    }
  }
  ul {
    width: 70px !important;
    text-align: center;
  }
</style>
