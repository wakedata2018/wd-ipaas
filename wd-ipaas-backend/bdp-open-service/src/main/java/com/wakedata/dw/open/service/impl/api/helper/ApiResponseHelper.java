package com.wakedata.dw.open.service.impl.api.helper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.gson.internal.LinkedTreeMap;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.model.api.ApiResponseParamPo;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.utils.JsonSchemaConvertUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author WangChenSheng
 * @descriptor 解析响应参数帮助类
 * @title ApiResponseHelper
 * @date 2022/9/7 15:06
 */
@Component
public class ApiResponseHelper {


    /*** 有响应*/
    private static final int HAD_RESPONSE = 0;

    /*** 模拟数据库自增ID */
    private static final int ID = 1;

    /**
     * 虚拟id
     */
    public static final int VIRTUAL_ID = -1;

    /**
     * 参数类型
     */
    public static final String PARAM_TYPE_OBJECT = "object";
    public static final String PARAM_TYPE_ARRAY = "array";
    public static final String PARAM_TYPE_STRING = "string";
    public static final String PARAM_TYPE_BOOLEAN = "boolean";
    public static final String PARAM_TYPE_INTEGER = "integer";
    public static final String PARAM_TYPE_NUMBER = "number";
    /**
     * 模拟assetColumns参数
     */
    public static final String VIRTUAL_ASSET_COLUMN = "body";

    /**
     * 模拟assetDataType参数
     */
    public static final String VIRTUAL_ASSET_DATA_TYPE = "json";

    /**
     * 解析树状结构的List<ApiRespParamDTO>,并转换成Map<id,ApiRespParamDTO>
     *
     * @param apiRespParamDTOList apiRespParamDTOList
     * @param map map
     */
    public static void buildApiResponseMap(List<ApiRespParamDTO> apiRespParamDTOList, Map<Integer, ApiRespParamDTO> map) {
        // 构建Map<id,paramDTO>
        for (ApiRespParamDTO respParamDTO : apiRespParamDTOList) {
            map.put(respParamDTO.getId(), respParamDTO);
            if (CollectionUtil.isNotEmpty(respParamDTO.getChildApiRespParams())){
                buildApiResponseMap(respParamDTO.getChildApiRespParams(),map);
            }
        }
    }

    /**
     * 将List<ApiResponseParamPo> 转换为 List<ApiRespParamDTO>
     * 如果存在树型结构则构建出树结构
     *
     * @param parentParams parentId=0的响应参数List
     * @param parentIdToApiRes parentIdToApiRes
     * @return List<ApiRespParamDTO>
     */
    public static List<ApiRespParamDTO> buildApiRespParamTree(List<ApiResponseParamPo> parentParams, Map<Integer, List<ApiResponseParamPo>> parentIdToApiRes) {

        if (CollectionUtils.isEmpty(parentParams)) {
            return Collections.emptyList();
        }

        List<ApiRespParamDTO> paramDtoList = new ArrayList<>(parentParams.size());
        for (ApiResponseParamPo parentParam : parentParams) {
            ApiRespParamDTO parentParamDTO = new ApiRespParamDTO();
            BeanUtil.copyProperties(parentParam, parentParamDTO);

            // 获取子树
            List<ApiResponseParamPo> childApiRespParams = parentIdToApiRes.get(parentParam.getId());
            parentParamDTO.setChildApiRespParams(buildApiRespParamTree(childApiRespParams, parentIdToApiRes));

            paramDtoList.add(parentParamDTO);
        }

        return paramDtoList;
    }

    /**
     * 构建响应头和响应体,便于前段展示树状(用于流程编排中的返回参数模版)
     *
     * @param liteFlowResult 响应参数(非树状结构,由n个响应头和1个响应体组成的集合)
     * @return 响应参数(树状)
     */
    public static List<ApiRespParamDTO> buildApiResponseDTOList(List<ApiRespParamDTO> liteFlowResult){
        List<ApiRespParamDTO> responseParamTreeDTOList = new ArrayList<>();
        if (CollectionUtil.isEmpty(liteFlowResult)){
            return Collections.emptyList();
        }

        // 转化响应类型为BODY的响应参数,其他的不做处理
        for (ApiRespParamDTO apiRespParamDTO : liteFlowResult) {
            apiRespParamDTO.setParentId(DwOpenConstant.RESPONSE_PARENT_ID);
            if (ObjectUtil.equal(apiRespParamDTO.getType(),HttpParamKind.HEAD)){
                responseParamTreeDTOList.add(apiRespParamDTO);
            } else {
                apiRespParamDTO.setChildApiRespParams(parseResponsePostData(apiRespParamDTO));
                responseParamTreeDTOList.add(apiRespParamDTO);
            }
        }

        return responseParamTreeDTOList;
    }

    /**
     * 将响应体中的responsePostData转换为List<ApiRespParamDTO>(树状结构)
     *
     * @param apiRespParamDTO 响应体
     * @return List<ApiRespParamDTO>(树状结构)
     */
    private static List<ApiRespParamDTO> parseResponsePostData(ApiRespParamDTO apiRespParamDTO) {
        String responsePostData = apiRespParamDTO.getResponsePostData();
        if (!JSONUtil.isJson(responsePostData) || StringUtils.isEmpty(responsePostData)){
            return Collections.emptyList();
        }
        //给参数树第一层的parentId赋值
        List<ApiRespParamDTO>  apiRespParamDtoList = JsonSchemaConvertUtil.convert(responsePostData).getChildApiRespParams();
        for (ApiRespParamDTO respParamDTO : apiRespParamDtoList) {
            respParamDTO.setParentId(apiRespParamDTO.getId());
        }
        return apiRespParamDtoList;

        // 这里使用递减计数避免出现计数器的值与parentId的值一致导致一致递归调用到同一条数据最后出现栈溢出
/*        AtomicInteger atomicInteger = new AtomicInteger(VIRTUAL_ID);
        LinkedTreeMap<String, Object> map = new LinkedTreeMap<>();
        List<ApiResponseParamPo> responseParamPoList = new ArrayList<>();

        DataAssetEnums.ApiResponseBusinessType businessType = apiRespParamDTO.getBusinessType();
        Integer apiRespParamId = apiRespParamDTO.getId();
        // 将responsePostData转换成List<ApiResponseParamPo>
        if (ObjectUtil.equal(DataAssetEnums.ApiResponseBusinessType.LITEFLOW_RESULT,businessType)){
            // 只有服务编排才会解析补充字段
            map = JsonUtils.JsonSchemaToExtraColum(responsePostData);
        }*/

/*        String responseJson = JsonUtils.JsonSchemaToJson(responsePostData);
        parseJson(JSONUtil.parseObj(responseJson), businessType, apiRespParamId, responseParamPoList,null,map,atomicInteger);

        // List<ApiResponseParamPo>转换List<ApiRespParamDTO>(树状结构)
        if (CollectionUtils.isEmpty(responseParamPoList)) {
            return Collections.emptyList();
        }
        // 父级id->响应参数列表
        Map<Integer, List<ApiResponseParamPo>> parentIdToApiResp = responseParamPoList.stream()
                .collect(Collectors.groupingBy(ApiResponseParamPo::getParentId));
        return buildApiRespParamTree(parentIdToApiResp.get(apiRespParamId), parentIdToApiResp);*/
    }

    /**
     * 将响应体json字符串组装成List<ApiResponseParamPo>数据结构
     * @param responseJson 响应体Json字符串
     * @param businessType 响应体参数类型
     * @param parentId parentId
     * @param responseParamPoList responseParamPoList
     * @param map 补充字段的LinkTreeMap结构
     * @param atomicInteger 模拟数据库自增id
     */
    @SuppressWarnings("unchecked")
    private static void parseJson(JSONObject responseJson, DataAssetEnums.ApiResponseBusinessType businessType,
                                  Integer parentId, List<ApiResponseParamPo> responseParamPoList,
                                  String parentKey, LinkedTreeMap<String, Object> map, AtomicInteger atomicInteger) {

        if (MapUtils.isEmpty(responseJson)) {
            return;
        }

        for (Map.Entry<String, Object> entry : responseJson.entrySet()) {
            ApiResponseParamPo param = new ApiResponseParamPo();
            // 模拟数据库主键自增ID和parentId(方便构建树结构)
            param.setId(atomicInteger.decrementAndGet());
            param.setParentId(parentId);
            param.setBusinessType(businessType);
            param.setType(HttpParamKind.BODY);
            param.setAssetColumns(entry.getKey());
            param.setResponded(HAD_RESPONSE);

            // 填充jsonSchema的补充字段
            String key = ObjectUtil.isNull(parentKey) ? entry.getKey() : parentKey.concat(".").concat(entry.getKey());
            if (CollectionUtil.isNotEmpty(map)){
                Map<String,String> extraColum = (Map<String, String>) map.get(key);
                // 已处理 多级参数无法获取到值
                if(extraColum != null){
                    param.setExpression(extraColum.get(DwOpenConstant.EXPRESSION));
                    param.setDefaultValue(extraColum.get(DwOpenConstant.DEFAULT_VALUE));
                    param.setParamValueType(extraColum.get(DwOpenConstant.PARAM_VALUE_TYPE));
                }
            }

            Object value = entry.getValue();
            if (value instanceof JSONObject) {
                param.setAssetDataType(PARAM_TYPE_OBJECT);
                responseParamPoList.add(param);
                parseJson((JSONObject) value, businessType, param.getId(),responseParamPoList,key, map, atomicInteger);
            } else if (value instanceof JSONArray) {
                if (ObjectUtil.isNotNull(value) && ((JSONArray) value).size() == 0 ){
                    ((JSONArray) value).add(new JSONObject());
                }
                param.setAssetDataType(PARAM_TYPE_ARRAY);
                responseParamPoList.add(param);
                parseJson((JSONObject) ((JSONArray) value).get(0), businessType, param.getId(),responseParamPoList,key, map, atomicInteger);
            } else if (value instanceof String) {
                param.setAssetDataType(PARAM_TYPE_STRING);
                responseParamPoList.add(param);
            } else if (value instanceof Integer) {
                param.setAssetDataType(PARAM_TYPE_INTEGER);
                responseParamPoList.add(param);
            } else if (value instanceof Number) {
                param.setAssetDataType(PARAM_TYPE_NUMBER);
                responseParamPoList.add(param);
            } else if (value instanceof Boolean) {
                param.setAssetDataType(PARAM_TYPE_BOOLEAN);
                responseParamPoList.add(param);
            }
        }
    }

}
