import BaseEnum from './enum-base';

const inPoints = [
  [0, 0.5],
  [0.5, 0],
];
const outPoints = [
  [1, 0.5],
  [0.5, 1],
];

class EdgeType extends BaseEnum {
  constructor() {
    super();
    this._withoutInput = { value: 'withoutInput', label: 'withoutInput' };
    this._input = { value: 'input', label: 'input' };
    this._multiInput = { value: 'multiInput', label: 'multiInput' };

    this._withoutOutput = { value: 'withoutOutput', label: 'withoutOutput' };
    this._output = { value: 'output', label: 'output' };
    this._multiOutput = { value: 'multiOutput', label: 'multiOutput' };

    this._list.push(this._full, this._inc);
  }

  getPoints(typeOfEdge) {
    const config = {};
    if (!typeOfEdge || typeOfEdge.length === 0) {
      return config;
    }

    typeOfEdge.forEach(type => {
      switch (type) {
        case this._input.value:
          config.inPoints = inPoints;
          break;
        case this._multiInput.value:
          config.inPoints = inPoints;
          break;
        case this._output.value:
          config.outPoints = outPoints;
          break;
        case this._multiOutput.value:
          config.outPoints = outPoints;
          break;
        default:
          break;
      }
    });
    return config;
  }

  getProp(typeOfEdge) {
    for (const val of typeOfEdge) {
      switch (val) {
        case this._output.value:
          return 'outputOperator';
        case this._multiOutput.value:
          return 'outputOperators';
        default:
          break;
      }
    }
    return '';
  }

  isInput(typeOfEdge) {
    return typeOfEdge.indexOf(this._input.value) > -1;
  }

  isOutput(typeOfEdge) {
    return typeOfEdge.indexOf(this._output.value) > -1;
  }
}

export default new EdgeType();
