<template>
  <div>
    <el-tabs v-model="dataAssetActive" type="card" @tab-click="handlerChange">
      <el-tab-pane
        v-for="i in BaseOperate.list"
        :key="i.value"
        :label="i.label"
        :name="i.value"
        :disabled="disabled && dataAssetActive !== i.value"
      ></el-tab-pane>
    </el-tabs>
    <el-form-item label="关键词" style="width: auto; display: inline-block">
      <el-input
        v-model.trim="keyword"
        placeholder="请输入内容"
        maxlength="64"
        clearable
        class="input-with-select"
        @keydown.native="onSearchEnter"
      >
        <el-button slot="append" icon="el-icon-search" @click="initData"></el-button>
      </el-input>
    </el-form-item>

    <el-tabs v-if="dataAssetActive === UPDATE_OPERATE.value" v-model="activeName" class="param_tab">
      <el-tab-pane label="请求参数" name="request"> </el-tab-pane>
      <el-tab-pane label="过滤条件" name="filters">
        <el-table
          :key="`${dataAssetActive}-${activeName}`"
          :data="filterData"
          style="width: 100%"
          class="dss-table bd-table"
        >
          <el-table-column prop="filter" label width="150" :render-header="renderCheckEdit">
            <template #default="{ row }">
              <el-checkbox
                v-model="row.filter"
                :disabled="disabled"
                @change="changeSelected($event, row, filterData)"
              />
            </template>
          </el-table-column>
          <el-table-column
            v-for="tableColumn in tableColumnsList"
            :key="tableColumn.prop"
            :prop="tableColumn.prop"
            :label="tableColumn.label"
          />
          <el-table-column prop="operatorId" label="来源">
            <template #default="{ row }">
              <span class="required">{{ row.isSelected && !isReadonly ? '*' : '' }}</span>
              <el-select
                v-model="row.operatorId"
                placeholder="来源节点"
                :disabled="!row.isSelected || isReadonly"
                @change="changeOperatorId(row)"
              >
                <el-option
                  v-for="item in operationList"
                  :key="item.operatorId"
                  :label="item.desc"
                  :value="item.operatorId"
                ></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column prop="expression" label="表达式">
            <template #default="{ row }">
              <span class="required">{{ row.isSelected && !isReadonly && row.required ? '*' : '' }}</span>
              <el-select
                v-model="row.expression"
                class="expression"
                placeholder="表达式"
                allow-create
                filterable
                :disabled="!row.isSelected || isReadonly"
                @visible-change="handlerVisible($event, row, filterData)"
              >
                <el-option :value="row.expression">
                  <el-tree
                    ref="tree"
                    :data="jsonObject"
                    :props="defaultProps"
                    default-expand-all
                    node-key="key"
                    @node-click="handleNodeClick($event, row)"
                  />
                </el-option>
              </el-select>
            </template>
            <template #header>
              <tips-icon content="例：$.ID"></tips-icon>
              表达式
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <el-table
      v-show="activeName !== 'filters' || dataAssetActive !== UPDATE_OPERATE.value"
      :key="dataAssetActive"
      :data="tableData"
      style="width: 100%"
      class="dss-table bd-table"
    >
      <el-table-column prop="parameter" label width="150" :render-header="renderCheckbox">
        <template #default="{ row }">
          <el-checkbox v-model="row.parameter" :disabled="disabled" @change="changeSelected($event, row, tableData)" />
        </template>
      </el-table-column>

      <el-table-column
        v-if="dataAssetActive === QUERY_OPERATE.value"
        prop="result"
        label
        width="150"
        :render-header="renderCheckboxa"
      >
        <template #default="{ row }">
          <el-checkbox v-model="row.result" :disabled="disabled" @change="changeSelected($event, row, tableData)" />
        </template>
      </el-table-column>
      <el-table-column
        v-for="tableColumn in tableColumnsList"
        :key="tableColumn.prop"
        :prop="tableColumn.prop"
        :label="tableColumn.label"
      />
      <!-- <el-table-column prop="operatorId" label="来源">
        <template #default="{ row }">
          <span class="required">{{ row.isSelected && !isReadonly ? '*' : '' }}</span>
          <el-select
            v-model="row.operatorId"
            placeholder="来源节点"
            :disabled="!row.isSelected || isReadonly || !row.parameter"
            @change="changeOperatorId(row)"
          >
            <el-option
              v-for="item in operationList"
              :key="item.operatorId"
              :label="item.desc"
              :value="item.operatorId"
            ></el-option>
          </el-select>
        </template>
      </el-table-column>
      <el-table-column prop="expression" label="表达式">
        <template #default="{ row }">
          <span class="required">{{ row.isSelected && !isReadonly && row.required ? '*' : '' }}</span>
          <el-select
            v-model="row.expression"
            placeholder="表达式"
            class="expression"
            allow-create
            filterable
            :disabled="!row.isSelected || isReadonly || !row.parameter"
            @visible-change="handlerVisible($event, row, tableData)"
          >
            <el-option :value="row.expression" style="height: auto; padding: 0">
              <el-tree
                ref="tree"
                :data="jsonObject"
                :props="defaultProps"
                default-expand-all
                node-key="key"
                @node-click="handleNodeClick($event, row)"
              />
            </el-option>
          </el-select>
        </template>
        <template #header>
          <tips-icon content="例：$.ID"></tips-icon>
          表达式
        </template>
      </el-table-column> -->

      <el-table-column label="选择方式" width="300px">
        <template #default="scope">
          <SetParamsValue
            v-model="scope.row"
            :table-index="scope.$index"
            :task-info="taskInfo"
            :cur-node="curNode"
          ></SetParamsValue>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
  import TipsIcon from '@/components/tips-icon';
  import Validator from '@/utils/validator';
  import { BaseOperate, QUERY_OPERATE, UPDATE_OPERATE, DELETE_OPERATE } from '@/enum';
  import apiController from '@/api/api-controller';
  import { TYPE_ELE, ApiType } from '@/utils/enum';
  import SetParamsValue from './SetParamsValue.vue';
  import { getIdFromItem } from '../../utils';

  export default {
    components: {
      TipsIcon,
      SetParamsValue,
    },
    props: {
      tableList: {
        type: Array,
        default: () => [],
      },
      config: {
        type: Object,
        default: () => ({
          parameters: [],
          results: [],
        }),
      },
      operationList: {
        type: Array,
        default: () => [],
      },
      isReadonly: {
        type: Boolean,
        default: false,
      },
      isEdit: {
        type: Boolean,
        default: false,
      },
      tableColumns: {
        type: Object,
        default: () => ({
          parameters: [
            {
              prop: 'datasourceTableColumnName',
              label: '字段',
            },
            {
              prop: 'datasourceTableColumnType',
              label: '字段类型',
            },
            {
              prop: 'datasourceTableColumnDesc',
              label: '字段描述',
            },
          ],
          results: [
            {
              prop: 'datasourceTableColumnName',
              label: '字段',
            },
            {
              prop: 'datasourceTableColumnType',
              label: '字段类型',
            },
            {
              prop: 'datasourceTableColumnDesc',
              label: '字段描述',
            },
          ],
        }),
      },
      canChangeSelect: {
        type: Boolean,
        default: true,
      },
      setType: {
        type: String,
        default: '',
      },
      refresh: {
        type: Boolean,
        default: false,
      },
      taskInfo: {
        type: Object,
        default: () => ({}),
      },
      curNode: {
        type: Object,
        default: () => ({}),
      },
    },
    data() {
      const START_ELE = {
        desc: 'start',
        name: 'start',
        operatorId: 'start',
      };
      return {
        TYPE_ELE,
        START_ELE,
        BaseOperate,
        QUERY_OPERATE,
        UPDATE_OPERATE,
        DELETE_OPERATE,
        dataAssetActive: BaseOperate.list[0].value,
        activeName: 'request',
        isIndeterminate: false,
        isIndeterminatea: false,
        isIndeterminatefilter: false,
        keyword: '',
        tableData: [],
        filterData: [],
        treeDataValue: '',
        jsonObject: [],
        defaultProps: {
          children: 'childApiRespParams',
          label: 'assetColumns',
        },
      };
    },
    computed: {
      disabled() {
        return this.isReadonly || this.isEdit;
      },
      tableColumnsList() {
        return this.tableColumns.parameters;
      },
      isCheckedAll: {
        get() {
          const checkeds = this.tableData.filter(item => item.parameter);
          return !!checkeds.length && checkeds.length === this.tableData.length;
        },
        set(val) {
          this.onCheckAllChange(val);
          const checkeds = this.tableData.filter(item => item.parameter);
          this.isIndeterminate = !!checkeds.length && checkeds.length < this.tableData.length;
        },
      },
      isCheckedAlla: {
        get() {
          const checkeds = this.tableData.filter(item => item.result);
          return !!checkeds.length && checkeds.length === this.tableData.length;
        },
        set(val) {
          this.onCheckAllChange(val);
          const checkeds = this.tableData.filter(item => item.result);
          this.isIndeterminatea = !!checkeds.length && checkeds.length < this.tableData.length;
        },
      },
      isCheckedAllFilter: {
        get() {
          const checkeds = this.filterData.filter(item => item.filter);
          return !!checkeds.length && checkeds.length === this.filterData.length;
        },
        set(val) {
          this.onCheckAllChangeFilter(val);
          const checkeds = this.filterData.filter(item => item.filter);
          this.isIndeterminatefilter = !!checkeds.length && checkeds.length < this.filterData.length;
        },
      },
      operatorId() {
        return getIdFromItem(this.curNode);
      },
    },

    watch: {
      tableList(val) {
        this.initData();
      },
      config: {
        handler: function (val) {
          return val;
        },
        deep: true,
        immediate: true,
      },
      refresh() {
        this.dataAssetActive = BaseOperate.list[0].value;
      },
    },
    methods: {
      renderCheckbox(h) {
        return h(
          'el-checkbox',
          {
            props: {
              value: this.isCheckedAll,
              disabled: this.disabled,
              indeterminate: this.isIndeterminate,
            },
            on: {
              change: this.onCheckAllChange,
            },
          },
          [h('strong', {}, [this.dataAssetActive === this.DELETE_OPERATE.value ? '全部' : '设为请求参数'])]
        );
      },
      renderCheckEdit(h) {
        return h(
          'el-checkbox',
          {
            props: {
              value: this.isCheckedAllFilter,
              disabled: this.disabled,
              indeterminate: this.isIndeterminatefilter,
            },
            on: {
              change: this.onCheckAllChangeFilter,
            },
          },
          [h('strong', {}, ['设为过滤条件'])]
        );
      },
      renderCheckboxa(h) {
        return h(
          'el-checkbox',
          {
            props: {
              value: this.isCheckedAlla,
              disabled: this.disabled,
              indeterminate: this.isIndeterminatea,
            },
            on: {
              change: this.onCheckAllChangea,
            },
          },
          [h('strong', {}, ['设为返回参数'])]
        );
      },

      renderSelectboxa(h) {
        return h(
          'el-checkbox',
          {
            props: {
              value: this.isCheckedAlla,
              disabled: this.disabled,
              indeterminate: this.isIndeterminatea,
            },
            on: {
              change: this.onCheckAllChangea,
            },
          },
          [h('strong', {}, ['全选'])]
        );
      },
      changeSelected(selected, row, data) {
        if (!selected) {
          row.type = '';
          row.expression = '';
          row.operatorId = this.START_ELE.operatorId;
        }
        const { parameter, filter, result } = row;
        row.isSelected = !!(parameter || filter || result);
      },
      validate() {
        let index = -1;
        let repeat = false;
        const fieldArr = [];
        const parameters = this.config.parameters;
        for (let i = 0; i < parameters.length > 0; i++) {
          const item = parameters[i];
          if (
            item.isSelected &&
            item.required &&
            (!item.datasourceTableColumnName ||
              !Validator.fieldEnNameValidatorFun(item.datasourceTableColumnName).result ||
              !item.operatorId ||
              !item.expression)
          ) {
            index = i;
            break;
          }
          if (
            fieldArr.indexOf(item.datasourceTableColumnName) !== -1 &&
            this.dataAssetActive !== UPDATE_OPERATE.value
          ) {
            repeat = true;
            break;
          }
          fieldArr.push(item.datasourceTableColumnName);
        }

        const found = index !== -1 || repeat;
        return !found;
      },
      setChange(val, key) {
        this.tableData.forEach(o => {
          o[key] = !!val;
          o.isSelected = !!(o.parameter || o.result);
        });
      },
      onCheckAllChange(val) {
        this.setChange(val, 'parameter');
      },
      onCheckAllChangea(val) {
        this.setChange(val, 'result');
      },
      onCheckAllChangeFilter(val) {
        this.filterData.forEach(o => {
          o.filter = !!val;
          o.isSelected = !!o.filter;
        });
      },
      handlerChange() {
        if (this.disabled) {
          return;
        }
        this.tableData.forEach(o => {
          o.parameter = false;
          o.result = false;
          o.isSelected = false;
          o.type = '';
          o.expression = '';
          o.operatorId = this.START_ELE.operatorId;
        });
        this.filterData.forEach(o => {
          o.filter = false;
          o.isSelected = false;
          o.type = '';
          o.expression = '';
          o.operatorId = this.START_ELE.operatorId;
        });
      },
      onSearchEnter(e) {
        if (!e) {
          e = window.event;
        }
        if ((e.keyCode || e.which) === 13) {
          this.initData();
        }
      },
      initData() {
        const { filters, parameters, results } = this.config;
        const curData = this.tableList.filter(
          item =>
            !this.keyword ||
            this.keyword === '' ||
            item.datasourceTableColumnName?.toUpperCase().indexOf(this.keyword.toUpperCase()) !== -1 ||
            item.datasourceTableColumnType?.toUpperCase().indexOf(this.keyword.toUpperCase()) !== -1 ||
            item.datasourceTableColumnDesc?.toUpperCase().indexOf(this.keyword.toUpperCase()) !== -1
        );
        this.tableData = curData.map((o, k) => ({
          ...o,
          parameter: !!parameters[k].isSelected,
          result: !!results[k].isSelected,
          isSelected: parameters[k].isSelected || results[k].isSelected,
          type: parameters[k].paramsType || '', // 因为用到的组件是默认type ,这里先不动了
          expression: parameters[k].expression || '',
          operatorId: parameters[k].operatorId || this.START_ELE.operatorId,
        }));

        this.dataAssetActive = this.setType || BaseOperate.list[0].value;

        this.filterData = curData.map((o, k) => ({
          ...o,
          filter: !!filters[k].isSelected,
          isSelected: filters[k].isSelected,
          type: filters[k].paramsType,
          expression: filters[k].expression || '',
          operatorId: filters[k].operatorId || this.START_ELE.operatorId,
        }));
      },
      async handleNodeClick(data, row) {
        const arr = Object.values(this.TYPE_ELE).map(o => o.key);
        if (arr.includes(data.key)) {
          return;
        }
        const result = await this.seachVal(data, row);
        row.expression = result;
      },
      async seachVal(data, row) {
        const newParam = {
          reqApiCondition: {
            httpParamKind: data.httpParamKind || 'QUERY',
            assetColumns: row.datasourceTableColumnName,
            descriptions: row.datasourceTableColumnDesc,
          },
        };
        if (row.operatorId === this.START_ELE.operatorId) {
          delete data.type;
          newParam.apiRespParam = data;
        } else {
          const { component } = this.operationList.find(o => o.operatorId === row.operatorId) || {};
          const { apiType } = component?.dataAssetApi || {};

          newParam.apiRespParam = {
            ...data,
            normalTable: apiType === ApiType._normal_table.value,
          };
        }

        try {
          const { data: newData } = await apiController.buildExpression(newParam);
          return newData;
        } catch (error) {
          console.log('🚀 ~ error', error);
        }
      },
      async getEleTreeData(res, data) {
        if (res.operatorId === this.START_ELE.operatorId) {
          const initBody = this.$plain(this.TYPE_ELE.BODY);
          const responseTree = (data?.filter(o => o.isSelected) || []).map(o => ({
            assetColumns: o.datasourceTableColumnName,
            description: o.datasourceTableColumnDesc,
            key: `${o.datasourceTableColumnName}_${Math.floor(Math.random() * 99999)}`,
            type: initBody.type,
            childApiRespParams: [],
          }));
          initBody.childApiRespParams = responseTree;
          this.jsonObject = [
            {
              ...this.TYPE_ELE.JSON_OBJECT,
              childApiRespParams: [initBody],
            },
          ];
        } else {
          const { component } = this.operationList.find(o => o.operatorId === res.operatorId) || {};
          const { dataAssetApiId } = component?.dataAssetApi || {};
          const { data: newData } = await apiController.queryRespParam({ apiId: dataAssetApiId });
          this.setReponseData(newData);
        }
      },
      async setReponseData(data) {
        const headData = this.$plain(this.TYPE_ELE.HEAD);
        const bodyData = this.$plain(this.TYPE_ELE.BODY);

        if (data?.length) {
          const setKey = arr => {
            arr.forEach(o => {
              o.key = `${o.assetColumns}_${Math.floor(Math.random() * 99999)}`;
              if (o.childApiRespParams.length) {
                setKey(o.childApiRespParams);
              }
            });
            return arr;
          };

          const cos = await setKey(data);
          const head = cos.filter(p => p.type === this.TYPE_ELE.HEAD.type);
          const body = cos.filter(p => p.type === this.TYPE_ELE.BODY.type);

          headData.childApiRespParams = head;
          bodyData.childApiRespParams = body;
        }
        this.jsonObject = [
          {
            ...this.TYPE_ELE.JSON_OBJECT,
            childApiRespParams: [headData, bodyData],
          },
        ];
      },
      changeOperatorId(row) {
        row.expression = '';
      },
      handlerVisible(val, row, data) {
        const newData = data.filter(o => o.isSelected && o.parameter);
        if (val) {
          this.getEleTreeData(row, newData);
        }
      },

      saveData() {
        // 数据保存到store中
        const operator = this.taskInfo.dataAssetApi.apiAttr?.operators?.[this.operatorId];
        if (operator) {
          const data = {
            ...operator,
            // requestParamMappings 是表单填的数据，回显时在配对
            requestParamMappings: this.tableData.map(item => ({
              ...item,
              field: item.datasourceTableColumnName, // 到时候匹配数据就是通过这个字段
              paramsType: item.paramsType, // 选择方式
            })),
            component: {
              ...operator.component,
              parameters: this.config.parameters,
            },
          };
          this.$set(this.taskInfo.dataAssetApi.apiAttr.operators, this.operatorId, data);
        }
      },
    },
  };
</script>

<style scoped lang="less">
  @import './style.less';
  .expression {
    .el-select-dropdown__item {
      padding: 0 !important;
    }
  }
</style>
