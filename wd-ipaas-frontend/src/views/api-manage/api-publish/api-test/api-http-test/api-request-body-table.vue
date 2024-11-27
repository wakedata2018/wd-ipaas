<template>
  <div class="api-body-table">
    <el-radio-group v-model="bodyType" class="api-type">
      <el-radio :label="BODY_TYPE.DEFAULT">默认</el-radio>
      <el-radio :label="BODY_TYPE.JSON">JSON</el-radio>
      <el-radio :label="BODY_TYPE.XML">XML</el-radio>
    </el-radio-group>
    <el-form ref="formRef" :model="form">
      <div v-if="bodyType === BODY_TYPE.DEFAULT" class="body-form">
        <el-row class="table-head" :gutter="10">
          <el-col :span="5" class="table-head__item"
            ><tips-icon :content="$t('validator.httpFieldNameDesc')"></tips-icon>参数名称</el-col
          >
          <el-col :span="3" class="table-head__item">参数类型</el-col>
          <el-col :span="2" class="table-head__item">必填</el-col>
          <el-col :span="3" class="table-head__item">mock类型</el-col>
          <el-col :span="4" class="table-head__item">参数值</el-col>
          <el-col :span="6" class="table-head__item">描述</el-col>
        </el-row>
        <JsonSchemaTest v-if="form.tree" :value="form.tree" />
      </div>

      <div v-else-if="bodyType === BODY_TYPE.JSON" class="monaco">
        <MonacoEditor v-model="form.jsonStr" style="height: 100%" language="json" :options="options" />
      </div>

      <div v-else class="monaco">
        <MonacoEditor v-model="form.xmlStr" style="height: 100%" language="xml" :options="options" />
      </div>
    </el-form>
  </div>
</template>
<script>
  import MonacoEditor from '@/components/monaco-editor';
  import JsonSchemaTest from './json-schema-test';
  import { BODY_TYPE } from '../constants';
  import TipsIcon from '@/components/tips-icon';
  import { schema2json } from '@/utils/schema2json';
  import vkbeautify from 'vkbeautify';
  import { XML } from '@/utils/xmljson';

  export default {
    name: 'ApiRequestBodyTable',
    components: { JsonSchemaTest, MonacoEditor, TipsIcon },
    props: {
      /**
       * 请求体参数
       */
      bodyData: {
        type: Object,
        default: () => ({}),
      },
      /**
       * 单选框类型 'default' | 'json' | 'xml'
       */
      requestType: {
        type: String,
        default: '',
      },
    },

    data() {
      return {
        BODY_TYPE,
        form: {},
        options: {
          validate: true,
        },
      };
    },
    computed: {
      bodyType: {
        get() {
          return this.requestType;
        },
        set(val) {
          if (val !== BODY_TYPE.DEFAULT) {
            this.updateData(val);
            this.$emit('change-type', val);
          } else {
            this.$emit('change-type', val);
          }
        },
      },
    },
    watch: {
      bodyData: {
        immediate: true,
        handler(val) {
          this.form = val;
        },
      },
      bodyType: {
        handler(val) {
          this.updateData(val);
        },
      },
    },
    methods: {
      updateData(val) {
        const jsonVal = this.form.tree.root.type === 'object' ? this.form.tree.root.properties : this.form.tree.root;
        const json = schema2json(jsonVal);
        const xotree = new XML.ObjTree();
        if (val === BODY_TYPE.JSON) {
          this.$set(this.bodyData, 'jsonStr', vkbeautify.json(JSON.stringify(json)));
        } else if (val === BODY_TYPE.XML) {
          this.$set(this.bodyData, 'xmlStr', vkbeautify.xml(xotree.writeXML(json)));
        }
      },
      async validate() {
        try {
          await this.$refs.formRef.validate();
          return true;
        } catch (error) {
          this.$message.error(`请求体格式错误`);
          return false;
        }
      },
    },
  };
</script>

<style lang="less" scoped>
  .api-body-table {
    .api-type {
      margin-bottom: 10px;
    }
  }

  .body-form {
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

    /deep/.el-form-item--mini.el-form-item {
      margin: 8px 0px;
    }

    /deep/ .cell {
      display: flex;
      flex-direction: row;
      align-items: center;
    }
  }

  .monaco {
    height: 240px;
    border: 1px solid lightgrey;
    padding: 2px 0;
    border-radius: 4px;
  }
</style>
