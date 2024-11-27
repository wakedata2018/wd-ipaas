<template>
  <div class="platform-interface bd-page">
    <div class="bd-header">
      <div class="title">{{ title }}</div>
    </div>
    <div class="bd-container main">
      <div class="aside">
        <span class="tree-title">平台列表</span>
        <el-scrollbar class="tree-wrap">
          <TreeGroup
            v-slot="{ data }"
            ref="treeGroup"
            :value="treeData"
            class="tree-group"
            :node-click="nodeClick"
            :el-tree-props="elTreeProps"
          >
            <span class="tree-node-item">
              <span class="ellipsis item-name" :title="data.groupName">{{ data.groupName }}</span>
              <span v-if="isFirstLevel(data)" class="item-btns">
                <el-button type="text" size="mini" @click.stop="addGroup(data)"> 新建分组 </el-button>
              </span>
              <span v-else class="item-btns">
                <el-button type="text" size="mini" @click.stop="editGroup(data)"> 编辑 </el-button>
                <el-button type="text" size="mini" @click.stop="delGroup(data)"> 删除 </el-button>
              </span>
            </span>
          </TreeGroup>
        </el-scrollbar>
      </div>
      <CreateGroupModal ref="createGroupModalRef" @finish="onAfterCreateGroupSumbmit" />
      <div class="content">
        <InterfaceList
          :extra-query="{ id: groupIdInfo?.id, parentId: groupIdInfo?.parentId }"
          :current-select="groupIdInfo"
          :extra="{ currentGroupInfo: groupIdInfo }"
          class="dss-table bd-table"
        />
      </div>
    </div>
  </div>
</template>

<script lang="tsx" setup>
  import { ref, onMounted, nextTick, computed } from 'vue';
  import { useRoute } from 'vue-router/composables';
  import { useStore } from '@/store/hook';

  import { MessageBox, Message } from 'element-ui';
  import { useFatFormModalRef } from '@wakeadmin/components';
  import TreeGroup from '@/components/tree-group/tree-group.vue';
  import { CreateGroupModal } from './create-group-modal';
  import InterfaceList from './interface-list';

  import interfaceApi from '@/api/link-interface';
  import { InterfaceGroup, CurSelectGroupInfo } from './type';
  import pageUtils from '@/utils/page.js';

  const store = useStore();
  const route = useRoute();
  const title = computed(() => {
    return pageUtils.findPageName(store.state.permitList, route.name);
  });
  // 当前选中的接口分组
  const groupIdInfo = ref<CurSelectGroupInfo>();
  const treeData = ref<InterfaceGroup[]>();
  const groupLoading = ref(false);
  const treeGroup = ref();

  const elTreeProps = {
    label: 'connectorName',
    children: 'connectorApiGroupDTOList',
  };

  const createGroupModalRef = useFatFormModalRef() as any;

  // 遍历树 判断是否为一级节点
  const isFirstLevel = computed(() => {
    return (node: InterfaceGroup) => {
      return node.connectorApiGroupDTOList !== undefined;
    };
  });

  const addGroup = (item: InterfaceGroup) => {
    createGroupModalRef.value?.open({
      title: '新建接口分组',
      initialValue: {
        connectorId: item.id,
        groupName: null,
      },
    });
    treeGroup.value.setHightKey(item.id);
  };

  const editGroup = (item: InterfaceGroup) => {
    createGroupModalRef.value?.open({
      title: '编辑接口分组',
      initialValue: {
        id: item.id,
        connectorId: item.connectorId,
        groupName: item.groupName,
      },
    });
  };
  const delGroup = (data: InterfaceGroup) => {
    MessageBox.confirm('是否确定要删除该接口分组？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then(async () => {
        await interfaceApi.delInterfaceGroup(data.id);
        Message.success({
          message: '删除成功',
        });
        fetchGroup();
      })
      .catch(err => {
        if (err.message) {
          Message.error({
            message: err.message,
          });
        } else {
          Message.info({
            message: '已取消删除',
          });
        }
      });
  };

  const nodeClick = (data: InterfaceGroup) => {
    groupIdInfo.value = {
      id: data.id,
      parentId: data.connectorId,
    };
  };

  // 获取接口分组列表
  const fetchGroup = async () => {
    try {
      groupLoading.value = true;
      const response: InterfaceGroup[] = await interfaceApi.queryInterfaceGroup();
      treeData.value = response.map((field: InterfaceGroup) => {
        const { connectorName, connectorId, ...other } = field;
        return {
          id: connectorId,
          groupName: connectorName,
          ...other,
        };
      });
    } catch (err) {
      Message.error(`获取分类列表分组失败: ${(err as Error).message}`);
    } finally {
      groupLoading.value = true;
    }
  };

  // 初始默认选中状态
  const init = () => {
    if (treeData.value?.length) {
      // 初始默认 选中第一个分组
      groupIdInfo.value = {
        parentId: undefined,
        id: treeData.value[0]?.id,
      };
      // 高亮第一个分组
      nextTick(() => {
        if (groupIdInfo.value?.id) {
          treeGroup.value.setHightKey(groupIdInfo.value.id);
        }
      });
    }
  };

  // 创建分组保存后触发高亮以及更新接口列表
  const onAfterCreateGroupSumbmit = async value => {
    await fetchGroup();
    nextTick(() => {
      treeGroup.value.setHightKey(value.connectorId);
    });
    // 重新查询当前平台下的所有接口
    groupIdInfo.value = {
      parentId: undefined,
      id: value.id,
    };
  };

  onMounted(async () => {
    await fetchGroup();
    init();
  });
</script>

<style lang="less" scoped>
  .bd-page {
    padding-top: 20px;
    margin-bottom: 0px;
    padding-bottom: 10px;
  }
  .platform-interface {
    .main {
      display: flex;
      background: #fff;
      height: calc(100vh - 182px);
      .aside {
        width: 250px;
        border-right: 1px solid #e1e1e1;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        .tree-title {
          display: flex;
          align-items: center;
          line-height: 40px;
          padding-left: 10px;
          border-bottom: 1px solid #e6e6e6;
          font-weight: 600;
          font-size: 14px;
        }
        .tree-wrap {
          flex: 1;
          margin: 10px 0;
          .tree-group {
            padding: 0px 10px;
          }
          ::v-deep .tree-node-item {
            flex: 1;
            display: flex;
            align-items: center;
            justify-content: space-between;
            font-size: 12px;
            padding-right: 12px;
            .item-name {
              width: 125px;
              padding-right: 10px;
            }
            .item-btns {
              font-size: 12px;
              display: none;
            }
            &:hover {
              .item-btns {
                display: inline-block;
              }
            }
          }
        }
      }
    }

    .content {
      flex: 1;
      overflow: hidden;
      .fat-card {
        border: none;
        border-radius: 0;
        /deep/ .fat-card__header {
          line-height: 41px;
          height: 41px;
          .fat-card__title {
            line-height: 40px;
            font-weight: 600;
            font-size: 14px;
          }
        }
        /deep/ .fat-card__content {
          .fat-container__body {
            padding-top: 10px;
          }
        }
      }
    }
  }
</style>
