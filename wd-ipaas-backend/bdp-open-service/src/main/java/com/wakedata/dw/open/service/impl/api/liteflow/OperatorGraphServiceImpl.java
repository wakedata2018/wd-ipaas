package com.wakedata.dw.open.service.impl.api.liteflow;

import com.wakedata.dw.open.enums.OperatorConditionEnum;
import com.wakedata.dw.open.liteflow.OperatorGraphLiteflowService;
import com.wakedata.dw.open.mapper.api.liteflow.OperatorAttributeMapper;
import com.wakedata.dw.open.model.api.flow.ApiFlowAttr;
import com.wakedata.dw.open.model.api.flow.operator.*;
import com.wakedata.dw.open.model.datasource.DataSourcePo;
import com.wakedata.dw.open.service.api.dto.LiteFlowAllOperatorTemplateDTO;
import com.wakedata.dw.open.service.api.flow.OperatorGraphService;
import com.wakedata.dw.open.service.datasource.DataSourceService;
import com.wakedata.dw.open.service.utils.AuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ZhangXueJun
 * @title OperatorGraphServiceImpl
 * @date 2021/5/11 16:27
 * @projectName dw-open
 * @description
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class OperatorGraphServiceImpl implements OperatorGraphService {

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private OperatorAttributeMapper operatorAttributeMapper;

    @Autowired
    private OperatorGraphLiteflowService operatorGraphLiteflowService;

    @Override
    public List<OperatorAttribute> listOperatorAttributes() {
        return operatorAttributeMapper.selectAll()
                .stream()
                .filter(OperatorAttribute::getUse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataSourcePo> apiComponents() {
        DataSourcePo dataSourcePo = new DataSourcePo();
        dataSourcePo.setLesseeId(AuthUtils.currentAppId());
        return dataSourceService.findByPo(dataSourcePo);
    }

    @Override
    public Operator saveOperator(Operator operator) {
        return null;
    }

    @Override
    public Boolean deleteOperator(Long apiId) {
        return null;
    }

    @Override
    public Set<String> dsfCycle(Map<String, Operator> operators) {
        return null;
    }

    @Override
    public List<LiteFlowAllOperatorTemplateDTO> getLiteFlowAllOperatorTemplateDTOList(ApiFlowAttr apiFlowAttr, String operatorType,Boolean isForBreak) {
        // 如果前端没传apiId，不应该返回参数
        Integer apiId = apiFlowAttr.getApiId();
        if (apiId == null) {
            return new ArrayList<>();
        }
        return operatorGraphLiteflowService.getLiteFlowAllOperatorTemplateDTOList(apiFlowAttr,operatorType,isForBreak);

    }

    @Override
    public Map<String, String> queryAllJudgeOperatorList() {
        //通过循环，获取已经使用的运算符
        Map<String, String> map = new HashMap<>();
        EnumSet<OperatorConditionEnum> weekSet = EnumSet.allOf(OperatorConditionEnum.class);
        for (OperatorConditionEnum operatorConditionEnum : weekSet) {
            if (Boolean.TRUE.equals(operatorConditionEnum.getEnabled())) {
                map.put(operatorConditionEnum.getCode(), operatorConditionEnum.getDesc());
            }
        }
        return map;
    }

}
