<template>
  <el-col class="param-value" :span="12">
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
    <FatFormItem v-else :prop="`${prefix}${props.keyName}.expression`"></FatFormItem>
    <AuthSelectParamsDialog :visible.sync="showParams" :params="props.responseParams" @select="onParamsSelect" />
    <AuthSelectMethodDialog :visible.sync="showMethod" :params="props.responseParams" @select="onMethodSelect" />
  </el-col>
</template>
<script lang="tsx" setup>
  import { ref, PropType } from 'vue';

  import { FatFormItem } from '@wakeadmin/components';
  import AuthSelectParamsDialog from './auth-select-params-dialog.vue';
  import AuthSelectMethodDialog from './auth-select-method-dialog.vue';

  import { InterfaceParams } from '@/utils/enum/auth-list';
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
    responseParams: {
      type: Array as PropType<InterfaceParams[]>,
      default: () => [],
    },
  });
  const prefix = 'authConfigParam.condition.';

  const getValue = (name: string) => {
    return props.value.getFieldValue(`${prefix}${props.keyName}.${name}`);
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

  const onParamsSelect = (value: string) => {
    props.value.setFieldValue(`${prefix}${props.keyName}.expression`, value);
    showParams.value = false;
  };

  const onMethodSelect = (value: string) => {
    props.value.setFieldValue(`${prefix}${props.keyName}.expression`, value);
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
