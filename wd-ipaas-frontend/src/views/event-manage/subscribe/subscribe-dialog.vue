<template>
  <el-dialog
    :visible.sync="isShow"
    :lock-scroll="false"
    :title="title"
    :close-on-click-modal="false"
    class="bd-dialog flow-dialog not-lock-scroll"
    width="750px"
    v-loading="loading"
  >
    <el-form ref="form" label-width="130px" :model="alarmForm" :rules="rules" style="padding-bottom: 20px">
      <el-form-item label="Topic名称" prop="topic">
        <el-input v-model="alarmForm.topic" maxlength="50" placeholder="请输入Topic名称,仅支持字母、数字、下划线" />
      </el-form-item>
      <el-form-item label="Topic描述" prop="desc">
        <el-input v-model="alarmForm.desc" maxlength="150" :rows="3" placeholder="请输入描述" type="textarea" />
      </el-form-item>
      <el-form-item label="Tag" prop="tag" v-if="addressType === MQ_TYPE.value">
        <el-input v-model="alarmForm.tag" type="textarea" placeholder="请输入tag" :rows="3" />
        <div class="email-info">如需订阅某Topic下所有类型的消息，Tag用星号（*）表示</div>
      </el-form-item>
      <!-- <el-form-item>抱歉，该Topic正在被API:xx名称 使用，不可修改或删除</el-form-item> -->
    </el-form>

    <div class="bd-dialog-footer" slot="footer">
      <el-button size="medium" @click="onCancle">取消</el-button>
      <el-button size="medium" type="primary" class="bd-button" @click="onSave">保存</el-button>
      <!-- <el-button size="medium" type="primary" class="bd-button" @click="onCancle">我知道了</el-button> -->
    </div>
  </el-dialog>
</template>

<script>
  import requester from '@/api/event-manage';
  import { MQ_TYPE } from '@/enum';

  function getAlarmForm() {
    return {
      topic: '',
      desc: '',
      tag: '',
      // apiGroupId: null,
      // apiId: null,
      // status: false,
      // ruleSecond: null,
      // ruleErrorTimes: null,
      // phone: null,
      // email: null,
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
      addressType: {
        type: Number,
        default: 1,
      },
    },
    data() {
      const CheckTopic = (rule, value, callback) => {
        const regRule = /^[_\-0-9a-zA-Z]+$/;
        if (value === '') {
          callback(new Error('告警名称不能为空'));
        } else if (!regRule.test(value)) {
          callback(new Error('topic名称不符合规则，仅仅支持字母、数字以及下划线'));
        } else {
          callback();
        }
      };
      return {
        MQ_TYPE,
        loading: false,
        alarmForm: getAlarmForm(),
        apiGroupList: [],
        option: [],
        rules: { topic: [{ required: true, trigger: 'blur', validator: CheckTopic }] },
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
      isEdit() {
        return !!this.alarmInfo;
      },
      title() {
        return this.isEdit ? '编辑Topic' : '新增Topic';
      },
    },
    watch: {
      isShow(val) {
        this.resetFields();
        if (val) {
          this.setValues();
        }
      },
      // 'alarmForm.apiGroupId'(val) {
      //   this.getApiNameList();
      // },
    },
    mounted() {
      // this.getApiGroupList();
    },
    methods: {
      // getApiGroupList() {
      //   warnApi.queryApiGroup().done(res => {
      //     this.apiGroupList = res.data;
      //   });
      // },
      // changeApiGroupList() {
      //   this.alarmForm.apiId = '';
      //   this.getApiNameList();
      // },
      // getApiNameList() {
      //   this.option = [];
      //   if (!!this.alarmForm.apiGroupId) {
      //     const groupId = this.alarmForm.apiGroupId;
      //     warnApi.queryApi({ groupId }).done(res => {
      //       this.option = res.data;
      //     });
      //   }
      // },
      resetFields() {
        const form = this.$refs.form;
        if (form && form.resetFields) {
          form.resetFields();
        }
        this.alarmForm = getAlarmForm();
      },
      setValues() {
        if (!this.alarmInfo) return;
        // this.alarmForm.name = this.alarmInfo.apiWarnName;
        // this.alarmForm.id = this.alarmInfo.apiWarnId;
        // this.alarmForm.apiId = this.alarmInfo.apiId;
        // this.alarmForm = Object.assign(this.alarmForm, this.alarmInfo);
        for (const key in this.alarmForm) {
          this.$set(this.alarmForm, key, this.alarmInfo[key]);
        }
      },
      onCancle() {
        this.isShow = false;
      },
      onSave() {
        const form = this.$refs.form;
        form.validate(valid => {
          if (!valid) return;
          if (!!this.alarmInfo && !!this.alarmInfo.id) {
            this.update();
          } else {
            this.save();
          }
        });
      },
      save() {
        this.loading = true;
        requester
          .addSubscribeAddress({ ...this.alarmForm, addressType: this.addressType })
          .done(res => {
            this.$message.success('保存成功');
            this.$emit('saved');
            this.isShow = false;
          })
          .always(res => {
            this.loading = false;
          });
      },
      update() {
        this.loading = true;
        requester
          .updateSubscribeAddress({ ...this.alarmForm, id: this.alarmInfo.id, addressType: this.addressType })
          .done(res => {
            this.$message.success('保存成功');
            this.$emit('saved');
            this.isShow = false;
          })
          .always(res => {
            this.loading = false;
          });
      },
      emailListValidator(rule, value, callback) {
        const patt =
          /^([_\-0-9A-Za-z\.]+\@[_\-0-9A-Za-z]+\.[A-Za-z]+\,)*[_\-0-9A-Za-z\.]+\@([_\-0-9A-Za-z]+\.)+[A-Za-z]+$/i;
        if (value == null || value === '') {
          callback(new Error('接收人不能为空'));
        } else if (!patt.test(value)) {
          callback(new Error('邮箱地址必须符合格式，多个邮箱地址以“,”分隔'));
        } else {
          callback();
        }
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
  }
</style>
