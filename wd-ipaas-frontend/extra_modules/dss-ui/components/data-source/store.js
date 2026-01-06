const store = {
  debug: true,
  state: {
    sourceType: []
  },
  setSourceTypeAction(newValue) {
    // if (this.debug) console.log('setSourceTypeAction triggered with', newValue);
    this.state.sourceType = newValue;
  }
};

export default store;
