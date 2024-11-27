<template>
  <div>
    <el-table :data="parameters" style="width: 100%" class="dss-table bd-table">
      <el-table-column prop="assetColumns" label="参数名称">
        <template slot-scope="scope">
          <el-form-item :prop="'list.' + scope.$index + '.assetColumns'">
            <el-input
              v-model="scope.row.assetColumns"
              style="width: 100%"
              maxlength="50"
              :disabled="isReadonly"
            ></el-input>
          </el-form-item>
        </template>
        <template #header>
          <tips-icon :content="$t('validator.httpFieldNameDesc')"></tips-icon>参数名称
        </template>
      </el-table-column>
      <!-- <el-table-column prop="httpParamKind" label="参数位置">
        <template slot-scope="scope">
          <el-form-item :prop="'list.' + scope.$index + '.httpParamKind'">
            <el-select v-model="scope.row.httpParamKind" :disabled="isReadonly">
              <el-option v-for="item in positionList" :key="item" :label="item" :value="item"></el-option>
            </el-select>
          </el-form-item>
        </template>
      </el-table-column> -->
      <el-table-column prop="assetDatatype" label="参数类型">
        <template slot-scope="scope">
          <el-form-item :prop="'list.' + scope.$index + '.assetDatatype'">
            <el-select v-model="scope.row.assetDatatype" :disabled="isReadonly">
              <el-option v-for="item in typeList" :key="item" :label="item" :value="item"></el-option>
            </el-select>
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column prop="required" label="是否必填">
        <template slot-scope="scope">
          <el-form-item :prop="'list.' + scope.$index + '.required'">
            <el-checkbox v-model="scope.row.required" :disabled="isReadonly"></el-checkbox>
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column prop="sample" label="默认值">
        <template slot-scope="scope">
          <el-form-item :prop="'list.' + scope.$index + '.sample'">
            <el-input v-model="scope.row.sample" maxlength="100" style="width: 100%" :disabled="isReadonly"></el-input>
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column prop="descriptions" label="描述">
        <template slot-scope="scope">
          <el-form-item :prop="'list.' + scope.$index + '.descriptions'">
            <el-input
              v-model="scope.row.descriptions"
              :maxlength="scope.row.httpParamKind!=='BODY'?256:''"
              style="width: 100%"
              :disabled="isReadonly"
            ></el-input>
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column label="操作" v-if="!isReadonly">
        <template slot-scope="scope">
          <el-button @click="onDelete(scope.$index, scope.row)" class="bd-button bd-table-danger">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="btn-box" v-if="!isReadonly">
      <el-button type="primary" size="mini" class="bd-button bd-strong" @click="onAddRow">+新增参数</el-button>
    </div>
  </div>
</template>
<script>
import Validator from "@/utils/validator.js";
import TipsIcon from '@/components/tips-icon';

export default {
  components: { TipsIcon },
  props: {
    parameters: {
      type: Array,
      default: () => {
        return [];
      },
    },
    isReadonly: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      positionList: ['QUERY', 'HEAD', 'BODY'],
      typeList: ['string', 'number', 'json'],
    };
  },
  computed: {
    form() {
      return {
        list: this.parameters,
      };
    },
  },
  methods: {
    onDelete(index, info) {
      // if (this.parameters.length > 0) {
      //   this.parameters.splice(index, 1);
      // }
      const parameters = this.parameters;
      parameters.length && parameters.splice(index, 1);
      this.$emit('update:parameters', parameters);
    },
    onAddRow() {
      const parameters = this.parameters;
      parameters.push({
        httpParamKind: 'QUERY',
        assetDatatype: 'string',
        type: 'PARAMETERS',
      });
      this.$emit('update:parameters', parameters);
    },
    validateParams() {
      let index = -1;
      let repeat = false;
      const fieldArr = [];
      const parameters = this.parameters;
      for (let i = 0; i < parameters.length > 0; i++) {
        const item = parameters[i];
        if (
          !item.assetColumns ||
          !Validator.httpFieldEnNameValidatorFun(item.assetColumns).result ||
          !item.httpParamKind ||
          !item.assetDatatype
        ) {
          index = i;
          break;
        }
        if (fieldArr.indexOf(item.assetColumns) !== -1) {
          repeat = true;
          break;
        }
        fieldArr.push(item.assetColumns);
      }

      const found = index !== -1 || repeat;
      return !found;
    },
    validate() {
      const notFound = this.validateParams();
      return notFound;
    },
    resetTable() {
      this.$emit('clearAll');
    },
  },
};
</script>

<style lang="less" scoped>
.api-http {
  .main-title {
    height: 22px;
    font-size: 16px;
    margin: 10px 10px;
    font-weight: 600;
    color: rgba(51, 51, 51, 1);
    line-height: 22px;
  }
  .btn-box {
    margin-top: 10px;
  }
  /deep/.el-form-item--mini.el-form-item {
    margin: 10px 0px;
  }
  /deep/ .el-select > .el-input {
    width: 130px !important;
  }
}
</style>
