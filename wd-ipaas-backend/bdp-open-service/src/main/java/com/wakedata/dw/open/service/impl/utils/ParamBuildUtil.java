package com.wakedata.dw.open.service.impl.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.datasource.model.DatasourceTableColumnDo;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.DataTypeEnum;
import com.wakedata.dw.open.enums.PublicReturnParametersEnum;
import com.wakedata.dw.open.parammapping.AbstractParamMapping;
import com.wakedata.dw.open.parammapping.ExpressionParam;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.parammapping.ParamMappingTypeEnum;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.impl.api.helper.ApiResponseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author WangChenSheng
 * @descriptor
 * @title ParamBuildUtil
 * @date 2022/10/26 17:35
 */
public class ParamBuildUtil {

    /**
     * 模拟assetColumns参数
     */
    private static final String VIRTUAL_ASSET_COLUMN = "body";

    /**
     * 模拟assetDataType参数
     */
    private static final String VIRTUAL_ASSET_DATA_TYPE = "json";

    /**
     * 模拟初始化参数上下文对象结构
     */
    public static Map<String, JSON> initMultiInputMappings() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(HttpParamKind.HEAD.name(), new JSONObject());
        jsonObject.put(HttpParamKind.QUERY.name(), new JSONObject());
        jsonObject.put(HttpParamKind.BODY.name(), new JSONObject());

        Map<String, JSON> paramMappings = Maps.newLinkedHashMap();
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            paramMappings.put(entry.getKey(), (JSON) entry.getValue());
        }
        return paramMappings;
    }

    /**
     * 构建树
     *
     * @param jsonSchema jsonSchema
     * @return tree
     */
    public static List<ApiRespParamDTO> buildTree(String jsonSchema, DataAssetEnums.ApiResponseBusinessType businessType) {
        ApiRespParamDTO apiRespParamDTO = new ApiRespParamDTO();
        apiRespParamDTO.setId(ApiResponseHelper.VIRTUAL_ID);
        apiRespParamDTO.setParentId(DwOpenConstant.RESPONSE_PARENT_ID);
        apiRespParamDTO.setType(HttpParamKind.BODY);
        apiRespParamDTO.setResponsePostData(jsonSchema);
        apiRespParamDTO.setBusinessType(businessType);
        apiRespParamDTO.setAssetColumns(VIRTUAL_ASSET_COLUMN);
        apiRespParamDTO.setAssetDataType(VIRTUAL_ASSET_DATA_TYPE);
        return ApiResponseHelper.buildApiResponseDTOList(Collections.singletonList(apiRespParamDTO));
    }

    /**
     * 响应参数转换
     *
     * @param respParamDTOList
     * @return
     */
    public static List<? extends AbstractParamMapping> convertRespParamMapping(List<ApiRespParamDTO> respParamDTOList, HttpParamKind httpParamKind) {
        List<AbstractParamMapping> mappings = Lists.newArrayList();
        if (CollUtil.isEmpty(respParamDTOList)) {
            return mappings;
        }
        for (ApiRespParamDTO respParamDTO : respParamDTOList) {
            AbstractParamMapping responseMappings = getResponseParamMappings(httpParamKind, respParamDTO);
            if (isAddChildRespParams(respParamDTO)) {
                responseMappings.setType(ParamMappingTypeEnum.REFERENCE_TYPE.getType());
                responseMappings.setExpressionIsJson(Boolean.TRUE);
                responseMappings.setExpression(JSONUtil.toJsonStr(getChildExpression(respParamDTO.getChildApiRespParams(), new JSONObject())));
            }
            mappings.add(responseMappings);
        }
        return mappings;
    }

    /**
     * 是否添加子集响应参数
     *
     * @param respParamDTO
     * @return
     */
    private static Boolean isAddChildRespParams(ApiRespParamDTO respParamDTO) {
        return CollUtil.isNotEmpty(respParamDTO.getChildApiRespParams())
                && (StrUtil.isEmpty(respParamDTO.getParamValueType())
                || (ParamMappingTypeEnum.FIXED_TYPE.getType().equals(respParamDTO.getParamValueType()) && StrUtil.isEmpty(respParamDTO.getDefaultValue()))
                || (ParamMappingTypeEnum.REFERENCE_TYPE.getType().equals(respParamDTO.getParamValueType()) && StrUtil.isEmpty(respParamDTO.getExpression()))
                || (ParamMappingTypeEnum.FUNCTION_TYPE.getType().equals(respParamDTO.getParamValueType()) && StrUtil.isEmpty(respParamDTO.getExpression()))
        );
    }

    /**
     * 设置表达式参数
     *
     * @param childApiRespParams
     * @param result
     * @return
     */
    private static JSONObject getChildExpression(List<ApiRespParamDTO> childApiRespParams, JSONObject result) {
        for (ApiRespParamDTO respParam : childApiRespParams) {
            ExpressionParam expression = new ExpressionParam();
            expression.setOperatorId(DwOpenConstant.API_LITE_FLOW_RESPONSE_OPERATOR);
            expression.setOperatorName(DwOpenConstant.API_LITE_FLOW_RESPONSE_OPERATOR);
            expression.setDataType(respParam.getAssetDataType());
            expression.setType(respParam.getParamValueType());
            if (ParamMappingTypeEnum.FIXED_TYPE.getType().equals(respParam.getParamValueType())) {
                expression.setValue(respParam.getDefaultValue());
            } else {
                expression.setValue(respParam.getExpression());
            }
            if (isAddChildRespParams(respParam)) {
                expression.setType(null);
                expression.setExpressionIsJson(Boolean.TRUE);
                expression.setValue(getChildExpression(respParam.getChildApiRespParams(), new JSONObject()));
            }
            result.put(respParam.getAssetColumns(), expression);
        }
        return result;
    }

    /**
     * 构造响应参数
     *
     * @param httpParamKind
     * @param respParamDTO
     * @return
     */
    private static AbstractParamMapping getResponseParamMappings(HttpParamKind httpParamKind, ApiRespParamDTO respParamDTO) {
        AbstractParamMapping abstractParamMapping = new AbstractParamMapping();
        abstractParamMapping.setOperatorId(DwOpenConstant.API_LITE_FLOW_RESPONSE_OPERATOR);
        abstractParamMapping.setField(respParamDTO.getAssetColumns());
        abstractParamMapping.setDataType(respParamDTO.getAssetDataType());
        abstractParamMapping.setType(respParamDTO.getParamValueType());
        abstractParamMapping.setFixedValue(respParamDTO.getDefaultValue());
        abstractParamMapping.setExpression(respParamDTO.getExpression());
        abstractParamMapping.setHttpParamKind(httpParamKind);
        abstractParamMapping.setExpressionIsJson(Boolean.FALSE);
        return abstractParamMapping;
    }

    /**
     * 获取公共的响应参数集合
     *
     * @param operationType SQL操作类型
     * @return 响应参数集合
     */
    public static List<DatasourceTableColumnDo> getDefaultPageResultParam(DataAssetEnums.DataApiOperationType operationType) {
        List<DatasourceTableColumnDo> result = new ArrayList<>();
        PublicReturnParametersEnum code = PublicReturnParametersEnum.CODE;
        result.add(new DatasourceTableColumnDo(code.getAttributeName(), code.getFrontType(), code.getAttributeDesc(), code.getSample(), null));
        PublicReturnParametersEnum msg = PublicReturnParametersEnum.MSG;
        result.add(new DatasourceTableColumnDo(msg.getAttributeName(), msg.getFrontType(), msg.getAttributeDesc(), msg.getSample(), null));
        PublicReturnParametersEnum data = PublicReturnParametersEnum.DATA;
        if (DataAssetEnums.DataApiOperationType.QUERY.equals(operationType)) {
            result.add(new DatasourceTableColumnDo(data.getAttributeName(), DataTypeEnum.ARRAY.getType(), data.getAttributeDesc(), null, null));
            PublicReturnParametersEnum pageSize = PublicReturnParametersEnum.PAGE_SIZE;
            result.add(new DatasourceTableColumnDo(pageSize.getAttributeName(), pageSize.getFrontType(), pageSize.getAttributeDesc(), pageSize.getSample(), null));
            PublicReturnParametersEnum pageNo = PublicReturnParametersEnum.PAGE_NO;
            result.add(new DatasourceTableColumnDo(pageNo.getAttributeName(), pageNo.getFrontType(), pageNo.getAttributeDesc(), pageNo.getSample(), null));
            PublicReturnParametersEnum totalCount = PublicReturnParametersEnum.TOTAL_COUNT;
            result.add(new DatasourceTableColumnDo(totalCount.getAttributeName(), totalCount.getFrontType(), totalCount.getAttributeDesc(), totalCount.getSample(), null));
        } else {
            result.add(new DatasourceTableColumnDo(data.getAttributeName(), DataTypeEnum.OBJECT.getType(), data.getAttributeDesc(), null, null));
        }
        PublicReturnParametersEnum success = PublicReturnParametersEnum.SUCCESS;
        result.add(new DatasourceTableColumnDo(success.getAttributeName(), success.getFrontType(), success.getAttributeDesc(), success.getSample(), null));
        return result;
    }
}
