<template>
  <el-table
    :data="responseData"
    style="width: 100%; margin-bottom: 20px"
    row-key="key"
    border
    default-expand-all
    :tree-props="{ children: 'childApiRespParams' }"
  >
    <el-table-column prop="assetColumns" min-width="120">
      <template slot-scope="scope">
        <span v-if="isParent(scope.row.key)">{{ scope.row.assetColumns }}</span>
        <el-input v-model="scope.row.assetColumns" placeholder="请输入内容" v-else :disabled="disabled"></el-input>
      </template>
    </el-table-column>
    <el-table-column prop="assetDataType" label="类型" min-width="60">
      <template slot-scope="scope">
        <div v-if="isParent(scope.row.key)"></div>
        <el-select v-model="scope.row.assetDataType" :disabled="disabled" v-else>
          <el-option v-for="item in RESPONSE_TYPE_LIST" :key="item" :label="item" :value="item"></el-option>
        </el-select>
      </template>
    </el-table-column>
    <el-table-column prop="description" label="描述" min-width="100">
      <template slot-scope="scope">
        <span v-if="isParent(scope.row.key)"></span>
        <el-input v-model="scope.row.description" placeholder="请输入描述" v-else :disabled="disabled"></el-input>
      </template>
    </el-table-column>
    <el-table-column label="操作" v-if="!disabled">
      <template slot-scope="scope">
        <el-button
          type="text"
          @click="addChild(scope.row)"
          v-if="
            scope.row.key === TYPE_ELE.HEAD.key ||
            scope.row.key === TYPE_ELE.BODY.key ||
            scope.row.assetDataType === TYPES.JSON.name ||
            scope.row.assetDataType === TYPES.OBJECT.name ||
            scope.row.assetDataType === TYPES.ARRAY.name
          "
          >新增子级</el-button
        >
        <el-button type="text" @click="deleteChild(scope.row)" v-if="!isParent(scope.row.key)">删除</el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script>
  import { TYPES_LIST, TYPES, RESPONSE_TYPE_LIST, TYPE_ELE } from '@/utils/enum';

  export default {
    components: {},
    props: {
      disabled: {
        type: Boolean,
        default: false,
      },
      tableList: {
        type: Array,
        default: () => [],
      },
    },
    data() {
      return {
        TYPES,
        TYPE_ELE,
        TYPES_LIST,
        RESPONSE_TYPE_LIST,
      };
    },
    computed: {
      responseData() {
        return this.tableList;
      },
    },
    methods: {
      /**
       * 是否初始化节点
       */
      isParent(data) {
        const arr = Object.values(this.TYPE_ELE).map(o => o.key);
        return arr.includes(data);
      },
      /**
       * 新增子节点
       */
      addChild(data) {
        const length = data?.childApiRespParams?.length ?? 0;
        if (!data.assetColumns) return this.$message.error('请先完善该父节点名称');
        const param = {
          key: `${data.assetColumns}_${length}_${Math.floor(Math.random() * 99999)}`,
          assetColumns: '',
          childApiRespParams: [],
          type: data.type,
          assetDataType: '',
        };

        data.childApiRespParams.push(param);
      },
      /**
       * 删除子节点
       */
      deleteChild(data) {
        const { key } = data;
        let result = '';
        const removeParent = (arr, val) => {
          arr.forEach(o => {
            const hasCurData = o.childApiRespParams.find(k => k.key === val);
            if (hasCurData) result = o;
            if (o.childApiRespParams.length) removeParent(o.childApiRespParams, val);
          });
          return result;
        };
        const rowParent = removeParent(this.responseData, key);
        if (rowParent) {
          const idx = rowParent.childApiRespParams.findIndex(k => k.key === key);
          rowParent.childApiRespParams.splice(idx, 1);
        }
      },
    },
  };
</script>

<style lang="less" scoped>
  .main-title {
    height: 22px;
    font-size: 16px;
    margin: 10px 0;
    font-weight: 600;
    color: rgba(51, 51, 51, 1);
    line-height: 22px;
  }
  /deep/ .el-select > .el-input {
    width: 150px !important;
  }
</style>
