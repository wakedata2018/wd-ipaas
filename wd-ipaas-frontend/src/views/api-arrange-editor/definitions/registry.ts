import { NamedRegistry } from '@wakeadmin/utils';
import { ElementDefinition } from './types';

export const definitionRegistry = new NamedRegistry<ElementDefinition>();

/**
 * 获取元素定义
 * @param name
 * @returns
 */
export function getDefinition(name: string) {
  return definitionRegistry.registered(name);
}
