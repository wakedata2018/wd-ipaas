package com.wakedata.dw.open.service.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.constant.JsonSchemaConstant;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.DataTypeEnum;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.parammapping.ParamMappingTypeEnum;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.impl.api.helper.ApiResponseHelper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * json schema格式转换，构造id，每个层级初始id在父id前面加1位数，最多10位数，如 100->1100->11100->111100
 * @author luomeng
 * @date 2022/10/29 13:15
 */
@Slf4j
public class JsonSchemaConvertUtil {

    /**
     * ID最大位数
     */
    private static final int MAX_VALUE_LENGTH = 9;

    /**
     * 超出后id生成规则改为加固定数
     */
    private static final int ADD_VALUE = 10000;

    /**
     * 标准ResultDTO格式的json schema
     */
    private static final String RESULT_DTO_JSON_SCHEMA = "{\"root\":{\"type\":\"object\",\"name\":\"root\",\"description\":\"根层级\",\"properties\":{\"field_0.8576029777513321\":{\"name\":\"code\",\"type\":\"integer\",\"value\":\"\",\"description\":\"状态码\"},\"field_0.7723207979860869\":{\"type\":\"string\",\"name\":\"msg\",\"description\":\"返回内容的描述\"},\"field_0.5537838171963632\":{\"name\":\"success\",\"type\":\"boolean\",\"description\":\"接口执行状态\"},\"field_0.5044802879838786\":{\"name\":\"data\",\"type\":\"object\",\"description\":\"返回的数据对象\"}}}}";

    /**
     * 标准PageResultDTO格式的json schema
     */
    private static final String PAGE_RESULT_DTO_JSON_SCHEMA = "{\"root\":{\"type\":\"object\",\"name\":\"root\",\"description\":\"根层级\",\"properties\":{\"field_0.8576029777513321\":{\"name\":\"code\",\"type\":\"integer\",\"value\":\"\",\"description\":\"状态码\"},\"field_0.7723207979860869\":{\"type\":\"string\",\"name\":\"msg\",\"description\":\"返回内容的描述\"},\"field_0.5044802879838786\":{\"name\":\"data\",\"type\":\"object\",\"description\":\"返回的数据对象\"},\"field_0.32971746873163665\":{\"name\":\"pageNo\",\"type\":\"integer\",\"description\":\"分页参数，当前页码\"},\"field_0.4803044785004511\":{\"name\":\"pageSize\",\"type\":\"integer\",\"description\":\"分页参数，每页显示数据的条数\"},\"field_0.8594612211247159\":{\"name\":\"totalCount\",\"type\":\"long\",\"description\":\"分页参数，返回数据总条数\"}}}}";

    /**
     * json schema格式转对象
     * @param jsonSchema
     * @return
     */
    public static ApiRespParamDTO convert(String jsonSchema){
        ApiRespParamDTO apiRespParamDTO = new ApiRespParamDTO();
        apiRespParamDTO.setId(DwOpenConstant.JSON_SCHEMA_INIT_ID);
        apiRespParamDTO.setParentId(DwOpenConstant.RESPONSE_PARENT_ID);
        apiRespParamDTO.setType(HttpParamKind.BODY);
        apiRespParamDTO.setResponsePostData(jsonSchema);
        apiRespParamDTO.setAssetColumns(ApiResponseHelper.VIRTUAL_ASSET_COLUMN);
        apiRespParamDTO.setAssetDataType(ApiResponseHelper.VIRTUAL_ASSET_DATA_TYPE);
        apiRespParamDTO.setDescription(HttpParamKind.BODY.name());
        apiRespParamDTO.setChildApiRespParams(parse(jsonSchema,DwOpenConstant.JSON_SCHEMA_INIT_ID));
        return apiRespParamDTO;
    }

    /**
     * 将标准ResultDTO格式的json schema转对象
     *
     * @return API响应对象
     */
    public static ApiRespParamDTO convertResultDto() {
        return convert(RESULT_DTO_JSON_SCHEMA);
    }

    /**
     * 将标准PageResultDTO格式的json schema转对象
     *
     * @return API响应对象
     */
    public static ApiRespParamDTO convertPageResultDto() {
        return convert(PAGE_RESULT_DTO_JSON_SCHEMA);
    }

    /**
     * 响应参数对象转换为json schema
     *
     * @param apiRespParamDTO 响应参数对象
     * @return json schema
     */
    public static String toJsonSchemaString(ApiRespParamDTO apiRespParamDTO) {
        JSONObject rootParam = new JSONObject();
        rootParam.set(JsonSchemaConstant.TYPE_KEY, DataTypeEnum.OBJECT.getType());
        rootParam.set(JsonSchemaConstant.NAME_KEY, JsonSchemaConstant.ROOT_KEY);
        rootParam.set(JsonSchemaConstant.DESCRIPTION_KEY, JsonSchemaConstant.ROOT_DESCRIPTION_VALUE);
        List<ApiRespParamDTO> childApiRespParams = apiRespParamDTO.getChildApiRespParams();
        JSONObject propertiesObj = CollectionUtil.isNotEmpty(childApiRespParams) ? createProperties(childApiRespParams) : JSONUtil.createObj();
        rootParam.set(JsonSchemaConstant.PROPERTIES_KEY, propertiesObj);
        return JSONUtil.createObj().putOnce(JsonSchemaConstant.ROOT_KEY, rootParam).toString();
    }

    /**
     * 将局部的参数schema json对象转换为响应参数对象
     *
     * @param sectionSchemaStr 局部schema参数json字符串
     * @return 响应参数对象
     */
    public static ApiRespParamDTO convertSectionSchema(String sectionSchemaStr) {
        JSONObject sectionSchema = JSONUtil.parseObj(sectionSchemaStr);
        ApiRespParamDTO apiRespParamDTO = new ApiRespParamDTO();
        apiRespParamDTO.setAssetColumns(sectionSchema.getStr(JsonSchemaConstant.NAME_KEY));
        apiRespParamDTO.setAssetDataType(sectionSchema.getStr(JsonSchemaConstant.TYPE_KEY));
        if (sectionSchema.containsKey(JsonSchemaConstant.DESCRIPTION_KEY)) {
            apiRespParamDTO.setDescription(sectionSchema.getStr(JsonSchemaConstant.DESCRIPTION_KEY));
        }
        if (DataAssetEnums.AssetDataTypeEnum.ARRAY.getValue().equals(sectionSchema.getStr(JsonSchemaConstant.TYPE_KEY))) {
            // 处理数组
            if (ObjectUtil.isNotNull(sectionSchema.getJSONObject(JsonSchemaConstant.ITEMS_KEY))) {
                JSONObject items = sectionSchema.getJSONObject(JsonSchemaConstant.ITEMS_KEY);
                apiRespParamDTO.setAssetDataType(apiRespParamDTO.getAssetDataType() + "<" + items.getStr(JsonSchemaConstant.TYPE_KEY) + ">");
                if (DataAssetEnums.AssetDataTypeEnum.OBJECT.getValue().equals(items.getStr(JsonSchemaConstant.TYPE_KEY)) || items.containsKey(JsonSchemaConstant.PROPERTIES_KEY)) {
                    JSONObject propertiesObject = items.getJSONObject(JsonSchemaConstant.PROPERTIES_KEY);
                    apiRespParamDTO.setChildApiRespParams(parseItems(null, null, propertiesObject));
                }
            }
        } else if (sectionSchema.containsKey(JsonSchemaConstant.PROPERTIES_KEY)) {
            // 处理对象
            JSONObject propertiesObject = sectionSchema.getJSONObject(JsonSchemaConstant.PROPERTIES_KEY);
            apiRespParamDTO.setChildApiRespParams(parseItems(null, null, propertiesObject));
        }
        return apiRespParamDTO;
    }

    /**
     * 根据子返回参数集合构建json schema的properties属性
     *
     * @param childApiRespParams 子返回参数集合
     * @return 子返回参数json对象
     */
    private static JSONObject createProperties(List<ApiRespParamDTO> childApiRespParams) {
        JSONObject properties = JSONUtil.createObj();
        for (ApiRespParamDTO childApiRespParam : childApiRespParams) {
            String paramName = childApiRespParam.getAssetColumns();
            String dataType = childApiRespParam.getAssetDataType();
            JSONObject paramObject = JSONUtil.createObj()
                    .putOnce(JsonSchemaConstant.NAME_KEY, paramName)
                    .putOnce(JsonSchemaConstant.TYPE_KEY, dataType.startsWith(JsonSchemaConstant.ARRAY_TYPE_VALUE) ? JsonSchemaConstant.ARRAY_TYPE_VALUE : dataType)
                    .putOnce(JsonSchemaConstant.DESCRIPTION_KEY, childApiRespParam.getDescription())
                    // 示例值，在键和值都为非空的情况下put到JSONObject中
                    .putOpt(JsonSchemaConstant.VALUE_KEY, childApiRespParam.getDefaultValue());
            List<ApiRespParamDTO> childParams = childApiRespParam.getChildApiRespParams();
            if (dataType.startsWith(JsonSchemaConstant.ARRAY_TYPE_VALUE)) {
                // 如果是参数是数组类型，需要添加items节点
                String genericType = dataType.replace("array<", "").replace(">", "");
                // 构建items对象
                JSONObject itemsObject = JSONUtil.createObj()
                        .putOnce(JsonSchemaConstant.TYPE_KEY, genericType)
                        .putOnce(JsonSchemaConstant.NAME_KEY, JsonSchemaConstant.ITEMS_KEY);
                if (DataTypeEnum.OBJECT.getType().equals(genericType) && CollectionUtil.isNotEmpty(childParams)) {
                    itemsObject.set(JsonSchemaConstant.PROPERTIES_KEY, createProperties(childParams));
                }
                paramObject.putOnce(JsonSchemaConstant.ITEMS_KEY, itemsObject);
            } else {
                // 类型不是数组，直接判断是否存在子参数，存在继续递归
                if (CollectionUtil.isNotEmpty(childParams)) {
                    paramObject.set(JsonSchemaConstant.PROPERTIES_KEY, createProperties(childParams));
                }
            }
            properties.putOnce(paramName, paramObject);
        }
        return properties;
    }

    /**
     * 解析jsonschema
     * @param jsonSchema
     * @param initId 初始id
     * @return
     */
    private static List<ApiRespParamDTO> parse(String jsonSchema,Integer initId) {
        JSONObject jsonObject = JSONUtil.parseObj(jsonSchema);
        JSONObject root = jsonObject.getJSONObject(JsonUtils.SCHEMA_ROOT);
        List<ApiRespParamDTO> respParamDTOList = new ArrayList<>();
        if (root != null) {
            if (DataAssetEnums.AssetDataTypeEnum.ARRAY.getValue().equals(root.get(JsonUtils.SCHEMA_TYPE))) {
                if (ObjectUtil.isNotNull(root.getJSONObject(JsonUtils.SCHEMA_ITEMS).getJSONObject(JsonUtils.SCHEMA_PROPERTIES))) {
                    JSONObject properties = root.getJSONObject(JsonUtils.SCHEMA_ITEMS).getJSONObject(JsonUtils.SCHEMA_PROPERTIES);
                    respParamDTOList = parseItems(initId, respParamDTOList, properties);
                }
            } else if (root.getJSONObject(JsonUtils.SCHEMA_PROPERTIES) != null) {
                JSONObject properties = root.getJSONObject(JsonUtils.SCHEMA_PROPERTIES);
                respParamDTOList = parseItems(initId, respParamDTOList, properties);
            }
        }
        return respParamDTOList;
    }

    /**
     * 解析子项
     * @param initId
     * @param respParamDTOList
     * @param properties
     * @return
     */
    private static List<ApiRespParamDTO> parseItems(Integer initId, List<ApiRespParamDTO> respParamDTOList, JSONObject properties) {
        if(respParamDTOList == null){
            respParamDTOList = new ArrayList<>();
        }
        if(properties == null){
            return respParamDTOList;
        }
        int number = 0;
        for(Map.Entry<String,Object> data : properties.entrySet()){
            JSONObject item = (JSONObject) data.getValue();
            ApiRespParamDTO respParamDTO = new ApiRespParamDTO();
            if (initId != null) {
                respParamDTO.setId(getId(initId, number++));
                respParamDTO.setParentId(initId);
            }
            respParamDTO.setType(HttpParamKind.BODY);
            respParamDTO.setAssetColumns(item.getStr(JsonUtils.SCHEMA_NAME));
            respParamDTO.setAssetDataType(item.getStr(JsonUtils.SCHEMA_TYPE));
            respParamDTO.setDescription(item.getStr(JsonUtils.SCHEMA_DESCRIPTION));
            respParamDTO.setAllowEmpty(item.getBool(JsonUtils.SCHEMA_ALLOW_EMPTY));
            if(ParamMappingTypeEnum.FIXED_TYPE.getType().equals(item.getStr(JsonUtils.SCHEMA_PARAM_VALUE_TYPE))) {
                respParamDTO.setDefaultValue(item.getStr(JsonUtils.SCHEMA_DEFAULT_VALUE));
            }else{
                respParamDTO.setDefaultValue(item.getStr(JsonUtils.SCHEMA_VALUE));
            }
            respParamDTO.setParamValueType(item.getStr(JsonUtils.SCHEMA_PARAM_VALUE_TYPE));
            respParamDTO.setExpression(item.getStr(JsonUtils.SCHEMA_EXPRESSION));
            //对象或数组需要循环遍历子项
            if(JsonUtils.SCHEMA_PARAMETER_OBJECT.equals(respParamDTO.getAssetDataType())){
                respParamDTO.setChildApiRespParams(parseItems(respParamDTO.getId(),respParamDTO.getChildApiRespParams(),item.getJSONObject(JsonUtils.SCHEMA_PROPERTIES)));
            }
            if(JsonUtils.SCHEMA_PARAMETER_ARRAY.equals(respParamDTO.getAssetDataType())){
                JSONObject items = item.getJSONObject(JsonUtils.SCHEMA_ITEMS);
                respParamDTO.setAssetDataType(respParamDTO.getAssetDataType() + "<"+items.getStr(JsonUtils.SCHEMA_TYPE)+">");
                //对象数组
                if(JsonUtils.SCHEMA_PARAMETER_OBJECT.equals(items.getStr(JsonUtils.SCHEMA_TYPE))){
                    respParamDTO.setChildApiRespParams(parseItems(respParamDTO.getId(),respParamDTO.getChildApiRespParams(),items.getJSONObject(JsonUtils.SCHEMA_PROPERTIES)));
                }
            }
            respParamDTOList.add(respParamDTO);
        }
        return respParamDTOList;
    }


    /**
     * id生成规则
     * @param initId
     * @param number
     * @return
     */
    public static Integer getId(Integer initId,int number){
        String subItemId = ("1"+initId);
        if(subItemId.length() > MAX_VALUE_LENGTH){
            subItemId = String.valueOf(initId + ADD_VALUE);
        }
        return Integer.parseInt(subItemId) + number;
    }



    public static void main(String[] args) {
//        String schema = "{\"root\":{\"type\":\"object\",\"name\":\"root\",\"description\":\"根层级\",\"properties\":{\"field_0.9555999797517156\":{\"type\":\"string\",\"name\":\"respLiteBody1\",\"paramValueType\":\"reference\",\"expression\":\"$.start.QUERY.liteflowQuery1\",\"defaultValue\":\"\",\"operatorId\":\"start\",\"description\":\"响应参数1\"},\"field_0.04957151978665597\":{\"type\":\"string\",\"name\":\"respLiteString2\",\"paramValueType\":\"reference\",\"expression\":\"$.start.BODY.liteflowString2\",\"defaultValue\":\"\",\"operatorId\":\"start\",\"description\":\"响应字符串参数2\"},\"field_0.35210397507631663\":{\"name\":\"object3\",\"type\":\"object\",\"properties\":{\"field_0.2208271502012895\":{\"name\":\"obj31\",\"type\":\"string\",\"paramValueType\":\"reference\",\"expression\":\"$.start.BODY.liteflowInt1\",\"defaultValue\":\"\",\"operatorId\":\"start\",\"description\":\"测试31\"},\"field_0.24009297130685736\":{\"name\":\"obj32\",\"type\":\"object\",\"properties\":{\"field_0.7494013586326462\":{\"type\":\"string\",\"name\":\"obj321\",\"paramValueType\":\"function\",\"expression\":\"fn.string.toString({$.start.BODY.liteflowString2})\",\"defaultValue\":\"\",\"description\":\"测试321\"},\"field_0.1714818397493687\":{\"name\":\"obj322\",\"type\":\"boolean\",\"paramValueType\":\"fixed\",\"expression\":\"\",\"defaultValue\":\"322\",\"description\":\"测试322\"},\"field_0.6049198098926569\":{\"name\":\"obj323\",\"type\":\"array\",\"items\":{\"type\":\"boolean\",\"name\":\"items\"},\"paramValueType\":\"function\",\"expression\":\"fn.string.uuid()\",\"defaultValue\":\"\",\"description\":\"测试323\"},\"field_0.8337707613411385\":{\"name\":\"obj324\",\"type\":\"array\",\"items\":{\"type\":\"object\",\"name\":\"items\",\"properties\":{\"field_0.06773828429117001\":{\"type\":\"string\",\"name\":\"obj3241\",\"paramValueType\":\"fixed\",\"expression\":\"\",\"defaultValue\":\"3241\",\"description\":\"测试3241\"},\"field_0.1843153087209568\":{\"name\":\"obj3242\",\"type\":\"number\",\"paramValueType\":\"reference\",\"expression\":\"$.start.BODY.liteflowInt1\",\"defaultValue\":\"\",\"operatorId\":\"start\",\"description\":\"测试3242\"}}}}}}}},\"field_0.9177158865091062\":{\"type\":\"string\",\"name\":\"obj4\",\"paramValueType\":\"function\",\"expression\":\"fn.date.timestamp()\",\"defaultValue\":\"\",\"description\":\"测试4\"},\"field_0.12937995059965735\":{\"name\":\"obj5\",\"type\":\"array\",\"items\":{\"type\":\"object\",\"name\":\"items\",\"properties\":{\"field_0.6352378437986952\":{\"type\":\"string\",\"name\":\"obj51\",\"paramValueType\":\"fixed\",\"expression\":\"\",\"defaultValue\":\"51\",\"description\":\"测试51\"}}}},\"field_0.7227801634098017\":{\"name\":\"obj6\",\"type\":\"array\",\"items\":{\"type\":\"string\",\"name\":\"items\"},\"paramValueType\":\"reference\",\"expression\":\"$.start.QUERY.liteflowQuery1\",\"defaultValue\":\"\",\"operatorId\":\"start\",\"description\":\"测试6\"}}}}";
//        String schema = "{\"root\":{\"type\":\"object\",\"name\":\"root\",\"description\":\"根层级\",\"properties\":{\"field_0.15758669097940348\":{\"name\":\"body1\",\"type\":\"object\",\"properties\":{\"field_0.6726639471900779\":{\"name\":\"intBody1\",\"type\":\"integer\",\"description\":\"整型参数1\",\"value\":\"2\"}},\"required\":[\"intBody1\"]},\"field_0.2735135430063509\":{\"type\":\"string\",\"name\":\"stringBody2\",\"description\":\"字符串参数2\",\"value\":\"3\"},\"field_0.6079035989769586\":{\"name\":\"arrayBoolean3\",\"type\":\"array\",\"items\":{\"type\":\"boolean\",\"name\":\"items\"},\"description\":\"数组布尔参数3\",\"value\":\"32\"},\"field_0.9769820294333413\":{\"name\":\"arrayObject4\",\"type\":\"array\",\"items\":{\"type\":\"object\",\"name\":\"items\",\"properties\":{\"field_0.17859640468508364\":{\"type\":\"string\",\"name\":\"stringObject4\",\"description\":\"数据对象字符串属性4\",\"value\":\"32\"}}}},\"field_0.46320860983206136\":{\"name\":\"res\",\"type\":\"boolean\",\"description\":\"ces\",\"value\":\"der2\"}},\"required\":[\"arrayBoolean3\"]}}";
        String schema = "{\"root\":{\"type\":\"object\",\"name\":\"root\",\"description\":\"根层级\",\"properties\":{\"field_0.2843685688226043\":{\"name\":\"err_code\",\"type\":\"integer\",\"description\":\"err_code\"},\"field_0.1250549580682383\":{\"type\":\"string\",\"name\":\"err_msg\",\"description\":\"err_msg\"},\"field_0.5358358200739115\":{\"name\":\"err_arr_data\",\"type\":\"array\",\"items\":{\"type\":\"object\",\"name\":\"items\",\"properties\":{\"field_0.5062880689386666\":{\"name\":\"id\",\"type\":\"long\",\"description\":\"id\"},\"field_0.4791119168005926\":{\"type\":\"string\",\"name\":\"name\",\"description\":\"姓名\"},\"field_0.4019702684797761\":{\"name\":\"age\",\"type\":\"integer\",\"description\":\"年龄\"}}},\"description\":\"err_arr_data\"}}}}";
        ApiRespParamDTO apiRespParamDTO = JsonSchemaConvertUtil.convert(schema);
        log.info("result : {}",JSONUtil.toJsonStr(apiRespParamDTO));

    }
}
