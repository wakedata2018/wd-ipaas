<template>
  <el-dialog
    :visible="props.visible"
    class="link-import-export-dialog"
    width="650px"
    :close-on-click-modal="false"
    destroy-on-close
    @open="handlerOpen"
    @close="handlerClose"
  >
    <template #title>
      <div class="title">连接器导入/导出</div>
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
        :value-props="{ options: LinkOperationTypeOptions }"
        required
      ></FatFormItem>
      <FatFormItem
        v-if="isImport"
        prop="connectorGroupId"
        label="选择分类"
        value-type="tree-select"
        :value-props="treeSelectProps"
        :width="230"
        required
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
      <FatFormItem
        v-if="!isImport"
        prop="connectorIdList"
        label="选择连接器"
        value-type="multi-select"
        message="可选择多个连接器"
        :value-props="{ options: LinkOptions }"
        :width="190"
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
<script lang="tsx" setup name="LinkImportExportDialog">
  import { computed, ref } from 'vue';

  import { FatForm, FatFormItem, useFatFormRef } from '@wakeadmin/components';
  import { MessageBox } from 'element-ui';

  import platformListApi from '@/api/platform-list';
  import linkInterface from '@/api/link-interface';
  import CategoryApi from '@/api/platform-category';
  import { LinkOperationTypeOptions, OperationType, LinkQueryData } from '@/utils/enum';

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

  const LinkOptions = ref();

  const renderPlaceholder = () => {
    return <a class="choose-file">选择文件</a>;
  };

  const initValue = {
    operationType: OperationType.IMPORT,
  };

  // 导入类型
  const isImport = computed(() => {
    const operationType = form.value?.getFieldValue('operationType');
    return operationType === OperationType.IMPORT;
  });

  // 定义Emits
  const emits = defineEmits<{
    (e: 'close'): void;
    (e: 'saved'): void;
    (e: 'refresh'): void;
  }>();

  const getLinkOptions = async () => {
    const res = await platformListApi.queryPlatForm();
    LinkOptions.value = res.data.map((item: LinkQueryData) => {
      return {
        label: item.name,
        value: item.id,
      };
    });
  };

  const treeSelectProps = {
    async data() {
      const res = await CategoryApi.getCategory({
        parentId: 0,
      });
      const loop = data => {
        return data.map(item => {
          const result = {
            label: item.groupName,
            value: item.id,
          };
          if (item.children) {
            return {
              ...result,
              children: loop(item.children),
            };
          }
          return result;
        });
      };
      return loop(res);
    },
  };
  const handlerOpen = () => {
    getLinkOptions();
  };

  const handlerClose = () => {
    emits('close');
  };

  // 保存
  const handleSubmit = async (values: any) => {
    const type = isImport.value ? OperationType.IMPORT : OperationType.EXPORT;
    const params = isImport.value
      ? {
          connectorGroupId: values.connectorGroupId,
          tempFilePath: fileUrl.value,
        }
      : {
          connectorIdList: values.connectorIdList,
        };
    const res = await linkInterface.importOrExportApi(type, params);

    handlerClose();
    // 导入后提示信息
    if (isImport.value && res) {
      const { failTotal, successTotal, importTotal } = res;
      // 延迟弹出 为了防止上个窗口刚关闭 产生视觉闪烁
      setTimeout(() => {
        MessageBox.confirm(`共导入${importTotal}条，成功${successTotal}条，失败${failTotal}条`, '提示', {
          confirmButtonText: '确定',
          type: 'warning',
          showCancelButton: false,
        }).then(async () => {
          if (successTotal) {
            // 刷新列表
            emits('refresh');
          }
        });
      }, 300);
    }
  };

  // 表单变动
  const onFormValueChange = async (values: any, prop: string, value: number) => {
    if (prop === 'operationType') {
      form.value?.setFieldValue('connectorIdList', []);
      form.value?.setFieldValue('connectorGroupId', null);
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
  .link-import-export-dialog {
    .title {
      padding-left: 10px;
      font-weight: 700;
      border-left: 4px solid #2776fb;
    }

    ::v-deep .content {
      padding: 0px 30px;
      background: #fff;

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
