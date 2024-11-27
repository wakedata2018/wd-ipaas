<template>
  <div class="edit-branch-table">
    <el-table :data="tableData" class="dss-table bd-table">
      <el-table-column label="分支名称" width="200px" prop="branchName">
        <template #default="{ row }">
          <el-input v-model="row.branchName" maxlength="30" placeholder="不超过30个字符" />
        </template>
      </el-table-column>

      <el-table-column label="判断条件" prop="branchRuleExpression">
        <template #default="{ row }">
          <span>{{ initRule(row) }}</span>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="200px">
        <template #default="{ row, $index }">
          <el-button type="primary" plain @click="onEdit($index, row)">编辑</el-button>
          <el-button type="danger" plain @click="onDelete($index, row)">删除分支</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
  import { EXPRESSION_TYPE_MAP } from '@/enum';

  const TextMap = {
    and: '和',
    or: '或',
  };

  export default {
    name: 'EditBranchTable',
    props: {
      tableData: {
        type: Array,
        default: () => {
          return [];
        },
      },
    },
    data() {
      return {
        EXPRESSION_TYPE_MAP,
      };
    },
    methods: {
      unifSymbol(text) {
        let res = text.replaceAll('(', '( ');
        res = res.replaceAll('（', '( ');
        res = res.replaceAll(')', ' )');
        res = res.replaceAll('）', ' )');
        return res;
      },

      returnConditionsMap(data) {
        const obj = {};
        const arr = [];
        data?.forEach(item => {
          obj[item.id] = item;
          arr.push(item.id);
        });
        return {
          obj,
          arr,
        };
      },

      initRule({ branchRuleExpression, conditions }) {
        const ruleNames = this.returnConditionsMap(conditions);
        let rule = '';
        const rulesArr = this.unifSymbol(branchRuleExpression).split(' ');

        rulesArr.forEach(item => {
          if (item && ruleNames.arr?.includes(item)) {
            const _e = this.formatRuleText(ruleNames?.obj[item]);
            rule += _e;
          } else {
            rule += ` ${TextMap[item] || item} `;
          }
        });
        return rule;
      },

      formatRuleText(data) {
        const arr = [];
        arr.push(data.value1.expression);
        arr.push(EXPRESSION_TYPE_MAP[data.operator]);
        arr.push(data.value2.expression);
        return arr.join(' ');
      },

      onEdit(index, row) {
        this.$emit('onEdit', row, index);
      },
      onDelete(index, row) {
        this.$emit('onDelete', index, row);
      },
    },
  };
</script>
