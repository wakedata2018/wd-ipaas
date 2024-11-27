<template>
  <el-upload
    :action="uploadUrl"
    :data="params"
    :on-success="handleSuccess"
    :on-error="handleError"
    :accept="accept"
    name="file"
    v-bind="uploadProps"
    :before-upload="beforeUpload"
    :auto-upload="autoUpload"
    :show-file-list="showFileList"
    :limit="1"
    ref="uploader"
  >
    <slot />
  </el-upload>
</template>
<script>
  import { FILE_TYPES, SIZE_ERROR, SIZE_LIMIT } from './materiaEnum';

  export default {
    data() {
      return {
        file: null,
      };
    },
    props: {
      uploadUrl: {
        type: String,
        default: '/dw/open/upload/image',
      },
      accept: {
        type: String,
        default: 'image/jpeg,image/gif,image/png',
      },
      uploadProps: {
        type: Object,
        default: () => ({ name: 'file' }),
      },
      autoUpload: {
        type: Boolean,
        default: true,
      },
      showFileList: {
        type: Boolean,
        default: false,
      },
      // 额外参数
      params: {
        type: Object,
        default: () => ({}),
      },
    },
    methods: {
      handleSuccess(opt) {
        this.$refs.uploader.clearFiles();
        if (!opt.success) {
          this.handleError(opt);
        } else {
          this.$emit('on-success', opt.data);
        }
        this.$emit('on-finally');
      },
      handleError(opt) {
        this.$refs.uploader.clearFiles();
        this.$emit('on-error', opt);
        this.$emit('on-finally');
        this.$message({
          type: 'error',
          message: `上传失败${opt && opt.errorMessage ? ':' + opt.errorMessage : ''}`,
        });
      },
      beforeUpload(file) {
        this.$emit('on-start');
        const size = file.size;
        const maxSize = SIZE_LIMIT[FILE_TYPES.IMAGE];
        if (size > maxSize) {
          this.handleError({ errorMessage: SIZE_ERROR[this.fileType] });
          return false;
        }
        return true;
      },
      getFile() {
        if (this.$refs.uploader.uploadFiles && this.$refs.uploader.uploadFiles.length) {
          return this.$refs.uploader.uploadFiles[0].raw;
        }
      },
      submit() {
        this.$emit('on-start');
        this.$refs.uploader.submit();
      },
    },
  };
</script>
