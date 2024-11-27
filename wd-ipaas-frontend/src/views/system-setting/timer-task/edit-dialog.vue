<template>
  <el-dialog
    class="bd-dialog edit-dialog"
    width="100%"
    top="60px"
    fullscreen
    custom-class="anim-left"
    :visible.sync="dialogVisible"
    append-to-body
    :close-on-click-modal="false"
    :title="isEdit ? `编辑-${data.taskName}` : '新建定时任务'"
  >
    <el-scrollbar class="edit-dialog__scrollbar">
      <el-form ref="form" :model="form" :rules="rules" label-width="110px" :validate-on-rule-change="false">
        <el-form-item label="任务名称" prop="taskName">
          <el-input
            v-model="form.taskName"
            class="edit-dialog__input"
            placeholder="不超过100字符"
            maxlength="100"
            show-word-limit
          ></el-input>
        </el-form-item>
        <el-form-item label="是否启用" prop="taskType">
          <el-switch
            v-model="form.taskType"
            :active-value="TASK_STATUS.ON"
            :inactive-value="TASK_STATUS.OFF"
          ></el-switch>
        </el-form-item>
        <div>
          <el-form-item label="选择接口" prop="dataAssetApiId">
            <el-select
              v-model="form.apiGroupId"
              placeholder="请选择分组"
              @change="
                form.dataAssetApiId = '';
                apiGroupChange = true;
              "
            >
              <el-option
                v-for="item in apiGroupList"
                :key="item.id"
                :label="item.groupName"
                :value="item.id"
              ></el-option>
            </el-select>
            <el-select
              v-model="form.dataAssetApiId"
              class="edit-dialog__select"
              reserve-keyword
              placeholder="请输入关键词"
              filterable
              :filter-method="filterApiList"
              clearable
            >
              <el-option
                v-for="item in apiList"
                :key="item.dataAssetApiId"
                :label="item.apiName"
                :value="item.dataAssetApiId"
              ></el-option>
            </el-select>
          </el-form-item>
        </div>
        <div>
          <el-form-item label="执行应用" prop="dataAssetAppId">
            <el-select v-model="form.dataAssetAppId" placeholder="请选择应用">
              <el-option
                v-for="item in appOptions"
                :key="item.dataAccessAppId"
                :label="item.dataAccessAppName"
                :value="item.dataAccessAppId"
              ></el-option>
            </el-select>
          </el-form-item>
        </div>
        <el-form-item label="执行规则" prop="taskCron">
          <el-input v-model="form.taskCron" class="edit-dialog__input" placeholder="请输入Cron表达式"></el-input>
        </el-form-item>
        <el-form-item label="执行时间">
          <el-radio-group v-model="form.taskExecuteType">
            <el-radio :label="TASK_EXECUTE_TYPE.FOREVER">永久</el-radio>
            <el-radio :label="TASK_EXECUTE_TYPE.CUSTOM">自定义</el-radio>
          </el-radio-group>
          <el-date-picker
            v-model="datePickerValue"
            class="edit-dialog__picker"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            :disabled="form.taskExecuteType === TASK_EXECUTE_TYPE.FOREVER"
            format="yyyy-MM-dd HH:mm"
            value-format="yyyy-MM-dd HH:mm"
            placement="bottom-start"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="任务描述">
          <el-input
            v-model="form.taskDesc"
            class="edit-dialog__textarea"
            type="textarea"
            :rows="5"
            placeholder="限制输入1000字符"
            maxlength="1000"
            show-word-limit
          ></el-input>
        </el-form-item>
      </el-form>
      <div class="edit-dialog__title">请求参数</div>
      <el-tabs v-model="activePane" v-loading="loading">
        <el-tab-pane label="请求头" :name="TAB_PANE_TYPE.HEAD">
          <api-params-table
            ref="headForm"
            v-model="form.apiHeadParams"
            params-type="HEAD"
            :params-map="paramsMapConfig"
            mode="preview"
            is-timer-task
            @select-method="onSelectMethodShow"
          ></api-params-table>
        </el-tab-pane>
        <el-tab-pane label="QUERY参数" :name="TAB_PANE_TYPE.QUERY">
          <api-params-table
            ref="queryForm"
            v-model="form.apiQueryParams"
            params-type="QUERY"
            is-timer-task
            mode="preview"
            @select-method="onSelectMethodShow"
          ></api-params-table>
        </el-tab-pane>
        <el-tab-pane v-if="isPostReqMethed" label="请求体" :name="TAB_PANE_TYPE.BODY">
          <request-body :tree="tree" mode="timerTask" :select-method="onSelectMethodShow"> </request-body>
        </el-tab-pane>
      </el-tabs>
    </el-scrollbar>
    <div class="bd-dialog-footer">
      <el-button class="dss-btn-circle" @click="handleClose(false)">取消</el-button>
      <el-button class="dss-btn-circle" type="primary" @click="onSave">确定</el-button>
    </div>
    <select-method-dialog
      :visible.sync="showMethodDialog"
      :expression="expression"
      :show-param-list="false"
      @selected="onSelectedMethod"
    ></select-method-dialog>
  </el-dialog>
</template>

<script>
  import { debounce } from 'throttle-debounce';

  import apiGroup from '@/api/api-group.js';
  import apiController from '@/api/api-controller.js';
  import apiTask from '@/api/api-timer-task';
  import { ApiParamsTable, BodyTree as RequestBody } from '@/components/api-edit/index.js';
  import SelectMethodDialog from '@/components/select-method-dialog';
  import { RequestMethod } from '@/utils/enum/index';

  import { TASK_STATUS, TASK_EXECUTE_TYPE, dateTimeToDate } from './common';
  import applicationApi from '@/api/api-application.js';

  const TAB_PANE_TYPE = {
    QUERY: 'query',
    HEAD: 'head',
    BODY: 'body',
  };

  /**
   * api全部分组默认id
   */
  const API_GROUP_DEFAULT_ID = -1;

  export default {
    name: 'EditDialog',
    components: { ApiParamsTable, RequestBody, SelectMethodDialog },
    props: {
      visible: {
        type: Boolean,
        default: false,
      },
      data: {
        type: Object,
        default: () => null,
      },
    },
    data() {
      return {
        TASK_STATUS,
        TASK_EXECUTE_TYPE,
        rules: {
          taskName: [{ required: true, trigger: 'blur', message: '任务名称不能为空' }],
          dataAssetApiId: [
            {
              required: true,
              trigger: 'blur',
              validator: (rule, value, callback) => {
                if (!value) {
                  this.isEdit && !this.apiGroupChange ? callback() : callback(new Error('请选择接口'));
                } else {
                  callback();
                }
              },
            },
          ],
          taskCron: [{ required: true, trigger: 'blur', message: '执行规则不能为空' }],
          dataAssetAppId: [{ required: true, trigger: 'blur', message: '请选择执行应用' }],
        },
        apiName: '',
        queryApiKeyword: '',
        datePickerValue: '',
        apiGroupChange: false,
        form: {
          taskName: '',
          taskType: TASK_STATUS.OFF,
          dataAssetApiId: null, // 接口id
          taskCron: '',
          taskExecuteType: TASK_EXECUTE_TYPE.FOREVER,
          taskEndTime: '',
          taskStartTime: '',
          taskDesc: '',
          apiHeadParams: [],
          apiQueryParams: [],
          apiBodyParams: {
            jsonSchema: '',
          },
          apiGroupId: '', // 选择接口id
          dataAssetAppId: null, // 执行应用id
        },
        isPostReqMethed: true,
        apiGroupList: [],
        apiList: [],
        appOptions: [],
        TAB_PANE_TYPE,
        activePane: TAB_PANE_TYPE.QUERY,
        apiPageInfo: {
          pageNo: 1,
          pageSize: 50,
        },
        tree: {},
        showMethodDialog: false,
        expression: '',
        queryMethodCallback: null,
        init: true,
        loading: false,
        // 参数设置别名
        paramsMapConfig: {
          dataType: 'assetDatatype',
          description: 'descriptions',
        },
      };
    },
    computed: {
      dialogVisible: {
        get() {
          return this.visible;
        },
        set(val) {
          this.$emit('update:visible', val);
        },
      },
      /**
       * 是否编辑模式
       */
      isEdit() {
        return !!this.data;
      },
      filterApiList() {
        return debounce(500, val => {
          this.queryApiKeyword = val;
          this.getApiGroupList();
        });
      },
    },

    watch: {
      'form.dataAssetApiId': {
        handler(val) {
          if (val) {
            // 首次初始化不执行
            this.init && this.buildRequestParams();
            this.init = true;
          } else {
            this.form.apiHeadParams = [];
            this.form.apiQueryParams = [];
            this.tree = {
              root: {
                type: 'object',
                name: 'root',
                description: '根层级',
                properties: {},
              },
            };
          }
        },
      },
      visible: {
        handler(val) {
          if (val) {
            this.getThemeList();
            this.getAuthApplicaptionList();
            this.initForm(this.data);
          } else {
            this.$refs.form.clearValidate();
          }
        },
      },
      isPostReqMethed(val) {
        if (!val && this.activePane === TAB_PANE_TYPE.BODY) {
          this.activePane = TAB_PANE_TYPE.QUERY; // 选择的API请求方方式变成GET后，如果Tab定位在请求体，需要切换
        }
      },

      'form.apiGroupId': {
        handler(val) {
          if (val) {
            this.resetRequestParams();
            this.getApiGroupList();
          }
        },
      },

      tree: {
        deep: true,
        handler(val) {
          if (val) {
            this.$set(this.form, 'apiBodyParams', { jsonSchema: JSON.stringify(val) });
          }
        },
      },

      datePickerValue(val) {
        this.form.taskStartTime = val ? val[0] : '';
        this.form.taskEndTime = val ? val[1] : '';
      },
    },

    methods: {
      initTree() {
        const postBody = this.form.apiBodyParams?.jsonSchema;
        if (!postBody) {
          this.tree = {
            root: {
              type: 'object',
              name: 'root',
              description: '根层级',
              properties: {},
            },
          };
        } else {
          this.tree = JSON.parse(postBody);
        }
      },

      resetRequestParams() {
        this.form.apiHeadParams = [];
        this.form.apiQueryParams = [];
        this.form.apiBodyParams = null;
        this.initTree();
      },

      /**
       * 构建请求参数
       */
      buildRequestParams() {
        if (!this.form.dataAssetApiId) {
          return;
        }
        this.resetRequestParams();
        const loading = this.$loading({});
        this.loading = true;
        apiController
          .showDetail({ dataAssetApiId: this.form.dataAssetApiId })
          .done(({ data }) => {
            this.apiName = data.dataAssetApi?.apiName;
            this.isPostReqMethed = data.dataAssetApi?.reqMethod === RequestMethod.POST;
            // httpParamKind QUERY,HEAD,BODY
            const parameters = data.parameters;
            try {
              parameters.forEach(item => {
                if (this.isEdit && data.dataAssetApi.dataAssetApiId === this.data?.dataAssetApiId) {
                  this.initParamsValue(item);
                }
                if (item.httpParamKind === 'QUERY') {
                  this.form.apiQueryParams.push(item);
                } else if (item.httpParamKind === 'HEAD') {
                  this.form.apiHeadParams.push(item);
                } else if (item.httpParamKind === 'BODY') {
                  this.form.apiBodyParams = item;
                }
              });
            } catch (err) {
              console.log(err);
            }
            this.deelOfflineApi();
            this.initTree();
          })
          .always(() => {
            loading.close();
            this.loading = false;
          });
      },

      /**
       * 请求参数赋值
       * @param {object} item
       */
      initParamsValue(item) {
        const colunmMap = {
          QUERY: 'apiQueryParams',
          HEAD: 'apiHeadParams',
        };
        if (item.httpParamKind === 'QUERY' || item.httpParamKind === 'HEAD') {
          const param = this.data[colunmMap[item.httpParamKind]].find(child => child.field === item.assetColumns);
          item.fixedValue = param?.fixedValue ?? '';
          item.expression = param?.expression ?? '';
          item.paramValueType = param?.type ?? '';
        } else if (item.httpParamKind === 'BODY') {
          this.jsonSchemaInitValue(item);
        }
      },
      /**
       * 数据转换
       */
      jsonSchemaInitValue(item) {
        const schema = JSON.parse(item.jsonSchema);
        const valueSchema = JSON.parse(this.data.apiBodyParams.jsonSchema);
        const exchangeValue = (origin, value) => {
          for (const key in origin) {
            const obj1 = origin[key];
            const obj2 = value[key];
            if (typeof obj1 === 'object' && typeof obj2 === 'object' && obj1.type === obj2.type) {
              if (obj1.type && obj2.type && obj1.type !== 'object') {
                obj1.defaultValue = obj2?.defaultValue ?? '';
                obj1.paramValueType = obj2?.paramValueType ?? '';
                obj1.expression = obj2?.expression ?? '';
              } else {
                exchangeValue(obj1, obj2);
              }
            }
          }
        };
        exchangeValue(schema, valueSchema);
        item.jsonSchema = JSON.stringify(schema);
      },

      /**
       * 取消新增/编辑
       * @param {boolean} auto false手动点取消按钮调用
       */
      handleClose(auto) {
        this.$emit('cancel', auto);
      },

      async onSave() {
        try {
          const isValidate = await this.$refs.form.validate();
          if (!isValidate) {
            return;
          }
          const api = this.isEdit ? 'updateTask' : 'addTask';
          const params = this.buildParams();
          const loading = this.$loading({});
          apiTask[api](params)
            .done(res => {
              this.$message.success('保存成功');
              this.handleClose();
            })
            .always(() => {
              loading.close();
            });
        } catch (e) {
          this.$message.error(e.message || '表单填写错误');
        }
      },

      /**
       * 构建保存定时任务接口参数
       */
      buildParams() {
        const params = { ...this.form };
        const { dataAssetApiId, taskStartTime, taskEndTime, apiHeadParams, apiQueryParams, apiGroupId, id } = params;
        // 编辑情况下存在id
        if (this.isEdit) {
          params.id = id;
        }
        // 选择全部无apiGroupId，单独处理
        if (apiGroupId === API_GROUP_DEFAULT_ID) {
          params.apiGroupId = this.apiList.find(item => item.dataAssetApiId === dataAssetApiId).apiGroupId;
        }
        if (this.isEdit && !dataAssetApiId) {
          params.dataAssetApiId = this.data.dataAssetApiId;
        }
        // 自定义时间
        if (params.taskExecuteType === TASK_EXECUTE_TYPE.CUSTOM) {
          params.taskStartTime = dateTimeToDate(taskStartTime) + ':00';
          params.taskEndTime = dateTimeToDate(taskEndTime) + ':00';
        }
        // 请求头参数
        params.apiHeadParams = apiHeadParams.map(item => {
          const param = {
            dataType: item.assetDatatype,
            expression: item.expression,
            field: item.assetColumns,
            fixedValue: item.fixedValue,
            isRequired: Number(item.required),
            type: item.paramValueType,
            httpParamKind: item.httpParamKind,
          };
          return param;
        });
        // QUERY参数
        params.apiQueryParams = apiQueryParams.map(item => {
          const param = {
            dataType: item.assetDatatype,
            expression: item.expression,
            field: item.assetColumns,
            fixedValue: item.fixedValue,
            isRequired: Number(item.required),
            type: item.paramValueType,
            httpParamKind: item.httpParamKind,
          };
          return param;
        });
        params.xxlTimeStamp = Date.now();
        return params;
      },

      /**
       * 编辑请求下初始化form表单
       * @param {*} val
       */
      initForm(val) {
        this.resetForm();
        if (!val) {
          return;
        }
        const { taskExecuteType, ...other } = val;
        this.form = {
          taskExecuteType: taskExecuteType ?? TASK_EXECUTE_TYPE.FOREVER,
          ...other,
        };
        this.getApiGroupList();
        this.buildRequestParams();
        this.init = false;
        this.datePickerValue =
          this.form.taskStartTime && this.form.taskEndTime ? [this.form.taskStartTime, this.form.taskEndTime] : '';
      },

      /**
       * 重置表单
       */
      resetForm() {
        this.form = {
          taskName: '',
          taskType: TASK_STATUS.OFF,
          dataAssetApiId: '',
          dataAssetAppId: '',
          taskCron: '',
          taskExecuteType: TASK_EXECUTE_TYPE.FOREVER,
          taskEndTime: '',
          taskStartTime: '',
          taskDesc: '',
          apiHeadParams: [],
          apiQueryParams: [],
          apiBodyParams: {
            jsonSchema: '',
          },
          apiGroupId: '',
        };
        this.datePickerValue = '';
        this.apiGroupChange = false;
        this.apiList = [];
      },

      /**
       * 配置表达式弹窗弹出调用
       * @param {string} expression
       * @param {function} callback
       */
      onSelectMethodShow(expression, callback) {
        this.showMethodDialog = true;
        this.expression = expression;
        if (typeof callback === 'function') {
          this.queryMethodCallback = callback;
        }
      },

      /**
       * 确定表达式
       * @param {*} expression
       */
      onSelectedMethod(expression) {
        this.expression = null;
        this.queryMethodCallback?.(expression);
      },

      /**
       * 获取接口分类列表
       */
      getThemeList() {
        apiGroup.getThemeList().done(res => {
          this.apiGroupList = res.data || [];
        });
      },

      /**
       * 获取分组下api
       * @param {object} params
       */
      getApiGroupList(params = {}) {
        if (this.form.apiGroupId !== API_GROUP_DEFAULT_ID) {
          params.apiGroupId = this.form.apiGroupId;
        }
        if (this.queryApiKeyword) {
          params.apiName = this.queryApiKeyword;
        }
        this.apiList = [];
        const loading = this.$loading({});
        apiController
          .queryList({
            ...params,
            apiType: -1, // -1全部Api （-1全部 0代表自定义sql,1代表普通的单表API，2注册HTTP API,3服务编排）
            dataAssetPublishStatus: 1, // dataAssetPublishStatus默认为1（发布状态 -1：全部 0：未发布，1：已发布）
            pageNo: this.apiPageInfo.pageNo,
            pageSize: this.apiPageInfo.pageSize,
          })
          .done(res => {
            this.apiList = res.data;
            this.deelOfflineApi();
          })
          .always(() => {
            loading.close();
          });
      },

      /**
       * 处理已下线api
       */
      deelOfflineApi() {
        if (this.isEdit && this.form.dataAssetApiId === this.data?.dataAssetApiId) {
          const findValue = this.apiList.find(item => item.dataAssetApiId === this.form.dataAssetApiId);
          if (!findValue) {
            this.apiList.push({ dataAssetApiId: this.form.dataAssetApiId, apiName: this.apiName });
          }
        }
      },
      // 获取可授权应用列表
      async getAuthApplicaptionList() {
        try {
          const { data } = await applicationApi.getApplicationList({ pageNo: 1, pageSize: 100 });
          this.appOptions = data;
        } catch (err) {
          console.error(err);
        }
      },
    },
  };
</script>
<style lang="less" scoped>
  .edit-dialog {
    &__scrollbar {
      box-sizing: border-box;
      overflow-x: hidden;
      overflow-y: hidden;
      margin-bottom: 10px;

      ::v-deep {
        .el-scrollbar__wrap {
          overflow-x: hidden;
          max-height: calc(100vh - 226px);

          .el-scrollbar__view {
            padding-bottom: 17px;
          }
        }
      }
    }

    &__title {
      height: 22px;
      font-size: 16px;
      margin: 10px 0;
      font-weight: 600;
      color: rgba(51, 51, 51, 1);
      line-height: 22px;
    }

    &__input {
      width: 400px;
    }

    &__select {
      width: 500px;
      margin-left: 10px;
    }

    &__picker {
      margin-left: 10px;
    }

    &__textarea {
      width: 500px;
    }

    ::v-deep {
      .el-dialog__footer {
        position: absolute;
        left: 0;
        bottom: 0;
        width: 100%;
        height: 70px;
        background: orange;
      }
      .el-form-item {
        width: auto;
        display: inline-block;
      }
    }
  }
</style>
