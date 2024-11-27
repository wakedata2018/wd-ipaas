package com.wakedata.dw.open.service.impl.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.mapper.api.ApiResponseParamMapper;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.api.ApiResponseParamPo;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.api.dto.BuildExpressionDTO;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;


/**
 * @author wanghu@wakedata.com
 * @title API响应参数单元测试
 * @date 2021/12/14
 * @since v1.0.0
 */
@Slf4j
public class ApiResponseParamServiceImplTest {

    private ApiResponseParamServiceImpl apiResponseParamService = new ApiResponseParamServiceImpl();

    private ApiResponseParamMapper apiResponseParamMapper = mock(ApiResponseParamMapper.class);

    @Before
    public void init() {
        apiResponseParamService.init(null, apiResponseParamMapper);
    }

    @Test
    public void listByApiId() {
        List<ApiResponseParamPo> params = Lists.newArrayList();
        buildParam(params, 1, "String", "message", "响应信息", 0);
        buildParam(params, 2, "Object", "data", "数据", 0);
        buildParam(params, 3, "String", "name", "姓名", 2);
        buildParam(params, 4, "Number", "age", "年龄", 2);

        when(apiResponseParamMapper.select(any())).thenReturn(params);

        List<ApiRespParamDTO> result = apiResponseParamService.listByApiId(1);
        log.info("result={}", JSON.toJSONString(result));
    }

    private void buildParam(
        List<ApiResponseParamPo> params, int id, String string, String message, String description, int parentId) {

        ApiResponseParamPo po = new ApiResponseParamPo();
        po.setId(id);
        po.setAssetDataType(string);
        po.setAssetColumns(message);
        po.setDescription(description);
        po.setDataAssetId(1);
        po.setParentId(parentId);
        params.add(po);
    }

    @Test
    public void convertColumnToExpression() {
        BuildExpressionDTO dto = new BuildExpressionDTO();

        ApiConditionPo apiConditionPo = new ApiConditionPo();
        apiConditionPo.setAssetColumns("name");
        apiConditionPo.setHttpParamKind(HttpParamKind.QUERY);

        dto.setReqApiCondition(apiConditionPo);

        ApiConditionPo conditionPo = new ApiConditionPo();
        conditionPo.setAssetColumns("username");
        conditionPo.setHttpParamKind(HttpParamKind.QUERY);
        dto.setBindApiCondition(conditionPo);

        log.info("expression={}", apiResponseParamService.convertColumnToExpression(dto, DataAssetEnums.ExpressionType.BETWEEN_OPERATOR.getValue()));

        apiConditionPo.setHttpParamKind(HttpParamKind.BODY);
        log.info("expression={}", apiResponseParamService.convertColumnToExpression(dto,DataAssetEnums.ExpressionType.BETWEEN_OPERATOR.getValue()));

        apiConditionPo.setHttpParamKind(HttpParamKind.HEAD);
        dto.setHead("Content-Type");
        log.info("expression={}", apiResponseParamService.convertColumnToExpression(dto,DataAssetEnums.ExpressionType.BETWEEN_OPERATOR.getValue()));
    }

    @Test
    public void convertColumnToExpressionResultStart() {
        BuildExpressionDTO dto = new BuildExpressionDTO();
        dto.setNodeName("start");

        ApiConditionPo apiConditionPo = new ApiConditionPo();
        apiConditionPo.setAssetColumns("name");
        apiConditionPo.setHttpParamKind(HttpParamKind.QUERY);

        dto.setReqApiCondition(apiConditionPo);

        ApiConditionPo conditionPo = new ApiConditionPo();
        conditionPo.setAssetColumns("username");
        conditionPo.setHttpParamKind(HttpParamKind.QUERY);
        dto.setBindApiCondition(conditionPo);

        log.info("expression={}", apiResponseParamService.convertColumnToExpression(dto, DataAssetEnums.ExpressionType.LITEFLOW_RESULT.getValue()));

        apiConditionPo.setHttpParamKind(HttpParamKind.BODY);
        log.info("expression={}", apiResponseParamService.convertColumnToExpression(dto,DataAssetEnums.ExpressionType.LITEFLOW_RESULT.getValue()));

        apiConditionPo.setHttpParamKind(HttpParamKind.HEAD);
        dto.setHead("Content-Type");
        log.info("expression={}", apiResponseParamService.convertColumnToExpression(dto,DataAssetEnums.ExpressionType.LITEFLOW_RESULT.getValue()));
    }

}
