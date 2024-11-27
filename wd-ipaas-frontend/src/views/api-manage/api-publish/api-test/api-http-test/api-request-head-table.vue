<template>
  <el-form ref="formRef" :model="form" class="head-form">
    <el-table :data="value" style="width: 100%" class="dss-table bd-table">
      <el-table-column prop="assetColumns" label="参数名称">
        <template #default="scope">
          {{ scope.row.assetColumns }}
        </template>
        <template #header> <tips-icon :content="$t('validator.httpFieldNameDesc')"></tips-icon>参数名称</template>
      </el-table-column>
      <el-table-column prop="assetDatatype" label="参数类型">
        <template #default="scope">
          {{ scope.row.assetDatatype }}
        </template>
      </el-table-column>
      <el-table-column prop="required" label="必填" width="100">
        <template #default="scope">
          <el-checkbox v-model="scope.row.required" disabled> </el-checkbox>
        </template>
      </el-table-column>
      <el-table-column prop="mockRule" label="mock类型" min-width="90">
        <template #default="scope">
          <div class="mock">
            <el-select
              v-model="scope.row.mockRule"
              :disabled="disabledCol(scope.row.assetColumns)"
              placeholder="不启用"
              clearable
              filterable
            >
              <el-option-group v-for="i in filterMockType(scope.row.assetDatatype)" :key="i.label" :label="i.label">
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
            <!-- 扩展先屏蔽 -->
            <!-- <i v-if="!disabledCol(scope.row.assetColumns)" class="el-icon-setting mock-setting" @click="onClickMockSetting(scope)"></i> -->
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="sample" label="参数值" min-width="120">
        <template #default="scope">
          <el-form-item
            style="width: 100%"
            :prop="'list.' + scope.$index + '.sample'"
            :rules="{
              required: scope.row.required,
              message: '请输入值',
              trigger: 'blur',
            }"
          >
            <el-input
              v-model="scope.row.sample"
              maxlength="100"
              style="width: 100%"
              :disabled="isContenTypeRow(scope.row.assetColumns)"
            >
            </el-input>
          </el-form-item>
        </template>
      </el-table-column>

      <el-table-column prop="descriptions" label="描述" min-width="180">
        <template #default="scope">
          <span class="description" :title="scope.row.descriptions">{{ scope.row.descriptions }}</span>
        </template>
      </el-table-column>
    </el-table>
    <mock-dialog
      v-if="mockDialogShow"
      v-model="currentRow.rule"
      :visible.sync="mockDialogShow"
      @save="onSaveMockRule"
    ></mock-dialog>
  </el-form>
</template>
<script>
  import TipsIcon from '@/components/tips-icon';
  import MockDialog from './mock-dialog.vue';

  import Validator from '@/utils/validator.js';
  import { TABLE_TYPE } from '../constants';
  import { filterMockType } from '@/utils/enum/mock-common-rule';

  export default {
    name: 'ApiRequestHeadTable',
    components: { TipsIcon, MockDialog },
    props: {
      /**
       * 展示类型： 请求/响应头 | QUERRY请求参数
       * HEAD | QUERY
       */
      type: {
        type: String,
        default: TABLE_TYPE.HEAD,
      },
      /**
       * 数据源
       */
      value: {
        type: Array,
        default: () => [],
      },
      disableMockList: {
        type: Array,
        default: () => [],
      },
    },
    data() {
      return {
        form: {
          list: [],
        },
        assetColumnsRule: [{ required: true, validator: Validator.httpFieldEnNameValidator, trigger: 'blur' }],
        filterMockType,
        mockDialogShow: false,
        currentRow: {
          index: null,
          rule: '',
        },
      };
    },
    computed: {
      isQuery() {
        return this.type === TABLE_TYPE.QUERY;
      },
      disabledListName() {
        return ['__enable_log__', ...this.disableMockList];
      },
    },
    watch: {
      value: {
        deep: true,
        immediate: true,
        handler(val) {
          this.form.list = val;
        },
      },
    },
    methods: {
      /**
       * Content-Type 字段为只读模式
       */
      isContenTypeRow(name) {
        return name === 'Content-Type';
      },
      /**
       * 字段为只读模式
       */
      disabledCol(name) {
        return this.disabledListName.includes(name);
      },
      async validate() {
        try {
          await this.$refs.formRef.validate();
          return true;
        } catch (error) {
          this.$message.error(`${this.isQuery ? 'query参数' : '请求头'}填写错误`);
          return false;
        }
      },
      onSaveMockRule(val) {
        this.$set(this.form.list[this.currentRow.index], 'mockRule', val);
      },
      onClickMockSetting(scope) {
        this.mockDialogShow = true;
        this.currentRow = {
          index: scope.$index,
          rule: scope.row.mockRule,
        };
      },
    },
  };
</script>

<style lang="less" scoped>
  .head-form {
    .el-form-item--mini.el-form-item {
      margin: 14px 0px;
    }

    /deep/.el-table--mini .el-table__cell {
      font-size: 14px;
    }

    /deep/ .cell {
      display: flex;
      flex-direction: row;
      align-items: center;
    }

    .btn-box {
      margin-top: 10px;
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
    .mock {
      &-setting {
        padding: 5px;
        cursor: pointer;
      }
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
