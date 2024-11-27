import BaseEnum from './enum-base';

class ApiPublishStatus extends BaseEnum {
  constructor() {
    super();
    this._unPublish = {
      value: 'UN_PUBLISH',
      label: '未发布',
    };
    this._publish = {
      value: 'PUBLISH',
      label: '已发布',
    };

    this._list.push(this._unPublish, this._publish);
  }

  get PUBLISH() {
    return this._publish.value;
  }

  get UN_PUBLISH() {
    return this._unPublish.value;
  }
}

export default new ApiPublishStatus();
