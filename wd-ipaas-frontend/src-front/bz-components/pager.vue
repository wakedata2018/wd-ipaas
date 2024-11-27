<template>
  <el-pagination
    background
    style="margin-top: 10px;"
    layout=" prev, pager, next,jumper, sizes, total"
    :page-size="pageSize"
    :page-sizes="[7, 14, 21]"
    :current-page="currentPage"
    :total="total"
    @size-change="onSizeChange"
    @current-change="onCurrentChange"
  ></el-pagination>
</template>

<script>
export default {
  props: {
    requestFn: {
      type: Function,
      default: null
    },
    pageSize: {
      type: Number,
      default: 7
    },
    currentPage: {
      type: Number,
      default: 1
    },
    total: {
      type: Number,
      default: 0
    }
  },
  methods: {
    onSizeChange(size) {
      this.$emit('update:currentPage', 1);
      this.$emit('update:pageSize', size);
      this.requestFn();
    },
    onCurrentChange(page) {
      this.$emit('update:currentPage', page);
      this.requestFn();
    }
  }
};
</script>