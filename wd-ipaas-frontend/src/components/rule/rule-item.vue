<template>
  <el-col class="param-value" :span="col">
    <!-- 引用值 -->
    <template v-if="getValue('type') === ModeEnum.reference">
      <div v-if="getValue('expression')" class="param-value__value">
        <el-tooltip :content="getValue('expression')" placement="top">
          <el-input :value="getValue('expression')"></el-input>
        </el-tooltip>
        <i class="el-icon-setting param-value__icon" @click="selectExpression(SelectType.PARAMS)"></i>
      </div>
      <el-button v-else class="param-value__btn" type="text" @click="selectExpression(SelectType.PARAMS)">
        选择参数
      </el-button>
    </template>
    <!-- 表达式 -->
    <template v-else-if="getValue('type') === ModeEnum.method">
      <div v-if="getValue('expression')" class="param-value__value">
        <el-tooltip :content="getValue('expression')" placement="top">
          <el-input :value="getValue('expression')"></el-input>
        </el-tooltip>
        <i class="el-icon-setting param-value__icon" @click="selectExpression(SelectType.METHOD)"></i>
      </div>
      <el-button v-else class="param-value__btn" type="text" @click="selectExpression(SelectType.METHOD)"
        >配置表达式</el-button
      >
    </template>
    <!-- 固定值 -->
    <FatFormItem v-else :prop="`${props.prefix}${props.keyName}.fixedValue`"></FatFormItem>
    <SelectParametersDialog
      :visible.sync="showParams"
      :task-info="taskInfo"
      :operator-id="operatorId"
      :response-query-params="responseQueryParams"
      @selected="onParamsSelect"
    ></SelectParametersDialog>

    <SelectMethodDialog
      :visible.sync="showMethod"
      :operator-id="operatorId"
      :task-info="taskInfo"
      :response-query-params="responseQueryParams"
      @selected="onMethodSelect"
    ></SelectMethodDialog>
  </el-col>
</template>
<script lang="tsx" setup name="RuleItem">
  import { ref, computed } from 'vue';

  import { FatFormItem } from '@wakeadmin/components';
  import SelectParametersDialog from '../select-parameter-dialog/index.vue';
  import SelectMethodDialog from '../select-method-dialog/index.vue';

  import { ModeEnum } from '@/utils/enum';

  const SelectType = {
    PARAMS: 'params',
    METHOD: 'method',
  };

  const showParams = ref<boolean>(false);
  const showMethod = ref<boolean>(false);

  const props = defineProps({
    value: {
      type: Object,
      default: () => null,
    },
    keyName: {
      type: String,
      default: 'value1',
    },
    prefix: {
      type: String,
      default: '',
    },
    col: {
      type: Number,
      default: 12,
    },
    operatorId: {
      type: String,
      default: '',
    },
    taskInfo: {
      type: Object,
      default: () => ({}),
    },
    /**
     * 退出条件选择指定条件
     */
    isCondition: {
      type: Boolean,
      default: false,
    },
  });

  const responseQueryParams = computed(() => {
    return props.isCondition ? '?isForBreak=true' : '';
  });

  const getValue = (name: string) => {
    return props.value.getFieldValue(`${props.prefix}${props.keyName}.${name}`);
  };

  const selectExpression = (type: string) => {
    if (!props.value.disabled) {
      if (type === SelectType.PARAMS) {
        showParams.value = true;
      } else {
        showMethod.value = true;
      }
    }
  };

  const onParamsSelect = (value: { data: string }) => {
    props.value.setFieldValue(`${props.prefix}${props.keyName}.expression`, value.data);
    showParams.value = false;
  };

  const onMethodSelect = (value: string) => {
    props.value.setFieldValue(`${props.prefix}${props.keyName}.expression`, value);
    showMethod.value = false;
  };
</script>
<style lang="less" scoped>
  .param-value {
    &__value {
      flex: 1;
      position: relative;
      display: flex;
      align-items: center;

      & /deep/ .el-input__inner {
        padding-right: 24px;
      }
    }

    &__btn {
      width: 100%;
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
    }
  }

  .el-form-item.is-error {
    .param-value {
      &__btn {
        color: #f56c6c;
        border: 1px solid #f56c6c;
      }
    }
  }
</style>
