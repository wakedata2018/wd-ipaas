package com.wakedata.openapi.sdk.generator;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.wakedata.openapi.sdk.common.TypeConvertUtil;
import com.wakedata.openapi.sdk.generator.dto.*;
import com.wakedata.openapi.sdk.generator.param.Api;
import com.wakedata.openapi.sdk.generator.param.ApiField;
import com.wakedata.openapi.sdk.generator.param.ApiGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.wakedata.openapi.sdk.common.StringUtil.*;

/**
 * api数据转换
 * @author luomeng
 * @date 2022/10/28 12:30
 */
@Slf4j
public class ApiGroupDataConvertUtil {

    /**
     * 对象转换
     * @param apiGroupDTOList
     * @return
     */
    public static List<ApiGroup> convert(List<ApiGroupDTO> apiGroupDTOList){
        List<ApiGroup> apiGroupList = new ArrayList<>();
        if(CollUtil.isNotEmpty(apiGroupDTOList)) {
            ApiGroup apiGroup = null;
            for (ApiGroupDTO apiGroupDTO : apiGroupDTOList) {
                apiGroup = new ApiGroup();
                if(CollUtil.isNotEmpty(apiGroupDTO.getPublishApiList())){
                    String path = replace(apiGroupDTO.getGroupPath(),true,"");
                    apiGroup.setName(capitalize(path));
                    apiGroup.setDesc(apiGroupDTO.getGroupName() + ";" + ObjectUtil.defaultIfNull(apiGroupDTO.getGroupDesc(),""));
                    apiGroup.setGroupPackage(path.toLowerCase());
                    apiGroup.setApiList(convertApiList(apiGroup, apiGroupDTO));
                    apiGroupList.add(apiGroup);
                }
            }
        }
        return apiGroupList;
    }

    /**
     * 转换api列表
     * @param apiGroup
     * @param apiGroupDTO
     * @return
     */
    private static List<Api> convertApiList(ApiGroup apiGroup, ApiGroupDTO apiGroupDTO) {
        List<Api> apiList = new ArrayList<>();
        for(DataAssetApiDetailDTO apiDetailDTO : apiGroupDTO.getPublishApiList()){
            String apiPath = substring(apiDetailDTO.getDataAssetApiMethod(),"/");
            Api api = new Api();
            api.setPathName(replace(apiPath,false,"_").toUpperCase());
            api.setClassName(capitalize(replace(apiPath,true,"")));
            api.setDesc(apiDetailDTO.getApiName() + ";" + ObjectUtil.defaultIfNull(apiDetailDTO.getApiDescription(),""));
            api.setUrl(apiDetailDTO.getDataAssetApiMethod());
            api.setMethod(apiDetailDTO.getReqMethod().name().toUpperCase());
            api.setGroupPackage(apiGroup.getGroupPackage());
            api.setGroupName(apiGroup.getName());
            api.setApiPackage(replace(apiPath,false,"").toLowerCase());
            //设置请求参数
            setRequestParam(apiDetailDTO, api);
            //设置响应参数
            setResponseParam(apiDetailDTO, api);
            apiList.add(api);
        }
        return apiList;
    }

    /**
     * 设置响应参数
     * @param apiDetailDTO
     * @param api
     */
    private static void setResponseParam(DataAssetApiDetailDTO apiDetailDTO, Api api) {
        if(CollUtil.isNotEmpty(apiDetailDTO.getApiRespParamList())){
            List<ApiField> respHeadList = new ArrayList<>();
            List<ApiField> respBodyList = new ArrayList<>();
            for(ApiRespParamDTO apiRespParamDTO : apiDetailDTO.getApiRespParamList()){
                if(HttpParamKind.HEAD == apiRespParamDTO.getType()){
                    respHeadList.add(convertApiField(apiRespParamDTO.getAssetColumns(), apiRespParamDTO.getAssetDataType(), apiRespParamDTO.getDescription()));
                }
                if(HttpParamKind.BODY == apiRespParamDTO.getType()){
                    if(CollUtil.isNotEmpty(apiRespParamDTO.getChildApiRespParams())){
                        respBodyList = getApiFieldParamList(respBodyList,apiRespParamDTO);
                    }
                }
            }
            if(respHeadList.size() > 0){
                api.setContainRespHead(true);
            }else{
                api.setContainRespHead(false);
            }
            if(respBodyList.size() > 0){
                api.setContainRespBody(true);
            }else{
                api.setContainRespBody(false);
            }
            api.setRespHeadList(respHeadList);
            api.setRespBodyList(respBodyList);
        }else{
            api.setContainRespHead(false);
            api.setContainRespBody(false);
        }
    }

    /**
     * 设置请求参数
     * @param apiDetailDTO
     * @param api
     */
    private static void setRequestParam(DataAssetApiDetailDTO apiDetailDTO, Api api) {
        if(CollUtil.isNotEmpty(apiDetailDTO.getApiConditionPoList())){
            List<ApiField> apiFieldHeadList = new ArrayList<>();
            List<ApiField> apiFieldBodyList = new ArrayList<>();
            List<ApiField> apiFieldParamList = new ArrayList<>();
            for(ApiConditionDTO apiConditionDTO : apiDetailDTO.getApiConditionPoList()){
                if(HttpParamKind.HEAD == apiConditionDTO.getHttpParamKind()){
                    apiFieldHeadList.add(convertApiField(apiConditionDTO.getAssetColumns(), apiConditionDTO.getAssetDatatype(), apiConditionDTO.getDescriptions()));
                }
                if(HttpParamKind.QUERY == apiConditionDTO.getHttpParamKind() || HttpParamKind.FILTER == apiConditionDTO.getHttpParamKind()){
                    apiFieldParamList.add(convertApiField(apiConditionDTO.getAssetColumns(), apiConditionDTO.getAssetDatatype(), apiConditionDTO.getDescriptions()));
                }
                if(HttpParamKind.BODY == apiConditionDTO.getHttpParamKind()){
                    if(CollUtil.isNotEmpty(apiConditionDTO.getSchemaParseDataList())){
                        apiFieldBodyList = getApiFieldParamList(apiFieldBodyList,apiConditionDTO.getSchemaParseDataList().get(0));
                    }
                }
            }
            if(apiFieldBodyList.size() > 0){
                api.setContainBody(true);
            }else{
                api.setContainBody(false);
            }
            if(apiFieldHeadList.size() > 0){
                api.setContainHead(true);
            }else{
                api.setContainHead(false);
            }
            if(apiFieldParamList.size() > 0){
                api.setContainParam(true);
            }else{
                api.setContainParam(false);
            }
            api.setReqHeadList(apiFieldHeadList);
            api.setReqBodyList(apiFieldBodyList);
            api.setReqParamList(apiFieldParamList);
        }else{
            api.setContainHead(false);
            api.setContainParam(false);
            api.setContainBody(false);
        }
    }

    /**
     * 转换属性
     * @param field
     * @param type
     * @param desc
     * @return
     */
    private static ApiField convertApiField(String field, String type, String desc) {
        ApiField respHead = new ApiField();
        respHead.setName(field);
        respHead.setType(TypeConvertUtil.convertFileType(type));
        respHead.setDesc(desc);
        return respHead;
    }

    /**
     * 获取子属性列表
     * @param fieldList
     * @param apiParam
     */
    private static List<ApiField> getApiFieldParamList(List<ApiField> fieldList, ApiRespParamDTO apiParam){
        fieldList = fieldList == null?new ArrayList<>():fieldList;
        for(ApiRespParamDTO subParam : apiParam.getChildApiRespParams()) {
            ApiField apiField = convertApiField(subParam.getAssetColumns(), subParam.getAssetDataType(), subParam.getDescription());
            if(CollUtil.isNotEmpty(subParam.getChildApiRespParams())){
                apiField.setItemClassName(capitalize(subParam.getAssetColumns() + "Item"));
                apiField.setItemFieldList(getApiFieldParamList(apiField.getItemFieldList(),subParam));
            }
            fieldList.add(apiField);
        }
        return fieldList;

    }



}
