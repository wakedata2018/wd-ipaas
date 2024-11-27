<template>
  <el-dialog
    :visible="props.visible"
    class="api-import-export-dialog"
    width="650px"
    :close-on-click-modal="false"
    @open="handlerOpen"
    @close="handlerClose"
  >
    <template #title>
      <div class="title">导入/导出</div>
    </template>
    <FatForm
      ref="form"
      class="content"
      :submit="handleSubmit"
      :initial-value="initValue"
      @values-change="onFormValueChange"
    >
      <FatFormItem
        prop="operationType"
        label="操作类型"
        value-type="radio"
        :value-props="{ options: OperationTypeOptions }"
        required
      ></FatFormItem>
      <FatFormItem
        v-if="!isExportDoc"
        key="apiGroupId"
        prop="apiGroupId"
        label="选择分类"
        value-type="select"
        :value-props="{ options: ClassifyOptions }"
        :width="190"
        required
      ></FatFormItem>
      <FatFormItem
        v-if="isExportDoc"
        key="apiGroupIds"
        prop="apiGroupIds"
        label="选择分类"
        value-type="select"
        :value-props="{ options: ClassifyOptions, multiple: true, clearable: true, placeholder: '全部分类' }"
        :width="190"
      ></FatFormItem>
      <FatFormItem
        v-if="!isExportDoc"
        prop="apiTypeList"
        label="接口类型"
        value-type="radio"
        :value-props="{ options: InterFaceTypeOptions }"
        required
        disabled
      ></FatFormItem>
      <FatFormItem
        v-if="isExportDoc"
        prop="docType"
        label="文档类型"
        value-type="radio"
        :value-props="{ options: DocTypeOptions }"
        required
        disabled
      ></FatFormItem>
      <FatFormItem
        v-if="isImport"
        prop="uploadFile"
        label="导入文件"
        value-type="files"
        message="仅支持从集成云导出的接口文件格式"
        :value-props="{
          renderPlaceholder,
          onSuccess: onUploadSuccess,
          beforeUpload: onBeforeUploadFile,
          beforeRemove: onBeformRemoveFile,
        }"
        required
      ></FatFormItem>
      <template #submitter="scope">
        <div class="footer-bottom">
          <el-button @click="handlerClose">取消</el-button>
          <el-button type="primary" :disabled="fileUploadStatus" @click="scope.submit">保存</el-button>
        </div>
      </template>
    </FatForm>
  </el-dialog>
</template>

<script lang="tsx" setup name="ApiImportExportDialog">
  import { computed, ref } from 'vue';

  import { FatForm, FatFormItem, useFatFormRef } from '@wakeadmin/components';
  import { MessageBox, Message } from 'element-ui';
  import apiGroup from '@/api/api-group';
  import apiController from '@/api/api-controller';
  import {
    InterFaceTypeOptions,
    OperationTypeOptions,
    DocTypeOptions,
    OperationType,
    InterfaceClassify,
    InterFaceType,
    ExportData,
    ImportData,
    DocType,
    API_ENUM,
  } from '@/utils/enum';

  const props = defineProps({
    visible: {
      type: Boolean,
      default: false,
    },
  });

  const form = useFatFormRef();
  // 文件上传接口返回地址
  const fileUrl = ref<String>();
  // 文件上传状态
  const fileUploadStatus = ref<Boolean>(false);

  const renderPlaceholder = () => {
    return <a class="choose-file">选择文件</a>;
  };

  const initValue = {
    operationType: OperationType.IMPORT,
    apiTypeList: InterFaceType.HTTP,
    docType: DocType.MD,
    apiGroupIds: [],
    apiGroupId: '',
  };

  // 定义Emits
  const emits = defineEmits<{
    (e: 'update:visible', value: boolean): void;
    (e: 'saved'): void;
  }>();

  const ClassifyOptions = async () => {
    const res = await apiGroup.getThemeList();

    return res.data.map((item: InterfaceClassify) => {
      return {
        label: item.groupName,
        value: item.id,
      };
    });
  };

  // 导入接口操作类型
  const isImport = computed(() => {
    const operationType = form.value?.getFieldValue('operationType');
    return operationType === OperationType.IMPORT;
  });

  // 导出文档操作类型
  const isExportDoc = computed(() => {
    const operationType = form.value?.getFieldValue('operationType');
    return operationType === OperationType.EXPORT_DOC;
  });

  const handlerOpen = () => {
    ClassifyOptions();
  };

  const handlerClose = () => {
    emits('update:visible', false);
  };

  // 保存
  const handleSubmit = async (values: ImportData & ExportData) => {
    const type = form.value?.getFieldValue('operationType');
    let params = null;
    switch (type) {
      // 导入
      case OperationType.IMPORT:
        params = {
          apiGroupId: values.apiGroupId,
          tempFilePath: fileUrl.value,
        };
        break;
      // 导出
      case OperationType.EXPORT:
        params = {
          apiGroupIdList: [values.apiGroupId],
          apiTypeList: [values.apiTypeList],
        };
        break;
      // 文档导出
      case OperationType.EXPORT_DOC:
        params = {
          apiGroupIdList: values.apiGroupIds,
          apiTypeList: Object.values(API_ENUM).map(item => item),
        };
        break;
      default:
        break;
    }
    try {
      const res = await apiController.importOrExportApi(type, params);
      // 分类下无API导出则抛出错误提示
      if ((type === OperationType.EXPORT || type === OperationType.EXPORT_DOC) && !res.success && res.msg) {
        Message.error(res.msg);
        return;
      }
      handlerClose();
      // 导入后提示信息
      if (isImport.value && res.success) {
        const { failTotal, successTotal, importTotal } = res.data;
        // 延迟弹出 为了防止上个窗口刚关闭 产生视觉闪烁
        setTimeout(() => {
          MessageBox.confirm(`共导入${importTotal}条，成功${successTotal}条，失败${failTotal}条`, '提示', {
            confirmButtonText: '确定',
            type: 'warning',
            showCancelButton: false,
          }).then(async () => {
            if (successTotal) {
              // 刷新列表
              emits('saved');
            }
          });
        }, 300);
      }
    } catch (err) {}
  };

  // 表单变动
  const onFormValueChange = async (values: any, prop: string, value: number) => {
    if (prop === 'operationType') {
      if (values.operationType === OperationType.EXPORT_DOC) {
        await form.value?.setFieldValue('apiGroupIds', []);
      } else {
        await form.value?.setFieldValue('apiGroupId', '');
      }
      form.value?.clearValidate();
    }
  };

  // 文件上传成功
  const onUploadSuccess = async (response: any) => {
    fileUrl.value = response.data;
    fileUploadStatus.value = false;
  };

  // 文件改变
  const onBeforeUploadFile = async (file: any) => {
    fileUploadStatus.value = true;
  };

  // 文件删除前
  const onBeformRemoveFile = () => {
    fileUrl.value = '';
    return true;
  };
</script>
<style lang="less" scoped>
  .api-import-export-dialog {
    .title {
      padding-left: 10px;
      font-weight: 700;
      border-left: 4px solid #2776fb;
    }

    ::v-deep .content {
      padding: 0px 30px;
      .choose-file {
        color: #2776fb;
      }
    }

    .footer-bottom {
      margin-left: auto;
      margin-right: auto;
      display: flex;
      justify-content: space-around;
      width: 200px;
      height: 30px;
      text-align: center;

      .el-button {
        width: 70px;
      }
    }
  }
</style>
