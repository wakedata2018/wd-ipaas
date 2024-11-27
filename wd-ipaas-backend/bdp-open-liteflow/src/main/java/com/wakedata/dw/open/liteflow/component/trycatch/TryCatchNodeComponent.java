package com.wakedata.dw.open.liteflow.component.trycatch;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.liteflow.NodeRunTimeContext;
import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.liteflow.slot.ApiFlowSlot;
import com.wakedata.dw.open.model.api.flow.operator.trycatch.TryCatchOperator;
import lombok.extern.slf4j.Slf4j;

/**
 * 捕获异常LiteFlow组件
 *
 * @author wujunqiang
 * @since 2023/3/15 14:33
 */
@Slf4j
public class TryCatchNodeComponent extends AbstractNodeComponent<TryCatchOperator> {

    @Override
    public void processCall() throws Exception {
        NodeRunTimeContext nodeRunTimeContext = threadLocal.get();
        TryCatchOperator tryCatchOperator = (TryCatchOperator) nodeRunTimeContext.getOperator();
        try {
            // 执行正常的try链
            String tryChainId = this.getCurrChainId() + tryCatchOperator.getTryTag();
            addLog(nodeRunTimeContext, String.format("执行开始，准备执行try子流程，执行链id: %s", tryChainId));
            this.invoke(tryChainId, null);
            addLog(nodeRunTimeContext, "try子流程执行完成");
        } catch (Exception e) {
            /*
                1. 子流程中的算子出现异常时，threadLocal中的上下文信息会被清除，所以在catch块不能通过threadLocal.get()获取上下文。
                    调用AbstractNodeComponent.addLog(String line)方法记录日志时拿不到上下文出现NPE，在catch块还是需要用到异常算子自身的上下文对象
                2. 捕获异常后，首先清除上下文中的异常信息，否则执行完编排后监测到存在异常信息会另外抛异常
             */
            ApiFlowSlot apiFlowSlot = nodeRunTimeContext.getApiFlowSlot();
            apiFlowSlot.setException(null);
            if (CollectionUtil.isNotEmpty(tryCatchOperator.getSubCatchOperators())) {
                String catchChainId = this.getCurrChainId() + tryCatchOperator.getCatchTag();
                addLog(nodeRunTimeContext, String.format("try子流程执行出现异常，准备执行catch子流程，执行链id: %s", catchChainId));
                this.invoke(catchChainId, null);
            } else {
                addLog(nodeRunTimeContext, "catch子流程中无内容");
            }
            addLog(nodeRunTimeContext, "catch子流程执行完成");
        } finally {
            // 只要调用this.invoke()方法，无论调用是否出现了异常，threadLocal中的内容都会被清掉，所以在最后还是要把上下文对象放入threadLocal中，不然在执行完processCall方法后后续打印日志会报NPE
            threadLocal.set(nodeRunTimeContext);
        }
    }

    @Override
    protected JSON simpleProcessInternal(NodeRunTimeContext nodeRunTimeContext, JSONObject currentRequestParams) throws Exception {
        return null;
    }

    @Override
    protected OpenApiMsgCodeEnum getOpenApiMsgEnum() {
        return OpenApiMsgCodeEnum.w_api_graph_execute_try_catch_operator_error;
    }

}
