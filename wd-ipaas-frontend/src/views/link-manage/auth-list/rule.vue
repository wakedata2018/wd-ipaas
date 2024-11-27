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
        <RuleItem key="value1" :value="scope" key-name="value1" :response-params="props.responseParams" />
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
          <RuleItem key="value2" :value="scope" key-name="value2" :response-params="props.responseParams" />
        </FatFormConsumer>
      </FatFormGroup>
    </FatFormConsumer>
  </FatFormGroup>
</template>

<script lang="tsx" setup>
  import { PropType, computed } from 'vue';

  import { FatFormItem, FatFormGroup, FatFormConsumer } from '@wakeadmin/components';
  import { InterfaceParams } from '@/utils/enum/auth-list';
  import RuleItem from './rule-item.vue';

  import { QUERY_TYPES_LIST, ModeMap } from '@/utils/enum';
  import { EXPRESSION_TYPE_MAP, EXPRESSION_TYPE_ENUM } from '@/enum';

  const props = defineProps({
    responseParams: {
      type: Array as PropType<InterfaceParams[]>,
      default: () => [],
    },
  });

  const prefix = 'authConfigParam.condition.';

  /**
   * 显示比较值2
   */
  const isShow = computed(() => {
    return (val: any) => {
      const list = [
        EXPRESSION_TYPE_ENUM.ISEMPTY,
        EXPRESSION_TYPE_ENUM.ISNOTEMPTY,
        EXPRESSION_TYPE_ENUM.ISNULL,
        EXPRESSION_TYPE_ENUM.ISNOTNULL,
      ];
      if (!list.includes(val.values.authConfigParam.condition.operator)) {
        return true;
      }
      return false;
    };
  });

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
