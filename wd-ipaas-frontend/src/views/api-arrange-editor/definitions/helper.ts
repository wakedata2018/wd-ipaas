import { IItemBase, ModelConfig } from '@antv/g6';
import apiControllApi from '@/api/api-controller';
import { getUniqueNameFromItem } from '../utils';
import { getDefinition } from './registry';
import { ApiModelForm } from './types';
import { ApiPublicKind } from '@/utils/enum';

/**
 * 判断是否能删除
 * @param item
 * @returns
 */
export function isRemovable(item: IItemBase) {
  const u = getUniqueNameFromItem(item);

  if (u != null) {
    const definition = getDefinition(u);

    return definition?.removable?.(item) !== false;
  }

  return true;
}

/**
 * 判断是否能复制
 * @param item
 * @returns
 */
export function isCopyable(item: IItemBase) {
  const u = getUniqueNameFromItem(item);

  if (u != null) {
    const definition = getDefinition(u);
    return definition?.copyable?.(item) !== false;
  }

  return true;
}

async function getApiInfoById(config: ApiModelForm, dataAssetApiId: number) {
  const res = await apiControllApi.showDetail({ dataAssetApiId });
  if (!res.data || !res.data.dataAssetApi) {
    return config;
  }

  const { dataAssetApiMethod, apiName } = res.data.dataAssetApi;
  const pathArr = dataAssetApiMethod.split('/');

  return Object.assign(config, {
    ...res.data,
    publicKind: config.publicKind ?? ApiPublicKind.SHARE,
    ...(!config.name
      ? {
          desc: apiName,
          name: pathArr.length > 0 ? pathArr[pathArr.length - 1].replace(/[^a-zA-Z0-9]+/g, '_') : '',
        }
      : {}),
  });
}

export async function initModelForm(model: ModelConfig) {
  const config = model.form as ApiModelForm;
  const dataAssetApiId = config.dataAssetApi?.dataAssetApiId;
  if (dataAssetApiId) {
    model.form = await getApiInfoById(config, dataAssetApiId);
  }
  return model;
}

async function getConnectorInfoById(config: ApiModelForm, id: number) {
  const res = await apiControllApi.fetchConnectorDetail({ id });
  if (!res.data || !res.data.connectorApi) {
    return config;
  }

  const { apiMethod, apiName } = res.data.connectorApi;
  const pathArr = apiMethod.split('/');

  return Object.assign(config, {
    ...res.data,
    ...(!config.name
      ? {
          desc: apiName,
          name: pathArr.length ? pathArr[pathArr.length - 1].replace(/\./g, '_') : '',
        }
      : {}),
  });
}

export async function initConnectorForm(model: ModelConfig) {
  const config = model.form as any;
  const id = config.connectorApi?.id;
  if (id) {
    model.form = await getConnectorInfoById(config, id);
  }
  return model;
}
