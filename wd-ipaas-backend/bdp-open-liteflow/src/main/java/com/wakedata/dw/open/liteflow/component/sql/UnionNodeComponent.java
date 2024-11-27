package com.wakedata.dw.open.liteflow.component.sql;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.liteflow.NodeRunTimeContext;
import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.model.api.flow.operator.sql.UnionTransformOperator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;


/**
 * @author ZhangXueJun
 * @title UnionNodeComponent
 * @date 2021/5/8 9:41
 * @projectName dw-open
 * @description
 */
@Slf4j
public class UnionNodeComponent extends AbstractNodeComponent<UnionTransformOperator> {

    @Override
    protected Pair<Boolean, JSONObject> redirectFromMultiOriginalInput() {
        return ImmutablePair.of(Boolean.TRUE, null);
    }

    @Override
    protected JSON simpleProcessInternal(NodeRunTimeContext nodeRunTimeContext, JSONObject currentRequestParams) {
        JSONArray jsonArray = new JSONArray();

        for (Map.Entry<String, Object> mapEntry : currentRequestParams.entrySet()) {
            JSON inputData = (JSON) mapEntry.getValue();
            if (inputData instanceof JSONObject) {
                jsonArray.add(JSON.parse(inputData.toJSONString()));
            } else {
                JSONArray inputArrays = (JSONArray) inputData;
                if (inputData == null || inputArrays.size() == 0) {
                    continue;
                }
                jsonArray.addAll((JSONArray) JSON.parse(inputArrays.toJSONString()));
            }
        }
        return jsonArray;
    }

    @Override
    protected OpenApiMsgCodeEnum getOpenApiMsgEnum() {
        return OpenApiMsgCodeEnum.w_api_graph_execute_union_all_operator_error;
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
