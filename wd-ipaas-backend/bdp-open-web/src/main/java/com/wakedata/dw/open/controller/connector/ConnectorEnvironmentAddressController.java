package com.wakedata.dw.open.controller.connector;

import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.service.connector.ConnectorEnvironmentAddressService;
import com.wakedata.dw.open.service.connector.dto.ConnectorEnvironmentAddressDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wujunqiang
 * @since 2022/11/21 10:54
 */
@RestController
@RequestMapping("${spring.mvc.backend.prefix}/business/connector/environment")
@Api(value = "平台环境地址接口", tags = "平台环境地址接口")
public class ConnectorEnvironmentAddressController {

    @Resource
    private ConnectorEnvironmentAddressService connectorEnvironmentAddressService;

    @GetMapping("/findByConnectorId")
    @ApiOperation(value = "根据平台id查询平台环境地址集合", notes = "根据平台id查询平台环境地址集合", httpMethod = "GET")
    public ResultDTO<List<ConnectorEnvironmentAddressDTO>> findByConnectorId(@RequestParam("connectorId") Long connectorId) {
        return connectorEnvironmentAddressService.findByConnectorId(connectorId);
    }

}
