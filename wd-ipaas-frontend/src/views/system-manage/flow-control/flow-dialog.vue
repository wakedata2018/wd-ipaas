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
    <el-form ref="form" label-width="auto" :model="flowForm" :rules="rules" style="padding-bottom: 20px">
      <el-form-item label="规则名称" prop="ruleName">
        <el-input v-model="flowForm.ruleName" maxlength="50" placeholder="请输入规则名称" />
      </el-form-item>
      <el-form-item label="描述" prop="ruleDesc">
        <el-input v-model="flowForm.ruleDesc" maxlength="50" :rows="3" placeholder="请输入描述" type="textarea" />
      </el-form-item>
      <el-row>
        <el-col :span="12">
          <el-form-item label="API接口分类" prop="apiGroupId">
            <el-select v-model="flowForm.apiGroupId" placeholder="请选择API接口分类" @change="handleChangeApiGroupId">
              <el-option
                style="height: auto"
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
          <el-form-item label="API名称" prop="apiName">
            <el-select v-model="flowForm.dataAssetApiId" filterable placeholder="请选择API名称">
              <el-option v-for="item in options" :key="item.apiId" :label="item.apiName" :value="item.apiId">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <div>
        <div class="call-text">流量控制规则</div>
        <el-form-item label="日调用次数" prop="dayLimit">
          <el-input-number v-model="flowForm.dayLimit" :min="0" style="width: 200px" placeholder="请输入每秒调用次数" />
          <span class="call-unit">次</span>
        </el-form-item>
        <el-form-item label="月调用次数" prop="monthLimit">
          <el-input-number
            :min="0"
            v-model="flowForm.monthLimit"
            style="width: 200px"
            placeholder="请输入每秒调用次数"
          />
          <span class="call-unit">次</span>
        </el-form-item>
        <el-form-item label="总调用次数" prop="totalLimit">
          <el-input-number :min="0" v-model="flowForm.totalLimit" style="width: 200px" placeholder="请输入总调用次数" />
          <span class="call-unit">次</span>
        </el-form-item>
        <el-form-item label="每秒最大访问次数" prop="qps">
          <el-input-number :min="0" v-model="flowForm.qps" style="width: 200px" placeholder="请输入每秒最大访问次数" />
          <span class="call-unit">次</span>
        </el-form-item>
        <div class="call-text">缓存失效规则</div>
        <el-form-item label="API缓存失效时间" prop="ttl">
          <el-input-number :min="0" v-model="flowForm.ttl" style="width: 200px" placeholder="请输入API缓存失效时间" />
          <span class="call-unit">秒</span>
        </el-form-item>
      </div>
    </el-form>
    <div class="bd-dialog-footer" slot="footer">
      <el-button size="medium" @click="onCancle">取消</el-button>
      <el-button size="medium" type="primary" class="bd-button" @click="onSave">保存</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import warnApi from '@/api/warn.js';
  import ruleApi from '@/api/rule.js';
  import apiGroup from '@/api/api-group.js';
  function getFlowForm() {
    return {
      id: null,
      ruleName: '',
      ruleDesc: '',
      dayLimit: '',
      monthLimit: '',
      totalLimit: '',
      apiGroupId: null,
      dataAssetApiId: null,
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
        options: [],
        defaultProps: {
          children: 'children',
          label: 'groupName',
        },
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
        return !!this.flowInfo;
      },
      title() {
        return this.isEdit ? '编辑流量控制' : '新增流量控制';
      },
      rules() {
        return {
          ruleName: [{ required: true, message: '规则名称不能为空', trigger: 'blur' }],
          apiGroupId: [{ required: true, message: 'API接口分类不能为空', trigger: 'blur' }],
          dataAssetApiId: [{ required: true, message: 'API名称不能为空', trigger: 'blur' }],
          dayLimit: [{ required: true, message: '日调用次数不能为空', trigger: 'change' }],
          monthLimit: [{ required: true, message: '月调用次数不能为空', trigger: 'change' }],
          totalLimit: [{ required: true, message: '总调用次数不能为空', trigger: 'change' }],
          ttl: [
            {
              required: true,
              message: 'API缓存失效时间不能为空',
              trigger: 'change',
            },
          ],
          qps: [
            {
              required: true,
              message: '每秒最大访问次数不能为空',
              trigger: 'change',
            },
          ],
        };
      },
    },
    watch: {
      isShow(val) {
        if (val) {
          this.setValues();
        } else {
          this.resetFields();
        }
      },
      'flowForm.apiGroupId'() {
        this.getApiNameList();
      },
    },
    mounted() {
      this.getApiGroupList();
    },
    methods: {
      handleChangeApiGroupId() {
        this.flowForm.dataAssetApiId = '';
      },
      getApiGroupList() {
        apiGroup.getGroupList().done(res => {
          this.apiGroupList = res.data;
        });
      },
      getApiNameList() {
        const groupId = this.flowForm.apiGroupId;
        if (groupId) {
          warnApi
            .queryApi({ groupId })
            .done(res => {
              this.options = res.data;
            })
            .catch(() => {
              this.options = [];
            });
        }
      },
      resetFields() {
        this.$refs.form?.resetFields();
        this.flowForm = getFlowForm();
      },
      setValues() {
        if (!this.flowInfo) {
          return;
        }
        for (const key in this.flowForm) {
          this.$set(this.flowForm, key, this.flowInfo[key]);
        }
      },
      onCancle() {
        this.isShow = false;
      },
      onSave() {
        const form = this.$refs.form;
        form.validate(valid => {
          if (!valid) {
            return;
          }
          if (!!this.flowInfo && !!this.flowInfo.id) {
            this.update();
          } else {
            this.save();
          }
        });
      },
      save() {
        this.loading = true;
        ruleApi
          .add({ ...this.flowForm })
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
        ruleApi
          .update({ ...this.flowForm })
          .done(res => {
            this.$message.success('保存成功');
            this.$emit('saved');
            this.isShow = false;
          })
          .always(res => {
            this.loading = false;
          });
      },
    },
  };
</script>

<style lang="less" scoped>
  .tree-option {
    padding: 0px !important;
  }

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
  }
</style>
