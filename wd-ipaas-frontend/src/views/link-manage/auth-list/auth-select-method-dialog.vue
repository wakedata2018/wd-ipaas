<!-- eslint-disable vue/no-deprecated-slot-scope-attribute  -->
<template>
  <el-dialog
    class="auth-select-method-dialog"
    width="960px"
    :visible="props.visible"
    :close-on-click-modal="false"
    append-to-body
    destroy-on-close
    @close="onClose"
  >
    <span slot="title">
      <div class="title">表达式编辑器</div>
    </span>

    <div class="auth-select-method-dialog__content">
      <div class="method-list content__list">
        <div class="list-title">函数列表</div>
        <div class="list-search">
          <el-input v-model="search" class="param-value__input" placeholder="请输入函数名" clearable></el-input>
        </div>
        <div class="list-content">
          <el-tree
            ref="tree"
            class="filter-tree"
            accordion
            :data="methods"
            :props="{
              children: 'list',
              label: 'method',
            }"
            :filter-node-method="filterNode"
            @node-click="handleNodeClick"
          >
            <span slot-scope="{ data }" class="custom-tree-node">
              {{ data.method }}
            </span>
          </el-tree>
        </div>
      </div>

      <div class="method-expression content__list">
        <div class="list-title">表达式</div>
        <div class="list-content">
          <textarea ref="expression" class="expression-text" rows="3" autofocus />
        </div>

        <div v-if="selectedMethod" class="list-extra">
          <template v-if="selectedMethod.type === SHOW_TYPE.METHOD">
            <div class="list-item list-item--bold">函数介绍</div>
            <div class="list-item"><span>函数名称：</span>{{ selectedMethod.name }}</div>
            <div class="list-item"><span>函数描述：</span>{{ selectedMethod.description }}</div>
            <div v-if="selectedMethod.paramDesc" class="list-item">
              <span>参数说明：</span>
              <div v-html="selectedMethod.paramDesc"></div>
            </div>
            <div class="list-item"><span>返回值类型：</span>{{ selectedMethod.returnType }}</div>
            <div v-if="selectedMethod.example" class="list-item">
              <span>使用示例：</span>
              <div v-html="selectedMethod.example"></div>
            </div>
          </template>
          <template v-if="selectedMethod.type === SHOW_TYPE.PARAM">
            <div class="list-item list-item--bold">接口介绍</div>
            <div class="list-item"><span>接口分组：</span>{{ selectedMethod.group }}</div>
            <div class="list-item"><span>接口名称：</span>{{ selectedMethod.name }}</div>
            <div v-if="selectedMethod.description" class="list-item">
              <span>接口描述：</span>{{ selectedMethod.description }}
            </div>
            <div v-if="selectedMethod.url" class="list-item">
              <a class="list-url" :href="selectedMethod.url" target="_blank">查看接口文档</a>
            </div>
          </template>
        </div>
      </div>

      <div class="method-params content__list">
        <div class="list-title">参数列表</div>
        <div class="list-content">
          <el-tree class="filter-tree" accordion :data="treeData" @node-click="handleParamsClick">
            <span slot-scope="{ data }" class="custom-tree-node">
              {{ data.assetColumns }}
              <span v-if="data.assetDataType" class="custom-tree-type"> &lt; {{ data.assetDataType }} &gt; </span>
            </span>
          </el-tree>
        </div>
      </div>
    </div>
    <span slot="footer">
      <div class="dialog-footer">
        <el-button type="plain" @click="onClose">取消</el-button>
        <el-button type="primary" @click="handleConfirm">确定</el-button>
      </div>
    </span>
  </el-dialog>
</template>
<script lang="tsx" setup>
  import { ref, watch, computed, PropType, onMounted } from 'vue';

  import { InterfaceParams } from '@/utils/enum/auth-list';

  import api from '@/api/api-method';

  const SHOW_TYPE = {
    METHOD: 'method',
    PARAM: 'param',
  };

  const props = defineProps({
    params: {
      type: Array as PropType<InterfaceParams[]>,
      default: () => [],
    },
    visible: {
      type: Boolean,
      default: false,
    },
  });

  const search = ref<string>();
  const tree = ref();
  const expression = ref();
  const selectedMethod = ref();
  const methods = ref();

  watch(search, curVal => {
    tree.value.filter(curVal);
  });

  const treeData = computed(() => {
    const header = props.params.filter(item => item.httpParamKind === 'HEAD');
    const query = props.params.filter(item => item.httpParamKind === 'QUERY');
    const body = props.params.find(item => item.httpParamKind === 'BODY');
    const list = [];

    const addExpression = (arr: InterfaceParams[], parentExpression: string) => {
      return arr.map(item => {
        return {
          ...item,
          expression: `${parentExpression}.${item.assetColumns}`,
        };
      });
    };

    if (header.length) {
      list.push({
        assetColumns: 'HEAD',
        id: 'HEAD',
        children: addExpression(header, '$.HEAD'),
        expression: '$.HEAD',
      });
    }

    if (query.length) {
      list.push({
        assetColumns: 'QUERY',
        id: 'QUERY',
        children: addExpression(query, '$.QUERY'),
        expression: '$.QUERY',
      });
    }
    if (body) {
      let initArry: InterfaceParams[] = [];
      const loop: any = (data: any, parentExpression: string) => {
        return data.map((item: any) => {
          let children = [];
          if (item[1].type === 'array' && item[1].items.properties) {
            children = loop(Object.entries(item[1].items.properties), `${parentExpression}.${item[1].name}`);
          } else if (item[1].properties) {
            children = loop(Object.entries(item[1].properties), `${parentExpression}.${item[1].name}`);
          }
          return {
            id: item[0],
            assetColumns: item[1].name,
            assetDataType: item[1].type,
            description: item[1].description,
            expression: `${parentExpression}.${item[1].name}`,
            children,
          };
        });
      };
      if (body?.responsePostData) {
        const data = JSON.parse(body?.responsePostData)?.root?.properties ?? {};
        initArry = loop(Object.entries(data), '$.BODY');
      }
      list.push({
        assetColumns: 'BODY',
        id: 'BODY',
        description: body.description,
        children: initArry,
        expression: '$.BODY',
      });
    }

    return list;
  });

  const filterNode = (value, data) => {
    if (!value) {
      return true;
    }
    return data.method.indexOf(value) !== -1;
  };

  const insertContent = (html: string) => {
    const el = expression.value;
    // 得到光标前的位置
    const startPos = el.selectionStart;
    // 得到光标后的位置
    const endPos = el.selectionEnd;
    if (startPos != null) {
      const val = el.value;
      el.value = val.substring(0, startPos) + html + val.substring(endPos, val.length);
      el.focus();
      el.selectionStart = startPos;
      el.selectionEnd = startPos + html.length;
    } else {
      el.value += html;
      el.focus();
    }
  };

  const handleNodeClick = data => {
    expression.value.focus();
    if (data.list) {
      return;
    }
    selectedMethod.value = {
      type: SHOW_TYPE.METHOD,
      name: data.method + data.param,
      description: data.description,
      returnType: data.returnType,
      example: (data.example || '').replaceAll('\n', '<br />'),
      paramDesc: (data.paramDesc || '').replaceAll('\n', '<br />'),
    };
    insertContent(data.method + data.param);
  };
  /**
   * 点击参数列表节点
   */
  const handleParamsClick = data => {
    insertContent(`{${data.expression}}`);
  };

  // 定义Emits
  const emits = defineEmits<{
    (e: 'update:visible', value: boolean): void;
    (e: 'select', value: string): void;
  }>();

  const handleConfirm = async () => {
    const expressVal = expression.value.value.trim();
    if (expressVal) {
      await api.checkExpression({ functionExpress: expressVal });
    }
    emits('select', expressVal);
  };

  const onClose = () => {
    emits('update:visible', false);
  };

  const handleGetAllMethod = async () => {
    const res = await api.getMethodList();
    methods.value = res.data;
  };

  onMounted(() => {
    handleGetAllMethod();
  });
</script>

<style scoped lang="less">
  .auth-select-method-dialog {
    /deep/ .el-dialog__body {
      padding: 16px 20px;
    }
    &__content {
      display: flex;
      .content__list:not(:first-child) {
        border-left: 0;
      }
    }
    .method-list,
    .method-expression,
    .method-params {
      border: 1px solid #ddd;
      height: 500px;
      display: flex;
      flex-direction: column;
    }
    .method-list {
      width: 200px;
    }
    .method-params {
      width: 200px;
    }
    .method-expression {
      flex: 1;
    }
    .title {
      padding-left: 10px;
      font-weight: 700;
      border-left: 4px solid #2776fb;
    }
    .dialog-footer {
      margin: 0 auto;
      width: 250px;
      height: 30px;
      display: flex;
      justify-content: space-evenly;

      /deep/ .el-button {
        width: 60px;
      }
    }
    .list-title {
      height: 40px;
      padding: 10px 24px;
      border-bottom: 1px solid #ddd;
    }
    .list-search {
      height: 40px;
      border-bottom: 1px solid #ddd;
      display: flex;
      align-items: center;
      /deep/ .el-input__inner {
        border: 0;
      }
      /deep/ .el-input__validateIcon {
        display: none;
      }
    }
    .list-content {
      overflow: auto;
      display: flex;
      flex: 1;
    }
    .list-extra {
      height: 318px;
      border-top: 1px solid #ddd;
      padding: 10px;
      overflow: auto;
      .list-item {
        margin-bottom: 5px;
        display: flex;
      }
      .list-item--bold {
        font-weight: bold;
      }
      span {
        width: 84px;
        text-align: right;
      }
      .list-url {
        text-decoration: none;
        color: #0f40f5;
      }
    }
    .expression-text {
      flex: 1;
      max-height: 120px;
      margin: 10px;
      border: 1px solid #ddd;
      border-radius: 4px;
      padding: 6px 8px;
      outline: none;
      &.edit {
        border-color: #b3d8ff;
      }
    }
    .filter-tree {
      width: 300px;
    }
    .custom-tree-node {
      flex: 1;
      padding-right: 8px;
      .custom-tree-type {
        color: #999;
        font-style: oblique;
        font-size: 12px;
      }
    }
  }
</style>
