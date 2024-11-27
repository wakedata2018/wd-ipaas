<template>
  <el-tabs type="border-card">
    <el-tab-pane label="JSON">
      <json-viewer :value="jsonValue" :expand-depth="5" copyable sort></json-viewer>
    </el-tab-pane>
    <el-tab-pane v-if="xmlValue" label="XML">
      <el-input v-model="xmlValue" type="textarea" autosize readonly />
    </el-tab-pane>
  </el-tabs>
</template>
<script>
  import JsonViewer from 'vue-json-viewer';
  import vkbeautify from 'vkbeautify';

  export default {
    name: 'ApiJsonViewer',
    components: {
      JsonViewer,
    },
    props: {
      value: {
        type: Array,
        default: () => {
          return [];
        },
      },
    },
    computed: {
      jsonValue() {
        return this.value[0] || {};
      },
      xmlValue() {
        return this.value[1] && vkbeautify.xml(this.value[1]);
      },
    },
  };
</script>
<style lang="less" scoped>
  ::v-deep .el-textarea {
    .el-textarea__inner {
      border: none !important;
    }
  }
</style>
