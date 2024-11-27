<template>
  <el-dialog
    class="bd-dialog params-mapping-dialog"
    :title="title"
    :visible="props.visible"
    :close-on-click-modal="false"
    lock-scroll
    custom-class="anim-left"
    fullscreen
    append-to-body
    @open="onOpen"
    @close="onClose"
  >
    <el-scrollbar :class="{ 'page-scrollbar': true, 'read-only': isReadonly }">
      <el-form
        ref="formRef"
        v-loading="loading"
        :model="form"
        class="params-mapping-form"
        :rules="rules"
        label-width="auto"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item prop="respParamMappingRuleName" label="规则名称">
              <el-input v-model="form.respParamMappingRuleName" :disabled="isReadonly"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="status" label="是否启用">
              <el-switch v-model="form.status" :disabled="isReadonly" :inactive-value="0" :active-value="1"></el-switch>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item prop="description" label="描述">
              <el-input
                v-model="form.description"
                type="textarea"
                rows="4"
                maxlength="256"
                placeholder="请输入描述"
                :disabled="isReadonly"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <div class="title">集成云返回体参数结构</div>
        <el-table :data="commonParams" class="dss-table bd-table params-table">
          <el-table-column prop="datasourceTableColumnName" label="参数名称"> </el-table-column>
          <el-table-column prop="datasourceTableColumnType" label="类型"> </el-table-column>
          <el-table-column prop="datasourceTableColumnDesc" label="描述"> </el-table-column>
        </el-table>
        <div class="title">系统返回体参数结构</div>
        <JsonSchemaMapping
          v-if="visible"
          :tree="responseParamsTree"
          :is-readonly="isReadonly"
          :mapping-options="mappingOptions"
        />
      </el-form>
    </el-scrollbar>
    <div v-if="!isReadonly" class="bd-dialog-footer">
      <el-button size="medium" @click="onClose">取消</el-button>
      <el-button size="medium" type="primary" class="bd-button" @click="onSave">保存</el-button>
    </div>
  </el-dialog>
</template>

<script lang="tsx" setup>
  import { computed, ref, nextTick, watch } from 'vue';

  import { Message } from 'element-ui';
  import JsonSchemaMapping from './json-schema-mapping.vue';

  import { ParamsMappingInfo, CommonParams } from '@/utils/enum/params-mapping';
  import ParamsMappingApi from '@/api/params-mapping';
  import { getRules } from '@/utils/mapping';
  import textUtils from '@/utils/text-utils';

  const form = ref<ParamsMappingInfo>({
    respParamMappingRuleName: '',
    description: '',
    status: 0,
    respParamMappingRuleJsonSchema: '',
  });

  const props = defineProps({
    id: {
      type: Number,
      default: null,
    },
    visible: {
      type: Boolean,
      default: false,
    },
    isReadonly: {
      type: Boolean,
      default: false,
    },
  });
  const commonParams = ref<CommonParams[]>([]);

  const loading = ref(true);

  const responseParamsTree = ref();

  // 表单实例
  const formRef = ref();

  const rules = {
    respParamMappingRuleName: [{ required: true, message: '请输入规则名称', trigger: 'blur' }],
    status: [{ required: true, message: '请选择', trigger: 'blur' }],
  };

  // 根据动态数据 动态筛选可用的选择映射参数列表 data、msg 可用重复被选
  const mappingOptions = ref([]);

  // 获取集成云返回体参数结构
  const defaultMappingOptions = computed(() => {
    const list = commonParams.value.map(item => {
      return {
        label: item.datasourceTableColumnName,
        value: item.datasourceTableColumnName,
      };
    });
    return list.filter(item => item.label !== 'code');
  });

  // 定义Emits
  const emits = defineEmits<{
    (e: 'update:visible', value: boolean): void;
    (e: 'save'): void;
  }>();

  const onClose = () => {
    emits('update:visible', false);
  };

  const title = computed(() => {
    let titlePrefix = '新增';
    if (props.id) {
      titlePrefix = props.isReadonly ? '查看' : '编辑';
    }
    return `${titlePrefix}规则`;
  });

  // 获取表单数据
  const getParamsMapping = async () => {
    if (props.id) {
      const res = await ParamsMappingApi.getParamsMappingById(props.id);
      const { respParamMappingRuleName, description, status, respParamMappingRuleJsonSchema } = res;
      form.value = {
        respParamMappingRuleName,
        description,
        status,
        respParamMappingRuleJsonSchema,
      };
      const jsonSchemaStr = form.value.respParamMappingRuleJsonSchema;
      if (jsonSchemaStr) {
        responseParamsTree.value = JSON.parse(jsonSchemaStr);
      } else {
        responseParamsTree.value = {
          root: {
            type: 'object',
            name: 'root',
            description: '根层级',
            properties: {},
          },
        };
      }
    }
  };

  // 获取集成云返回体格式数据
  const getCommonParams = async () => {
    commonParams.value = await ParamsMappingApi.getCommonParamsList();
  };

  // 重置页面
  const reSetForm = () => {
    form.value = {
      respParamMappingRuleName: '',
      description: '',
      status: 0,
      respParamMappingRuleJsonSchema: '',
    };
    responseParamsTree.value = {
      root: {
        type: 'object',
        name: 'root',
        description: '根层级',
        properties: {},
      },
    };
    nextTick(() => {
      formRef.value.clearValidate();
    });
  };

  // 查找已用的映射参数值 筛选出可用映射参数列表
  const changeParamsMappingRule = (tree: any) => {
    const rule = getRules(tree);
    // 查找已用的映射参数值 筛选出可用映射参数列表
    mappingOptions.value = defaultMappingOptions.value.filter(field => {
      // data和msg固定可用
      const except = ['data', 'msg'];
      return except.includes(field.value) || !rule.includes(field.value);
    });
  };

  const onOpen = async () => {
    loading.value = true;
    // 初始化
    reSetForm();
    // 获取集成云返回体格式数据
    await getCommonParams();
    // 获取表单数据
    await getParamsMapping();

    loading.value = false;
  };

  // 保存
  const onSave = async () => {
    formRef.value.validate(async (valid: boolean) => {
      if (valid) {
        const validateTree = isValidateTree(responseParamsTree.value.root.properties);
        if (validateTree) {
          const jsonSchemaStr = JSON.stringify(responseParamsTree.value);

          let params = {
            ...form.value,
            respParamMappingRuleJsonSchema: jsonSchemaStr,
          };

          // 更新
          if (props.id) {
            params = {
              ...params,
              id: props.id,
            };
          }

          try {
            await ParamsMappingApi.addOrUpdateParamsMapping(params);
            Message.success('保存成功');
            onClose();
            emits('save');
          } catch (err) {
            Message.error(err.message);
          }
        }
      } else {
        return false;
      }
    });
  };

  // 校验树是否为空和重名
  const isValidateTree = (data: any) => {
    const res = textUtils.hasEmptyOrMultiName(data);
    if (!res.success) {
      Message.error('参数名称' + res.message);
    }
    return res.success;
  };

  // 监听树变动
  watch(
    responseParamsTree,
    (curVal, oldVal) => {
      if (curVal) {
        const tree = curVal.root.properties;
        changeParamsMappingRule(tree);
      }
    },
    {
      deep: true,
    }
  );
</script>
<style lang="less" scoped>
  .params-mapping-dialog {
    /deep/ .el-dialog {
      overflow: hidden;
    }
    .page-scrollbar {
      box-sizing: border-box;
      overflow-x: hidden;
      margin-bottom: 10px;
      /deep/ .el-scrollbar__wrap {
        overflow-x: hidden;
        max-height: calc(100vh - 200px);
        margin-bottom: 0px !important;
      }

      .params-mapping-form {
        padding: 10px;
        margin-bottom: 40px;

        .description {
          max-width: 700px;
        }

        .title {
          height: 22px;
          font-size: 16px;
          margin: 10px 0;
          font-weight: 600;
          color: #333333;
          line-height: 22px;
        }

        .params-table {
          margin: 10px 0px;
        }
      }
    }
    .page-scrollbar.read-only {
      /deep/ .el-scrollbar__wrap {
        max-height: calc(100vh - 120px);
      }
    }
  }
</style>
