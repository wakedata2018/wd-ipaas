<template>
  <el-scrollbar>
    <div class="content">
      <el-breadcrumb separator-class="el-icon-arrow-right" class="hdfs-breadcrumb">
        <!-- 面包屑 -->
        <el-breadcrumb-item :key="sourceId">
          <el-button
            type="text"
            :class="[{ 'current-breadcrumb-btn': !breadcrumbList.length }, 'breadcrumb-btn']"
            @click="handleChangeBreadcrumb(-1)"
            >{{ sourceName }}</el-button
          >
        </el-breadcrumb-item>
        <el-breadcrumb-item v-for="(item, index) in breadcrumbList" :key="item.id">
          <el-button
            type="text"
            :class="[{ 'current-breadcrumb-btn': breadcrumbList.length - 1 === index }, 'breadcrumb-btn']"
            @click="handleChangeBreadcrumb(index)"
            >{{ item.name }}</el-button
          >
        </el-breadcrumb-item>
      </el-breadcrumb>

      <!-- 搜索 -->
      <div class="bd-search">
        <el-form size="mini" label-width="80px" class="bd-form">
          <el-row>
            <div class="bd-search-group">
              <el-input
                v-model.trim="params.keyword"
                maxlength="50"
                clearable
                @clear="onSearch"
                @keyup.native.enter="onSearch"
              />
              <el-button type="primary" size="mini" class="bd-button bd-strong" @click="onSearch">查询</el-button>
            </div>
          </el-row>
        </el-form>
      </div>

      <!-- 表格数据列表 -->
      <el-table v-loading="table.loading" :data="table.list" style="width: 100%" class="dss-table bd-table">
        <el-table-column label="名称" show-overflow-tooltip>
          <template slot-scope="scope">
            <div
              :class="[{ 'tbl-name-folder': scope.row.dir === dirEnum.folder }, 'tbl-name-container']"
              @click="handleClick(scope.row)"
            >
              <img :src="iconEnum[scope.row.dir]" class="tbl-name-icon" />
              {{ scope.row.name }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="上传时间" show-overflow-tooltip>
          <div class="operation" slot-scope="scope">
            {{ scope.row.date }}
          </div>
        </el-table-column>
        <el-table-column label="类型" show-overflow-tooltip>
          <div class="operation" slot-scope="scope">
            {{ typeVal(scope.row.dir, scope.row.type) }}
          </div>
        </el-table-column>
        <el-table-column label="大小">
          <div class="operation" slot-scope="scope">
            {{ scope.row.dir === dirEnum.folder ? '--' : `${formatFileSize(scope.row.size)}` }}
          </div>
        </el-table-column>
      </el-table>
      <div class="bd-pagination">
        <el-pagination
          layout="prev, pager, next, sizes, total"
          :total="table.totalCount"
          :current-page="params.pageNo"
          :page-size="params.pageSize"
          @size-change="onSizeChange"
          @current-change="onCurrentChange"
        />
      </div>
    </div>
  </el-scrollbar>
</template>

<script>
import sourceApi from '../../api/dataSource.js';
import folderIcon from './images/folder.png';
import fileIcon from './images/file.png';

// 数据源类型对应数据接口
const sourceApiEnum = {
  ftp: sourceApi.queryFile,
  sftp: sourceApi.queryFile,
  hdfs: sourceApi.queryFile,
};

export default {
  props: {
    sourceId: Number,
    refresh: Boolean,
    sourceName: {
      type: String,
      default: '',
    },
    type: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      params: {
        keyword: '',
        pageNo: 1,
        pageSize: 10,
      }, // 获取列表默认参数
      table: {
        list: [],
        totalCount: 0,
        loading: false,
      }, // 表格数据
      breadcrumbList: [], // 面包屑数据
      dirEnum: {
        file: 0, // 文件类型
        folder: 1, // 文件夹类型
      },
      iconEnum: {
        0: fileIcon, // 文件图标
        1: folderIcon, // // 文件夹图标
      },
    };
  },
  computed: {
    typeVal() {
      return (dir, type) => {
        return dir === this.dirEnum.folder ? '文件夹' : type;
      };
    },
  },
  watch: {
    sourceId: {
      handler() {
        this.breadcrumbList = [];
        this.onReset();
      },
      immediate: true,
    },
    refresh() {
      this.onReset();
    },
  },

  methods: {
    onReset() {
      this.params = {
        keyword: '',
        pageNo: 1,
        pageSize: 10,
      };
      this.getTableList();
    },
    onSearch() {
      this.onSizeChange(10);
    },
    getTableList() {
      this.table.list = [];
      if (this.sourceId) {
        this.table.loading = true;
        // this.table.totalCount = 0;s
        const path = this.breadcrumbList.map(item => item.name).join('/');
        const params = { ...this.params, dataSourceId: this.sourceId, path: path ? `/${path}` : '' };
        if (['sftp', 'ftp'].includes(this.type)) {
          params.ssh = this.type === 'sftp';
        }
        sourceApiEnum[this.type](params)
          .done(
            res => {
              this.table.list = res.data || [];
              this.table.totalCount = res.totalCount || 0;
            },
            res => {
              this.table.totalCount = 0;
            }
          )
          .always(() => {
            this.table.loading = false;
          });
      } else {
        this.table.totalCount = 0;
      }
    },

    handleChangeBreadcrumb(index) {
      this.breadcrumbList = this.breadcrumbList.slice(0, index + 1);
      this.onReset();
    },

    handleClick(item) {
      if (item.dir === this.dirEnum.file) return;
      this.breadcrumbList.push({
        name: item.name,
      });
      this.onReset();
    },
    onSizeChange(val) {
      this.params.pageSize = val;
      this.onCurrentChange(1);
    },
    onCurrentChange(val) {
      this.params.pageNo = val;
      this.getTableList();
    },

    formatFileSize(size) {
      let number = size;
      let unit = 'B';
      let formatted = number + unit;
      if (!size && size !== 0) return '-';
      while (unit === 'YB' || number >= 1024) {
        number = new Number(number / 1024).toFixed(1);
        switch (unit) {
          case 'B':
            unit = 'KB';
            break;
          case 'KB':
            unit = 'MB';
            break;
          case 'MB':
            unit = 'GB';
            break;
          case 'GB':
            unit = 'TB';
            break;
          case 'TB':
            unit = 'PB';
            break;
          case 'PB':
            unit = 'EB';
            break;
          case 'EB':
            unit = 'ZB';
            break;
          case 'ZB':
            unit = 'YB';
            break;
        }
        formatted = number + unit;
      }
      return formatted;
    },
  },
};
</script>

<style lang="less" scoped>
@import '../style.less';
.el-scrollbar {
  height: 100%;
  /deep/ .el-scrollbar__wrap {
    height: calc(100% + 17px);
  }
}
.hdfs-breadcrumb {
  margin: 16px 16px 0px;
}
.breadcrumb-btn {
  font-size: 14px;
  padding: 0;
  color: #a1a4ab;

  &:active,
  &:focus,
  &:hover {
    color: #303133;
  }
}
.current-breadcrumb-btn {
  color: #303133;
}

.bd-search {
  padding: 24px;
  /deep/ .el-form {
    background: #f3f6f8;
    padding: 24px 0;
  }
}

.bd-search-group {
  display: flex;
  align-items: center;
  justify-content: center;
  .bd-button {
    margin-left: 20px;
  }
}

.dss-table {
  /deep/ .el-table th {
    border-top: 1px solid #ebeef5;
  }
}

.tbl-name-container {
  display: flex;
  align-items: center;
  .tbl-name-icon {
    width: 16px;
    height: 16px;
    margin-right: 5px;
  }
  &.tbl-name-folder {
    cursor: pointer;
  }
}
.content {
  padding-bottom: 20px;
}
</style>