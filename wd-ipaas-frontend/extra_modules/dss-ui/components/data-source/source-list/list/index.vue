<template>
  <el-scrollbar>
    <div
      class="item"
      :class="{ active: item.id === selectedSource.id }"
      v-for="item in list"
      :key="item.id"
      @click="onSourceSelected(item)"
    >
      <div class="info">
        <div class="name ellipsis" :title="item.dataSourceName" @click.stop="onDetail(item)">
          {{ item.dataSourceName }}
        </div>
        <div class="config ellipsis" :title="formatDbName(item)">{{ formatDbName(item) }}</div>
        <div class="config ellipsis" :title="formatConfig(item)">{{ formatConfig(item) }}</div>
        <div class="config ellipsis" :title="formatReference(item)">{{ formatReference(item) }}</div>
      </div>
      <div class="operate">
        <img :src="formatIcon(item)" style="width: 50px; height: 30px" />
        <div class="btn">
          <i class="el-icon-edit" title="编辑" @click.prevent.stop="onEdit(item)"></i>
          <i class="el-icon-delete" title="删除" @click.prevent.stop="onDelete(item)"></i>
        </div>
      </div>
    </div>
    <source-detail :visible.sync="detailDialog.visible" :detail="detailDialog.detail"></source-detail>
  </el-scrollbar>
</template>

<script>
import imgs from '../../sourceTypeImages.js';
import SourceDetail from '../source-detail/index.vue';

export default {
  components: { SourceDetail },
  props: {
    list: {
      type: Array,
      default: () => [],
    },
    typeList: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      imgs,
      detailDialog: {
        visible: false,
        detail: {},
      },
      selectedSource: {},
    };
  },
  watch: {
    list: {
      handler(val) {
        this.onSourceSelected(val[0] || {});
      },
      deep: true,
      immediate: true,
    },
  },
  methods: {
    formatConfig(item) {
      const host = item.dataSourceConf['db.host'] || '';
      const port = item.dataSourceConf['db.port'] || '';
      if (item.dataSourceType === 'redis') {
        return item.dataSourceConf['redis.ip.port'] || '';
      }
      return host || port ? `${host}:${port}` : '';
    },
    formatDbName(item) {
      const host = item.dataSourceConf['db.database'] || '';
      return item.dataSourceConf['db.database'] || '';
    },
    formatReference(item) {
      let reference = item.reference || '';
      if (item.reference === 0) {
        reference = 0;
      }
      return `${reference}个引用` || '';
    },
    formatIcon(item) {
      for (let j = 0; j < this.typeList.length; j++) {
        if (item.dataSourceType === this.typeList[j].dataSourceType) {
          this.$set(item, 'dataSourceIcon', this.typeList[j] && this.typeList[j].dataSourceIconMid);
        }
      }
      return item.dataSourceIcon && item.dataSourceIcon;
    },
    onDetail(detail) {
      this.detailDialog.detail = detail;
      this.detailDialog.visible = true;
    },
    onEdit(item) {
      this.$emit('edit', item);
    },
    onDelete(item) {
      this.$emit('delete', item);
    },
    afterDelete(item) {
      if (this.selectedSource.id === item.id) {
        this.onSourceSelected({});
      }
    },
    onSourceSelected(item) {
      // if (this.selectedSource.id !== item.id) {
      this.$emit('source-selected', item);
      // }
      this.selectedSource = item;
    },
  },
};
</script>

<style lang="less" scoped>
@import '../../style.less';

.el-scrollbar {
  height: 100%;
  /deep/ .el-scrollbar__wrap {
    height: calc(100% + 17px);
  }
}
.item {
  position: relative;
  height: 150px;
  margin: 5px 20px 20px;
  box-shadow: 0px 4px 16px 0px #eaeef1;
  cursor: pointer;
  border: 1px solid transparent;
  &:hover,
  &.active {
    border: 1px solid #2196f3;
  }
  .info {
    padding: 20px 0 0 32px;
    width: 228px;

    .name {
      color: #2196f3;
      font-weight: 600;
      cursor: pointer;
      &:hover {
        // font-size: 16px;
        background-color: rgba(221, 240, 255, 0.65);
      }
    }
    .config {
      margin-top: 10px;
      font-size: 12px;
      color: #bdbdbd;
    }
    & > div {
      width: fit-content;
      width: -webkit-fit-content;
      width: -moz-fit-content;
      max-width: 100%;
    }
  }
  .operate {
    position: absolute;
    bottom: 0;
    height: 30px;
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    .btn {
      padding-right: 10px;
      font-size: 16px;
      i + i {
        margin-left: 20px;
      }
      i {
        cursor: pointer;
      }
    }
  }
}
</style>