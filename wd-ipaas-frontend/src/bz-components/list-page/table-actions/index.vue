<template>
  <div class="table-actions">
    <el-button
      v-for="option of list"
      type="text"
      :class="['table-actions__btn', option.type]"
      :disabled="option.disabled"
      :key="option.name"
      :style="option.style"
      @click="handleClick(option)"
      >{{ option.name }}</el-button
    >
    <el-dropdown v-if="showMore" trigger="click"
      ><i class="el-icon-more" />
      <template #dropdown>
        <el-dropdown-menu class="table-actions__more">
          <el-button
            v-for="option of moreList"
            type="text"
            :class="['table-actions__btn', option.type]"
            :disabled="option.disabled"
            :key="option.name"
            :style="option.style"
            @click="handleClick(option)"
            >{{ option.name }}</el-button
          >
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script>
/**
 * 表格操作
 */
export default {
  name: 'TableActions',
  props: {
    /**
     * 选项
     * @type {{
     *   name: string, // 名称
     *   type?: 'default' | 'danger'
     *   visible?: boolean
     *   disabled?: boolean
     *   onClick?: () => void // 处理点击k
     *   link?: string | object // 路由，如果提供这个，将忽略 onClick
     *   style?: object // 自定义样式
     * }}
     */
    options: {
      type: Array,
      default: () => [],
    },
    /**
     * 最多显示多少个
     */
    max: {
      type: Number,
      default: 3,
    },
  },
  computed: {
    rawList() {
      return this.options.filter((i) => ('visible' in i ? i.visible : true));
    },
    list() {
      return this.rawList.slice(0, this.max);
    },
    moreList() {
      return this.rawList.slice(this.max);
    },
    showMore() {
      return this.rawList.length > this.max;
    },
  },
  methods: {
    handleClick(option) {
      console.info('option', option);
      if (option.link) {
        if (option.link.path && option.link.path.indexOf('.html') > -1) {
          window.top.location.href = option.link.path;
        } else {
          this.$router.push(option.link);
        }
      } else if (option.onClick) {
        option.onClick();
      }
    },
  },
};
</script>

<style scoped>
.table-actions {
  white-space: nowrap;
}

.table-actions__btn {
  color: #2e8dff;
  width: 80px;
  display: inline-block;
  padding: 0 5px;
  text-align: center;
}

.table-actions__btn.is-disabled {
  opacity: 0.4;
}

.table-actions__btn.danger {
  color: #f5222d;
}

.table-actions__more > button {
  display: block;
  padding-left: 10px;
  padding-right: 10px;
  text-align: center;
  width: 100%;
  margin: 0;
}

.el-icon-more {
  font-size: 22px;
  color: #b1b5bd;
  vertical-align: bottom;
  margin-left: 5px;
  cursor: pointer;
}
</style>
