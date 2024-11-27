<template>
  <div class="doc-content-side-catalog">
    <side-catalog class="catalog" v-bind="catalogProps" v-if="show">
      <template #default="{ isActive }">
        <i :class="['line-style', isActive ? 'line-style--active' : '']"></i>
      </template>
    </side-catalog>
  </div>
</template>
<script>
  /**
   * 滚动目录组件
   */
  import SideCatalog from 'vue-side-catalog';
  import 'vue-side-catalog/lib/vue-side-catalog.css';

  export default {
    name: 'ApiSideCatalog',
    props: {
      /**
       * 是否显示
       */
      show: {
        type: Boolean,
        default: false,
      },
    },
    components: {
      SideCatalog,
    },
    data() {
      return {
        catalogProps: {
          container: '#api',
          iconLeft: true,
          lineLeft: 0,
          activeColor: '#1890FF',
          levelList: ['h2'],
        },
      };
    },
  };
</script>
<style lang="less" scoped>
  .doc-content-side-catalog {
    width: 179px;
    padding: 20px;

    ::v-deep .catalog {
      position: fixed;
      top: 100px;
      font-size: 12px;
      width: 119px;

      .side-catalog__title {
        font-size: 12px !important;
        color: #606266;
      }

      .line-style {
        display: inline-block;
        height: 20px;
        width: 3px;
        background: transparent;
      }

      .line-style--active {
        background: currentColor;
      }

      .side-catalog__list-item-title {
        color: #909399;
        font-weight: 500;
      }

      .side-catalog__list-item-title.side-catalog__list-item--active {
        color: #1890ff;
      }
    }
  }
</style>
