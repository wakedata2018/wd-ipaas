<template>
  <el-dialog
    :visible.sync="isShow"
    title="判断算子"
    class="bd-dialog scrollbar-dialog medium"
    :close-on-click-modal="false"
    custom-class="anim-left"
    fullscreen
    @open="handleOpen"
  >
    <el-form ref="form" :model="form" :rules="rules" label-position="left" label-width="auto" :disabled="isReadonly">
      <el-form-item label="步骤名称" prop="name">
        <template slot="label">
          <tips-icon content="英文字母开头，仅包含数字、字母和下划线"></tips-icon>
          步骤名称:
        </template>
        <el-input
          v-model.trim="form.name"
          maxlength="30"
          class="w460"
          placeholder="输入下划线、英文字母、数字，不超过30个字"
        ></el-input>
      </el-form-item>
      <el-form-item label="步骤描述" prop="desc">
        <el-input v-model.trim="form.desc" maxlength="50" class="w460" placeholder="限制输入50字符"></el-input>
      </el-form-item>
      <el-form-item label="比较值1" prop="value1">
        <div class="w460">
          <el-cascader
            v-model="form.value1.dataType"
            :options="CASCADER_DATA_TYPE_OPTIONS"
            :show-all-levels="false"
            :props="{ expandTrigger: 'hover' }"
          ></el-cascader>

          <SetParamsValue v-model="form.value1" :task-info="taskInfo" :cur-node="curNode" @check="check('value1')" />
        </div>
      </el-form-item>

      <el-form-item label="比较类型" prop="operator">
        <el-select v-model="form.operator" placeholder="请选择比较类型" class="w150">
          <el-option
            v-for="key in Object.keys(EXPRESSION_TYPE_MAP)"
            :key="key"
            :label="EXPRESSION_TYPE_MAP[key]"
            :value="key"
          ></el-option>
        </el-select>
      </el-form-item>

      <el-form-item v-if="!isJudgeNull" label="比较值2" prop="value2" :rules="!isJudgeNull && rules.value2">
        <div class="w460">
          <el-cascader
            v-model="form.value2.dataType"
            :options="CASCADER_DATA_TYPE_OPTIONS"
            :show-all-levels="false"
            :props="{ expandTrigger: 'hover' }"
          ></el-cascader>

          <SetParamsValue
            v-model="form.value2"
            :task-info="taskInfo"
            :cur-node="curNode"
            :disabled="isReadonly"
            @check="check('value2')"
          />
        </div>
      </el-form-item>
    </el-form>
    <div v-if="!isReadonly" class="bd-dialog-footer">
      <el-button class="dss-btn-circle" @click="isShow = false">取消</el-button>
      <el-button class="dss-btn-circle" type="primary" @click="onSave">保存</el-button>
    </div>
  </el-dialog>
</template>
<script>
  import { Message } from 'element-ui';
  import cloneDeep from 'lodash/cloneDeep';
  import Validator from '@/utils/validator.js';
  import { CASCADER_DATA_TYPE_OPTIONS } from '@/components/json-schema-editor/type/type';
  import { EXPRESSION_TYPE_ENUM, EXPRESSION_TYPE_MAP } from '@/enum';
  import eventBus, { EventName } from '@/components/g6-editor/event-bus';
  import TipsIcon from '@/components/tips-icon';
  import { getInitForm } from '../global/task-conf';
  import SetParamsValue from './components/SetParamsValue.vue';
  import { getUniqueNameFromItem, getIdFromItem } from '../utils';

  const valueValidator = (rule, value, callback) => {
    if (value) {
      if (!value.dataType || !value.type || !value.expression) {
        callback(new Error('请正确设置比较值'));
      }
      callback();
    }
  };

  export default {
    components: { SetParamsValue, TipsIcon },
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
      taskInfo: {
        type: Object,
        default: () => getInitForm(),
      },
      isReadonly: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        CASCADER_DATA_TYPE_OPTIONS,
        EXPRESSION_TYPE_ENUM,
        EXPRESSION_TYPE_MAP,
        form: {
          value1: {
            dataType: [],
          },
          value2: {
            dataType: [],
          },
        },
        rules: {
          name: [{ required: true, trigger: 'blur', validator: Validator.enNameValidator }],
          desc: [{ required: true, message: '不超过50个字符', trigger: 'blur' }],
          value1: [{ required: true, validator: valueValidator, trigger: ['blur', 'change'] }],
          value2: [{ required: true, validator: valueValidator, trigger: ['blur', 'change'] }],
          operator: [{ required: true, message: '请选择比较类型', trigger: 'change' }],
        },
        dialog: {
          editBranch: false,
        },
      };
    },
    computed: {
      isShow: {
        get() {
          return this.visible;
        },
        set(val) {
          if (!val) {
            this.$emit('cancel', this.uniqueName);
          }
          this.$emit('update:visible', val);
        },
      },
      uniqueName() {
        return getUniqueNameFromItem(this.curNode);
      },
      operatorId() {
        return getIdFromItem(this.curNode);
      },
      isJudgeNull() {
        return (
          this.form.operator === EXPRESSION_TYPE_ENUM.ISNULL ||
          this.form.operator === EXPRESSION_TYPE_ENUM.ISNOTNULL ||
          this.form.operator === EXPRESSION_TYPE_ENUM.ISEMPTY ||
          this.form.operator === EXPRESSION_TYPE_ENUM.ISNOTEMPTY
        );
      },
      apiAttrs() {
        return this.config?.dataAssetApi?.apiAttrs ?? [];
      },
    },
    methods: {
      check(key) {
        this.$refs.form.validateField(key);
      },
      initDataType(value) {
        return {
          ...value,
          dataType: value?.dataType?.split(/[<>]/).slice(0, 2) || [],
        };
      },
      handleOpen() {
        const { value1, value2, operator } = this.config.dataAssetApi.comparisonValue || {};
        this.form = {
          name: this.config.name,
          desc: this.config.desc,
          value1: cloneDeep(this.initDataType(value1)),
          value2: cloneDeep(this.initDataType(value2)),
          operator,
        };
        this.$nextTick(() => {
          this.$refs.form.clearValidate();
        });
      },
      validate() {
        if (!this.curNode || this.curNode.destroyed) {
          throw new Error('该节点不存在，无法保存，请重新选择节点。');
        }
        if (this.form.name === 'globalParam') {
          throw new Error('globalParam不能作为步骤名称。');
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
          const { name, desc } = this.form;
          this.$emit(
            'save',
            {
              name,
              desc,
              dataAssetApi: { apiAttrs: this.apiAttrs, comparisonValue: cloneDeep(this.formatForm(this.form)) },
            },
            cloneDeep(this.curNode.getModel())
          );
          this.isShow = false;
        } catch (error) {
          this.$message.error(error.message);
        }
      },

      formatForm(data) {
        const v1DataType = data.value1.dataType;
        const v2DataType = data.value2.dataType;
        return {
          ...data,
          value1: {
            ...data.value1,
            dataType: Array.isArray(v1DataType)
              ? v1DataType[0] + (v1DataType[1] ? `<${v1DataType[1]}>` : '')
              : v1DataType,
          },
          value2: {
            ...data.value2,
            dataType: Array.isArray(v2DataType)
              ? v2DataType[0] + (v2DataType[1] ? `<${v2DataType[1]}>` : '')
              : v2DataType,
          },
        };
      },
    },
  };
</script>
<style lang="less" scoped>
  .w150 {
    width: 150px;
  }
  .w460 {
    width: 460px;
    /deep/ .el-cascader {
      width: 150px;
      margin-right: 10px;
    }
    .param-value {
      display: inline-block;
    }
  }

  /deep/ .el-dialog__body {
    height: 400px;
  }
</style>
