// import JsonSchemaEditor from './main.vue';
import JsonSchemaEditor from './index.vue';

JsonSchemaEditor.install = function (Vue) {
  Vue.component(JsonSchemaEditor.name, JsonSchemaEditor);
};

export default JsonSchemaEditor;
