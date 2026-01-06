<template>
  <div class="permission-page">
    <div class="no-permission">
      <div class="icon">
        <img src="./images/empty.png" />
      </div>
      <div class="text">{{ text || '您当前没有权限访问，请联系系统管理人员' }}</div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'DssNoPermission',
  props: {
    text: {
      type: String,
      default: null,
    },
  },
  data() {
    return {};
  },
  methods: {},
};
</script>

<style scoped lang="less">
@import './style.less';
</style>
