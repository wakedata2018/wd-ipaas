<template>
  <el-dialog title="Mock" class="mock-dialog" :visible="props.visible" width="30%" append-to-body @close="onClose">
    <el-input v-model="mockRule" type="textarea" :rows="10" placeholder="请输入Mock语句">
    </el-input>
    <div class="tip"><i class="el-icon-warning-outline"></i><span>语法请查看Mock.js文档</span></div>
    <span slot="footer" class="dialog-footer">
      <el-button @click="onClose">取 消</el-button>
      <el-button type="primary" @click="onSumbit">确 定</el-button>
    </span>
  </el-dialog>
</template>
<script lang="tsx" setup>
import { ref } from 'vue'

const props = defineProps({
  value: {
    type: String,
    default: ''
  },
  visible: {
    type: Boolean,
    default: false,
  }
});

const mockRule = ref(props.value)

// 定义Emits
const emits = defineEmits<{
  (e: 'update:visible', value: boolean): void;
  (e: 'save', value: string): void;
}>();

const onClose = () => {
  emits('update:visible', false);
};

const onSumbit = () => {
  emits('save', mockRule.value);
  onClose()
};
</script>
<style lang="less" scoped>
.mock-dialog {
  .tip {
    padding: 8px 0px;
    color: #8492a6;

    span {
      padding-left: 3px;
    }
  }
}
</style>
