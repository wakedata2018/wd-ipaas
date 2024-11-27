<template>
  <div class="page-system-setting bd-page">
    <div class="bd-header">
      <div class="title">{{ title }}</div>
    </div>
    <div class="bd-container">
      <FatForm
        ref="form"
        class="setting-form"
        :submit="onSubmit"
        :enable-reset="false"
        :request="request"
        @finish="handleFinish"
      >
        <FatFormItem prop="systemName" label="系统名称" placeholder="不超过15个字" required></FatFormItem>
        <FatFormItem
          prop="logoUrl"
          label="登录页LOGO"
          value-type="image"
          :value-props="{ accept: ['.png', '.jpg', '.jpeg', '.gif', '.bmp'] }"
          message="建议图片尺寸 400 × 100，在登录页面展示"
          required
        ></FatFormItem>
        <FatFormItem
          prop="miniLogoUrl"
          label="系统LOGO"
          value-type="image"
          :value-props="{ accept: ['.png', '.jpg', '.jpeg', '.gif', '.bmp'] }"
          message="建议图片尺寸 100 × 100，在系统登录页面及接口文档页面展示"
          required
        ></FatFormItem>
      </FatForm>
    </div>
  </div>
</template>
<script lang="tsx" setup>
  import { computed } from 'vue';
  import { useRoute } from 'vue-router/composables';
  import { useStore } from '@/store/hook';
  import { Message } from 'element-ui';
  import { FatForm, FatFormItem } from '@wakeadmin/components';

  import { SettingInfo } from '@/utils/enum';
  import SettingApi from '@/api/setting';
  import pageUtils from '@/utils/page.js';

  const store = useStore();
  const route = useRoute();
  const title = computed(() => {
    return pageUtils.findPageName(store.state.permitList, route.name);
  });

  const request = async () => {
    const { systemDetailInfo } = await SettingApi.fetchSetting();
    return systemDetailInfo;
  };

  // 保存
  const onSubmit = async (values: SettingInfo) => {
    await SettingApi.updateSetting({ systemDetailInfo: values });
    // 缓存系统名称
    localStorage.setItem('systemName', values.systemName);
  };

  // 处理表单提交完成
  const handleFinish = async () => {
    // 成功保存
    Message.success('保存成功');

    // 刷新页面
    window.location.reload();
  };
</script>
<style lang="less" scoped>
  .page-system-setting {
    .setting-form {
      padding: 10px;
      width: 500px;
    }
  }
</style>
