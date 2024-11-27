package com.wakedata.dw.open.liteflow.component.script;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.liteflow.NodeRunTimeContext;
import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.model.api.flow.operator.script.DataConvert;
import com.wakedata.dw.open.model.api.flow.operator.script.GroovyScriptTransformOperator;
import groovy.lang.GroovyClassLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Objects;

/**
 * @author ZhangXueJun
 * @title GovveryScriptComponent
 * @date 2021/5/7 16:33
 * @projectName dw-open
 * @description
 */
@Slf4j
public class GroovyScriptNodeComponent extends AbstractNodeComponent<GroovyScriptTransformOperator> {

    private final GroovyClassLoader classLoader = new GroovyClassLoader(this.getClass().getClassLoader());

    @Override
    protected Pair<Boolean, JSONObject> redirectFromMultiOriginalInput() {
        return ImmutablePair.of(Boolean.FALSE, threadLocal.get().getApiFlowSlot().getFullOperatorResults());
    }

    @Override
    protected JSON simpleProcessInternal(NodeRunTimeContext nodeRunTimeContext, JSONObject currentRequestParams) throws Exception {
        GroovyScriptTransformOperator operator = (GroovyScriptTransformOperator) nodeRunTimeContext.getOperator();
        return executeScript(operator, currentRequestParams);
    }

    @Override
    protected OpenApiMsgCodeEnum getOpenApiMsgEnum() {
        return OpenApiMsgCodeEnum.w_api_graph_execute_groovy_script_operator_error;
    }

    public JSON executeScript(GroovyScriptTransformOperator operator, JSONObject requestParams) throws Exception {
        Class groovyClass = classLoader.parseClass((Objects.requireNonNull(operator.getGroovy())));
        DataConvert dataConvert = (DataConvert) groovyClass.newInstance();
        return dataConvert.convert(requestParams);
    }

    @Override
    protected void releaseResource() {
        if (classLoader == null) {
            return;
        }
        try {
            classLoader.close();
            addLog("释放资源成功!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            addLog("释放资源失败!");
            addLog(ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public void processCall() throws Exception {
        // 1、处理请求参数映射
        beforeProcessInternal();

        // 2、内部处理算子
        processInternal();

        // 3、处理响应参数映射
        afterProcessInternal();
    }
}
