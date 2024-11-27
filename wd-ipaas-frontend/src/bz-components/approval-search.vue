<template>
  <div class="approval-search">
    <el-form>
      <el-row :gutter="20" class="search-row">
        <!-- <el-col :span="8">
          <el-form-item label="关键字：" label-width="68px">
            <el-input v-model="form.keyword"></el-input>
          </el-form-item>
        </el-col>-->
        <el-col :span="12">
          <el-form-item label="接入名称：" label-width="85px">
            <el-select placeholder="请选择" style="width:100%" v-model="form.accessAppId">
              <el-option
                v-for="app in appList"
                :key="app.dataAccessAppId"
                :value="app.dataAccessAppId"
                :label="app.dataAccessAppName"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="审批状态：" label-width="85px">
            <el-select placeholder="请选择" style="width:100%" v-model="form.statusEnum">
              <el-option
                v-for="item in status"
                :value="item.value"
                :label="item.label"
                :key="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <!-- <div class="operation">
          <el-button type="primary" @click="onSearch">查询</el-button>
        </div> -->
      </el-row>
    </el-form>
  </div>
</template>

<script>
import daaAPI from "@/api/data-access-app.js";
import { ApprovalStatus } from "@/enum.js";
export default {
  props: {
    query: {
      type: Object,
      default() {
        return null;
      }
    }
  },
  data() {
    return {
      appList: [],
      form: {
        keyword: '',
        accessAppId: '',
        statusEnum: ''
      },
      status: [{ value: '', label: '全部' }].concat(ApprovalStatus.list)
    };
  },
  created() {
    this.getAppList();
  },
  methods: {
    getAppList() {
      daaAPI.list().done(res => {
        this.appList = [{ dataAccessAppId: '', dataAccessAppName: '全部' }].concat(res.data || []);
      });
    },
    onSearch() {
      this.$emit('update:query', this.$plain(this.form));
      this.$emit('search', this.form);
    }
  }
};
</script>

<style lang="less" scoped>
.approval-search {
  .search-row {
    max-width: 800px;
    position: relative;
    padding-right: 60px;

    .operation {
      position: absolute;
      right: 0;
    }
  }
}
</style>