<template>
  <div class="api-params-tree-table">
    <el-table
      :data="value"
      style="width: 100%"
      class="dss-table bd-table"
      row-key="assetColumns"
      :tree-props="{ children: 'children' }"
      default-expand-all
    >
      <el-table-column v-if="!isHidden('assetColumns')" prop="assetColumns" label="参数名称" max-width="100">
        <template #default="scope">
          <span v-if="!isColFixed('assetColumns') && isEdit" class="required">*</span>
          <el-input
            v-if="!isColFixed('assetColumns')"
            v-model.trim="scope.row.assetColumns"
            style="width: 100%"
            maxlength="50"
            :disabled="isDisable('assetColumns') || isPreview"
            placeholder="请输入参数名称"
            @change="handleAssetColumns(scope.row.assetColumns, scope.$index)"
          >
          </el-input>
          <span v-else>{{ scope.row.assetColumns }}</span>
        </template>
        <template #header> <tips-icon :content="$t('validator.httpFieldNameDesc')"></tips-icon>参数名称 </template>
      </el-table-column>
      <el-table-column v-if="!isHidden(defaultParamMap.dataType)" :prop="defaultParamMap.dataType" label="参数类型">
        <template #default="scope">
          <el-select
            v-if="!isColFixed(defaultParamMap.dataType)"
            v-model="scope.row[defaultParamMap.dataType]"
            :disabled="isDisable(defaultParamMap.dataType) || isObjectDisable(scope.row) || isPreview"
            style="width: 100%"
          >
            <el-option v-for="item in QUERY_TYPES_LIST" :key="item" :label="item" :value="item"> </el-option>
          </el-select>
          <span v-else>{{ scope.row[defaultParamMap.dataType] }}</span>
        </template>
      </el-table-column>
      <el-table-column v-if="!isHidden('required')" prop="required" label="是否必填" width="100">
        <template #default="scope">
          <el-checkbox
            v-if="!isColFixed('required')"
            v-model="scope.row.required"
            :disabled="isDisable('required') || isPreview"
          >
          </el-checkbox>
          <span v-else>{{ scope.row.required ? '是' : '否' }}</span>
        </template>
      </el-table-column>
      <el-table-column v-if="!isHidden('allowEmpty')" prop="allowEmpty" label="允许为空" width="100">
        <template #default="scope">
          <el-checkbox
            v-if="!isColFixed('allowEmpty')"
            v-model="scope.row.allowEmpty"
            :disabled="isDisable('allowEmpty') || isPreview"
          >
          </el-checkbox>
          <span v-else>{{ scope.row.required ? '是' : '否' }}</span>
        </template>
      </el-table-column>
      <el-table-column
        v-if="!isHidden(defaultParamMap.sample) && !isFlow"
        :prop="defaultParamMap.sample"
        label="示例值"
      >
        <template #default="scope">
          <el-input
            v-if="!isColFixed(defaultParamMap.sample) && !isContenTypeRow(scope.row.assetColumns)"
            v-model.trim="scope.row[defaultParamMap.sample]"
            maxlength="100"
            style="width: 100%"
            :disabled="isDisable(defaultParamMap.sample) || isObjectDisable(scope.row) || isPreview"
            type="text"
            placeholder="请输入示例值"
          >
          </el-input>

          <el-input
            v-else-if="!isColFixed(defaultParamMap.sample) && isContenTypeRow(scope.row.assetColumns)"
            v-model.trim="scope.row[defaultParamMap.sample]"
            maxlength="100"
            style="width: 100%"
            :disabled="isPreview"
            type="text"
          >
          </el-input>
          <span v-else>{{ scope.row[defaultParamMap.sample] }}</span>
        </template>
      </el-table-column>
      <!-- 选择方式 -->
      <el-table-column v-if="isFlow || isTimerTask" prop="paramValueType" label="赋值类型" max-width="100px">
        <template #default="scope">
          <el-select
            v-if="!isColFixed('paramValueType')"
            v-model="scope.row.paramValueType"
            placeholder="请选择方式"
            :disabled="isPreview"
            clearable
            @change="modeChange(scope.row)"
            @clear="modeChange(scope.row)"
          >
            <el-option v-for="key in Object.keys(ModeMap)" :key="key" :label="ModeMap[key]" :value="key"> </el-option>
          </el-select>
          <span v-else>{{ ModeMap[scope.row.paramValueType] }}</span>
        </template>
      </el-table-column>
      <!-- 参数值 -->
      <el-table-column v-if="isFlow || isTimerTask" prop="expression" label="值">
        <template #default="scope">
          <span v-if="isColFixed('expression')">
            {{ scope.row.paramValueType === ModeEnum.fixed ? scope.row.defaultValue : scope.row.expression }}
          </span>
          <div v-else-if="scope.row.paramValueType === ModeEnum.reference" class="params">
            <div v-if="scope.row.expression" class="map-value">
              <el-tooltip :content="scope.row.expression" placement="top">
                <el-input
                  v-model.trim="scope.row.expression"
                  placeholder="请输入表达式"
                  :disabled="isPreview"
                ></el-input>
              </el-tooltip>
              <i v-if="!isPreview" class="el-icon-setting expression-setting" @click="selectParams(scope.row)"></i>
            </div>
            <el-button v-else type="text" class="select-params" :disabled="isPreview" @click="selectParams(scope.row)"
              >选择参数
            </el-button>
          </div>
          <div v-else-if="scope.row.paramValueType === ModeEnum.method">
            <div v-if="scope.row.expression" class="map-value">
              <el-tooltip :content="scope.row.expression" placement="top">
                <el-input
                  v-model.trim="scope.row.expression"
                  placeholder="请输入表达式"
                  :disabled="isPreview"
                ></el-input>
              </el-tooltip>
              <i class="el-icon-setting expression-setting" @click="selectMethod(scope.row)"></i>
            </div>
            <div v-else class="expression-btn">
              <el-button type="text" class="select-params" :disabled="isPreview" @click="selectMethod(scope.row)"
                >配置表达式</el-button
              >
            </div>
          </div>
          <!-- 固定值 -->
          <el-input
            v-else-if="scope.row.paramValueType === ModeEnum.fixed && tableType === 'request'"
            v-model="scope.row.fixedValue"
            type="text"
            placeholder="请输入固定值"
            :disabled="isPreview"
          ></el-input>
          <el-input
            v-else
            v-model.trim="scope.row.defaultValue"
            type="text"
            :disabled="disableSelectParams(scope.row) || isPreview"
          ></el-input>
        </template>
      </el-table-column>
      <el-table-column v-if="!isHidden(defaultParamMap.description)" :prop="defaultParamMap.description" label="描述">
        <template #default="scope">
          <el-input
            v-if="!isColFixed(defaultParamMap.description)"
            v-model.trim="scope.row[defaultParamMap.description]"
            maxlength="100"
            style="width: 100%"
            :disabled="isDisable(defaultParamMap.description) || isObjectDisable(scope.row) || isPreview"
            placeholder="请输入描述"
          >
          </el-input>
          <span v-else>{{ scope.row[defaultParamMap.description] }}</span>
        </template>
      </el-table-column>
      <el-table-column v-if="canOperation && isEdit" label="操作">
        <template #default="scope">
          <el-button
            class="bd-button bd-table-danger"
            :disabled="isContenTypeRow(scope.row.assetColumns)"
            @click="onDelete(scope.$index, scope.row)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>
    <div v-if="canAdd && isEdit" class="btn-box">
      <el-button type="primary" size="mini" class="bd-button bd-strong" @click="onAddRow">+新增参数</el-button>
    </div>
  </div>
</template>
<script>
  import TipsIcon from '@/components/tips-icon';
  import { QUERY_TYPES_LIST, TYPES, ModeMap, ModeEnum, PARAMS_TYPE } from '@/utils/enum';

  export default {
    name: 'ApiParamsTreeTable',
    components: { TipsIcon },
    props: {
      /**
       * 展示类型： 请求/响应头 | QUERRY请求参数 | 请求体 | 响应体
       * HEAD | QUERY | REQUEST_BODY | RESPONSE_BODY
       */
      paramsType: {
        type: String,
        default: 'HEAD',
      },
      /**
       * 数据源
       */
      value: {
        type: Array,
        default: () => [],
      },
      /**
       * 显示模式：编辑模式 | 预览模式 | 固定模式
       * edit | preview | fixed
       */
      mode: {
        type: String,
        default: 'edit',
      },
      /**
       * 固定的列
       * 传入列名的字符串
       */
      fixedList: {
        type: Array,
        default: () => [],
      },
      /**
       * 隐藏的列
       */
      hiddenList: {
        type: Array,
        default: () => [],
      },
      /**
       * 禁用的列
       */
      disableList: {
        type: Array,
        default: () => [],
      },
      /**
       * 固定请求头
       */
      bodyType: {
        type: String,
        default: 'json',
      },
      /**
       * 类型
       * 请求参数 | 响应类型
       * request | response
       */
      tableType: {
        type: String,
        default: 'request',
      },
      /**
       * 服务编排新加 请求方式和参数值
       */
      isFlow: {
        type: Boolean,
        default: false,
      },

      /**
       * 定时任务
       */
      isTimerTask: {
        type: Boolean,
        default: false,
      },
      /**
       * 字段名称映射：
       * {
            name:'assetDataType', 参数名称
            description:'description', 描述
            sample:'sample' 示例值
          }
       *
       */
      paramsMap: {
        type: Object,
        default: null,
      },
      /**
       * 是否允许新增参数
       */
      canAdd: {
        type: Boolean,
        default: true,
      },
      /**
       * 是否允许操作行
       */
      canOperation: {
        type: Boolean,
        default: true,
      },
      /**
       * 对象根节点不可编辑
       */
      isObjectRootDisable: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        ModeMap: { ...ModeMap },
        ModeEnum,
        QUERY_TYPES_LIST,
        TYPES,
        PARAMS_TYPE,
      };
    },

    computed: {
      isPreview() {
        return this.mode === 'preview';
      },
      isFixed() {
        return this.mode === 'fixed';
      },
      isEdit() {
        return this.mode === 'edit';
      },
      /**
       * 请求头默认值 Content-Type
       */
      contentType() {
        return this.bodyType === 'json' ? 'application/json' : 'application/xml';
      },
      /**
       * 列名自定义映射
       */
      defaultParamMap() {
        const defaultMap = {
          dataType: 'assetDataType',
          description: 'description',
          sample: 'sample',
        };
        if (this.paramsMap) {
          return {
            ...defaultMap,
            ...this.paramsMap,
          };
        }
        return defaultMap;
      },
      /**
       * 选择方式为空时，参数值不可输入
       */
      disableSelectParams() {
        return row => {
          return !row.paramValueType;
        };
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
       * 参数错误位置名称
       */
      validateErrorPosName() {
        let transform = this.paramsType;
        if (transform === 'HEAD') {
          transform = this.tableType === 'request' ? 'REQUEST_HEAD' : 'RESPONSE_HEAD';
        }
        return this.PARAMS_TYPE[transform];
      },
      isObjectDisable() {
        return row => {
          if (this.isObjectRootDisable) {
            // eslint-disable-next-line no-prototype-builtins
            return row.hasOwnProperty('children');
          }
          return false;
        };
      },
    },
    watch: {
      /**
       * 切换请求类型 同时修改请求头
       */
      bodyType: {
        handler(val) {
          const newValue = this.value.map(item => {
            if (item.assetColumns === 'Content-Type') {
              return {
                ...item,
                sample: this.contentType,
              };
            }
            return item;
          });
          this.$emit('input', newValue);
        },
      },
    },

    created() {
      // 定时任务需要删除引用值选项
      if (this.isTimerTask) {
        Reflect.deleteProperty(this.ModeMap, ModeEnum.reference);
      }
    },
    methods: {
      handleAssetColumns(val, index) {
        // 判断是否填充默认值
        if (!this.isColFixed(this.defaultParamMap.sample) && this.isContenTypeRow(val)) {
          const _item = Object.assign({}, this.value[index], {
            sample: this.defaultValue(val),
          });
          this.$set(this.value, index, _item);
          this.$emit('input', this.value);
        }
      },
      isHidden(item) {
        return this.hiddenList.includes(item);
      },
      isColFixed(item) {
        return this.isFixed || this.fixedList.includes(item);
      },
      isDisable(item) {
        return this.disableList.includes(item);
      },
      /**
       * Content-Type 字段为只读模式
       */
      isContenTypeRow(row) {
        return row === 'Content-Type';
      },
      /**
       * 默认值
       * Content-Type列特殊处理
       */
      defaultValue(row) {
        return (this.isContenTypeRow(row) && this.contentType) || row;
      },
      onDelete(index) {
        const newData = this.value.filter((item, i) => i !== index);
        this.$emit('input', newData);
      },
      onAddRow() {
        const paramsObj = this.defaultParamMap;
        let newRowObj = {
          assetColumns: '',
          required: false,
          [paramsObj.dataType]: 'string',
          [paramsObj.description]: '',
          [paramsObj.sample]: '',
        };

        if (this.tableType === 'request') {
          newRowObj = {
            ...newRowObj,
            httpParamKind: this.paramsType,
            type: 'PARAMETERS',
            required: false,
          };
        } else if (this.tableType === 'response') {
          newRowObj = {
            ...newRowObj,
            type: this.paramsType,
          };
        }
        this.$emit('input', [...this.value, newRowObj]);
      },
      validate() {
        return new Promise((resolve, reject) => {
          this.$refs.formRef.validate(valid => {
            if (!valid) {
              reject(new Error(`${this.validateErrorPosName}参数名称不能为空`));
              return;
            } else {
              const tmp = this.value.map(i => i.assetColumns);
              const set = new Set(tmp);
              if (set.size !== tmp.length) {
                reject(new Error(`${this.validateErrorPosName}参数名称不能重复`));
                return;
              }
            }
            resolve();
          });
        });
      },
      modeChange(row) {
        this.$set(row, 'expression', '');
        this.$set(row, 'defaultValue', '');
        this.$set(row, 'fixedValue', '');
      },
      selectParams(row) {
        this.$emit('select-params', data => {
          this.$set(row, 'expression', data.id);
        });
      },
      selectMethod(row) {
        this.$emit('select-method', row.expression, data => {
          this.$set(row, 'expression', data);
        });
      },
    },
  };
</script>

<style lang="less" scoped>
  .api-params-tree-table {
    .map-value {
      flex: 1;
      position: relative;
      display: flex;
      align-items: center;

      & /deep/ .el-input__inner {
        padding-right: 24px;
      }

      .expression-setting {
        cursor: pointer;
        position: absolute;
        padding: 0px 3px;
        right: 3px;
        background: #fff;
      }
    }

    .expression-btn {
      min-width: 178px;
    }

    .select-params {
      width: 100%;
      border-radius: 4px;
      border: 1px solid #dcdfe6;
      text-align: left;
      height: 28px;
      padding: 0 15px;
    }

    .required {
      color: red;
      padding-right: 4px;
      vertical-align: middle;
    }

    /deep/.el-form-item--mini.el-form-item {
      margin: 10px 0px;
    }

    /deep/ .cell {
      display: flex;
      flex-direction: row;
      align-items: center;

      .params {
        width: 100%;
      }
      .is-error {
        height: 40px;
        display: flex;
        align-items: center;
        .el-form-item__content {
          width: 100%;
        }
      }
    }

    ::v-deep {
      .el-form-item {
        width: 100%;

        .el-form-item__content {
          width: 100%;
          font-size: 12px;
        }
      }
    }
  }
</style>
