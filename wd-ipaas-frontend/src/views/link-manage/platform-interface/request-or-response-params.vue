<template>
  <div class="response-params">
    <div class="table-title">{{ tableTitle }}头设置</div>
    <ApiParamsTable ref="headerTableRef" v-model="headParams" params-type="HEAD" :hidden-list="hiddenCol" />

    <div v-if="isRequestTab" class="table-title">Query参数</div>
    <ApiParamsTable v-if="isRequestTab" ref="queryTableRef" v-model="queryParams" params-type="QUERY" />

    <div v-if="isPost || !isRequestTab" class="table-title">{{ tableTitle }}体</div>
    <el-row v-if="isShowBodyTree" class="table-head" :gutter="10">
      <el-col :span="6" class="table-head__item">参数名称</el-col>
      <el-col :span="4" class="table-head__item">参数类型</el-col>
      <el-col v-if="isRequestTab" :span="2" class="table-head__item">是否必填</el-col>
      <el-col :span="4" class="table-head__item">示例值</el-col>
      <el-col :span="4" class="table-head__item">描述</el-col>
      <el-col :span="4" class="table-head__item">操作</el-col>
    </el-row>
    <JsonSchemaEditor v-if="isShowBodyTree" ref="bodyTableRef" :value="tree" :is-show-required="isRequestTab" />
  </div>
</template>
<script lang="tsx" setup>
  import { computed, PropType, watch, ref } from 'vue';
  import omit from 'lodash/omit';

  import { ApiParamsTable } from '@/components/api-edit';
  import JsonSchemaEditor from '@/components/json-schema-editor';

  import { RequestOrResponseParams, ParamPosition, ReqMethodEnum, ParamsType } from './type';

  const props = defineProps({
    value: {
      type: Array as PropType<RequestOrResponseParams[]>,
      default: () => [],
    },
    reqMethod: {
      type: String as PropType<ReqMethodEnum>,
      default: () => ReqMethodEnum.GET,
    },
    paramsType: {
      type: String as PropType<ParamsType>,
      default: () => ParamsType.REQUEST,
    },
  });

  const tree = ref(); // 请求/响应体数据源

  let schemaCache: string = ''; // schema缓存，避免初始化重复渲染

  const headerTableRef = ref();
  const queryTableRef = ref();
  const bodyTableRef = ref();

  const emits = defineEmits(['input']);

  // 响应参数隐藏必填列
  const hiddenCol = computed(() => {
    if (props.paramsType === ParamsType.RESPONSE) {
      return ['required'];
    }
    return [];
  });

  /**
   * 请求参数Tab页
   */
  const isRequestTab = computed<boolean>(() => {
    return props.paramsType === ParamsType.REQUEST;
  });

  const tableTitle = computed<string>(() => {
    return isRequestTab.value ? '请求' : '响应';
  });

  /**
   * 过滤器 过滤符合条件的
   *
   */
  const filterType =
    (type: ParamPosition) =>
    (value: any): value is RequestOrResponseParams => {
      return value.httpParamKind === type;
    };

  /**
   * 过滤器 过滤不符合条件的
   *
   */
  const filterOtherType =
    (type: ParamPosition) =>
    (value: any): value is RequestOrResponseParams => {
      return value.httpParamKind !== type;
    };

  const isShowBodyTree = computed(() => {
    return (props.reqMethod === ReqMethodEnum.POST && tree.value) || (!isRequestTab.value && tree.value);
  });

  /**
   * POST请求
   */

  const isPost = computed(() => {
    return props.reqMethod === ReqMethodEnum.POST;
  });

  /**
   * POST请求过滤掉请求体参数
   */
  const delRequestBody = list => {
    return list.filter(item => item.httpParamKind !== 'BODY');
  };

  /**
   * 请求头/响应头
   */
  const headParams = computed<RequestOrResponseParams[]>({
    get() {
      return props.value?.filter(filterType(ParamPosition.HEAD));
    },
    set(val) {
      let other = props.value?.filter(filterOtherType(ParamPosition.HEAD)) ?? [];
      if (!isPost.value && props.paramsType !== ParamsType.RESPONSE) {
        other = delRequestBody(other);
      }
      const newVal = val.map(item => {
        return omit(item, ['type']);
      });
      emits('input', [...other, ...newVal]);
    },
  });

  /**
   * Query参数
   */
  const queryParams = computed<RequestOrResponseParams[]>({
    get() {
      return props.value?.filter(filterType(ParamPosition.QUERY));
    },
    set(val) {
      let other = props.value?.filter(filterOtherType(ParamPosition.QUERY)) ?? [];
      if (!isPost.value && props.paramsType !== ParamsType.RESPONSE) {
        other = delRequestBody(other);
      }
      const newVal = val.map(item => {
        return omit(item, ['type']);
      });
      emits('input', [...other, ...newVal]);
    },
  });

  /**
   * 请求体/响应体
   */
  const bodyParams = computed<RequestOrResponseParams | {}>(() => {
    return (
      props.value?.find(item => item.httpParamKind === ParamPosition.BODY) ?? {
        assetColumns: 'BODY',
        assetDataType: 'json',
        httpParamKind: 'BODY',
      }
    );
  });

  /**
   * 初始化请求体/响应体数据源
   */
  const initTree = () => {
    const init = { root: { type: 'object', name: 'root', description: '根层级' } };
    const schema =
      props.paramsType === ParamsType.REQUEST ? bodyParams.value?.jsonSchema : bodyParams.value?.responsePostData;
    if (schemaCache !== schema) {
      tree.value = schema ? JSON.parse(schema) : init;
    }
  };

  watch(
    bodyParams,
    () => {
      initTree();
    },
    { immediate: true }
  );

  watch(
    tree,
    value => {
      if (props.reqMethod === ReqMethodEnum.POST || props.paramsType === ParamsType.RESPONSE) {
        const serialized = (schemaCache = JSON.stringify(value));
        const other = props.value?.filter(filterOtherType(ParamPosition.BODY)) ?? [];
        const newVal =
          props.paramsType === ParamsType.REQUEST ? { jsonSchema: serialized } : { responsePostData: serialized };
        emits('input', [...other, { ...bodyParams.value, ...newVal }]);
      }
    },
    { deep: true }
  );

  /**
   *  切换请求方式后初始化BODY参数
   */
  watch(
    () => props.reqMethod,
    (cur, pre) => {
      if (cur === ReqMethodEnum.GET && cur !== pre) {
        tree.value = { root: { type: 'object', name: 'root', description: '根层级' } };
      }
    }
  );
  const validator = async () => {
    await headerTableRef.value.validate();
    await queryTableRef.value?.validate();
    await bodyTableRef.value?.validator();
  };

  defineExpose({
    validator,
  });
</script>
<style lang="less" scoped>
  .response-params {
    .table-title {
      font-weight: 600;
      padding: 10px 0px;
    }

    .table-head {
      border-bottom: 1px solid #ebeef5;
      padding: 6px 0px;
      background-color: #f3f6f8;
      color: #909399;
      font-weight: bold;
      margin: 0px 0px 5px 0px !important;
      display: flex;
      align-items: center;
    }

    .table-head__item {
      padding: 0 10px !important;
      line-height: 23px;
      font-size: 12px;
    }
  }
</style>
