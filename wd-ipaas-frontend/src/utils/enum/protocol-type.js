import BaseEnum from './enum-base';

class ProtocolType extends BaseEnum {
  constructor() {
    super();

    this._https = {
      value: 'HTTPS',
      label: 'HTTPS',
    };
    this._list.push(this._https);
  }

  get HTTPS() {
    return this._https.value;
  }
}

export default new ProtocolType();
