package com.wakedata.dw.open.operator;

import com.wakedata.common.core.hashids.HashidsUtil;
import com.wakedata.dw.open.config.DwOpenCommonConfig;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.mapper.api.ApiGroupMapper;
import com.wakedata.dw.open.model.api.ApiGroupPo;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.model.api.flow.Component;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.api.flow.operator.VertexOperator;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.api.dto.LiteFlowAllOperatorTemplateDTO;
import com.wakedata.dw.open.service.impl.api.helper.ApiResponseHelper;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * 算子执行器抽象类
 *
 * @author wujunqiang
 * @since 2022/11/24 10:37
 */
public abstract class BaseOperatorProcessor implements OperatorProcessor {

    @Resource
    private ApiGroupMapper apiGroupMapper;

    @Resource
    private DwOpenCommonConfig dwOpenCommonConfig;

    @Resource
    private OperatorProcessorFactory operatorProcessorFactory;

    @Override
    public LiteFlowAllOperatorTemplateDTO buildOperatorTemplate(Operator<? extends Component> operator, Integer apiId, String operateId, String operatorName) {
        // 不需要返回参数的算子只需要返回算子id和算子名称
        LiteFlowAllOperatorTemplateDTO liteFlowAllOperatorTemplateDTO = new LiteFlowAllOperatorTemplateDTO();
        liteFlowAllOperatorTemplateDTO.setNodeId(operateId);
        liteFlowAllOperatorTemplateDTO.setNodeName(operatorName);
        liteFlowAllOperatorTemplateDTO.setNodeClass(this.getOperatorClassName());
        return liteFlowAllOperatorTemplateDTO;
    }

    @PostConstruct
    protected void registerProcessor() {
        operatorProcessorFactory.register(getOperatorClassName(), this);
    }

    /**
     * 获取com.wakedata.dw.open.model.api.flow.operator.Operator实现类class name
     *
     * @return class name
     */
    protected abstract String getOperatorClassName();

    /**
     * 记得在Component.Kind枚举类中加上算子对应的枚举值，不然进入编排画布时调用/business/api/component/operator/list/impl接口会报错，说明：
     * 1. 枚举值与dw_open_api_operator_attribute.unique_name字段的值需要对应
     * 2. 如果算子不出现在dw_open_api_operator_attribute表中可返回null
     * 3. 该方法没有实际作用，只是提醒需要在枚举类添加值，避免程序运行打印异常
     *
     * @return Component.Kind
     */
    protected abstract Component.Kind getComponentKind();

    /**
     * 根据ApiRespParamDTO集合，生成参数模版
     *
     * @param liteFlowResult API返回参数DTO集合
     * @param operateId      算子id
     * @param operatorName   算子名称
     * @return 参数模版
     */
    protected LiteFlowAllOperatorTemplateDTO getTemplateFromApiRespParamDtoList(List<ApiRespParamDTO> liteFlowResult, String operateId, String operatorName, String operatorDesc) {
        List<ApiRespParamDTO> liftFlowRespParamDTOList = ApiResponseHelper.buildApiResponseDTOList(liteFlowResult);
        return getLiteFlowAllOperatorTemplateDTO(operateId, operatorName, operatorDesc, liftFlowRespParamDTOList);
    }

    /**
     * 构建公共的响应参数模版
     *
     * @param responsePostData 响应参数JsonSchema
     * @param operateId        算子id
     * @param operatorName     算子名称
     * @return 参数模版
     */
    protected LiteFlowAllOperatorTemplateDTO buildBaseOperatorTemplate(String responsePostData, String operateId, String operatorName, String operatorDesc) {
        ApiRespParamDTO apiRespParamDTO = getDefaultRespParamDTO();
        apiRespParamDTO.setResponsePostData(responsePostData);
        List<ApiRespParamDTO> liftFlowRespParamDTOList = ApiResponseHelper.buildApiResponseDTOList(Collections.singletonList(apiRespParamDTO));
        // 前端树形数据结构所需
        return getLiteFlowAllOperatorTemplateDTO(operateId, operatorName, operatorDesc, liftFlowRespParamDTOList);
    }

    /**
     * 获取默认响应数据
     *
     * @return ApiRespParamDTO
     */
    protected ApiRespParamDTO getDefaultRespParamDTO(){
        ApiRespParamDTO apiRespParamDTO = new ApiRespParamDTO();
        apiRespParamDTO.setId(ApiResponseHelper.VIRTUAL_ID);
        apiRespParamDTO.setParentId(DwOpenConstant.RESPONSE_PARENT_ID);
        apiRespParamDTO.setType(HttpParamKind.BODY);
        apiRespParamDTO.setBusinessType(DataAssetEnums.ApiResponseBusinessType.HTTP_RESULT);
        apiRespParamDTO.setAssetColumns(ApiResponseHelper.VIRTUAL_ASSET_COLUMN);
        apiRespParamDTO.setAssetDataType(ApiResponseHelper.VIRTUAL_ASSET_DATA_TYPE);
        return apiRespParamDTO;
    }

    /**
     * 填充api信息到LiteFlowAllOperatorTemplateDTO中
     *
     * @param dto            流程编排所有算子的参数模板
     * @param dataAssetApiPo DataAssetApiPo
     */
    protected void fillApiInfo(LiteFlowAllOperatorTemplateDTO dto, DataAssetApiPo dataAssetApiPo) {
        if (dataAssetApiPo == null) {
            return;
        }

        dto.setApiName(dataAssetApiPo.getApiName());
        dto.setApiDescription(dataAssetApiPo.getApiDescription());
        // 当前API不显示文档链接
        if (!VertexOperator.VERTEX_OPERATOR_ID.equals(dto.getNodeId())) {
            dto.setApiDocUrl(String.format(dwOpenCommonConfig.getApiUrl(), HashidsUtil.encodeDefault(dataAssetApiPo.getDataAssetApiId())));
        }
        // 填充API分组信息
        ApiGroupPo apiGroupPo = apiGroupMapper.selectByPrimaryKey(dataAssetApiPo.getApiGroupId());
        if (apiGroupPo != null) {
            dto.setApiGroupName(apiGroupPo.getGroupName());
        }
    }

    /**
     * 创建参数模版
     *
     * @param operateId                算子id
     * @param operatorName             算子名称
     * @param liftFlowRespParamDTOList API返回参数DTO集合
     * @return LiteFlowAllOperatorTemplateDTO
     */
    protected LiteFlowAllOperatorTemplateDTO getLiteFlowAllOperatorTemplateDTO(String operateId, String operatorName, String operatorDesc, List<ApiRespParamDTO> liftFlowRespParamDTOList) {
        liftFlowRespParamDTOList.forEach(apiRespParamDTO -> {
            apiRespParamDTO.setNodeId(operateId);
            apiRespParamDTO.setNodeName(operatorName);
        });
        LiteFlowAllOperatorTemplateDTO liteFlowAllOperatorTemplateDTO = new LiteFlowAllOperatorTemplateDTO();
        liteFlowAllOperatorTemplateDTO.setNodeId(operateId);
        liteFlowAllOperatorTemplateDTO.setNodeName(operatorName);
        liteFlowAllOperatorTemplateDTO.setNodeDesc(operatorDesc);
        liteFlowAllOperatorTemplateDTO.setNodeClass(this.getOperatorClassName());
        liteFlowAllOperatorTemplateDTO.setApiRespParamDTOS(liftFlowRespParamDTOList);
        return liteFlowAllOperatorTemplateDTO;
    }

}
