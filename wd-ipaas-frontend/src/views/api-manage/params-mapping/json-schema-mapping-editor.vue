<template>
  <div class="json-schema-mapping-editor">
    <el-row class="jse-row" :gutter="10">
      <!-- 参数名称 -->
      <el-col :span="6" class="jse-name">
        <div :style="{ marginLeft: `${10 * deep}px` }" class="jse-name-arrow">
          <i
            v-if="isObject || isObjectArray"
            :class="hidden ? 'el-icon-caret-right' : 'el-icon-caret-bottom'"
            @click="hidden = !hidden"
          />
          <span v-else style="width: 14px; display: inline-block"></span>
          <div class="jse-name-row" :style="`width: calc(100% - ${10 * deep}px;)`">
            <span v-if="!disabled && !root" class="required">*</span>
            <el-form style="width: 100%" :model="pickValue" :rules="rules">
              <el-form-item style="margin: 0px; width: 100%" prop="name">
                <el-input
                  ref="nameInput"
                  v-model.trim="pickValue.name"
                  style="width: 100%"
                  :disabled="disabled || root"
                  class="jse-name-input"
                  :maxlength="50"
                  placeholder="请输入参数名称"
                  @input="onInputName"
                />
              </el-form-item>
            </el-form>
          </div>
        </div>
      </el-col>
      <!-- 参数类型 -->
      <el-col :span="4">
        <el-cascader
          v-model="cascaderValue"
          :disabled="disabled || root"
          class="jse-type"
          :options="typeOptions"
          :show-all-levels="false"
          :props="{ expandTrigger: 'hover' }"
          @change="onChangeType"
        >
        </el-cascader>
      </el-col>
      <!-- 选择映射参数 -->
      <el-col :span="4">
        <el-select
          v-model="pickValue.value"
          :disabled="root || disabled || disabledMapping"
          placeholder="请选择映射参数"
          clearable
        >
          <el-option
            v-for="item in mappingOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
      </el-col>
      <el-col :span="6" class="description" :title="pickValue.description">
        <el-input
          v-model="pickValue.description"
          :disabled="disabled || root"
          class="jse-description"
          placeholder="最多输入1000个字符"
          :maxlength="1000"
        />
      </el-col>
      <!-- 操作 -->
      <el-col v-if="!disabled" :span="4">
        <div class="operation-col">
          <!-- 新增子节点 -->
          <i v-if="isObject || isArrayObject || root" class="el-icon-plus size" @click="addChild"></i>
          <!-- 删除节点 -->
          <i v-if="!root" class="el-icon-close size" @click="removeNode"></i>
        </div>
      </el-col>
    </el-row>
    <template v-if="!hidden && isObject">
      <JsonSchemaMappingEditor
        v-for="(item, key, index) in pickValue.properties"
        :key="index"
        :value="{ [key]: item }"
        :parent="pickValue"
        :deep="deep + 1"
        :disabled="disabled || isArrayRoot"
        :root="false"
        :mapping-options="mappingOptions"
        :inited="init"
        :disabled-mapping="disabledMapping"
      />
    </template>
    <template v-if="!hidden && isObjectArray">
      <JsonSchemaMappingEditor
        v-for="(item, key, index) in pickValue.items.properties"
        :key="index"
        :value="{ [key]: item }"
        :parent="pickValue"
        :deep="deep + 1"
        :disabled="disabled || isArrayRoot"
        :root="false"
        :mapping-options="mappingOptions"
        :inited="init"
        disabled-mapping
      />
    </template>
  </div>
</template>

<script>
  import {
    REQUEST_ROOT_TYPE_OPTIONS,
    CASCADER_DATA_TYPE_OPTIONS,
    DATA_TYPE_ENUM,
    ARRAY_SUB_LABEL,
  } from '@/components/json-schema-editor/type/type';
  import Validator from '@/utils/validator.js';

  export default {
    name: 'JsonSchemaMappingEditor',
    components: {},
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
      disabled: {
        type: Boolean,
        default: false,
      },
      root: {
        // 是否root节点
        type: Boolean,
        default: true,
      },
      mappingOptions: {
        type: Array,
        default: () => [],
      },
      /**
       * 解决首次加载弹窗参数名称输入不聚焦
       */
      inited: {
        type: Boolean,
        default: false,
      },
      /**
       * 禁用 数组对象下面所有子类 选择参数映射
       */
      disabledMapping: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        hidden: false,
        REQUEST_ROOT_TYPE_OPTIONS,
        CASCADER_DATA_TYPE_OPTIONS,
        DATA_TYPE_ENUM,
        ARRAY_SUB_LABEL,
        customing: false,
        init: this.inited,
      };
    },
    computed: {
      /**
       * rules
       */
      rules() {
        let validator = Validator.httpFieldEnNameValidator;
        if (this.selectNameMode) {
          validator = Validator.variableNameValidator;
        }
        return {
          name: [{ required: true, validator }],
        };
      },
      pickType() {
        return this.pickValue.type;
      },
      pickValue() {
        return Object.values(this.value)[0];
      },
      pickKey() {
        return Object.keys(this.value)[0];
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
      /**
       * 是否是对象数组Array[Object],其余为基本数据类型数组Array[string|integer...]
       */
      isArrayObject() {
        return this.pickType === DATA_TYPE_ENUM.ARRAY && this.pickValue.items?.type === DATA_TYPE_ENUM.OBJECT;
      },
      /**
       * 根节点是数组对象类型，则所有子节点都不可输入,除了Mock列
       */
      isArrayRoot() {
        return this.deep === 0 && this.value?.root.type === 'array';
      },
      /**
       * 参数类型
       */
      typeOptions() {
        return (this.root && [this.REQUEST_ROOT_TYPE_OPTIONS[0]]) || this.CASCADER_DATA_TYPE_OPTIONS;
      },

      /**
       * el-cascader级联选择框绑定值，一级值示例：['string']，二级值示例：['array','string']
       */
      cascaderValue: {
        get() {
          if (this.pickType === DATA_TYPE_ENUM.ARRAY) {
            if (this.root) {
              return [this.ARRAY_SUB_LABEL.OBJECT];
            } else {
              return [this.pickType, this.pickValue.items.type];
            }
          }

          return [this.pickType];
        },

        set(val) {
          this.$delete(this.pickValue, 'type');
          this.$delete(this.pickValue, 'items');
          if (val[0] === this.ARRAY_SUB_LABEL.OBJECT) {
            this.$set(this.pickValue, 'type', 'array');
            this.$set(this.pickValue, 'items', { type: 'object', name: 'items' });
          } else {
            this.$set(this.pickValue, 'type', val[0]);
          }
          if (val.length === 2) {
            // 有二级级联选择
            this.$set(this.pickValue, 'items', { type: val[1] || 'object', name: 'items' });
          }
        },
      },
    },
    mounted() {
      if (this.init) {
        /**
         * 自动聚焦
         */
        this.$refs?.nameInput?.focus();
      }
      this.init = true;
    },
    methods: {
      addChild() {
        const name = this._joinName();
        const type = 'string';
        const node = this.isArrayObject ? this.pickValue.items : this.pickValue;
        node.properties || this.$set(node, 'properties', {});
        const props = node.properties;
        this.$set(props, name, { type, name: '' });
      },
      removeNode() {
        const { properties, required } = this.parent.type === DATA_TYPE_ENUM.ARRAY ? this.parent.items : this.parent;
        this.$delete(properties, this.pickKey);
        if (required) {
          const pos = required.indexOf(this.pickValue.name);
          pos >= 0 && required.splice(pos, 1);
          required.length === 0 && this.$delete(this.parent, 'required');
        }
      },
      _joinName() {
        return `field_` + Math.random();
      },
      /**
       * 参数名限制只能输入字母、数字、下划线
       * @param {string} value input框输入值
       */
      onInputName(value) {
        this.pickValue.name = value.replace(/[^\w]/g, '');
      },
      onChangeType() {
        this.$delete(this.pickValue, 'properties');
        this.$delete(this.pickValue, 'value');
        this.$delete(this.pickValue, 'description');
      },
    },
  };
</script>

<style lang="less" scoped>
  .json-schema-mapping-editor {
    ::v-deep .jse-row {
      height: auto;
      display: flex;
      align-items: center;
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
      flex: 1;
    }
    .required {
      color: red;
      font-size: 12px;
      margin: auto 5px;
    }
    .description {
      overflow: hidden;
      text-overflow: ellipsis;
      -webkit-line-clamp: 2;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      max-height: 100px;
      line-height: 32px;
    }
    .operation-col {
      width: 100%;
      min-height: 32px;
      display: flex;
      flex-direction: row;
      align-items: center;

      .size {
        font-size: 20px;
        cursor: pointer;
      }
    }
    ::v-deep .el-form {
      .el-form-item__content {
        width: 100%;
      }

      .is-error {
        height: 60px;
        display: flex;
        align-items: center;
      }
    }

    .el-cascader,
    .el-select {
      width: 100%;
    }
  }
</style>
