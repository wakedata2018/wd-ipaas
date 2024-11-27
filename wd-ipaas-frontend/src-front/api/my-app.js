import mydata from './mydata';

let list = [];

let p = null;

export default {
  getAppList() {
    if (list.length > 0) {
      return Promise.resolve(list);
    }
    if (p !== null) {
      return p;
    }
    p = mydata.getJoinInfo().done(res => {
      list = (res.data || []).filter(item => item.status === 'PASS');
      return list;
    });
    p.always(() => {
      p = null;
    });
    return p;
  },
  getAllAPPList() {
    return mydata.getJoinInfo().done(res => {
      return res.data || [];
    });
  },
  clear() {
    list = [];
  },
};
