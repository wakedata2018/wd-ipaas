package com.wakedata.dw.open.operator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wujunqiang
 * @since 2022/11/24 10:15
 */
@Component
public class OperatorProcessorFactory {

    /**
     * 算子执行器map，key为com.wakedata.dw.open.model.api.flow.operator.Operator实现类class name
     */
    public Map<String, OperatorProcessor> operatorProcessorMap = new HashMap<>();

    public void register(String operatorClassName, OperatorProcessor processor) {
        operatorProcessorMap.put(operatorClassName, processor);
    }

    public OperatorProcessor getProcessor(String operatorClassName) {
        return StringUtils.isBlank(operatorClassName) ? null : operatorProcessorMap.get(operatorClassName);
    }

}
