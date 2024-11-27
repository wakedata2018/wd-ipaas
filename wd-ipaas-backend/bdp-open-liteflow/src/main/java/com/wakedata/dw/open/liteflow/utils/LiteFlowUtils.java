package com.wakedata.dw.open.liteflow.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wakedata.common.core.GlobalApplicationContext;
import com.wakedata.common.core.constants.CommonConstant;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.liteflow.DAGTaskEngine;
import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.model.api.flow.operator.EndOperator;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.api.flow.operator.edge.InputEdge;
import com.wakedata.dw.open.model.api.flow.operator.edge.InputMultiEdge;
import com.wakedata.dw.open.model.api.flow.operator.edge.OutputEdge;
import com.wakedata.dw.open.model.api.flow.operator.edge.OutputMultiEdge;
import com.wakedata.dw.open.model.api.flow.operator.foreach.BreakOperator;
import com.wakedata.dw.open.model.api.flow.operator.foreach.ForOperator;
import com.wakedata.dw.open.model.api.flow.operator.trycatch.TryCatchOperator;
import com.wakedata.dw.open.operator.OperatorProcessor;
import com.wakedata.dw.open.operator.OperatorProcessorFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 服务编排LiteFlow工具包
 *
 * @author ZhangXueJun
 * @title LiteFlowUtils
 * @date 2021/5/7 11:41
 * @projectName dw-open
 * @description
 */
public class LiteFlowUtils {

    /**
     * api整个流程节点
     */
    private static final String NODE_FLOW = "flow";

    /**
     * 节点
     */
    static class Node {
        private static final String NODE_NODES = "nodes";
        private static final String NODE_NODE = "node";
        private static final String NODE_NODE_ID = "id";
        private static final String NODE_NODE_CLASS = "class";

    }

    /**
     * 链路
     */
    static class Chain {
        private static final String MAIN_CHAIN = "main_chain";
        private static final String NODE_CHAIN = "chain";
        private static final String NODE_CHAIN_NAME = "name";
        /**
         * 新版改用el表达式
         */
        private static final String NODE_CHAIN_VALUE = "value";

        /**
         * 节点id EL表达式不支持使用数字开头
         */
        public static final String CHAIN_EL_ID_PREFIX = "node_";

        /**
         * El表达式不支持的字符
         */
        public static final String CHAIN_EL_NOT_SUPPORT = "-";

        /**
         * 替换字符
         */
        public static final String CHAIN_EL_NOT_SUPPORT_REPLACE = "__";
    }

    /**
     * 组件
     */
    static class Component {

        /**
         * 串行
         */
        private static final String NODE_CHAIN_CONDITION_TYPE_THEN = "THEN";
        /**
         * 并行
         */
        private static final String NODE_CHAIN_CONDITION_TYPE_WHEN = "WHEN";
        /**
         * for循环,改用liteflow的while组件实现，因为liteflow的for组件无法获取到当前循环的次数和当前循环的对象值
         * ，使用for需要定义一个中间算子自己处理
         * ，因此改用while，每循环一次都会重新走到条件算子，可更方便定义参数设置当前循环的次数以及循环的对象值
         */
        private static final String NODE_CHAIN_CONDITION_TYPE_FOR = "WHILE";
        /**
         * for循环内部执行链
         */
        private static final String NODE_CHAIN_CONDITION_TYPE_DO = "DO";
        /**
         * 退出循环
         */
        private static final String NODE_CHAIN_CONDITION_TYPE_BREAK = "BREAK";
    }

    /**
     * 链路辅助，帮助构建LiteFlow子执行链用
     */
    static class ChainHelper {
        /**
         * 用于存放多个执行链的标识
         */
        private static final String NODE_VALUES = "values";
    }

    /**
     * 解析规则文件格式
     *
     * @param apiId
     * @param operatorContexts
     * @return
     */
    public static JSONObject parseJsonLiteFlow(Integer apiId, Map<String, DAGTaskEngine.OperatorContext> operatorContexts) {
        JSONObject flow = new JSONObject(true);

        // 组装nodes节点
        List<String> removeOperatorIds = new ArrayList<>();
        JSONObject nodes = new JSONObject(true);
        JSONObject node = new JSONObject(true);
        JSONArray nodeArr = new JSONArray();
        for (Map.Entry<String, DAGTaskEngine.OperatorContext> mapEntry : operatorContexts.entrySet()) {

            Operator operator = mapEntry.getValue().getOperator();
            String operatorId = operator.getOperatorId();
            if (operator instanceof EndOperator) {
                removeOperatorIds.add(operatorId);
                continue;
            }
            //非内部嵌套算子才过滤
            if (StringUtils.isBlank(operator.getParentOperatorId())) {
                // 如果算子为单出边，但是无出边算子则忽略
                if (operator instanceof OutputEdge && StringUtils.isBlank(((OutputEdge) operator).getOutputOperator())) {
                    removeOperatorIds.add(operatorId);
                    continue;
                }
                // 如果算子为多出边，但是无出边算子则忽略
                if (operator instanceof OutputMultiEdge && CollectionUtil.isEmpty(((OutputMultiEdge<?>) operator).getOutputOperators())) {
                    removeOperatorIds.add(operatorId);
                    continue;
                }
                // 如果算子为单入边或多入边，但是无入边算子则忽略
                if ((operator instanceof InputEdge || operator instanceof InputMultiEdge) && CollectionUtil.isEmpty(mapEntry.getValue().getInputEdges())) {
                    removeOperatorIds.add(operatorId);
                    continue;
                }
                //循环算子如果没有添加子流程则忽略
                if (operator instanceof ForOperator && (CollectionUtil.isEmpty(((ForOperator) operator).getSubOperators()))) {
                    removeOperatorIds.add(operatorId);
                    continue;
                }
                // 捕获异常算子如果没有添加子流程则忽略
                if (operator instanceof TryCatchOperator && (CollectionUtil.isEmpty(((TryCatchOperator) operator).getSubTryOperators()))) {
                    removeOperatorIds.add(operatorId);
                    continue;
                }
            }

            JSONObject operatorJson = new JSONObject(true);
            operatorJson.put(Node.NODE_NODE_ID, operatorIdToNodeId(mapEntry.getKey()));
            OperatorProcessorFactory processorFactory = GlobalApplicationContext.getBean(OperatorProcessorFactory.class);
            String operatorClassName = operator.getClass().getName();
            OperatorProcessor processor = processorFactory.getProcessor(operatorClassName);
            if (processor == null) {
                throw new UnsupportedOperationException("un support type:" + operatorClassName);
            }
            Class<? extends AbstractNodeComponent> className = processor.getNodeComponent();

            operatorJson.put(Node.NODE_NODE_CLASS, className.getName());
            nodeArr.add(operatorJson);
        }

        node.put(Node.NODE_NODE, nodeArr);
        nodes.put(Node.NODE_NODES, node);
        flow.put(NODE_FLOW, nodes);

        // 组装liteflow执行链时，移除需要忽略的节点
        removeOperatorIds.forEach(operatorContexts::remove);
        JSONArray chain = layoutLiteFlowChain(apiId, operatorContexts);
        nodes.put(Chain.NODE_CHAIN, chain);
        return flow;
    }

    /**
     * 刻画LiteFlow Chain
     *
     * @param apiId
     * @param operatorContexts
     * @return
     */
    private static JSONArray layoutLiteFlowChain(
            Integer apiId,
            Map<String, DAGTaskEngine.OperatorContext> operatorContexts) {
        //根据算子所在层级分组
        Map<Integer, List<DAGTaskEngine.OperatorContext>> operatorRanks = operatorContexts.values()
                .stream()
                //过滤掉子流程算子
                .filter(value -> StringUtils.isBlank(value.getOperator().getParentOperatorId()))
                .collect(
                        Collectors.groupingBy(
                                operatorContext -> operatorContext.getOperator().getRank(),
                                Collectors.toList()
                        )
                );

        JSONArray jsonArray = new JSONArray();

        //获取主链名称
        String mainChainName = getMainChainName(apiId);

        //设置所有子链
        List<String> sortChains = Lists.newArrayList(operatorRanks.keySet()).stream().map(rank -> assembleChainName(mainChainName, rank)).sorted().collect(Collectors.toList());
        //设置主链执行节点
        JSONObject mainChain = assembleChain(mainChainName, sortChains);
        jsonArray.add(mainChain);

        //设置子链
        for (Map.Entry<Integer, List<DAGTaskEngine.OperatorContext>> mapEntry : operatorRanks.entrySet()) {
            Integer rank = mapEntry.getKey();
            List<DAGTaskEngine.OperatorContext> groupRankOperators = mapEntry.getValue();
            List<String> operatorComponents = groupRankOperators.stream().map(operatorContext -> operatorIdToNodeId(operatorContext.getOperator().getOperatorId())).collect(Collectors.toList());
            JSONObject rankChain = assembleChain(assembleChainName(mainChainName, rank), operatorComponents, operatorContexts);
            // 如果JSONObject对象存在values这个key，说明存在多个子链，需要遍历出来添加到jsonArray中
            if (rankChain.containsKey(ChainHelper.NODE_VALUES)) {
                JSONArray rankChains = rankChain.getJSONArray(ChainHelper.NODE_VALUES);
                jsonArray.addAll(rankChains);
                rankChain.remove(ChainHelper.NODE_VALUES);
            }
            jsonArray.add(rankChain);
        }
        return jsonArray;
    }

    /**
     * 算子ID字符格式替换
     *
     * @param operatorId
     * @return
     */
    private static String operatorIdToNodeId(String operatorId) {
        return Chain.CHAIN_EL_ID_PREFIX + operatorId.replaceAll(Chain.CHAIN_EL_NOT_SUPPORT, Chain.CHAIN_EL_NOT_SUPPORT_REPLACE);
    }

    /**
     * 节点id转算子id
     *
     * @param nodeId
     * @return
     */
    public static String nodeIdToOperatorId(String nodeId) {
        return nodeId.replaceAll(Chain.CHAIN_EL_ID_PREFIX, "").replaceAll(Chain.CHAIN_EL_NOT_SUPPORT_REPLACE, Chain.CHAIN_EL_NOT_SUPPORT);
    }

    /**
     * 构建子链
     *
     * @param chainName
     * @param values
     * @return
     */
    private static JSONObject assembleChain(String chainName, List<String> values) {
        JSONObject chain = new JSONObject(true);
        chain.put(Chain.NODE_CHAIN_NAME, chainName);
        chain.put(Chain.NODE_CHAIN_VALUE, Component.NODE_CHAIN_CONDITION_TYPE_THEN + "(" + StringUtils.join(values, ",") + ")");
        return chain;
    }

    /**
     * 处理特殊的子链，如循环等
     *
     * @param chainName
     * @param values
     * @param operatorContexts
     * @return
     */
    private static JSONObject assembleChain(String chainName, List<String> values, Map<String, DAGTaskEngine.OperatorContext> operatorContexts) {
        JSONObject chain = new JSONObject(true);
        chain.put(Chain.NODE_CHAIN_NAME, chainName);
        //用于存放多个子链用
        List<JSONObject> subChainList = new ArrayList<>();
        //判断是否有循环算子，循环算子要使用for表达式
        List<String> expressions = new ArrayList<>();
        boolean isFor = false;
        boolean isTryCatch = false;
        for (String nodeId : values) {
            DAGTaskEngine.OperatorContext operatorContext = operatorContexts.get(nodeIdToOperatorId(nodeId));
            Operator operator = operatorContext.getOperator();
            if (operator instanceof ForOperator) {
                isFor = true;
                String forExpression = getForExpression(operatorContexts, operatorContext);
                if (StringUtils.isNotBlank(forExpression)) {
                    expressions.add(forExpression);
                }
            } else if (operator instanceof TryCatchOperator) {
                isTryCatch = true;
                // 生成异常算子主执行链
                expressions.add(nodeId);
                TryCatchOperator tryCatchOperator = (TryCatchOperator) operator;
                // 生成try子链
                String tryExpression = getTryCatchSubExpression(operatorContexts, tryCatchOperator.getSubTryOperators());
                JSONObject tryChain = new JSONObject(true);
                tryChain.put(Chain.NODE_CHAIN_NAME, chainName + tryCatchOperator.getTryTag());
                tryChain.put(Chain.NODE_CHAIN_VALUE, tryExpression);
                subChainList.add(tryChain);
                // 生成catch子链
                String catchExpression = getTryCatchSubExpression(operatorContexts, tryCatchOperator.getSubCatchOperators());
                if (StringUtils.isNotBlank(catchExpression)) {
                    JSONObject catchChain = new JSONObject(true);
                    catchChain.put(Chain.NODE_CHAIN_NAME, chainName + tryCatchOperator.getCatchTag());
                    catchChain.put(Chain.NODE_CHAIN_VALUE, catchExpression);
                    subChainList.add(catchChain);
                }
            } else {
                expressions.add(nodeId);
            }
        }
        if (values.size() == CommonConstant.ONE && isFor) {
            chain.put(Chain.NODE_CHAIN_VALUE, StringUtils.join(expressions, ","));
        } else if (values.size() == CommonConstant.ONE && isTryCatch) {
            chain.put(Chain.NODE_CHAIN_VALUE, Component.NODE_CHAIN_CONDITION_TYPE_THEN + "(" + StringUtils.join(expressions, ",") + ")");
            chain.put(ChainHelper.NODE_VALUES, subChainList);
        } else if (values.size() == CommonConstant.ONE) {
            chain.put(Chain.NODE_CHAIN_VALUE, Component.NODE_CHAIN_CONDITION_TYPE_THEN + "(" + StringUtils.join(expressions, ",") + ")");
        } else {
            chain.put(Chain.NODE_CHAIN_VALUE, Component.NODE_CHAIN_CONDITION_TYPE_WHEN + "(" + StringUtils.join(expressions, ",") + ")");
        }
        return chain;
    }

    /**
     * 构造异常算子try/catch子流程执行链
     *
     * @param operatorContexts 编排算子上下文map
     * @param subOperators     捕获异常算子中try/catch内部算子集合
     * @return try/catch子流程执行链表达式
     */
    private static String getTryCatchSubExpression(Map<String, DAGTaskEngine.OperatorContext> operatorContexts, Set<String> subOperators) {
        if (CollectionUtil.isEmpty(subOperators)) {
            return null;
        }
        List<Operator> subOperatorList = subOperators.stream().map(x -> operatorContexts.get(x).getOperator())
                .sorted(Comparator.comparingInt(Operator::getRank))
                .collect(Collectors.toList());
        // 子流程中只有一个算子，使用THEN语法组装执行链
        if (subOperatorList.size() == CommonConstant.ONE) {
            return Component.NODE_CHAIN_CONDITION_TYPE_THEN + "(" + subOperatorList.stream().map(x -> operatorIdToNodeId(x.getOperatorId())).collect(Collectors.joining(",")) + ")";
        }
        // 如果子流程中有多个元素，按算子层级分组后组装执行链
        Map<String, List<Operator>> map = subOperatorList.stream().collect(Collectors.groupingBy(
                operator -> String.valueOf(operator.getRank()),
                Collectors.toList()
        ));
        // 只有一个层级，说明是并行，使用WHEN语法组装执行链
        if (map.size() == CommonConstant.ONE) {
            return Component.NODE_CHAIN_CONDITION_TYPE_WHEN + "(" + map.get(String.valueOf(subOperatorList.get(CommonConstant.ZERO).getRank())).stream().map(x -> operatorIdToNodeId(x.getOperatorId())).collect(Collectors.joining(",")) + ")";
        }
        // 存在多个层级，根据层级按顺序组装执行链
        List<String> expressions = getExpressionsFromRankMap(map);
        return Component.NODE_CHAIN_CONDITION_TYPE_THEN + "(" + String.join(",", expressions) + ")";
    }

    /**
     * 构造for循环执行链
     *
     * @param operatorContexts
     * @param operatorContext
     * @return
     */
    private static String getForExpression(Map<String, DAGTaskEngine.OperatorContext> operatorContexts, DAGTaskEngine.OperatorContext operatorContext) {
        //构造for表达式
        ForOperator forOperator = (ForOperator) operatorContext.getOperator();
        Set<String> subOperators = forOperator.getSubOperators();
        String forExpression = Component.NODE_CHAIN_CONDITION_TYPE_FOR + "(" + operatorIdToNodeId(forOperator.getOperatorId()) + ")" + DwOpenConstant.JOIN_POINT;
        if (CollectionUtil.isNotEmpty(subOperators)) {
            List<Operator> subOperatorList = subOperators.stream().map(x -> operatorContexts.get(x).getOperator())
                    .sorted(Comparator.comparingInt(Operator::getRank))
                    .collect(Collectors.toList());
            //获取最后一个节点，看是否是退出循环算子
            Operator lastOperator = subOperatorList.get(subOperatorList.size() - CommonConstant.ONE);
            if (lastOperator instanceof BreakOperator) {
                //只有退出循环算子
                if (subOperatorList.size() == CommonConstant.ONE) {
                    return null;
                }
                forExpression += (getDoExpression(subOperatorList.subList(CommonConstant.ZERO, subOperatorList.size() - CommonConstant.ONE))
                        + DwOpenConstant.JOIN_POINT + Component.NODE_CHAIN_CONDITION_TYPE_BREAK + "(" + operatorIdToNodeId(lastOperator.getOperatorId()) + ")");
            } else {
                forExpression += getDoExpression(subOperatorList);
            }
            return forExpression;
        }
        return null;
    }

    /**
     * 获取do执行链
     * @param operatorList
     * @return
     */
    private static String getDoExpression(List<Operator> operatorList){
        if(operatorList.size() == CommonConstant.ONE){
            return Component.NODE_CHAIN_CONDITION_TYPE_DO + "(" +Component.NODE_CHAIN_CONDITION_TYPE_THEN + "(" + operatorList.stream().map(x -> operatorIdToNodeId(x.getOperatorId())).collect(Collectors.joining(",")) + "))";
        }
        Map<String, List<Operator>> map = operatorList.stream().collect(Collectors.groupingBy(
                operator -> String.valueOf(operator.getRank()),
                Collectors.toList()
        ));
        if(map.size() == CommonConstant.ONE){
            return Component.NODE_CHAIN_CONDITION_TYPE_DO + "(" + Component.NODE_CHAIN_CONDITION_TYPE_WHEN + "(" + map.get(String.valueOf(operatorList.get(CommonConstant.ZERO).getRank())).stream().map(x -> operatorIdToNodeId(x.getOperatorId())).collect(Collectors.joining(",")) + "))";
        }
        List<String> expressions = getExpressionsFromRankMap(map);
        return Component.NODE_CHAIN_CONDITION_TYPE_DO + "(" +Component.NODE_CHAIN_CONDITION_TYPE_THEN + "(" + String.join(",", expressions) + "))";
    }

    /**
     * 根据算子按照层级分组的map，构建LiteFlow执行链集合
     *
     * @param map 算子层级分组map
     * @return LiteFlow执行链集合
     */
    private static List<String> getExpressionsFromRankMap(Map<String, List<Operator>> map) {
        List<String> expressions = new ArrayList<>();
        map.forEach((key, value) -> {
            if (value.size() > CommonConstant.ONE) {
                // 同一个层级中有多个值，说明是并行执行，使用WHEN语法组装执行链
                expressions.add(Component.NODE_CHAIN_CONDITION_TYPE_WHEN + "(" + value.stream().map(x -> operatorIdToNodeId(x.getOperatorId())).collect(Collectors.joining(",")) + ")");
            } else {
                // 只有一个元素，直接填充执行链节点
                expressions.add(value.stream().map(x -> operatorIdToNodeId(x.getOperatorId())).collect(Collectors.joining(",")));
            }
        });
        return expressions;
    }

    private static String assembleChainName(String mainChainName, Integer rank) {
        return mainChainName + "_" + "chain" + rank;
    }

    public static String getMainChainName(Integer apiId) {
        return Chain.MAIN_CHAIN + "_" + apiId;
    }
}
