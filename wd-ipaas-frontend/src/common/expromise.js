/* eslint-disable no-extend-native */
Promise.prototype.always = function (cb) {
  return this.then(cb, cb);
};

Promise.prototype.done = function (cb, rejectCb) {
  if (!rejectCb) {
    rejectCb = res => {
      return res;
    };
  }
  return this.then(cb, rejectCb);
};

Promise.prototype.fail = function (cb) {
  return this.then(res => {
    return res;
  }, cb);
};
