<template>
  <div>
    <el-table border :data="filterData"  class="dss-table bd-table">
      <el-table-column
        prop
        label
        align="center"
        :render-header="renderCheckbox"
        width="50px"
        v-if="hasCheck"
        >
        <template slot-scope="scope">
          <el-checkbox
            :disabled="reverse ? !scope.row.authorize : scope.row.authorize"
            v-model="scope.row.apply"
          />
        </template>
      </el-table-column>
      <el-table-column prop="datasourceTableColumnName" label="字段名称"></el-table-column>
      <!-- <el-table-column prop="datasourceTableColumnType" label="字段类型"></el-table-column> -->
      <el-table-column prop="datasourceTableColumnDesc" label="描述"></el-table-column>
      <el-table-column prop="authorize" label="已有权限">
        <template slot-scope="scope">{{scope.row.authorize ? '是' : '否'}}</template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
export default {
  props: {
    data: {
      type: Array,
      default() {
        return [];
      }
    },
    hasCheck: {
      type: Boolean,
      default: true
    },
    reverse: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      isIndeterminate: false
    };
  },
  computed: {
    isCheckedAll: {
      get() {
        let checkeds = this.filterData.filter(item => item.apply);
        let enableChecks = [];
        if (this.reverse) {
          enableChecks = this.filterData.filter(item => item.authorize && item.apply);
        } else {
          enableChecks = this.filterData.filter(item => !item.authorize && item.apply);
        }
        this.isIndeterminate = enableChecks.length > 0 && checkeds.length < this.filterData.length;
        return !!this.filterData.length && checkeds.length === this.filterData.length;
      },
      set(val) {
        this.onCheckAllChange(val);
      }
    },
    filterData() {
      if (!this.reverse) {
        return this.data;
      }
      return this.data.filter(item => item.authorize);
    }
  },
  methods: {
    renderCheckbox(h) {
      return h('el-checkbox', {
        props: {
          value: this.isCheckedAll,
          indeterminate: this.isIndeterminate
        },
        on: {
          change: this.onCheckAllChange
        }
      });
    },
    onCheckAllChange(val) {
      this.filterData.forEach(item => {
        if (this.reverse) {
          if (!item.authorize) {
            return;
          }
        } else if (item.authorize) {
          return;
        }
        item.apply = val;
      });
    }
  }
};
</script>