package com.wakedata.dw.open.liteflow.component.variable;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.github.wnameless.json.unflattener.JsonUnflattener;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.liteflow.NodeRunTimeContext;
import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.liteflow.component.variable.helper.VariableUtil;
import com.wakedata.dw.open.liteflow.slot.ApiFlowSlot;
import com.wakedata.dw.open.model.api.flow.operator.variable.UpdateVariableOperator;
import com.wakedata.dw.open.model.api.flow.operator.variable.model.VariableParams;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.parammapping.util.JsonUtil;
import com.wakedata.dw.open.parammapping.util.ParamMappingsUtils;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.utils.JsonSchemaConvertUtil;
import com.wakedata.dw.open.service.utils.JsonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.wakedata.dw.open.liteflow.component.variable.helper.VariableUtil.convertContextParamMappings;

/**
 * @author WangChenSheng
 * @descriptor
 * @title UpdateVariableNodeComponent
 * @date 2022/11/15 15:39
 */
public class UpdateVariableNodeComponent extends AbstractNodeComponent<UpdateVariableOperator> {

    private static final String JSON_OBJECT = "JSONObject";

    private static final String DEFAULT_ARRAY_INDEX = "[0]";

    @Override
    protected JSON simpleProcessInternal(NodeRunTimeContext nodeRunTimeContext, JSONObject currentRequestParams) throws Exception {
        return null;
    }

    @Override
    protected OpenApiMsgCodeEnum getOpenApiMsgEnum() {
        return OpenApiMsgCodeEnum.w_api_graph_execute_variable_update_operator_error;
    }

    @Override
    public void processCall() throws Exception {
        NodeRunTimeContext nodeRunTimeContext = threadLocal.get();
        ApiFlowSlot apiFlowSlot = nodeRunTimeContext.getApiFlowSlot();
        UpdateVariableOperator variableOperator = (UpdateVariableOperator) nodeRunTimeContext.getOperatorContext().getOperator();
        apiFlowSlot.addLog(DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + String.format(" 更新变量算子[%s]: 执行开始....", variableOperator.getName()));

        // 获取更新变量算子中的变量
        VariableParams variableParams = variableOperator.getComponent().getVariableParams();
        if (Objects.isNull(variableParams)){
            variableParams = new VariableParams();
        }
        // 获取每个创建变量算子需要更新对变量
        List<ApiRespParamDTO> apiRespParamDTOList = sortVariable(variableParams.getVariableJsonSchema(), JsonSchemaConvertUtil.convert(variableParams.getVariableJsonSchema()).getChildApiRespParams());
        Map<String, List<ApiRespParamDTO>> updateVariableFiledMap = buildUpdateVariableFiledMap(apiRespParamDTOList);
        JSON message = VariableUtil.convertParams(apiFlowSlot, apiRespParamDTOList);
        // 更新指定的变量
        updateValueAndPutContext(apiFlowSlot, updateVariableFiledMap);
        apiFlowSlot.addLog(DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + String.format(" 更新变量算子[%s]: 获取参数成功！\n",
                variableOperator.getName()) + ParamMappingsUtils.prettyFormatJson(message));
        apiFlowSlot.addLog(DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + String.format(" 更新变量算子[%s]: 执行结束....！\n", variableOperator.getName()));
    }

    /**
     * 获取每个创建变量算子需要更新的变量
     */
    private Map<String,List<ApiRespParamDTO>> buildUpdateVariableFiledMap(List<ApiRespParamDTO> apiRespParamDTOList) {
        Map<String,List<ApiRespParamDTO>> updateVariableFiledMap = new HashMap<>(apiRespParamDTOList.size());
        for (ApiRespParamDTO apiRespParamDTO : apiRespParamDTOList) {
            String operatorName = StringUtils.substringBefore(apiRespParamDTO.getAssetColumns(),DwOpenConstant.JOIN_POINT);
            String assetColumns = StringUtils.substringAfter(apiRespParamDTO.getAssetColumns(),DwOpenConstant.JOIN_POINT);
            ApiRespParamDTO convertApiResponse = convertApiResponse(apiRespParamDTO, assetColumns);

            // 以创建变量为分组,构建外层结构
            if (Objects.isNull(updateVariableFiledMap.get(operatorName))){
                List<ApiRespParamDTO> list = new ArrayList<>();
                list.add(convertApiResponse);
                updateVariableFiledMap.put(operatorName, list);
            } else {
                updateVariableFiledMap.get(operatorName).add(convertApiResponse);
            }
        }
        return updateVariableFiledMap;
    }

    /**
     * 更新指定的变量并放入编排上下文
     *
     * @param apiFlowSlot apiFlowSlot
     * @param updateVariableFiledMap updateVariableFiledMap
     */
    public static void updateValueAndPutContext(ApiFlowSlot apiFlowSlot, Map<String, List<ApiRespParamDTO>> updateVariableFiledMap) {
        Map<String,JSON> contextParamMappings;

        for (String operatorName : updateVariableFiledMap.keySet()) {
            // 解析每一行的变量，获取需要更新的字段以及更新的value
            JSON updateValue;
            List<ApiRespParamDTO> updateApiRespParamDTOList = updateVariableFiledMap.get(operatorName);
            for (ApiRespParamDTO apiRespParamDTO : updateApiRespParamDTOList) {
                contextParamMappings = convertContextParamMappings(apiFlowSlot.getFullOperatorResults());
                updateValue = VariableUtil.convertParams(apiFlowSlot, Collections.singletonList(apiRespParamDTO));
                updateValueAndPutContext(operatorName, contextParamMappings, updateValue, apiFlowSlot);
            }
        }
    }

    /**
     * 类型为对象时，需要构建childList结构
     */
    private ApiRespParamDTO convertApiResponse(ApiRespParamDTO apiRespParamDTO, String assetColumns) {
        String[] split = StringUtils.split(assetColumns, DwOpenConstant.JOIN_POINT);
        return buildApiRespParamDTO(apiRespParamDTO, split);
    }

    /**
     * 更新指定指端的值并放入上下文
     */
    private static void updateValueAndPutContext(String name, Map<String, JSON> contextParamMappings, JSON updateValue, ApiFlowSlot apiFlowSlot) {
        JSONObject contextParamMapping = (JSONObject) contextParamMappings.get(name);
        if (Objects.isNull(contextParamMapping)){
            return;
        }

        if (Objects.equals(contextParamMapping.getClass().getSimpleName(),JSON_OBJECT)){
            JSONObject context = contextParamMapping.getJSONObject(HttpParamKind.BODY.name());
            JSONObject update = (JSONObject) updateValue;

            // 将创建变量数据扁平和更新变量数据扁平化
            Map<String, Object> updateMap = new JsonFlattener(JSON.toJSONString(update, SerializerFeature.WriteMapNullValue)).withSeparator('.').flattenAsMap();
            Map<String, Object> createMap = new JsonFlattener(JSON.toJSONString(context, SerializerFeature.WriteMapNullValue)).withSeparator('.').flattenAsMap();

            // 处理对象.数组的情况 并更新指定字段
            String key = new ArrayList<>(updateMap.keySet()).get(DwOpenConstant.DEFAULT_FIRST);
            if (StringUtils.contains(key,DEFAULT_ARRAY_INDEX)){
                updateMap = processingUpdateMap(updateMap);
            }

            // 去扁平
            createMap.putAll(updateMap);
            String unFlattenJson = JsonUnflattener.unflatten(String.valueOf(createMap));

            // 以key = 创建变量算子 value=更新后的数据 放入全局参数池中
            storeOperatorResultSet(apiFlowSlot,name,JSONObject.parseObject(unFlattenJson));
        }
    }

    /**
     * 处理对象.数组的情况
     */
    private static Map<String, Object> processingUpdateMap(Map<String, Object> updateMap) {

        Map<String, Object> map = new HashMap<>(updateMap.size());
        List<Object> values = new ArrayList<>(updateMap.values());

        String key = new ArrayList<>(updateMap.keySet()).get(DwOpenConstant.DEFAULT_FIRST);
        if (StringUtils.endsWith(key,DwOpenConstant.MIDDLE_BRACKET_RIGHT)){
            // 对象.数组[基本数据类型]
            key = StringUtils.substringBefore(new ArrayList<>(updateMap.keySet()).get(0),DwOpenConstant.MIDDLE_BRACKET_LEFT);
            map.put(key,JSONObject.toJSONString(values));
        } else {
            // 对象.数组[对象]
            key  = StringUtils.substringBefore(StringUtils.substringBeforeLast(key,DwOpenConstant.JOIN_POINT),DwOpenConstant.MIDDLE_BRACKET_LEFT);
            String[] nameList = StringUtils.split(key, DwOpenConstant.JOIN_POINT);
            String lastColum = ArrayUtil.get(nameList,nameList.length - 1);

            JSONObject jsonObject = JSONObject.parseObject(JsonUnflattener.unflatten(String.valueOf(updateMap)));
            for (String colum : nameList) {
                if (ObjectUtil.notEqual(lastColum,colum)){
                    jsonObject = jsonObject.getJSONObject(colum);
                }
            }

            map.put(key,jsonObject.getJSONArray(lastColum));
        }

        return map;
    }


    public List<ApiRespParamDTO> sortVariable(String jsonSchema, List<ApiRespParamDTO> apiRespParamDTOList){
        if (StringUtils.isEmpty(jsonSchema)){
            return new ArrayList<>();
        }
        JSONObject jsonObject = JSONObject.parseObject(jsonSchema, Feature.OrderedField).getJSONObject(JsonUtils.SCHEMA_ROOT).getJSONObject(JsonUtils.SCHEMA_PROPERTIES);
        List<String> nameList =  jsonObject.values().stream().map(value -> ((JSONObject) value).getString(JsonUtils.SCHEMA_NAME)).collect(Collectors.toList());

        Map<String,ApiRespParamDTO> respParamMap = apiRespParamDTOList.stream().collect(Collectors.toMap(ApiRespParamDTO::getAssetColumns, apiRespParamDTO -> apiRespParamDTO, (assetColumns, apiRespParamDTO) -> apiRespParamDTO));
        return nameList.stream().map(respParamMap::get).collect(Collectors.toList());
    }

    /**
     * 将变量算子的参数放入上下文
     *
     * @param apiFlowSlot apiFlowSlot
     * @param name name
     * @param message message
     */
    private static void storeOperatorResultSet(ApiFlowSlot apiFlowSlot, String name, JSON message) {
        JSONObject fullOperatorResults = apiFlowSlot.getFullOperatorResults();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(HttpParamKind.BODY.name(), message);
        fullOperatorResults.put(name, jsonObject);
    }

    /**
     * 构建每个创建变量算子的ApiRespParamDTO
     */
    private static ApiRespParamDTO buildApiRespParamDTO(ApiRespParamDTO apiRespParamDTO, String[] split) {

        // 构建List<ApiRespParamDTO>
        int number = 0;
        Integer parentId = apiRespParamDTO.getParentId();
        List<ApiRespParamDTO> list = new ArrayList<>();
        for (String s : split) {
            Integer id = JsonSchemaConvertUtil.getId(parentId, number++);
            ApiRespParamDTO respParamDTO = new ApiRespParamDTO();
            respParamDTO.setId(id);
            respParamDTO.setParentId(parentId);
            respParamDTO.setAssetColumns(s);
            respParamDTO.setAssetDataType(DataAssetEnums.AssetDataTypeEnum.OBJECT.getValue());
            respParamDTO.setType(HttpParamKind.BODY);
            list.add(respParamDTO);
            parentId = id;
        }

        if (CollectionUtils.isEmpty(list)){
            return apiRespParamDTO;
        }

        // 同步最后一层的信息
        ApiRespParamDTO last = list.get(list.size() - 1);
            last.setExpression(apiRespParamDTO.getExpression());
            last.setParamValueType(apiRespParamDTO.getParamValueType());
            last.setAssetDataType(apiRespParamDTO.getAssetDataType());
            last.setDefaultValue(apiRespParamDTO.getDefaultValue());
            last.setResultColumnType(apiRespParamDTO.getResultColumnType());
            last.setDescription(apiRespParamDTO.getDescription());

        // 构建树结构
        Map<Integer, List<ApiRespParamDTO>> parentIdToApiResp = list.stream()
                .collect(Collectors.groupingBy(ApiRespParamDTO::getParentId));
        return VariableUtil.buildApiRespParamTree(parentIdToApiResp.get(apiRespParamDTO.getParentId()), parentIdToApiResp).get(0);

    }

}
