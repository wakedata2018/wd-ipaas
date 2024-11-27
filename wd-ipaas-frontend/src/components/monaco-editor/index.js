import MonacoEditor from './monaco-editor.vue';

export default MonacoEditor;

if (typeof window !== 'undefined' && window.Vue) {
  window.Vue.component(MonacoEditor.name, MonacoEditor);
}
