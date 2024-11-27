package com.wakedata.dw.open.liteflow.component.api;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.liteflow.NodeRunTimeContext;
import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.liteflow.utils.LiteFlowUtils;
import com.wakedata.dw.open.model.api.flow.operator.simple.SimpleOperator;

/**
 * 用于测试
 *
 * @author ZhangXueJun
 * @title SimpleNodeComponent
 * @date 2021/5/8 9:40
 * @projectName dw-open
 * @description
 */
public class SimpleNodeComponent extends AbstractNodeComponent<SimpleOperator> {

    public static final String ID ="id";
    public static final String SLOT_INDEX ="slot_index";
    public static final String DATA ="data";


    @Override
    protected JSON simpleProcessInternal(NodeRunTimeContext nodeRunTimeContext, JSONObject currentRequestParams) {
        JSONObject jsonObject = getSimpleData(0);
        return jsonObject;
    }

    @Override
    protected OpenApiMsgCodeEnum getOpenApiMsgEnum() {
        return OpenApiMsgCodeEnum.s_error;
    }

    private JSONObject getSimpleData(int index) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(ID, LiteFlowUtils.nodeIdToOperatorId(this.getNodeId()) + "|"+ index);
        jsonObject.put(SLOT_INDEX, this.getSlotIndex());
        jsonObject.put(DATA, DATA + index);
        return jsonObject;
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
