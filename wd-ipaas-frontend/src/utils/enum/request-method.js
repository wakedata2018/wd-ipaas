import BaseEnum from './enum-base';

class RequestMethod extends BaseEnum {
  constructor() {
    super();
    this._get = {
      value: 'GET',
      label: 'GET',
    };
    this._post = {
      value: 'POST',
      label: 'POST',
    };
    this._put = {
      value: 'PUT',
      label: 'PUT',
    };
    this._del = {
      value: 'DELETE',
      label: 'DELETE',
    };

    this._list.push(this._get, this._post);
  }

  get GET() {
    return this._get.value;
  }

  get POST() {
    return this._post.value;
  }

  get PUT() {
    return this._put.value;
  }

  get DELETE() {
    return this._del.value;
  }
}

export default new RequestMethod();
