<template>
  <div class="transfer">
    <div class="header">{{title}}</div>
    <div class="transfer-body transfer-search transfer-page">
      <div class="search">
        <el-input class="search-input" v-model="keyword">
          <el-button slot="append" icon="el-icon-search" @click="onSearch"></el-button>
        </el-input>
      </div>
      <div class="content">
        <div v-for="user in list" :key="user.zusrid" class="user-item">
          <el-checkbox
            v-model="user.checked"
            @change="onChange($event, user)"
            :disabled="user.hasPermit"
          >
            <div>
              <div class="user-tip">{{user.zorgname}}</div>
              {{user.znachn}}
              <span class="user-tip">（{{user.zusrid}}）</span>
            </div>
          </el-checkbox>
        </div>
      </div>
      <div class="transfer-footer">
        <el-pagination
          :total="total"
          :page-size.sync="page.pageSize"
          :current-page="pageNo"
          :pager-count="5"
          layout="prev, pager, next"
          @current-change="onCurrentChange"
        ></el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
// import MPager from '../../mixins/m-pager.js';
// import userAPI from '@/dss-power/src/api/user.js';

export default {
  // mixins: [MPager],
  props: {
    title: {
      type: String,
      default: ''
    },
    list: {
      type: Array,
      default() {
        return [];
      }
    },
    requestFn: {
      type: Function,
      default: null
    },
    value: {
      type: Array,
      default() {
        return [];
      }
    },
    total: {
      type: Number,
      default: 0
    },
    page: {
      type: Object,
      default() {
        return {
          pageNo: 1,
          pageSize: 15
        };
      }
    }
  },
  data() {
    return {
      keyword: ''
    };
  },
  computed: {
    pageNo: {
      get() {
        return this.page.pageNo;
      },
      set(val) {
        this.page.pageNo = val;
        this.$emit('update:page', this.page);
      }
    }
  },
  methods: {
    onSearch() {
      this.$emit('search', this.keyword);
    },
    onCurrentChange(val) {
      this.pageNo = val;
      this.requestFn();
    },
    onChange(val, user) {
      this.$emit('input', this.list.filter(item => !item.hasPermit && item.checked));
    },
    clear() {
      this.keyword = '';
    }
  }
};
</script>

<style lang="less" scoped>
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


