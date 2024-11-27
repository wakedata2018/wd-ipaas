package com.wakedata.dw.open.model.api.flow.operator;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wakedata.common.core.constants.CommonConstant;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.model.api.flow.operator.edge.OutputEdge;
import com.wakedata.dw.open.model.api.flow.operator.edge.OutputMultiEdge;
import com.wakedata.dw.open.model.api.flow.operator.foreach.BreakOperator;
import com.wakedata.dw.open.model.api.flow.operator.foreach.ForOperator;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 算子工具类
 *
 * @author ZhangXueJun
 * @title OperatorTools
 * @date 2019/10/29 15:43
 * @projectName dw-stream
 * @description
 */
@Slf4j
public class OperatorTools {

    /**
     * 添加算子
     *
     * @param inputEdge
     * @param outputEdge
     */
    public static void addOutOperator(OutputEdge inputEdge, Operator outputEdge) {
        inputEdge.addOutputOperator(outputEdge);
    }

    /**
     * 添加算子
     *
     * @param inputMultiEdge
     * @param outputEdges
     */
    public static void addOutOperator(OutputMultiEdge inputMultiEdge, Operator... outputEdges) {
        for (Operator operator : outputEdges) {
            inputMultiEdge.addOutOperators(operator);
        }
    }

    /**
     * 具体思想是：
     * 1、记录node节点数和对应的线。
     * 2、将其转化为矩阵。
     * 3、通过dsf算法查找闭环。
     */
    public class DsfCycle {

        /**
         * 限制node最大数
         */
        private int maxOperators = 100;

        /**
         * node集合
         */
        private List<String> operators = Lists.newArrayList();

        /**
         * 有向图的邻接矩阵
         */
        private int[][] adjacencyMatrix = new int[maxOperators][maxOperators];

        /**
         * 需要校验的算子，key为组算子id，value为相关算子
         */
        private Map<String,List<Operator>> checkOperator = new HashMap<>();

        private static final String NO_PARENT_BREAK_KEY = "no_parent_break";

        /**
         * 添加节点
         *
         * @param operatorId
         * @return
         */
        private int addOperator(String operatorId) {
            if (!operators.contains(operatorId)) {
                if (operators.size() >= maxOperators) {
                    log.error("operators exceed:{}", maxOperators);
                    return -1;
                }
                operators.add(operatorId);
                return operators.size() - 1;
            }
            return operators.indexOf(operatorId);
        }

        /**
         * 添加线，初始化邻接矩阵
         *
         * @param inputOperator
         * @param outputOperator
         */
        public void addEdge(String inputOperator, String outputOperator) {
            int startIndex = addOperator(inputOperator);
            int endIndex = addOperator(outputOperator);
            if (startIndex >= 0 && endIndex >= 0) {
                adjacencyMatrix[startIndex][endIndex] = 1;
            }
        }

        /**
         * 寻找闭环
         *
         * @return
         */
        public Set<String> find() {
            // 从出发节点到当前节点的轨迹
            List<Integer> trace = Lists.newArrayList();
            // 返回值
            Set<String> result = Sets.newLinkedHashSet();
            if (adjacencyMatrix.length > 0) {
                findCycle(0, trace, result);
            }
            return result;
        }

        /**
         * 发现闭环信息
         *
         * @param v
         * @param trace
         * @param result
         */
        private void findCycle(int v, List<Integer> trace, Set<String> result) {
            int j;
            // 添加闭环信息
            if ((j = trace.indexOf(v)) != -1) {
                StringBuffer sb = new StringBuffer();
                String startNode = operators.get(trace.get(j));
                while (j < trace.size()) {
                    sb.append(operators.get(trace.get(j)) + " --> ");
                    j++;
                }
                result.add(sb.toString() + startNode);
                return;
            }
            trace.add(v);
            for (int i = 0; i < operators.size(); i++) {
                if (adjacencyMatrix[v][i] == 1) {
                    findCycle(i, trace, result);
                }
            }
            trace.remove(trace.size() - 1);
        }

        /**
         * 根据算子组装数据
         * @param operators
         */
        public void addOperators(Map<String, Operator> operators) {
            operators.forEach((s, operator) -> {
                if (operator instanceof OutputEdge) {
                    OutputEdge outputEdge = (OutputEdge) operator;
                    addEdge(operator.getOperatorId(), outputEdge.getOutputOperator());
                } else if (operator instanceof OutputMultiEdge) {
                    OutputMultiEdge outputMultiEdge = (OutputMultiEdge) operator;
                    outputMultiEdge.getOutputOperators().forEach(o -> {
                        addEdge(operator.getOperatorId(), String.valueOf(o));
                    });
                }
                //添加需要校验的算子
                if(operator instanceof ForOperator){
                    addCheckOperator(operator,operator.getOperatorId());
                }
                if(operator instanceof BreakOperator && StrUtil.isBlank(operator.getParentOperatorId())){
                    addCheckOperator(operator,NO_PARENT_BREAK_KEY);
                }
                if(StrUtil.isNotBlank(operator.getParentOperatorId())){
                    addCheckOperator(operator,operator.getParentOperatorId());
                }
            });
        }

        /**
         * 添加需要校验的数据
         * @param operator
         * @param key
         */
        private void addCheckOperator(Operator operator, String key) {
            List<Operator> list = ObjectUtil.defaultIfNull(checkOperator.get(key),new ArrayList<>());
            list.add(operator);
            checkOperator.put(key,list);
        }

        /**
         * 校验退出循环算子是否符合规则
         */
        public void checkBreakRules(){
            List<Operator> noParentBreakList = checkOperator.get(NO_PARENT_BREAK_KEY);
            //未在循环内的退出算子
            if(CollUtil.isNotEmpty(noParentBreakList)){
                log.error("check break operator noSub:{}",noParentBreakList.stream().map(x-> x.getName() + "," + x.getDesc()).collect(Collectors.toList()));
                throw new OpenException(OpenApiMsgCodeEnum.w_api_graph_break_parse_error);
            }
            checkOperator.forEach((key,value)->{
                //循环算子没有添加子项
                if(CollUtil.isEmpty(value)){
                    log.error("check for operator no item:{}",key);
                    throw new OpenException(OpenApiMsgCodeEnum.w_api_graph_for_item_parse_error);
                }
//                List<Operator> subOperators = value.stream().filter(x-> !(x instanceof ForOperator)).collect(Collectors.toList());
//                if(CollUtil.isEmpty(subOperators)){
//                    log.error("check for operator no item:{}",key);
//                    throw new OpenException(OpenApiMsgCodeEnum.w_api_graph_for_item_parse_error);
//                }

                List<Operator> breakOps = value.stream().filter(x -> x instanceof BreakOperator).collect(Collectors.toList());
                //循环内出现了多个退出算子
                if(CollUtil.isNotEmpty(breakOps) && breakOps.size() > CommonConstant.ONE){
                    log.error("check break operator index:{}",breakOps.stream().map(x-> x.getName() + "," + x.getDesc()).collect(Collectors.toList()));
                    throw new OpenException(OpenApiMsgCodeEnum.w_api_graph_break_parse_error);
                }
                if(CollUtil.isNotEmpty(breakOps)){
                    BreakOperator operator = (BreakOperator) breakOps.get(CommonConstant.ZERO);
                    Set<String> outputOperators = operator.getOutputOperators();
                    //退出算子后面连了循环中其他的节点
                    if(CollUtil.isNotEmpty(outputOperators)
                            && value.stream().anyMatch(x->outputOperators.contains(x.getOperatorId()))){
                        log.error("check break operator last:{}",value.stream().filter(x-> outputOperators.contains(x.getOperatorId())).map(x-> x.getName() + "," + x.getDesc()).collect(Collectors.toList()));
                        throw new OpenException(OpenApiMsgCodeEnum.w_api_graph_break_parse_error);
                    }
                }
            });
        }
    }

    /**
     * 测试
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        String path = "file:////E:\\wekedata\\dw-stream\\streaming-master\\streaming-master-web\\target/streaming-master-web-4.1-SNAPSHOT.jar!/BOOT-INF/lib/streaming-master-common-4.1-SNAPSHOT.jar!/";
        OperatorTools operatorTools = new OperatorTools();
        DsfCycle dsfCycle = operatorTools.new DsfCycle();
        dsfCycle.addEdge("A", "B");
        dsfCycle.addEdge("A", "C");
        dsfCycle.addEdge("B", "D");
        dsfCycle.addEdge("D", "A");
        Set<String> result = dsfCycle.find();
        for (String string : result) {
            System.out.println(string);
        }
    }
}
