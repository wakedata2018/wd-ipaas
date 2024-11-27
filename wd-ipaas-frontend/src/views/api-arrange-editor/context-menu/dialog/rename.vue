<template>
  <el-dialog
    v-loading="loading"
    :visible.sync="visible"
    append-to-body
    class="bd-dialog send-data-dialog"
    title="重命名"
    width="100%"
    :close-on-click-modal="false"
  >
    <el-form
      ref="form"
      label-position="right"
      :model="form"
      :rules="rules"
      label-width="100px"
      style="margin-bottom: 50px"
      @submit.native.prevent
    >
      <el-form-item prop="title" label="标题">
        <el-input v-model="form.title"></el-input>
      </el-form-item>
      <!-- <el-form-item prop="label" label="名称">
        <el-input v-model="form.label"></el-input>
      </el-form-item> -->
    </el-form>
    <div slot="footer" class="bd-dialog-footer">
      <el-button class="dss-btn-circle" @click="visible = false">取消</el-button>
      <el-button class="dss-btn-circle" type="primary" @click="onApply">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import eventBus, { EventName } from '@/components/g6-editor/event-bus';

  export default {
    data() {
      return {
        visible: false,
        form: {
          label: '',
          title: '',
        },
        rules: {},
        command: null,
        node: null,
        loading: false,
      };
    },
    computed: {
      title() {
        const title = '发送数据';
        if (!this.config) {
          return title;
        }
        return `${title}(${this.config.label})`;
      },
    },
    created() {
      eventBus.$on(EventName.nodeRename, this.onRename);
      this.rules = {
        contents: [
          {
            required: true,
            message: '请输入按行分隔分隔符',
          },
        ],
      };
    },
    beforeDestroy() {
      eventBus.$off(EventName.nodeRename, this.onRename);
      this.command = null;
      this.node = null;
    },
    methods: {
      onRename(e) {
        const { command, node } = e;
        this.visible = true;
        this.command = command;
        this.node = node;
        this.$nextTick(() => {
          this.$refs.form.resetFields();
          const model = node.getModel();
          this.form.label = model.label;
          this.form.title = model.title;
        });
      },
      onApply() {
        this.$refs.form.validate(isValidate => {
          if (!isValidate) {
            return;
          }
          if (!this.command || !this.node) {
            return;
          }
          const model = this.node.getModel();
          model.label = this.form.label;
          model.title = this.form.title;
          const updateData = {
            item: this.node,
            newModel: model,
          };
          this.command.executeCommand('update', [updateData]);
          this.model = null;
          this.command = null;
          this.visible = false;
        });
      },
    },
  };
</script>

<style lang="less" scoped>
  .send-data-dialog {
    /deep/ .el-dialog {
      max-width: 700px;
    }
  }
</style>
