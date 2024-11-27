<template>
  <!-- <div class="title" style="margin-bottom:20px;">返回数据设置</div> -->
  <el-table
    border
    :data="filterData"
    class="dss-table bd-table"
    :height="height"
  >
    <el-table-column
      prop
      label
      align="center"
      :render-header="renderCheckbox"
      v-if="hasCheck"
    >
      <template slot-scope="scope">
        <el-checkbox
          :disabled="reverse ? !scope.row.authorize : scope.row.authorize"
          v-model="scope.row.apply"
        />
      </template>
    </el-table-column>
    <el-table-column
      prop="datasourceTableColumnName"
      label="字段名称"
    ></el-table-column>
    <el-table-column
      prop="datasourceTableColumnType"
      label="字段类型"
    ></el-table-column>
    <el-table-column
      prop="datasourceTableColumnDesc"
      label="描述"
    ></el-table-column>
  </el-table>
</template>

<script>
export default {
  props: {
    data: {
      type: Array,
      default() {
        return [];
      },
    },
    hasCheck: {
      type: Boolean,
      default: true,
    },
    reverse: {
      type: Boolean,
      default: false,
    },
    keyword: {
      type: String,
      default: null,
    },
    height: {
      type: String,
      default: null,
    },
  },
  data() {
    return {
      isIndeterminate: false,
    };
  },
  computed: {
    isCheckedAll: {
      get() {
        // console.log(this.data)
        let checkeds = this.filterData.filter((item) => item.apply);
        let enableChecks = [];
        // if (this.reverse) {
        //   enableChecks = this.filterData.filter(item => item.authorize && item.apply);
        // } else {
        enableChecks = this.filterData.filter(
          (item) => !item.authorize && item.apply
        );
        // }
        this.isIndeterminate =
          enableChecks.length > 0 && checkeds.length < this.filterData.length;
        return checkeds.length === this.filterData.length;
      },
      set(val) {
        this.onCheckAllChange(val);
      },
    },
    filterData() {
      return this.data.filter(
        (item) =>
          !this.keyword ||
          this.keyword === "" ||
          (!!item.datasourceTableColumnName &&
            item.datasourceTableColumnName
              .toUpperCase()
              .indexOf(this.keyword.toUpperCase()) !== -1) ||
          (!!item.datasourceTableColumnType &&
            item.datasourceTableColumnType
              .toUpperCase()
              .indexOf(this.keyword.toUpperCase()) !== -1) ||
          (!!item.datasourceTableColumnDesc &&
            item.datasourceTableColumnDesc
              .toUpperCase()
              .indexOf(this.keyword.toUpperCase()) !== -1)
      );
    },
  },
  methods: {
    renderCheckbox(h) {
      return h("el-checkbox", {
        props: {
          value: this.isCheckedAll,
          indeterminate: this.isIndeterminate,
        },
        on: {
          change: this.onCheckAllChange,
        },
      });
    },
    onCheckAllChange(val) {
      this.filterData.forEach((item) => {
        // console.log(val)
        // if (this.reverse) {
        //   if (!item.authorize) {
        //     return;
        //   }
        // } else if (item.authorize) {
        //   return;
        // }
        item.apply = val;
      });
    },
  },
};
</script>