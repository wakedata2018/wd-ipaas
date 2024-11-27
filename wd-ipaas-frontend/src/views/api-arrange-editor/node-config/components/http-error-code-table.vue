<template>
  <div>
    <div style="padding: 5px 0" v-if="!disabled">
      <el-button type="primary" icon="el-icon-plus" @click="onAdd">添加</el-button>
    </div>
    <el-table :data="tableList" style="width: 100%" class="dss-table bd-table">
      <el-table-column prop="errorCode" label="错误码">
        <template #default="{ row }">
          <span class="required">{{ !disabled ? '*' : '' }}</span>
          <el-input-number v-model="row.errorCode" :min="0" :max="99999" size="mini" :disabled="disabled" />
        </template>
      </el-table-column>
      <el-table-column prop="errorMsg" label="错误信息">
        <template #default="{ row }">
          <el-input v-model="row.errorMsg" size="mini" maxlength="256" :disabled="disabled" />
        </template>
      </el-table-column>
      <el-table-column prop="solution" label="解决方案">
        <template #default="{ row }">
          <el-input v-model="row.solution" size="mini" maxlength="256" :disabled="disabled" />
        </template>
      </el-table-column>
      <el-table-column prop="expression" label="操作" align="center" width="70px" v-if="!disabled">
        <template #default="{ row, $index }">
          <el-button @click="onDelete($index)" class="bd-button bd-table-danger">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
  export default {
    props: {
      tableList: {
        type: Array,
        default: () => [],
      },
      disabled: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {};
    },
    methods: {
      onDelete(index) {
        this.tableList.splice(index, 1);
      },
      onAdd() {
        const operator = {
          errorCode: '',
          errorMsg: '',
          solution: '',
        };
        this.tableList.push(operator);
      },
      validate() {
        let index = -1;
        let repeat = false;
        const fieldArr = [];
        const parameters = this.tableList || [];
        for (let i = 0; i < parameters.length > 0; i++) {
          const item = parameters[i];
          if (item.errorCode !== null && item.errorCode !== undefined) {
            index = i;
            break;
          }
          if (fieldArr.indexOf(item.errorCode) !== -1) {
            repeat = true;
            break;
          }
          fieldArr.push(item.errorCode);
        }

        const found = index !== -1 || repeat;
        return !found;
      },
    },
  };
</script>

<style scoped lang="less">
  @import './style.less';
</style>
