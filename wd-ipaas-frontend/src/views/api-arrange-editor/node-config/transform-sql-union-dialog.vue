<template>
  <el-dialog
    :visible.sync="isShow"
    title="UnionAll"
    class="bd-dialog scrollbar-dialog not-lock-scroll"
    :lock-scroll="false"
    top="80px"
    width="600px"
  >
    <el-scrollbar class="scrollbar">
      <el-form ref="form" :model="form" :rules="rules" label-position="left" label-width="120px" @submit.native.prevent>
        <el-form-item label="步骤名称" prop="name">
          <template slot="label">
            <tips-icon :content="$t('validator.nameWithoutChineseValidateDesc')"></tips-icon>
            步骤名称
          </template>
          <el-input v-model="form.name" size="mini" maxlength="50" :disabled="disabled"> </el-input>
        </el-form-item>
        <el-form-item prop="desc">
          <template slot="label"> 步骤描述 </template>
          <el-input v-model="form.desc" size="mini" maxlength="50" :disabled="disabled"> </el-input>
        </el-form-item>
      </el-form>
    </el-scrollbar>
    <div v-if="!isReadonly" slot="footer" class="bd-dialog-footer">
      <el-button class="dss-btn-circle" @click="isShow = false">取消</el-button>
      <el-button class="dss-btn-circle" type="primary" @click="onSave">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import cloneDeep from 'lodash/cloneDeep';
  import { Message } from 'element-ui';
  import TipsIcon from '@/components/tips-icon';
  import Validator from '@/utils/validator.js';
  import { getInitForm } from '../node-convert/transform-sql-union';
  import { getIdFromItem, getUniqueNameFromItem } from '../utils';

  export default {
    components: { TipsIcon },
    props: {
      visible: {
        type: Boolean,
        default: false,
      },
      config: {
        type: Object,
        default: null,
      },
      curNode: {
        type: Object,
        default: null,
      },
      isReadonly: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        form: getInitForm(),
        rules: {},
        refresh: false,
      };
    },
    computed: {
      operatorId() {
        return getIdFromItem(this.curNode);
      },
      isShow: {
        get() {
          return this.visible;
        },
        set(val) {
          if (!val) {
            this.$emit('cancel', getUniqueNameFromItem(this.curNode));
          }
          this.$emit('update:visible', val);
        },
      },
      disabled() {
        return this.isReadonly;
      },
    },
    watch: {
      isShow(val) {
        const form = this.$refs.form;
        if (form && form.resetFields) {
          form.resetFields();
        }
        this.$nextTick(() => {
          if (!val) {
            this.form = getInitForm();
          } else {
            this.initForm();
          }
        });
      },
    },
    created() {
      this.rules = {
        name: [
          {
            required: true,
            trigger: 'blur',
            validator: Validator.enNameValidator,
          },
        ],
        desc: [
          {
            required: true,
            trigger: 'blur',
            message: '不能为空',
          },
        ],
      };
    },
    methods: {
      initForm() {
        this.refresh = !this.refresh;
        this.form = Object.assign({}, getInitForm(), this.config);
      },
      validate() {
        if (!this.curNode || this.curNode.destroyed) {
          throw new Error('该节点不存在，无法保存，请重新选择节点。');
        }

        const operators = this.taskInfo.dataAssetApi.apiAttr.operators;
        const isFind = Object.keys(operators).some(key => {
          return operators[key].name === this.form.name && key !== this.operatorId;
        });
        if (isFind) {
          throw new Error('步骤名称不能重复，请重新设置。');
        }
      },
      onSave() {
        this.$refs.form.validate(isValidate => {
          if (!isValidate) {
            this.$message.error(this.$t('validator.emptyWarning'));
            return;
          }
          this.save();
        });
      },
      save() {
        try {
          this.validate();
          this.$emit('save', this.form, cloneDeep(this.curNode.getModel()));
          this.isShow = false;
        } catch (error) {
          this.$message.error(error.message);
        }
      },
    },
  };
</script>

<style scoped lang="less"></style>
