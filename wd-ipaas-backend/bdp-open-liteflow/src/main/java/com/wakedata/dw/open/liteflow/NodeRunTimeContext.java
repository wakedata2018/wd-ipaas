package com.wakedata.dw.open.liteflow;

import com.alibaba.fastjson.JSON;
import com.wakedata.dw.open.liteflow.slot.ApiFlowSlot;
import com.wakedata.dw.open.liteflow.utils.LiteFlowUtils;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.service.api.DataApiAccessService;
import com.wakedata.dw.open.service.api.GlobalParamService;
import com.yomahub.liteflow.core.NodeComponent;
import lombok.Data;

import java.util.Set;

/**
 * @author ssg
 * 流程编排运行时上下文
 */
@Data
public class NodeRunTimeContext {

    /**
     * 当前算子
     */
    private DAGTaskEngine.OperatorContext operatorContext;

    /**
     * 所有算子
     */
    private DAGTaskEngine.OperatorContainer operatorContainer;

    private DataApiAccessService dataApiAccessService;

    private GlobalParamService globalParamService;

    /**
     * 应用id
     */
    private Integer appId;

    private Set<String> inputEdges;

    /**
     * 上下文数据
     */
    private ApiFlowSlot apiFlowSlot;

    /**
     * 当前算子
     */
    private Operator operator;

    /**
     * 当前算子请求参数:
     * 初始为JSONObject,
     * 后续可能会是JSONArray参数
     */
    protected JSON currentRequestParams;

    public void init(NodeComponent component) {
        ApiFlowSlot apiFlowSlot = component.getContextBean(ApiFlowSlot.class);
        setApiFlowSlot(apiFlowSlot);
        DAGTaskEngine.OperatorContainer operatorContainer = component.getRequestData();
        setOperatorContainer(operatorContainer);
        setAppId(operatorContainer.getAppId());
        String nodeId = LiteFlowUtils.nodeIdToOperatorId(component.getNodeId());
        DAGTaskEngine.OperatorContext operatorContext = operatorContainer.getOperatorContext(nodeId);
        setOperatorContext(operatorContext);
        setDataApiAccessService(operatorContainer.getDataApiAccessService());
        setInputEdges(operatorContext.getInputEdges());
    }
}
