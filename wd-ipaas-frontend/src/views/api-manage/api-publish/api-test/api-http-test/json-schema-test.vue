<template>
  <div class="json-schema-test">
    <el-row class="jse-row" :gutter="10">
      <!-- 参数名称 -->
      <el-col :span="5" class="jse-name">
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
      <el-col :span="3"> {{ type }} </el-col>
      <el-col :span="2">
        <!-- 是否必填 -->
        <div class="jse-name-required">
          <el-checkbox disabled :checked="checked" class="jse-name-required-checkbox" />
        </div>
      </el-col>
      <!-- Mock -->
      <el-col :span="3" class="mock">
        <el-select v-model="pickValue.mockRule" :disabled="disableMock" placeholder="不启用" clearable filterable>
          <el-option-group v-for="i in filterMockType(type)" :key="i.label" :label="i.label">
            <el-option
              v-for="item in i.options"
              :key="item.value"
              class="mock-select"
              :label="item.label"
              :value="item.value"
            >
              <span class="mock-select-item__label" :title="item.label">{{ item.label }}</span>
              <span class="mock-select-item__desc">{{ item.desc }}</span>
            </el-option>
          </el-option-group>
        </el-select>
        <!-- <i v-if="!disableMock" class="el-icon-setting mock-setting" @click="onClickMockSetting"></i> -->
      </el-col>
      <el-col :span="4">
        <!-- 参数值 -->
        <el-form-item :rules="valueRule" prop="value">
          <!-- input不支持布尔型 -->
          <el-radio-group v-if="type === 'boolean'" v-model="pickValue.value" :disabled="disableValue || disabled">
            <el-radio :label="true">true</el-radio>
            <el-radio :label="false">false</el-radio>
          </el-radio-group>
          <el-input
            v-else
            v-model="pickValue.value"
            :disabled="disableValue || disabled"
            class="jse-title"
            placeholder="请输入参数值"
          />
        </el-form-item>
      </el-col>

      <el-col :span="6" class="description" :title="pickValue.description">{{ pickValue.description }} </el-col>
    </el-row>
    <template v-if="!hidden && isObject">
      <JsonSchemaTest
        v-for="(item, key, index) in pickValue.properties"
        :key="index"
        :value="{ [key]: item }"
        :parent="pickValue"
        :deep="deep + 1"
        :disabled="disabled || isArrayRoot || isArrayParent"
      />
    </template>
    <template v-if="!hidden && isObjectArray">
      <JsonSchemaTest
        v-for="(item, key, index) in pickValue.items.properties"
        :key="index"
        :value="{ [key]: item }"
        :parent="pickValue"
        :deep="deep + 1"
        :disabled="disabled || isArrayRoot || isArrayParent"
      />
    </template>
    <mock-dialog
      v-if="mockDialogShow"
      v-model="pickValue.mockRule"
      :visible.sync="mockDialogShow"
      @save="onSaveMockRule"
    ></mock-dialog>
  </div>
</template>

<script>
  import { filterMockType } from '@/utils/enum/mock-common-rule';
  import MockDialog from './mock-dialog.vue';

  export default {
    name: 'JsonSchemaTest',
    components: {
      MockDialog,
    },
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
    },
    data() {
      return {
        hidden: false,
        valueRule: {
          // eslint-disable-next-line vue/no-computed-properties-in-data
          required: this.checked,
          trigger: 'blur',
          validator: (r, v, callback) => {
            let { type, value, items } = this.pickValue || {};
            if (!this.disableValue) {
              try {
                if (!value && this.checked) {
                  throw new Error();
                }
                if (value) {
                  if (type === 'integer' || type === 'number') {
                    if (isNaN(Number(value))) {
                      throw new Error();
                    }
                  }

                  if (type === 'boolean') {
                    if (typeof value !== 'boolean' && value !== 'true' && value !== 'false') {
                      throw new Error();
                    }
                  }
                  if (type === 'array') {
                    value = JSON.parse(value);
                  }

                  // 数组格式校验、对象数组格式校验
                  if (
                    type === 'array' &&
                    (!Array.isArray(value) || (items.type === 'object' && value.some(i => typeof i !== 'object')))
                  ) {
                    throw new Error();
                  }
                }
              } catch (error) {
                callback(new Error('值为空或者格式错误'));
              }
            }
            callback();
          },
        },
        mockDialogShow: false,
        ruleVal: '',
        filterMockType,
      };
    },
    computed: {
      pickValue() {
        const temp = Object.values(this.value)[0];
        /** boolean类型有可能是'true' */
        if (temp.type === 'boolean' && typeof temp.value === 'string') {
          temp.value = temp.value === 'true';
        }
        return temp;
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
      /**
       * 参数值禁用规则
       * 以下情况禁用
       * 1.类型为object
       * 2.父节点类型为数组
       */
      disableValue() {
        return this.isObject || this.parent?.type === 'array';
      },
      /**
       * Mock禁用规则
       * 1.类型为object
       * 2.对象数组
       */
      disableMock() {
        return this.isObject || this.isObjectArray;
      },
      /**
       * 根节点是数组对象类型，则所有子节点都不可输入,除了Mock列
       */
      isArrayRoot() {
        return this.deep === 0 && this.value?.root.type === 'array';
      },
      /**
       * 父节点是数组，则子节点参数值都不允许输入
       */
      isArrayParent() {
        return this.parent?.type === 'array';
      },
    },
    methods: {
      onSaveMockRule(val) {
        this.$set(this.pickValue, 'mockRule', val);
      },
      onClickMockSetting() {
        this.mockDialogShow = true;
      },
    },
  };
</script>

<style lang="less" scoped>
  .json-schema-test {
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
      line-height: 28px;
    }
  }
  .mock {
    &-setting {
      padding: 5px;
      cursor: pointer;
    }
  }
  ::v-deep .mock-select {
    &-item__label {
      float: left;
      width: 150px;
      padding-right: 10px;
      text-overflow: ellipsis;
      overflow: hidden;
    }

    &-item__desc {
      float: right !important;
      color: #8492a6;
      font-size: 13px;
    }
  }
</style>
