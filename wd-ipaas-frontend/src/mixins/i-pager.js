export default {
  data() {
    return {
      pageSize: 7,
      pageIndex: 1
    };
  },
  computed: {
    pageData() {
      const tableData = this[this.tableProp];
      const len = tableData.length;
      let start = (this.pageIndex - 1) * this.pageSize;
      let end = start + this.pageSize;
      let list = [];
      for (let i = start; i < len && i < end; i++) {
        list.push(tableData[i]);
      }
      return list;
    }
  }
};
