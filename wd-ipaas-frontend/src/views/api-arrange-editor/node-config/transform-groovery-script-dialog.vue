<template>
  <el-dialog
    :visible.sync="isShow"
    title="Groovy Script"
    class="bd-dialog groovy"
    top="60px"
    fullscreen
    custom-class="anim-left"
    :close-on-click-modal="false"
  >
    <el-scrollbar class="page-scrollbar">
      <el-form ref="form" :model="form" :rules="rules" label-position="right" label-width="auto" @submit.native.prevent>
        <el-row type="flex" justify="space-between">
          <el-col :span="11">
            <el-form-item label="步骤名称" prop="name">
              <template slot="label">
                <tips-icon content="英文字母开头，仅包含数字、字母和下划线"></tips-icon>
                步骤名称:
              </template>
              <el-input v-model.trim="form.name" size="mini" maxlength="30" :disabled="isReadonly"> </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="11">
            <el-form-item prop="desc" label="步骤描述">
              <el-input v-model.trim="form.desc" size="mini" maxlength="50" :disabled="isReadonly"> </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item prop="resultData" label="返回值" class="block">
          <response-body :tree="tree" tree-type="response" class="result-params" :is-readonly="isReadonly" />
        </el-form-item>
        <el-form-item prop="groovy" style="width: 100%" label="code" class="block">
          <CodemirrorEditor
            ref="codeMirrorEditor"
            prop="groovy"
            :refresh="refresh"
            :config="form"
            :is-readonly="isReadonly"
            class="code"
            @change-setting-value="changeApiSql"
          />
        </el-form-item>
      </el-form>
    </el-scrollbar>
    <div v-if="!isReadonly" slot="footer" class="bd-dialog-footer">
      <el-button class="dss-btn-circle" @click="isShow = false">取消</el-button>
      <el-button class="dss-btn-circle" type="primary" @click="onSave">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import cloneDeep from 'lodash/cloneDeep';
  import eventBus, { EventName } from '@/components/g6-editor/event-bus';
  import Validator from '@/utils/validator.js';
  import { getInitForm } from '../node-convert/transform-groovery-script';
  import { BodyTree as ResponseBody } from '@/components/api-edit/index.js';
  import textUtils from '@/utils/text-utils';
  import TipsIcon from '@/components/tips-icon';
  import CodemirrorEditor from '@/bz-components/codemirror-editor';
  import { getUniqueNameFromItem, getIdFromItem } from '../utils';

  function validateResult(rule, value, callback) {
    const res = textUtils.hasEmptyOrMultiName(this.tree?.root?.properties);
    if (!res.success) {
      this.$message.error('参数名称' + res.message);
    } else if (!value) {
      callback(new Error('返回值配置为空'));
    } else {
      callback();
    }
  }

  export default {
    components: { ResponseBody, CodemirrorEditor, TipsIcon },
    props: {
      visible: {
        type: Boolean,
        default: false,
      },
      config: {
        type: Object,
        default: null,
      },
      curNode: {
        type: Object,
        default: null,
      },
      isReadonly: {
        type: Boolean,
        default: false,
      },
      taskInfo: {
        type: Object,
        default: null,
      },
    },
    data() {
      return {
        form: getInitForm(),
        refresh: false,
        tree: null,
        rules: {
          name: [{ required: true, trigger: 'blur', validator: Validator.enNameValidator }],
          desc: [{ required: true, message: '步骤描述不能为空', trigger: 'blur' }],
          resultData: [{ required: true, trigger: 'change', validator: validateResult }],
          groovy: [{ required: true, message: '请输入代码', trigger: 'blur' }],
        },
      };
    },
    computed: {
      uniqueName() {
        return getUniqueNameFromItem(this.curNode);
      },
      operatorId() {
        return getIdFromItem(this.curNode);
      },
      isShow: {
        get() {
          return this.visible;
        },
        set(val) {
          if (!val) {
            this.$emit('cancel', this.uniqueName);
          }
          this.$emit('update:visible', val);
        },
      },
    },
    watch: {
      isShow(val) {
        this.$nextTick(() => {
          if (val) {
            this.initForm();
          }
        });
      },
      tree: {
        deep: true,
        handler(val) {
          this.$nextTick(() => {
            this.$refs.form.clearValidate('resultData');
          });

          if (val && Object.keys(val?.root?.properties).length) {
            this.form.resultData = JSON.stringify(val);
          } else {
            this.form.resultData = null;
          }
        },
      },
    },
    methods: {
      initForm() {
        this.$refs.form.resetFields();
        this.refresh = !this.refresh;
        this.form = Object.assign({}, getInitForm(), this.config);
        if (!this.form?.resultData) {
          this.tree = {
            root: {
              type: 'object',
              name: 'root',
              description: '根层级',
              properties: {},
            },
          };
        } else {
          this.tree = JSON.parse(this.form.resultData);
        }
      },
      validate() {
        if (!this.curNode || this.curNode.destroyed) {
          throw new Error('该节点不存在，无法保存，请重新选择节点。');
        }

        const operators = this.taskInfo.dataAssetApi.apiAttr.operators;
        const isFind = Object.keys(operators).some(key => {
          return operators[key].name === this.form.name && key !== this.operatorId;
        });

        if (this.form.name === 'globalParam') {
          throw new Error('globalParam不能作为步骤名称。');
        }

        if (isFind) {
          throw new Error('步骤名称不能重复，请重新设置。');
        }

        if (this.tree?.root?.properties) {
          const res = textUtils.hasEmptyOrMultiName(this.tree.root.properties);
          if (!res.success) {
            throw new Error('参数名称' + res.message);
          }
        }
      },

      onSave() {
        this.$refs.form.validate(isValidate => {
          if (!isValidate) {
            this.$message.error(this.$t('validator.emptyWarning'));
            return;
          }
          this.save();
        });
      },
      save() {
        try {
          this.validate();
          this.$emit('save', this.form, cloneDeep(this.curNode.getModel()));
          this.isShow = false;
        } catch (error) {
          this.$message.error(error.message);
        }
      },
      changeApiSql() {
        this.$refs.form.clearValidate('groovy');
      },
    },
  };
</script>

<style scoped lang="less">
  .groovy {
    ::v-deep .page-scrollbar {
      box-sizing: border-box;
      overflow-x: hidden;
      overflow-y: hidden;
      margin-bottom: 10px;
      .el-scrollbar__bar.is-horizontal {
        display: none;
      }
      .el-scrollbar__wrap {
        overflow-x: hidden;
        max-height: calc(100vh - 210px);

        .el-scrollbar__view {
          padding: 17px 0px;
        }
      }
    }
    ::v-deep .el-dialog__footer {
      position: absolute;
      left: 0;
      bottom: 0;
      width: 100%;
      height: 70px;
    }

    ::v-deep .el-form {
      .block.el-form-item {
        display: flex;
        flex-direction: column;
        .el-form-item__label-wrap {
          margin-left: 0px !important;
        }
        > .el-form-item__content {
          margin-left: 0px !important;
          > .el-form-item__error {
            padding-left: 30px;
          }
        }
      }
    }
    .result-params {
      padding: 10px 0;
    }
  }
</style>
