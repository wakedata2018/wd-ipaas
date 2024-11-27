/**
 * Created by a123 on 2020/12/4.
 * @author trumpli<李志伟>
 */
export const SQL_OPTION = {
  mode: "text/x-sql", //实现Java代码高亮
  lineNumbers: true,
  // theme: "dracula",
  theme: "default",
  keyMap: "sublime",
  extraKeys: {"Ctrl": "autocomplete"},
  lineWrapping: true, //是否换行
  foldGutter: true, //是否折叠
  gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter"], //添加行号栏，折叠栏
};
