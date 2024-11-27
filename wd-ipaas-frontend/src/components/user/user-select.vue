<template>
  <div>
    <el-input
      :value="strUser"
      readonly
      @focus="dialog.userSelect = true"
      :placeholder="placeholder"
    />
    <user-dialog
      :visible.sync="dialog.userSelect"
      @ok="onOK"
      ref="userDialog"
      :multiple="multiple"
    />
  </div>
</template>
<script>
import UserDialog from './user-dialog.vue';

export default {
  components: {
    UserDialog
  },
  props: {
    value: {
      type: Array,
      default() {
        return [];
      }
    },
    placeholder: {
      type: String,
      default: ''
    },
    multiple: {
      type: Boolean,
      default: true
    },
    labelKey: {
      type: String,
      default: 'znachn'
    }
  },
  data() {
    return {
      dialog: {
        userSelect: false
      }
    };
  },
  computed: {
    strUser() {
      return this.value.map(user => user[this.labelKey]).join('，');
    },
    userDialog() {
      return this.$refs.userDialog;
    }
  },
  watch: {
    'dialog.userSelect'(val) {
      if (val) {
        setTimeout(() => {
          this.userDialog.clear();
          this.userDialog.setSelected(this.$plain(this.value));
        }, 10);
      }
    }
  },
  methods: {
    onOK(users) {
      this.$emit('input', users);
      this.dialog.userSelect = false;
    }
  }
};
</script>