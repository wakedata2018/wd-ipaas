<template>
  <div>
    <el-button class="add-btn" type="primary" plain icon="el-icon-plus" size="mini" @click="onAdd">添加</el-button>
    <el-table :data="tableData" style="width: 100%" max-height="300px" border class="flow-table">
      <el-table-column :key="column.prop" :prop="column.prop" :label="column.label" v-for="column in columnsList">
        <template #default="{ row }"><el-input v-model="row[column.prop]" maxlength="2000" /></template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="70px">
        <template #default="{ row, $index }">
          <el-button class="bd-button bd-table-danger" @click="onDelete($index)" size="mini">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
export default {
  name: 'DataInParams',
  components: {},
  props: {
    params: {
      type: Object,
    },
    item: {
      type: Object,
      default: _ => ({}),
    },
    refresh: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      tableData: [],
    };
  },
  computed: {
    columnsList() {
      const result = [];
      const arr = this.item.candidateValues.split(',');
      arr.forEach(item => {
        const keys = item.split(':');
        result.push({
          prop: keys[1],
          label: keys[0],
        });
      });
      return result;
    },
  },
  watch: {
    refresh: {
      immediate: true,
      handler() {
        this.initTable();
      },
    },
    tableData: {
      deep: true,
      handler(val) {
        const str = val ? JSON.stringify(val) : '[]';
        this.params[this.item.prop] = str;
      },
    },
  },
  methods: {
    initTable() {
      const str = this.params[this.item.prop];
      this.tableData = str ? JSON.parse(str) : [];
    },
    onDelete(index) {
      this.tableData.splice(index, 1);
      this.$emit('clear-validate');
    },
    onAdd() {
      const newObj = {};
      this.columnsList.forEach(item => {
        newObj[item.prop] = '';
      });
      this.tableData.push(newObj);
    },
  },
  mounted() {},
};
</script>
<style lang="less" scoped>
@import '../../../style.less';
.add-btn {
  margin-bottom: 5px;
  margin-right: 10px;
  color: #409eff;
  border-color: #409eff;
  background-color: transparent;
}
</style>
