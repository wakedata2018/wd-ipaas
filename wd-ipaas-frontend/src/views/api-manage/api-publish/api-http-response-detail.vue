<template>
  <div class="api-http-response-detail">
    <el-tabs v-model="activeName">
      <el-tab-pane v-for="item in Object.entries(data)" :key="item[0]" :name="item[0]" :label="label(item[0])">
        <el-table
          class="dss-table bd-table"
          :data="item[1]"
          header-row-class-name="table-header"
          row-key="id"
          :tree-props="{ children: 'children' }"
        >
          <el-table-column prop="assetColumns" label="参数名称"> </el-table-column>
          <el-table-column prop="assetDataType" label="类型"> </el-table-column>
          <el-table-column prop="description" label="描述"> </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
    <!-- <span class="empty" v-else>暂无数据</span> -->
  </div>
</template>

<script>
  export default {
    name: 'ApiHttpResponseDetail',
    props: {
      value: {
        type: Array,
        default: () => [],
      },
    },

    data() {
      return {
        activeName: 'BODY',
      };
    },

    computed: {
      label() {
        return type => {
          const typeMap = {
            HEAD: '响应头',
            BODY: '响应体',
          };
          return typeMap[type];
        };
      },
      data() {
        const HEAD = this.value.filter(item => item.type === 'HEAD') ?? [];
        const BODY = this.value.find(item => item.type === 'BODY')?.children ?? [];
        return { HEAD, BODY };
      },
    },
  };
</script>
<style lang="less" scoped>
  .api-http-response-detail {
    min-height: 50px;
    .empty {
      display: block;
      text-align: center;
      color: #909399;
      font-size: 12px;
    }
  }
</style>
