<template>
  <el-tree
    ref="treeGroupRef"
    v-slot="{ node, data }"
    :props="elTreeProps"
    node-key="id"
    :expand-on-click-node="false"
    :data="props.value"
    :allow-drop="props.allowDrop"
    highlight-current
    draggable
    default-expand-all
    @node-click="props.nodeClick"
  >
    <slot :node="node" :data="data"></slot>
  </el-tree>
</template>

<script lang="tsx" setup>
  import { ref } from 'vue';
  import { DropPositionType } from './type';

  const props = defineProps<{
    value: any;
    elTreeProps?: { label: string; children: string };
    nodeClick?: (data: any) => {};
    allowDrop?: (draggingNode: any, dropNode: any, type: DropPositionType) => {};
  }>();

  const treeGroupRef = ref();

  // 取消高亮
  const clearHightKey = () => {
    treeGroupRef.value.setCurrentKey(null);
  };

  // 高亮
  const setHightKey = (id: number) => {
    treeGroupRef.value.setCurrentKey(id);
  };

  defineExpose({
    clearHightKey,
    setHightKey,
  });
</script>
