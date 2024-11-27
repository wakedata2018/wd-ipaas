import BaseEnum from './enum-base';

class SourceCategory extends BaseEnum {
  constructor() {
    super();
    this._sink = {
      value: 'SINK',
      label: 'Sink'
    };
    this._dimension = {
      value: 'DIMENSION',
      label: i18n.t("dataSource.dimension")
    };
    this._datain = {
      value: 'DATAIN',
      label: i18n.t("dataSource.datain")
    };

    this._list.push(this._sink, this._dimension, this._datain);
  }

  get SINK() {
    return this._sink.value;
  }

  get DIMENSION() {
    return this._dimension.value;
  }

  get DATAIN() {
    return this._datain.value;
  }
}

export default new SourceCategory();
