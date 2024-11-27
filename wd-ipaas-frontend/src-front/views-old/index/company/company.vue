<template>
  <div class="bd-page page-company" v-loading="loading">
    <div class="bd-header">
      <div class="header-content">
        <el-breadcrumb separator-class="el-icon-arrow-right">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>{{ groupName }}</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
    </div>

    <div class="bd-container">
      <div class="bd-search">
        <el-form size="mini" label-position="right" class="bd-form" inline>
          <el-form-item label="信息">
            <el-input v-model.trim="value" placeholder="请输入内容"></el-input>
          </el-form-item>
        </el-form>
        <div class="bd-search-group">
          <el-button type="primary" @click="onSearch" class="bd-button bd-strong">查询</el-button>
        </div>
      </div>
      <div class="info">
        <div class="filter">
          <!-- <div class="frequency">
            <div class="title">更新频率</div>
            <el-form ref="forma" :model="form" label-width="20px">
              <el-form-item>
                <el-checkbox-group v-model="form.type" @change="handleChecked">
                  <el-checkbox
                    v-for="(item,index) in option"
                    :label="item.value"
                    :key="index"
                  >{{item.label}}</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
            </el-form>
          </div> -->
          <div class="format">
            <div class="title">接口格式</div>
            <el-form ref="formb" :model="form" label-width="20px">
              <el-form-item>
                <el-checkbox-group v-model="form.format" @change="handleChecked">
                  <el-checkbox label="JSON" name="format"></el-checkbox>
                  <el-checkbox label="XML" name="format"></el-checkbox>
                </el-checkbox-group>
              </el-form-item>
            </el-form>
          </div>
        </div>
        <emptyInfo v-if="(!pageList || pageList.length <= 0) && !loading" style="width: 100%">
          <div v-text="'哎呀，暂时没有数据'"></div>
        </emptyInfo>
        <div class="app-table" v-else>
          <div class="top">
            <el-checkbox :indeterminate="isIndeterminate" v-model="checkAll" @change="handleCheckAllChange"
              >全选</el-checkbox
            >
            <el-button type="primary" class="btnTwo" @click="onApplyMultiple(checkedCards)">批量申请</el-button>
          </div>
          <div style="margin: 15px 0"></div>
          <el-checkbox-group v-model="checkedCards" @change="handleCheckedCardChange">
            <el-checkbox
              v-for="item in pageList"
              :label="item.dataAssetApiId"
              :key="item.dataAssetApiId"
              :disabled="!(item.approval == 0 && item.created == 0)"
            >
              <list-item :item="item" @command="handleCommand"></list-item>
            </el-checkbox>
          </el-checkbox-group>
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
      </div>
    </div>
  </div>
</template>

<script>
  import Pager from '../../../bz-components/pager.vue';
  import IPagesize from '../../../mixins/i-pagesize.js';
  import index from '../../../api/index.js';
  import listItem from '../list-item.vue';
  import emptyInfo from '../../../components/empty-info.vue';
  export default {
    components: { Pager, listItem, emptyInfo },
    mixins: [IPagesize],

    data() {
      return {
        checkAll: false,
        value: null,
        totalCount: 0,
        cards: [],
        checkedCards: [],
        isIndeterminate: false,
        apiGroupId: null,
        groupName: null,
        loading: false,
        form: {
          type: [],
          format: [],
        },
        option: [
          { label: '年', value: 'YEAR' },
          { label: '月', value: 'MONTH' },
          { label: '周', value: 'WEEK' },
          { label: '日', value: 'DAY' },
          { label: '小时', value: 'HOUR' },
        ],
        updateFrequency: [],
        tableData: [1, 2, 3],
        apiList: [],
        // approvalStatus: null //存放申请状态
        filterApiList: [],

        //用来存放api列表用于过滤
      };
    },
    computed: {
      pageList() {
        const arr = this.filterApiList.slice(
          (this.page.pageNo - 1) * this.page.pageSize,
          this.page.pageNo * this.page.pageSize
        );
        return arr;
      },
      // filterApiList: {
      //   get() {//本地分页
      //   const arr = this.cards.slice((this.page.pageNo - 1) * this.page.pageSize, this.page.pageNo * this.page.pageSize);
      //   return arr;
      //   },
      //   set() {

      //   }
      // }
    },
    created() {
      this.apiGroupId = +this.$route.query.groupId;
      this.groupName = this.$route.query.groupName;
      this.getCardList();
    },
    methods: {
      getCardList() {
        this.loading = true;
        index
          .getCardList()
          .done(res => {
            // console.log(res);
            this.apiList = res.data;
            this.filterCardList();
          })
          .always(() => {
            this.loading = false;
          });
      },
      filterCardList() {
        this.cards = this.apiList.filter(item => {
          if (item.apiGroupId == this.apiGroupId) {
            return item;
          }
        });
        this.filterApiList = [...this.cards];
        this.totalCount = this.cards.length;
        this.filterApiList.forEach(item => {
          if (item.approval == 0 && item.created == 0) {
            item.approvalStatus = '申请';
          } else if (item.approval == 1) {
            item.approvalStatus = '已申请';
          } else {
            item.approvalStatus = '待审批';
          }
        });
        // console.log(this.filterApiList);
      },
      handleCommand(operation, obj) {
        if (!!this['on' + operation]) this['on' + operation](...obj);
      },
      onCollect(dataAssetApiId, collected) {
        if (collected == 0) {
          this.$confirm('确认收藏吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
            // center: true
          })
            .then(() => {
              index
                .addCollect({
                  dataAssetApiId,
                })
                .done(res => {
                  this.$message({
                    type: 'success',
                    message: '收藏成功',
                  });
                  this.getCardList();
                });
            })
            .catch(res => {
              console.log('用户取消操作', res);
            });
        } else {
          this.$confirm('确定取消该收藏吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
            // center: true
          })
            .then(() => {
              index.deleteCollect({ collectId: dataAssetApiId }).done(res => {
                this.$message({
                  type: 'success',
                  message: '取消收藏成功',
                });
                this.getCardList();
              });
            })
            .catch(res => {
              console.log('用户取消操作', res);
            });
        }
      },
      onApply(dataAssetApiId) {
        this.onApplyMultiple([dataAssetApiId]);
      },
      onApplyMultiple(dataAssetApiIds) {
        this.$confirm('确认申请吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
          // center: true
        })
          .then(() => {
            index.apply(dataAssetApiIds).done(res => {
              this.$message({
                type: 'success',
                message: '申请成功',
              });
              this.getCardList();
            });
          })
          .catch(res => {
            console.log('用户取消操作', res);
          });
      },
      onSearch() {
        // this.handleChecked();
        this.filterApiList = this.cards.filter(item => {
          // console.log(item)
          return (
            item.apiName.toUpperCase().indexOf(this.value.toUpperCase()) !== -1 || this.value === ''
            // (item.apiName.indexOf(this.value) !== -1 || this.value === '')
          );
        });
      },
      handleSelectionChange() {
        console.log('选择框触发了');
      },
      handleChecked() {
        this.filterApiList = this.cards.filter(item => {
          // console.log(item)
          return (
            (this.form.type.indexOf(item.updateFrequency) !== -1 || this.form.type.length === 0) &&
            (this.form.format.indexOf(item.responseContentType) !== -1 || this.form.format.length === 0)
            // (item.apiName.toUpperCase().indexOf(this.value.toUpperCase()) !== -1 || this.value === '')
            // (item.apiName.indexOf(this.value) !== -1 || this.value === '')
          );
        });
      },
      handleCheckedCardChange(value) {
        let checkedCount = value.length;
        this.checkAll = checkedCount === this.cards.length;
        this.isIndeterminate = checkedCount > 0 && checkedCount < this.cards.length;
      },
      handleCheckAllChange(val) {
        if (!!val) {
          let result = [];

          this.filterApiList.forEach(item => {
            if (item.approval == 0 && item.created == 0) {
              result.push(item.dataAssetApiId);
            }
          });

          this.checkedCards = result;
          this.isIndeterminate = false;
        } else {
          this.checkedCards = [];
          this.isIndeterminate = false;
        }
      },
      onSizeChange(val) {
        this.page.pageSize = val;
        this.getCardList();
      },
      onCurrentChange(val) {
        this.page.pageNo = val;
        this.getCardList();
      },
    },
  };
</script>

<style scoped lang="less">
  .page-company {
    .app-table {
      background: white;
      padding-bottom: 15px;
      .top {
        display: flex;
        justify-content: space-around;
        .btnTwo {
          margin-right: 30px;
        }
      }
    }
    width: 100%;
    margin-bottom: 50px;
    .bd-header {
      background-color: #fff;
      display: flex;
      align-items: center;
      position: fixed;
      top: 52px;
      .header-content {
        width: calc(100% - 80px);
        max-width: 1200px;
        margin: 0 auto;
      }
    }
    .bd-container {
      width: calc(100% - 80px);
      max-width: 1200px;
      margin: 80px auto;
      background-color: #fff;
      .info {
        display: flex;
        padding: 20px;
        .filter {
          width: 180px;
          height: 100%;
          border: 1px solid #ccc;
          box-sizing: border-box;
          /deep/.el-checkbox {
            width: 100%;
            margin-right: 0;
          }
          /deep/.el-checkbox__label {
            width: 130px !important;
            font-size: 12px;
          }
          .title {
            font-size: 16px;
            font-weight: 600;
            color: #333;
            padding: 14px 20px;
            background: #f3f6f8;
          }
        }
        .app-table {
          width: calc(100% - 180px);
          margin-left: 20px;
          /deep/.el-table {
            td {
              border-bottom: none;
            }
          }
        }
      }
    }
    /deep/.el-checkbox {
      width: 95%;
    }
    /deep/.el-checkbox__label {
      width: 100%;
    }
  }
</style>
