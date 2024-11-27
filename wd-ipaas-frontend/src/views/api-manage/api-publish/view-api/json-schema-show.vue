<template>
  <div class="json-schema-show">
    <el-row v-if="root" class="table-head" :gutter="10">
      <el-col :span="4" class="table-head__item">参数名称</el-col>
      <el-col :span="4" class="table-head__item">参数类型</el-col>
      <el-col :span="2" class="table-head__item">是否必填</el-col>
      <el-col :span="4" class="table-head__item">示例值</el-col>
      <el-col :span="4" class="table-head__item">描述</el-col>
    </el-row>
    <el-row class="jse-row" :gutter="10">
      <!-- 参数名称 -->
      <el-col :span="4" class="jse-name">
        <div :style="{ marginLeft: `${10 * deep}px` }" class="jse-name-arrow">
          <i
            v-if="isObject || isObjectArray"
            :class="hidden ? 'el-icon-caret-right' : 'el-icon-caret-bottom'"
            @click="hidden = !hidden"
          />
          <span v-else style="width: 14px; display: inline-block"></span>
          <div class="jse-name-row" :style="`width: calc(100% - ${10 * deep}px;)`">
            {{ pickValue?.name ?? pickKey }}
          </div>
        </div>
      </el-col>
      <!-- 参数类型 -->
      <el-col :span="4"> {{ type }} </el-col>
      <el-col :span="2">
        <!-- 是否必填 -->
        {{ checked ? '是' : '否' }}
      </el-col>
      <el-col :span="4">
        <!-- 示例值 -->
        {{ pickValue.value }}
      </el-col>
      <el-col :span="4" class="description" :title="pickValue.description">{{ pickValue.description }} </el-col>
    </el-row>
    <template v-if="!hidden && isObject">
      <JsonSchemaShow
        v-for="(item, key, index) in pickValue.properties"
        :key="index"
        :value="{ [key]: item }"
        :parent="pickValue"
        :deep="deep + 1"
      />
    </template>
    <template v-if="!hidden && isObjectArray">
      <JsonSchemaShow
        v-for="(item, key, index) in pickValue.items.properties"
        :key="index"
        :value="{ [key]: item }"
        :parent="pickValue"
        :deep="deep + 1"
      />
    </template>
  </div>
</template>

<script>
  export default {
    name: 'JsonSchemaShow',
    props: {
      value: {
        type: Object,
        required: true,
      },
      /**
       * 父节点
       */
      parent: {
        type: Object,
        default: null,
      },
      /**
       * 节点深度
       */
      deep: {
        type: Number,
        default: 0,
      },
      /**
       * 是否为根层级
       */
      root: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        hidden: false,
      };
    },
    computed: {
      pickValue() {
        return Object.values(this.value)[0];
      },
      pickKey() {
        return Object.keys(this.value)[0];
      },
      checked() {
        return (this.parent?.required || []).indexOf(this.pickKey) >= 0;
      },
      type() {
        return this.pickValue.type === 'array' ? `array[${this.pickValue.items.type}]` : this.pickValue.type;
      },
      isObject() {
        return this.pickValue.type === 'object';
      },
      isArray() {
        return this.pickValue.type === 'array';
      },
      isObjectArray() {
        return this.pickValue.type === 'array' && this.pickValue.items.type === 'object';
      },
      isRoot() {
        return this.deep === 1;
      },
    },
  };
</script>

<style lang="less" scoped>
  .json-schema-show {
    font-size: 12px;
    color: #606266;
    ::v-deep .jse-row {
      height: auto;
      display: flex;
      align-items: center;
      padding: 0 10px;
      line-height: 60px;
      border-bottom: 1px solid #ebeef5;

      .el-col {
        padding: 0 10px !important;
      }
    }
    .jse-name-arrow {
      display: flex;
      flex-direction: row;
      align-items: center;
    }
    .jse-name-row {
      display: flex;
      flex-direction: row;
      align-items: center;
      // height: 26px;
      flex: 1;
    }
    .description {
      overflow: hidden;
      text-overflow: ellipsis;
      -webkit-line-clamp: 2;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      max-height: 100px;
      line-height: 25px;
    }

    .table-head {
      border-bottom: 1px solid #ebeef5;
      padding: 6px 0px;
      background-color: #f3f6f8;
      color: #909399;
      font-weight: bold;
      margin: 0px 0px 5px 0px !important;
      display: flex;
      align-items: center;
    }
    .table-head__item {
      padding: 0 10px !important;
      line-height: 23px;
      font-size: 12px;
    }
  }
</style>
