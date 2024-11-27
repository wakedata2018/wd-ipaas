package com.wakedata.dw.open.liteflow;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.model.api.flow.ApiFlowAttr;
import com.wakedata.dw.open.model.api.flow.operator.*;
import com.wakedata.dw.open.model.api.flow.operator.api.ApiOperator;
import com.wakedata.dw.open.model.api.flow.operator.connector.ConnectorOperator;
import com.wakedata.dw.open.model.api.flow.operator.edge.OutputEdge;
import com.wakedata.dw.open.model.api.flow.operator.edge.OutputMultiEdge;
import com.wakedata.dw.open.model.api.flow.operator.foreach.BreakOperator;
import com.wakedata.dw.open.model.api.flow.operator.foreach.ForOperator;
import com.wakedata.dw.open.model.api.flow.operator.global.GlobalOperator;
import com.wakedata.dw.open.model.api.flow.operator.script.GroovyScriptTransformOperator;
import com.wakedata.dw.open.model.api.flow.operator.simple.SimpleOperator;
import com.wakedata.dw.open.model.api.flow.operator.sql.JoinTransformOperator;
import com.wakedata.dw.open.model.api.flow.operator.sql.UnionTransformOperator;
import com.wakedata.dw.open.model.api.flow.operator.transform.ColumnSelectTransformOperator;
import com.wakedata.dw.open.model.api.flow.operator.transform.RowSelectTransformOperator;
import com.wakedata.dw.open.model.api.flow.operator.trycatch.TryCatchOperator;
import com.wakedata.dw.open.service.api.DataApiAccessService;
import com.wakedata.dw.open.service.api.GlobalParamService;
import com.wakedata.dw.open.service.impl.api.DataApiAccessServiceImpl;
import com.wakedata.dw.open.service.impl.api.GlobalParamServiceImpl;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * @author ZhangXueJun
 * @title DAGTaskEngine
 * @date 2021/3/22 11:44
 * @projectName dw-open
 * @description
 */
@Slf4j
public class DAGTaskEngine {

    /**
     * 服务编排算子详细信息
     */
    private ApiFlowAttr apiFlowAttr;

    /**
     * 服务编排算子访问者
     */
    private OperatorVisitor operatorVisitor;

    /**
     * 服务编排算子输入
     * a、通用鉴权
     * b、无入边API算子入参
     */
    private JSONObject startInputParams;

    @Setter
    private DataApiAccessService dataApiAccessService;

    @Setter
    private GlobalParamService globalParamService;

    /**
     * 用于控制权限
     */
    private Integer appId;

    /**
     * For Testing
     */
    DAGTaskEngine() {
        super();
    }

    public static OperatorContainer justParse(ApiFlowAttr apiFlowAttr) {
        return execute(apiFlowAttr, 0, new JSONObject(), null, null);
    }

    public static OperatorContainer execute(
            ApiFlowAttr apiFlowAttr,
            Integer appId,
            JSONObject inputParams,
            DataApiAccessServiceImpl dataApiAccessService,
            GlobalParamServiceImpl globalParamService) {
        DAGTaskEngine dagTaskEngine = DAGTaskEngine.getInstance(apiFlowAttr, appId, inputParams);
        dagTaskEngine.setDataApiAccessService(dataApiAccessService);
        dagTaskEngine.setGlobalParamService(globalParamService);

        try {
            OperatorContainer operatorContainer = dagTaskEngine.execute();
            return operatorContainer;
        } catch (Exception e) {
            log.error("api编排数据解析出错：{}", e.getMessage(), e);
            if (e instanceof OpenException) {
                throw (OpenException) e;
            }
            throw new OpenException(OpenApiMsgCodeEnum.w_api_graph_parse_error);
        }
    }

    private DAGTaskEngine(ApiFlowAttr apiFlowAttr, Integer appId, JSONObject startInputParams) {
        super();
        this.apiFlowAttr = apiFlowAttr;
        this.startInputParams = startInputParams;
        this.appId = appId;
    }

    public static DAGTaskEngine getInstance(ApiFlowAttr apiFlowAttr, Integer appId, JSONObject inputParams) {
        return new DAGTaskEngine(apiFlowAttr, appId, inputParams);
    }

    /**
     * 算子上下文
     */
    public class OperatorContainer {
        /**
         * 所有算子及其父类算子id
         */
        private Map<String, OperatorContext> fullOperators;

        public JSONObject getStartInputParams() {
            return startInputParams;
        }

        public DataApiAccessService getDataApiAccessService() {
            return dataApiAccessService;
        }

        public GlobalParamService getGlobalParamService() {
            return globalParamService;
        }

        public Map<String, OperatorContext> getFullOperators() {
            return fullOperators;
        }

        public OperatorContext getOperatorContext(String operatorId) {
            return fullOperators.get(operatorId);
        }

        public Operator getOperator(String operatorId) {
            return getOperatorContext(operatorId).getOperator();
        }

        public Set<String> getInputEdges(String operatorId) {
            return getOperatorContext(operatorId).getInputEdges();
        }

        public boolean isEmpty() {
            return MapUtils.isEmpty(fullOperators);
        }

        public Integer getAppId() {
            return appId;
        }

        /**
         * 根据算子id获取上级算子节点集合
         *
         * @param operatorId 算子id
         * @return 算子节点集合
         */
        public List<Operator> getInputOperators(String operatorId) {
            OperatorContext operatorContext = fullOperators.get(operatorId);
            Set<String> inputEdges = operatorContext.inputEdges;

            List<Operator> inputOperators = Lists.newArrayList();
            if (CollectionUtils.isEmpty(inputEdges)) {
                return inputOperators;
            }

            for (String inputEdge : inputEdges) {
                inputOperators.add(getOperator(inputEdge));
            }
            return inputOperators;
        }

        /**
         * 根据算子id获取所有上级算子节点集合
         *
         * @param operatorId 算子id
         * @param operators  算子节点集合
         * @return 上级算子节点集合
         */
        public List<Operator> getSuperiorOperators(String operatorId, List<Operator> operators) {
            // 获取算子上下文
            OperatorContext operatorContext = fullOperators.get(operatorId);
            Set<String> inputEdges = operatorContext.inputEdges;
            if (CollectionUtils.isEmpty(inputEdges)) {
                return operators;
            }

            for (String inputEdge : inputEdges) {
                OperatorContext inputOperatorContext = fullOperators.get(inputEdge);
                operators.add(inputOperatorContext.getOperator());
                // 递归获取当前节点的上级节点放入结果集
                getSuperiorOperators(inputEdge, operators);
            }

            return operators;
        }

        /**
         * 根据算子id获取所有上级算子上下文集合
         *
         * @param operatorId          算子id
         * @param operatorContextList 算子上下文集合
         * @return 算子上下文集合
         */
        public List<OperatorContext> getInputOperatorContexts(String operatorId, List<OperatorContext> operatorContextList) {
            OperatorContext globalOperatorContext = fullOperators.get(GlobalOperator.GLOBAL_OPERATOR_ID);

            // 获取算子上下文
            OperatorContext operatorContext = fullOperators.get(operatorId);
            if (ObjectUtil.isEmpty(operatorContext)) {
                return operatorContextList;
            }
            Set<String> inputEdges = operatorContext.inputEdges;
            if (CollectionUtils.isEmpty(inputEdges)) {
                return operatorContextList;
            }

            for (String inputEdge : inputEdges) {
                OperatorContext inputOperatorContext = fullOperators.get(inputEdge);
                operatorContextList.add(inputOperatorContext);
                if (Objects.equals(inputEdge, VertexOperator.VERTEX_OPERATOR_ID) && Objects.nonNull(globalOperatorContext)) {
                    operatorContextList.add(globalOperatorContext);
                }
                // 递归获取当前节点的上级节点放入结果集
                getInputOperatorContexts(inputEdge, operatorContextList);
            }
            return operatorContextList;
        }

        public List<ApiOperator> filterApiOperator() {
            return fullOperators.values()
                    .stream()
                    .map(OperatorContext::getOperator)
                    .filter(operator -> operator instanceof ApiOperator)
                    .map(operator -> (ApiOperator) operator)
                    .collect(Collectors.toList());
        }

        public List<Operator> filterSinkOperator() {
            return fullOperators.values()
                    .stream()
                    .filter(OperatorContext::isSink)
                    .map(OperatorContext::getOperator)
                    .collect(Collectors.toList());
        }

        public List<Operator> filterSourceOperator() {
            return fullOperators.values()
                    .stream()
                    .filter(operatorContext -> operatorContext.getInputEdges().size() == 1
                            && operatorContext.getInputEdges().iterator().next().equals(VertexOperator.VERTEX_OPERATOR_ID))
                    .map(OperatorContext::getOperator)
                    .collect(Collectors.toList());
        }
    }

    /**
     * 算子上下文
     */
    public class OperatorContext {
        private Operator operator;
        private Set<String> inputEdges;
        private boolean sink;

        public OperatorContext(Operator operator, Set<String> inputEdges) {
            this.operator = operator;
            this.inputEdges = inputEdges;
        }

        public Operator getOperator() {
            return operator;
        }

        public Set<String> getInputEdges() {
            return inputEdges;
        }

        public boolean isSink() {
            return sink;
        }
    }

    public OperatorContainer execute() throws Exception {
        beforeExecute();
        OperatorContainer operatorContainer = executeInternal();
        afterExecute();
        return operatorContainer;
    }

    private void afterExecute() {

    }


    protected OperatorContainer executeInternal() throws Exception {
        this.operatorVisitor.visit((VertexOperator) apiFlowAttr.getOperators().get(VertexOperator.VERTEX_OPERATOR_ID));
        return this.operatorVisitor.container;
    }

    /**
     * 前置校验
     */
    protected void beforeExecute() {
        Map<String, Operator> operators = apiFlowAttr.getOperators();
        OperatorTools operatorTools = new OperatorTools();
        OperatorTools.DsfCycle dsfCycle = operatorTools.new DsfCycle();
        dsfCycle.addOperators(operators);
        Set<String> cycles = dsfCycle.find();
        if (CollectionUtils.isNotEmpty(cycles)) {
            log.error("exits dsf cycle!\n {}", cycles);
            throw new OpenException(OpenApiMsgCodeEnum.w_api_graph_cycle_parse_error);
        }
        //校验退出循环算子的位置
        dsfCycle.checkBreakRules();
        this.operatorVisitor = this.new OperatorVisitor(operators);
    }

    /**
     * 负责迭代，组装最后visit 算子最后准备
     */
    private class VisitorTools {

        private OperatorContainer container;
        private OperatorVisitor visitor;
        /**
         * 因算子顺序可能不固定，记录含内部算子的id组
         */
        private Map<String, Set<String>> groupOperators = new HashMap<>();

        public VisitorTools(OperatorVisitor visitor, OperatorContainer container) {
            this.container = container;
            this.visitor = visitor;
        }

        public void recursiveFindOperatorFather(VertexOperator vertexOperator) {
            vertexOperator.getOutputOperators().forEach(operator -> {
                assembleFatherOperator(vertexOperator, operator);
            });

            // 计算算子在DAG 所在层级，用于判断LiteFlow Chain设置
            for (Map.Entry<String, OperatorContext> mapEntry : visitor.container.getFullOperators().entrySet()) {
                Operator operator = mapEntry.getValue().getOperator();

                if (operator instanceof VertexOperator) {
                    continue;
                }

                Set<String> inputEdges = mapEntry.getValue().getInputEdges();

                if (CollectionUtil.isNotEmpty(inputEdges)) {
                    String inputEdge = inputEdges.iterator().next();
                    while (!inputEdge.equals(VertexOperator.VERTEX_OPERATOR_ID)) {
                        OperatorContext inputOperatorContext = visitor.container.fullOperators.get(inputEdge);
                        inputEdge = inputOperatorContext.inputEdges.iterator().next();
                    }
                }
            }
        }

        /**
         * 构造出边入边逻辑
         *
         * @param inputOperator
         * @param outputOperatorId
         */
        private void assembleFatherOperator(Operator inputOperator, String outputOperatorId) {
            if (StringUtils.isEmpty(outputOperatorId) || EndOperator.END_OPERATOR_ID.equals(outputOperatorId)) {
                return;
            }

            OperatorContext content = container.getOperatorContext(outputOperatorId);
            Preconditions.checkState(content != null && content.operator != null, "operator[" + outputOperatorId + "] must appear!");
            Operator operator = content.operator;
            assembleFatherOperator(inputOperator, operator);
            if (operator instanceof OutputEdge) {
                outputOperatorId = ((OutputEdge) operator).getOutputOperator();
                assembleFatherOperator(operator, outputOperatorId);
            } else if (operator instanceof OutputMultiEdge) {
                Set<String> outputOperatorIds = ((OutputMultiEdge) operator).getOutputOperators();
                for (String operatorId : outputOperatorIds) {
                    assembleFatherOperator(operator, operatorId);
                }
                // 循环、异常算子需要迭代子流程
                assembleSubOperator(operator, outputOperatorIds, this::assembleFatherOperator);

            } else {
                throw new UnsupportedOperationException("Please impl [" + operator.getName() + "] visit function!");
            }
        }

        /**
         * 组装循环、异常算子子流程
         *
         * @param operator          循环/异常算子
         * @param outputOperatorIds 循环/异常算子的的出边算子id集合
         * @param consumer          自定义消费者函数
         */
        private void assembleSubOperator(Operator operator, Set<String> outputOperatorIds, BiConsumer<Operator, String> consumer) {
            if (operator instanceof ForOperator) {
                Set<String> subFirstOperatorIds = ((ForOperator) operator).getSubFirstOperators();
                extracted(operator, outputOperatorIds, consumer, subFirstOperatorIds);
            } else if (operator instanceof TryCatchOperator) {
                // 标记try子流程
                Set<String> subFirstTryOperatorIds = ((TryCatchOperator) operator).getSubFirstTryOperators();
                extracted(operator, outputOperatorIds, consumer, subFirstTryOperatorIds);
                // 标记catch子流程
                Set<String> subFirstCatchOperatorIds = ((TryCatchOperator) operator).getSubFirstCatchOperators();
                extracted(operator, outputOperatorIds, consumer, subFirstCatchOperatorIds);
            }
        }

        /**
         * 遍历并标记循环/异常算子子流程的算子
         *
         * @param operator            循环/异常算子
         * @param outputOperatorIds   循环/异常算子的的出边算子id集合
         * @param consumer            自定义消费者函数
         * @param subFirstOperatorIds 循环/异常算子中子流程第一层算子id集合
         */
        private void extracted(Operator operator, Set<String> outputOperatorIds, BiConsumer<Operator, String> consumer, Set<String> subFirstOperatorIds) {
            if (CollUtil.isNotEmpty(subFirstOperatorIds)) {
                for (String operatorId : subFirstOperatorIds) {
                    if (!outputOperatorIds.contains(operatorId)) {
                        consumer.accept(operator, operatorId);
                    }
                }
            }
        }

        /**
         * 组装节点的输入节点
         *
         * @param inputOperator 输入节点
         * @param operator      下一个节点
         */
        private void assembleFatherOperator(Operator inputOperator, Operator operator) {
            OperatorContext content = container.getOperatorContext(operator.getOperatorId());

            Set<String> inputs = content.inputEdges;
            if (inputs == null) {
                inputs = Sets.newLinkedHashSet();
                content.inputEdges = inputs;
            }
            inputs.add(inputOperator.getOperatorId());
        }

        public void recursiveAcceptOperator(VertexOperator vertexOperator) throws Exception {
            for (String outputOperatorId : vertexOperator.getOutputOperators()) {
                recursiveAccept(outputOperatorId);
            }
        }

        /**
         * 填充子项
         */
        public void fillGroupOperatorItems() {
            groupOperators.forEach((key, value) -> {
                OperatorContext context = container.getOperatorContext(key);
                if (ObjectUtil.isNotNull(context)) {
                    Operator group = context.getOperator();
                    if (group instanceof ForOperator) {
                        ForOperator forOperator = (ForOperator) group;
                        forOperator.batchAddSubOperator(value);
                        context.operator = forOperator;
                    } else if (group instanceof TryCatchOperator) {
                        TryCatchOperator tryCatchOperator = (TryCatchOperator) group;
                        String tryTag = tryCatchOperator.getTryTag();
                        String catchTag = tryCatchOperator.getCatchTag();
                        // 将子项算子id集合根据标识拆分成try/catch块对应的集合
                        tryCatchOperator.batchAddSubTryOperators(value.stream().filter(x -> x.endsWith(tryTag)).map(x -> x.replace(tryTag, "")).collect(Collectors.toSet()));
                        tryCatchOperator.batchAddSubCatchOperators(value.stream().filter(x -> x.endsWith(catchTag)).map(x -> x.replace(catchTag, "")).collect(Collectors.toSet()));
                        context.operator = tryCatchOperator;
                    }
                }
            });
        }

        /**
         * 迭代Accept
         *
         * @param operatorId
         */
        private void recursiveAccept(String operatorId) throws Exception {
            if (StringUtils.isEmpty(operatorId) || EndOperator.END_OPERATOR_ID.equals(operatorId)) {
                return;
            }

            OperatorContext content = container.getOperatorContext(operatorId);
            Operator operator = content.operator;
            Preconditions.checkNotNull(operator, "operator[" + operatorId + "] must appear!");

            markedDependencyIfNecessary(operator);

            /**
             * 核心方法：标记并处理具体算子业务
             */
            acceptAndMarkOperator(operator);

            /**
             * 组装嵌套算子的数据
             */
            addGroupOperatorSubs(operator);

            if (operator instanceof OutputEdge) {
                String outputOperatorId = ((OutputEdge) operator).getOutputOperator();
                recursiveAccept(outputOperatorId);
            } else if (operator instanceof OutputMultiEdge) {
                Set<String> outputOperators = ((OutputMultiEdge) operator).getOutputOperators();
                for (String outputOperator : outputOperators) {
                    recursiveAccept(outputOperator);
                }
                //循环算子需要迭代子流程
                assembleSubOperator(operator, outputOperators, (outPutOp, outPutOpId) -> {
                    try {
                        recursiveAccept(outPutOpId);
                    } catch (Exception e) {
                        log.error("recursiveAccept sub operator exception:", e);
                        throw new OpenException(OpenApiMsgCodeEnum.w_liteflow_exception_msg.getCode(), e.getMessage());
                    }
                });
            } else {
                throw new UnsupportedOperationException("Please impl [" + operator.getName() + "] visit function!");
            }
        }

        /**
         * 组装嵌套算子的数据 ，如循环算子的所有下级算子
         *
         * @param operator
         */
        private void addGroupOperatorSubs(Operator operator) {
            /**
             * 无上级算子
             */
            String parentOperatorId = operator.getParentOperatorId();
            if (StringUtils.isBlank(parentOperatorId)) {
                return;
            }
            // 前端在保存异常算子内的时候，不好去掉父算子id后缀，后端处理
            String conversionParentOperatorId = parentOperatorId.replace(Operator.TRY_TAG, "").replace(Operator.CATCH_TAG, "");
            Operator parentOperator = container.getOperatorContext(conversionParentOperatorId).operator;
            Set<String> list = ObjectUtil.defaultIfNull(groupOperators.get(conversionParentOperatorId), new HashSet<>());
            String operatorId = operator.getOperatorId();
            if (parentOperator instanceof ForOperator) {
                list.add(operatorId);
            } else if (parentOperator instanceof TryCatchOperator) {
                TryCatchOperator tryCatchOperator = (TryCatchOperator) parentOperator;
                Set<String> subFirstTryOperators = tryCatchOperator.getSubFirstTryOperators();
                Set<String> subFirstCatchOperators = tryCatchOperator.getSubFirstCatchOperators();
                // 判断当前算子处于try块内还是catch块内，做对应的分组标识放入list中
                if (subFirstTryOperators.contains(operatorId)) {
                    list.add(operatorId + tryCatchOperator.getTryTag());
                }
                if (subFirstCatchOperators.contains(operatorId)) {
                    list.add(operatorId + tryCatchOperator.getCatchTag());
                }
                List<Operator> superiorOperators = container.getSuperiorOperators(operatorId, new ArrayList<>());
                List<Operator> inputOperators = superiorOperators.stream().filter(x -> StringUtils.isNotBlank(x.getParentOperatorId())).collect(Collectors.toList());
                for (Operator inputOperator : inputOperators) {
                    String inputOperatorId = inputOperator.getOperatorId();
                    if (subFirstTryOperators.contains(inputOperatorId)) {
                        list.add(operatorId + tryCatchOperator.getTryTag());
                    }
                    if (subFirstCatchOperators.contains(inputOperatorId)) {
                        list.add(operatorId + tryCatchOperator.getCatchTag());
                    }
                }
            }
            groupOperators.put(conversionParentOperatorId, list);
        }

        /**
         * 如果有前置依赖，检查标记即可
         *
         * @param fatherOperator
         */
        private void markedDependencyIfNecessary(Operator fatherOperator) {
            OperatorContext content = container.getOperatorContext(fatherOperator.getOperatorId());
            Set<String> inputEdges = content.inputEdges;
            Stack<Operator> unMarkedOperators = new Stack<>();
            findUnMarkedOperator(unMarkedOperators, inputEdges);
            while (!unMarkedOperators.empty()) {
                Operator inputEdge = unMarkedOperators.pop();

                // 如果已经被标记过
                if (inputEdge.isMarked()) {
                    continue;
                }

                // 如果和最新处理重合，跳过
                if (fatherOperator.getOperatorId().equals(inputEdge.getOperatorId())) {
                    continue;
                }

                try {
                    acceptAndMarkOperator(inputEdge);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    break;
                }
            }
        }

        /**
         * 搜索未标记算子
         *
         * @param unMarkedOperators
         * @param inputEdges
         */
        private void findUnMarkedOperator(Stack<Operator> unMarkedOperators, Set<String> inputEdges) {
            Preconditions.checkState(CollectionUtils.isNotEmpty(inputEdges),
                    "operator must have least one input edges!");

            // 已经达到顶级节点
            if (inputEdges.size() == 1 &&
                    VertexOperator.VERTEX_OPERATOR_ID.equals(inputEdges.iterator().next())) {
                return;
            }

            inputEdges.forEach(input -> {
                OperatorContext content = container.getOperatorContext(input);
                Operator inputEdge = content.operator;
                if (inputEdge.isMarked()) {
                    return;
                }

                unMarkedOperators.push(inputEdge);
                findUnMarkedOperator(unMarkedOperators, content.inputEdges);
            });
        }

        /**
         * 标记并处理具体算子业务
         *
         * @param operator
         */
        public void acceptAndMarkOperator(Operator operator) throws Exception {
            // 如果已经被标记过
            if (operator.isMarked()) {
                return;
            }

            operator.accept(visitor);
            operator.setMarked(true);

            // java对象引用决定不需要此操作，但会存在没同步更新到operators中的对象
            OperatorContext content = container.getOperatorContext(operator.getOperatorId());
            content.operator.setMarked(true);

            layoutRank(operator);
        }

        /**
         * 刻画层级
         *
         * @param operator
         */
        private void layoutRank(Operator operator) {
            // 当处于此状态时，说明前置依赖已经被前部标记
            // 计算算子入编最大层级+1即可
            OperatorContext content = container.getOperatorContext(operator.getOperatorId());
            Set<String> inputEdges = content.getInputEdges();
            int rank = 0;
            for (String inputEdge : inputEdges) {
                OperatorContext inputOperator = container.getOperatorContext(inputEdge);
                if (rank == 0) {
                    rank = inputOperator.getOperator().getRank();
                }

                if (inputOperator.getOperator().getRank() > rank) {
                    rank = inputOperator.getOperator().getRank();
                }
            }
            operator.setRank(rank + 1);
        }
    }


    private class OperatorVisitor implements OpVisitor {

        private OperatorContainer container;
        private VisitorTools visitorTools;

        public OperatorVisitor(Map<String, Operator> operators) {
            this.container = new OperatorContainer();
            this.container.fullOperators = Maps.newHashMap();
            for (Map.Entry<String, Operator> mapEntry : operators.entrySet()) {
                String operatorId = mapEntry.getKey();
                Operator operator = mapEntry.getValue();
                OperatorContext operatorContext = new OperatorContext(operator, Sets.newHashSet());
                this.container.fullOperators.put(operatorId, operatorContext);
                if (operator instanceof OutputEdge) {
                    OutputEdge outputEdge = (OutputEdge) operator;
                    if (StringUtils.isEmpty(outputEdge.getOutputOperator())) {
                        operatorContext.sink = true;
                    }
                } else if (operator instanceof OutputMultiEdge) {
                    OutputMultiEdge outputMultiEdge = (OutputMultiEdge) operator;
                    if (CollectionUtils.isEmpty(outputMultiEdge.getOutputOperators())) {
                        operatorContext.sink = true;
                    }
                }
            }
            this.visitorTools = new VisitorTools(this, this.container);
        }

        @Override
        public void visit(VertexOperator vertexOperator) throws Exception {
            visitorTools.recursiveFindOperatorFather(vertexOperator);
            visitorTools.recursiveAcceptOperator(vertexOperator);
            visitorTools.fillGroupOperatorItems();
        }

        @Override
        public void visit(ApiOperator apiOperator) throws Exception {
        }

        @Override
        public void visit(UnionTransformOperator unionTransformOperator) throws Exception {
        }

        @Override
        public void visit(GroovyScriptTransformOperator groovyScriptTransformOperator) throws Exception {
        }

        @Override
        public void visit(JoinTransformOperator joinTransformOperator) {
        }

        @Override
        public void visit(SimpleOperator simpleOperator) {
        }

        @Override
        public void visit(ColumnSelectTransformOperator columnSelectTransformOperator) {
        }

        @Override
        public void visit(RowSelectTransformOperator rowSelectTransformOperator) {
        }

        @Override
        public void visit(ConnectorOperator connectorOperator) {
        }

        @Override
        public void visit(ForOperator forOperator) {

        }

        @Override
        public void visit(BreakOperator breakOperator) {

        }
    }
}