import { ModelConfig, IItemBase } from '@antv/g6';

/**
 * 从模型定义中获取 uniqueName
 * @param model
 * @returns
 */
export function getUniqueNameFromModelConfig(model: ModelConfig | null) {
  // @ts-expect-error
  return model?.data?.uniqueName;
}

/**
 * 从 g6 实例中获取 uniqueName
 * @param item
 */
export function getUniqueNameFromItem(item: Partial<IItemBase>): string | undefined {
  const model = !item || item.destroyed ? null : item?.getModel?.() ?? null;
  return getUniqueNameFromModelConfig(model);
}

export function getIdFromItem(item: Partial<IItemBase>): string | undefined {
  const model = !item || item.destroyed ? null : item?.getModel?.();

  return model?.id as string;
}

/**
 * 生成随机字符串
 */
export const randomStr = (randomFlag = true, min = 0, max: number) => {
  let str = '';
  let range = min;
  const arrStr = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz';
  const len = arrStr.length;
  if (randomFlag) {
    range = Math.round(Math.random() * (max - min)) + min;
  }
  for (let i = 0; i < range; i++) {
    str += arrStr.charAt(Math.floor(Math.random() * len));
  }
  return str;
};
