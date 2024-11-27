<template>
  <div class="category-craate-edit">
    <div class="category-content">
      <span class="category-title">{{ title }}</span>
      <div class="category-form">
        <el-form ref="formRef" :model="form" label-width="auto">
          <el-row v-if="form.parentName" type="flex" justify="space-between">
            <el-col :span="21">
              <el-form-item label="一级分类名称" prop="parentName">
                <el-input v-model.trim="form.parentName" disabled></el-input>
              </el-form-item>
            </el-col>
            <el-col v-if="isEdit" :span="3" class="category-form-del">
              <el-button type="text" @click="onDel">删除分类</el-button>
            </el-col>
          </el-row>
          <el-row type="flex" justify="space-between">
            <el-col :span="21">
              <el-form-item label="分类名称" prop="groupName" :rules="rules">
                <el-input v-model.trim="form.groupName" maxlength="200" placeholder="最多输入200个字"></el-input>
              </el-form-item>
            </el-col>
            <el-col v-if="isEdit && !form.parentName" :span="3" class="category-form-del">
              <el-button type="text" @click="onDel">删除分类</el-button>
            </el-col>
          </el-row>
          <el-form-item label="描述" prop="description">
            <el-input
              v-model.trim="form.description"
              type="textarea"
              :rows="5"
              maxlength="200"
              placeholder="最多输入200个字"
            ></el-input>
          </el-form-item>
        </el-form>
      </div>
    </div>
    <div class="category-footer">
      <el-button type="primary" size="mini" @click="onSave">保存</el-button>
      <el-button size="mini" @click="onClose">关闭</el-button>
    </div>
  </div>
</template>

<script lang="tsx" setup>
  import { ref, computed, watch, nextTick } from 'vue';
  import CategoryApi from '@/api/platform-category';
  import { statusType, CategoryItemInfo, ActionType } from './type';

  const props = defineProps<{
    value: CategoryItemInfo | undefined;
    status: ActionType;
  }>();

  const formRef = ref();
  const form = ref<CategoryItemInfo>({} as any);
  const rules = {
    required: true,
    message: '分类名称不能为空',
  };

  const title = computed(() => {
    return statusType[props.status];
  });

  // 是否是编辑分类
  const isEdit = computed(() => {
    return props.status === ActionType.Edit;
  });

  // 定义Emits
  const emits = defineEmits<{
    (e: 'save', value: CategoryItemInfo): void;
    (e: 'del', id: number): void;
    (e: 'close'): void;
  }>();

  const onSave = () => {
    emits('save', form.value);
  };

  const onDel = () => {
    props?.value?.id && emits('del', props.value.id);
  };

  const validator = async () => {
    return new Promise((resolve, reject) => {
      formRef.value.validate((valid: boolean) => {
        if (!valid) {
          reject(new Error('分类名称不能为空'));
          return;
        }
        resolve('');
      });
    });
  };

  const clearValidator = () => {
    formRef.value.clearValidate();
  };

  const initForm = async () => {
    const response = await CategoryApi.getCategory({
      id: props.value?.id,
    });
    form.value = response[0];
  };

  watch(
    () => [props.value, props.status],
    (newVal: any) => {
      if (isEdit.value) {
        initForm();
      } else {
        form.value = {} as any;
        nextTick(() => {
          clearValidator();
        });
        if (props.status === ActionType.CreateChild) {
          form.value.parentName = props.value?.groupName;
        }
      }
    },
    { immediate: true, deep: true }
  );

  const onClose = () => {
    emits('close');
  };

  defineExpose({
    validator,
  });
</script>

<style lang="less" scoped>
  .category-craate-edit {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    height: 100%;

    .category-content {
      flex: 1;

      .category-title {
        display: flex;
        align-items: center;
        line-height: 40px;
        padding-left: 10px;
        border-bottom: 1px solid #e6e6e6;
        font-weight: 600;
        font-size: 14px;
      }

      .category-form {
        padding: 10px 40px;
        font-size: 14px;

        .category-form-del {
          text-align: right;
        }
      }
    }

    .category-footer {
      align-self: center;
      height: 60px;
      display: flex;
      align-items: center;
    }
  }
</style>
