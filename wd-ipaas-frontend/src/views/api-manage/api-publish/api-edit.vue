<template>
  <el-dialog
    fullscreen
    custom-class="anim-left"
    lock-scroll
    append-to-body
    class="bd-dialog api-edit-dialog"
    :class="isReadonly ? 'is-readonly' : ''"
    :title="title"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    @opened="loadForm"
  >
    <el-tabs v-model="activeName" v-loading="loading">
      <el-tab-pane label="基础信息" name="basic">
        <el-scrollbar ref="scrollbar" class="page-scrollbar" :class="scrollbarStyle">
          <div class="scrollbar-content">
            <el-form ref="ruleForm" :model="form" :rules="rules" label-width="140px" class="demo-ruleForm">
              <!-- <div class="main-title">{{apiInfo? ap       iInfo.apiName:''}}</div>
                            <br />-->
              <div class="main-title">基本信息</div>
              <div class="basic-info">
                <el-form-item label="API名称" prop="dataAssetApi.apiName">
                  <template slot="label">
                    <tips-icon :content="$t('validator.nameWithChineseValidateDesc')"></tips-icon>
                    API名称
                  </template>
                  <el-input
                    v-model="form.dataAssetApi.apiName"
                    maxlength="50"
                    type="text"
                    :disabled="operationType === OPERATION_TYPE.EDIT || isReadonly"
                    placeholder="请输入API名称"
                  />
                </el-form-item>
                <el-form-item label="接口分类" prop="dataAssetApi.apiGroupId">
                  <el-select
                    ref="groupSelect"
                    v-model="form.dataAssetApi.apiGroupId"
                    placeholder="请选择接口分类"
                    :disabled="isReadonly"
                    @change="handleChangeApiGroup"
                  >
                    <el-option
                      v-for="item in apiGroups"
                      :key="item.id"
                      :label="item.groupName"
                      :value="item.id"
                    ></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="API Path" prop="dataAssetApi.dataMethod">
                  <template slot="label">
                    <tips-icon :content="$t('validator.nameWithoutChineseValidateDesc')"></tips-icon>
                    API Path
                  </template>
                  <el-input
                    v-model="form.dataAssetApi.dataMethod"
                    placeholder="请输入API路径"
                    maxlength="256"
                    :disabled="isReadonly"
                  >
                    <div slot="prepend" class="path-prepend" :title="path ? `${path}/` : ''">
                      {{ path ? `${path}/` : '' }}
                    </div>
                  </el-input>
                </el-form-item>
                <!-- <el-form-item label="更新频率" prop="dataAssetApi.updateFrequency">
                                <el-select
                                  v-model="form.dataAssetApi.updateFrequency"
                                  placeholder="更新频率"
                                >
                                  <el-option
                                    v-for="item in options"
                                    :key="item"
                                    :label="item"
                                    :value="item"
                                  ></el-option>
                                </el-select>
                              </el-form-item> -->
                <el-form-item label="请求协议" prop="dataAssetApi.protocol">
                  <el-select v-model="form.dataAssetApi.protocol" placeholder="请求协议" :disabled="true">
                    <el-option v-for="item in protocolList" :key="item.value" :label="item.label" :value="item.value">
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="请求方式" prop="dataAssetApi.reqMethod">
                  <el-select v-model="form.dataAssetApi.reqMethod" placeholder="请求方式" :disabled="isReadonly">
                    <el-option v-for="item in methodList" :key="item.value" :label="item.label" :value="item.value">
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="返回格式" prop="dataAssetApi.responseContentType">
                  <el-select
                    v-model="form.dataAssetApi.responseContentType"
                    :disabled="isReadonly"
                    placeholder="返回格式"
                    multiple
                  >
                    <el-option
                      v-for="item in reponseContentTypeEnum"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    >
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="是否公开" prop="dataAssetApi.secret">
                  <el-select v-model="form.dataAssetApi.secret" :disabled="isReadonly" placeholder="公开方式">
                    <el-option v-for="item in secretList" :key="item.value" :label="item.label" :value="item.value">
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="API描述" prop="dataAssetApi.apiDescription">
                  <el-input
                    v-model="form.dataAssetApi.apiDescription"
                    type="textarea"
                    maxlength="256"
                    placeholder="请输入描述"
                    :disabled="isReadonly"
                  ></el-input>
                </el-form-item>
                <el-form-item label="API类型" prop="dataAssetApi.apiType">
                  <el-radio-group v-model="form.dataAssetApi.apiType">
                    <el-radio
                      :disabled="(apiInfo.dataAssetApiId && form.dataAssetApi.apiType !== API_TYPE.TABLE) || isReadonly"
                      label="NORMAL_TABLE"
                      >数据表</el-radio
                    >
                    <el-radio
                      :disabled="(apiInfo.dataAssetApiId && form.dataAssetApi.apiType !== API_TYPE.SQL) || isReadonly"
                      label="CUSTOM_SQL"
                      >SQL模式</el-radio
                    >
                    <el-radio
                      :disabled="(apiInfo.dataAssetApiId && form.dataAssetApi.apiType !== API_TYPE.HTTP) || isReadonly"
                      label="EXTERNAL_HTTP"
                      >HTTP模式</el-radio
                    >
                    <el-radio
                      :disabled="(apiInfo.dataAssetApiId && form.dataAssetApi.apiType !== API_TYPE.WS) || isReadonly"
                      label="WEB_SERVICE"
                      >Webservice</el-radio
                    >
                  </el-radio-group>
                </el-form-item>
              </div>
              <div
                v-if="form.dataAssetApi.apiType !== API_TYPE.HTTP && form.dataAssetApi.apiType !== API_TYPE.WS"
                class="main-title"
              >
                参数信息
              </div>
              <el-form-item
                v-if="form.dataAssetApi.apiType !== API_TYPE.HTTP && form.dataAssetApi.apiType !== API_TYPE.WS"
                label="选择数据源"
                prop="dataAssetApi.dataSourceId"
                style="width: auto; display: inline-block"
              >
                <el-select
                  v-model="form.dataAssetApi.dataSourceId"
                  v-loading="optionsSource.loading"
                  filterable
                  placeholder="请选择"
                  :disabled="isReadonly"
                  @focus="showDataSource"
                  @change="handleChangeDataSource"
                >
                  <el-option
                    v-for="item in optionsSource.list"
                    :key="item.id"
                    :label="item.connectionName"
                    :value="item.id"
                  ></el-option>
                </el-select>
              </el-form-item>

              <!-- API类型为HTTP模式 -->
              <div v-if="form.dataAssetApi.apiType == API_TYPE.HTTP || form.dataAssetApi.apiType === API_TYPE.WS">
                <div class="main-title">后端服务定义</div>
                <el-form-item class="main-form-item" label="接口域名" prop="dataAssetApi.apiAttr.host">
                  <template slot="label">
                    <tips-icon content="以http://或https://开头，并且不包含Path"></tips-icon>
                    接口域名
                  </template>
                  <el-input
                    v-model="form.dataAssetApi.apiAttr.host"
                    maxlength="200"
                    type="text"
                    placeholder="请输入接口域名"
                    :disabled="isReadonly"
                  />
                </el-form-item>
                <el-form-item class="main-form-item" label="接口地址" prop="dataAssetApi.apiAttr.path">
                  <template slot="label">
                    <tips-icon :content="$t('validator.nameWithoutChineseValidateDesc')"></tips-icon>
                    接口地址
                  </template>
                  <el-input
                    v-model="form.dataAssetApi.apiAttr.path"
                    maxlength="220"
                    type="text"
                    placeholder="请输入接口地址"
                    :disabled="isReadonly"
                  />
                </el-form-item>
                <br />

                <el-form-item
                  v-if="form.dataAssetApi.apiType == API_TYPE.WS"
                  class="main-form-item"
                  label="方法名"
                  prop="dataAssetApi.apiAttr.wsMethod"
                >
                  <el-input
                    v-model="form.dataAssetApi.apiAttr.wsMethod"
                    maxlength="100"
                    type="text"
                    placeholder="请输入方法名"
                    :disabled="isReadonly"
                  />
                </el-form-item>
                <el-form-item
                  v-if="form.dataAssetApi.apiType == API_TYPE.WS"
                  class="main-form-item"
                  label="命名空间"
                  prop="dataAssetApi.apiAttr.wsNameSpaceUri"
                >
                  <el-input
                    v-model="form.dataAssetApi.apiAttr.wsNameSpaceUri"
                    maxlength="100"
                    type="text"
                    placeholder="请输入命名空间"
                    :disabled="isReadonly"
                  />
                </el-form-item>
                <br />
                <el-form-item label="响应超时设置" prop="dataAssetApi.apiAttr.timeout">
                  <el-input-number
                    v-model="form.dataAssetApi.apiAttr.timeout"
                    :min="-1"
                    :max="999999"
                    :disabled="isReadonly"
                  ></el-input-number>
                  毫秒，最大输入999999
                </el-form-item>
                <br />
                <el-form-item
                  v-if="form.dataAssetApi.apiType == API_TYPE.HTTP"
                  label="参数映射规则"
                  prop="dataAssetApi.respMappingRule"
                >
                  <el-select v-model="form.dataAssetApi.respMappingRule" placeholder="默认" clearable>
                    <el-option
                      v-for="item in ParamsMappingOptions"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    >
                    </el-option>
                  </el-select>
                </el-form-item>

                <api-http
                  ref="apiHttp"
                  :show-body="form.dataAssetApi.reqMethod === 'POST'"
                  :body-data.sync="form.bodyData"
                  :parameters.sync="form.parameters"
                  :http-codes="form.dataAssetApi.apiAttr.httpCodes"
                  :response-data.sync="form.responseParams"
                  :is-readonly="isReadonly"
                ></api-http>
              </div>
              <!-- API类型为SQL模式 -->
              <div v-if="form.dataAssetApi.apiType == API_TYPE.SQL">
                <el-form-item prop="dataAssetApi.apiSql" style="width: 100%" label="查询SQL">
                  <!-- <api-sql-code
                                  @enter="handleSqlEnter()"
                                  v-model="form.dataAssetApi.apiSql"
                                /> -->
                  <template slot="label">
                    <tips-icon
                      :content="'例：select ID, TASK_NAME from T_STREAM_TASK where ID = ${ID:long}'"
                    ></tips-icon>
                    SQL语句
                  </template>
                  <CodemirrorEditor
                    ref="codeMirrorEditor"
                    :refresh="visible"
                    prop="apiSql"
                    :config="form.dataAssetApi"
                    :is-readonly="isReadonly"
                    @change-value="changeApiSql"
                    @change-setting-value="changeSettingValue"
                  >
                    <template #example>
                      <el-button type="text" class="example-btn" @click="openExample">示例值</el-button>
                    </template>
                  </CodemirrorEditor>
                  <div v-if="!isReadonly" style="padding: 10px 0">
                    <el-button type="primary" class="bd-button" @click="handleSqlEnter">检测语句 </el-button>
                  </div>
                </el-form-item>
                <!-- <api-sql-detail-table
                                v-if="phoenixQuery.response"
                                :request="phoenixQuery.request"
                                :response="phoenixQuery.response"
                                :is-readonly="isReadonly" -->
                <api-sql-params
                  v-if="sqlParams.response"
                  v-model="sqlParams"
                  :is-readonly="isReadonly"
                  request-tip="参数为空时，系统在执行SQL内将不拼接使用此参数的条件及对应的查询字段，若勾选“允许为空”，则不启用此规则；"
                />
              </div>
              <!-- API类型为数据表 -->
              <template v-if="form.dataAssetApi.apiType == API_TYPE.TABLE">
                <el-form-item
                  label="选择数据表"
                  prop="dataAssetApi.dataAssetName"
                  style="width: auto; display: inline-block"
                >
                  <el-button
                    type="primary"
                    icon="el-icon-coin"
                    :disabled="!form.dataAssetApi.dataSourceId || isReadonly"
                    @click="changeDataAssetName"
                  >
                    {{ !!form.dataAssetApi.dataAssetName ? form.dataAssetApi.dataAssetName : '请选择数据表' }}
                  </el-button>
                </el-form-item>
                <br />
                <el-form-item label="操作类型" required>
                  <el-radio-group
                    v-model="dataAssetActive"
                    :disabled="!!apiInfo.dataAssetApiId"
                    @input="handleChangeType"
                  >
                    <el-radio v-for="i in BaseOperate.list" :key="i.value" :label="i.value">{{ i.label }}</el-radio>
                  </el-radio-group>
                </el-form-item>
                <div class="main-title">选择参数</div>

                <!-- <el-form-item label="关键词" style="width: auto; display: inline-block">
                                <el-input
                                  v-model.trim="name"
                                  placeholder="请输入内容"
                                  maxlength="64"
                                  clearable
                                  class="input-with-select"
                                  @keydown.native="onSearchEnter"
                                >
                                  <el-button slot="append" icon="el-icon-search" @click="onSearchParams"></el-button>
                                </el-input>
                              </el-form-item> -->
                <detail-table
                  :key="dataAssetActive"
                  v-loading="columnLoading"
                  :set-type="dataAssetActive"
                  :keyword="keyword"
                  :selected-source="selectedSource"
                  :data-asset-api-id="form.dataAssetApi.dataAssetApiId"
                  :data="tableData"
                  :results="form.results"
                  :parameters="form.parameters"
                  :filters="form.filters"
                  :is-readonly="isReadonly"
                  @change-selection="changeSelection"
                />
              </template>
            </el-form>
          </div>
        </el-scrollbar>
      </el-tab-pane>
      <el-tab-pane
        v-if="form.dataAssetApi.apiType !== API_TYPE.HTTP && form.dataAssetApi.apiType !== API_TYPE.WS"
        label="Redis锁"
        name="redisLock"
      >
        <redis-lock
          ref="redisLock"
          :data="form.dataAssetApi.apiAttr"
          :check-fields="
            form.dataAssetApi.apiType === API_TYPE.TABLE ? [...form.parameters, ...form.filters] : sqlParams.request
          "
        />
      </el-tab-pane>
    </el-tabs>

    <div v-if="!isReadonly" class="bd-dialog-footer">
      <el-button size="medium" class="cancel" @click="hide">取消</el-button>
      <el-button type="primary" size="medium" :disabled="saveDisabled" class="bd-button" @click="handleSave"
        >保存
      </el-button>
    </div>
    <table-selector-dialog
      :source-id="form.dataAssetApi.dataSourceId"
      :value.sync="form.dataAssetApi.dataAssetName"
      :visible.sync="dialog.tableSelector"
      @change="handleChange()"
    />
  </el-dialog>
</template>

<script>
  import { MessageBox, Message } from 'element-ui';
  import TipsIcon from '@/components/tips-icon';
  import ApiSqlParams from '@/components/api-edit/api-sql-params.vue';

  import dataSource from '@/api/data-source';
  import dataAnalysis from '@/api/data-analysis';
  import CodemirrorEditor from '@/bz-components/codemirror-editor';
  import apiControll from '@/api/api-controller';
  import DetailTable from '@/bz-components/detail-table.vue';
  import TableSelectorDialog from '@/bz-components/table-selector-dialog.vue';
  import apiGroup from '@/api/api-group';
  import ParamsMappingApi from '@/api/params-mapping';

  import { BaseOperate, UPDATE_OPERATE, QUERY_OPERATE, DELETE_OPERATE } from '@/enum';
  import {
    ApiType,
    ApiPublishStatus,
    ProtocolType,
    RequestMethod,
    ResponseContentType,
    SecretType,
    UpdateFrequency,
  } from '@/utils/enum/index';
  import { INIT_ELE } from '@/utils/enum';
  import { schema2json } from '@/utils/schema2json';
  import textUtils from '@/utils/text-utils';
  import {
    paramsTransformToTree,
    treeTransformToParams,
    dataTableParametersToTree,
    dataTableResponseTree,
    handleFilter,
  } from '@/utils/api-http';
  import { sqlPlacholderText } from '@/utils/sql-placholder';
  import { IMPORT_TYPE, IMPORT_TYPE_OPTIONS } from '@/utils/enum/api-swagger';
  import RedisLock from '@/bz-components/redis-lock.vue';
  import { getRedisForm } from '@/utils/redisLockUtil';
  import ApiHttp from './api-http.vue';
  import { OPERATION_TYPE, API_TYPE } from './types';

  const getDefaultForm = () => ({
    dataAssetApi: {
      dataAssetApiId: null,
      apiDescription: null,
      apiGroupId: null,
      apiName: null,
      dataAssetApiMethod: null,
      dataAssetName: null,
      dataAssetPublishStatus: null,
      dataSourceId: null,
      inCharge: null,
      protocol: 'HTTPS',
      reqMethod: 'GET',
      dataMethod: '',
      responseContentType: ['JSON'],
      secret: 'PRIVATE',
      updateFrequency: 'DAY',
      apiType: API_TYPE.TABLE,
      apiSql: null,
      respMappingRule: null,
      apiAttr: {
        host: '',
        path: '',
        timeout: 60000,
        httpCodes: [],
        wsMethod: null,
        wsNameSpaceUri: null,
      },
    },
    bodyData: {
      bodyType: 'json',
      bodyXml: '',
      jsonSchema: true,
      jsonText: '',
      tree: {
        root: {
          type: 'object',
          name: 'root',
          description: '根层级',
          properties: {},
          rootRequired: false,
        },
      },
    },
    parameters: [],
    responseParams: [],
    results: [],
    filters: [],
  });

  const domainValidator = (_rule, value, callback) => {
    const rules = /^(http|https):\/\/([\w.]+\/?)\S*/;
    // 校验IP地址
    const ip =
      /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
    // 校验域名
    const url = /^(([-\u4E00-\u9FA5a-z0-9]{1,63})\.)+([\u4E00-\u9FA5a-z]{2,63})\.?$/;
    // 校验端口号
    const port = /^(([0-9]|[1-9]\d{1,3}|[1-5]\d{4}|6[0-5]{2}[0-3][0-5]))$/;

    const valueSplit = (value || '').split(':');
    const second = valueSplit.length >= 2 ? valueSplit[1] : '';
    const domain = (second || '').substring(2);

    if (!value) {
      callback(new Error('接口域名不能为空'));
    } else if (value.length > 100) {
      callback(new Error('域名过长,不能超过100个字符'));
    } else if (!rules.test(value) || (valueSplit.length !== 2 && valueSplit.length !== 3)) {
      callback(new Error('请输入正确的IP地址或域名及端口格式!例如：http://www.baidu.com:80'));
    } else if (!(ip.test(domain) || url.test(domain))) {
      if (domain.split('/').length === 1) {
        callback(new Error('请输入正确的IP地址例如：127.0.0.1或者域名例如：www.baidu.com'));
      } else {
        callback(new Error('请去掉域名后面的路径'));
      }
    } else if (valueSplit.length === 3 && !port.test(valueSplit[2])) {
      const third = valueSplit[2];
      if (third.split('/').length === 1) {
        callback(new Error('请输入正确的端口号例如：80'));
      } else {
        callback(new Error('请去掉端口后面的路径'));
      }
    } else {
      callback();
    }
  };
  const pathValidator = (_rule, value, callback) => {
    const regRule = /^[{}/_\-0-9a-zA-Z/.]+$/g;

    const propLabel = '接口地址';
    if (!value) {
      return callback(new Error(`${propLabel}不能为空`));
    } else if (value.length > 200) {
      callback(new Error(`${propLabel}过长,不能超过200个字符`));
    } else if (!regRule.test(value)) {
      return callback(new Error(`${propLabel}格式不正确`));
    }

    return callback();
  };
  const dataMethodValidator = (_rule, value, callback) => {
    const Rule = /^[_\-0-9a-zA-Z/.]+$/;
    if (!value) {
      return callback(new Error('API路径不能为空'));
    } else if (value.length > 200) {
      callback(new Error(`API路径过长,不能超过200个字符`));
    } else if (!Rule.test(value)) {
      return callback(new Error('API路径格式不正确'));
    }

    return callback();
  };
  export default {
    components: {
      DetailTable,
      TipsIcon,
      TableSelectorDialog,
      // ApiSqlCode,
      // ApiSqlDetailTable,
      CodemirrorEditor,
      ApiHttp,
      ApiSqlParams,
      RedisLock,
    },
    props: {
      apiInfo: {
        type: Object,
        default: () => ({}),
      },
      visible: {
        type: Boolean,
        default: false,
      },
      groupIdInfo: {
        type: [Number, String],
        default: null,
      },
      treeData: {
        type: Array,
        default: () => [],
      },
      operationType: {
        type: String,
        default: '',
      },
    },
    data() {
      return {
        INIT_ELE,
        API_TYPE,
        OPERATION_TYPE,
        UPDATE_OPERATE,
        QUERY_OPERATE,
        DELETE_OPERATE,
        BaseOperate,
        saveDisabled: true,
        sqlParams: {
          request: null,
          response: null,
        },
        // groupNameConfirm: null, //存放选择的接口分类名/
        optionsSource: {
          list: [],
          loading: false,
        }, // 数据源名称,
        selectedSource: {},
        tableData: [],
        name: '', // 字段名
        keyword: '',
        path: '',
        form: getDefaultForm(),
        protocolList: ProtocolType._list,
        options: UpdateFrequency._list,
        methodList: RequestMethod._list,
        reponseContentTypeEnum: ResponseContentType._list,
        secretList: SecretType._list,
        apiGroups: [],
        rules: {
          'dataAssetApi.apiName': [{ required: true, message: '请输入API名称', trigger: 'blur' }],
          'dataAssetApi.dataMethod': [{ required: true, validator: dataMethodValidator, trigger: 'blur' }],
          'dataAssetApi.updateFrequency': [{ required: true, message: '请选择更新频率' }],
          'dataAssetApi.protocol': [{ required: true, message: '请输入请求协议' }],
          'dataAssetApi.reqMethod': [{ required: true, message: '请选择请求方式', trigger: 'blur' }],
          'dataAssetApi.responseContentType': [{ required: true, message: '请选择返回格式', trigger: 'blur' }],
          'dataAssetApi.secret': [{ required: true, message: '请选择保密方式', trigger: 'blur' }],
          'dataAssetApi.apiGroupId': [{ required: true, message: '请选择接口分类', trigger: 'blur' }],
          'dataAssetApi.apiType': [{ required: true, message: '请选择API类型' }],
          'dataAssetApi.dbType': [{ required: true, message: '请选择数据源类型' }],
          'dataAssetApi.dataSourceId': [{ required: true, message: '请选择数据源名称' }],
          'dataAssetApi.dataAssetName': [{ required: true, message: '请选择数据表名称' }],
          'dataAssetApi.apiSql': [{ required: true, message: '查询Sql不能为空' }],
          'dataAssetApi.apiAttr.host': [{ required: true, validator: domainValidator, trigger: 'blur' }],
          'dataAssetApi.apiAttr.path': [{ required: true, validator: pathValidator, trigger: 'blur' }],
          'dataAssetApi.apiAttr.wsMethod': [{ required: true, message: '方法名不能为空' }],
          'dataAssetApi.apiAttr.wsNameSpaceUri': [{ required: true, message: '命名空间不能为空' }],
          // 'dataAssetApi.apiAttr.resultExample': [
          //   { required: true, message: '正常返回结果示例不能为空', trigger: 'blur' },
          // ],
          // 'dataAssetApi.apiAttr.errorExample': [
          //   { required: true, message: '异常返回结果示例不能为空', trigger: 'blur' },
          // ],
        },
        loading: false,
        columnLoading: false,
        dialog: {
          tableSelector: false,
        },
        dataAssetActive: BaseOperate.list[0].value,
        responseContentType: ['JSON'], // API返回格式,
        baseResponseParam: null, // 公用响应体参数 数据表保存用
        sqlPlacholder: sqlPlacholderText,
        sqlOperationType: '', // sql操作类型
        activeName: 'basic', // tab标签选中项
        ParamsMappingOptions: [], // 参数映射规则
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
      /** 是否开启编辑
       * 1. 未发布的可以编辑
       * 2. 复制的可以编辑
       */
      isReadonly() {
        return (
          this.apiInfo.dataAssetPublishStatus === ApiPublishStatus.PUBLISH && this.operationType !== OPERATION_TYPE.COPY
        );
      },
      getResponseContentType() {
        return this.reponseContentTypeEnum
          .map(item => item.value)
          .every(item => this.form.dataAssetApi.responseContentType.includes(item))
          ? 'ALL'
          : this.form.dataAssetApi.responseContentType[0];
      },
      scrollbarStyle() {
        const isMac = /macintosh|mac os x/i.test(navigator.userAgent);
        return isMac ? 'mac' : '';
      },
      /**
       * 是否为修改操作类型
       */
      isUpdateType() {
        return this.dataAssetActive === UPDATE_OPERATE.value;
      },
      title() {
        if (this.isReadonly) {
          return '查看API';
        } else {
          switch (this.operationType) {
            case OPERATION_TYPE.CREATE:
            case OPERATION_TYPE.COPY:
              return '新增API';
            case OPERATION_TYPE.EDIT:
              return '编辑API';
            default:
              return '';
          }
        }
      },
    },
    watch: {
      'form.dataAssetApi.apiType': {
        immediate: true,
        handler: async function (val) {
          if (val) {
            // 新增
            if (!this.apiInfo.dataAssetApiId) {
              this.form.dataAssetApi.dataSourceId = null;
              if (val === API_TYPE.SQL || val === API_TYPE.TABLE) {
                this.form.dataAssetApi.apiAttr = getRedisForm();
              } else {
                this.form.dataAssetApi.apiAttr = getDefaultForm().dataAssetApi.apiAttr;
              }
            }
            this.saveDisabled = val === API_TYPE.SQL;

            // HTTP类型获取参数映射数据源
            if (val === API_TYPE.HTTP) {
              this.getParamsMappingOptions();
            }
          }
        },
      },
      'form.dataAssetApi.dataAssetName': {
        handler: function (val, pre) {
          if (val && pre !== null) {
            this.form.parameters = [];
            this.form.filters = [];
            this.form.results = [];
          }
        },
      },
      apiInfo: {
        deep: true,
        immediate: true,
        handler: function (val) {
          this.dataAssetActive = val?.operationType || this.BaseOperate.list[0].value;
        },
      },
      path(val) {
        const string = this.form.dataAssetApi.dataAssetApiMethod || '';
        if (string.indexOf(val) !== -1) {
          this.form.dataAssetApi.dataMethod = string.substring(this.path.length + 1);
        }
      },
      'form.dataAssetApi.dataAssetApiMethod'(val) {
        const string = val || '';
        if (string.indexOf(this.path) !== -1) {
          this.form.dataAssetApi.dataMethod = string.substring(this.path.length + 1);
        }
      },
    },
    methods: {
      changeSettingValue() {
        if (this.form.dataAssetApi.apiType === API_TYPE.SQL) {
          this.saveDisabled = true;
        }
        this.$refs.ruleForm.clearValidate('dataAssetApi.apiSql');
      },
      changeApiSql() {
        if (this.form.dataAssetApi.apiType === API_TYPE.SQL && !this.saveDisabled) {
          this.saveDisabled = true;
        }
        this.$refs.ruleForm.clearValidate('dataAssetApi.apiSql');
      },
      sqlDataTransform(response) {
        return (response || []).map(item => {
          return {
            allowEmpty: !!item.allowEmpty,
            assetColumns: item.alias || item.datasourceTableColumnName,
            assetDatatype: item.datasourceTableColumnType ?? 'string',
            required: item.required,
            sample: item.sample ?? '',
            descriptions: item.datasourceTableColumnDesc ?? '',
          };
        });
      },
      /**
       * 公共响应参数转换
       */
      responseBase(item) {
        return {
          assetColumns: item.alias || item.datasourceTableColumnName,
          assetDatatype: item.datasourceTableColumnType ?? 'string',
          required: !!item.required,
          sample: item.sample ?? '',
          descriptions: item.datasourceTableColumnDesc ?? '',
        };
      },
      sqlResponseDataTransform(base, response) {
        return base.map(item => {
          let obj = this.responseBase(item);
          if (item.datasourceTableColumnName === 'data') {
            const responseData = this.sqlDataTransform(response);
            obj = {
              ...obj,
              children: responseData,
            };
          }
          return obj;
        });
      },

      handleSqlEnter() {
        const { apiSql, dataSourceId } = this.form.dataAssetApi;
        // 由于存在特定的一些占位符，格式化SQL后会出现多余空格而导致后端解析报错，故替换处理
        const formatApiSql = apiSql.replace(/: /g, ':');
        const params = {
          sql: formatApiSql,
          dataSourceId,
        };
        return dataAnalysis.sql(params).then(res => {
          const { requestParam, responseParam, baseResponseParam, sqlOperationType } = res.data;
          // 请求参数
          this.sqlParams.request = handleFilter(this.sqlParams.request, this.sqlDataTransform(requestParam));

          // 响应参数
          this.sqlParams.response = handleFilter(
            this.sqlParams.response,
            this.sqlResponseDataTransform(baseResponseParam, responseParam)
          );
          this.sqlOperationType = sqlOperationType;
          this.saveDisabled = false;
        });
      },
      resetRedisForm() {
        if (this.form.dataAssetApi.apiType === API_TYPE.SQL || this.form.dataAssetApi.apiType === API_TYPE.TABLE) {
          this.form.dataAssetApi.apiAttr = getRedisForm();
        }
      },
      loadForm() {
        this.activeName = 'basic';
        this.$refs.ruleForm.resetFields();
        this.path = '';
        this.keyword = '';
        this.name = '';
        this.optionsSource = {
          list: [],
          loading: false,
        };
        this.selectedSource = {};
        this.form = getDefaultForm();
        this.resetRedisForm();
        this.tableData = [];
        this.initDialog().then(async () => {
          // this.handleChangeApiGroup();

          await this.handleChange();
          await this.getSelectedDataSource();

          this.$nextTick(() => {
            this.$refs.ruleForm.clearValidate();
          });
          this.saveDisabled = false;
        });
      },
      hide() {
        this.dialogVisible = false;
      },
      show() {
        this.dialogVisible = true;
      },
      async showDataSource() {
        this.optionsSource.loading = true;
        await dataSource
          .list()
          .then(res => {
            // 限制类型为Hbase的数据源只支持数据表类型的API
            if (this.form.dataAssetApi.apiType === API_TYPE.SQL) {
              this.optionsSource.list = res.data.filter(item => item.dbType !== 'HBASE');
            } else {
              this.optionsSource.list = res.data;
            }
          })
          .catch(() => {
            this.optionsSource.list = [];
          })
          .always(() => {
            this.optionsSource.loading = false;
          });
      },
      // 数据表 查询模式 公共参数作为请求参数
      normalQueryModeAddPublicParameters() {
        const listName = ['pageNo', 'pageSize', 'orderBy'];
        const find = this.form.parameters.find(item => item.assetColumns === 'pageNo');
        if (!find) {
          this.tableData.forEach(item => {
            if (listName.includes(item.datasourceTableColumnName)) {
              const obj = {
                assetColumns: item.datasourceTableColumnName,
                assetColumnsLength: item.datasourceTableColumnLength,
                assetDatatype: item.datasourceTableColumnType,
                descriptions: item.datasourceTableColumnDesc,
                sample: item.sample,
                required: item.required,
                filter: false,
                parameter: true,
                result: false,
              };

              this.form.parameters.push(obj);
            }
          });
        }
      },
      getColumn() {
        /**
         * 数据表类型
         */
        if (
          !!this.form.dataAssetApi.dataSourceId &&
          !!this.form.dataAssetApi.dataAssetName &&
          this.form.dataAssetApi.apiType !== API_TYPE.HTTP &&
          this.form.dataAssetApi.apiType !== API_TYPE.WS
        ) {
          this.columnLoading = true;
          dataSource
            .showColumn({
              dataSourceId: this.form.dataAssetApi.dataSourceId,
              tableName: this.form.dataAssetApi.dataAssetName,
              operationType: this.dataAssetActive,
            })
            .done(res => {
              const { datasourceTableColumnDoList, baseResponseParam } = res.data;
              this.tableData = datasourceTableColumnDoList;
              this.baseResponseParam = baseResponseParam;

              if (this.dataAssetActive === QUERY_OPERATE.value) {
                this.normalQueryModeAddPublicParameters();
              }
            })
            .always(() => {
              this.columnLoading = false;
            });
        }
        if (this.form.dataAssetApi.apiType === API_TYPE.TABLE && !this.dataAssetActive) {
          this.dataAssetActive = this.apiInfo?.operationType || this.BaseOperate.list[0].value;
        }
      },
      handleChangeDataSource() {
        this.tableData = [];
        this.form.results = [];
        this.form.parameters = [];
        this.form.dataAssetApi.dataAssetName = null;
        this.form.dataAssetApi.apiSql = '';
        this.sqlParams = {
          request: null,
          response: null,
        };
        this.getSelectedDataSource();
        this.$nextTick(() => {
          this.$refs.ruleForm.clearValidate('dataAssetApi.dataAssetName');
        });
      },
      getSelectedDataSource() {
        this.selectedSource = {};
        const arr = this.optionsSource.list.filter(item => item.id === this.form.dataAssetApi.dataSourceId);
        if (!!arr && arr.length > 0) {
          this.selectedSource = arr[0];
        }
      },

      /** 获取初始数据 */
      async initDialog() {
        await this.getApiThemeList();
        const p1 = new Promise(resolve => {
          this.sqlParams = { request: null, response: null };
          switch (this.operationType) {
            case OPERATION_TYPE.CREATE:
              this.form.dataAssetApi.apiGroupId = this.groupIdInfo;
              this.handleChangeApiGroup();
              resolve(this.form);
              break;
            case OPERATION_TYPE.EDIT:
              resolve(this.showDetail(this.apiInfo.dataAssetApiId));
              break;
            case OPERATION_TYPE.COPY:
              resolve(this.showDetail(this.apiInfo.dataAssetApiId, true));
              break;
            default:
              break;
          }
        });
        const p2 = this.showDataSource();
        return Promise.all([p1, p2]);
      },

      /** 获取api详细数据
       * id apiId
       * resetApiId 是否需要重置apiId(复制模式下)
       */
      showDetail(id, resetApiId = false) {
        this.loading = true;
        return new Promise(resolve => {
          apiControll
            .showDetail({ dataAssetApiId: id })
            .done(async res => {
              const data = res.data;
              const { apiType, reqMethod, operationType } = data.dataAssetApi;
              const newData = {
                ...data,
              };
              if (apiType === API_TYPE.TABLE && data.parameters.length) {
                /**
                 * GET请求
                 */
                if (reqMethod === 'GET') {
                  const filterData = data.parameters.filter(o => o.typeAttr === 'FILTER');
                  const queryData = data.parameters.filter(o => o.typeAttr !== 'FILTER');
                  newData.filters = filterData;
                  newData.parameters = queryData;
                } else {
                  // POST请求
                  const filterDataArr = Object.entries(
                    JSON.parse(data.parameters[0].jsonSchema).root.properties?.filters?.properties ?? {}
                  );

                  let queryDataArr = [];
                  /**
                   * 数据表修改操作类型特殊处理
                   */
                  if (this.isUpdateType) {
                    queryDataArr = Object.entries(
                      JSON.parse(data.parameters[0].jsonSchema).root.properties?.parameters?.properties ?? {}
                    );
                  } else {
                    queryDataArr = Object.entries(JSON.parse(data.parameters[0].jsonSchema).root?.properties ?? {});
                  }
                  const _required = JSON.parse(data.parameters[0].jsonSchema).root?.required || [];
                  const filterData = (filterDataArr.length && treeTransformToParams(filterDataArr, _required)) || [];
                  const queryData = (queryDataArr.length && treeTransformToParams(queryDataArr, _required)) || [];
                  if (filterData.length) {
                    newData.filters = filterData;
                  }
                  if (queryData.length) {
                    newData.parameters = queryData;
                  }
                }
              }
              // 请求参数体
              const postBody = data.parameters.filter(t => t.httpParamKind === 'BODY')[0] ?? null;
              if (postBody) {
                newData.bodyData = {
                  bodyType: 'json',
                  tree: JSON.parse(postBody.jsonSchema),
                };
              }

              this.form = Object.assign({}, getDefaultForm(), newData);
              if (apiType === API_TYPE.HTTP && !this.form.dataAssetApi.respMappingRule) {
                this.form.dataAssetApi.respMappingRule = null;
              }
              // 复制重置输入的apiID
              if (resetApiId) {
                try {
                  const temp = this.form.dataAssetApi;
                  temp.apiName = `${temp.apiName}_copy`;
                  temp.dataAssetApiMethod = `${temp.dataAssetApiMethod}_copy`;
                  temp.dataAssetApiId = null;
                  if (temp.apiAttr) {
                    delete temp.apiAttr.apiId;
                    delete temp.apiAttr.id;
                  }
                } catch (e) {
                  console.error(e);
                }
              }
              // 编辑时设置之前选择的 接口分类
              // this.form.dataAssetApi.apiGroupId = data.dataAssetApi.apiGroupId
              this.handleChangeApiGroup();
              const string = data.dataAssetApi.dataAssetApiMethod;
              if (string && this.path && string.indexOf(this.path) !== -1) {
                this.$set(this.form.dataAssetApi, 'dataMethod', string.substring(this.path.length + 1));
              }
              const { apiSql } = this.form.dataAssetApi;
              if (data.dataAssetApi.responseContentType === 'ALL') {
                this.form.dataAssetApi.responseContentType = this.reponseContentTypeEnum.map(item => item.value);
              } else {
                this.form.dataAssetApi.responseContentType = [data.dataAssetApi.responseContentType];
              }

              /**
               * SQL类型回显
               */
              if (apiType === API_TYPE.SQL && !!apiSql) {
                // 请求参数
                if (reqMethod === 'GET') {
                  // GET 请求
                  this.sqlParams.request = data.parameters;
                } else {
                  // POST请求
                  const arr = Object.entries(JSON.parse(postBody.jsonSchema).root.properties);
                  const requiredList = JSON.parse(postBody.jsonSchema).root?.required ?? [];
                  this.sqlParams.request = (arr.length && treeTransformToParams(arr, requiredList)) || [];
                }

                // 响应参数
                if (data.responseParams?.length) {
                  const arr = Object.entries(JSON.parse(data.responseParams[0]?.responsePostData).root.properties);
                  this.sqlParams.response = (arr.length && treeTransformToParams(arr, [])) || [];
                }
                this.sqlOperationType = operationType;
              }
            })
            .always(() => {
              this.loading = false;
              resolve(this.form);
              this.saveDisabled = false;
            });
        });
      },
      handleChangeApiGroup(e) {
        let current = null;
        const id = e || this.form.dataAssetApi.apiGroupId;
        if (id) {
          current = this.apiGroups.find(item => id === item.id);
        } else {
          current = this.apiGroups[0];
          this.form.dataAssetApi.apiGroupId = this.apiGroups[0]?.id;
        }
        this.path = current?.groupPath || '';
      },
      async getApiThemeList() {
        const res = await apiGroup.getThemeList();
        this.apiGroups = res.data;
      },
      // HTTP类型获取参数映射规则
      async getParamsMappingOptions() {
        try {
          const res = await ParamsMappingApi.getParamsMappingList();
          this.ParamsMappingOptions = res.map(item => {
            return {
              label: item.respParamMappingRuleName,
              value: item.id,
            };
          });
        } catch (err) {
          this.$message({
            type: 'error',
            message: err.message,
          });
        }
      },
      changeSelection(results, parameters, filters) {
        this.form.results = results;
        this.form.parameters = parameters;
        this.form.filters = filters;
      },
      changeDataAssetName() {
        this.dialog.tableSelector = true;
      },
      handleChange() {
        this.getColumn();
      },
      formValidate() {
        return new Promise((resolve, _reject) => {
          this.$refs.ruleForm.validate(valid => {
            if (valid) {
              resolve();
            } else {
              _reject(new Error('请检查必填项'));
              return false;
            }
          });
        });
      },
      async handleSave() {
        Promise.all([this.formValidate(), this.$refs?.redisLock?.validator()])
          .then(() => {
            this.save();
          })
          .catch(_error => {
            Message.closeAll();
            this.$message.error('请检查必填项');
          });
      },
      async save() {
        try {
          this.loading = true;
          const apiTypeObj = ApiType['_' + this.form.dataAssetApi.apiType.toLowerCase()];
          const apiDeploy = Object.assign({}, getDefaultForm(), this.form, {
            dataAssetApi: {
              ...this.form.dataAssetApi,
              dataAssetApiMethod: this.path + '/' + this.form.dataAssetApi.dataMethod,
              operationType: this.dataAssetActive,
              responseContentType: this.getResponseContentType,
              apiAttr: {
                ...this.form.dataAssetApi.apiAttr,
                clazzName: apiTypeObj ? apiTypeObj.clazzName : undefined,
              },
            },
          });

          delete apiDeploy.dataMethod;
          delete apiDeploy.updateTime;

          if (this.form.dataAssetApi.apiType !== API_TYPE.HTTP) {
            delete apiDeploy.dataAssetApi.respMappingRule;
          }

          // sql api
          if (apiDeploy.dataAssetApi.apiType !== API_TYPE.HTTP && apiDeploy.dataAssetApi.apiType !== API_TYPE.WS) {
            // delete apiDeploy.dataAssetApi.apiAttr;
            apiDeploy.dataAssetApi.apiAttr = this.$refs.redisLock.redisForm;
            if (apiDeploy.dataAssetApi.apiType === API_TYPE.TABLE) {
              apiDeploy.dataAssetApi.apiAttr.configType = 0;
            } else if (apiDeploy.dataAssetApi.apiType === API_TYPE.SQL) {
              apiDeploy.dataAssetApi.apiAttr.configType = 1;
            }
          } else {
            /**
             * 校验请求头、QUERY参数、响应头
             */
            await this.$refs.apiHttp.validate();
          }

          let action = 'editApi';
          if (!apiDeploy.dataAssetApi.dataAssetApiId) {
            delete apiDeploy.dataAssetApi.dataAssetApiId;
            action = 'newApiAdd';
          }

          /**
           * SQL数据的保存
           */

          if (apiDeploy.dataAssetApi.apiType === API_TYPE.SQL) {
            if (this.sqlParams.request.length) {
              // GET请求
              if (this.form.dataAssetApi.reqMethod === 'GET') {
                apiDeploy.parameters = this.sqlParams.request;
              } else {
                // POST请求
                apiDeploy.parameters = [
                  {
                    assetColumns: 'body',
                    assetDatatype: 'json',
                    httpParamKind: 'BODY',
                    descriptions: '请求体参数',
                    typeAttr: 'OPERATOR',
                    jsonSchema: JSON.stringify(paramsTransformToTree(this.sqlParams.request)),
                  },
                ];
              }
            }
            if (this.sqlParams.response.length) {
              apiDeploy.results = this.sqlParams.response.find(item => item.assetColumns === 'data').children;
              apiDeploy.responseParams = [
                {
                  assetColumns: 'body',
                  assetDataType: 'json',
                  type: 'BODY',
                  responsePostData: JSON.stringify(paramsTransformToTree(this.sqlParams.response)),
                },
              ];
            }
            apiDeploy.dataAssetApi.operationType = this.sqlOperationType;
          }

          const { filters, ...other } = apiDeploy;
          const curFilter = filters.map(o => ({ ...o, typeAttr: 'FILTER', httpParamKind: 'FILTER' }));
          const curOther = other.parameters.map(o => ({ ...o, typeAttr: 'OPERATOR' }));
          const params = {
            ...other,
            parameters: curOther,
          };

          /**
           * 数据表保存
           */
          if (apiDeploy.dataAssetApi.apiType === API_TYPE.TABLE) {
            const data = curOther.map(item => {
              /**
               * 查询操作类型非必填
               */
              if (this.dataAssetActive === this.QUERY_OPERATE.value) {
                return {
                  ...item,
                  required: false,
                };
              }
              return item;
            });

            /**
             * 删除和修改操作类型，至少选择一个请求参数
             */
            if (
              (this.dataAssetActive === this.DELETE_OPERATE.value ||
                this.dataAssetActive === this.UPDATE_OPERATE.value) &&
              !curOther.length
            ) {
              this.$message({
                type: 'error',
                message: '请至少设置一个请求参数',
              });
              return;
            }

            /**
             * 修改操作类型 至少选择一个过滤条件
             */
            if (this.dataAssetActive === this.UPDATE_OPERATE.value) {
              if (!curFilter.length) {
                this.$message({
                  type: 'error',
                  message: '请至少设置一个过滤条件',
                });
                return;
              }
            }

            /**
             * 请求参数处理
             */
            if (apiDeploy.dataAssetApi.reqMethod === 'POST') {
              const isOther = data.filter(item => item.httpParamKind !== 'BODY');
              params.parameters = [
                {
                  assetColumns: 'body',
                  assetDatatype: 'json',
                  httpParamKind: 'BODY',
                  descriptions: '请求体参数',
                  type: 'PARAMETERS',
                  typeAttr: 'OPERATOR',
                  jsonSchema: JSON.stringify(dataTableParametersToTree(isOther, curFilter, this.isUpdateType)),
                },
              ];
            } else {
              params.parameters = this.dataAssetActive === UPDATE_OPERATE.value ? [...data, ...curFilter] : data;
            }
            /**
             * 响应参数处理
             */

            // 查询的操作类型jsonSchema中的data返回数组类型
            if (this.dataAssetActive === QUERY_OPERATE.value) {
              const responseArr = params.results.filter(item => item.assetColumns !== '__ALL__');
              /**
               * 查询操作类型 至少设置一个返回参数
               */
              if (!responseArr.length) {
                this.$message({
                  type: 'error',
                  message: '请至少设置一个返回参数',
                });
                return;
              }
              const baseParams = this.baseResponseParam.map(item => this.responseBase(item));
              params.responseParams = [
                {
                  assetColumns: 'body',
                  assetDataType: 'json',
                  type: 'BODY',
                  responsePostData: JSON.stringify(dataTableResponseTree(baseParams, responseArr)),
                },
              ];
            } else {
              /**
               * 排除的公共参数
               */
              const excludeList = ['pageNo', 'pageSize', 'totalCount'];
              const baseParams = this.baseResponseParam
                .filter(item => !excludeList.includes(item.datasourceTableColumnName))
                .map(item => this.responseBase(item));
              /**
               * 将参数组装成对象
               */
              const resultTree = paramsTransformToTree(baseParams);
              /**
               * 找到data那一层,设置数据类型为object
               */
              const findData = Object.entries(resultTree.root.properties).find(item => item[1].name === 'data');
              resultTree.root.properties[findData[0]].type = 'object';

              params.responseParams = [
                {
                  assetColumns: 'body',
                  assetDataType: 'json',
                  type: 'BODY',
                  responsePostData: JSON.stringify(resultTree),
                },
              ];
            }
          }

          delete params.bodyData;
          if (
            params.dataAssetApi.reqMethod === 'POST' &&
            apiDeploy.dataAssetApi.apiType !== API_TYPE.SQL &&
            apiDeploy.dataAssetApi.apiType !== API_TYPE.TABLE
          ) {
            /**
             * 校验请求体树
             */
            this.isValidateTree(this.form.bodyData.tree);
            // 构建请求体树参数
            const postBody = {
              assetColumns: 'body',
              assetDatatype: this.form.bodyData.bodyType,
              httpParamKind: 'BODY',
              dataAssetId: params.dataAssetApi.dataAssetApiId,
              type: 'PARAMETERS',
              typeAttr: 'OPERATOR',
              jsonSchema: JSON.stringify(this.form.bodyData.tree),
              descriptions: '请求体参数',
              sample: JSON.stringify(schema2json(this.form.bodyData.tree.root.properties)),
            };
            const isOther = curOther.filter(item => item.httpParamKind !== 'BODY');
            params.parameters = [...isOther, postBody];
            /**
             * 校验响应体树
             */
            const responseBodyData = JSON.parse(
              apiDeploy.responseParams.find(o => o.type === 'BODY')?.responsePostData ?? '{}'
            );
            this.isValidateTree(responseBodyData);
          }

          if (apiDeploy.dataAssetApi.apiType === API_TYPE.HTTP && !this.form.dataAssetApi.respMappingRule) {
            params.dataAssetApi.respMappingRule = 0;
          }
          await apiControll[action](params);
          this.$message.success('保存成功');
          this.$emit('saved');
          this.hide();
        } catch (error) {
          this.$message.error(error.message);
        } finally {
          this.loading = false;
        }
      },
      // onSearchEnter(e) {
      //   if (!e) {
      //     e = window.event;
      //   }
      //   if ((e.keyCode || e.which) === 13) {
      //     this.onSearchParams();
      //   }
      // },
      onSearchParams() {
        this.keyword = this.name;
      },

      handleChangeType() {
        if (this.apiInfo.dataAssetApiId) {
          return;
        }
        this.form.results = [];
        this.form.parameters = [];
        this.form.filters = [];
        this.getColumn();
      },
      isValidateTree(data) {
        const root = data?.root;
        if (root?.properties) {
          const res = textUtils.hasEmptyOrMultiName(root.properties);
          if (!res.success) {
            throw new Error('参数名称' + res.message);
          }
        }
      },
      openExample() {
        this.$confirm(sqlPlacholderText, '帮助', {
          showCancelButton: false,
          showConfirmButton: false,
          customClass: 'example',
          dangerouslyUseHTMLString: true,
        });
      },
    },
  };
</script>

<style scoped lang="less">
  /deep/.el-scrollbar__bar.is-horizontal {
    z-index: 111 !important;
  }

  /deep/ .el-dialog__body {
    padding: 10px 20px;
  }

  .api-edit-dialog {
    .page-scrollbar.mac {
      /deep/ .el-scrollbar__wrap {
        max-height: calc(100vh - 230px);
      }
    }

    .page-scrollbar {
      overflow-x: hidden;
      overflow-y: hidden;
      margin-bottom: 20px;

      /deep/ .el-scrollbar__wrap {
        overflow-x: hidden;
        max-height: calc(100vh - 210px);
      }

      .scrollbar-content {
        padding: 10px;
        margin-bottom: 40px;
      }
    }

    &.is-readonly {
      .page-scrollbar {
        /deep/ .el-scrollbar__wrap {
          max-height: calc(100vh - 190px);
        }
      }
    }

    .main-title {
      height: 22px;
      font-size: 16px;
      margin: 10px 0;
      font-weight: 600;
      color: rgba(51, 51, 51, 1);
      line-height: 22px;
    }

    .main-form-item {
      margin-bottom: 30px;
    }

    /deep/ .el-input,
    /deep/ .el-select {
      width: 250px;
    }

    /deep/ .el-textarea {
      width: 900px;
    }

    /deep/ .path-prepend {
      max-width: 80px;
      white-space: nowrap;
      text-overflow: ellipsis;
      overflow: hidden;
    }

    .basic-info {
      max-width: 900px;
    }

    /deep/ .el-form-item {
      width: auto;
      display: inline-block;
    }
  }

  .parameter {
    font-weight: 800;
    margin-bottom: 28px;
  }

  .search {
    padding: 8px 20px;
  }

  /deep/.el-input-number > .el-input {
    width: 130px;
  }

  ::v-deep .example-btn {
    color: #2776fb !important;
  }
</style>
