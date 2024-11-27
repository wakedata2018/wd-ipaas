<template>
  <div class="permission-page">
    <div class="no-permission">
      <div class="icon">
        <img src="../../assets/images/empty.png">
      </div>
      <div class="text">{{text||$t('validator.noPermission')}}</div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    text: {
      type: String,
      default: null
    }
  },
  data() {
    return {};
  },
  methods: {}
};
</script>

<style scoped lang="less">
@import '../../css/var.less';
.permission-page {
  position: relative;
  height: calc(100vh - @header-height);
  .no-permission {
    position: absolute;
    top: 50%;
    width: 100%;
    width: 100%;
    color: #b2b6bb;
    text-shadow: 1px 1px 1px #fff;
    display: inline-block;
    transform: translateY(-50%);
    .icon {
      padding: 10px;
      font-size: 150px;
      text-align: center;
      img {
        width: 200px;
      }
    }
    .text {
      font-size: 18px;
      text-align: center;
    }
  }
}
</style>
