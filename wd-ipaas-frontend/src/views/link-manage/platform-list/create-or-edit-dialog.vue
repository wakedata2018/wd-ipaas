<template>
  <FatFormDrawer
    ref="drawerRef"
    :form="FormTab"
    append-to-body
    drawer-size="1200px"
    :class="['bd-dialog', 'platform-dialog']"
    destroy-on-close
    @finish="onFinish"
  />
</template>
<script lang="tsx" setup>
  import { FatFormDrawer, useFatFormDrawerRef, FatFormMode } from '@wakeadmin/components';
  import FormTab from './FormTab';

  import { PlatFormBasicInfo } from './type';

  const drawerRef = useFatFormDrawerRef<PlatFormBasicInfo>();

  const dataFormat = (data: PlatFormBasicInfo) => {
    const { enableStatus, connectorParamsDTOList, ...other } = data;
    const newList = connectorParamsDTOList.map(item => {
      return {
        ...item,
        isRequired: Boolean(item.isRequired),
        hiddenType: Boolean(item.hiddenType),
      };
    });
    return {
      enableStatus: Boolean(enableStatus),
      connectorParamsDTOList: newList,
      ...other,
    };
  };

  const open = (detail: PlatFormBasicInfo, mode: FatFormMode) => {
    const title = detail?.id ? (mode === 'preview' ? '查看连接器' : '编辑连接器') : '新增连接器';
    let data;
    // 数据转换
    if (detail?.id) {
      data = dataFormat(detail);
    }

    drawerRef.value?.open({
      title,
      initialValue: data,
      mode,
    });
  };

  // 定义Emits
  const emits = defineEmits<{
    (e: 'refresh'): void;
  }>();

  const onFinish = () => {
    emits('refresh');
  };

  defineExpose({
    open,
  });
</script>
<style lang="less" scoped>
  .platform-dialog {
    .vertical-column {
      flex-direction: column;
    }
  }
</style>
