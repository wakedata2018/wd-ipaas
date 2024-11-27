<template>
  <el-dialog
    class="bd-dialog platform-interface-dialog"
    custom-class="anim-left"
    :visible.sync="visible"
    append-to-body
    fullscreen
    :title="title"
    :close-on-click-modal="false"
  >
    <FatFormTabs
      v-if="visible"
      ref="interfaceForm"
      :submit="handleSubmit"
      label-width="100px"
      :request="getRequestData"
      :before-cancel="onCancel"
      :initial-value="initialValue"
      :page-layout="drawLayout"
      @values-change="onFormValueChange"
    >
      <FatFormTabPane label="基础信息" name="basic">
        <FatFormGroup>
          <FatFormItem
            :col="12"
            prop="connectorApi.connectorId"
            label="所属连接器"
            value-type="select"
            :value-props="{
              options: getPlatformOptions,
            }"
            disabled
            required
          ></FatFormItem>
          <FatFormItem
            :col="12"
            prop="connectorApi.apiGroupId"
            label="接口分组"
            value-type="select"
            :value-props="{
              options: interfaceGroupOptions,
            }"
            disabled
            required
          ></FatFormItem>
        </FatFormGroup>
        <FatFormItem
          prop="connectorApi.apiName"
          label="接口名称"
          :value-props="{
            placeholder: '最多输入100个字',
            maxlength: 100,
          }"
          required
        ></FatFormItem>
        <FatFormItem
          prop="connectorApi.apiMethod"
          label="接口地址"
          :value-props="{
            placeholder: '最多输入200个字',
            maxlength: 200,
          }"
          required
        ></FatFormItem>
        <FatFormGroup>
          <FatFormItem
            :col="12"
            prop="connectorApi.reqMethod"
            label="请求方法"
            value-type="radio"
            :value-props="{
              options: [
                {
                  label: 'GET',
                  value: 'GET',
                },
                {
                  label: 'POST',
                  value: 'POST',
                },
              ],
            }"
            initial-value="GET"
            required
          ></FatFormItem>
          <FatFormItem
            :col="12"
            prop="connectorApi.apiType"
            label="接口类型"
            value-type="radio"
            :value-props="{
              options: [
                {
                  label: 'HTTP/HTTPS',
                  value: 'EXTERNAL_HTTP',
                },
                // {
                //   label: 'WebService',
                //   value: 'WEB_SERVICE',
                // },
              ],
            }"
            initial-value="EXTERNAL_HTTP"
            required
          ></FatFormItem>
        </FatFormGroup>
        <FatFormItem
          prop="connectorApi.apiDescription"
          value-type="textarea"
          label="接口描述"
          :value-props="{
            maxlength: 1000,
          }"
        ></FatFormItem>
      </FatFormTabPane>

      <FatFormTabPane label="请求参数" name="request">
        <FatFormConsumer v-slot="scope">
          <RequestOrResponseParams
            ref="requestParamsRef"
            v-model="scope.values.requestParams"
            :req-method="scope.values.connectorApi.reqMethod"
            :params-type="ParamsType.REQUEST"
          />
        </FatFormConsumer>
      </FatFormTabPane>

      <FatFormTabPane label="响应参数" name="response">
        <FatFormConsumer v-slot="scope">
          <RequestOrResponseParams
            ref="responseParamsRef"
            v-model="scope.values.responseParams"
            :params-type="ParamsType.RESPONSE"
          />
        </FatFormConsumer>
      </FatFormTabPane>
    </FatFormTabs>
  </el-dialog>
</template>

<script lang="tsx" setup>
  import { computed, ref } from 'vue';

  import {
    FatFormTabs,
    FatFormTabPane,
    FatFormItem,
    FatFormGroup,
    FatFormConsumer,
    useFatFormTabsRef,
  } from '@wakeadmin/components';
  import RequestOrResponseParams from './request-or-response-params.vue';
  import { Message } from 'element-ui';

  import drawLayout from '@/components/draw-layout';
  import InterfaceApi from '@/api/link-interface';
  import { PlatFormInterfaceList, PlatFormInterfaceInfo, ParamsType, CurSelectGroupInfo } from './type';

  type Options = {
    label: string;
    value: number;
  };

  const visible = ref<boolean>(false);
  const id = ref<number>();
  const currentGroup = ref();
  const interfaceGroupOptions = ref<Options[]>([]);
  const interfaceForm = useFatFormTabsRef();
  const initialValue = ref();
  const requestParamsRef = ref();
  const responseParamsRef = ref();
  // 定义Emits
  const emits = defineEmits<{
    (e: 'refresh'): void;
  }>();

  const open = async (item: PlatFormInterfaceList, extra: CurSelectGroupInfo) => {
    id.value = item?.id;

    // 新增接口
    if (!id.value) {
      currentGroup.value = {
        connectorApi: {
          connectorId: extra.parentId,
          apiGroupId: extra.id,
        },
      };
      getInterfaceGroupOptions(extra.parentId);
    } else {
      // 编辑接口
      getInterfaceGroupOptions(item?.connectorId);
    }
    visible.value = true;
  };

  const title = computed(() => {
    return id.value ? '编辑接口' : '新增接口';
  });

  // 获取所属平台
  const getPlatformOptions = async () => {
    const response = await InterfaceApi.queryPlatForm();
    return response.map(item => {
      return {
        label: item.name,
        value: item.id,
      };
    });
  };

  // 获取所属接口分类
  const getInterfaceGroupOptions = async (idNum?: number) => {
    interfaceGroupOptions.value = [];
    if (idNum) {
      const response = await InterfaceApi.queryInterfaceGroup({ connectorId: idNum });
      interfaceGroupOptions.value = response?.[0]?.connectorApiGroupDTOList?.map(item => {
        return {
          label: item.groupName,
          value: item.id,
        };
      });
    }
    return interfaceGroupOptions.value;
  };

  const onFormValueChange = async (values: PlatFormInterfaceInfo, props: string, value: number) => {
    if (props === 'connectorApi.connectorId') {
      getInterfaceGroupOptions(value);
      interfaceForm.value?.setFieldValue('connectorApi.apiGroupId', undefined);
    }
  };

  const handleSubmit = async (values: any) => {
    try {
      await requestParamsRef.value?.validator();
      await responseParamsRef.value?.validator();
      if (!values.requestParams) {
        values.requestParams = [];
      }
      if (!values.responseParams) {
        values.responseParams = [];
      }
      if (id.value) {
        await InterfaceApi.updateInterface(values);
        Message.success('更新成功');
      } else {
        await InterfaceApi.addInterface(values);
        Message.success('保存成功');
      }
      emits('refresh');
      visible.value = false;
    } catch (err) {
      Message.error(err.message);
    }
  };

  const getRequestData = async () => {
    if (id.value) {
      const response = await InterfaceApi.getApiDetail({ id: id.value });
      return response;
    }
    return {
      ...currentGroup.value,
    };
  };

  const onCancel = (done: () => void) => {
    visible.value = false;
  };

  defineExpose({
    open,
  });
</script>

<style lang="less" scoped>
  .platform-interface-dialog {
    /deep/ .draw-layout {
      margin-bottom: 80px;
      .footer {
        position: fixed;
        width: 80%;
        left: 20%;
      }
    }
  }
</style>
