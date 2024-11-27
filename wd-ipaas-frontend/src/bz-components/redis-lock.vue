<template>
  <div class="redis-lock-page">
    <el-form
      ref="formRef"
      :model="redisForm"
      :rules="redisForm.enableRedisLock ? rules : {}"
      :disabled="isReadonly"
      label-position="right"
      label-width="100px"
    >
      <el-row>
        <el-form-item label="是否启用" prop="enableRedisLock">
          <el-radio-group v-model="redisForm.enableRedisLock" @change="enableChange">
            <el-radio v-for="item in ENABLE_TYPE" :key="item.value" :label="item.value">{{ item.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-row>
      <el-row>
        <el-form-item label="锁类型" prop="lockType">
          <el-radio-group v-model="redisForm.lockType">
            <el-radio v-for="item in LOCK_TYPE" :key="item.value" :label="item.value">{{ item.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-row>
      <el-row>
        <el-form-item label="检测字段" prop="keyParams">
          <el-select v-model="redisForm.keyParams" placeholder="请选择" multiple collapse-tags>
            <el-option v-for="item in filterFields" :key="item" :value="item" :label="item" />
          </el-select>
        </el-form-item>
      </el-row>
      <el-row>
        <el-form-item label="等待时间" prop="waitTime">
          <el-input-number
            v-model.trim="redisForm.waitTime"
            controls-position="right"
            :min="0"
            :max="60"
          ></el-input-number>
          <span class="text">秒</span>
        </el-form-item>
      </el-row>
      <el-row>
        <el-form-item label="锁释放时间" prop="leaseTime">
          <el-input-number
            v-model.trim="redisForm.leaseTime"
            controls-position="right"
            :min="0"
            :max="60"
          ></el-input-number>
          <span class="text">秒</span>
        </el-form-item>
      </el-row>
      <el-row>
        <el-form-item label="备注">
          <el-input
            v-model="redisForm.description"
            type="textarea"
            maxlength="1000"
            :rows="8"
            placeholder="限制输入1000个字符"
          />
        </el-form-item>
      </el-row>
    </el-form>
  </div>
</template>

<script lang="ts" setup>
  import { ref, toRefs, PropType, watch } from 'vue';
  import { Form as ElForm } from 'element-ui';
  import { ENABLE_TYPE, LOCK_TYPE } from '@/utils/enum';
  import { getRedisForm } from '@/utils/redisLockUtil';

  const props = defineProps({
    data: {
      type: Object,
      // eslint-disable-next-line no-empty-function
      default: () => {},
    },
    checkFields: {
      type: Array as PropType<any[]>,
      default: () => [],
    },
    isReadonly: {
      type: Boolean,
      default: false,
    },
  });

  const redisForm = ref<any>({});
  const { data, checkFields } = toRefs(props);

  const rules = ref({
    enableRedisLock: [
      {
        required: true,
        message: '请选择是否启用',
      },
    ],
    lockType: [
      {
        required: true,
        message: '请选择锁类型',
      },
    ],
    keyParams: [
      {
        required: true,
        message: '请选择检测字段',
      },
    ],
    waitTime: [
      {
        required: true,
        message: '请输入等待时间',
      },
    ],
    leaseTime: [
      {
        required: true,
        message: '请输入锁释放时间',
      },
    ],
  });

  const formRef = ref<ElForm | null>(null);
  const filterFields = ref<any[]>([]);

  const resetFields = () => {
    const refEl = formRef.value as ElForm;
    if (refEl && refEl.resetFields) {
      refEl.resetFields();
    }
  };

  const validator = async () => {
    return new Promise((resolve, reject) => {
      formRef.value.validate((valid: boolean) => {
        if (!valid) {
          reject(new Error('请检查必填项'));
          return false;
        }
        resolve('');
      });
    });
  };

  const clearValidate = () => {
    const refEl = formRef.value as ElForm;
    if (refEl && refEl.clearValidate) {
      refEl.clearValidate();
    }
  };

  const enableChange = () => {
    clearValidate();
  };

  const compareOldNewValue = (keyParams: any[], filterFieldList: any[]) => {
    keyParams?.map((item, index) => {
      return !filterFieldList?.includes(item) && keyParams?.splice(index, 1);
    });
  };

  watch(
    data,
    () => {
      resetFields();
      redisForm.value = data.value ?? getRedisForm();
    },
    {
      immediate: true,
    }
  );

  watch(
    checkFields,
    () => {
      filterFields.value = checkFields.value?.map(item => item.assetColumns);
      compareOldNewValue(redisForm.value.keyParams, filterFields.value);
    },
    {
      immediate: true,
    }
  );

  defineExpose({
    redisForm,
    validator,
  });
</script>

<style lang="less" scoped>
  .text {
    margin-left: 5px;
  }
</style>
