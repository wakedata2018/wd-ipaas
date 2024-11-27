<template>
  <div class="json-schema-editor">
    <el-row class="jse-row" :gutter="10">
      <!-- 参数名称 -->
      <el-col :span="isBody ? 6 : 4" class="jse-name">
        <div :style="{ marginLeft: `${10 * deep}px` }" class="jse-name-arrow">
          <i
            v-if="isObject || isArrayObject"
            :class="hidden ? 'el-icon-caret-right' : 'el-icon-caret-bottom'"
            @click="hidden = !hidden"
          />
          <span v-else style="width: 14px; display: inline-block"></span>
          <div class="jse-name-row" :style="`width: calc(100% - ${10 * deep}px;)`">
            <span v-if="!disabled && !root && !preview" class="required">*</span>
            <el-form style="width: 100%" :model="pickValue" :rules="rules">
              <el-form-item style="margin: 0px; width: 100%" prop="name">
                <el-input
                  v-if="!preview && !selectNameMode"
                  ref="nameInput"
                  v-model.trim="pickValue.name"
                  style="width: 100%"
                  :disabled="disabledType || (disabled && isItem) || root || isFlowDetail || isTimerTask"
                  class="jse-name-input"
                  :maxlength="50"
                  placeholder="请输入参数名称"
                  @input="onInputName"
                />
                <div v-else-if="!preview && selectNameMode">
                  <div v-if="pickValue.name" class="map-value">
                    <el-tooltip :content="pickValue?.name ?? pickKey" placement="top">
                      <el-input
                        v-model="pickValue.name"
                        placeholder="请输入参数名称"
                        class="jse-name-input"
                        :disabled="disabledType || (disabled && isItem) || root || isFlowDetail || isTimerTask"
                      />
                    </el-tooltip>
                    <i
                      v-if="(!disabledType && !root) || (disabled && isItem) || isFlowDetail || isTimerTask"
                      class="el-icon-setting variable-setting"
                      @click="selectVariable"
                    >
                    </i>
                  </div>
                  <el-button
                    v-else
                    type="text"
                    :disabled="disabledType || (disabled && isItem) || root || isFlowDetail || isTimerTask"
                    class="arrange-disable"
                    @click="selectVariable"
                    >选择变量</el-button
                  >
                </div>
                <span v-else>{{ pickValue?.name ?? pickKey }}</span>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </el-col>
      <!-- 参数类型 -->
      <el-col :span="4">
        <el-cascader
          v-if="!preview"
          v-model="cascaderValue"
          :disabled="
            disabledType || (disabled && isItem) || isFlowDetail || isTimerTask || isResponseType || disableRootType
          "
          class="jse-type"
          :options="typeOptions"
          :show-all-levels="false"
          :props="{ expandTrigger: 'hover' }"
          @change="onChangeType"
        >
        </el-cascader>
        <span v-else>{{ pickType }}</span>
      </el-col>
      <!-- 是否必填 -->
      <el-col v-if="isShowRequired && (isBody || isFlowDetail || isTimerTask)" :span="2">
        <div class="jse-name-required">
          <span v-if="preview">{{ checked ? '是' : '否' }}</span>
          <el-checkbox
            v-else-if="root"
            disabled
            :checked="rootChecked"
            class="jse-name-required-checkbox"
            @change="onRootCheck"
          />
          <el-checkbox
            v-else
            :disabled="disabledType || (disabled && isItem) || isFlowDetail || isTimerTask"
            :checked="checked"
            class="jse-name-required-checkbox"
            @change="onCheck"
          />
        </div>
      </el-col>
      <!-- 示例值 -->
      <el-col v-if="isShowSample" :span="4">
        <span v-if="preview">{{ pickValue.value }}</span>
        <el-input
          v-else
          v-model="pickValue.value"
          :disabled="
            disabledType || (disabled && isItem) || isFlowDetail || isObject || isArrayObject || isTimerTask || root
          "
          class="jse-title"
          placeholder="请输入示例值"
          :maxlength="1000"
        />
      </el-col>
      <!-- 流程编排 API详情 -->
      <template v-if="isFlowResponse || isFlowDetail || isTimerTask">
        <!-- 选择方式 -->
        <el-col :span="3">
          <span v-if="preview && !isItem">{{ ModeMap[pickValue.paramValueType] }}</span>
          <el-select
            v-model="pickValue.paramValueType"
            :disabled="disabled || isObject || isItem || disabledSample"
            placeholder="请选择方式"
            clearable
            @change="modeChange"
            @clear="modeChange"
          >
            <el-option v-for="key in Object.keys(ModeMap)" :key="key" :label="ModeMap[key]" :value="key"></el-option>
          </el-select>
        </el-col>
        <!-- 选择参数 -->
        <el-col :span="5">
          <span v-if="preview">
            {{ pickValue.paramValueType === ModeEnum.fixed ? pickValue.defaultValue : pickValue.expression }}
          </span>
          <div v-else-if="pickValue.paramValueType === ModeEnum.reference">
            <div v-if="pickValue.expression" class="map-value">
              <el-tooltip :content="pickValue.expression" placement="top">
                <el-input
                  v-model="pickValue.expression"
                  placeholder="请输入表达式"
                  :disabled="disabled || isObject || isItem"
                ></el-input>
              </el-tooltip>
              <i v-if="!disabled" class="el-icon-setting expression-setting" @click="selectParams"></i>
            </div>
            <el-button
              v-else
              type="text"
              :disabled="disabled || isObject || isItem"
              class="arrange-disable"
              @click="selectParams"
              >选择参数</el-button
            >
          </div>
          <div v-else-if="pickValue.paramValueType === ModeEnum.method">
            <template v-if="!(isTimerTask && isItem)">
              <div v-if="pickValue.expression" class="map-value">
                <el-tooltip :content="pickValue.expression" placement="top">
                  <el-input
                    v-model="pickValue.expression"
                    placeholder="请输入表达式"
                    :disabled="disabled || isObject || isItem"
                  ></el-input>
                </el-tooltip>
                <i v-if="!disabled" class="el-icon-setting expression-setting" @click="selectMethod"></i>
              </div>
              <div v-else class="expression-btn">
                <el-button
                  type="text"
                  :disabled="disabled || isObject || isItem"
                  class="arrange-disable"
                  @click="selectMethod"
                  >配置表达式</el-button
                >
              </div>
            </template>
          </div>
          <div v-else>
            <el-input
              v-model="pickValue.defaultValue"
              :disabled="disabled || isObject || isItem || disabledSample || disableSelectParams"
              :placeholder="fixValuePlaceholder"
            ></el-input>
          </div>
        </el-col>
      </template>
      <!-- 描述 -->
      <el-col :span="4">
        <el-input
          v-if="!preview"
          v-model="pickValue.description"
          :disabled="disabledType || (disabled && isItem) || isFlowDetail || isTimerTask"
          class="jse-description"
          placeholder="请输入描述"
          :maxlength="1000"
        />
        <span v-else>{{ pickValue.description }}</span>
      </el-col>
      <!-- 操作 -->
      <el-col v-if="!disabledType && !preview && !disabled && !isFlowDetail && !isTimerTask" :span="4">
        <div class="operation-col">
          <!-- 高级设置 -->
          <!-- <i class="el-icon-s-tools size" @click="onSetting"></i> -->
          <!-- 新增子节点 -->
          <i v-if="isObject || isArrayObject || root" class="el-icon-plus size" @click="addChild"></i>
          <!-- 删除节点 -->
          <i v-if="!root" class="el-icon-close size" @click="removeNode"></i>
        </div>
      </el-col>
    </el-row>
    <template v-if="!hidden && pickValue.properties && !isArrayObject">
      <JsonSchemaEditor
        v-for="(item, key, index) in pickValue.properties"
        :key="index"
        :value="{ [key]: item }"
        :parent="pickValue"
        :mode="mode"
        :deep="deep + 1"
        :disabled-type="disabledType"
        :disabled-sample="disabledSample || isArray"
        :root="false"
        class="children"
        :custom="custom"
        :preview="preview"
        :is-show-required="isShowRequired"
        :disabled="disabled"
        :is-item="isItem"
        :is-show-sample="isShowSample"
        :select-name-mode="selectNameMode"
        :inited="init"
        @select-params="selectParams"
        @select-method="selectMethod"
        @select-variable="selectVariable"
      />
    </template>
    <template v-if="!hidden && isArrayObject">
      <JsonSchemaEditor
        v-for="(item, key, index) in pickValue.items.properties"
        :key="index"
        :value="{ [key]: item }"
        :parent="pickValue"
        :mode="mode"
        :deep="deep + 1"
        :disabled-type="disabledType"
        :disabled-sample="disabledSample || isArray"
        is-item
        :root="false"
        class="children"
        :custom="custom"
        :preview="preview"
        :is-show-required="isShowRequired"
        :is-show-sample="isShowSample"
        :disabled="disabled"
        :select-name-mode="selectNameMode"
        :inited="init"
        @select-params="selectParams"
        @select-method="selectMethod"
        @select-variable="selectVariable"
      />
    </template>
    <el-dialog
      :visible="modalVisible"
      title="高级设置"
      width="800px"
      center
      class="json-schema-dialog"
      @close="modalVisible = false"
    >
      <h3>基础设置</h3>
      <el-form v-model="advancedValue" class="ant-advanced-search-form">
        <el-row :gutter="6">
          <el-col v-for="(item, key) in advancedValue" :key="key" :span="8">
            <el-form-item>
              <span>{{ languageMap[key] }}</span>
              <el-input-number
                v-if="advancedAttr[key].type === 'integer' || advancedAttr[key].type === 'number'"
                v-model="advancedValue[key]"
                style="width: 100%"
                :placeholder="key"
              />
              <span v-else-if="advancedAttr[key].type === 'boolean'" style="display: inline-block; width: 100%">
                <el-switch v-model="advancedValue[key]" />
              </span>
              <el-input
                v-else-if="key === 'enum'"
                type="textarea"
                :value="enumText"
                :rows="2"
                placeholder="每行写一个值"
                @blur="changeEnumValue"
              ></el-input>
              <el-select
                v-else-if="advancedAttr[key].type === 'array'"
                v-model="advancedValue[key]"
                style="width: 100%"
                :placeholder="key"
              >
                <!-- <el-option value="">无</el-option> -->
                <el-option v-for="t in advancedAttr[key].enums" :key="t" :label="t" :value="t" />
              </el-select>
              <el-input v-else v-model="advancedValue[key]" style="width: 100%" :placeholder="key" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <h3 v-show="custom">添加自定义属性</h3>
      <el-form v-show="custom" class="ant-advanced-search-form">
        <el-row :gutter="6">
          <el-col v-for="item in customProps" :key="item.key" :span="8">
            <el-form-item :label="item.key">
              <el-input v-model="item.value" style="width: calc(100% - 30px)" />
              <el-button
                icon="el-icon-close"
                style="width: 30px"
                @click="customProps.splice(customProps.indexOf(item), 1)"
              ></el-button>
            </el-form-item>
          </el-col>
          <el-col v-show="addProp.key != undefined" :span="8">
            <el-form-item>
              <el-input slot="label" v-model="addProp.key" style="width: 100px" />
              <el-input v-model="addProp.value" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col v-if="!preview" :span="8">
            <el-form-item>
              <el-button v-if="customing" icon="el-icon-check" @click="confirmAddCustomNode(null)"></el-button>
              <el-tooltip v-else title="添加自定义属性">
                <el-button icon="el-icon-plus" @click="addCustomNode"></el-button>
              </el-tooltip>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <h3>预览</h3>
      <pre style="width: 100%">{{ completeNodeValue }}</pre>

      <span slot="footer" class="dialog-footer">
        <el-button @click="modalVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleOk">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
  import { isNull } from './util';
  import {
    TYPE_NAME,
    TYPE,
    CASCADER_DATA_TYPE_OPTIONS,
    DATA_TYPE_ENUM,
    PARAMS_TYPE,
    REQUEST_ROOT_TYPE_OPTIONS,
    ARRAY_SUB_LABEL,
  } from './type/type';
  import languageMap from './language-map';
  import textUtils from '@/utils/text-utils';
  import { ModeMap, ModeEnum } from '@/utils/enum';
  import swapVertical from '@/assets/images/swapVertical.png';
  import Validator from '@/utils/validator.js';

  export default {
    name: 'JsonSchemaEditor',
    props: {
      value: {
        type: Object,
        required: true,
      },
      disabled: {
        // name不可编辑，根节点name不可编辑
        type: Boolean,
        default: false,
      },
      // 禁用根类型选择 默认不禁用
      disableRootType: {
        type: Boolean,
        default: false,
      },
      disabledType: {
        // 禁用类型选择 开启可用于只读模式
        type: Boolean,
        default: false,
      },
      isItem: {
        // 是否数组元素
        type: Boolean,
        default: false,
      },
      deep: {
        // 节点深度，根节点deep=0
        type: Number,
        default: 0,
      },
      root: {
        // 是否root节点
        type: Boolean,
        default: true,
      },
      parent: {
        // 父节点
        type: Object,
        default: null,
      },
      custom: {
        // enable custom properties
        type: Boolean,
        default: false,
      },
      mode: {
        // 'body'为api详情中body参数设置
        // 'flowResponse' 为服务编排中整体返回参数选择
        // 'flowDetail' 为服务编排中单个api详情中body参数需要映射
        type: String,
        default: 'body',
      },
      /**
       * FIX:该模式废弃
       */
      preview: {
        type: Boolean,
        default: false,
      },
      /**
       * 显示是否必填
       */
      isShowRequired: {
        type: Boolean,
        default: true,
      },
      /**
       *  一级类型为数组，禁用子类型的示例值
       */
      disabledSample: {
        type: Boolean,
        default: false,
      },
      /**
       * 是否显示示例值 默认true
       */
      isShowSample: {
        type: Boolean,
        default: true,
      },
      /**
       * 流程编排中更新算子参数名称可选择
       */
      selectNameMode: {
        type: Boolean,
        default: false,
      },
      /**
       * 解决首次加载弹窗参数名称输入不聚焦
       */
      inited: {
        type: Boolean,
        default: false,
      },
      /**
       * request 请求体 | response响应体
       */
      treeType: {
        type: String,
        default: 'request',
      },
    },
    data() {
      return {
        swapVertical,
        ModeMap: { ...ModeMap },
        ModeEnum,
        languageMap,
        TYPE_NAME,
        hidden: false,
        countAdd: 1,
        modalVisible: false,
        advancedValue: {},
        addProp: {}, // 自定义属性
        customProps: [],
        customing: false,
        CASCADER_DATA_TYPE_OPTIONS,
        ARRAY_SUB_LABEL,
        DATA_TYPE_ENUM,
        init: this.inited,
        PARAMS_TYPE,
        REQUEST_ROOT_TYPE_OPTIONS,
      };
    },
    computed: {
      /**
       * 响应体类型
       */
      isResponseType() {
        return this.treeType === this.PARAMS_TYPE.RESPONSE;
      },
      /**
       * 是否是对象数组Array[Object],其余为基本数据类型数组Array[string|integer...]
       */
      isArrayObject() {
        return this.pickType === DATA_TYPE_ENUM.ARRAY && this.pickValue.items?.type === DATA_TYPE_ENUM.OBJECT;
      },

      /**
       * 一级类型为数组，禁用子类型的示例值
       */
      isArray() {
        return this.pickType === DATA_TYPE_ENUM.ARRAY;
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
          if (this.isRequestRoot && val[0] === this.ARRAY_SUB_LABEL.OBJECT) {
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

      pickValue() {
        return Object.values(this.value)[0];
      },
      pickType() {
        return this.pickValue.type;
      },
      pickKey() {
        return Object.keys(this.value)[0];
      },
      isObject() {
        return this.pickValue?.type === DATA_TYPE_ENUM.OBJECT;
      },
      checked() {
        return this.parent && this.parent.required && this.parent.required.indexOf(this.pickKey) >= 0;
      },
      rootChecked() {
        return this.pickValue.rootRequired || false;
      },
      advanced() {
        return TYPE[this.pickType];
      },
      advancedAttr() {
        return TYPE[this.pickValue.type].attr;
      },
      advancedNotEmptyValue() {
        const jsonNode = Object.assign({}, this.advancedValue);
        for (const key in jsonNode) {
          isNull(jsonNode[key]) && delete jsonNode[key];
        }
        return jsonNode;
      },
      completeNodeValue() {
        const t = {};
        for (const item of this.customProps) {
          t[item.key] = item.value;
        }
        return Object.assign({}, this.pickValue, this.advancedNotEmptyValue, t);
      },
      enumText() {
        const t = this.advancedValue.enum;
        if (!t) {
          return '';
        }
        if (!t.length) {
          return '';
        }
        return t.join('\n');
      },
      isBody() {
        return this.mode === 'body';
      },
      isFlowResponse() {
        return this.mode === 'flowResponse';
      },
      isFlowDetail() {
        return this.mode === 'flowDetail';
      },
      /**
       * 定时任务模式
       */
      isTimerTask() {
        return this.mode === 'timerTask';
      },
      /**
       * 选择方式为空时，参数值不可输入
       */
      disableSelectParams() {
        return !this.pickValue.paramValueType;
      },
      /**
       * 参数值Placheholder
       */
      fixValuePlaceholder() {
        if (this.disableSelectParams) {
          return '';
        }
        return '请输入固定值';
      },
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
      /**
       * 请求体Root节点
       */
      isRequestRoot() {
        return this.treeType === this.PARAMS_TYPE.REQUEST && this.root;
      },
      /**
       * 参数类型
       */
      typeOptions() {
        return (this.isRequestRoot && this.REQUEST_ROOT_TYPE_OPTIONS) || this.CASCADER_DATA_TYPE_OPTIONS;
      },
    },

    created() {
      // 定时任务需要删除引用值选项
      if (this.isTimerTask) {
        Reflect.deleteProperty(this.ModeMap, ModeEnum.reference);
      }
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
      validator() {
        const res = textUtils.hasEmptyOrMultiName(this.value?.root?.properties);
        if (!res.success) {
          throw new Error('BODY参数名称' + res.message);
        }
      },
      modeChange() {
        this.$set(this.pickValue, 'expression', '');
        this.$set(this.pickValue, 'defaultValue', '');
      },
      parseCustomProps() {
        const ownProps = [
          'name',
          'type',
          'value',
          'properties',
          'items',
          'required',
          'description',
          'paramValueType',
          'expression',
          'defaultValue',
          'operatorId',
          ...Object.keys(this.advancedAttr),
        ];
        Object.keys(this.pickValue).forEach(key => {
          ownProps.indexOf(key) === -1 && this.confirmAddCustomNode({ key, value: this.pickValue[key] });
        });
      },

      /**
       * 参数名限制只能输入字母、数字、下划线
       * @param {string} value input框输入值
       */
      onInputName(value) {
        this.pickValue.name = value.replace(/[^\w]/g, '');
      },
      onChangeType() {
        this.parseCustomProps();
        // 删除自定义属性
        this.customProps.forEach(item => {
          this.$delete(this.pickValue, item.key);
        });
        this.customProps = [];

        this.$delete(this.pickValue, 'properties');
        this.$delete(this.pickValue, 'required');
        this.$delete(this.pickValue, 'format');
        this.$delete(this.pickValue, 'enum');
        this.$delete(this.pickValue, 'value');
        this.$delete(this.pickValue, 'paramValueType');
        this.$delete(this.pickValue, 'expression');
        this.$delete(this.pickValue, 'defaultValue');
        this.$delete(this.pickValue, 'operatorId');
      },
      onCheck(e) {
        this._checked(e, this.parent);
      },
      onRootCheck(e) {
        this.$set(this.pickValue, 'rootRequired', e);
        this._deepCheck(e, this.pickValue);
      },
      changeEnumValue(e) {
        const value = e.target.value;
        const arr = value.split('\n');

        if (this.pickType === 'string') {
          this.advancedValue.enum = arr.map(item => item);
        } else {
          if (arr.length === 0 || (arr.length === 1 && arr[0] === '')) {
            this.advancedValue.enum = null;
          } else {
            this.advancedValue.enum = arr.map(item => +item);
          }
        }
      },
      _deepCheck(checked, node) {
        if (node.type === 'object' && node.properties) {
          checked ? this.$set(node, 'required', Object.keys(node.properties)) : this.$delete(node, 'required');
          Object.keys(node.properties).forEach(key => this._deepCheck(checked, node.properties[key]));
        } else if (node.type === 'array' && node.items.type === 'object') {
          checked
            ? this.$set(node.items, 'required', Object.keys(node.items.properties))
            : this.$delete(node.items, 'required');
          Object.keys(node.items.properties).forEach(key => this._deepCheck(checked, node.items.properties[key]));
        }
      },
      _checked(checked, parent) {
        let required = parent.required;
        if (checked) {
          // 初始化required为空数组
          required || this.$set(this.parent, 'required', []);
          required = this.parent.required;
          // 找不到则加入数组中
          required.indexOf(this.pickKey) === -1 && required.push(this.pickKey);

          //
        } else {
          const pos = required.indexOf(this.pickKey);
          pos >= 0 && required.splice(pos, 1);
        }
        required.length === 0 && this.$delete(parent, 'required');
      },
      addChild() {
        const name = this._joinName();
        const type = 'string';
        const node = this.isArrayObject ? this.pickValue.items : this.pickValue;
        node.properties || this.$set(node, 'properties', {});
        const props = node.properties;
        this.$set(props, name, { type, name: '' });
      },
      addCustomNode() {
        this.$set(this.addProp, 'key', this._joinName());
        this.$set(this.addProp, 'value', '');
        this.customing = true;
      },
      confirmAddCustomNode(prop) {
        const p = prop || this.addProp;
        this.customProps.push(p);
        this.addProp = {};
        this.customing = false;
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
        // return `field_${this.deep}_${this.countAdd++}`;
        return `field_` + Math.random();
      },
      onSetting() {
        this.modalVisible = true;
        this.advancedValue = { ...this.advanced.value };
        for (const k in this.advancedValue) {
          if (this.pickValue[k]) {
            this.advancedValue[k] = this.pickValue[k];
          }
        }
        this.parseCustomProps();
      },

      handleOk() {
        this.modalVisible = false;
        for (const key in this.advancedValue) {
          if (isNull(this.advancedValue[key])) {
            this.$delete(this.pickValue, key);
          } else {
            this.$set(this.pickValue, key, this.advancedValue[key]);
          }
        }
        for (const item of this.customProps) {
          this.$set(this.pickValue, item.key, item.value);
        }
      },
      selectParams(callback) {
        const args =
          typeof callback === 'function'
            ? callback
            : data => {
                this.pickValue.expression = data.id;
                this.pickValue.operatorId = data.operatorId;
                this.$forceUpdate();
              };

        this.$emit('select-params', args);
      },
      selectVariable(callback) {
        const args =
          typeof callback === 'function'
            ? callback
            : data => {
                this.pickValue.name = data;
                this.$forceUpdate();
              };

        this.$emit('select-variable', args);
      },
      selectMethod(exp, callback) {
        const _exp = typeof exp === 'string' ? exp : this.pickValue.expression;

        const args =
          typeof callback === 'function'
            ? callback
            : data => {
                this.$set(this.pickValue, 'expression', data);
              };

        this.$emit('select-method', _exp, args);
      },
    },
  };
</script>
<style lang="less" scoped>
  .json-schema-editor {
    ::v-deep .jse-row {
      height: auto;
      display: flex;
      align-items: center;

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

    /deep/ .el-form-item {
      margin: 0px;
    }

    .required {
      color: red;
      font-size: 12px;
      margin: auto 5px;
    }

    .jse-name-input {
      width: 100% !important;
    }

    .jse-type,
    .jse-select {
      width: 100% !important;

      ::v-deep {
        .el-input {
          width: 100%;
        }
      }
    }

    .jse-name-required {
      width: 100%;
      height: 32px;
      display: flex;
      align-items: center;
    }

    .jse-title {
      width: 100% !important;
    }

    .jse-description {
      width: 100% !important;
    }

    .operation-col {
      width: 100%;
      min-height: 32px;
      display: flex;
      flex-direction: row;
      align-items: center;

      .size {
        // margin-left: 10px;
        font-size: 20px;
        cursor: pointer;
      }
    }

    .json-schema-dialog {
      z-index: 2005;
    }

    .arrange-disable {
      width: 100%;
      border-radius: 4px;
      border: 1px solid #dcdfe6;
      text-align: left;
      height: 28px;
      padding: 0 15px;
    }

    .map-value {
      flex: 1;
      position: relative;
      display: flex;
      align-items: center;

      & /deep/ .el-input__inner {
        padding-right: 24px;
      }

      .expression-setting,
      .variable-setting {
        cursor: pointer;
        position: absolute;
        padding: 0px 3px;
        right: 3px;
        background: #fff;
      }
    }

    .map-value-text {
      margin: 0 5px;
      width: calc(100% - 30px);
      font-size: 12px;
      color: #333;
      overflow: hidden;
      text-overflow: ellipsis;
      display: inline-block;
      white-space: nowrap;
    }

    .swap-vertical {
      width: 20px;
      height: 20px;
    }

    ::v-deep .arrayItems {
      .jse-name-arrow {
        padding-left: 15px;
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
  }
</style>
