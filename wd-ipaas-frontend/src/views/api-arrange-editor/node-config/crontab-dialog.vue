<template>
  <el-dialog
    :visible.sync="isShow"
    title="定时任务"
    :close-on-click-modal="false"
    :lock-scroll="false"
    class="bd-dialog scrollbar-dialog not-lock-scroll"
    top="80px"
    width="900px"
  >
    <el-scrollbar class="scrollbar">
      <el-form ref="form" :model="form" :rules="rules" label-position="left" label-width="140px">
        <el-form-item label="步骤名称" prop="name">
          <el-input v-model="form.name" size="mini" maxlength="50"></el-input>
        </el-form-item>
        <el-form-item label="步骤描述" prop="desc">
          <el-input v-model="form.desc" size="mini" maxlength="50"></el-input>
        </el-form-item>
        <el-form-item label="任务描述" prop="taskDescription">
          <el-input v-model="form.taskDescription" size="mini" maxlength="50"></el-input>
        </el-form-item>
        <el-form-item label="执行周期" prop="timeForm">
          <el-form ref="timeForm" :model="timeForm" inline>
            每
            <el-form-item prop="everyTime">
              <el-input-number v-model="timeForm.cycleValue" :min="0" :max="maxNum" tyle="width: 100px">
              </el-input-number>
            </el-form-item>
            <el-form-item prop="timeType">
              <el-select
                v-model="timeForm.cycleUnit"
                placeholder="请选择"
                style="width: 100px"
                @change="handleCycleUnitChange"
              >
                <el-option
                  v-for="item in timeTypeEnum.TIME_TYPE_LIST"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </el-select>
              执行一次
            </el-form-item>
          </el-form>
        </el-form-item>
      </el-form>
    </el-scrollbar>
    <div slot="footer" class="bd-dialog-footer">
      <el-button class="dss-btn-circle" @click="isShow = false">取消</el-button>
      <el-button class="dss-btn-circle" type="primary" @click="onSave">提交</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import cloneDeep from 'lodash/cloneDeep';
  import { Message } from 'element-ui';
  import timeTypeEnum from '@/utils/enum/time-type.js';
  import { getIdFromItem, getUniqueNameFromItem } from '../utils';

  export default {
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
    },
    data() {
      const timeFormValidator = (rule, value, callback) => {
        if (this.timeForm.cycleValue || this.timeForm.cycleValue === 0) {
          callback();
        } else {
          callback(new Error('请输入执行周期'));
        }
      };
      const nameValidator = (rule, value, callback) => {
        if (value) {
          const reg = /[a-zA-Z]+$/;
          if (reg.test(value)) {
            callback();
          } else {
            callback(new Error('英文格式错误'));
          }
        } else {
          callback(new Error('英文名称为空'));
        }
      };
      this.rules = {
        name: [
          {
            required: true,
            trigger: 'blur',
            validator: nameValidator,
          },
        ],
        desc: [
          {
            required: true,
            trigger: 'blur',
            message: '请输入步骤描述',
          },
        ],
        taskDescription: [
          {
            required: true,
            trigger: 'blur',
            message: '请输入任务描述',
          },
        ],
        timeForm: [
          {
            required: true,
            trigger: 'blur',
            validator: timeFormValidator,
          },
        ],
      };
      return {
        form: {},
        timeForm: {
          cycleUnit: timeTypeEnum.TIME_TYPE.second.value,
          cycleValue: '',
        },
        timeTypeEnum,
      };
    },
    computed: {
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
        operatorId() {
          return getIdFromItem(this.curNode);
        },
      },
      maxNum() {
        const { second, min, hour, day, month, year } = timeTypeEnum.TIME_TYPE;
        if (this.timeForm.cycleUnit === second.value || this.timeForm.cycleUnit === min.value) {
          return second.maxNum;
        } else if (this.timeForm.cycleUnit === hour.value) {
          return hour.maxNum;
        } else if (this.timeForm.cycleUnit === day.value) {
          return day.maxNum;
        } else if (this.timeForm.cycleUnit === month.value) {
          return month.maxNum;
        } else {
          return year.maxNum;
        }
      },
    },
    watch: {
      isShow(val) {
        this.resetFields();
        this.$nextTick(() => {
          if (val) {
            this.initForm();
          }
        });
      },
    },
    methods: {
      resetFields() {
        const form = this.$refs.form;
        const timeForm = this.$refs.timeForm;
        if (form && form.resetFields) {
          form.resetFields();
        }
        if (timeForm && timeForm.resetFields) {
          timeForm.resetFields();
        }
      },
      initForm() {
        this.form = this.$plain(this.config);
        this.timeForm.cycleValue = this.config.cycleValue;
        this.timeForm.cycleUnit = this.config.cycleUnit;
      },
      handleCycleUnitChange() {
        if (this.timeForm.cycleValue > this.maxNum) {
          this.timeForm.cycleValue = this.maxNum;
        }
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
        const form = this.$refs.form;
        form.validate(isValidate => {
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
          this.$emit('save', { ...this.form, ...this.timeForm }, cloneDeep(this.curNode.getModel()));
          this.isShow = false;
        } catch (error) {
          this.$message.error(error.message);
        }
      },
    },
  };
</script>

<style scoped lang="less"></style>
