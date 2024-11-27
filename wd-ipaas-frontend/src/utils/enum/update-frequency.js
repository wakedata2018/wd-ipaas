import BaseEnum from './enum-base';

class UpdateFrequency extends BaseEnum {
  constructor() {
    super();
    this._hour = {
      value: 'HOUR',
      label: 'HOUR'
    };
    this._day = {
      value: 'DAY',
      label: 'DAY'
    };
    this._week = {
      value: 'WEEK',
      label: 'WEEK'
    };
    this._month = {
      value: 'MONTH',
      label: 'MONTH'
    };
    this._year = {
      value: 'YEAR',
      label: 'YEAR'
    };

    this._list.push(this._hour, this._day, this._week, this._month, this._year);
  }

  get HOUR() {
    return this._hour.value;
  }

  get DAY() {
    return this._day.value;
  }

  get WEEK() {
    return this._week.value;
  }

  get MONTH() {
    return this._month.value;
  }

  get YEAR() {
    return this._year.value;
  }

}

export default new UpdateFrequency();
