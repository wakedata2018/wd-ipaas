<script>
  import eventBus, { EventName } from '@/components/g6-editor/event-bus';
  import { NoopArray } from '@wakeadmin/utils';

  import { getDefinition } from '../../definitions';
  import BasicMenu from '../basic-menu.vue';

  import transform_sql_union from './transform-sql-union.js';
  import transform_select_row from './transform-select-row.js';
  import transform_select_column from './transform-select-column.js';
  import transform_groovery_script from './transform-groovery-script.js';
  import start from './start';
  import end from './end';
  import api from './api.js';

  export default {
    extends: BasicMenu,
    data() {
      return {
        menuObjMap: Object.freeze({
          transform_sql_union,
          transform_select_row,
          transform_select_column,
          transform_groovery_script,
          start,
          end,
          api,
          api_normal_table: api,
          api_external_http: api,
          api_connector: api,
          api_custom_sql: api,
          api_lite_flow: api,
          api_web_service: api,
        }),
      };
    },

    methods: {
      /**
       * 获取通用菜单
       */
      getCommonMenus() {
        if (this.node == null) {
          return NoopArray;
        }

        const type = this.uniqueName;

        if (type == null) {
          return NoopArray;
        }

        const definition = getDefinition(type);

        const menus = [];

        if (definition?.removable?.(this.node) !== false) {
          menus.push({ cmd: 'delete', name: '删除' });
        }

        // if (definition?.renameable?.(this.node) !== false) {
        //   menus.push({ cmd: 'rename', name: '重命名' });
        // }

        return menus;
      },

      on_delete(command, node) {
        this.node = null;
        command.executeCommand('delete', [node]);
      },
      on_rename(command, node, graph) {
        eventBus.$emit(EventName.nodeRename, {
          node,
          graph,
          command,
        });
      },
    },
  };
</script>
