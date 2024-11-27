<template>
  <el-button v-if="authorize(permitList, code)" v-bind="buttonProps" @click="handleClick">
    <slot></slot>
  </el-button>
</template>

<script>
  import { mapState } from 'vuex';
  import { Button } from 'element-ui';
  import { authorize } from '../../utils/authorize';

  export default {
    name: 'WKAuthButton',
    props: {
      ...Button.props,
      code: {
        type: String,
        default: '',
        require: true,
      },
    },
    data() {
      return {
        authorize,
      };
    },
    computed: {
      ...mapState({
        permitList: 'permitList',
      }),
      buttonProps() {
        const { code, ...props } = this.$props;
        return props;
      },
    },
    methods: {
      handleClick(e) {
        this.$emit('click', e);
      },
    },
  };
</script>
