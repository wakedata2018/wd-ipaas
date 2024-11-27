<template>
  <div class="edit-branch-condition-table">
    <el-table :data="tableData" class="dss-table bd-table">
      <el-table-column label="条件名称" min-width="100px" prop="id">
        <template #default="{ row }">
          <el-input v-model="row.id" maxlength="30" placeholder="不超过30个字符" />
        </template>
      </el-table-column>

      <el-table-column label="比较值1" min-width="140px" prop="value1.dataType">
        <template #default="{ row }">
          <el-cascader
            v-model="row.value1.dataType"
            :options="CASCADER_DATA_TYPE_OPTIONS"
            :show-all-levels="false"
            :props="{ expandTrigger: 'hover' }"
          >
          </el-cascader>
        </template>
      </el-table-column>

      <el-table-column min-width="300px">
        <template #default="{ row }">
          <SetParamsValue v-model="row.value1" :task-info="taskInfo" :cur-node="curNode" />
        </template>
      </el-table-column>

      <el-table-column label="比较类型" min-width="120px" prop="operator">
        <template #default="{ row }">
          <el-select v-model="row.operator" @change="handleOperator(row)">
            <el-option
              v-for="key in Object.keys(EXPRESSION_TYPE_MAP)"
              :key="key"
              :label="EXPRESSION_TYPE_MAP[key]"
              :value="key"
            ></el-option>
          </el-select>
        </template>
      </el-table-column>

      <el-table-column label="比较值2" min-width="140px" prop="value2.dataType">
        <template #default="{ row }">
          <el-cascader
            v-if="hideValue2(row.operator)"
            v-model="row.value2.dataType"
            :options="CASCADER_DATA_TYPE_OPTIONS"
            :show-all-levels="false"
            :props="{ expandTrigger: 'hover' }"
          >
          </el-cascader>
        </template>
      </el-table-column>

      <el-table-column min-width="300px">
        <template #default="{ row }">
          <SetParamsValue
            v-if="hideValue2(row.operator)"
            v-model="row.value2"
            :task-info="taskInfo"
            :cur-node="curNode"
          />
        </template>
      </el-table-column>

      <el-table-column label="操作" fixed="right">
        <template #default="{ row, $index }">
          <el-button type="danger" plain @click="onDelete($index, row._conditionId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
  import { EXPRESSION_TYPE_MAP } from '@/enum';
  import { CASCADER_DATA_TYPE_OPTIONS } from '@/components/json-schema-editor/type/type';
  import { getInitForm } from '../../global/task-conf';
  import SetParamsValue from '../components/SetParamsValue.vue';

  const targetArr = ['isNull', 'isNotNull', 'isEmpty', 'isNotEmpty']; // 不需要输入value2的值

  export default {
    name: 'EditBranchConditionTable',
    components: { SetParamsValue },
    props: {
      tableData: {
        type: Array,
        default: () => {
          return [];
        },
      },
      curNode: {
        type: Object,
        default: null,
      },
      taskInfo: {
        type: Object,
        default: () => getInitForm(),
      },
    },
    data() {
      return {
        EXPRESSION_TYPE_MAP,
        CASCADER_DATA_TYPE_OPTIONS,
        defaultProps: {
          children: 'childApiRespParams',
          label: 'assetColumns',
        },
      };
    },
    methods: {
      handleOperator(row) {
        if (this.hideValue2(row.operator)) {
          this.$set(row, 'value2', {
            disabled: true,
            type: '',
            dataType: '',
            expression: '',
          });
        } else {
          row.value2.disabled = false;
        }
      },
      hideValue2(operator) {
        return !targetArr.includes(operator);
      },
      validateConditions() {
        for (const condition of this.tableData) {
          if (
            !condition.id ||
            !condition.operator ||
            !condition.value1.dataType ||
            !condition.value1.type ||
            !condition.value1.expression ||
            (this.hideValue2(condition.operator) &&
              (!condition.value2.dataType || !condition.value2.type || !condition.value2.expression))
          ) {
            this.$message.error('条件填写不完整');
            return false;
          }
        }
        return true;
      },
      onDelete(row) {
        this.$emit('onDelete', row);
      },
    },
  };
</script>
