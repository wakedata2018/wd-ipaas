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
    <el-form
      ref="form"
      label-width="130px"
      :model="alarmForm"
      :rules="rules"
      style="padding-bottom: 20px"
    >
      <el-form-item label="告警名称" prop="name">
        <el-input
          v-model="alarmForm.name"
          maxlength="50"
          placeholder="请输入告警名称"
        />
      </el-form-item>
      <el-form-item label="描述" prop="warnDesc">
        <el-input
          v-model="alarmForm.warnDesc"
          maxlength="150"
          :rows="3"
          placeholder="请输入描述"
          type="textarea"
        />
      </el-form-item>
      <el-row>
        <el-col :span="12">
          <el-form-item label="API接口分类" prop="apiGroupId">
            <el-select
              v-model="alarmForm.apiGroupId"
              placeholder="请选择API接口分类"
              @change="changeApiGroupList"
            >
              <el-option
                v-for="item in apiGroupList"
                :key="item.id"
                :label="item.groupName"
                :value="item.id"
              >
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="API名称" prop="apiId">
            <el-select
              v-model="alarmForm.apiId"
              placeholder="请选择API名称"
              @focus="getApiNameList"
            >
              <el-option
                v-for="item in option"
                :key="item.apiId"
                :label="item.apiName"
                :value="item.apiId"
              >
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <div>
        <div class="call-text">告警规则</div>
        <el-form-item label="访问时长超过" prop="ruleSecond">
          <el-input-number
            v-model.trim="alarmForm.ruleSecond"
            :min="0"
            :max="10000"
            style="width: 200px"
            placeholder="请输入访问时长"
          />
          <span class="call-unit">秒</span>
        </el-form-item>
      </div>
      <el-form-item label="访问异常次数大于" prop="ruleErrorTimes">
        <el-input-number
          :min="0"
          :max="10000"
          v-model.trim="alarmForm.ruleErrorTimes"
          style="width: 200px"
          placeholder="请输入访问异常次数"
        />
        <span class="call-unit">次</span>
      </el-form-item>
      <el-form-item label="接收人" prop="email">
        <el-input
          v-model="alarmForm.email"
          type="textarea"
          placeholder="请输入邮箱"
          :rows="3"
        />
        <div class="email-info">（告警多个邮箱地址以“,”分隔）</div>
      </el-form-item>
      <!-- <el-form-item label="手机号码"  prop="phone">
        <el-input v-model="alarmForm.phone" :rows="3" placeholder="请输入手机号码" type="textarea" />
      </el-form-item> -->
    </el-form>
    <div class="bd-dialog-footer" slot="footer">
      <el-button size="medium" @click="onCancle">取消</el-button>
      <el-button size="medium" type="primary" class="bd-button" @click="onSave"
        >保存</el-button
      >
    </div>
  </el-dialog>
</template>

<script>
import warnApi from "@/api/warn.js";
function getAlarmForm() {
  return {
    name: "",
    warnDesc: "",
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
        this.$emit("update:visible", val);
      },
    },
    isEdit() {
      return !!this.alarmInfo;
    },
    title() {
      return this.isEdit ? "编辑监控告警" : "新增监控告警";
    },
    rules() {
      return {
        name: [
          { required: true, message: "告警名称不能为空", trigger: "blur" },
        ],
        apiGroupId: [
          { required: true, message: "API接口分类不能为空", trigger: "change" },
        ],
        apiId: [
          { required: true, message: "API名称不能为空", trigger: "change" },
        ],
        email: [{ required: true, validator: this.emailListValidator, trigger: 'blur' }],
      };
    },
  },
  watch: {
    isShow(val) {
      this.resetFields();
      if (val) {
        this.setValues();
      }
    },
    "alarmForm.apiGroupId"(val) {
      this.getApiNameList();
    },
  },
  mounted() {
    this.getApiGroupList();
  },
  methods: {
    getApiGroupList() {
      warnApi.queryApiGroup().done((res) => {
        this.apiGroupList = res.data;
      });
    },
    changeApiGroupList() {
      this.alarmForm.apiId = "";
      this.getApiNameList();
    },
    getApiNameList() {
      this.option = [];
      if (this.alarmForm.apiGroupId) {
        const groupId = this.alarmForm.apiGroupId;
        warnApi.queryApi({ groupId }).done((res) => {
          this.option = res.data;
        });
      }
    },
    resetFields() {
      const form = this.$refs.form;
      if (form && form.resetFields) {
        form.resetFields();
      }
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
      // for (const key in this.alarmForm) {
      //   this.$set(this.alarmForm, key, this.alarmInfo[key]);
      // }
    },
    onCancle() {
      this.isShow = false;
    },
    onSave() {
      // console.log(this.alarmForm);
      const form = this.$refs.form;
      form.validate((valid) => {
        if (!valid) {
          return;
        }
        if (!!this.alarmInfo && !!this.alarmInfo.apiWarnId) {
          this.update();
        } else {
          this.save();
        }
      });
    },
    save() {
      this.loading = true;
      warnApi
        .add({ ...this.alarmForm })
        .done((res) => {
          this.$message.success("保存成功");
          this.$emit("saved");
          this.isShow = false;
        })
        .always((res) => {
          this.loading = false;
        });
    },
    update() {
      this.loading = true;
      warnApi
        .edit({ ...this.alarmForm })
        .done((res) => {
          this.$message.success("保存成功");
          this.$emit("saved");
          this.isShow = false;
        })
        .always((res) => {
          this.loading = false;
        });
    },
    emailListValidator(rule, value, callback) {
      const patt = /^([_\-0-9A-Za-z\.]+\@[_\-0-9A-Za-z]+\.[A-Za-z]+\,)*[_\-0-9A-Za-z\.]+\@([_\-0-9A-Za-z]+\.)+[A-Za-z]+$/i;
      if (value == null || value === '') {
        callback(
          new Error(
            '接收人不能为空'
          )
        );
      } else if (!patt.test(value)) {
        callback(
          new Error(
            '邮箱地址必须符合格式，多个邮箱地址以“,”分隔'
          )
        );
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