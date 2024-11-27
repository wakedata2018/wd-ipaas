<template>
  <FatFormGroup>
    <FatFormGroup :col="10">
      <FatFormItem
        :prop="`${prefix}value1.dataType`"
        :col="6"
        value-type="select"
        :value-props="{ options: DataTypeOptions }"
      >
      </FatFormItem>
      <FatFormItem
        :prop="`${prefix}value1.type`"
        :col="6"
        value-type="select"
        :value-props="{ options: SelectTypeOptions }"
      >
      </FatFormItem>
      <FatFormConsumer v-slot="scope">
        <RuleItem
          key="value1"
          :value="scope"
          key-name="value1"
          :operator-id="operatorId"
          :task-info="taskInfo"
          :prefix="prefix"
          :is-condition="isCondition"
        />
      </FatFormConsumer>
    </FatFormGroup>

    <FatFormItem
      :prop="`${prefix}operator`"
      :col="4"
      value-type="select"
      :value-props="{ options: OperatorOptions }"
      placeholder="请选择比较类型"
    >
    </FatFormItem>

    <FatFormConsumer v-slot="scope">
      <FatFormGroup v-if="isShow(scope)" :col="10">
        <FatFormItem
          :prop="`${prefix}value2.dataType`"
          :col="6"
          value-type="select"
          :value-props="{ options: DataTypeOptions }"
        >
        </FatFormItem>
        <FatFormItem
          :prop="`${prefix}value2.type`"
          :col="6"
          value-type="select"
          :value-props="{ options: SelectTypeOptions }"
        >
        </FatFormItem>
        <FatFormConsumer v-slot="scope">
          <RuleItem
            key="value2"
            :value="scope"
            key-name="value2"
            :prefix="prefix"
            :operator-id="operatorId"
            :task-info="taskInfo"
            :is-condition="isCondition"
          />
        </FatFormConsumer>
      </FatFormGroup>
    </FatFormConsumer>
  </FatFormGroup>
</template>

<script lang="tsx" setup name="Rule">
  import { computed } from 'vue';

  import { FatFormItem, FatFormGroup, FatFormConsumer } from '@wakeadmin/components';
  import RuleItem from './rule-item.vue';

  import { QUERY_TYPES_LIST, ModeMap } from '@/utils/enum';
  import { EXPRESSION_TYPE_MAP, EXPRESSION_TYPE_ENUM } from '@/enum';

  const props = defineProps({
    firstPrefix: {
      type: String,
      default: '',
    },
    secondPrefix: {
      type: String,
      default: '',
    },
    operatorId: {
      type: String,
      default: '',
    },
    taskInfo: {
      type: Object,
      default: () => ({}),
    },
    isCondition: {
      type: Boolean,
      default: false,
    },
  });

  const prefix = computed(() => {
    return `${props.firstPrefix}${props.secondPrefix}`;
  });

  /**
   * 显示比较值2
   */
  const isShow = (val: any) => {
    const operator = val.getFieldValue(`${prefix.value}operator`);
    const list = [
      EXPRESSION_TYPE_ENUM.ISEMPTY,
      EXPRESSION_TYPE_ENUM.ISNOTEMPTY,
      EXPRESSION_TYPE_ENUM.ISNULL,
      EXPRESSION_TYPE_ENUM.ISNOTNULL,
    ];
    if (list.includes(operator)) {
      return false;
    }
    return true;
  };

  const DataTypeOptions = QUERY_TYPES_LIST.map(item => {
    return {
      label: item,
      value: item,
    };
  });

  const SelectTypeOptions = Object.entries(ModeMap).map(item => {
    return {
      label: item[1],
      value: item[0],
    };
  });

  const OperatorOptions = Object.entries(EXPRESSION_TYPE_MAP).map(item => {
    return {
      label: item[1],
      value: item[0],
    };
  });
</script>
