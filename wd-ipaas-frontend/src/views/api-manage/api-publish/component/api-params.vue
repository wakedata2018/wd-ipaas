<template>
  <el-tabs v-model="activeName" type="card">
    <!-- (${requestContent.header.length}) -->
    <el-tab-pane :label="`请求头部`" name="1">
      <el-button type="text" @click="openInput(1)">导入</el-button>
      <el-button type="text" @click="resetTable">清空参数</el-button>
      <param-table class="param-table" ></param-table>
    </el-tab-pane>
    <!-- (${requestContent.body.length}) -->
    <el-tab-pane :label="`请求体`" name="2">
      <el-radio-group v-model="body.radio" class="tips">
        <el-radio :label="1">Form-data</el-radio>
        <el-radio :label="2">JSON</el-radio>
      </el-radio-group>
      <br />
      <param-table v-if="body.radio === 1" class="param-table" ></param-table>
      <el-input
        type="textarea"
        :rows="8"
        placeholder="请求JSON："
        v-model="requestContent.body.jsonContent"
        v-else
        class="param-table"
        >>
      </el-input>
    </el-tab-pane>
    <el-tab-pane name="3">
      <span slot="label">
        <span>Query</span>
        <el-tooltip
          class="item"
          effect="dark"
          content="Query 参数指的是地址栏中跟在问号？后面的参数，如 /user/login?user_name=jackliu"
          placement="top-start"
        >
          <i class="el-icon-question"></i>
        </el-tooltip>
      </span>
      <div>
        <el-button type="text" @click="openInput(2)">导入</el-button>
        <el-button type="text" @click="resetTable">清空参数</el-button>
        <param-table class="param-table" ></param-table>
      </div>
    </el-tab-pane>
    <!-- 导入 -->
    <el-dialog
      :title="isInput === 1 ? '导入头部' : '导入Query参数'"
      :visible.sync="dialogFormVisible"
      width="500px"
      class="inputDialog"
      :modal="false"
      :close-on-click-modal="false"
    >
      <p>
        {{
          isInput === 1
            ? `批量导入的数据格式为 key:value ，一行一条 header 信息，如：Connection:keep-alive`
            : `批量导入的数据格式为 ?key=value… ，通过&分隔多个参数，如：
api.eolinker.com/user/login?user_name=jackliu&user_password=hello`
        }}
      </p>
      <el-form :model="inputForm">
        <el-form-item>
          <el-input v-model="inputForm.content" type="textarea" style="width: 460px" :rows="6"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="rewardData">确认</el-button>
        <!-- <el-button type="primary" @click="dialogFormVisible = false">在末端插入</el-button>
        <el-button type="primary" @click="dialogFormVisible = false">全量替换</el-button> -->
      </div>
    </el-dialog>
  </el-tabs>
</template>
<script>
import TipsIcon from '@/components/tips-icon';
import ParamTable from './param-table.vue';

export default {
  components: { TipsIcon, ParamTable },
  props: {
    // parameters: {
    //   type: Array,
    //   default: () => {
    //     return [];
    //   },
    // },
  },
  data() {
    return {
      requestContent: {
        header: [],
        body: [],
        query: '',
      },
      activeName: '1',
      body: {
        radio: 1,
        jsonContent: '',
      },
      inputForm: {
        content: '',
      },
      dialogFormVisible: false,
      isInput: 1,
    };
  },
  computed: {
    form() {
      return {
        list: this.parameters,
        errorList: this.httpCodes,
      };
    },
  },
  methods: {
    inputData() {},
    resetTable() {},
    openInput(idx) {
      this.isInput = idx;
      this.dialogFormVisible = true;
    },
    rewardData() {
      let newData = [];
      if (this.isInput === 1) {
        const dataList = this.inputForm.content.split('\n');
        newData = dataList.map(o => ({
          key: o.split(':')[0],
          value: o.split(':')[1],
        }));
      } else {
        newData = this.setUrlObject(this.inputForm.content);
      }
    },
    setUrlObject(url) {
      const params = [];
      const str = url.indexOf('?') !== -1 ? url.split('?')[1] : url;
      const strs = str.split('&');
      for (let i = 0; i < strs.length; i++) {
        const data = {
          key: strs[i].split('=')[0],
          value: strs[i].split('=')[1],
        };
        params.push(data);
      }
      return params;
    },
  },
};
</script>

<style lang="less" scoped>
.tips {
  margin-bottom: 10px;
  span {
    font-size: 12px;
    color: #bbb;
  }
}
.param-table {
  margin-top: 15px;
}
</style>
