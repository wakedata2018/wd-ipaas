enum RuleStatus {
  CLOSE,
  OPEN,
}

const StatusOptions = [
  {
    label: '未启用',
    value: RuleStatus.CLOSE,
  },
  {
    label: '启用',
    value: RuleStatus.OPEN,
  },
];

const DefaultRuleStatusOptions = [
  {
    label: '是',
    value: true,
  },
  {
    label: '否',
    value: false,
  },
];

// 集成云返回提参数结构
interface ParamsMappingInfo {
  id?: number;
  respParamMappingRuleName: string;
  description: string;
  status: RuleStatus;
  respParamMappingRuleJsonSchema: string;
}

// 系统返回体参数结构
interface CommonParams {
  datasourceTableColumnName: string;
  datasourceTableColumnType: string;
  datasourceTableColumnDesc: string;
}

export { StatusOptions, DefaultRuleStatusOptions, RuleStatus, ParamsMappingInfo, CommonParams };
