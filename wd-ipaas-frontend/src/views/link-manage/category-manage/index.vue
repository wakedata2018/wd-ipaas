<template>
  <div class="category bd-page">
    <div class="bd-header">
      <div class="title">{{ title }}</div>
    </div>
    <div class="bd-container main">
      <div class="aside">
        <span class="tree-title">分类列表</span>

        <el-scrollbar class="tree-wrap">
          <TreeGroup
            v-slot="{ data }"
            ref="treeGroup"
            class="tree-group"
            :value="treeData"
            :node-click="nodeClick"
            :el-tree-props="elTreeProps"
          >
            <span class="tree-node-item">
              <span class="ellipsis item-name" :title="data.groupName">{{ data.groupName }}</span>
              <span v-if="isFirstLevel(data)" class="item-btns">
                <el-button type="text" size="mini" @click.stop="append(ActionType.CreateChild, data)">
                  新建子类
                </el-button>
              </span>
            </span>
          </TreeGroup>
        </el-scrollbar>
        <div class="footer">
          <el-button type="primary" size="mini" @click="append(ActionType.Create)">新建分类</el-button>
        </div>
      </div>
      <div class="content">
        <CreateOrEdit
          v-if="visible"
          ref="form"
          :value="currentInfo"
          :status="status"
          @save="onSave"
          @del="onDel"
          @close="onClose"
        />
      </div>
    </div>
  </div>
</template>

<script lang="tsx" setup>
  import { ref, onMounted, nextTick, computed } from 'vue';
  import { useRoute } from 'vue-router/composables';
  import { useStore } from '@/store/hook';

  import { Message, MessageBox } from 'element-ui';
  import TreeGroup from '@/components/tree-group/tree-group.vue';
  import CreateOrEdit from './creat-or-edit.vue';

  import CategoryApi from '@/api/platform-category';
  import { ActionType, CategoryItemInfo } from './type';
  import clone from 'lodash/clone';
  import pageUtils from '@/utils/page.js';

  const store = useStore();
  const route = useRoute();
  const treeData = ref<CategoryItemInfo>([] as any);
  const visible = ref<boolean>(false);
  const form = ref();
  const treeGroup = ref();
  const loading = ref(false);

  const title = computed(() => {
    return pageUtils.findPageName(store.state.permitList, route.name);
  });

  const elTreeProps = {
    label: 'groupName',
    children: 'children',
  };
  // 当前选中节点信息
  const currentInfo = ref<CategoryItemInfo | undefined>({} as any);
  // 当前状态
  const status = ref<ActionType>(ActionType.Create);

  // 是否为一级节点
  const isFirstLevel = (node: CategoryItemInfo) => {
    return node.parentId === 0;
  };

  const append = (type: ActionType = ActionType.Create, data?: CategoryItemInfo) => {
    nextTick(() => {
      treeGroup.value.setHightKey(currentInfo.value?.id);
    });
    currentInfo.value = data;
    status.value = type;
    visible.value = true;
  };

  const nodeClick = (data: CategoryItemInfo) => {
    currentInfo.value = clone(data);
    status.value = ActionType.Edit;
    visible.value = true;
  };

  const fetchGroup = async () => {
    loading.value = true;
    const response = await CategoryApi.getCategory({
      parentId: 0,
    });
    treeData.value = response;
    loading.value = true;
  };

  const onSave = async (value: CategoryItemInfo) => {
    try {
      // 表单校验
      await form.value.validator();
      const params = { ...value };
      if (status.value === ActionType.Create) {
        params.parentId = 0;
      } else if (status.value === ActionType.CreateChild) {
        params.parentId = currentInfo.value?.id;
      }
      await CategoryApi.addCategory(params);
      Message.success('保存成功');
      fetchGroup();
      currentInfo.value = {} as any;
      visible.value = false;
    } catch (err) {
      Message.error(`保存失败: ${err.message}`);
    }
  };

  const onDel = async (id: number) => {
    MessageBox.confirm('确定删除该分类？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then(async () => {
        await CategoryApi.delCategory(id);
        Message.success({
          message: '删除成功',
        });
        fetchGroup();
        visible.value = false;
      })
      .catch(error => {
        if (error.message) {
          Message.error({
            message: `删除失败：${error.message}`,
          });
        } else {
          Message.info({
            message: '已取消删除',
          });
        }
      });
  };

  const onClose = () => {
    visible.value = false;
  };

  onMounted(() => {
    fetchGroup();
  });
</script>

<style lang="less" scoped>
  .bd-page {
    padding-top: 20px;
    margin-bottom: 0px;
    padding-bottom: 10px;
  }
  .category {
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
            padding-right: 10px;
            .item-name {
              width: 151px;
              padding-right: 8px;
            }
            .item-btns {
              font-size: 12px;
            }
          }
        }

        .footer {
          padding: 5px;
          height: 60px;
          width: 100%;
          display: flex;
          align-items: center;
          justify-content: center;
        }
      }
    }

    .content {
      flex: 1;
    }
  }
</style>
