<template>
  <div>
    <el-form label-position="right" label-width="180px" :model="form" ref="form" :rules="rules" class="origin-info">
      <div class="checkbox-item">
        <el-radio v-model="form.addressType" :label="KAFKA_TYPE.value" :disabled="disabled || isMqEdit">{{
          KAFKA_TYPE.label
        }}</el-radio>
        <div class="desc-tip">
          <el-form-item prop="bootstrapServers">
            <el-input
              v-model="form.bootstrapServers"
              placeholder="集群地址(多个以逗号拼接)"
              :disabled="disabled || isMqEdit"
            ></el-input>
          </el-form-item>

          <div class="desc-title">
            <div class="topic-title">Topic（Topic名称长度不得超过64个字符，否则会导致无法发送或者订阅）</div>
          </div>
          <div class="topic-box" v-for="(i, k) in kafkaTopic" :key="i.id">
            <el-form-item prop="kafkaTopic">
              <el-input
                v-model="i.topic"
                placeholder="Topic"
                :disabled="disabled || isMqEdit"
                @blur="$refs.form.validateField('kafkaTopic')"
              ></el-input>
            </el-form-item>

            <div v-if="isEdit && !isUsing && !isMqEdit" class="plus-box">
              <div class="plus" @click="toAdd(kafkaTopic, i)" v-if="k === kafkaTopic.length - 1">+</div>
              <div class="plus" @click="toRemove(kafkaTopic, k, i)" v-if="kafkaTopic.length > 1">-</div>
            </div>
          </div>
        </div>
      </div>
      <div class="checkbox-item">
        <el-radio v-model="form.addressType" :label="MQ_TYPE.value" :disabled="disabled || isKafkaEdit">{{
          MQ_TYPE.label
        }}</el-radio>
        <div class="desc-tip">
          <el-form-item prop="mqServers">
            <el-input
              v-model="form.mqServers"
              placeholder="集群地址(多个以逗号拼接)"
              :disabled="disabled || isKafkaEdit"
            ></el-input>
          </el-form-item>
          <div class="desc-title">
            <div class="topic-title">Topic</div>
            <div class="tag-title">Tag</div>
          </div>
          <div class="topic-box" v-for="(i, k) in mqTopic" :key="i.id">
            <el-form-item prop="mqTopic">
              <el-input
                v-model="i.topic"
                placeholder="Topic"
                :disabled="disabled || isKafkaEdit"
                @blur="$refs.form.validateField('mqTopic')"
              ></el-input>
              <el-input v-model="i.tag" placeholder="tag" :disabled="disabled || isKafkaEdit" class="tag"></el-input>
            </el-form-item>
            <div v-if="isEdit && !isUsing && !isKafkaEdit" class="plus-box">
              <div class="plus" @click="toAdd(mqTopic, i)" v-if="k === mqTopic.length - 1">+</div>
              <div class="plus" @click="toRemove(mqTopic, k, i)" v-if="mqTopic.length > 1">-</div>
            </div>
          </div>
        </div>
      </div>
    </el-form>
  </div>
</template>
<script>
  import { MQ_TYPE, KAFKA_TYPE } from '@/enum';

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
    data() {
      const checkKafkaTopic = (rule, value, callback) => {
        const kafkaTopicItem = this.kafkaTopic.filter(item => !item.topic);
        if (this.form.addressType === KAFKA_TYPE.value && kafkaTopicItem?.length) {
          return callback(new Error('有未输入的topic'));
        } else {
          return callback();
        }
      };

      const checkMqTopic = (rule, value, callback) => {
        const mqTopicItem = this.mqTopic.filter(item => !item.topic);
        const mqTagItem = this.mqTopic.filter(item => !item.tag);
        if (this.form.addressType === MQ_TYPE.value && mqTopicItem?.length) {
          return callback(new Error('有未输入的topic'));
        } else if (this.form.addressType === MQ_TYPE.value && mqTagItem?.length) {
          return callback(new Error('有未输入的tag'));
        } else {
          return callback();
        }
      };

      const checkBootstrapServers = (rule, value, callback) => {
        if (!value && this.form.addressType === KAFKA_TYPE.value) {
          return callback(new Error('请输入集群地址'));
        } else {
          return callback();
        }
      };

      const checkMqServers = (rule, value, callback) => {
        if (!value && this.form.addressType === MQ_TYPE.value) {
          return callback(new Error('请输入地址'));
        } else {
          return callback();
        }
      };

      return {
        KAFKA_TYPE,
        MQ_TYPE,
        select: [],
        selectObject: [],
        form: {
          mqServers: '', // mq地址
          bootstrapServers: '', // kafka地址
          addressType: KAFKA_TYPE.value,
          enable: true,
          eventType: null,
        },
        kafkaTopic: [
          {
            topic: '', // kafka的topic
            tag: '', // kafka的tag
          },
        ],
        mqTopic: [
          {
            topic: '', // mq的topic
            tag: '', // mq的tag
          },
        ],
        rules: {
          kafkaTopic: [{ required: false, validator: checkKafkaTopic.bind(this) }],
          mqTopic: [{ required: false, validator: checkMqTopic.bind(this) }],
          bootstrapServers: [{ required: false, validator: checkBootstrapServers.bind(this), trigger: 'blur' }],
          mqServers: [{ required: false, validator: checkMqServers.bind(this), trigger: 'blur' }],
        },
        id: '',
        // isUsing: false,
      };
    },
    computed: {
      isKafkaEdit() {
        return this.form.addressType === KAFKA_TYPE.value && !!this.id;
      },
      isMqEdit() {
        return this.form.addressType === MQ_TYPE.value && !!this.id;
      },
      disabled() {
        return !!this.id && (!this.isEdit || this.isUsing);
      },
    },
    watch: {
      form: {
        handler() {
          this.handleChange();
        },
        deep: true,
      },
      'form.bootstrapServers'() {
        if (this.form.addressType !== KAFKA_TYPE.value) this.form.addressType = KAFKA_TYPE.value;
        this.handleChange();
      },
      'form.mqServers'() {
        if (this.form.addressType !== MQ_TYPE.value) this.form.addressType = MQ_TYPE.value;
        this.handleChange();
      },
      kafkaTopic: {
        handler() {
          if (this.form.addressType !== KAFKA_TYPE.value) this.form.addressType = KAFKA_TYPE.value;
          this.handleChange();
        },
        deep: true,
      },
      mqTopic: {
        handler() {
          if (this.form.addressType !== MQ_TYPE.value) this.form.addressType = MQ_TYPE.value;
          this.handleChange();
        },
        deep: true,
      },
      info: {
        handler(val) {
          if (!val) return;
          const { eventSourceAddress, id } = this.$plain(val);
          this.id = id || '';
          // this.isUsing = subscribeStatus;
          eventSourceAddress?.forEach(o => {
            const { bootstrapServers, topicInfos } = o;
            topicInfos.forEach(topicItem => {
              this.form.addressType = topicItem.addressType;
              if (topicItem.addressType === KAFKA_TYPE.value) {
                this.form.bootstrapServers = bootstrapServers;
                this.kafkaTopic = topicInfos;
              } else {
                this.form.mqServers = bootstrapServers;
                this.mqTopic = topicInfos;
              }
            });
          });
          this.handleChange();
        },
        deep: true,
      },
    },
    methods: {
      handleChange() {
        const params = [];
        const addressType = this.form.addressType || null;

        // form里面每一项的key
        if (this.id) {
          const keys = this.info?.eventSourceAddress[0].topicInfos.map(item => item.id) || [];

          // 把form里面的值赋给topic
          if (this.form.addressType === KAFKA_TYPE.value) {
            this.kafkaTopic.forEach((item, index) => {
              this.kafkaTopic[index].id = keys[index];
            });
          } else {
            this.mqTopic.forEach((item, index) => {
              this.mqTopic[index].id = keys[index];
            });
          }
        }

        // 往每个topic里面塞addressType，后端需求
        this.kafkaTopic.forEach(item => {
          item.addressType = addressType;
        });
        this.mqTopic.forEach(item => {
          item.addressType = addressType;
        });

        // 把需要的参数都push进params里
        // eventSourceAddress[0]只取第一项，后端传值类型错误问题
        let id = null;
        if (this.id) {
          id = this.info.eventSourceAddress[0].id || 0;
        }
        if (addressType === KAFKA_TYPE.value) {
          params.push({
            bootstrapServers: this.form.bootstrapServers,
            topicInfos: this.kafkaTopic,
            enable: true,
            eventType: addressType,
          });
        } else {
          params.push({
            bootstrapServers: this.form.mqServers,
            topicInfos: this.mqTopic,
            enable: true,
            eventType: addressType,
          });
        }
        if (this.id) {
          params[0].id = id;
          this.$refs.form.validateField('kafkaTopic');
          this.$refs.form.validateField('bootstrapServers');
          this.$refs.form.validateField('mqTopic');
          this.$refs.form.validateField('mqServers');
        }

        this.selectObject = params;
      },
      getFormData() {
        const _this = this;
        return new Promise((res, rej) => {
          _this.$refs.form.validate(valid => {
            if (valid) {
              const newData = _this.$plain(_this.selectObject);
              res(newData);
            } else {
              rej(valid);
            }
          });
        });
      },
      toAdd(row, item) {
        row.push({ topic: '', tag: '' });
      },
      toRemove(row, idx, item) {
        row.splice(idx, 1);
      },
    },
  };
</script>
<style lang="less" scoped>
  .origin-info {
    margin: 40px 0;
    .info-item {
      display: flex;
      justify-content: space-between;
      align-items: top;
    }
    /deep/ .el-form-item__label {
      padding-right: 20px;
    }
    /deep/ .el-checkbox {
      margin-bottom: 30px;
      width: 110px;
      height: 20px;
      .el-checkbox__label {
        flex: 1;
      }
    }
    .desc-title {
      display: flex;
      width: 500px;
      height: 28px;
      font-size: 14px;
      color: #606266;
      .topic-title {
        flex: 1;
        padding-left: 4px;
      }
      .tag-title {
        flex: 1;
        padding-left: 3px;
      }
    }
    .checkbox-item {
      display: flex;
      margin-bottom: 26px;
      /deep/ .el-radio {
        padding-top: 6px;
        width: 80px;
        margin-right: 0px;
      }
      /deep/ .el-form-item__content {
        margin-left: 0 !important;
      }
    }
    /deep/ .el-input,
    .el-textarea {
      margin-bottom: 10px;
      display: block;
      width: 500px;
    }
    /deep/ .el-input.is-disabled {
      .el-input__inner {
        background-color: #f5f7fa;
        color: #c0c4cc;
      }
    }
    /deep/ .el-textarea__inner {
      resize: none;
      &::-webkit-scrollbar {
        width: 14px;
      }
    }
    .topic-box {
      /deep/ .el-input {
        display: inline-block;
        width: 245px;
        margin-right: 5px;
      }
      .plus-box {
        display: inline-block;
        margin-left: 10px;
      }
      .plus {
        border: 1px solid #ccc;
        border-radius: 50%;
        width: 26px;
        height: 26px;
        text-align: center;
        font-size: 16px;
        cursor: pointer;
        line-height: 26px;
        display: inline-block;
        margin-left: 10px;
      }
      /deep/ .el-form-item {
        display: inline-block;
      }
    }
  }
</style>
