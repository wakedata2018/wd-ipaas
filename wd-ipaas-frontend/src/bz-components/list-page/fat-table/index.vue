<template>
  <div class="fat-table flow-table">
    <el-alert v-if="error" type="error" :title="error.errorMessage || error.msg || error.message" />
    <!-- 表格形式 -->
    <el-table
      v-if="type === 'table'"
      ref="table"
      v-loading="loading"
      :data="list"
      :row-key="rowKey"
      v-bind="tableProps"
      @selection-change="handleSelectChange"
      @sort-change="sortChange"
      class="bd-table"
    >
      <el-table-column v-if="selectable" width="80" type="selection" :selectable="checkSelectable" />
      <slot />
    </el-table>
    <!-- 列表形式 -->
    <div class="fat-table__list" v-else>
      <template v-if="hasSlots('list')">
        <slot name="list" :list="list"></slot>
      </template>
      <template v-else>
        <div v-for="(item, idx) of list" :key="rowKey ? item[rowKey] : idx" class="fat-table__item">
          <slot name="item" :row="item" />
        </div>
      </template>
    </div>
    <el-pagination
      class="fat-table__pagination"
      v-bind="pagination"
      :disabled="loading"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :hide-on-single-page="hideOnSinglePage"
    />
  </div>
</template>

<script>
  import { hasSlots } from '@/utils/vue';
  import diff from 'lodash/difference';
  import debounce from 'lodash/debounce';

  /**
   * 通用的表格列表组件
   * @event update:selected 选择更新
   * @event update:state 表格状态恢复
   */
  export default {
    name: 'FatTable',
    props: {
      /**
       * 展示类型
       * 有两种展示形式: table, list
       * list 用于自定义整个行的展示
       */
      type: {
        type: String,
        default: 'table',
      },
      /**
       * 列表接口请求
       */
      fetchHandler: {
        // 用于拉取数据
        // 接受一个参数，包含 {pagination: PaginationProps, list: T[]}
        // 返回一个 promise， 结果为 {list: T[], total: number}
        type: Function,
        required: true,
      },
      /**
       * 删除接口
       */
      removeHandler: {
        // 接受一个参数，即 ids
        // 返回一个 Promise
        type: Function,
        default: null,
      },
      defaultPageSize: {
        type: Number,
        default: 10,
      },
      defaultPageLayout: {
        type: String,
        default: 'total, sizes, prev, pager, next',
      },
      defaultPageSizes: {
        type: Array,
        default: () => [10, 15, 50, 100],
      },
      /**
       * 在挂载时触发请求
       */
      fetchOnMounted: {
        type: Boolean,
        default: true,
      },
      rowKey: {
        type: [String, Number],
        default: undefined,
      },
      /**
       * 已选中id, 只适用于表格
       */
      selected: {
        type: Array,
        default: () => [],
      },
      /**
       * 删除确认消息，可以为函数，函数接受一个待删除的列表项数组
       */
      removeConfirmMessage: {
        type: [String, Function],
        default: '是否确认删除？',
      },
      removeConfirmTitle: {
        type: String,
        default: '提示',
      },
      removeSuccessMessage: {
        type: String,
        default: '删除成功',
      },
      /**
       * 直接传递给 el-table 的props
       * 只适用于表格
       */
      tableProps: {
        type: Object,
        default: () => ({}),
      },
      /**
       * 是否支持选择
       * 只适用于表格
       * @sync
       */
      selectable: {
        type: Boolean,
        default: false,
      },
      checkSelectable: {
        type: Function,
        default: () => () => true,
      },
      hideOnSinglePage: {
        type: Boolean,
        default: false,
      },
      /**
       * 表格状态,  用于支撑以下功能：
       * 1. 表格状态缓存： 你可以将表格的表单请求参数放置在这里，fat-table 会缓存下来，并在页面刷新或后退时恢复状态
       *    缓存功能只有使用了 :state.sync 才会启用.
       * 2. 用于监听变动，如果开启了 searchOnStateChange, 一旦 state 变动，就会重新 search
       * 你可以监听 :state.sync 来恢复它
       */
      state: {
        type: Object,
        default: () => ({}),
      },
      /**
       * 是否在 state 变动时 fetch
       */
      searchOnStateChange: {
        type: Boolean,
        default: true,
      },
      // 是否需要选择的列表数据
      needSelectData: {
        type: Boolean,
        default: false,
      },
      // 选择的数据
      selectedData: {
        type: Array,
        default: () => [],
      },
    },
    data() {
      return {
        uid: `${Math.random().toFixed(4).slice(-4)}_${Date.now()}`,
        // 列表
        list: [],
        // 状态已经准备好
        ready: false,
        loading: false,
        error: null,
        pagination: {
          total: 0,
          currentPage: 1,
          pageSize: this.defaultPageSize,
          pageSizes: this.defaultPageSizes,
          layout: this.defaultPageLayout,
        },
      };
    },
    computed: {
      /**
       * 是否启用了缓存
       */
      stateCachable() {
        return !!this.$listeners['update:state'];
      },
    },
    watch: {
      selected: {
        immediate: true,
        deep: true,
        // selected 变更
        handler(val, oldVal) {
          if (!this.selectable || this.type === 'list') {
            return;
          }

          const added = diff(val, oldVal);
          const deleted = diff(oldVal, val);
          this.selectIds(added);
          this.unselectIds(deleted);
        },
      },
      // 列表变动，重新选择
      list(val) {
        if (!this.selectable || this.type === 'list') {
          return;
        }

        if (val && val.length) {
          this.$nextTick(() => {
            this.selectIds(this.selected);
          });
        }
      },
      /**
       * 监听状态变动并缓存
       */
      state: {
        deep: true,
        handler() {
          // 这里使用了 ready 避免在 state 从缓存中恢复后又重新请求一次
          // ready 表示的是已经进行过请求。
          if (this.searchOnStateChange && this.ready) {
            this.debouncedSearch();
          }

          // 触发缓存
          if (!this.stateCachable) {
            return;
          }
          this.saveCache();
        },
      },
    },
    created() {
      if (this.selectable && this.rowKey == null) {
        throw new Error('selectable 开始后，必须提供 rowKey');
      }
    },
    mounted() {
      if (this.stateCachable) {
        // 尝试恢复表格缓存
        if (this.$route.query.t != null) {
          this.uid = this.$route.query.t;
          this.restoreCache();

          // 恢复请求
          this.$nextTick(() => {
            if (this.fetchOnMounted) {
              this.fetch();
            }
          });
        } else {
          // 设置当前表格缓存 key
          this.initialHashKeyIfNeed();

          // 初始请求
          this.$nextTick(() => {
            this.initialFetch();
          });
        }
      } else {
        this.initialFetch();
      }
    },

    methods: {
      hasSlots,
      /**
       * 公开方法
       */
      getPagination() {
        return this.pagination;
      },

      // 搜素并还原页码
      async search() {
        this.reset();
        this.fetch();
      },

      debouncedSearch: debounce(async function () {
        this.search();
      }, 800),

      // 刷新数据
      refresh() {
        this.fetch();
      },

      updateCheckboxSelected(index) {
        this.list = this.list.map((item, ind) => {
          if (ind === index) {
            item.checked = true;
          } else {
            item.checked = false;
          }
          return item;
        });
        console.log(this.list);
      },

      reset() {
        this.clearSelect();
        this.loading = false;
        this.error = null;
        this.list = [];
        this.pagination.total = 0;
        this.pagination.currentPage = 1;
      },

      /**
       * 删除选中的列表项
       */
      async removeSelected() {
        if (this.selected == null || this.selected.length === 0) {
          return;
        }

        if (!(await this.confirmDelete(this.selected))) {
          return;
        }

        this.handleRemove(this.selected);
      },

      /**
       * 删除指定项
       */
      async remove(id) {
        const selected = [id];
        console.info('删除:', selected);
        if (!(await this.confirmDelete(selected))) {
          return;
        }

        this.handleRemove(selected);
      },

      /**
       * 选中全部
       */
      selectAll() {
        const ids = this.list.map(i => i[this.rowKey]);
        this.$emit('update:selected', ids);
      },
      /**
       * 取消选中
       */
      clearSelect() {
        this.$emit('update:selected', []);
      },

      /**
       * 选中指定 ids
       * @param {Array} ids
       */
      selectIds(ids) {
        const list = this.list.filter(item => ids.findIndex(id => item[this.rowKey] == id) !== -1);
        list.forEach(item => {
          this.$refs.table.toggleRowSelection(item, true);
        });
      },

      /**
       * 取消选中指定 ids
       */
      unselectIds(ids) {
        const list = this.list.filter(item => ids.findIndex(id => item[this.rowKey] == id) !== -1);
        list.forEach(item => {
          this.$refs.table.toggleRowSelection(item, false);
        });
      },

      /**
       * 通过 Id 获取实例
       */
      getItemsByIds(ids) {
        console.info('ids:', this.list);

        return ids.map(i => this.list.find(j => j[this.rowKey] === i));
      },

      /**
       * 私有方法
       */
      fetch: debounce(async function () {
        const params = {
          pagination: { ...this.pagination },
          list: this.list,
        };

        try {
          this.loading = true;
          this.error = null;
          const { list, total } = await this.fetchHandler(params);
          this.list = list;
          this.pagination.total = total;
          this.ready = true;
        } catch (err) {
          this.error = err;
        } finally {
          this.loading = false;

          // 缓存状态
          this.saveCache();
        }
      }, 200),

      initialFetch() {
        if (this.fetchOnMounted) {
          this.search();
        }
      },

      async handleRemove(ids) {
        if (this.rowKey == null) {
          throw new Error('必须提供 rowKey props');
        }

        try {
          await this.removeHandler(ids);

          if (ids.length > this.list.length / 3) {
            // 直接刷新
            this.refresh();
            return;
          }

          let len = 0;
          ids.forEach(id => {
            const idx = this.list.findIndex(i => i[this.rowKey] === id);
            if (idx !== -1) {
              this.list.splice(idx, 1);
              len++;
            }
          });

          this.pagination.total -= len;

          this.$message.success(this.removeSuccessMessage);
        } catch (err) {
          // TODO: 错误
          console.error(err);
        }
      },
      async confirmDelete(ids) {
        try {
          const message =
            typeof this.removeConfirmMessage === 'function'
              ? this.removeConfirmMessage(this.getItemsByIds(ids))
              : this.removeConfirmMessage;
          await this.$confirm(message, this.removeConfirmTitle, { type: 'warning' });
          return true;
        } catch (err) {
          console.log(err);
          return false;
        }
      },
      initialHashKeyIfNeed() {
        if (this.$route.query.t == null) {
          this.$router.replace({
            ...this.$route,
            query: {
              ...this.$route.query,
              t: this.uid,
            },
          });
        }
      },
      /**
       * 缓存状态
       */
      saveCache: debounce(function () {
        if (!this.stateCachable) {
          return;
        }

        const key = `ft_${this.uid}`;
        const payload = { pagination: this.pagination, selected: this.selected, state: this.state };
        window.sessionStorage.setItem(key, JSON.stringify(payload));

        // 检查缓存 key 是否被清除
        this.initialHashKeyIfNeed();
      }, 700),

      /**
       * 缓存恢复
       */
      restoreCache() {
        const key = `ft_${this.uid}`;
        const data = window.sessionStorage.getItem(key);
        if (data) {
          const { pagination, selected, state } = JSON.parse(data);
          Object.assign(this.pagination, pagination);

          if (selected) {
            // TODO: 暂时不做, 选中恢复
          }

          if (state) {
            this.$emit('update:state', state);
          }
        }
      },

      handleSizeChange(evt) {
        this.pagination.pageSize = evt;
        this.search();
      },

      handleCurrentChange(evt) {
        this.pagination.currentPage = evt;
        this.fetch();
      },

      handleSelectChange(evt) {
        console.log('fat-table select change', evt);
        if (this.needSelectData) {
          this.$emit('update:selected-data', evt);
        }
        const selectedNotInList = this.selected.filter(id => this.list.findIndex(i => i[this.rowKey] == id) === -1);
        this.$emit('update:selected', selectedNotInList.concat(evt.map(i => i[this.rowKey])));
      },
      sortChange(evt) {
        this.$emit('update:sort-change', evt);
      },
    },
  };
</script>

<style>
  .fat-table__pagination {
    text-align: right;
    margin-top: 20px;
  }
</style>
