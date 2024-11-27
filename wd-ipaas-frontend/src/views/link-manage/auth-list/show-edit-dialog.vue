<template>
  <FatFormDrawer
    ref="drawerRef"
    title="鉴权方式"
    :submit="handleSubmit"
    drawer-size="60%"
    :class="{ 'bd-dialog': true, 'auth-list-dialog': true, 'show-mode': isShowMode }"
    label-width="90px"
    append-to-body
    destroy-on-close
    @finish="handleFinish"
    @values-change="onFormValueChange"
  >
    <FatFormGroup>
      <FatFormItem prop="connectorName" label="连接器名称" :col="12" disabled required></FatFormItem>
      <FatFormItem
        prop="connectorEnvironmentId"
        label="选择环境"
        :col="12"
        value-type="select"
        :value-props="{ options: envOptipns }"
        required
      ></FatFormItem>
    </FatFormGroup>
    <FatFormGroup label="选择接口" required>
      <FatFormItem
        prop="connectorApiGroupId"
        :col="6"
        value-type="select"
        :value-props="{ options: interfaceGroupOptipns }"
        required
      ></FatFormItem>
      <FatFormItem
        prop="connectorApiId"
        :col="18"
        value-type="select"
        :value-props="{ options: interfaceOptipns }"
        required
      ></FatFormItem>
    </FatFormGroup>
    <FatFormGroup
      ref="ruleRef"
      label="规则"
      prop="authConfigParam.condition"
      :rules="{ validator: RuleValidator, required: true }"
    >
      <Rule :response-params="requestAndResponseParams?.responseParams" />
    </FatFormGroup>
    <InterfaceInfo v-model="requestAndResponseParams" v-loading="paramsInfoLoading" />
  </FatFormDrawer>
</template>
<script lang="tsx" setup name="showEditDialog">
  import { ref } from 'vue';

  import { Message } from 'element-ui';

  import {
    FatFormDrawer,
    FatFormItem,
    FatFormGroup,
    FatFormMode,
    useFatFormDrawerRef,
    useFatFormRef,
  } from '@wakeadmin/components';
  import Rule from './rule.vue';
  import InterfaceInfo from './interface-info.vue';

  import {
    AuthInfo,
    EnvInfo,
    PlatformGroup,
    InterfaceList,
    RequestAndResponseParams,
    AuthConfigParamCondition,
  } from '@/utils/enum/auth-list';
  import AuthApi from '@/api/auth';

  import { EXPRESSION_TYPE_ENUM } from '@/enum';

  type Options = {
    label: string;
    value: number;
  };

  const drawerRef = useFatFormDrawerRef();
  const envOptipns = ref<Options[]>([]);
  const interfaceGroupOptipns = ref<Options[]>([]);
  const interfaceOptipns = ref<Options[]>([]);
  const requestAndResponseParams = ref<RequestAndResponseParams | undefined>();
  const paramsInfoLoading = ref<boolean>(false);
  const ruleRef = useFatFormRef();
  const isShowMode = ref(false);

  // 定义Emits
  const emits = defineEmits<{
    (e: 'refresh'): void;
  }>();

  const open = async (detail: AuthInfo, mode?: FatFormMode) => {
    const { connectorId, connectorApiGroupId, connectorApiId } = detail;
    isShowMode.value = mode === 'preview';
    if (connectorId) {
      Promise.allSettled([
        getEnvOptions(connectorId),
        getInterfaceGroupOptions(connectorId),
        getInterfaceOptions(connectorApiGroupId),
        getInterfaceDetail(connectorApiId),
      ]);
    }
    drawerRef.value?.open({
      initialValue: detail,
      disabled: isShowMode.value,
    });
  };

  const RuleValidator = (rule: object, value: AuthConfigParamCondition, callback: (message: Error | void) => void) => {
    const { id, desc, ...other } = value;
    let resultFlag = true;

    const validate = (data: Omit<AuthConfigParamCondition, 'id' | 'desc'>) => {
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
      validate(params);
    } else {
      validate(other);
    }

    if (!resultFlag) {
      callback(new Error('*请补全规则条件'));
    } else {
      callback();
    }
  };

  const handleSubmit = async (values: any) => {
    await ruleRef.value?.validate();
    await AuthApi.updateAuthInfo(values);
  };

  const handleFinish = (values: any) => {
    // 保存成功，可以在这里进行列表刷新之类的操作
    Message.success('保存成功');
    emits('refresh');
  };

  /**
   * 获取环境下拉数据
   */
  const getEnvOptions = async (id: number) => {
    const response = await AuthApi.fetchEnvironmentListById(id);
    envOptipns.value = response[0].connectorEnvironmentAddressDTOList.map((item: EnvInfo) => {
      return {
        label: item.addressName,
        value: item.id,
      };
    });
  };

  /**
   * 获取指定平台下接口分组列表
   */
  const getInterfaceGroupOptions = async (id: number) => {
    const response = await AuthApi.fetchInterfaceGroupById(id);
    interfaceGroupOptipns.value =
      response[0].connectorApiGroupDTOList?.map((item: PlatformGroup) => {
        return {
          label: item.groupName,
          value: item.id,
        };
      }) ?? [];
  };

  /**
   * 获取指定分组下的接口列表
   */
  const getInterfaceOptions = async (id: number) => {
    if (id) {
      const response = await AuthApi.fetchInterfaceBytId(id);
      interfaceOptipns.value = response?.map((item: InterfaceList) => {
        return {
          label: item.apiName,
          value: item.id,
        };
      });
    } else {
      interfaceOptipns.value = [];
    }
  };

  /**
   * 接口信息
   */
  const getInterfaceDetail = async (id: number) => {
    if (id) {
      paramsInfoLoading.value = true;
      const { requestParams, responseParams, connectorApi } = await AuthApi.fetchInterfaceDetail(id);
      requestAndResponseParams.value = {
        reqMethod: connectorApi.reqMethod,
        requestParams,
        responseParams,
      };
    } else {
      requestAndResponseParams.value = undefined;
    }
    paramsInfoLoading.value = false;
  };

  const onFormValueChange = async (values: AuthInfo, props: string, value: number) => {
    if (props === 'connectorApiGroupId') {
      await getInterfaceOptions(value);
      drawerRef.value?.setFieldValue('connectorApiId', null);
    }
    if (props === 'connectorApiId') {
      await getInterfaceDetail(value);
      drawerRef.value?.setFieldValue('authConfigParam.condition', null);
      drawerRef.value?.clearValidate();
    }
    if (props === 'authConfigParam.condition.value1.type') {
      drawerRef.value?.setFieldValue('authConfigParam.condition.value1.expression', null);
    }
    if (props === 'authConfigParam.condition.value2.type') {
      drawerRef.value?.setFieldValue('authConfigParam.condition.value2.expression', null);
    }
  };

  defineExpose({
    open,
  });
</script>
<style lang="less" scoped>
  .auth-list-dialog.show-mode {
    ::v-deep .fat-form-drawer__footer {
      display: none;
    }
  }
</style>
