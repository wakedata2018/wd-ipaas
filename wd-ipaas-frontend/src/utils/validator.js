// import { isValidCron } from '@/utils/corn-validator';

/* eslint-disable no-useless-escape */
function nameValidator(rule, value, callback) {
  const patt = /^[_\-0-9A-Za-z\u4e00-\u9fa5\.]+$/i;
  if (value == null || value.trim() === '') {
    callback(new Error(window.i18n.t('validator.isNotAllowedToBeEmpty')));
  } else if (!patt.test(value)) {
    callback(new Error(window.i18n.t('validator.nameWithChineseValidateDesc')));
  } else if (value.length < 1 || value.length > 50) {
    callback(new Error(window.i18n.t('validator.lengthLimitDesc')));
  } else {
    callback();
  }
}
function enNameValidator(rule, value, callback) {
  const patt = /^[A-Za-z][_0-9A-Za-z]+$/i;
  if (value == null || value.trim() === '') {
    callback(new Error('步骤名称不能为空'));
  } else if (!patt.test(value)) {
    callback(new Error('格式错误'));
  } else if (value.length < 1 || value.length > 30) {
    callback(new Error('长度在 1 到 30 个字'));
  } else {
    callback();
  }
}
function nameValidatorFun(value, name) {
  const patt = /^[_\-0-9A-Za-z\u4e00-\u9fa5\.]+$/i;
  if (value == null || value.trim() === '') {
    return {
      result: false,
      message: name + window.i18n.t('validator.isNotAllowedToBeEmpty'),
    };
  } else if (!patt.test(value)) {
    return {
      result: false,
      message: name + window.i18n.t('validator.nameWithChineseValidateDesc'),
    };
  } else if (value.length < 1 || value.length > 50) {
    return {
      result: false,
      message: name + window.i18n.t('validator.lengthLimitDesc'),
    };
  } else {
    return {
      result: true,
      message: '',
    };
  }
}
function enNameValidatorFun(value, name) {
  const patt = /^[_0-9A-Za-z\.\-]+$/i;
  if (value == null || value.trim() === '') {
    return {
      result: false,
      message: name + window.i18n.t('validator.isNotAllowedToBeEmpty'),
    };
  } else if (!patt.test(value)) {
    return {
      result: false,
      message: name + window.i18n.t('validator.nameWithoutChineseValidateDesc'),
    };
  } else if (value.length < 1 || value.length > 50) {
    return {
      result: false,
      message: name + window.i18n.t('validator.lengthLimitDesc'),
    };
  } else {
    return {
      result: true,
      message: '',
    };
  }
}
function portValidator(rule, value, callback) {
  if (value == null || value === '') {
    callback(new Error(window.i18n.t('validator.isNotAllowedToBeEmpty')));
  } else if (value < 0 || value > 65536) {
    callback(new Error(window.i18n.t('validator.portLimitDesc')));
  } else {
    callback();
  }
}
function ipValidator(rule, value, callback) {
  const patt = /^((25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d)))\.){3}(25[0-5]|2[0-4]\d|((1\d{2})|([1-9]?\d)))$/i;
  if (value == null || value === '') {
    callback(new Error(window.i18n.t('validator.isNotAllowedToBeEmpty')));
  } else if (!patt.test(value)) {
    callback(new Error(window.i18n.t('validator.ipValidator')));
  } else {
    callback();
  }
}

function maxLengthValidator(maxValue) {
  return (rule, value, callback) => {
    if (value.length < 0 || value.length > maxValue) {
      callback(
        new Error(
          window.i18n.t('validator.maxLengthLimitDescPre') +
            maxValue +
            window.i18n.t('validator.maxLengthLimitDescAfter')
        )
      );
    } else {
      callback();
    }
  };
}
function valueRangeValidator(minValue, maxValue, name = '') {
  return (rule, value, callback) => {
    if (value < minValue || value > maxValue) {
      callback(
        new Error(
          name +
            window.i18n.t('validator.numberMinValidator') +
            minValue +
            ',' +
            window.i18n.t('validator.numberMaxValidator') +
            maxValue
        )
      );
    } else {
      callback();
    }
  };
}
function valueRangeValidatorFunc(value, minValue, maxValue, name = '') {
  if (value < minValue || value > maxValue) {
    return {
      result: false,
      message:
        name +
        window.i18n.t('validator.numberMinValidator') +
        minValue +
        ',' +
        window.i18n.t('validator.numberMaxValidator') +
        maxValue,
    };
  } else {
    return {
      result: true,
      message: '',
    };
  }
}
function fieldEnNameValidator(rule, value, callback) {
  const patt = /^[A-Za-z]+[_0-9A-Za-z]*$/i;
  if (value == null || value.trim() === '') {
    callback(new Error(window.i18n.t('validator.isNotAllowedToBeEmpty')));
  } else if (!patt.test(value)) {
    callback(new Error(window.i18n.t('validator.fieldNameDesc')));
  } else if (value.length < 1 || value.length > 256) {
    callback(new Error(window.i18n.t('validator.lengthLimitDesc')));
  } else {
    callback();
  }
}
function fieldEnNameValidatorFun(value, name) {
  const patt = /^[A-Za-z]+[_0-9A-Za-z]*$/i;
  if (value == null || value.trim() === '') {
    return {
      result: false,
      message: name + window.i18n.t('validator.isNotAllowedToBeEmpty'),
    };
  } else if (!patt.test(value)) {
    return {
      result: false,
      message: name + window.i18n.t('validator.fieldNameDesc'),
    };
  } else if (value.length < 1 || value.length > 256) {
    return {
      result: false,
      message: name + window.i18n.t('validator.lengthLimitDesc'),
    };
  } else {
    return {
      result: true,
      message: '',
    };
  }
}
function httpFieldEnNameValidator(rule, value, callback) {
  const patt = /^[-_0-9A-Za-z]*[A-Za-z]+[-_0-9A-Za-z]*$/i;
  if (value == null || value.trim() === '') {
    callback(new Error(window.i18n.t('validator.isNotAllowedToBeEmpty')));
  } else if (!patt.test(value)) {
    callback(new Error('须符合HTTP参数命名规则'));
  } else if (value.length < 1 || value.length > 50) {
    callback(new Error(window.i18n.t('validator.lengthLimitDesc')));
  } else {
    callback();
  }
}

function variableNameValidator(rule, value, callback) {
  if (value == null || value.trim() === '') {
    callback(new Error(window.i18n.t('validator.isNotAllowedToBeEmpty')));
  } else if (value.length < 1 || value.length > 50) {
    callback(new Error(window.i18n.t('validator.lengthLimitDesc')));
  } else {
    callback();
  }
}
function httpFieldEnNameValidatorFun(value, name) {
  const patt = /^[-_0-9A-Za-z]*[A-Za-z]+[-_0-9A-Za-z]*$/i;
  if (value == null || value.trim() === '') {
    return {
      result: false,
      message: name + window.i18n.t('validator.isNotAllowedToBeEmpty'),
    };
  } else if (!patt.test(value)) {
    return {
      result: false,
      message: name + window.i18n.t('validator.httpFieldNameDesc'),
    };
  } else if (value.length < 1 || value.length > 15) {
    return {
      result: false,
      message: name + window.i18n.t('validator.queryLengthLimitDesc'),
    };
  } else {
    return {
      result: true,
      message: '',
    };
  }
}
function chineseNameValidator(rule, value, callback) {
  const regRule = /^(?:[\u4e00-\u9fa5·]{1,30})$/;
  if (!value || value.length === 0) {
    callback(new Error('请输入中文名'));
  } else if (!regRule.test(value)) {
    callback(new Error('请输入中文'));
  } else if (value.length > 30) {
    callback(new Error('最多输入30个字'));
  } else {
    callback();
  }
}
const crontabValidator = (rule, value, callback) => {
  if (!value || value.length === 0) {
    callback(new Error('请输入crontab表达式'));
  } else {
    callback();
  }
};

const stepNameValidator = (data, name, operatorId) => {
  const isFind = Object.keys(data).some(key => {
    return data[key].name === name && key !== this.operatorId;
  });
  if (isFind) {
    throw new Error('步骤名称不能重复，请重新设置。');
  }
};
export default {
  nameValidator,
  enNameValidator,
  nameValidatorFun,
  enNameValidatorFun,
  portValidator,
  ipValidator,
  maxLengthValidator,
  fieldEnNameValidator,
  fieldEnNameValidatorFun,
  valueRangeValidator,
  valueRangeValidatorFunc,
  httpFieldEnNameValidator,
  variableNameValidator,
  httpFieldEnNameValidatorFun,
  inputMaxLength: 256,
  textAreaMaxLength: 65535,
  chineseNameValidator,
  crontabValidator,
};
