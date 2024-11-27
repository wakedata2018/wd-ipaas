<template>
  <el-dialog
    title="循环算子"
    class="bd-dialog scrollbar-dialog medium foreach-dialog"
    :visible="props.visible"
    :close-on-click-modal="false"
    custom-class="anim-left"
    fullscreen
    @close="onClose"
  >
    <FatForm
      ref="form"
      :initial-value="config"
      :submit="handleSubmit"
      :disabled="isReadonly"
      @values-change="onFormValueChange"
    >
      <FatFormGroup>
        <FatFormItem prop="name" label="步骤名称" :col="12" required> </FatFormItem>
        <FatFormItem label="步骤描述" :col="12" prop="desc"> </FatFormItem>
      </FatFormGroup>
      <FatFormGroup label="循环条件" required>
        <FatFormItem
          prop="loopCondition.type"
          value-type="select"
          :value-props="{ options: SelectTypeOptions }"
          :col="6"
        >
        </FatFormItem>
        <FatFormConsumer v-slot="scope">
          <RuleItem
            :value="scope"
            key-name="loopCondition"
            prefix=""
            :operator-id="operatorId"
            :task-info="taskInfo"
            :col="6"
          />
        </FatFormConsumer>
      </FatFormGroup>
      <FatFormGroup ref="ruleRef" label="退出条件" :rules="{ validator: RuleValidator }" vertical>
        <!-- 退出条件类型 -->
        <FatFormItem prop="breakCondition.type" value-type="radio" :value-props="{ options: LoopCondition }">
        </FatFormItem>
        <!-- 指定次数 -->
        <FatFormItem
          v-if="isShowLoopCount"
          prop="breakCondition.count"
          value-type="integer"
          :value-props="{ min: 0 }"
        ></FatFormItem>
        <!-- 指定条件 -->
        <Rule
          v-else
          :operator-id="operatorId"
          :task-info="taskInfo"
          first-prefix="breakCondition."
          second-prefix="condition."
          is-condition
        />
      </FatFormGroup>
      <FatFormGroup>
        <span class="tip"
          >使用描述：循环算子的条件必须为数组，系统自动循环数组内容，支持指定循环退出条件，可以按固定次数或者指定表达式判断退出；</span
        >
      </FatFormGroup>
      <template #submitter="scope">
        <div class="bd-dialog-footer">
          <el-button class="dss-btn-circle" @click="onClose">取消</el-button>
          <el-button class="dss-btn-circle" type="primary" @click="scope.submit">保存</el-button>
        </div>
      </template>
    </FatForm>
  </el-dialog>
</template>
<script lang="tsx" setup>
  import { computed } from 'vue';

  import { FatForm, FatFormItem, FatFormGroup, FatFormConsumer, useFatFormRef } from '@wakeadmin/components';
  import Rule from '@/components/rule/index.vue';
  import RuleItem from '@/components/rule/rule-item.vue';

  import cloneDeep from 'lodash/cloneDeep';
  import { getInitForm } from '../../global/task-conf';
  import { getIdFromItem } from '../../utils';
  import eventBus, { EventName } from '@/components/g6-editor/event-bus';

  import { AuthConfigParamCondition } from '@/utils/enum/auth-list';
  import { ModeMap, LoopConditionType } from '@/utils/enum';
  import { EXPRESSION_TYPE_ENUM } from '@/enum';

  const props = defineProps({
    visible: {
      type: Boolean,
      default: false,
    },
    config: {
      type: Object,
      default: () => ({}),
    },
    curNode: {
      type: Object,
      default: null,
    },
    taskInfo: {
      type: Object,
      default: () => getInitForm(),
    },
    isReadonly: {
      type: Boolean,
      default: false,
    },
  });

  const form = useFatFormRef();

  const operatorId = computed(() => {
    return getIdFromItem(props.curNode);
  });

  const LoopCondition = [
    {
      label: '指定次数',
      value: LoopConditionType.COUNT,
    },
    {
      label: '指定条件',
      value: LoopConditionType.CONDITION,
    },
  ];
  const SelectTypeOptions = Object.entries(ModeMap).map(item => {
    return {
      label: item[1],
      value: item[0],
    };
  });

  // 定义Emits
  const emits = defineEmits<{
    (e: 'update:visible', value: boolean): void;
    (e: 'save', value: any, model: any): void;
  }>();

  const onClose = () => {
    emits('update:visible', false);
  };

  const validate = (values: any) => {
    if (!props.curNode || props.curNode.destroyed) {
      throw new Error('该节点不存在，无法保存，请重新选择节点。');
    }

    if (values.name === 'globalParam') {
      throw new Error('globalParam不能作为步骤名称。');
    }

    const operators = props.taskInfo.dataAssetApi.apiAttr.operators;
    const isFind = Object.keys(operators).some(key => {
      return operators[key].name === values.name && key !== operatorId.value;
    });
    if (isFind) {
      throw new Error('步骤名称不能重复，请重新设置。');
    }
  };

  const handleSubmit = async (values: any) => {
    validate(values);
    emits('save', values, cloneDeep(props.curNode.getModel()));
    onClose();
  };

  const RuleValidator = (rule: object, value: AuthConfigParamCondition, callback: (message: Error | void) => void) => {
    const { id, desc, ...other } = value;
    let resultFlag = true;

    const validateCondition = (data: Omit<AuthConfigParamCondition, 'id' | 'desc'>) => {
      for (const item of Object.values(data)) {
        const isObjType = typeof item === 'object' && !Array.isArray(item);
        if (isObjType) {
          // 对象类型
          validate(item);
        } else if (!item) {
          // 值类型
          resultFlag = false;
        }
      }
    };
    const list = [
      EXPRESSION_TYPE_ENUM.ISEMPTY,
      EXPRESSION_TYPE_ENUM.ISNOTEMPTY,
      EXPRESSION_TYPE_ENUM.ISNULL,
      EXPRESSION_TYPE_ENUM.ISNOTNULL,
    ];
    if (list.includes(other.operator)) {
      const { value2, ...params } = other;
      validateCondition(params);
    } else {
      validateCondition(other);
    }

    if (!resultFlag) {
      callback(new Error('*请补全规则条件'));
    } else {
      callback();
    }
  };

  // 控制退出条件组件显示
  const isShowLoopCount = computed(() => {
    return form.value?.getFieldValue('breakCondition.type') === LoopConditionType.COUNT;
  });

  // 表单值改变联动改变其他值
  const onFormValueChange = async (values: any, prop: string, value: number) => {
    if (prop === 'loopCondition.type') {
      form.value?.setFieldValue('loopCondition.fixedValue', null);
      form.value?.setFieldValue('loopCondition.expression', null);
    }
    if (prop === 'breakCondition.condition.value1.type') {
      form.value?.setFieldValue('breakCondition.condition.value1.expression', null);
    }
    if (prop === 'breakCondition.value2.type') {
      form.value?.setFieldValue('breakCondition.condition.value2.expression', null);
    }
    if (prop === 'breakCondition.type') {
      if (value === LoopConditionType.CONDITION) {
        form.value?.setFieldValue('isForBreak', true);
      } else {
        form.value?.setFieldValue('isForBreak', false);
      }
    }
  };
</script>
<style lang="less" scoped>
  .foreach-dialog {
    .tip {
      margin-left: 10px;
    }
  }
</style>
