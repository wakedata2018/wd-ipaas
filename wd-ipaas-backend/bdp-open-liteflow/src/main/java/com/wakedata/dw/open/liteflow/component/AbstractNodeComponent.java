package com.wakedata.dw.open.liteflow.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.exception.OperatorOpenException;
import com.wakedata.dw.open.liteflow.DAGTaskEngine;
import com.wakedata.dw.open.liteflow.NodeRunTimeContext;
import com.wakedata.dw.open.liteflow.component.param.AbstractParamMappingsStrategy;
import com.wakedata.dw.open.liteflow.component.param.ParamMappingsStrategyFactory;
import com.wakedata.dw.open.liteflow.component.transform.ColumnSelectNodeComponent;
import com.wakedata.dw.open.liteflow.component.transform.RowSelectNodeComponent;
import com.wakedata.dw.open.liteflow.slot.ApiFlowSlot;
import com.wakedata.dw.open.liteflow.utils.LiteFlowUtils;
import com.wakedata.dw.open.model.api.external.http.HttpExternalApiAttr;
import com.wakedata.dw.open.model.api.flow.operator.AbstractOperator;
import com.wakedata.dw.open.model.api.flow.operator.VertexOperator;
import com.wakedata.dw.open.model.api.flow.operator.branch.BranchOperator;
import com.wakedata.dw.open.model.api.flow.operator.edge.OutputEdge;
import com.wakedata.dw.open.model.api.flow.operator.edge.OutputMultiEdge;
import com.wakedata.dw.open.model.api.flow.operator.foreach.ForOperator;
import com.wakedata.dw.open.model.api.flow.operator.judge.JudgeOperator;
import com.wakedata.dw.open.model.api.flow.operator.trycatch.TryCatchOperator;
import com.wakedata.dw.open.parammapping.*;
import com.wakedata.dw.open.parammapping.util.JsonPathHelper;
import com.wakedata.dw.open.parammapping.util.ParamMappingsUtils;
import com.wakedata.dw.open.service.utils.RequestParamUtils;
import com.wakedata.dw.open.utils.JsonUtil;
import com.wakedata.dw.open.utils.threadlocal.LiteFlowStartParamThreadLocal;
import com.yomahub.liteflow.core.NodeComponent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.*;

/**
 * 通用服务编排组件
 *
 * @author ZhangXueJun
 * @title AbstractNodeComponent
 * @date 2021/5/7 16:21
 * @projectName dw-open
 * @description
 */
@Slf4j
public abstract class AbstractNodeComponent<Operator extends AbstractOperator> extends NodeComponent {

    public static ThreadLocal<NodeRunTimeContext> threadLocal = new ThreadLocal<>();

    /**
     * 需要执行的算子Id集合key
     */
    protected static final String KEY_AFTER_JUDGE_OPERATOR_IDS = "AfterJudgeOperatorIds";

    private static ExecutorService thread_poll = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 3,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(10000),
            new ThreadFactoryBuilder()
                    .setNameFormat("node-component-exec-%d").build()
    );

    /**
     * 处理模板，异常捕获及资源释放，子类需实现内部的processCall方法，实现具体业务
     * @throws Exception
     */
    @Override
    public void process() throws Exception {
        try {
            addLog("执行开始!");
            //具体执行
            processCall();
            addLog("执行成功!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            addLog("执行异常!\n" + ExceptionUtils.getStackTrace(e));
            OperatorOpenException openException = createOperatorOpenException(e);
            ApiFlowSlot apiFlowSlot = threadLocal.get().getApiFlowSlot();
            if (apiFlowSlot.getException() == null) {
                apiFlowSlot.setException(openException);
                throw openException;
            }
            throw apiFlowSlot.getException();
        } finally {
            releaseResource();
            threadLocal.remove();
        }
    }

    /**
     * 具体执行逻辑
     * @throws Exception
     */
    public abstract void processCall() throws Exception;


    private OperatorOpenException createOperatorOpenException(Exception e) {
        return new OperatorOpenException(threadLocal.get().getOperator().getName(), getOpenApiMsgEnum(), e);
    }

    /**
     * 通过算子Id迭代获取后续出边算子的Id，放入公共参数池中，遇到判断算子和没有出边的算子则停
     * 开始算子只能选择一个，不会出现在判断算子之后，后续增加了其它算子类型需要考虑增加类型处理
     *
     * @param operatorId 算子Id
     */
    protected void iteratorSetOutputOperators(String operatorId) {
        NodeRunTimeContext nodeRunTimeContext = threadLocal.get();
        ApiFlowSlot apiFlowSlot = nodeRunTimeContext.getApiFlowSlot();
        com.wakedata.dw.open.model.api.flow.operator.Operator operator = nodeRunTimeContext.getOperatorContainer().getOperator(operatorId);
        //若当前算子是判断算子或分支算子，将算子Id存放到公共参数池后停止递归
        if (operator instanceof JudgeOperator || operator instanceof BranchOperator) {
            apiFlowSlot.getAfterJudgeOperatorIds().add(operatorId);
            return;
        }
        //若当前算子是实现了多出边接口，则遍历当前算子的出边算子Id存放到公共参数池
        if (OutputMultiEdge.class.isAssignableFrom(operator.getClass())) {
            //将当前算子Id加入AfterJudgeOperatorIds
            apiFlowSlot.getAfterJudgeOperatorIds().add(operatorId);
            Set<String> outputOperators = ((OutputMultiEdge<?>) operator).getOutputOperators();
            for (String outputOperator : outputOperators) {
                apiFlowSlot.getAfterJudgeOperatorIds().add(outputOperator);
                iteratorSetOutputOperators(outputOperator);
            }
        }
        //若当前算子是实现的是单出边接口，则递归遍历当前算子的出边算子Id存放到公共参数池
        if (OutputEdge.class.isAssignableFrom(operator.getClass())) {
            String outputOperator = ((OutputEdge<?>) operator).getOutputOperator();
            apiFlowSlot.getAfterJudgeOperatorIds().add(outputOperator);
            iteratorSetOutputOperators(outputOperator);
        }
    }

    /**
     * 通过父算子id，迭代后续出边算子id，放入公共参数池的afterJudgeOperatorIds属性中
     *
     * @param currentOperatorId 当前运行的算子id
     * @param parentOperatorId  父算子id
     */
    protected void iteratorSetOutputOperatorsFromSubElement(String currentOperatorId, String parentOperatorId) {
        if (StringUtils.isBlank(parentOperatorId)) {
            return;
        }
        String conversionParentOperatorId = conversionParentOperatorId(parentOperatorId);
        com.wakedata.dw.open.model.api.flow.operator.Operator operator = threadLocal.get().getOperatorContainer().getOperator(conversionParentOperatorId);
        if (operator == null) {
            return;
        }
        if (operator instanceof TryCatchOperator) {
            TryCatchOperator tryCatchOperator = (TryCatchOperator) operator;
            // 如果当前算子处于try子流程内，需要把catch流程中的算子id以及异常算子后续的算子id都放入到afterJudgeOperatorIds集合中
            if (tryCatchOperator.getSubTryOperators().contains(currentOperatorId)) {
                for (String subCatchOperatorId : tryCatchOperator.getSubFirstCatchOperators()) {
                    iteratorSetOutputOperators(subCatchOperatorId);
                }
            }
            iteratorSetOutputOperators(tryCatchOperator.getOperatorId());
        }
        if (operator instanceof ForOperator && ((ForOperator) operator).getSubOperators().contains(currentOperatorId)) {
            iteratorSetOutputOperators(((ForOperator) operator).getOperatorId());
        }
    }

    protected void releaseResource() {
        releaseStartParamThreadLocal();
    }

    /**
     * 处理请求参数映射
     */
    protected void beforeProcessInternal() throws Exception {
        NodeRunTimeContext nodeRunTimeContext = threadLocal.get();
        JSON currentRequestParams;
        List<RequestParamMapping> requestParamMappings = getRequestParamMappings();
        if (CollectionUtils.isEmpty(requestParamMappings)) {
            JSONObject requestParams = new JSONObject();
            Pair<Boolean, JSONObject> fromMultiOriginalInput = redirectFromMultiOriginalInput();
            if (fromMultiOriginalInput.getLeft()) {
                for (String inputEdge : nodeRunTimeContext.getInputEdges()) {
                    Operator inputOperator = (Operator) nodeRunTimeContext.getOperatorContainer().getOperator(inputEdge);
                    JSON input = getOperatorResultSet(nodeRunTimeContext, inputOperator.getName());
                    requestParams.put(inputOperator.getName(), input);

                    if (MapUtils.isNotEmpty(fromMultiOriginalInput.getRight())) {
                        // 预留脚本算子：请求参数包含全局参数
                        requestParams.put(VertexOperator.VERTEX_OPERATOR_ID, fromMultiOriginalInput.getRight());
                    }
                }
            }
            if (fromMultiOriginalInput.getRight() != null) {
                currentRequestParams = fromMultiOriginalInput.getRight();
            } else {
                currentRequestParams = requestParams;
            }
        } else {
            transformParamMappings(requestParamMappings);
            AbstractParamMappingsStrategy requestParamStrategy = ParamMappingsStrategyFactory.getRequestParamStrategy(nodeRunTimeContext);
            currentRequestParams = requestParamStrategy.getParamMappingsResult(requestParamMappings);
            currentRequestParams = afterMappingRequestParams(currentRequestParams);
        }
        nodeRunTimeContext.setCurrentRequestParams(currentRequestParams);
        addLog("获取请求参数信息成功！ \n" + ParamMappingsUtils.prettyFormatJson(currentRequestParams));
    }

    /**
     * 是否直接来源输入边 结果集，比如union all，脚本等
     *
     * @return
     */
    protected Pair<Boolean, JSONObject> redirectFromMultiOriginalInput() {
        return ImmutablePair.of(Boolean.FALSE, null);
    }


    protected void transformParamMappings(List<? extends AbstractParamMapping> paramMappings) {
        for (Iterator<? extends AbstractParamMapping> iterator = paramMappings.iterator(); iterator.hasNext(); ) {
            AbstractParamMapping paramMapping = iterator.next();
            if (paramMapping == null) {
                continue;
            }
            String type = paramMapping.getType();
            // 表达式不为空或者类型为固定值都不移除映射参数
            if (StringUtils.isNotEmpty(paramMapping.getExpression()) || ParamMappingTypeEnum.FIXED_TYPE.getType().equals(type)) {
                // 如果参数映射使用的是引用值，需要判断表达式是否为json对象，如果是json对象需要把表达式中所有的id都取出来
                if (ParamMappingTypeEnum.REFERENCE_TYPE.getType().equals(type)) {
                    parseOperatorIdToName(paramMapping);
                }
                // 转换回 operator name
                String operatorId = paramMapping.getOperatorId();
                Operator operator = (Operator) threadLocal.get().getOperatorContainer().getOperator(operatorId);
                paramMapping.setOperatorId(operator.getName());
                continue;
            }
            iterator.remove();
        }
    }

    /**
     * 提供钩子，再次处理请求参数映射结果
     *
     * @param currentRequestParams
     * @return
     */
    protected JSON afterMappingRequestParams(JSON currentRequestParams) {
        return currentRequestParams;
    }

    /**
     * 获取请求参数映射列表
     *
     * @return
     */
    protected List<RequestParamMapping> getRequestParamMappings() {
        return Lists.newArrayList();
    }

    /**
     * 内部处理算子
     *
     * @throws Exception
     */
    protected void processInternal() throws Exception {
        NodeRunTimeContext nodeRunTimeContext = threadLocal.get();
        JSON currentRequestParams = nodeRunTimeContext.getCurrentRequestParams();
        if (currentRequestParams instanceof JSONObject) {
            JSON results = simpleProcessInternal(nodeRunTimeContext, (JSONObject) currentRequestParams);
            //将单个运行的api结果存放到全局参数里面
            storeOperatorResultSet(nodeRunTimeContext, results);
            addLog("单次请求处理成功、返回结果!\n" + ParamMappingsUtils.prettyFormatJson(results));
        } else {
            // 当请求参数映射为JSONArray、应循环处理
            // TODO 执行优化，api condition？
            List<Pair<Future<JSON>, JSONObject>> futures = Lists.newArrayList();
            JSONArray jsonArray = (JSONArray) currentRequestParams;

            addLog("并发请求处理参数 \n" + ParamMappingsUtils.prettyFormatJson(jsonArray) + "\n 大小 " + jsonArray.size());

            if (jsonArray.size() > 10) {
                log.warn("并发执行任务数超过10次：size {}, appId {}, operatorName {}", jsonArray.size(), nodeRunTimeContext.getAppId(), nodeRunTimeContext.getOperator().getName());
            }
            for (Object requestParam : jsonArray) {
                Future<JSON> future = thread_poll.submit(() -> simpleProcessInternal(nodeRunTimeContext, (JSONObject) requestParam));
                futures.add(ImmutablePair.of(future, (JSONObject) requestParam));
            }

            JSONArray results = new JSONArray();
            Iterator<Pair<Future<JSON>, JSONObject>> iterator = futures.iterator();
            while (iterator.hasNext()) {
                Pair<Future<JSON>, JSONObject> pair = iterator.next();
                JSON json = pair.getLeft().get(30, TimeUnit.SECONDS);
                if (json != null && json instanceof JSONArray) {
                    results.addAll((Collection<? extends Object>) json);
                } else {
                    results.add(json);
                }
                iterator.remove();
                addLog("并发请求单次处理参数:\n" + ParamMappingsUtils.prettyFormatJson(pair.getRight()) + ", 请求结果:\n" + ParamMappingsUtils.prettyFormatJson(json));
            }
            storeOperatorResultSet(nodeRunTimeContext, results);
        }
    }

    /**
     * 当请求参数为JSONObject、简单处理即可
     *
     * @param currentRequestParams
     * @return
     * @throws Exception
     */
    protected abstract JSON simpleProcessInternal(NodeRunTimeContext nodeRunTimeContext,
                                                  JSONObject currentRequestParams) throws Exception;

    /**
     * 是否需要对结果再次转换。一般{@link RowSelectNodeComponent} & {@link ColumnSelectNodeComponent}
     *
     * @return
     */
    protected List<ResponseParamMappings> getResponseParamMappings() {
        return Lists.newArrayList();
    }

    /**
     * 处理响应参数映射
     */
    protected void afterProcessInternal() throws Exception {
        NodeRunTimeContext nodeRunTimeContext = threadLocal.get();
        List<ResponseParamMappings> responseParamMappings = getResponseParamMappings();
        if (CollectionUtils.isEmpty(responseParamMappings)) {
            return;
        }

        transformParamMappings(responseParamMappings);

        // 无需处理映射
        addLog("结果映射配置信息:\n" + JsonUtil.toJson(responseParamMappings));


        AbstractParamMappingsStrategy requestParamStrategy
                = ParamMappingsStrategyFactory.getRequestParamStrategy(nodeRunTimeContext);
        JSON mappingJson = requestParamStrategy.getParamMappingsResult(responseParamMappings);

        JSON originJson = nodeRunTimeContext.getApiFlowSlot().getOperatorResultSet(nodeRunTimeContext.getOperator().getName());

        addLog("结果映射成功，原始数据：\n" + ParamMappingsUtils.prettyFormatJson(originJson)
                + "\n映射后数据：" + ParamMappingsUtils.prettyFormatJson(mappingJson)
        );

        // 重新覆盖值
        storeOperatorResultSet(nodeRunTimeContext, mappingJson);
    }

    /**
     * 触发动作前
     *
     * @return
     */
    @Override
    public boolean isAccess() {
        // 初始化运行时参数
        NodeRunTimeContext nodeRunTimeContext = new NodeRunTimeContext();

        ApiFlowSlot apiFlowSlot = this.getContextBean(ApiFlowSlot.class);
        if (apiFlowSlot.isHappenError()) {
            return false;
        }
        nodeRunTimeContext.setApiFlowSlot(apiFlowSlot);
        DAGTaskEngine.OperatorContainer operatorContainer = this.getRequestData();
        if (operatorContainer == null) {
            return false;
        }
        if (operatorContainer.isEmpty()) {
            return false;
        }
        nodeRunTimeContext.setOperatorContainer(operatorContainer);

        nodeRunTimeContext.setAppId(operatorContainer.getAppId());

        String nodeId = LiteFlowUtils.nodeIdToOperatorId(this.getNodeId());
        DAGTaskEngine.OperatorContext operatorContext = operatorContainer.getOperatorContext(nodeId);
        if (operatorContext == null) {
            return false;
        }
        Operator operator = (Operator) operatorContext.getOperator();
        // 如果前面的流程执行过判断算子或分支算子，且当前算子被判断为不执行的算子返回false
        if (apiFlowSlot.getIsHaveJudgeOperatorBefore() && !apiFlowSlot.getAfterJudgeOperatorIds().contains(operator.getOperatorId())) {
            return false;
        }
        nodeRunTimeContext.setGlobalParamService(operatorContainer.getGlobalParamService());
        nodeRunTimeContext.setOperator(operatorContext.getOperator());
        nodeRunTimeContext.setOperatorContext(operatorContext);
        nodeRunTimeContext.setDataApiAccessService(operatorContainer.getDataApiAccessService());
        nodeRunTimeContext.setInputEdges(operatorContext.getInputEdges());
        threadLocal.set(nodeRunTimeContext);
        return super.isAccess();
    }

    /**
     * 移除start节点的ThreadLocal
     */
    public void releaseStartParamThreadLocal() {
        LiteFlowStartParamThreadLocal.removeStartParam();
    }

    /**
     * 工具方法
     *
     * @param results
     */
    protected void storeOperatorResultSet(NodeRunTimeContext nodeRunTimeContext, Object results) {
        nodeRunTimeContext.getApiFlowSlot().storeOperatorResultSet(nodeRunTimeContext.getOperator().getName(), nodeRunTimeContext.getOperatorContext().isSink(), results);
    }

    protected JSON getOperatorResultSet(NodeRunTimeContext nodeRunTimeContext, String operatorName) {
        return nodeRunTimeContext.getApiFlowSlot().getOperatorResultSet(operatorName);
    }

    /**
     * 根据当前API的请求参数，再次封装成集成开发平台定义的参数JSON
     *
     * @param origin        请求参数json
     * @param fieldMappings 请求参数映射MAP
     * @return 集成开发平台定义的参数JSON
     */
    protected JSONObject assembleHttpRequestParams(JSONObject origin, Map<String, RequestParamMapping> fieldMappings) {
        JSONObject result = new JSONObject();
        result.put(HttpParamKind.QUERY.name(), new JSONObject());
        result.put(HttpParamKind.HEAD.name(), new JSONObject());
        result.put(HttpParamKind.FILTER.name(), new JSONObject());
        result.put(HttpParamKind.BODY.name(), StringUtils.EMPTY);

        for (Map.Entry<String, RequestParamMapping> mapEntry : fieldMappings.entrySet()) {
            String field = getField(mapEntry);
            RequestParamMapping requestParamMapping = mapEntry.getValue();
            if (requestParamMapping.getHttpParamKind() == null) {
                requestParamMapping.setHttpParamKind(HttpParamKind.QUERY);
            }

            HttpParamKind httpParamKind = requestParamMapping.getHttpParamKind();
            Object value = origin.get(field);
            if (value != null) {
                switch (httpParamKind) {
                    case QUERY:
                    case HEAD:
                    case FILTER:
                        result.getJSONObject(httpParamKind.name()).put(field, value);
                        break;
                    default:
                        result.put(HttpParamKind.BODY.name(), value);
                        break;
                }
            }
        }
        return result;
    }

    private String getField(Map.Entry<String, RequestParamMapping> mapEntry) {
        String field = null;
        String key = mapEntry.getKey();
        if (StringUtils.isNotEmpty(key)) {
            String[] keys = key.split(":");
            if (keys.length > 1) {
                field = keys[0];
            }
        }
        return field;
    }

    public static JSON jsonParse(Object results) {
        JSON jsonResults;
        if (results instanceof List) {
            // JDBC
            jsonResults = new JSONArray((List<Object>) results);
        } else {
            String value = String.valueOf(results);
            try {
                JSONObject tmpJsonObject = JSONObject.parseObject(value);
                // Groovy如果没有配置BODY，就不会存在BODY节点，此时手动创建BODY节点放入数据
                if (tmpJsonObject.containsKey(HttpParamKind.BODY.name())) {
                    String bodyString = tmpJsonObject.get(HttpParamKind.BODY.name()).toString();
                    if (StringUtils.isNotEmpty(bodyString)) {
                        tmpJsonObject.put(HttpParamKind.BODY.name(), JSONObject.parseObject(bodyString));
                    }
                    jsonResults = tmpJsonObject;
                } else {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(HttpParamKind.BODY.name(), tmpJsonObject);
                    jsonResults = jsonObject;
                }
            } catch (Exception e) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(HttpExternalApiAttr.ALL_FIELD, value);
                jsonResults = jsonObject;
            }
        }
        return jsonResults;
    }

    /**
     * 从threadLocal中获取上下文并记录日志信息，异常算子catch块调用此方法会出现NPE，需要调用下面的重载方法
     *
     * @param line 日志内容
     */
    protected void addLog(String line) {
        NodeRunTimeContext nodeRunTimeContext = threadLocal.get();
        addLog(nodeRunTimeContext, line);
    }

    /**
     * 在上下文中记录日志
     *
     * @param nodeRunTimeContext 流程编排运行时上下文
     * @param line               日志内容
     */
    protected void addLog(NodeRunTimeContext nodeRunTimeContext, String line) {
        boolean enableLog = nodeRunTimeContext.getOperatorContainer().getStartInputParams()
                .getJSONObject(HttpParamKind.QUERY.name()).containsKey(RequestParamUtils.ENABLE_LOG);
        if (!enableLog) {
            return;
        }

        nodeRunTimeContext.getApiFlowSlot().addLog(
                DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss")
                        + " 算子[" + nodeRunTimeContext.getOperator().getName() + "(" + nodeRunTimeContext.getOperator().getDesc() + ")" + "]: "
                        + line
        );
    }

    @Override
    public boolean isContinueOnError() {
        return false;
    }

    protected abstract OpenApiMsgCodeEnum getOpenApiMsgEnum();

    /**
     * 从表达式中找到所有的算子id值，转换为算子名称
     *
     * @param paramMapping 参数映射
     */
    private void parseOperatorIdToName(AbstractParamMapping paramMapping) {
        String expression = paramMapping.getExpression();
        // 如果表达式为JSON对象字符串
        if (com.wakedata.dw.open.parammapping.util.JsonUtil.isJson(expression)) {
            JSONObject jsonObject = JSONObject.parseObject(expression);
            paramMapping.setExpressionIsJson(Boolean.TRUE);
            paramMapping.setExpression(parseJsonObject(jsonObject).toJSONString());
            return;
        }
        // 如果表达式为JSON数组字符串
        if (com.wakedata.dw.open.parammapping.util.JsonUtil.isJsonArr(expression)) {
            JSONArray jsonArray = JSONArray.parseArray(expression);
            paramMapping.setExpressionIsJson(Boolean.TRUE);
            paramMapping.setExpression(parseJsonArray(jsonArray).toJSONString());
            return;
        }
        paramMapping.setExpressionIsJson(Boolean.FALSE);
    }

    /**
     * 解析JSONObject中存在的operatorId属性，将值放入operatorIds集合中
     *
     * @param jsonObject JSONObject
     */
    private JSON parseJsonObject(JSONObject jsonObject) {
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            Object value = entry.getValue();
            // 如果value是数组对象
            if (com.wakedata.dw.open.parammapping.util.JsonUtil.isJsonArr(value)) {
                parseJsonArray((JSONArray) value);
                continue;
            }
            // 如果value是JSON对象
            if (com.wakedata.dw.open.parammapping.util.JsonUtil.isJson(value)) {
                JSONObject valueObject = jsonObject.getJSONObject(entry.getKey());
                if (!valueObject.containsKey(JsonPathHelper.OPERATOR_ID_KEY)) {
                    parseJsonObject(valueObject);
                }
                if (valueObject.containsKey(JsonPathHelper.OPERATOR_ID_KEY)
                        && StringUtils.isNotBlank(valueObject.getString(JsonPathHelper.OPERATOR_ID_KEY))) {
                    // 转换回 operator name
                    String operatorId = valueObject.getString(JsonPathHelper.OPERATOR_ID_KEY);
                    Operator operator = (Operator) threadLocal.get().getOperatorContainer().getOperator(operatorId);
                    valueObject.put(JsonPathHelper.OPERATOR_NAME_KEY, operator.getName());
                }
            }
        }
        return jsonObject;
    }

    /**
     * 解析JSONArray中存在的operatorId属性，将值放入operatorIds集合中
     *
     * @param jsonArray JSONArray
     */
    private JSON parseJsonArray(JSONArray jsonArray) {
        for (Object object : jsonArray) {
            if (com.wakedata.dw.open.parammapping.util.JsonUtil.isJsonArr(object)) {
                parseJsonArray((JSONArray) object);
            }
            if (com.wakedata.dw.open.parammapping.util.JsonUtil.isJson(object) && object instanceof JSONObject) {
                parseJsonObject((JSONObject) object);
            }
        }
        return jsonArray;
    }

    /**
     * 转换算子parentId，前端在保存异常算子内的时候，不好去掉父算子id后缀，所以后端处理
     *
     * @param parentOperatorId 原parentId
     * @return 转换后的parentId
     */
    private String conversionParentOperatorId(String parentOperatorId) {
        return parentOperatorId.replace(com.wakedata.dw.open.model.api.flow.operator.Operator.TRY_TAG, "").replace(com.wakedata.dw.open.model.api.flow.operator.Operator.CATCH_TAG, "");
    }

}