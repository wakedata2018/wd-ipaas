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
    <el-form ref="form" label-width="130px" :model="alarmForm" style="padding-bottom: 20px">
      <el-form-item label="告警名称" prop="name">
        {{ alarmForm.name }}
      </el-form-item>
      <el-form-item label="描述" prop="warnDesc">
        {{ alarmForm.warnDesc }}
      </el-form-item>

      <el-form-item label="API接口分类" prop="apiGroupId">
        {{ alarmForm.apiGroup }}
      </el-form-item>

      <el-form-item label="API名称" prop="apiId">
        {{ alarmForm.apiName }}
      </el-form-item>

      <div>
        <div class="call-text">告警规则</div>
        <el-form-item label="访问时长超过" prop="ruleSecond">
          {{ alarmForm.ruleSecond }}
          <span class="call-unit">秒</span>
        </el-form-item>
      </div>
      <el-form-item label="访问异常次数大于" prop="ruleErrorTimes">
        {{ alarmForm.ruleErrorTimes }}
        <span class="call-unit">次</span>
      </el-form-item>
      <el-form-item label="接收人" prop="email">
        {{ alarmForm.email }}
      </el-form-item>
      <!-- <el-form-item label="手机号码"  prop="phone">
        <el-input v-model="alarmForm.phone" :rows="3" placeholder="请输入手机号码" type="textarea" />
      </el-form-item> -->
    </el-form>
  </el-dialog>
</template>

<script>
  import warnApi from '@/api/warn.js';
  function getAlarmForm() {
    return {
      name: '',
      warnDesc: '',
      apiGroupId: null,
      apiId: null,
      status: false,
      ruleSecond: null,
      ruleErrorTimes: null,
      phone: null,
      email: null,
    };
  }
  export default {
    props: {
      visible: {
        type: Boolean,
        default: false,
      },
      alarmInfo: {
        type: Object,
        default() {
          return null;
        },
      },
    },
    data() {
      return {
        loading: false,
        alarmForm: getAlarmForm(),
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
        return '监控告警详情';
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
        this.alarmForm = getAlarmForm();
      },
      setValues() {
        if (!this.alarmInfo) {
          return;
        }
        this.alarmForm.name = this.alarmInfo.apiWarnName;
        this.alarmForm.id = this.alarmInfo.apiWarnId;
        this.alarmForm.apiId = this.alarmInfo.apiId;
        this.alarmForm = Object.assign(this.alarmForm, this.alarmInfo);
      },
    },
  };
</script>

<style lang="less" scoped>
  .flow-dialog {
    .call-text {
      width: 130px;
      text-align: right;
      padding: 0px 12px 10px 0px;
      font-size: 16px;
      color: #333;
      font-weight: bold;
    }
    .call-unit {
      margin-left: 10px;
    }
    .email-info {
      font-size: 12px;
      color: #9e9e9e;
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
