<template>
  <el-dialog
    :visible.sync="isShow"
    title="发送事件"
    :close-on-click-modal="false"
    class="bd-dialog scrollbar-dialog event-send"
    custom-class="anim-left"
    fullscreen
    append-to-body
  >
    <el-scrollbar class="page-scrollbar">
      <el-form ref="form" :model="form" :rules="rules" label-position="right" label-width="auto" :disabled="isReadonly">
        <el-row :gutter="30">
          <el-col :span="12">
            <el-form-item label="步骤名称" prop="name">
              <template slot="label">
                <tips-icon content="英文字母开头，仅包含数字、字母和下划线"></tips-icon>
                步骤名称:
              </template>
              <el-input
                v-model.trim="form.name"
                placeholder="输入下划线、英文字母、数字，不超过30个字"
                maxlength="30"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="步骤描述" prop="desc">
              <el-input v-model.trim="form.desc" maxlength="50" placeholder="不超过50个字符"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="服务端类型" prop="mqType">
          <el-radio-group v-model="form.mqType">
            <el-radio v-for="item in SERVICE_TYPE" :key="item.value" :label="item.value">{{ item.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="endpoint" prop="clusterAddress">
          <el-input v-model.trim="form.clusterAddress" maxlength="200" :placeholder="lengPlaceHolder"></el-input>
        </el-form-item>
        <el-form-item label="accessKey" prop="accessKey">
          <el-input v-model.trim="form.accessKey" maxlength="200" :placeholder="lengPlaceHolder"></el-input>
        </el-form-item>
        <el-form-item label="secretKey" prop="secretKey">
          <el-input v-model.trim="form.secretKey" maxlength="200" :placeholder="lengPlaceHolder"></el-input>
        </el-form-item>
        <el-form-item label="topic" prop="topic">
          <el-input v-model.trim="form.topic" maxlength="200" :placeholder="lengPlaceHolder"></el-input>
        </el-form-item>
        <el-form-item label="message" required> 发送消息时会将参数进行序列化操作 </el-form-item>
        <el-form-item label-width="0px" prop="messageTemplate">
          <message-tree
            :tree="tree"
            tree-type="response"
            mode="flowResponse"
            :select-params="selectParams"
            :select-method="selectMethod"
            :is-show-sample="false"
            :is-readonly="isReadonly"
          />
        </el-form-item>
      </el-form>
    </el-scrollbar>
    <div v-if="!isReadonly" slot="footer" class="bd-dialog-footer">
      <el-button class="dss-btn-circle" @click="isShow = false">取消</el-button>
      <el-button class="dss-btn-circle" type="primary" @click="onSave">确定</el-button>
    </div>
    <SelectParametersDialog
      :visible.sync="showDialog"
      :task-info="taskInfo"
      :operator-id="operatorId"
      @selected="selected"
    >
    </SelectParametersDialog>
    <SelectMethodDialog
      :visible.sync="showMethodDialog"
      :expression="expression"
      :task-info="taskInfo"
      :operator-id="operatorId"
      @selected="selectedMethod"
    ></SelectMethodDialog>
  </el-dialog>
</template>
<script>
  import cloneDeep from 'lodash/cloneDeep';
  import { BodyTree as MessageTree } from '@/components/api-edit';
  import SelectParametersDialog from '@/components/select-parameter-dialog';
  import SelectMethodDialog from '@/components/select-method-dialog';
  import { SERVICE_TYPE } from '@/utils/enum';
  import textUtils from '@/utils/text-utils';
  import eventBus, { EventName } from '@/components/g6-editor/event-bus';
  import TipsIcon from '@/components/tips-icon';
  import Validator from '@/utils/validator.js';
  import { getUniqueNameFromItem, getIdFromItem } from '../utils';

  function getInitForm(name = 'event_send', desc = '发送事件') {
    return {
      name,
      desc,
      mqType: SERVICE_TYPE[0].value,
      clusterAddress: '',
      accessKey: '',
      secretKey: '',
      topic: '',
      messageTemplate: '',
    };
  }

  function validateResult(rule, value, callback) {
    const res = textUtils.hasEmptyOrMultiName(this.tree?.root?.properties);
    if (!res.success) {
      this.$message.error('参数名称' + res.message);
    } else if (!value) {
      callback(new Error('请配置发送的消息内容'));
    } else {
      callback();
    }
  }

  export default {
    components: {
      TipsIcon,
      MessageTree,
      SelectParametersDialog,
      SelectMethodDialog,
    },
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
      taskInfo: {
        type: Object,
        default: null,
      },
      isReadonly: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      const lengPlaceHolder = '不超过200个字符';
      return {
        showDialog: false,
        showMethodDialog: false,
        form: getInitForm(),
        tree: null,
        expression: null,
        callback: null,
        methodCallback: null,
        SERVICE_TYPE,
        lengPlaceHolder,
        rules: {
          name: [{ required: true, trigger: 'blur', validator: Validator.enNameValidator }],
          desc: [{ required: true, message: '步骤描述不能为空', trigger: 'blur' }],
          mqType: [{ required: true, message: '服务类型不能为空', trigger: 'blur' }],
          clusterAddress: [{ required: true, message: '服务端调用地址不能为空', trigger: 'blur' }],
          topic: [{ required: true, message: '请求服务端所需的topic不能为空', trigger: 'blur' }],
          messageTemplate: [{ required: true, trigger: 'blur', validator: validateResult }],
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
          if (val && Object.keys(val?.root?.properties).length) {
            this.form.messageTemplate = JSON.stringify(val);
          } else {
            this.form.messageTemplate = null;
          }
        },
      },
    },
    methods: {
      initForm() {
        this.$refs.form.resetFields();
        const { name, desc } = this.config;
        this.form = {
          ...getInitForm(name, desc),
          ...this.config.dataAssetApi.apiAttr,
        };
        if (!this.form?.messageTemplate) {
          this.tree = {
            root: {
              type: 'object',
              name: 'root',
              description: '根层级',
              properties: {},
            },
          };
        } else {
          this.tree = JSON.parse(this.form.messageTemplate);
        }
      },
      validate() {
        if (!this.curNode || this.curNode.destroyed) {
          throw new Error('该节点不存在，无法保存，请重新选择节点。');
        }

        if (this.form.name === 'globalParam') {
          throw new Error('globalParam不能作为步骤名称。');
        }

        const operators = this.taskInfo.dataAssetApi.apiAttr.operators;
        const isFind = Object.keys(operators).some(key => {
          return operators[key].name === this.form.name && key !== this.operatorId;
        });
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
          const { name, desc, ...other } = this.form;
          this.$emit(
            'save',
            { name, desc, dataAssetApi: { apiAttr: Object.assign({}, this.config.dataAssetApi.apiAttr, other) } },
            cloneDeep(this.curNode.getModel())
          );
          this.isShow = false;
        } catch (error) {
          this.$message.error(error.message);
        }
      },
      selectParams(callback) {
        this.showDialog = true;
        if (typeof callback === 'function') {
          this.callback = callback;
        }
      },
      selected({ nodeId, data }) {
        this.callback?.({ operatorId: nodeId, id: data });
      },
      selectMethod(expression, callback) {
        this.showMethodDialog = true;
        this.expression = expression;
        if (typeof callback === 'function') {
          this.methodCallback = callback;
        }
      },
      selectedMethod(expression) {
        this.expression = null;
        this.methodCallback?.(expression);
      },
    },
  };
</script>

<style scoped lang="less">
  .event-send {
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
      }
      .el-scrollbar__view {
        padding: 17px 0px;
      }
    }
  }
</style>
