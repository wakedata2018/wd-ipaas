<template>
  <div>
    <div style="padding: 5px 0" v-if="!disabled">
      <el-button type="primary" icon="el-icon-plus" @click="onAdd"
        >添加</el-button
      >
    </div>
    <el-table :data="tableList" style="width: 100%" class="dss-table bd-table">
      <el-table-column prop="field" label="字段" width="200px">
        <template v-slot="{ row }">
          <span class="required">{{ !disabled ? "*" : "" }}</span>
          <el-input
            v-model="row.field"
            size="mini"
            maxlength="50"
            :disabled="disabled"
          />
        </template>
        <template v-slot:header>
          <tips-icon
            :content="$t('validator.httpFieldNameDesc')"
          ></tips-icon
          >字段
        </template>
      </el-table-column>
      <el-table-column prop="operatorId" label="来源" width="200px">
        <template v-slot="{ row }">
          <span class="required">{{ !disabled ? "*" : "" }}</span>
          <el-select
            v-model="row.operatorId"
            placeholder="来源节点"
            :disabled="disabled"
          >
            <el-option
              v-for="item in operationList"
              :key="item.operatorId"
              :label="item.desc"
              :value="item.operatorId"
            ></el-option>
          </el-select>
        </template>
      </el-table-column>
      <el-table-column prop="expression" label="表达式">
        <template v-slot="{ row }">
          <span class="required">{{ !disabled ? "*" : "" }}</span>
          <el-input
            v-model="row.expression"
            size="mini"
            maxlength="256"
            :disabled="disabled"
          />
        </template>
        <template v-slot:header>
          <tips-icon
            v-if="templateStr"
            :content="'例：' + templateStr"
          ></tips-icon
          >表达式
        </template>
      </el-table-column>
      <el-table-column
        prop="expression"
        label="操作"
        align="center"
        width="70px"
        v-if="!disabled"
      >
        <template v-slot="{ row, $index }">
          <el-button @click="onDelete($index)" class="bd-button bd-table-danger"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import TipsIcon from "@/components/tips-icon";
import Validator from "@/utils/validator.js";

export default {
  components: { TipsIcon },
  props: {
    tableList: {
      type: Array,
      default: () => [],
    },
    operationList: {
      type: Array,
      default: () => [],
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    curModel: {
      type: Object,
      default: () => ({}),
    },
  },
  computed: {
    templateStr() {
      if (this.curModel) {
        const { data } = this.curModel;
        if (data) {
          const { template } = data;
          return template;
        }
      }
      return null;
    },
  },
  methods: {
    onDelete(index) {
      this.tableList.splice(index, 1);
    },
    onAdd() {
      // let expression = '';
      // if (this.curModel) {
      //   const { data } = this.curModel;
      //   if (data) {
      //     const { template } = data;
      //     expression = template;
      //   }
      // }
      let operator = {
        field: "",
        expression: "",
        operatorId: "start",
      };
      this.tableList.push(operator);
    },
    validate() {
      let index = -1;
      let repeat = false;
      let fieldArr = [];
      const parameters = this.tableList;
      for (let i = 0; i < parameters.length > 0; i++) {
        const item = parameters[i];
        if (
          !item.field ||
          !Validator.httpFieldEnNameValidatorFun(item.field).result ||
          !item.operatorId ||
          !item.expression
        ) {
          index = i;
          break;
        }
        if (fieldArr.indexOf(item.field) !== -1) {
          repeat = true;
          break;
        }
        fieldArr.push(item.field);
      }

      const found = index !== -1 || repeat;
      return this.tableList.length > 0 && !found;
    },
  },
};
</script>

<style scoped lang="less">
@import "./style.less";
</style>
