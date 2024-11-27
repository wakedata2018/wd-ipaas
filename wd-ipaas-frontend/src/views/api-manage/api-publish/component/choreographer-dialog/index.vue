<template>
  <el-dialog
    :visible="visible"
    class="choreographer-dialog"
    width="580px"
    :close-on-click-modal="false"
    @close="handlerClose"
    @open="handleOpen"
  >
    <span slot="title">
      <div class="title">新增服务编排</div>
    </span>
    <div class="choreographer-content">
      <el-form ref="arrangeForm" :model="form" label-width="100px" :rules="rules" label-suffix=":">
        <el-form-item label="API名称" prop="apiName">
          <el-input v-model="form.apiName" class="w300" placeholder="请输入API名称"></el-input>
        </el-form-item>
        <el-form-item label="接口分类" prop="apiGroupId">
          <el-select v-model="form.apiGroupId" class="w300" placeholder="请选择接口分类">
            <el-option v-for="item of cateforyList" :key="item.id" :label="item.groupName" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="API Path" prop="apiPath">
          <el-input v-model="form.apiPath" class="w300" placeholder="请输入API路径">
            <template slot="prepend">
              <div v-if="publicPath" class="path-prepend">{{ publicPath }}/</div>
              <div v-else class="path-prepend-default"></div>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="请求协议" prop="protocol">
          <el-select v-model="form.protocol" class="w300" disabled>
            <el-option v-for="item in protocols" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="请求方式" prop="reqMethod">
          <el-select v-model="form.reqMethod" class="w300" placeholder="请选择请求方式">
            <el-option v-for="item in methods" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否公开" prop="secret">
          <el-select v-model="form.secret" class="w300" placeholder="请选择是否公开">
            <el-option
              v-for="item in SecretType._list"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="描述：" prop="apiDescription">
          <el-input
            v-model="form.apiDescription"
            class="w300 textarea"
            type="textarea"
            rows="8"
            show-word-limit
            maxlength="80"
            placeholder="请输入描述"
          ></el-input>
        </el-form-item>
      </el-form>
    </div>
    <span slot="footer">
      <div class="footer-bottom">
        <el-button @click="handlerClose">取消</el-button>
        <el-button type="primary" @click="handlerSave">确认</el-button>
      </div>
    </span>
  </el-dialog>
</template>

<script>
  import apiGroup from '@/api/api-group';
  import { ProtocolType, RequestMethod, SecretType } from '@/utils/enum/index';
  import apiControll from '@/api/api-controller';
  import Validator from '@/utils/validator.js';

  export default {
    name: 'ChoreographerDialog',
    props: {
      visible: {
        type: Boolean,
        default: false,
      },
      groupIdInfo: {
        type: [Number, String],
        default: null,
      },
    },
    data() {
      return {
        form: {
          apiName: '',
          apiGroupId: '',
          apiPath: '',
          protocol: 'HTTPS',
          apiDescription: '',
          reqMethod: '',
          secret: SecretType.PRIVATE,
        },
        cateforyList: [],
        protocols: ProtocolType._list,
        methods: RequestMethod._list,
        rules: {
          apiName: [{ required: true, trigger: 'blur', validator: Validator.nameValidator }],
          apiGroupId: [{ required: true, message: '请选择接口分类', trigger: ['blur'] }],
          apiPath: [{ required: true, message: '请输入API路径', trigger: 'blur' }],
          protocol: [{ required: true, message: '请选择请求协议', trigger: 'blur' }],
          reqMethod: [{ required: true, message: '请输入请求方式', trigger: ['blur'] }],
          secret: [{ required: true, message: '请选择是否公开', trigger: ['blur'] }],
        },
        SecretType,
      };
    },
    computed: {
      dataAssetApiMethod() {
        return this.publicPath + '/' + this.form.apiPath;
      },
      publicPath() {
        return this.cateforyList.find(o => o.id === this.form.apiGroupId)?.groupPath;
      },
    },
    mounted() {
      this.queryCateforyList();
    },
    methods: {
      handleOpen() {
        this.$nextTick(() => {
          this.$refs.arrangeForm.resetFields();
          if (this.groupIdInfo) {
            this.form.apiGroupId = this.groupIdInfo === -1 ? null : this.groupIdInfo;
            if (!this.form.apiGroupId) {
              this.form.apiGroupId = this.cateforyList[0]?.id ?? null;
            }
          }
        });
      },
      queryCateforyList() {
        apiGroup.getThemeList().then(res => {
          this.cateforyList = res.data;
        });
      },
      handlerClose() {
        this.$emit('update:visible', false);
      },
      handlerSave() {
        this.$refs.arrangeForm.validate(valid => {
          if (valid) {
            this.save();
          } else {
            this.$message.error('请检查必填项');
          }
        });
      },
      save() {
        const start = {
          clazzName: 'com.wakedata.dw.open.model.api.flow.operator.VertexOperator',
          operatorId: 'start',
          name: 'start',
          desc: '开始算子',
          outputOperators: [],
        };

        const end = {
          clazzName: 'com.wakedata.dw.open.model.api.flow.operator.EndOperator',
          operatorId: 'end',
          name: 'end',
          desc: '结束算子',
          outputOperators: [],
        };

        const locationJson = {
          nodes: [
            {
              x: 371,
              y: 440,
              outPoints: [
                [0, 0.5],
                [0.5, 0],
                [1, 0.5],
                [0.5, 1],
              ],
              forbidInCombo: true, // 禁止移入 combo
              uniqueName: 'start',
              label: '开始算子',
              title: '开始算子',
              id: 'start',
              component: null,
            },
            {
              x: 900,
              y: 440,
              inPoints: [
                [0, 0.5],
                [0.5, 0],
                [1, 0.5],
                [0.5, 1],
              ],
              forbidInCombo: true, // 禁止移入 combo
              uniqueName: 'end',
              label: '结束算子',
              title: '结束算子',
              id: 'end',
              component: null,
            },
          ],
          edges: [],
          combos: [],
          count: 0,
        };
        const params = {
          dataAssetApi: {
            ...this.form,
            dataAssetApiMethod: this.dataAssetApiMethod,
            apiType: 'LITE_FLOW',
            responseContentType: 'JSON',
            apiAttr: {
              clazzName: 'com.wakedata.dw.open.model.api.flow.ApiFlowAttr',
              locationJson: JSON.stringify(locationJson),
              operators: {
                start,
                end,
              },
            },
          },
          results: [],
          resutRespParamDTOS: [],
          parameters: [],
        };
        delete params.dataAssetApi.publicPath;
        delete params.dataAssetApi.apiPath;

        apiControll.addLiteflow(params).then(({ success, data }) => {
          if (success && !!data) {
            this.$emit('update:visible', false);
            this.$emit('enter-choreography', data);
          }
        });
      },
    },
  };
</script>

<style lang="less" scoped>
  .choreographer-dialog {
    .title {
      padding-left: 10px;
      font-weight: 700;
      border-left: 4px solid #2776fb;
    }
    /deep/ .el-dialog__body {
      padding: 15px 20px;
    }
    .choreographer-content {
      margin-left: 30px;
      margin-right: 30px;
      width: calc(100% - 60px);
    }
    .w300 {
      width: 350px;
    }
    .path-prepend {
      width: auto;
    }

    .path-prepend-default {
      width: 50px;
    }
    /deep/ .el-input-group__prepend {
      padding: 0 10px;
    }
    .footer-bottom {
      margin-left: auto;
      margin-right: auto;
      display: flex;
      justify-content: space-around;
      width: 200px;
      height: 30px;
      text-align: center;
      .el-button {
        width: 70px;
      }
    }
  }
</style>
