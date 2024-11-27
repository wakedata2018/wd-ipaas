import BaseEnum from './enum-base';

class ResponseContentType extends BaseEnum {
  constructor() {
    super();
    this._json = {
      value: 'JSON',
      label: 'JSON'
    };
    this._xml = {
      value: 'XML',
      label: 'XML'
    };

    this._list.push(this._json, this._xml);
  }

  get JSON() {
    return this._json.value;
  }

  get XML() {
    return this._xml.value;
  }
}

export default new ResponseContentType();
