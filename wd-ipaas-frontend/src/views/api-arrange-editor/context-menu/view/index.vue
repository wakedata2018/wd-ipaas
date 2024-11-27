<script>
  import BasicMenu from '../basic-menu.vue';
  import api from './api.js';
  import eventBus, { EventName } from '@/components/g6-editor/event-bus';

  export default {
    extends: BasicMenu,
    data() {
      return {
        menuObjMap: Object.freeze({
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
      getCommonMenus() {
        return [{ cmd: 'edit', name: '查看配置' }];
      },
      on_edit(command, node) {
        eventBus.$emit(EventName.nodeEdit, node);
      },
    },
  };
</script>
