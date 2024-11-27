import daaAPI from "@/api/data-access-app.js";
let list = [];

let p = null;

export default {
  getAuthTypes() {
    if (list.length > 0) {
      return Promise.resolve(list);
    }
    if (p !== null) {
      return p;
    }
    p = daaAPI.getAuthTypes().done(res => {
      const authTypeList = [];
      const keyValue = res.data || {};
      for (const key in keyValue) {
        authTypeList.push({
          label: keyValue[key],
          value: key,
        });
      }
      list = authTypeList;
      return list;
    });
    p.always(() => p = null);
    return p;
  }
}
