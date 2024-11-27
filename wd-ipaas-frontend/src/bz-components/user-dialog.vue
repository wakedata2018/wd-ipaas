<template>
  <el-dialog
    :visible.sync="isShow"
    title="关联用户"
    width="100%"
    class="user-dialog"
    v-loading="loading.block"
  >
    <el-row :gutter="60">
      <el-col :span="12">
        <user-list
          title="用户"
          :list="allList"
          :page.sync="allQuery"
          :request-fn="getAll"
          v-model="grantList"
          :total="allTotal"
          ref="user"
          v-loading="loading.all"
          @search="onAllSearch"
        />
      </el-col>
      <el-col :span="12">
        <user-list
          title="已授权用户"
          :list="authedList"
          :page.sync="query"
          :request-fn="getAuthedList"
          v-model="cacelList"
          :total="total"
          v-loading="loading.authed"
          ref="userAuthed"
          @search="onAuthedSearch"
        />
      </el-col>
      <div class="btn-group">
        <div style="margin-bottom: 20px;">
          <el-tooltip content="授予用户权限" placement="top">
            <el-button
              type="primary"
              size="media"
              circle
              icon="el-icon-d-arrow-right"
              @click="onGrant"
            ></el-button>
          </el-tooltip>
        </div>
        <el-tooltip content="取消用户权限" placement="bottom">
          <el-button
            type="primary"
            size="media"
            circle
            icon="el-icon-d-arrow-left"
            @click="onCancelGrant"
          ></el-button>
        </el-tooltip>
      </div>
    </el-row>
    <div slot="footer" class="dialog-footer">
      <el-button @click="isShow = false">关闭</el-button>
    </div>
  </el-dialog>
</template>

<script>
// import MPager from '../../mixins/m-pager.js';
import userAPI from '../api/user.js';
import crystalAPI from '../api/crystal.js';
import UserList from './user-list.vue';

export default {
  // mixins: [MPager],
  components: { UserList },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    report: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      authedList: [],
      cacelList: [],
      allList: [],
      grantList: [],
      query: {
        pageNo: 1,
        pageSize: 15,
        keyword: ''
      },
      allQuery: {
        pageNo: 1,
        pageSize: 15,
        keyword: ''
      },
      allTotal: 0,
      total: 0,
      loading: {
        all: false,
        authed: false,
        block: false
      }
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
    },
    reportId() {
      if (!this.report) {
        return '';
      }
      return this.report.id;
    }
  },
  watch: {
    isShow(val) {
      if (val) {
        this.clearKeyword();
        this.getAll();
        this.getAuthedList();
      }
    }
  },
  methods: {
    getAll() {
      this.loading.all = true;
      this.grantList = [];
      const params = {
        ...this.allQuery,
        crystalDetailId: this.reportId
      };
      userAPI
        .list(params)
        .done(res => {
          this.allList = this.format(res.data);
          this.allTotal = res.totalCount;
        })
        .always(() => {
          this.loading.all = false;
        });
    },
    onAllSearch(keyword) {
      this.allQuery.keyword = keyword;
      this.allQuery.pageNo = 1;
      this.getAll();
    },
    getAuthedList() {
      this.cacelList = [];
      this.loading.authed = true;
      const params = {
        ...this.query,
        id: this.reportId
      };
      crystalAPI
        .showUsers(params)
        .done(res => {
          this.total = res.totalCount || 0;
          this.authedList = this.formatAuthedList(res.data);
        })
        .always(() => {
          this.loading.authed = false;
        });
    },
    onAuthedSearch(keyword) {
      this.query.keyword = keyword;
      this.query.pageNo = 1;
      this.getAuthedList();
    },
    onGrant() {
      if (this.grantList.length === 0) {
        this.$message('请选择要授权的用户。');
        return;
      }
      this.loading.block = true;
      const userIds = this.grantList.map(user => user.zusrid).join(',');
      crystalAPI
        .userJoin(this.reportId, userIds)
        .done(res => {
          this.refresh();
        })
        .always(() => {
          this.loading.block = false;
        });
    },
    onCancelGrant() {
      if (this.cacelList.length === 0) {
        this.$message('请选择要取消授权的用户。');
        return;
      }
      this.loading.block = true;
      const userIds = this.cacelList.map(user => user.zusrid).join(',');
      crystalAPI
        .userUnjoin(this.reportId, userIds)
        .done(res => {
          this.refresh();
        })
        .always(() => {
          this.loading.block = false;
        });
    },
    format(list) {
      const newList = list || [];
      newList.forEach(user => {
        if (user.hasPermit) {
          user.checked = true;
          return;
        }
        user.checked = false;
      });
      return newList;
    },
    formatAuthedList(list) {
      const newList = list || [];
      newList.forEach(user => {
        user.hasPermit = false;
        user.checked = false;
      });
      return newList;
    },
    refresh() {
      this.getAll();
      this.query.pageNo = 1;
      this.getAuthedList();
    },
    clearKeyword() {
      this.query.keyword = '';
      this.allQuery.keyword = '';
      const cmpUser = this.$refs.user;
      const cmpUserAuthed = this.$refs.userAuthed;
      if (cmpUser) {
        cmpUser.clear();
      }
      if (cmpUserAuthed) {
        cmpUserAuthed.clear();
      }
    }
  }
};
</script>

<style lang="less" scoped>
.user-dialog {
  /deep/ .el-dialog {
    max-width: 850px;
  }

  .btn-group {
    position: absolute;
    left: 50%;
    transform: translateX(-50%);
    top: 160px;
  }
}
</style>


