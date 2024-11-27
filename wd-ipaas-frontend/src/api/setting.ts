import { requestByGet, requestByPost } from '@wakeapp/wakedata-backend';
import { SystemSetting } from '@/utils/enum';

const controller = '/dw/open/sys/setting';

export default {
  // 查询配置
  fetchSetting() {
    return requestByGet(`${controller}/query/name.and.logo`);
  },
  // 更新配置
  updateSetting(params: SystemSetting) {
    return requestByPost(`${controller}/build/name.and.logo`, params);
  },
};
