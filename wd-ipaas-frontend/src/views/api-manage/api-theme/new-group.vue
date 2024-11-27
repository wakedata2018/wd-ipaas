<template>
  <el-dialog
    class="bd-dialog"
    width="600px"
    :visible.sync="isShow"
    v-loading="loading.saving"
    :close-on-click-modal="false"
    :title="title"
  >
    <el-form label-width="120px" style="padding-bottom: 20px" ref="elForm" :model="form" :rules="rules">
      <el-form-item label="接口分类名称" prop="groupName">
        <el-input placeholder="请输入接口分类名称" maxlength="30" v-model="form.groupName"></el-input>
      </el-form-item>
      <el-form-item label="接口分类编码" prop="groupCode">
        <el-input placeholder="请输入接口分类编码" maxlength="30" v-model="form.groupCode"></el-input>
      </el-form-item>
      <el-form-item prop="groupPath" label="公共路径">
        <template slot="label">
          <tips-icon content="路径的开头和结尾不需要添加'/'"></tips-icon>
          公共路径
        </template>
        <el-input placeholder="请输入公共路径" maxlength="225" v-model="form.groupPath"></el-input>
      </el-form-item>
      <!-- <el-form-item label="上级接口分类" prop="parentId">
        <template slot="label">
          <tips-icon content="接口分类目录不能超过三级,一级接口分类选首层接口分类作为上级接口分类"></tips-icon>
          上级接口分类
        </template>
        <el-select style="width: 100%" v-if="!isEdit" v-model="displayText" placeholder="请选择上级接口分类">
          <el-option :value="displayText" style="height: auto" class="tree-option">
            <el-tree
              ref="tree"
              highlight-current
              :data="treeData"
              :expand-on-click-node="false"
              :props="defaultProps"
              @node-click="changeApiGroup()"
            ></el-tree>
          </el-option>
        </el-select>
        <el-input v-else :disabled="!!isEdit" v-model="displayText"></el-input>
      </el-form-item> -->
      <el-form-item prop="groupDesc" label="接口分类描述">
        <el-input placeholder="接口分类描述" maxlength="225" type="textarea" v-model="form.groupDesc"></el-input>
      </el-form-item>
    </el-form>
    <div class="bd-dialog-footer" slot="footer">
      <el-button size="medium" @click="onCancle">取消</el-button>
      <el-button size="medium" type="primary" class="bd-button" @click="onSave">保存</el-button>
    </div>
  </el-dialog>
</template>

<script>
  import apiGroup from '@/api/api-group.js';
  import TipsIcon from '@/components/tips-icon';
  import treePicker from '@/components/tree-picker';

  export default {
    props: {
      appInfo: {
        type: Object,
        default() {
          return null;
        },
      },
      visible: {
        type: Boolean,
        default: false,
      },
    },
    components: { TipsIcon, treePicker },
    data() {
      const pathTest = (rule, value, callback) => {
        const rules = /^(?!\d+$)[\da-zA-Z]+$/;
        if (value === '') {
          callback(new Error('公共路径不能为空'));
        } else if (value.length > 30) {
          callback(new Error('公共路径过长,不能超过30个字符'));
        } else if (!rules.test(value)) {
          callback(new Error('公共路径格式有误，不能包含特殊字符和中文字符，不能只是数字'));
        } else {
          callback();
        }
      };

      const themeName = (rule, value, callback) => {
        if (value === '') {
          callback(new Error('接口分类名不能为空'));
        } else {
          callback();
        }
      };
      return {
        loading: {
          saving: false,
        },
        form: {
          groupName: '',
          groupPath: '',
          groupDesc: '',
          // parentId: '',
          // level: null,
          // inCharge: [],
          groupCode: '',
        },
        rules: {
          groupName: [{ required: true, validator: themeName, trigger: 'blur' }],
          // groupDesc: [{ validator: desc, trigger: 'blur' }],
          groupPath: [{ required: true, validator: pathTest, trigger: 'blur' }],
          // parentId: [{ required: true, validator: parentId, trigger: 'blur' }],
          groupCode: [{ required: true, message: '请输入接口分类编码', trigger: 'blur' }],
        },
        treeData: [],
        defaultProps: {
          children: 'children',
          label: 'groupName',
        },
        displayText: '',
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
        return !!this.appInfo;
      },
      title() {
        return this.isEdit ? '编辑接口分类' : '新增接口分类';
      },
    },
    watch: {
      isShow: {
        immediate: true,
        handler(val) {
          this.resetFields();
          // this.getTreeData();
          if (val) {
            this.setValues();
          }
        },
      },
    },
    mounted() {
      window.scrollTo(0, 0);
    },
    methods: {
      resetFields() {
        const elForm = this.$refs.elForm;
        if (elForm && elForm.resetFields) {
          elForm.resetFields();
        }
        this.displayText = '';
        this.form.groupName = '';
        this.form.groupPath = '';
        this.form.groupDesc = '';
        this.form.groupCode = '';
        // this.form.parentId = null;
        // this.form.level = null;
        // this.form.inCharge = [];
      },
      setValues() {
        if (!this.appInfo) {
          return;
        }
        for (const key in this.form) {
          this.$set(this.form, key, this.appInfo[key]);
        }
        this.getFlatData(this.treeData);
      },
      changeApiGroup() {
        const res = this.$refs.tree.getCurrentNode();
        if (res) {
          this.displayText = res.groupName;
          // this.form.parentId = res.id;
          // 顶级接口分类level默认为0，其余接口分类为父级接口分类+1
          // if (res.level != null) {
          //   this.form.level = res.level + 1;
          // } else {
          //   this.form.level = 0;
          // }
        }
      },
      getFlatData(arr) {
        return arr.find(item => {
          if (this.form.parentId === 0) {
            this.displayText = '首层接口分类';
            return item;
          }
          if (this.form.parentId === item.id) {
            this.displayText = item.groupName;
          } else {
            this.getFlatData(item.children);
          }
          return item;
        });
      },
      // getLevel(level) {
      //   // 顶级接口分类level默认为0，其余接口分类为父级接口分类+1
      //   if (level != null) {
      //     this.form.level = level + 1;
      //   } else {
      //     this.form.level = 0;
      //   }
      // },
      // getTreeData() {
      //   apiGroup.apiGroupList().done(res => {
      //     const headTheme = [
      //       {
      //         id: 0,
      //         level: null,
      //         parentId: 0,
      //         groupName: '首层接口分类',
      //         children: [],
      //       },
      //     ];
      //     this.treeData = headTheme.concat(res.data || []);
      //   });
      // },
      onCancle() {
        this.isShow = false;
      },
      onSave() {
        const elForm = this.$refs.elForm;
        elForm.validate(valid => {
          if (!valid) {
            return;
          }
          if (!!this.appInfo && !!this.appInfo.id) {
            this.update();
          } else {
            this.add();
          }
        });
      },
      add() {
        if (this.form.level >= 3) {
          this.$message.warning('接口分类目录不能超过三级，请重新选择上级接口分类！');
          return;
        }
        this.loading.saving = true;
        apiGroup
          .addTree(this.toParams(this.form))
          .done(res => {
            this.$message.success('保存成功');
            this.$emit('saved', 'create', this.form, res.data);
            this.isShow = false;
          })
          .always(res => {
            this.loading.saving = false;
          });
      },
      update() {
        this.loading.saving = true;
        const parmas = {
          id: this.appInfo.id,
          ...this.toParams(this.form),
        };
        apiGroup
          .updateTree(parmas)
          .done(res => {
            this.$emit('saved', 'edit', this.form, this.appInfo);
            this.$message.success('保存成功');
            this.isShow = false;
          })
          .always(res => {
            this.loading.saving = false;
          });
      },
      toParams() {
        const params = this.$plain(this.form);
        return params;
      },
    },
  };
</script>
<style lang="less" scoped>
  .tree-option {
    padding: 0px !important;
  }
  /deep/.el-dialog__body {
    padding: 20px 60px;
  }
</style>
