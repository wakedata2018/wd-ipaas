package com.wakedata.dw.open.service.api;


import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.model.api.ApiConditionPo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author yiyufeng
 * @title DataApiGatewayService
 * @projectName bdp-open
 * @date
 * @description
 */
public interface DataApiGatewayService {


    /**
     * 导出api
     *
     * @return
     */
    ResultDTO export();

    /**
     * API 入参详情
     *
     * @param dataAssetId API ID
     * @param timestamp 时间戳（生成临时Token用）
     * @param isApiTest
     * @return API入参数据
     */
    ResultDTO<Map<String, Object>> apiDetail(Integer dataAssetId, String timestamp,Boolean isApiTest);


    /**
     * API文档：查询公共参数
     *
     * @param dataAssetId API ID
     * @return API入参数据
     */
    ResultDTO<Map<String, Object>> getRequireInput(Integer dataAssetId);

    /**
     * 选择应用测试
     * @param lesseeId
     * @param appKey
     * @return
     */
    Map<String, List<ApiConditionPo>> chooseAppTest(Long lesseeId, String appKey);

}
