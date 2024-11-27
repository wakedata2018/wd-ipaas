import { object } from 'dss-common';

export default class EnumBase {
  get list() {
    return this._list;
  }

  constructor() {
    this._list = [];
  }

  getLabel(type) {
    let valueLabel = null;
    if (object.isNumber(type)) {
      valueLabel = this.list.find(item => item.key === type);
    } else {
      valueLabel = this.list.find(item => item.value === type);
    }
    if (valueLabel) {
      return valueLabel.label;
    }
    return '';
  }

  getItem(val) {
    return this._list.find(item => item.value === val);
  }
}
