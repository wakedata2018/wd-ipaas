<!-- eslint-disable vue/no-mutating-props -->
<template>
  <div class="param-value">
    <!-- 选择方式 -->
    <el-select v-model="value.type" class="param-value__selector" @change="modeChange">
      <el-option v-for="key in Object.keys(ModeMap)" :key="key" :label="ModeMap[key]" :value="key"></el-option>
    </el-select>
    <!-- 选择参数 -->
    <template v-if="value.type === ModeEnum.reference">
      <div v-if="value.expression" class="param-value__value">
        <el-tooltip :content="value.expression" placement="top">
          <el-input ref="reference" v-model="value.expression"></el-input>
        </el-tooltip>
        <i v-if="!disabled" class="el-icon-setting param-value__icon" @click="selectParams"></i>
      </div>
      <el-button v-else class="param-value__btn" type="text" @click="selectParams"> 选择参数 </el-button>
    </template>
    <template v-else-if="value.type === ModeEnum.method">
      <div v-if="value.expression" class="param-value__value">
        <el-tooltip :content="value.expression" placement="top">
          <el-input v-model="value.expression"></el-input>
        </el-tooltip>
        <i v-if="!disabled" class="el-icon-setting param-value__icon" @click="selectMethod"></i>
      </div>
      <el-button v-else class="param-value__btn" type="text" @click="selectMethod">配置表达式</el-button>
    </template>
    <el-input
      v-else
      v-model="value.expression"
      class="param-value__input"
      placeholder="请输入固定值"
      clearable
    ></el-input>
    <SelectParametersDialog
      :visible.sync="showDialog"
      :task-info="taskInfo"
      :operator-id="operatorId"
      @selected="selected"
    ></SelectParametersDialog>
    <SelectMethodDialog
      :visible.sync="showMethodDialog"
      :expression="value.expression"
      :task-info="taskInfo"
      :operator-id="operatorId"
      @selected="selectedMethod"
    ></SelectMethodDialog>
  </div>
</template>

<script>
  import { ModeMap, ModeEnum } from '@/utils/enum';
  import SelectParametersDialog from '@/components/select-parameter-dialog';
  import SelectMethodDialog from '@/components/select-method-dialog';
  import { getIdFromItem } from '../../utils';

  export default {
    name: 'SetParamsValue',
    components: { SelectParametersDialog, SelectMethodDialog },
    props: {
      value: {
        type: Object,
        default: () => ({}),
      },
      taskInfo: {
        type: Object,
        default: () => ({}),
      },
      curNode: {
        type: Object,
        default: null,
      },
      disabled: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        ModeMap,
        ModeEnum,
        showDialog: false,
        showMethodDialog: false,
      };
    },
    computed: {
      operatorId() {
        return getIdFromItem(this.curNode);
      },
    },

    methods: {
      modeChange() {
        this.$set(this.value, 'expression', '');
      },
      selectParams() {
        this.showDialog = true;
      },
      selected({ data }) {
        this.$set(this.value, 'expression', data);
        this.$emit('check');
      },
      selectMethod() {
        this.showMethodDialog = true;
      },
      selectedMethod(expression) {
        this.$set(this.value, 'expression', expression);
        this.$emit('check');
      },
    },
  };
</script>

<style lang="less" scoped>
  .param-value {
    display: flex;
    align-items: center;

    &__selector {
      width: 90px;
      margin-right: 10px;
    }

    &__value {
      position: relative;
      display: inline-block;
      align-items: center;
      width: 200px;
      & /deep/ .el-input__inner {
        padding-right: 24px;
      }
    }
    &__btn {
      width: 200px;
      border-radius: 4px;
      border: 1px solid #dcdfe6;
      text-align: left;
      height: 28px;
      padding: 0 15px;
    }
    &__icon {
      cursor: pointer;
      position: absolute;
      padding: 0px 3px;
      right: 3px;
      background: #fff;
      top: 50%;
      transform: translateY(-50%);
    }
    &__input {
      width: 200px;
    }
  }
</style>
