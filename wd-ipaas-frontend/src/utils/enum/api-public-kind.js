import BaseEnum from './enum-base';
/**
 * API可见范围
 */
class ApiPublicKind extends BaseEnum {
  constructor() {
    super();
    this._share = {
      value: 'share',
      label: '工作空间'
    };
    this._self = {
      value: 'self',
      label: '不可见'
    };
   
    this._list.push(this._share, this._self);
  }

  get SHARE() {
    return this._share.value;
  }

  get SELF() {
    return this._self.value;
  }
}

export default new ApiPublicKind();
