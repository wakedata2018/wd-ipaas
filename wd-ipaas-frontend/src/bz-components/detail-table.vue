<template>
  <div>
    <el-table :data="filterData" class="dss-table bd-table detail-table">
      <el-table-column v-if="hasCheck" prop="parameter" label width="150" :render-header="renderCheckbox">
        <template #default="scope">
          <el-checkbox
            v-model="scope.row.parameter"
            :disabled="selectedSource.dbType === 'HBASE' || isReadonly || isDisableCol(scope.row)"
            @change="changeParameter(scope.row)"
          />
        </template>
      </el-table-column>

      <el-table-column
        v-if="hasCheck && setType === QUERY_OPERATE.value"
        prop="result"
        label
        width="150"
        :render-header="renderCheckboxa"
      >
        <template #default="scope">
          <el-checkbox
            v-model="scope.row.result"
            :disabled="isReadonly || isDisableCol(scope.row)"
            @change="changeResult(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column
        v-if="hasCheck && setType === UPDATE_OPERATE.value"
        prop="filter"
        label
        width="150"
        :render-header="renderCheckEdit"
      >
        <template #default="scope">
          <el-checkbox v-model="scope.row.filter" :disabled="isReadonly" @change="changeFilter(scope.row)" />
        </template>
      </el-table-column>
      <el-table-column prop="assetColumns" label="列名称"></el-table-column>
      <el-table-column prop="assetDatatype" label="数据类型"></el-table-column>
      <el-table-column prop="colDescriptions" label="列描述"></el-table-column>
      <el-table-column prop="sample" label="示例值">
        <template #default="scope">
          <el-input
            v-model="scope.row.sample"
            :disabled="!isEdit(scope.row) || isReadonly"
            @input="changeValue(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column prop="descriptions" label="描述">
        <template #default="scope">
          <el-input
            v-model="scope.row.descriptions"
            :disabled="!isEdit(scope.row) || isReadonly"
            @input="changeValue(scope.row)"
          />
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
  import { INSERT_OPERATE, QUERY_OPERATE, UPDATE_OPERATE } from '@/enum';

  export default {
    props: {
      data: {
        type: Array,
        default() {
          return [];
        },
      },
      results: {
        type: Array,
        default() {
          return [];
        },
      },
      parameters: {
        type: Array,
        default() {
          return [];
        },
      },
      filters: {
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
        default: '',
      },
      dataAssetApiId: {
        type: Number,
        default: null,
      },
      selectedSource: {
        type: Object,
        default: () => ({}),
      },
      isReadonly: {
        type: Boolean,
        default: false,
      },
      setType: {
        type: String,
        default: INSERT_OPERATE.value,
      },
    },
    data() {
      return {
        QUERY_OPERATE,
        UPDATE_OPERATE,
        isIndeterminate: false,
        isIndeterminatea: false,
      };
    },
    computed: {
      /**
       * 选择方式为空时，参数值不可输入
       */
      disableSelectParams() {
        return row => {
          return !row.paramValueType;
        };
      },
      isDisableCol() {
        return row => {
          const disabledList = ['pageNo', 'pageSize', 'orderBy'];
          // 操作类型为 新增 修改时，主键不能设置成请求参数
          if ((this.setType === INSERT_OPERATE.value || this.setType === UPDATE_OPERATE.value) && row.isAutoIncrement) {
            disabledList.push(row.assetColumns);
          }
          return disabledList.includes(row.assetColumns);
        };
      },
      isCheckedAll: {
        get() {
          const checkeds = this.filterData.filter(item => item.parameter);
          let enableChecks = [];

          enableChecks = this.filterData.filter(item => item.parameter);

          // eslint-disable-next-line vue/no-side-effects-in-computed-properties
          this.isIndeterminate = enableChecks.length > 0 && checkeds.length < this.filterData.length;
          return checkeds.length > 0 && checkeds.length === this.filterData.length;
        },
        set(val) {
          this.onCheckAllChange(val);
        },
      },
      isCheckedAlla: {
        get() {
          const checkeds = this.filterData.filter(item => item.result);
          let enableChecks = [];

          enableChecks = this.filterData.filter(item => item.result);

          // eslint-disable-next-line vue/no-side-effects-in-computed-properties
          this.isIndeterminatea = enableChecks.length > 0 && checkeds.length < this.filterData.length;
          return checkeds.length > 0 && checkeds.length === this.filterData.length;
        },
        set(val) {
          this.onCheckAllChange(val);
        },
      },
      isCheckedAllFilter: {
        get() {
          const checkeds = this.filterData.filter(item => item.filter);
          let enableChecks = [];

          enableChecks = this.filterData.filter(item => item.filter);

          // eslint-disable-next-line vue/no-side-effects-in-computed-properties
          this.isIndeterminatea = enableChecks.length > 0 && checkeds.length < this.filterData.length;
          return checkeds.length > 0 && checkeds.length === this.filterData.length;
        },
        set(val) {
          this.onCheckAllChangeFilter(val);
        },
      },
      filterData() {
        return this.tableData.filter(
          item =>
            !this.keyword ||
            this.keyword === '' ||
            (!!item.assetColumns && item.assetColumns.toUpperCase().indexOf(this.keyword.toUpperCase()) !== -1) ||
            (!!item.assetDatatype && item.assetDatatype.toUpperCase().indexOf(this.keyword.toUpperCase()) !== -1) ||
            (!!item.descriptions && item.descriptions.toUpperCase().indexOf(this.keyword.toUpperCase()) !== -1)
        );
      },
      tableData: {
        /**
         * 数据组装
         * 示例值和描述从 请求的 result parameters filter拿
         */
        get() {
          return this.data.map(item => {
            const result = this.results.find(itema => itema.assetColumns === item.datasourceTableColumnName);
            const parameter = this.parameters.find(itema => itema.assetColumns === item.datasourceTableColumnName);
            const filter = this.filters.find(itema => itema.assetColumns === item.datasourceTableColumnName);
            const other = {
              descriptions:
                result?.descriptions ||
                parameter?.descriptions ||
                filter?.descriptions ||
                item.datasourceTableColumnDesc,
              sample: result?.sample || parameter?.sample || filter?.sample || '',
              isAutoIncrement: item.isAutoIncrement,
              required: item.required,
            };

            return window.$.extend(
              true,
              {},
              {
                dataAssetId: this.dataAssetApiId,
                assetColumns: item.datasourceTableColumnName,
                assetDatatype: item.datasourceTableColumnType,
                colDescriptions: item.datasourceTableColumnDesc,
                assetColumnsLength: item.datasourceTableColumnLength,
                result: !!result,
                parameter: !!parameter,
                filter: !!filter,
                ...other,
              }
            );
          });
        },
        set(val) {
          const results = [];
          const parameters = [];
          const filters = [];

          val.forEach(item => {
            if (item.result) {
              results.push(item);
            }
            if (item.parameter) {
              parameters.push(item);
            }
            if (item.filter) {
              filters.push(item);
            }
          });
          this.$emit('change-selection', results, parameters, filters);
        },
      },
      isEdit() {
        return row => {
          return row.result || row.filter || row.parameter;
        };
      },
    },
    methods: {
      modeChange(row) {
        this.$set(row, 'expression', '');
      },
      selectParams(row) {
        this.$emit('select-params', data => {
          this.$set(row, 'expression', data.id);
        });
      },
      selectMethod(row) {
        this.$emit('select-method', row.expression, data => {
          this.$set(row, 'expression', data);
        });
      },
      renderCheckbox(h) {
        return h(
          'el-checkbox',
          {
            props: {
              value: this.isCheckedAll,
              disabled: this.selectedSource.dbType === 'HBASE' || this.isReadonly,
              indeterminate: this.isIndeterminate,
            },
            on: {
              change: this.onCheckAllChange,
            },
          },
          [h('strong', {}, ['设为请求参数'])]
        );
      },
      renderCheckboxa(h) {
        return h(
          'el-checkbox',
          {
            props: {
              value: this.isCheckedAlla,
              disabled: this.isReadonly,
              indeterminate: this.isIndeterminatea,
            },
            on: {
              change: this.onCheckAllChangea,
            },
          },
          [h('strong', {}, ['设为返回参数'])]
        );
      },
      renderCheckEdit(h) {
        return h(
          'el-checkbox',
          {
            props: {
              value: this.isCheckedAllFilter,
              disabled: this.isReadonly,
              indeterminate: this.isIndeterminatea,
            },
            on: {
              change: this.onCheckAllChangeFilter,
            },
          },
          [h('strong', {}, ['设为过滤条件'])]
        );
      },
      renderSelectboxa(h) {
        return h(
          'el-checkbox',
          {
            props: {
              value: this.isCheckedAlla,
              disabled: this.isReadonly,
              indeterminate: this.isIndeterminatea,
            },
            on: {
              change: this.onCheckAllChangea,
            },
          },
          [h('strong', {}, ['全选'])]
        );
      },
      onCheckAllChange(val) {
        if (!val) {
          const listName = ['pageNo', 'pageSize', 'orderBy'];
          const newFilter = this.parameters.filter(item => listName.includes(item.assetColumns));
          this.$emit('change-selection', this.results, newFilter, this.filters);
        } else {
          // 操作类型为新增/修改时，主键不可勾选(设置成请求参数)
          const newFilter = this.filterData.filter(
            item =>
              !(
                (this.setType === INSERT_OPERATE.value || this.setType === UPDATE_OPERATE.value) &&
                item.isAutoIncrement
              )
          );
          this.$emit('change-selection', this.results, newFilter, this.filters);
        }
      },
      onCheckAllChangea(val) {
        if (!val) {
          this.$emit('change-selection', [], this.parameters, this.filters);
        } else {
          const listName = ['pageNo', 'pageSize', 'orderBy'];
          const newFilter = this.filterData.filter(item => !listName.includes(item.assetColumns));
          this.$emit('change-selection', newFilter, this.parameters, this.filters);
        }
      },
      onCheckAllChangeFilter(val) {
        if (!val) {
          this.$emit('change-selection', this.results, this.parameters, []);
        } else {
          this.$emit('change-selection', this.results, this.parameters, this.filterData);
        }
      },
      changeParameter(row) {
        if (row.parameter) {
          this.$emit('change-selection', this.results, [...this.parameters, row], this.filters);
        } else {
          this.$emit(
            'change-selection',
            this.results,
            this.parameters.filter(item => item.assetColumns !== row.assetColumns),
            this.filters
          );
        }
      },
      changeResult(row) {
        if (row.result) {
          this.$emit('change-selection', [...this.results, row], this.parameters, this.filters);
        } else {
          this.$emit(
            'change-selection',
            this.results.filter(item => item.assetColumns !== row.assetColumns),
            this.parameters,
            this.filters
          );
        }
      },
      changeFilter(row) {
        if (row.filter) {
          this.$emit('change-selection', this.results, this.parameters, [...this.filters, row]);
        } else {
          this.$emit(
            'change-selection',
            this.results,
            this.parameters,
            this.filters.filter(item => item.assetColumns !== row.assetColumns)
          );
        }
      },
      changeValue(row) {
        const params = {
          filter: this.filters,
          parameter: this.parameters,
          result: this.results,
        };
        if (row.filter || row.result || row.parameter) {
          if (row.filter) {
            const index = this.filters.findIndex(item => item.assetColumns === row.assetColumns);
            params.filter[index] = {
              ...this.filters[index],
              sample: row.sample,
              descriptions: row.descriptions,
            };
          }
          if (row.result) {
            const index = this.results.findIndex(item => item.assetColumns === row.assetColumns);
            params.result[index] = {
              ...this.results[index],
              sample: row.sample,
              descriptions: row.descriptions,
            };
          }
          if (row.parameter) {
            const index = this.parameters.findIndex(item => item.assetColumns === row.assetColumns);
            params.parameter[index] = {
              ...this.parameters[index],
              sample: row.sample,
              descriptions: row.descriptions,
            };
          }
          this.$emit('change-selection', params.result, params.parameter, params.filter);
        }
      },
    },
  };
</script>
<style lang="less" scoped>
  .detail-table {
    .el-input {
      width: 100% !important;
    }

    .map-value {
      flex: 1;
      position: relative;
      display: flex;
      align-items: center;

      & /deep/ .el-input__inner {
        padding-right: 24px;
      }

      .expression-setting {
        cursor: pointer;
        position: absolute;
        padding: 0px 3px;
        right: 3px;
        background: #fff;
      }
    }

    .select-params {
      width: 100%;
      border-radius: 4px;
      border: 1px solid #dcdfe6;
      text-align: left;
      height: 28px;
      padding: 0 15px;
    }
  }
</style>
