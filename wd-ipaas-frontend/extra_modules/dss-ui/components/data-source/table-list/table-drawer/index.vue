<template>
  <el-drawer
    :title="info.params.tableName + '-' + (info.type === 'detail' ? '表详情' : '预览数据')"
    :visible.sync="show"
    direction="rtl"
    size="75%"
    @open="openDrawer"
  >
    <div class="drawer" v-loading="loading">
      <el-scrollbar>
        <div class="content" v-if="info.type === 'detail'">
          <el-table
            v-loading="table.loading"
            :data="table.list.slice((params.pageNo - 1) * params.pageSize, params.pageNo * params.pageSize)"
            style="width: 100%"
            class="dss-table bd-table"
          >
            <el-table-column v-for="(val, key) in detailColMap" :key="key" :prop="key" :label="val"></el-table-column>
          </el-table>
        </div>
        <div class="content" v-else>
          <el-table
            v-loading="table.loading"
            :data="table.list.slice((params.pageNo - 1) * params.pageSize, params.pageNo * params.pageSize)"
            style="width: 100%"
            class="dss-table bd-table"
          >
            <el-table-column v-for="col in previewCol" :key="col" :prop="col" :label="col"></el-table-column>
          </el-table>
          <div class="bd-pagination">
            <el-pagination
              layout="prev, pager, next, sizes, total"
              :total="table.totalCount"
              :current-page="params.pageNo"
              :page-size="params.pageSize"
              :page-sizes="[10, 20, 30, 40, 50, 100]"
              @size-change="onSizeChange"
              @current-change="onCurrentChange"
            />
          </div>
        </div>
      </el-scrollbar>
      <div class="footer">
        <el-button @click="closeDrawer">关闭</el-button>
      </div>
    </div>
  </el-drawer>
</template>

<script>
import sourceApi from '../../../api/dataSource.js';
import { Drawer as ElDrawer } from 'element-ui';

export default {
  components: { ElDrawer },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    info: {
      type: Object,
      default: () => {
        return {
          type: '',
          params: {
            dataSourceId: null,
            table: '',
          },
        };
      },
    },
  },
  data() {
    return {
      loading: false,
      params: {
        pageNo: 1,
        pageSize: 10,
      },
      table: {
        list: [],
        totalCount: 0,
        loading: false,
      },
      detailColMap: {
        colName: '字段名',
        dataType: '类型',
        length: '长度',
        scale: '小数点',
        nullable: '允许空值',
        comment: '注释',
      },
      previewCol: [],
    };
  },
  computed: {
    show: {
      get() {
        return this.visible;
      },
      set(val) {
        this.$emit('update:visible', val);
      },
    },
  },
  methods: {
    openDrawer() {
      this.params = {
        pageNo: 1,
        pageSize: 10,
      };

      if (this.info.type === 'detail') {
        this.getTableDetail();
      } else {
        this.getTablePreview();
      }
    },
    getTableDetail() {
      this.table.loading = true;
      this.table.list = [];
      sourceApi
        .queryTableColumn(this.info.params)
        .done(res => {
          this.table.list = (res.data || []).map(item => {
            if (typeof item.nullable === 'boolean') {
              item.nullable = item.nullable.toString();
            }
            return item;
          });
        })
        .always(() => {
          this.table.loading = false;
        });
    },
    getTablePreview() {
      this.table.loading = true;
      this.table.list = [];
      this.table.totalCount = 0;
      const params = Object.assign({}, this.params, this.info.params);
      sourceApi
        .previewTable(params)
        .done(res => {
          this.table.list = res.data || [];
          // this.table.totalCount = res.totalCount || 0;
          this.table.totalCount = this.table.list.length || 0;
          this.getPreviewCol();
        })
        .always(() => {
          this.table.loading = false;
        });
      
    },
    getPreviewCol() {
      const row = this.table.list[0] || {};
      this.previewCol = Object.keys(row);
    },
    closeDrawer() {
      this.show = false;
    },
    onSizeChange(val) {
      this.params.pageSize = val;
      this.onCurrentChange(1);
    },
    onCurrentChange(val) {
      this.params.pageNo = val;
      this.getTablePreview();
    },
  },
};
</script>

<style lang="less" scoped>
@import '../../style.less';
.content {
  padding-bottom: 20px;
}
</style>