package com.wakedata.dw.open.operator.processor;

import cn.hutool.core.collection.CollectionUtil;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.liteflow.component.api.StartNodeComponent;
import com.wakedata.dw.open.mapper.api.ApiConditionMapper;
import com.wakedata.dw.open.mapper.api.DataAssetApiMapper;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.model.api.flow.Component;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.api.flow.operator.VertexOperator;
import com.wakedata.dw.open.operator.BaseOperatorProcessor;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.api.dto.LiteFlowAllOperatorTemplateDTO;
import com.wakedata.dw.open.service.impl.api.helper.ApiResponseHelper;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 虚拟算子执行器
 *
 * @author wujunqiang
 * @since 2022/11/24 10:44
 */
@Service
public class VertexOperatorProcessor extends BaseOperatorProcessor {

    @Resource
    private DataAssetApiMapper dataAssetApiMapper;

    @Resource
    private ApiConditionMapper apiConditionMapper;

    @Override
    protected String getOperatorClassName() {
        return VertexOperator.class.getName();
    }

    @Override
    protected Component.Kind getComponentKind() {
        return Component.Kind.vertex;
    }

    @Override
    public LiteFlowAllOperatorTemplateDTO buildOperatorTemplate(Operator<? extends Component> operator, Integer apiId, String operateId, String operatorName) {
        return buildVertexOperatorTemplate(operator, apiId);
    }

    @Override
    public Class<? extends AbstractNodeComponent> getNodeComponent() {
        return StartNodeComponent.class;
    }

    /**
     * 构建开始算子类型的参数模版
     *
     * @param apiId Api ID
     * @return LiteFlowAllOperatorTemplateDTO
     */
    private LiteFlowAllOperatorTemplateDTO buildVertexOperatorTemplate(Operator<? extends Component> operator, Integer apiId) {
        Example example = new Example(ApiConditionPo.class);
        example.createCriteria()
                .andEqualTo("dataAssetId", apiId)
                .andEqualTo("type", DataAssetEnums.FiledTypeEnums.PARAMETERS.getValue());
        List<ApiConditionPo> apiConditionPos = apiConditionMapper.selectByExample(example);
        if (CollectionUtil.isNotEmpty(apiConditionPos)) {
            LiteFlowAllOperatorTemplateDTO startTemplate = new LiteFlowAllOperatorTemplateDTO();
            startTemplate.setNodeId(VertexOperator.VERTEX_OPERATOR_ID);
            startTemplate.setNodeName(VertexOperator.VERTEX_OPERATOR_ID);
            startTemplate.setNodeClass(this.getOperatorClassName());
            List<ApiRespParamDTO> paramList = new ArrayList<>();
            apiConditionPos.forEach(x -> {
                ApiRespParamDTO paramDTO = new ApiRespParamDTO();
                paramDTO.setId(x.getId());
                paramDTO.setType(x.getHttpParamKind());
                paramDTO.setAssetColumns(x.getAssetColumns());
                paramDTO.setAssetDataType(x.getAssetDatatype());
                paramDTO.setResponsePostData(x.getJsonSchema());
                paramDTO.setDescription(x.getDescriptions());
                paramList.add(paramDTO);
            });

            fillApiInfo(startTemplate, apiId);
            startTemplate.setApiRespParamDTOS(ApiResponseHelper.buildApiResponseDTOList(paramList));
            startTemplate.setNodeDesc(operator.getDesc());
            return startTemplate;
        }
        return null;
    }

    /**
     * 填充api信息到LiteFlowAllOperatorTemplateDTO中
     *
     * @param dto   流程编排所有算子的参数模板
     * @param apiId api id
     */
    private void fillApiInfo(LiteFlowAllOperatorTemplateDTO dto, Integer apiId) {
        Example apiExample = new Example(DataAssetApiPo.class);
        apiExample.createCriteria().andEqualTo("dataAssetApiId", apiId);
        DataAssetApiPo dataAssetApiPo = dataAssetApiMapper.selectOneByExample(apiExample);
        fillApiInfo(dto, dataAssetApiPo);
    }

}
