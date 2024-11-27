package com.wakedata.dw.open.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.wakedata.common.core.constants.CommonConstant;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.liteflow.DAGTaskEngine;
import com.wakedata.dw.open.liteflow.OperatorGraphLiteflowService;
import com.wakedata.dw.open.liteflow.component.foreach.ForNodeComponent;
import com.wakedata.dw.open.model.api.flow.ApiFlowAttr;
import com.wakedata.dw.open.model.api.flow.operator.EndOperator;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.api.flow.operator.OperatorAttribute;
import com.wakedata.dw.open.model.api.flow.operator.VertexOperator;
import com.wakedata.dw.open.model.api.flow.operator.edge.*;
import com.wakedata.dw.open.model.api.flow.operator.foreach.ForOperator;
import com.wakedata.dw.open.model.api.flow.operator.trycatch.TryCatchOperator;
import com.wakedata.dw.open.operator.OperatorProcessor;
import com.wakedata.dw.open.operator.OperatorProcessorFactory;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.parammapping.util.ParamMappingsUtils;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.api.dto.LiteFlowAllOperatorTemplateDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.regex.Pattern.compile;

/**
 * @author luomeng
 * @date 2022/12/14 14:20
 */
@Slf4j
public class OperatorGraphLiteflowServiceImpl implements OperatorGraphLiteflowService {

    @Autowired
    private OperatorProcessorFactory operatorProcessorFactory;


    @Override
    public List<Operator> getOperatorInputEdges(ApiFlowAttr apiFlowAttr, String operatorId) {
        DAGTaskEngine.OperatorContainer operatorContainer = DAGTaskEngine.justParse(apiFlowAttr);
        List<Operator> operators = operatorContainer.getSuperiorOperators(operatorId, new ArrayList<>());
        if (!operators.contains(VertexOperator.INSTANCE)) {
            operators.add(operatorContainer.getOperator(VertexOperator.VERTEX_OPERATOR_ID));
        }
        return operators;
    }

    @Override
    public OperatorAttribute assembleEdgeAttribute(OperatorAttribute operatorAttribute, Operator operator) {
        if (operator instanceof OutputMultiEdge) {
            operatorAttribute.getTypeOfEdge().add(Edge.Type.multiOutput);
        }

        if (operator instanceof OutputEdge) {
            operatorAttribute.getTypeOfEdge().add(Edge.Type.output);
        }

        if (!operatorAttribute.getTypeOfEdge().contains(Edge.Type.multiOutput) &&
                !operatorAttribute.getTypeOfEdge().contains(Edge.Type.output)) {
            operatorAttribute.getTypeOfEdge().add(Edge.Type.withoutOutput);
        }

        if (operator instanceof InputMultiEdge) {
            operatorAttribute.getTypeOfEdge().add(Edge.Type.multiInput);
        }

        if (operator instanceof InputEdge) {
            operatorAttribute.getTypeOfEdge().add(Edge.Type.input);
        }

        if (!operatorAttribute.getTypeOfEdge().contains(Edge.Type.multiInput) &&
                !operatorAttribute.getTypeOfEdge().contains(Edge.Type.input)) {
            operatorAttribute.getTypeOfEdge().add(Edge.Type.withoutInput);
        }
        return operatorAttribute;
    }

    @Override
    public List<LiteFlowAllOperatorTemplateDTO> getLiteFlowAllOperatorTemplateDTOList(ApiFlowAttr apiFlowAttr, String operatorType,Boolean isForBreak) {
        DAGTaskEngine.OperatorContainer operatorContainer = DAGTaskEngine.justParse(apiFlowAttr);

        // 判断前端是否传入operator，用来区分查询的是返回结果模版还是入参模版
        Map<String, DAGTaskEngine.OperatorContext> fullOperators;
        if (StringUtils.isBlank(apiFlowAttr.getOperatorId())) {
            // 如果算子id是空的，直接获取全部算子的算子id
            fullOperators = operatorContainer.getFullOperators();
        } else {
            // 算子id不为空，根据算子id往前迭代此算子之前的所有算子上下文
            List<DAGTaskEngine.OperatorContext> inputOperatorContexts = operatorContainer.getInputOperatorContexts(apiFlowAttr.getOperatorId(), new ArrayList<>());
            //循环算子退出条件选择引用值支持选循环算子中的循环变量
            if(isForBreak){
                DAGTaskEngine.OperatorContext operatorContext = operatorContainer.getOperatorContext(apiFlowAttr.getOperatorId());
                if(ObjectUtil.isNotNull(operatorContext)
                        && operatorContext.getOperator() instanceof ForOperator){
                    inputOperatorContexts.add(operatorContext);
                }
            }
            fullOperators = inputOperatorContexts.stream()
                    .collect(Collectors.toMap(context -> context.getOperator().getOperatorId(), context -> context, (oldVal, newVal) -> newVal));
        }

        List<LiteFlowAllOperatorTemplateDTO> liteFlowAllOperatorTemplateDTOList = new ArrayList<>();
        if (null == fullOperators) {
            return liteFlowAllOperatorTemplateDTOList;
        }

        fullOperators.forEach((operateId, operatorContext) -> {
            Operator operator = operatorContext.getOperator();
            String operatorName = operator.getName();
            //根据算子的类名，生成对应的算子处理器
            OperatorProcessor processor = operatorProcessorFactory.getProcessor(operator.getClass().getName());
            //如果生成算子处理器失败，返回一个固定的参数模板
            if (processor == null) {
                LiteFlowAllOperatorTemplateDTO liteFlowAllOperatorTemplateDTO = new LiteFlowAllOperatorTemplateDTO();
                liteFlowAllOperatorTemplateDTO.setNodeId(operateId);
                liteFlowAllOperatorTemplateDTO.setNodeName(operatorName);
                liteFlowAllOperatorTemplateDTOList.add(liteFlowAllOperatorTemplateDTO);
            } else {
                Optional<LiteFlowAllOperatorTemplateDTO> optional = Optional.ofNullable(processor.buildOperatorTemplate(operator, apiFlowAttr.getApiId(), operateId, operatorName));
                optional.ifPresent(liteFlowAllOperatorTemplateDTOList::add);
            }
        });

        List<LiteFlowAllOperatorTemplateDTO> sortList = sort(fullOperators, apiFlowAttr, liteFlowAllOperatorTemplateDTOList);
        return filterDtoList(sortList,operatorType);
    }

    /**
     * 根据编排顺序，从开始节点开始排序
     *
     * @param fullOperators   编排画布对应的算子map
     * @param apiFlowAttr     ApiFlowAttr
     * @param templateDtoList 算子的参数模板信息集合
     * @return 排序后结果
     */
    private List<LiteFlowAllOperatorTemplateDTO> sort(Map<String, DAGTaskEngine.OperatorContext> fullOperators, ApiFlowAttr apiFlowAttr
            , List<LiteFlowAllOperatorTemplateDTO> templateDtoList) {
        List<LiteFlowAllOperatorTemplateDTO> sortList = new ArrayList<>();
        // 首先添加全局变量算子 和 开始算子节点
        Map<String, LiteFlowAllOperatorTemplateDTO> templateDtoMap = templateDtoList.stream().collect(Collectors.toMap(LiteFlowAllOperatorTemplateDTO::getNodeId, x -> x));
        addOperatorParamTemplate(templateDtoMap, VertexOperator.GLOBAL_OPERATOR_ID, sortList);
        addOperatorParamTemplate(templateDtoMap, VertexOperator.VERTEX_OPERATOR_ID, sortList);

        // 根据开始节点算子找到后面出边的算子，依次加入sortList中
        Map<String, Operator> operators = apiFlowAttr.getOperators();
        String sourceOperatorId = apiFlowAttr.getOperatorId();
        Operator operator = operators.get(VertexOperator.VERTEX_OPERATOR_ID);
        Set<String> outputOperators = ((VertexOperator) operator).getOutputOperators();
        if (outputOperators != null) {
            recursionAddOutputTemplate(fullOperators, outputOperators, templateDtoMap, sortList,sourceOperatorId);
        }
        return sortList;
    }

    /**
     * 递归获取出边算子并将对应的模版并添加到sortList中
     *
     * @param fullOperators   编排画布对应的算子map
     * @param outputOperators 出边算子id集合
     * @param templateDtoMap  模版map
     * @param sortList        排序集合
     */
    private void recursionAddOutputTemplate(Map<String, DAGTaskEngine.OperatorContext> fullOperators, Set<String> outputOperators
            , Map<String, LiteFlowAllOperatorTemplateDTO> templateDtoMap, List<LiteFlowAllOperatorTemplateDTO> sortList,String sourceOperatorId) {
        for (String outputOperatorId : outputOperators) {
            if (EndOperator.END_OPERATOR_ID.equals(outputOperatorId)) {
                continue;
            }
            // 将对应的出边算子
            LiteFlowAllOperatorTemplateDTO dto = templateDtoMap.get(outputOperatorId);
            if (dto == null) {
                continue;
            }
            // 如果sortList中已经包含nodeId的数据则跳过
            boolean hasNodeId = sortList.stream().anyMatch(templateDTO -> dto.getNodeId().equals(templateDTO.getNodeId()));
            if (hasNodeId) {
                continue;
            }
            // 拿到对应算子的出边信息，递归添加后面的算子
            Operator operator = fullOperators.get(outputOperatorId).getOperator();
            //添加循环算子的响应参数处理
            forRespParamHandle(templateDtoMap, dto, operator);

            sortList.add(dto);

            if (operator == null) {
                continue;
            }
            //循环内子流程可选择子流程中的参数引用，主流程无法引用子流程参数
            if (operator instanceof ForOperator && StrUtil.isNotBlank(sourceOperatorId)
                    && CollectionUtil.isNotEmpty(((ForOperator) operator).getSubOperators())
                    && ((ForOperator) operator).getSubOperators().contains(sourceOperatorId)) {
                recursionAddOutputTemplate(fullOperators, ((ForOperator) operator).getSubOperators(), templateDtoMap, sortList, sourceOperatorId);
            }
            if (operator instanceof TryCatchOperator && StrUtil.isNotBlank(sourceOperatorId)) {
                TryCatchOperator tryCatchOperator = (TryCatchOperator) operator;
                // 异常算子要判断sourceOperatorId是在哪个子流程中，只能获取算子所在的子流程中的前面算子出参
                if (CollectionUtil.isNotEmpty(tryCatchOperator.getSubTryOperators()) && tryCatchOperator.getSubTryOperators().contains(sourceOperatorId)) {
                    recursionAddOutputTemplate(fullOperators, tryCatchOperator.getSubTryOperators(), templateDtoMap, sortList, sourceOperatorId);
                }
                if (CollectionUtil.isNotEmpty(tryCatchOperator.getSubCatchOperators()) && tryCatchOperator.getSubCatchOperators().contains(sourceOperatorId)) {
                    recursionAddOutputTemplate(fullOperators, tryCatchOperator.getSubCatchOperators(), templateDtoMap, sortList, sourceOperatorId);
                }
            }
            if (operator instanceof TryCatchOperator && StrUtil.isBlank(sourceOperatorId)) {
                TryCatchOperator tryCatchOperator = (TryCatchOperator) operator;
                recursionAddOutputTemplate(fullOperators, tryCatchOperator.getSubTryOperators(), templateDtoMap, sortList, sourceOperatorId);
                recursionAddOutputTemplate(fullOperators, tryCatchOperator.getSubCatchOperators(), templateDtoMap, sortList, sourceOperatorId);
            }
            if (operator instanceof OutputMultiEdge) {
                Set<String> outputOperatorIds = ((OutputMultiEdge) operator).getOutputOperators();
                recursionAddOutputTemplate(fullOperators, outputOperatorIds, templateDtoMap, sortList, sourceOperatorId);
            }
        }
    }

    /**
     * 循环算子参数处理
     * @param templateDtoMap
     * @param dto
     * @param operator
     */
    private static void forRespParamHandle(Map<String, LiteFlowAllOperatorTemplateDTO> templateDtoMap, LiteFlowAllOperatorTemplateDTO dto, Operator operator) {
        if(ObjectUtil.isNotNull(operator) && operator instanceof ForOperator){
            List<ApiRespParamDTO> child = dto.getApiRespParamDTOS().get(CommonConstant.ZERO).getChildApiRespParams();
            for(ApiRespParamDTO respParamDTO : child){
                //需要将引用值转换成对应的对应结构
                if(respParamDTO.getAssetColumns().equals(ForNodeComponent.LoopFieldEnum.CURRENT_ITEM.getField())
                        && StringUtils.isNotBlank(respParamDTO.getExpression())){
                    List<String> expressions = compile("\\.").splitAsStream(respParamDTO.getExpression()).collect(Collectors.toList());
                    //排除前三个数据，$.nodeName.BODY.xxx
                    int index = 2;
                    LiteFlowAllOperatorTemplateDTO sourceTemplate = null;
                    for(Map.Entry<String,LiteFlowAllOperatorTemplateDTO> entry:templateDtoMap.entrySet()){
                        if(entry.getValue().getNodeName().equals(expressions.get(CommonConstant.ONE))){
                            sourceTemplate = entry.getValue();
                            break;
                        }
                    }
                    if(ObjectUtil.isNull(sourceTemplate)){
                        continue;
                    }
                    //引用的参数类型
                    List<ApiRespParamDTO> expressionNodeParams = sourceTemplate.getApiRespParamDTOS();
                    for(int i = index;i<expressions.size();i++) {
                        boolean isBreak = true;
                        for (ApiRespParamDTO resp : expressionNodeParams) {
                            if((i == index && resp.getAssetColumns().equals(expressions.get(i).toLowerCase()))
                                    || resp.getAssetColumns().equals(expressions.get(i))){
                                expressionNodeParams = resp.getChildApiRespParams();
                                isBreak = false;
                                break;
                            }
                        }
                        if(isBreak){
                            expressionNodeParams = null;
                            break;
                        }
                    }
                    if(CollectionUtil.isNotEmpty(expressionNodeParams)){
                        respParamDTO.setId(expressionNodeParams.get(CommonConstant.ZERO).getParentId());
                    }
                    respParamDTO.setChildApiRespParams(expressionNodeParams);
                    break;
                }
            }
        }
    }


    private List<LiteFlowAllOperatorTemplateDTO> filterDtoList(List<LiteFlowAllOperatorTemplateDTO> liteFlowAllOperatorTemplateDTOList, String operatorType) {
        // 筛选第一层和第二层中apiRespParamDTOS为null的数据
        for (Iterator<LiteFlowAllOperatorTemplateDTO> it = liteFlowAllOperatorTemplateDTOList.iterator(); it.hasNext();) {
            LiteFlowAllOperatorTemplateDTO liteFlowAllOperatorTemplateDTO = it.next();
            // 过滤第一层，如果这个算子没有query和body的模板参数，直接去掉这个算子，不展示在这个引用值参数模板上
            if (CollectionUtil.isEmpty(liteFlowAllOperatorTemplateDTO.getApiRespParamDTOS())){
                it.remove();
                continue;
            }
            // 过滤第二层（算子底下的第一层）
            Iterator<ApiRespParamDTO> iterator = liteFlowAllOperatorTemplateDTO.getApiRespParamDTOS().iterator();
            while (iterator.hasNext()) {
                ApiRespParamDTO apiRespParamDTO = iterator.next();
                // 位置在BODY，子参数模板是null，并且不是array的，需要去除
                if(ObjectUtil.equal(apiRespParamDTO.getType(), HttpParamKind.BODY)
                        && CollectionUtil.isEmpty(apiRespParamDTO.getChildApiRespParams())
                        && !DataAssetEnums.AssetDataTypeEnum.ARRAY.getValue().equalsIgnoreCase(ParamMappingsUtils.buildDataType(apiRespParamDTO.getAssetDataType()))){
                   iterator.remove();
                }
            }
        }

        // 根据类型获取指定的算子的参数列表
        if (operatorType != null){
            liteFlowAllOperatorTemplateDTOList = liteFlowAllOperatorTemplateDTOList.stream()
                    .filter(liteFlowAllOperatorTemplateDTO -> Objects.equals(liteFlowAllOperatorTemplateDTO.getNodeClass(), operatorType)).collect(Collectors.toList());
        }

        return liteFlowAllOperatorTemplateDTOList;
    }


    /**
     * 添加全局变量算子 和 开始算子参数模版列表
     */
    private static void addOperatorParamTemplate(Map<String, LiteFlowAllOperatorTemplateDTO> templateDtoMap, String globalOperatorId, List<LiteFlowAllOperatorTemplateDTO> sortList) {
        LiteFlowAllOperatorTemplateDTO dto = templateDtoMap.get(globalOperatorId);
        if (dto != null) {
            sortList.add(dto);
        }
    }

}
