package com.wakedata.dw.open.openapi;

import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.service.openapi.OpenApiGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author luomeng
 * @date 2022/10/8 10:23
 */
@Service
public class OpenApiGatewayFactory {

    @Autowired
    private List<OpenApiGatewayService> openApiGatewayServiceList;

    /**
     * 选择执行
     *
     * @param apiType
     * @return
     */
    public OpenApiGatewayService getOpenApiGatewayService(DataAssetEnums.DataApiType apiType) {
        Optional<OpenApiGatewayService> groupSelectOptional = openApiGatewayServiceList.stream().filter(t -> t.apiType() == apiType).findAny();
        return groupSelectOptional.orElseThrow(() -> new IllegalArgumentException("apiType error，api request fail"));
    }
}
