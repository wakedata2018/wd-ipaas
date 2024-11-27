<!-- eslint-disable vue/no-use-v-if-with-v-for -->
<template>
  <div class="interface-info">
    <div class="interface-info__title">接口信息</div>
    <el-tabs v-if="value" :activate-name="activeName" class="interface-info__content" @input="onChangeTabs">
      <el-tab-pane v-for="item in tabs" v-if="item.isShow" :key="item.key" :label="item.label">
        <component :is="item.Component" v-bind="item.props" />
      </el-tab-pane>
    </el-tabs>
    <el-empty v-else description="暂无数据"></el-empty>
  </div>
</template>

<script lang="tsx" setup>
  import { ref, PropType, computed } from 'vue';

  import {
    RequestAndResponseParams,
    ParamsTabsKey,
    InterfaceParams,
    ParamsType,
    HttpParamKind,
    ReqMethod,
  } from '@/utils/enum/auth-list';
  import ApiParamsTable from '@/components/api-edit/api-params-table.vue';
  import JsonSchemaShow from '../../api-manage/api-publish/view-api/json-schema-show.vue';

  const props = defineProps({
    value: {
      type: Object as PropType<RequestAndResponseParams>,
      default: () => null,
    },
  });

  const activeName = ref<ParamsTabsKey>(ParamsTabsKey.RequestHeader);

  const isShow = computed(() => {
    return (key: ParamsTabsKey) => {
      return props.value.reqMethod === ReqMethod.POST && key === ParamsTabsKey.RequestBody;
    };
  });

  const getDataSource = (paramsKey: ParamsType, option: HttpParamKind) => {
    const data = props.value?.[paramsKey].filter((item: InterfaceParams) => item.httpParamKind === option);
    if (option !== HttpParamKind.BODY) {
      return data;
    } else if (paramsKey === ParamsType.requestParams) {
      return data?.[0]?.jsonSchema && JSON.parse(data[0]?.jsonSchema);
    } else {
      return data[0]?.responsePostData && JSON.parse(data[0]?.responsePostData);
    }
  };

  const onChangeTabs = (value: ParamsTabsKey) => {
    activeName.value = value;
  };

  const tabs = computed(() => {
    return [
      {
        label: '请求头',
        key: ParamsTabsKey.RequestHeader,
        Component: ApiParamsTable,
        isShow: true,
        props: {
          value: getDataSource(ParamsType.requestParams, HttpParamKind.HEAD),
          mode: 'fixed',
        },
      },
      {
        label: 'Query参数',
        key: ParamsTabsKey.RequestQuery,
        Component: ApiParamsTable,
        isShow: true,
        props: {
          value: getDataSource(ParamsType.requestParams, HttpParamKind.QUERY),
          mode: 'fixed',
        },
      },
      // POST请求才显示
      {
        label: '请求体',
        key: ParamsTabsKey.RequestBody,
        Component: JsonSchemaShow,
        isShow: isShow.value(ParamsTabsKey.RequestBody),
        props: {
          value: getDataSource(ParamsType.requestParams, HttpParamKind.BODY),
          root: true,
        },
      },
      {
        label: '响应头',
        key: ParamsTabsKey.ResponseHeader,
        Component: ApiParamsTable,
        isShow: true,
        props: {
          value: getDataSource(ParamsType.responseParams, HttpParamKind.HEAD),
          mode: 'fixed',
        },
      },
      {
        label: '响应体',
        key: ParamsTabsKey.ResponseBody,
        Component: JsonSchemaShow,
        isShow: true,
        props: {
          value: getDataSource(ParamsType.responseParams, HttpParamKind.BODY),
          root: true,
        },
      },
    ];
  });
</script>
<style lang="less" scoped>
  .interface-info {
    &__title {
      font-weight: 500;
      width: 90px;
      text-align: right;
      padding: 12px;
    }
    &__content {
      padding: 0px 12px;
    }
  }
</style>
