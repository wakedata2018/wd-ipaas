import { defineFatFormModal } from '@wakeadmin/components';
import { requestByPost } from '@wakeapp/wakedata-backend';

import { InterfaceGroup } from './type';

export const CreateGroupModal = defineFatFormModal(
  ({ item }) => {
    return () => ({
      title: '新建接口分组',
      width: '600px',
      async submit(params: InterfaceGroup) {
        // 编辑
        if (params.id) {
          await requestByPost('/dw/open/business/connector/api/group/modify', params);
        } else {
          // 新增
          await requestByPost('/dw/open/business/connector/api/group/add', params);
        }
      },
      children: [
        item({
          prop: 'groupName',
          label: '分组名称',
          width: 'huge',
          valueProps: { placeholder: '最多输入10个字', maxlength: 10 },
          required: true,
        }),
      ],
    });
  },
  { name: 'CreateGroupModal' }
);
