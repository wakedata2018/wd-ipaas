import BaseEnum from './enum-base';

class SecretType extends BaseEnum {
  constructor() {
    super();
    this._public = {
      value: 'PUBLIC',
      label: '公开'
    };
    this._private = {
      value: 'PRIVATE',
      label: '加密'
    };

    this._list.push(this._public, this._private);
  }

  get PUBLIC() {
    return this._public.value;
  }

  get PRIVATE() {
    return this._private.value;
  }
}

export default new SecretType();
