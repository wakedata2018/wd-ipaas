<template>
  <el-tooltip :placement="placement">
    <div slot="content" style="max-width: 250px">
      <div v-html="content"></div>
    </div>
    <el-button type="text" icon="el-icon-question"></el-button>
  </el-tooltip>
</template>

<script>
export default {
  name: 'tips-icon',
  props: {
    content: {
      type: String,
      default: ''
    },
    placement: {
      type: String,
      default: 'top-start'
    }
  }
}
</script>
