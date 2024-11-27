<template>
  <div class="right-panel-div">
    <el-scrollbar class="page-scrollbar">
      <div class="page-task">
        <div
          :class="{'type-tab': true,'selected': typeSelected===componentType.nodeType}"
          v-for="componentType in componentTypes"
          :title="componentType.label"
          :key="componentType.nodeType"
          @click="_=>{changeTypeSelected(componentType.nodeType);}"
        >
          <i :class="componentType.icon" />
          <div class="tab-text">{{componentType.label}}</div>
        </div>
      </div>
    </el-scrollbar>
    <div class="panel-btn-div">
      <div class="panel-btn" v-if="!!typeSelected" @click="changeTypeSelected(null)">
        <i class="el-icon-d-arrow-right" />
      </div>
    </div>
  </div>
</template>

<script>
export default {
  components: {},
  mixins: [],
  props: {
    typeSelected: {
      type: String,
      default: null
    }
  },
  data() {
    return {
      componentTypes: [
        {
          label: '设置',
          icon: 'el-icon-setting',
          nodeType: 'setting'
        }
        // {
        //   label: 'Transform',
        //   icon: imgs['transform'],
        //   nodeType: 'transform',
        //   a_icon: imgs['transform_a'],
        // },
        // {
        //   label: 'Sink',
        //   icon: imgs['sink'],
        //   nodeType: 'sink',
        //   a_icon: imgs['sink_a'],
        // }
      ]
    };
  },
  mounted() {},
  beforeDestroy() {},
  methods: {
    changeTypeSelected(val) {
      this.$emit('change-type-selected', val);
    }
  }
};
</script>

<style scoped lang="less">
@import '../../../css/var.less';
@toolbar-height: 60px;
@tab-width: 30px;
.right-panel-div {
  position: absolute;
  height: calc(100vh - @header-height - @toolbar-height);
  .page-scrollbar {
    //   border-right: 1px solid #eee;

    background-color: white;
    box-sizing: content-box;
    overflow-x: hidden;
    overflow-y: hidden;
    position: absolute;
    left: 0px;
    width: @tab-width;
    height: calc(100% - 40px);
    /deep/ .el-scrollbar__wrap {
      overflow-x: hidden;
    }

    .page-task {
      // height: @full-height;
      // overflow: auto;
      box-sizing: border-box;
      .type-tab {
        width: 30px;
        overflow: hidden;
        padding: 5px 5px 5px 2px;
        box-sizing: border-box;
        text-align: center;
        vertical-align: middle;
        cursor: pointer;
        transition: all 0.3s;
        color: #656565;
        border-left: 3px solid transparent;
        &:hover {
          border-left: 3px solid #2195f3;
          color: #2195f3;
          background-color: #f7f9fb;
        }
        .tab-text {
          text-align: center;
          // writing-mode: vertical-lr;
          font-size: 13px;
          font-weight: 600;
          // transform: rotate(180deg);
        }
        &.selected {
          border-left: 3px solid #2195f3;
          color: #2195f3;
        }
        img {
          width: 20px;
          height: 20px;
        }
      }

      // padding: 10px;
    }
  }
  .panel-btn-div {
    
    
    width: @tab-width;
    box-sizing: border-box;
    bottom: 0;
    position: absolute;
    display: inline-block;
    .panel-btn {
      height: 100%;
      width: 100%;
      height: 40px;
      line-height: 40px;
      color: #889098;
      cursor: pointer;
      transition: all 0.3s;
      text-align: center;
      font-size: 17px;
      &:hover {
        background-color: #f7f9fb;
        color: #2195f3;
      }
    }
  }
}
</style>
