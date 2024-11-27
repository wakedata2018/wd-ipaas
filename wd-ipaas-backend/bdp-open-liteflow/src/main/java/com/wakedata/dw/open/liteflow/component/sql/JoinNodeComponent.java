package com.wakedata.dw.open.liteflow.component.sql;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.liteflow.NodeRunTimeContext;
import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.model.api.flow.operator.sql.JoinTransformOperator;

/**
 * @author ZhangXueJun
 * @title UnionNodeComponent
 * @date 2021/5/8 9:41
 * @projectName dw-open
 * @description
 */
public class JoinNodeComponent extends AbstractNodeComponent<JoinTransformOperator> {

    @Override
    protected JSON simpleProcessInternal(NodeRunTimeContext nodeRunTimeContext,JSONObject currentRequestParams) throws Exception {
        return null;
    }

    @Override
    protected OpenApiMsgCodeEnum getOpenApiMsgEnum() {
        return OpenApiMsgCodeEnum.s_error;
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
