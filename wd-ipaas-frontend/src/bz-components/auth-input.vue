<template>
  <input
    class="input"
    :class="{
        success:status=='success',
        error:status=='error'
    }"
    :placeholder="placeholder"
    :value="value"
    @input="handleInput"
    @change="handleChange"
  />
</template>

<script>
export default {
  data() {
    return {
      status: ''
    };
  },
  props: ['placeholder', 'value', 'name', 'rule', 'err_message'],
  mounted() {
    // console.log(this.rule)
  },
  methods: {
    handleInput(e) {
      // this.$emit("input",e.target.value)
      // console.log(e.target.value)
      //每当input 事件触发获取 输入的 value值
      const { value } = e.target;
      this.$emit('input', value);
      if (this.rule) {
        if (this.rule.test(value)) {
          this.status = 'success';
        } else {
          this.status = 'error';
        }
      }
    },
    //输入框失去焦点时被触发
    handleChange() {
      if (this.err_message && this.status == 'error') {
        // alert(this.err_message)
        this.$message({
          message: this.err_message,
          type: 'error'
        });
      }
    }
  }
};
</script>

<style scoped lang="less">
.input {
  width: 218px;
  height: 30px;
  padding: 20px;
  box-sizing: border-box;
  background-color: #fff;
  border: none;
  outline: none;
  border:1px solid #ccc;
}
.success {
  border-color: blue;
}
.error {
  border-color: red;
}
</style>