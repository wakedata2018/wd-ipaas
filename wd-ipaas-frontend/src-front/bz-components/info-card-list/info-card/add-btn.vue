<template>
  <div class="card-item view" :style="cardStyle" @click="clickAdd">
    <div class="card-image-container">
      <i class="el-icon-plus"></i>
    </div>
  </div>
</template>

<script>
  import { mapState } from 'vuex';
  export default {
    name: 'CardItem',
    data() {
      return {};
    },
    props: {
      cardStyle: {
        type: Object,
        default: _ => {},
      },
    },
    computed: {
      cardImage() {
        return null;
      },
    },
    methods: {
      clickAdd() {
        this.$emit('click-add');
      },
    },
  };
</script>
<style lang="less" scoped>
  .card-item {
    padding: 10px;
    width: 240px;
    height: 180px;
    display: inline-block;
    position: relative;
    border: 1px solid transparent;
    // box-shadow: 0px 4px 16px 0px rgba(234, 238, 241, 1);
    margin: 10px;
    box-sizing: border-box;
    transition: border 0.3s;
    vertical-align: top;
    overflow: hidden;
    background-color: #f3f6f8;
    border: 1px solid rgba(33, 150, 243, 1);
    &:hover {
      background-color: #ebf4fc;
      border: 1px solid #5daef0;
    }
  }
  .card-item .card-image-container {
    width: 100%;
    height: 100%;
    overflow: hidden;
    position: relative;
    text-align: center;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-content: center;
    font-size: 35px;
    color: #2776fb;
  }

  .view {
    cursor: pointer;
  }
</style>
