<template>
  <div class="api-sql-code">
    <header>
      <span class="title">查询SQL</span>
      <!-- <div>
        <span class="covert" @click="onSubmitSql">SQL校验</span>
      </div> -->
    </header>
    <CodeMirror
      v-model="code"
      :options="cmOptions"
      @ready="onCmReady"
      ref="codemirror"
    ></CodeMirror>
    <div style="padding: 10px 0">
      <el-button type="primary" @click="onSubmitSql" class="bd-button"
        >SQL校验
      </el-button>
    </div>
  </div>
</template>
<script>
import CodeMirror from "./codemirror";
import "codemirror/lib/codemirror.css";
import "codemirror/theme/dracula.css";
import "codemirror/mode/sql/sql.js"; // 对于sql模式的支持
import "codemirror/keymap/sublime.js";
import "codemirror/addon/hint/show-hint.css";
import "codemirror/addon/hint/show-hint.js";
import sqlFormatter from "sql-formatter";
import { SQL_OPTION } from "./config";

export default {
  name: "api-sql-code",
  components: {
    CodeMirror,
  },
  computed: {
    codemirror() {
      return this.$refs.codemirror.codemirror;
    },
  },
  data() {
    return {
      code: "",
      cmOptions: SQL_OPTION,
    };
  },
  mounted() {
    const sql = this.$attrs.value;
    !!sql && this.$nextTick(() => this.codemirror.setValue(sql));
  },
  methods: {
    onSubmitSql() {
      this.$emit("input", this.outputSql());
      this.$emit("enter", this.outputSql());
    },
    onCmReady(cm) {
      cm.on("keypress", () => {
        cm.showHint();
      });
    },
    outputSql() {
      const sql = this.codemirror.getValue();
      console.log(sql);
      return sql;
    },
    format() {
      return sqlFormatter.format(this.codemirror.getValue(), {
        language: "sql",
      });
    },
    autoFormatSelection() {
      this.codemirror.setValue(this.format());
    },
  },
};
</script>
<style lang="less" scoped>
.api-sql-code {
  width: 90%;
  margin: 10px 10px;
  header {
    width: 100%;
    height: 30px;
    // background-color: #1d364a;
    // color: #fff;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .title {
    font-weight: 800;
    // margin-left: 1.5rem;
  }
  .covert,
  .format {
    text-align: center;
    background-color: #fff;
    padding: 3px 5px;
    color: #000;
    border-radius: 1px;
    cursor: pointer;
    margin-right: 1.5rem;
  }
  .covert {
    background-color: #4dabf5;
    color: #fff;
  }
}
</style>
