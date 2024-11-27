import { ref } from 'vue';
import { Message } from 'element-ui';
import { defineFatFormTabs } from '@wakeadmin/components';

import { PlatFormGroupInfo, PlatFormGroupOption } from './type';
import platformListApi from '@/api/platform-list';

import './index.less';

export default defineFatFormTabs(({ tabPane, item, group, table, tableColumn }) => {
  const smallTip = {
    placeholder: '最多输入50个字',
    maxlength: 50,
  };

  const mediumTip = {
    placeholder: '最多输入200个字符',
    maxlength: 200,
  };

  const largeTip = {
    placeholder: '最多输入1000个字符',
    maxlength: 1000,
  };

  const platFormOptions = ref<PlatFormGroupOption[] | undefined>();

  // 获取平台分类数据
  const fetchPlatFormGroup = async () => {
    const response: PlatFormGroupInfo[] = await platformListApi.getCategory({ parentId: 0 });
    const loop = (field: PlatFormGroupInfo): PlatFormGroupOption => {
      let children: PlatFormGroupOption[] = [];
      if (field?.children?.length) {
        children = field.children.map(params => loop(params));
      }
      const res = {
        label: field.groupName,
        value: field.id,
      };
      if (children.length) {
        return {
          ...res,
          children,
        };
      }

      return res;
    };
    platFormOptions.value = response.map(field => {
      return loop(field);
    });
    return platFormOptions.value;
  };
  /**
   * 自定义表单校验
   */
  const envNameValidator = (rule: any, value: { addressName: string }[], callback: any) => {
    const arr = value.map(field => field.addressName);
    const len = [...new Set(arr)].length;
    if (len < arr.length) {
      callback(new Error('环境名称不能重复'));
    }
    callback();
  };

  /**
   * 参数名称不能重复且
   */
  const allParamNameValidator = (rule: any, value: { paramName: string }[], callback: any) => {
    const arr = value?.map(field => field.paramName) ?? [];
    const len = [...new Set(arr)].length;
    if (len < arr.length) {
      callback(new Error('鉴权字段名称不能重复'));
    }
    callback();
  };

  const paramNameValidator = (rule: any, value: string, callback: any) => {
    const regx = /^[-_0-9A-Za-z]*[A-Za-z]+[-_0-9A-Za-z]*$/i;
    if (!regx.test(value)) {
      callback(new Error('输入下划线、英文字母、数字'));
    } else if (value.length >= 50) {
      callback(new Error('不能超过50个字'));
    }
    callback();
  };

  return () => {
    return {
      labelWidth: '120px',
      submit: async values => {
        if (values?.id) {
          await platformListApi.updatePlatForm(values);
          Message.success('更新连接器成功');
        } else {
          await platformListApi.addPlatForm(values);
          Message.success('新增连接器成功');
        }
      },
      children: [
        tabPane({
          label: '基础信息',
          name: 'baseInfo',
          children: [
            group({
              children: [
                item({
                  prop: 'groupId',
                  label: '连接器分类',
                  col: 12,
                  valueType: 'tree-select',
                  valueProps: {
                    data: fetchPlatFormGroup,
                  },
                  rules: { required: true, message: '请选择' },
                }),
                item({
                  prop: 'enableStatus',
                  label: '是否启用',
                  col: 12,
                  valueType: 'switch',
                  initialValue: true,
                  transform: v => {
                    return v ? 1 : 0;
                  },
                }),
              ],
            }),
            group({
              children: [
                item({
                  prop: 'name',
                  label: '连接器名称',
                  col: 12,
                  valueProps: mediumTip,
                  rules: { required: true, message: '请输入平台名称' },
                }),
                item({
                  prop: 'version',
                  label: '版本信息',
                  col: 12,
                  valueProps: mediumTip,
                  rules: { required: true, message: '请输入版本信息' },
                }),
              ],
            }),

            item({
              prop: 'developer',
              label: '开发者',
              valueProps: mediumTip,
              required: true,
            }),
            group({
              children: [
                item({
                  prop: 'phone',
                  label: '联系电话',
                  col: 12,
                  valueProps: mediumTip,
                  valueType: 'phone',
                }),
                item({
                  prop: 'email',
                  label: '邮箱',
                  col: 12,
                  valueType: 'email',
                  valueProps: mediumTip,
                }),
              ],
            }),
            item({
              prop: 'website',
              label: '官网',
              valueProps: mediumTip,
              valueType: 'url',
              required: true,
            }),
            item({
              prop: 'helpDocument',
              label: '帮助文档',
              valueProps: mediumTip,
            }),
            item({
              prop: 'privacyAgreement',
              label: '隐私协议',
              valueProps: mediumTip,
            }),
            item({
              prop: 'usageAgreement',
              label: '使用协议',
              valueProps: mediumTip,
            }),
            item({
              prop: 'platformIntroduction',
              label: '连接器介绍',
              valueType: 'textarea',
              valueProps: largeTip,
            }),
          ],
        }),
        tabPane({
          label: '开发管理',
          name: 'devManage',
          children: [
            table({
              prop: 'connectorEnvironmentAddressDTOList',
              label: '环境地址管理',
              width: 1300,
              rowKey: 'id',
              // 关闭删除提示
              removeConfirm: false,
              class: 'vertical-column',
              rules: { validator: envNameValidator },
              required: true,
              columns: [
                tableColumn({
                  prop: 'addressName',
                  label: '环境名称',
                  // 表单项级别的验证规则
                  required: true,
                  valueProps: smallTip,
                }),
                tableColumn({
                  prop: 'environmentAddress',
                  label: '访问地址',
                  valueType: 'url',
                  required: true,
                  valueProps: largeTip,
                }),
              ],
              createText: '新增一行',
              removeText: '删除',
            }),
            table({
              prop: 'connectorParamsDTOList',
              label: '密钥字段设置',
              width: 1300,
              rowKey: 'id',
              // 关闭删除提示
              removeConfirm: false,
              class: 'vertical-column',
              rules: { validator: allParamNameValidator },
              columns: [
                tableColumn({
                  prop: 'paramName',
                  label: '参数名称',
                  // 表单项级别的验证规则
                  required: true,
                  rules: { validator: paramNameValidator },
                }),
                tableColumn({
                  prop: 'paramType',
                  label: '参数类型',
                  valueType: 'select',
                  valueProps: {
                    options: [
                      {
                        value: 'string',
                        label: 'String',
                      },
                      {
                        value: 'number',
                        label: 'Number',
                      },
                    ],
                  },
                  required: true,
                }),
                tableColumn({
                  prop: 'isRequired',
                  label: '必填',
                  width: 'mini',
                  valueType: 'checkbox',
                  valueProps: {
                    previewActiveText: '是',
                    previewInactiveText: '否',
                  },
                  transform: v => {
                    return v ? 1 : 0;
                  },
                }),
                tableColumn({
                  prop: 'hiddenType',
                  label: '展示脱敏',
                  width: 'mini',
                  valueType: 'checkbox',
                  valueProps: {
                    previewActiveText: '是',
                    previewInactiveText: '否',
                  },
                  transform: v => {
                    return v ? 1 : 0;
                  },
                }),
                tableColumn({
                  prop: 'defaultValue',
                  label: '示例值',
                  valueProps: largeTip,
                }),
                tableColumn({
                  prop: 'description',
                  label: '说明',
                  valueProps: largeTip,
                }),
              ],
              createText: '新增一行',
              removeText: '删除',
            }),
          ],
        }),
      ],
    };
  };
});
