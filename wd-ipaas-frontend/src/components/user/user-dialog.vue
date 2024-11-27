<template>
  <el-dialog
    :visible.sync="isShow"
    width="100%"
    class="user-dialog"
    :append-to-body="true"
    title="选择用户"
  >
    <el-row :gutter="20">
      <el-col :span="multiple ? 12 : 24">
        <div class="transfer" v-loading="loading">
          <div class="header">用户列表</div>
          <div class="transfer-body transfer-search transfer-page">
            <div class="search">
              <el-input class="search-input" v-model="keyword">
                <el-button slot="append" icon="el-icon-search" @click="onSearch"></el-button>
              </el-input>
            </div>
            <div class="content">
              <div v-for="user in userList" :key="user.id" class="user-item">
                <el-checkbox
                  v-model="user.checked"
                  @change="onChange($event, user)"
                  v-if="multiple"
                >
                  <div>
                    <!-- <div class="user-tip">{{user.displayName}}</div> -->
                    {{user.user}}
                    <!-- <span class="user-tip">（{{user.employeeNumber}}）</span> -->
                  </div>
                </el-checkbox>
                <div
                  v-else
                  @click="onChecked(user)"
                  :class="{'acitve-user': selected.findIndex(item => item.id === user.id) > -1}"
                >
                  <!-- <div class="user-tip">{{user.displayName}}</div> -->
                  {{user.user}}
                  <!-- <span class="user-tip">（{{user.employeeNumber}}）</span> -->
                </div>
              </div>
            </div>
            <div class="transfer-footer">
              <el-pagination
                :total="total"
                :page-size="page.pageSize"
                :current-page="page.pageNo"
                :pager-count="5"
                layout="prev, pager, next"
                @current-change="onCurrentChange"
              ></el-pagination>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="12" v-if="multiple">
        <div class="transfer">
          <div class="header">选中用户</div>
          <div class="transfer-body">
            <div class="content">
              <div v-for="user in selected" :key="user.id" class="user-item">
                <el-checkbox v-model="user.checked" @change="onChange($event, user)">
                  <div>
                    <!-- <div class="user-tip">{{user.displayName}}</div> -->
                    {{user.displayName}}
                    <!-- <span class="user-tip">（{{user.displayName}}）</span> -->
                  </div>
                </el-checkbox>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
    <div slot="footer" class="dialog-footer">
      <el-button @click="isShow = false">取 消</el-button>
      <el-button type="primary" @click="onApply">确 定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import MPager from '../../mixins/i-pagesize.js';
// import userAPI from '../../api/user.js';
import userAPI from '../../api/user-list.js';

export default {
  mixins: [MPager],
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    multiple: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      total:null,
      userList: [],
      selected: [],
      query: {
        keyword: ''
      },
      keyword: '',
      page: {
        pageSize: 15
      },
      loading: false
    };
  },
  computed: {
    isShow: {
      get() {
        return this.visible;
      },
      set(val) {
        this.$emit('update:visible', val);
      }
    }
  },
  mounted() {
    this.getList();
  },
  methods: {
    getList() {
      this.loading = true;
      const params = {
        ...this.page,
        ...this.query,
        crystalDetailId: 3
      };
      userAPI
        .listUser(params)
        .done(res => {
          // console.log(res)
          this.userList = this.format(res.data);
          this.total = res.totalCount;
        })
        .always(() => {
          this.loading = false;
        });
    },
    format(users) {
      users = users || [];
      users.forEach(user => {
        if (this.selected.findIndex(item => item.id === user.id) > -1) {
          user.checked = true;
        } else {
          user.checked = false;
        }
      });
      return users;
    },
    onChange(checked, user) {
      if (checked) {
        if (this.selected.findIndex(item => item.id === user.id) > -1) {
          // 已存在selected数组，不在添加
          return;
        }
        this.selected.push(user);
      } else {
        const finded = this.userList.find(item => item.id === user.id);
        if (finded) {
          finded.checked = false;
        }
        this.selected = this.selected.filter(item => item.id !== user.id);
      }
    },
    onSearch() {
      this.query.keyword = this.keyword;
      this.page.pageNo = 1;
      this.getList();
    },
    onApply() {
      this.$emit('ok', this.selected);
    },
    setSelected(users) {
      this.selected = users;
      this.format(this.userList);
    },
    onCurrentChange(page) {
      this.page.pageNo = page;
      this.getList();
    },
    clear() {
      this.keyword = '';
      this.query.keyword = this.keyword;
      this.page.pageNo = 1;
      this.getList();
    },
    onChecked(user) {
      this.$emit('ok', [user]);
    }
  }
};
</script>

<style lang="less" scoped>
.user-dialog {
  /deep/ .el-dialog {
    max-width: 700px;
  }
}
.transfer {
  border: 1px solid #ebeef5;
  .header {
    text-align: center;
    background: #f5f7fa;
    height: 40px;
    line-height: 40px;
    border-bottom: 1px solid #ebeef5;
  }

  .transfer-search {
    padding-top: 50px;
  }

  .transfer-page {
    padding-bottom: 40px;
  }

  .transfer-body {
    box-sizing: border-box;
    height: 450px;
    box-sizing: border-box;
    position: relative;
    // max-height: 400px;

    .search {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      padding: 5px 15px 10px;
      border-bottom: 1px solid #ebeef5;
    }

    .search-input {
      /deep/ input {
        border-top-left-radius: 20px;
        border-bottom-left-radius: 20px;
        // border-radius: 20px;
      }

      /deep/ .el-input-group__append {
        border-top-right-radius: 20px;
        border-bottom-right-radius: 20px;
      }
    }

    .content {
      overflow: auto;
      padding: 15px;
      height: 100%;
      box-sizing: border-box;
    }

    .user-item {
      border-bottom: 1px solid #e4e4e4;
      padding: 3px 0;
    }

    .acitve-user {
      color: #00a1c9;
    }

    /deep/ .el-checkbox {
      .el-checkbox__label {
        vertical-align: middle;
      }

      .user-tip {
        font-size: 12px;
        color: gray;
      }

      &.is-checked {
        .user-tip {
          color: #00a1c9;
        }
      }

      &.is-disabled {
        .user-tip {
          color: gray !important;
        }
      }
    }
  }

  .transfer-footer {
    border-top: 1px solid #ebeef5;
    height: 40px;
    line-height: 40px;
    box-sizing: border-box;
    padding-top: 5px;
  }
}
</style>


