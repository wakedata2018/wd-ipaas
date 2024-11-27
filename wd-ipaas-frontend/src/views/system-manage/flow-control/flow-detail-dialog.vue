<template>
  <el-dialog
    :visible.sync="isShow"
    :lock-scroll="false"
    :title="title"
    :close-on-click-modal="true"
    class="bd-dialog flow-dialog not-lock-scroll"
    width="600px"
    v-loading="loading"
  >
    <el-form ref="form" label-width="130px" :model="flowForm" style="padding-bottom: 20px">
      <el-form-item label="规则名称" prop="name">
        {{ flowForm.name }}
      </el-form-item>
      <el-form-item label="描述" prop="ruleDesc">
        {{ flowForm.ruleDesc }}
      </el-form-item>

      <el-form-item label="API接口分类" prop="apiGroupId">
        {{ flowForm.apiGroupName }}
      </el-form-item>

      <el-form-item label="API名称" prop="apiId">
        {{ flowForm.apiName }}
      </el-form-item>

      <div>
        <div class="call-text">流量控制规则</div>
        <el-form-item
          label="日调用次数"
          prop="dayLimit"
          v-if="flowForm.dayLimit !== null && flowForm.dayLimit !== undefined"
        >
          {{ flowForm.dayLimit }}
          <span class="call-unit">次</span>
        </el-form-item>
        <el-form-item
          label="月调用次数"
          prop="monthLimit"
          v-if="flowForm.monthLimit !== null && flowForm.monthLimit !== undefined"
        >
          {{ flowForm.monthLimit }}
          <span class="call-unit">次</span>
        </el-form-item>
        <el-form-item
          label="总调用次数"
          prop="totalLimit"
          v-if="flowForm.totalLimit !== null && flowForm.totalLimit !== undefined"
        >
          {{ flowForm.totalLimit }}
          <span class="call-unit">次</span>
        </el-form-item>
        <el-form-item label="每秒最大访问次数" prop="qps" v-if="flowForm.qps !== null && flowForm.qps !== undefined">
          {{ flowForm.qps }}
          <span class="call-unit">次</span>
        </el-form-item>
        <div class="call-text">缓存失效规则</div>
        <el-form-item label="API缓存失效时间" prop="ttl">
          {{ flowForm.ttl }}
          <span class="call-unit">秒</span>
        </el-form-item>
      </div>
    </el-form>
  </el-dialog>
</template>

<script>
  import { Message } from 'element-ui';
  import warnApi from '@/api/warn.js';
  import ruleApi from '@/api/rule.js';
  function getFlowForm() {
    return {
      id: null,
      name: '',
      ruleDesc: '',
      dayLimit: null,
      monthLimit: null,
      totalLimit: null,
      apiGroupId: null,
      apiId: null,
      qps: null,
      ttl: null,
    };
  }
  export default {
    props: {
      visible: {
        type: Boolean,
        default: false,
      },
      flowInfo: {
        type: Object,
        default() {
          return null;
        },
      },
    },
    data() {
      return {
        loading: false,
        flowForm: getFlowForm(),
        apiGroupList: [],
        option: [],
      };
    },
    computed: {
      isShow: {
        get() {
          return this.visible;
        },
        set(val) {
          this.$emit('update:visible', val);
        },
      },
      title() {
        return '流量控制详情';
      },
    },
    watch: {
      isShow(val) {
        this.resetFields();
        if (val) {
          this.setValues();
        }
      },
    },
    mounted() {},
    methods: {
      resetFields() {
        this.flowForm = getFlowForm();
      },
      setValues() {
        if (!this.flowInfo) {
          return;
        }
        for (const key in this.flowInfo) {
          this.$set(this.flowForm, key, this.flowInfo[key]);
        }
      },
      onCancle() {
        this.isShow = false;
      },
    },
  };
</script>

<style lang="less" scoped>
  .flow-dialog {
    .call-text {
      width: 110px;
      text-align: right;
      padding: 0px 12px 10px 0px;
      font-size: 16px;
      color: #333;
      font-weight: bold;
    }
    .call-unit {
      margin-left: 10px;
    }
    /deep/.el-dialog__body {
      padding: 20px 60px;
    }
    /deep/.el-form-item__label {
      color: #2776fb;
      font-weight: 600;
    }
  }
</style>
