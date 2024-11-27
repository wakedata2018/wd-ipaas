<template>
  <el-form inline label-position="right" label-width="180px" :model="form" :rules="rules" ref="base" class="base-info">
    <el-form-item label="使用状态" class="base-info-item" v-if="isUsing">
      <span slot="label"> 使用状态 </span>
      <el-tag type="danger">使用中</el-tag>
      <span class="tips">(使用中的事件不允许修改)</span>
    </el-form-item>
    <!-- 事件编码在编辑时候不能修改 -->
    <el-form-item label="事件编码" class="base-info-item" prop="code">
      <span slot="label"> 事件编码 </span>
      <el-input
        v-model.trim="form.code"
        placeholder="Ipaas-请输入事件编码后缀，只能填写英文"
        :disabled="disabled"
      ></el-input>
    </el-form-item>
    <el-form-item label="状态" class="base-info-item" prop="status">
      <span slot="label">
        状态
        <el-tooltip class="item" effect="dark" placement="top">
          <div slot="content">禁用：禁用事件后，将不会接收事件数据<br />启用：启用事件后，将接收事件数据</div>
          <i class="el-icon-warning-outline"></i>
        </el-tooltip>
      </span>
      <el-radio-group v-model="form.status" :disabled="disabled">
        <el-radio :label="1">启用</el-radio>
        <el-radio :label="0">禁用</el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="事件名称" class="base-info-item" prop="name">
      <el-input v-model.trim="form.name" placeholder="只能填写英文且不超过32位" :disabled="disabled"></el-input>
    </el-form-item>
    <el-form-item label="创建人/创建时间" class="base-info-item" :disabled="disabled"
      >{{ form.createUserName }}&nbsp;/&nbsp;{{ form.createTime }}
    </el-form-item>
    <el-form-item label="事件描述" class="base-info-item" prop="remark">
      <el-input
        v-model="form.remark"
        type="textarea"
        placeholder="请输入事件描述"
        :disabled="disabled"
        :rows="3"
      ></el-input>
    </el-form-item>
    <el-form-item label="最后修改人/修改时间" class="base-info-item"
      >{{ form.updateUserName }}&nbsp;/&nbsp;{{ form.updateTime }}
    </el-form-item>
    <el-form-item label="事件类型" class="base-info-item">
      <div class="type-ipass">Ipaas</div>
    </el-form-item>
    <el-form-item class="base-info-item"> </el-form-item>
    <el-form-item label="消息最大长度" class="base-info-item" prop="messageMaxLength">
      <el-input
        v-model.number="form.messageMaxLength"
        placeholder="请输入消息最大长度"
        type="number"
        :disabled="disabled"
      >
        <span slot="suffix"> KB </span>
      </el-input>
      <div class="desc-tip">范围在1-1024KB</div>
    </el-form-item>
    <el-form-item label="事件分析拓展点" class="base-info-item" prop="eventExpandPoint">
      <el-input
        v-model="form.eventExpandPoint"
        type="textarea"
        placeholder="请输入回调地址"
        :disabled="disabled"
        :rows="3"
      ></el-input>
    </el-form-item>
  </el-form>
</template>

<script>
  // import requester from 'requester';

  export default {
    props: {
      info: {
        type: Object,
        default: () => {
          return {};
        },
      },
      isEdit: {
        type: Boolean,
        default: true,
      },
      isUsing: {
        type: Boolean,
        default: false,
      },
    },
    computed: {
      disabled() {
        return !this.isEdit || this.isUsing;
      },
    },
    data() {
      const checkName = (rule, value, callback) => {
        if (!value) return callback(new Error('请输入事件名称'));
        if (!/^[\u4E00-\u9FA5A-Za-z0-9_]{2,20}$/.test(value))
          return callback(new Error('事件名称2-20位且不包含特殊字符'));
        return callback();
      };
      const checkMessageMax = (rule, value, callback) => {
        if (!value) return callback(new Error('请输入消息最大长度'));
        if (value <= 1024 && value >= 1) return callback();
        else return callback(new Error('消息最大长度必须在1-1024区间选择'));
      };
      const checkCode = (rule, value, callback) => {
        if (!value) return callback(new Error('请输入事件编码'));
        if (!/^[a-zA-Z_]{6,36}$/.test(value)) return callback(new Error('事件编码6-36位且不包含特殊字符'));
        return callback();
      };

      return {
        form: {
          code: '', // 事件编码
          name: '', // 事件名称
          status: 1, // 是否启用[0-关闭；1-启用]
          messageMaxLength: '', // 消息最大长度，单位KB
          remark: '',
          eventExpandPoint: '',
        },
        rules: {
          code: [{ required: true, validator: checkCode.bind(this), trigger: 'blur' }],
          name: [{ required: true, validator: checkName.bind(this), trigger: 'blur' }],
          messageMaxLength: [{ required: true, validator: checkMessageMax.bind(this), trigger: 'blur' }],
        },
        eventType: [],
      };
    },
    watch: {
      info: {
        handler(val) {
          if (!val) return;
          Object.assign(this.form, val);
        },
        deep: true,
      },
    },
    methods: {
      getFormData() {
        const _this = this;
        return new Promise((res, rej) => {
          _this.$refs.base.validate(valid => {
            if (valid) {
              const newData = _this.$plain(_this.form);
              if (newData.createTime || newData.updateTime || newData.eventSourceAddress) {
                delete newData.createTime;
                delete newData.updateTime;
              }
              res(newData);
            } else {
              rej(valid);
            }
          });
        });
      },
    },
  };
</script>
<style lang="less" scoped>
  .base-info {
    margin: 40px 0;
    max-width: 85%;
    &-item {
      width: 480px;
      margin-right: 30px;
      .type-ipass {
        padding-left: 2px;
      }
    }
    /deep/ .el-form-item__content,
    .el-select {
      width: 300px;
    }
    /deep/ .el-form-item__label {
      padding-right: 20px;
    }
    /deep/ .el-textarea__inner {
      resize: none;
    }
    /deep/ .el-input.is-disabled {
      .el-input__inner {
        background-color: #f5f7fa;
        color: #c0c4cc;
      }
    }
    .tips {
      margin-left: 10px;
    }
  }
</style>
