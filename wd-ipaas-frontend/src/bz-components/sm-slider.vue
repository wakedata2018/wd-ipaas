<template>
  <el-scrollbar class="slider">
    <div class="title">API列表菜单</div>
    <el-menu>
      <el-submenu
        :index="item.groupName"
        class="api-manage"
        v-for="(item,index) in apiGroupList"
        :key="index"
      >
        <template slot="title">
          <span slot="title" @click="handleApi($event)" :data-id="item.id">{{item.groupName}}</span>
        </template>
      </el-submenu>
    </el-menu>
  </el-scrollbar>
</template>

<script>
import apiGroup from "@/api/api-group.js";
export default {
  data() {
    return {
      apiGroupList: [], // 接口分类名
      apiList: {}, // 每个接口分类的所有内容
      activeNames: ['1'],
      arr: [1, 2, 3]
    };
  },
  created() {
    this.getList();
    // console.log(this.apiList['用户数据']);
  },
  methods: {
    getList() {
      // console.log('获取接口分类列表');
      apiGroup.getThemeList().done(res => {
        this.apiGroupList = res.data;
      });
    },
    handleChange(val) {
      // console.log(val);
    },
    handleApi(e) {
      const id = +e.target.dataset.id;
      this.$emit('getGroupId', id);
    }
  }
};
</script>

<style scoped lang="less">
.slider {
  background-color: #fff !important;
  box-sizing: border-box;
  max-height: 900px;
  .title {
    padding: 15px 0;
    font-size: 16px;
    color: #333;
  }
  .el-menu {
    border: none;
  }
  .el-menu-item {
    min-width: 0;
    box-sizing: border-box;
  }
  /deep/.el-submenu__icon-arrow {
    display: none;
    right: 2px;
  }
  /deep/.el-menu-item {
    padding-left: 30px;
    background-color: #fff !important;
    color: #333 !important;
  }
  /deep/ .sider-bar {
    background-color: #fff;
  }
  /deep/ .el-submenu {
    .el-submenu__title {
      height: 44px;
      line-height: 44px;

      &:hover {
        background-color: rgba(0, 198, 247, 0.1);
      }
    }

    &.is-active {
      .el-submenu__title {
        color: #333;
        font-weight: bold;
        i {
          color: #333;
        }

        &::after {
          content: " ";
          position: absolute;
          left: 0;
          border-radius: 1px;
          bottom: 0;
          top: 0;
          width: 0px;
          border: 2px solid #fff;
        }
      }
    }

    .el-menu {
      .el-menu-item {
        padding-left: 45px !important;
        color: #333;
      }
      .is-active {
        color: #333;
        font-weight: bold;
      }
    }
  }
}
</style>