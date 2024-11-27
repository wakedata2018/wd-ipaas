<template>
  <div class="bd-search">
    <el-form size="mini" label-position="right" class="bd-form" inline>
      <el-form-item label="接入名称">
        <el-input v-model.trim="form.keyword" type="text" placeholder="单行输入" /> </el-form-item
      ><br />
      <el-form-item label="创建时间">
        <el-date-picker
          v-model="value"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          @change="handleDate"
        ></el-date-picker>
      </el-form-item>
    </el-form>
    <div class="bd-search-group">
      <el-button type="primary" class="bd-button bd-strong" @click="onSearch">查询</el-button>
      <el-button type="primary" plain size="mini" class="bd-button bd-strong" @click="onReset">重置</el-button>
    </div>
  </div>
</template>

<script>
  import daaAPI from '../api/data-access-app.js';
  import { date } from 'dss-common';
  import { format } from 'path';
  export default {
    data() {
      return {
        appList: [],
        value: '', // 时间
        form: {
          keyword: '',
          from: null,
          to: null,
        },
      };
    },
    methods: {
      onSearch() {
        this.$emit('search', this.form);
      },
      onReset() {
        this.form.keyword = '';
        this.form.from = '';
        this.form.to = '';
        this.value = '';
        this.onSearch();
      },
      handleDate() {
        const time1 = date.format(this.value[0]);
        const time2 = date.format(this.value[1]);
        this.form.from = time1;
        this.form.to = time2;
      },
    },
  };
</script>

<style scoped lang="less">
  .search {
    background-color: #fff;
    padding: 20px;
    .contant {
      background-color: #f3f6f8;
      padding: 22px 20px;
      .condition {
        input {
          width: 218px;
          height: 30px;
          border: 1px solid #ccc;
          padding-left: 8px;
        }
        .timer {
          margin: 16px 0;
        }
      }
      .button {
        margin-left: 75px;
        .onSearch {
          background-color: #2776fb;
          color: #fff;
        }
        .reset {
          border: 1px solid #2776fb;
          color: #2776fb;
        }
      }
    }
  }
</style>
