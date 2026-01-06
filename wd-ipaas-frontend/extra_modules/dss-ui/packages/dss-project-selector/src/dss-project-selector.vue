<template>
  <el-select
    v-model="selectedProject"
    v-loading="projectList.loading && currentProject.loading && changeLoading"
    filterable
    placeholder="请选择项目"
    title="请选择项目"
    class="project-selector new"
    popper-class="project-selector-popper"
    @change="changeProject"
  >
    <el-option v-for="item in projectList.list" :key="item.id" :label="item.name" :value="item.id">
      <div class="project-selector-item-title" :title="item.name">{{ item.name }}</div>
    </el-option>
  </el-select>
</template>
<i18n>
{
  "en": {
    "请选择项目": "Please select project."
  }
}
</i18n>
<script>
import projectApi from '../../api/projectApi.js';
export default {
  name: 'DssProjectSelector',
  props: {
    /**
     * 接口前缀，机器学习用
     */
    apiPrefix: {
      type: String,
      default: null,
    },
    /**
     * 更改项目后刷新页面，为否则向上抛出change-project事件
     */
    refreshAfterChangingProject: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      selectedProject: null,
      currentProject: {
        loading: false,
        obj: {},
      },
      projectList: {
        loading: false,
        list: [],
      },
      changeLoading: false,
    };
  },
  watch: {},
  mounted() {
    this.getProjectList();
  },
  methods: {
    getProjectList() {
      this.projectList.loading = true;
      projectApi
        .list({}, this.apiPrefix)
        .then(res => {
          this.projectList.list = res.data;
        })
        .catch(_ => {
          this.projectList.list = [];
        })
        .always(_ => {
          this.projectList.loading = false;
          this.getCurrentProject();
        });
    },
    getCurrentProject() {
      this.currentProject.loading = true;
      projectApi
        .getCurrentProject({}, this.apiPrefix)
        .then(res => {
          this.currentProject.obj = res.data;
          if (this.currentProject.obj && this.currentProject.obj.id) {
            this.selectedProject = this.currentProject.obj.id;
          } else {
            this.selectedProject = null;
          }
        })
        .catch(_ => {
          this.currentProject.obj = {};
          this.selectedProject = null;
        })
        .always(_ => {
          this.currentProject.loading = false;
        });
    },
    changeProject(val) {
      this.changeLoading = true;
      projectApi
        .changeProject(
          {
            projectId: val,
          },
          this.apiPrefix
        )
        .then(res => {
          setTimeout(_ => {
            if (this.refreshAfterChangingProject) {
              window.location.reload();
            } else {
              this.$emit('change-project', val);
            }
          }, 200);
        })
        .always(_ => {
          this.changeLoading = false;
        });
    },
  },
};
</script>

<style lang="less" scoped>
@header-height: 52px;

.project-selector {
  font-size: 14px;
  &.old {
    // margin: 5px;
    /deep/ .el-input {
      & > input {
        background-color: #363636;
        color: rgb(148, 148, 148) !important;
        text-align: center;
        font-size: 13px;
        border: 0px;
        // border: 1px solid #505050;
        border-radius: 0px;
        transition: all 0.2s;
        &::-webkit-input-placeholder {
          /* WebKit browsers */
          color: rgb(131, 131, 131) !important;
        }
        &::-moz-placeholder {
          /* Mozilla Firefox 19+ */
          color: rgb(131, 131, 131) !important;
        }
        &:hover {
          background-color: #303030;
          // border: 1px solid #5c5c5c;
        }
      }
    }
  }
  &.new {
    // margin: 5px;
    margin-right: 10px;
    vertical-align: top;
    border: 0px;
    height: @header-height;
    /deep/ .el-input {
      height: @header-height;
      & > input {
        background-color: transparent;
        color: #c2c5cc !important;
        width: 150px;
        height: @header-height;
        text-align: center;
        font-size: 14px;
        text-overflow: ellipsis;
        border: 0px;
        // border: 1px solid #505050;
        border-radius: 0px;
        transition: all 0.2s;
        &::-webkit-input-placeholder {
          /* WebKit browsers */
          color: rgb(131, 131, 131) !important;
        }
        &::-moz-placeholder {
          /* Mozilla Firefox 19+ */
          color: rgb(131, 131, 131) !important;
        }
        &:hover {
          background-color: #252525;
          // border: 1px solid #5c5c5c;
        }
      }
      .el-select__caret {
        vertical-align: top;
        height: @header-height;
      }
    }
  }
}
// project-selector-item-title
/deep/ .project-selector-item-title {
  // font-weight: 600;
  line-height: 25px;
  font-size: 13px;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
}
/deep/ .project-selector-item-desc {
  line-height: 20px;
  font-size: 13px;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
  color: rgb(145, 148, 153);
  font-weight: 200;
}
/deep/.el-loading-mask {
  background-color: rgba(0, 0, 0, 0.3);
}
</style>
<style lang="less">
.project-selector-popper {
  background-color: #363636 !important;
  border: 1px solid #5c5c5c !important;
  border-radius: 0px !important;
  width: 165px;
  transform: translate(-5px, -5px) !important;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.3) !important;
  .el-scrollbar {
    .el-scrollbar__wrap {
      max-height: calc(100vh - 95px) !important;
    }
  }
  .popper__arrow {
    border-bottom-color: #5c5c5c !important;
    &::after {
      border-bottom-color: #363636 !important;
    }
  }

  .el-select-dropdown__list {
    padding: 0px;
  }
  .el-select-dropdown__item {
    font-size: 12px;
    color: #c0c4cc;
    height: 25px;
    padding: 10px 20px;
    box-sizing: content-box;
    transition: all 0.2s;
    & + .el-select-dropdown__item {
      border-top: 1px solid #494949 !important;
    }
    &.selected {
      background-color: #252525 !important;
      .project-selector-item-title {
        color: #2196f3;
      }
    }
    &.hover {
      background-color: #2b2b2b !important;
    }
  }
}
</style>