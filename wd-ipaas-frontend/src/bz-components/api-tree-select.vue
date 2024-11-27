<template>
  <span class="tree-select">
    <el-select
      v-model="displayName"
      v-loading="apiGroupList.loading"
      placeholder="API接口分类"
      ref="select"
      :popper-append-to-body="false"
      :disabled="disabled"
      :clearable="canClear"
    >
      <el-option :value="displayName" style="height: auto; width: 100%" class="tree-option">
        <el-tree
          ref="tree"
          node-key="id"
          :data="apiGroupList.list"
          :expand-on-click-node="false"
          :props="defaultProps"
          @node-click="handleNodeClick"
        ></el-tree>
      </el-option>
    </el-select>
  </span>
</template>

<script>
  import apiGroup from '@/api/api-group.js';
  import { getTreeCurrentObj, getPathData } from '@/utils/tree-utils';

  export default {
    props: {
      value: {
        type: Number,
        default: null,
      },
      disabled: {
        type: Boolean,
        default: false,
      },
      refresh: {
        type: Boolean,
        default: false,
      },
      canClear: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        groupName: '',
        apiGroupList: {
          list: [],
          loading: false,
        },
        defaultProps: {
          children: 'children',
          label: 'groupName',
        },
        idProp: 'id',
        pathProp: 'groupPath',
      };
    },
    computed: {
      valueData: {
        get() {
          return this.value;
        },
        set(val) {
          this.$emit('update:value', val);
        },
      },
      curDataObj() {
        return this.getCurDataObj();
      },
      valueDataId() {
        return this.curDataObj && this.curDataObj.id;
      },
      displayName: {
        get() {
          return (this.curDataObj && this.curDataObj.groupName) || this.value;
        },
        set(val) {
          if (val) this.$emit('update:value', val);
          else this.$emit('update:value', null);
        },
      },
    },
    watch: {
      refresh: {
        immediate: true,
        handler(val) {
          this.getApiGroupList();
        },
      },
    },
    created() {
      // this.getApiGroupList();
    },
    methods: {
      refreshPath(isInit = false) {
        this.$nextTick(() => {
          this.$emit('change', this.getCurDataObj(), isInit);
        });
      },
      handleNodeClick(curr) {
        // if (curr.disabled || !curr.isLeaf) return; // 舍弃禁用的节点
        this.$refs.tree.setCheckedKeys([getPathData(curr, this.idProp)]);
        // this.displayName = curr.label;
        this.valueData = getPathData(curr, this.idProp);
        this.refreshPath(false);
        this.$nextTick(() => {
          this.$refs.select.handleClose();
        });
      },
      getCurDataObj() {
        const curDataObj = getTreeCurrentObj(this.apiGroupList.list, this.valueData, {
          valueProp: this.idProp,
          pathProp: this.pathProp,
        });
        return curDataObj;
      },
      getApiGroupList() {
        this.apiGroupList.loading = true;
        apiGroup
          .getThemeList()
          .done(res => {
            this.apiGroupList.list = res.data || [];
            this.refreshPath(true);
          })
          .always(() => {
            this.apiGroupList.loading = false;
          });
      },
    },
  };
</script>

<style scoped lang="less">
  .tree-option {
    padding: 0px !important;
  }
  .tree-select {
    position: relative;
    display: inline-block;
    width: 100%;
  }
  .el-select {
    position: relative;
    display: inline-block;
    width: 100%;
    /deep/.el-scrollbar {
      .el-select-dropdown__wrap {
        max-height: 300px !important;
        padding-bottom: 17px !important;
      }
    }
    /deep/.el-select-dropdown {
      width: 100%;
      display: inline-block;
      position: absolute !important;
      overflow: hidden;
      left: -0px !important;
    }
  }
</style>
